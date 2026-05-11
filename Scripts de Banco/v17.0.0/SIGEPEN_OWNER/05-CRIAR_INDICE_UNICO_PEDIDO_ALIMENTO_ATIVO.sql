------------------------------------------------------------------
--- SCRIPT DE CRIACAO DO INDICE UNICO DE PEDIDO DE ALIMENTO ATIVO
------------------------------------------------------------------
-- Objetivo:
--   Impedir mais de um pedido ATIVO para a mesma combinacao de
--   data do pedido, tipo de refeicao, unidade prisional e contrato.
--   Caso existam duplicidades ativas antes da criacao do indice,
--   manter ATIVO apenas o pedido mais recente e inativar os demais.
--
-- Observacao:
--   O status ATIVO e gravado como 1 em STAT_PEDIDO porque a entidade
--   PedidoAlimento usa @Enumerated(EnumType.ORDINAL) e o enum Status
--   esta na ordem INATIVO, ATIVO, PENDENTE, SUSPENSO.
--
-- Consulta para validar duplicidades ativas antes/depois da execucao:
--
-- SELECT DATA_PEDIDO,
--        ID_TIPO_REFEICAO,
--        ID_UNIDADE_PRISIONAL,
--        ID_CONTRATO,
--        COUNT(*) AS QTD
--   FROM SIGEPEN_OWNER.SIGEPEN_PEDIDO_ALIMENTO
--  WHERE STAT_PEDIDO = 1
--  GROUP BY DATA_PEDIDO,
--           ID_TIPO_REFEICAO,
--           ID_UNIDADE_PRISIONAL,
--           ID_CONTRATO
-- HAVING COUNT(*) > 1;
------------------------------------------------------------------

UPDATE SIGEPEN_OWNER.SIGEPEN_PEDIDO_ALIMENTO p
   SET p.STAT_PEDIDO = 0
 WHERE p.STAT_PEDIDO = 1
   AND p.ID_PEDIDO_ALIMENTO IN (
       SELECT ID_PEDIDO_ALIMENTO
         FROM (
              SELECT p2.ID_PEDIDO_ALIMENTO,
                     ROW_NUMBER() OVER (
                         PARTITION BY p2.DATA_PEDIDO,
                                      p2.ID_TIPO_REFEICAO,
                                      p2.ID_UNIDADE_PRISIONAL,
                                      p2.ID_CONTRATO
                         ORDER BY p2.DATA_EMISSAO_PEDIDO DESC,
                                  p2.ID_PEDIDO_ALIMENTO DESC
                     ) AS RN
                FROM SIGEPEN_OWNER.SIGEPEN_PEDIDO_ALIMENTO p2
               WHERE p2.STAT_PEDIDO = 1
         )
        WHERE RN > 1
   );

CREATE UNIQUE INDEX SIGEPEN_OWNER.UK_SGP_PED_ALIM_ATIVO
ON SIGEPEN_OWNER.SIGEPEN_PEDIDO_ALIMENTO (
    CASE WHEN STAT_PEDIDO = 1 THEN DATA_PEDIDO END,
    CASE WHEN STAT_PEDIDO = 1 THEN ID_TIPO_REFEICAO END,
    CASE WHEN STAT_PEDIDO = 1 THEN ID_UNIDADE_PRISIONAL END,
    CASE WHEN STAT_PEDIDO = 1 THEN ID_CONTRATO END
);
