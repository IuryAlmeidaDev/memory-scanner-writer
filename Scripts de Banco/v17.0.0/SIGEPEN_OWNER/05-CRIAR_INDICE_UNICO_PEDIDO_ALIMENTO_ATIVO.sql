------------------------------------------------------------------
--- SCRIPT DE CRIACAO DO INDICE UNICO DE PEDIDO DE ALIMENTO ATIVO
------------------------------------------------------------------
-- Objetivo:
--   Impedir mais de um pedido ATIVO para a mesma combinacao de
--   dia do pedido, tipo de refeicao, unidade prisional e contrato.
--   Caso existam duplicidades ativas antes da criacao do indice,
--   manter ATIVO apenas o pedido mais recente e inativar os demais.
--
-- Contexto:
--   DATA_PEDIDO contem horario. Por isso, a unicidade deve considerar apenas
--   SUBSTR(DATA_PEDIDO, 1, 10), evitando dois pedidos ATIVOS da mesma refeicao
--   no mesmo dia quando o horario da refeicao for alterado.
--
-- Observacao:
--   O status ATIVO e gravado como 1 em STAT_PEDIDO porque a entidade
--   PedidoAlimento usa @Enumerated(EnumType.ORDINAL) e o enum Status
--   esta na ordem INATIVO, ATIVO, PENDENTE, SUSPENSO.
--   Portanto, os valores esperados neste script sao:
--     INATIVO = 0
--     ATIVO   = 1
--     PENDENTE = 2
--     SUSPENSO = 3
--
-- Consulta para validar duplicidades ativas antes/depois da execucao:
--
-- SELECT SUBSTR(DATA_PEDIDO, 1, 10) AS DIA_PEDIDO,
--        ID_TIPO_REFEICAO,
--        ID_UNIDADE_PRISIONAL,
--        ID_CONTRATO,
--        COUNT(*) AS QTD
--   FROM SIGEPEN_OWNER.SIGEPEN_PEDIDO_ALIMENTO
--  WHERE STAT_PEDIDO = 1
--  GROUP BY SUBSTR(DATA_PEDIDO, 1, 10),
--           ID_TIPO_REFEICAO,
--           ID_UNIDADE_PRISIONAL,
--           ID_CONTRATO
-- HAVING COUNT(*) > 1;
------------------------------------------------------------------

DECLARE
   v_status_pedido_desconhecidos NUMBER;
   v_status_item_desconhecidos NUMBER;
   v_status_dieta_desconhecidos NUMBER;
BEGIN
   SELECT COUNT(*)
     INTO v_status_pedido_desconhecidos
     FROM SIGEPEN_OWNER.SIGEPEN_PEDIDO_ALIMENTO
    WHERE STAT_PEDIDO NOT IN (0, 1, 2, 3);

   SELECT COUNT(*)
     INTO v_status_item_desconhecidos
     FROM SIGEPEN_OWNER.SGP_ITEM_PEDIDO_ALIMENTO
    WHERE STATUS NOT IN (0, 1, 2, 3);

   SELECT COUNT(*)
     INTO v_status_dieta_desconhecidos
     FROM SIGEPEN_OWNER.SGP_DIETA_ESP_PEDIDO
    WHERE STATUS NOT IN (0, 1, 2, 3);

   IF v_status_pedido_desconhecidos > 0
      OR v_status_item_desconhecidos > 0
      OR v_status_dieta_desconhecidos > 0 THEN
      RAISE_APPLICATION_ERROR(
         -20001,
         'Pedido, item ou dieta de pedido de alimento possui status fora dos ordinais esperados: 0, 1, 2, 3.'
      );
   END IF;
END;
/

UPDATE SIGEPEN_OWNER.SGP_ITEM_PEDIDO_ALIMENTO i
   SET STATUS = 0
 WHERE i.ID_PEDIDO_ALIMENTO IN (
       SELECT ID_PEDIDO_ALIMENTO
         FROM (
              SELECT p2.ID_PEDIDO_ALIMENTO,
                     ROW_NUMBER() OVER (
                         PARTITION BY SUBSTR(p2.DATA_PEDIDO, 1, 10),
                                      p2.ID_TIPO_REFEICAO,
                                      p2.ID_UNIDADE_PRISIONAL,
                                      p2.ID_CONTRATO
                         ORDER BY p2.DATA_EMISSAO_PEDIDO DESC NULLS LAST,
                                  p2.ID_PEDIDO_ALIMENTO DESC
                     ) AS RN
                FROM SIGEPEN_OWNER.SIGEPEN_PEDIDO_ALIMENTO p2
               WHERE p2.STAT_PEDIDO = 1
         )
        WHERE RN > 1
   );

UPDATE SIGEPEN_OWNER.SGP_DIETA_ESP_PEDIDO d
   SET STATUS = 0
 WHERE d.ID_PEDIDO_ALIMENTO IN (
       SELECT ID_PEDIDO_ALIMENTO
         FROM (
              SELECT p2.ID_PEDIDO_ALIMENTO,
                     ROW_NUMBER() OVER (
                         PARTITION BY SUBSTR(p2.DATA_PEDIDO, 1, 10),
                                      p2.ID_TIPO_REFEICAO,
                                      p2.ID_UNIDADE_PRISIONAL,
                                      p2.ID_CONTRATO
                         ORDER BY p2.DATA_EMISSAO_PEDIDO DESC NULLS LAST,
                                  p2.ID_PEDIDO_ALIMENTO DESC
                     ) AS RN
                FROM SIGEPEN_OWNER.SIGEPEN_PEDIDO_ALIMENTO p2
               WHERE p2.STAT_PEDIDO = 1
         )
        WHERE RN > 1
   );

UPDATE SIGEPEN_OWNER.SIGEPEN_PEDIDO_ALIMENTO p
   SET STAT_PEDIDO = 0
 WHERE p.STAT_PEDIDO = 1
   AND p.ID_PEDIDO_ALIMENTO IN (
       SELECT ID_PEDIDO_ALIMENTO
         FROM (
               SELECT p2.ID_PEDIDO_ALIMENTO,
                      ROW_NUMBER() OVER (
                          PARTITION BY SUBSTR(p2.DATA_PEDIDO, 1, 10),
                                       p2.ID_TIPO_REFEICAO,
                                       p2.ID_UNIDADE_PRISIONAL,
                                       p2.ID_CONTRATO
                         ORDER BY p2.DATA_EMISSAO_PEDIDO DESC NULLS LAST,
                                  p2.ID_PEDIDO_ALIMENTO DESC
                     ) AS RN
                FROM SIGEPEN_OWNER.SIGEPEN_PEDIDO_ALIMENTO p2
               WHERE p2.STAT_PEDIDO = 1
         )
        WHERE RN > 1
   );

DECLARE
   v_duplicidades_ativas NUMBER;
BEGIN
   SELECT COUNT(*)
     INTO v_duplicidades_ativas
     FROM (
          SELECT 1
            FROM SIGEPEN_OWNER.SIGEPEN_PEDIDO_ALIMENTO
           WHERE STAT_PEDIDO = 1
           GROUP BY SUBSTR(DATA_PEDIDO, 1, 10),
                    ID_TIPO_REFEICAO,
                    ID_UNIDADE_PRISIONAL,
                    ID_CONTRATO
          HAVING COUNT(*) > 1
     );

   IF v_duplicidades_ativas > 0 THEN
      RAISE_APPLICATION_ERROR(
         -20002,
         'Ainda existem duplicidades ativas por dia, tipo de refeicao, unidade e contrato. Indice unico nao sera recriado.'
      );
   END IF;
END;
/

BEGIN
   FOR idx IN (
      SELECT OWNER, INDEX_NAME
        FROM ALL_INDEXES
       WHERE OWNER = 'SIGEPEN_OWNER'
         AND INDEX_NAME = 'UK_SGP_PED_ALIM_ATIVO'
   ) LOOP
      EXECUTE IMMEDIATE 'DROP INDEX ' || idx.OWNER || '.' || idx.INDEX_NAME;
   END LOOP;
END;
/

CREATE UNIQUE INDEX SIGEPEN_OWNER.UK_SGP_PED_ALIM_ATIVO
ON SIGEPEN_OWNER.SIGEPEN_PEDIDO_ALIMENTO (
    DECODE(STAT_PEDIDO, 1, SUBSTR(DATA_PEDIDO, 1, 10), NULL),
    DECODE(STAT_PEDIDO, 1, ID_TIPO_REFEICAO, NULL),
    DECODE(STAT_PEDIDO, 1, ID_UNIDADE_PRISIONAL, NULL),
    DECODE(STAT_PEDIDO, 1, ID_CONTRATO, NULL)
);
