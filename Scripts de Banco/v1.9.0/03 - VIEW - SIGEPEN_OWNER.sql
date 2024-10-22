create or replace view lista_todos_recuperandos as
select pessoa.ID as id_pessoa,
       recuperando.ID as matricula,
       pessoa.NOME as nome,
       pessoa.NOME_MAE as nome_mae,
       pessoa.DATA_NASCIMENTO as data_nascimento,
       (case recuperando.Status
         when 1 then
          'Ativo'
         when 2 then
          'Inativo Parcial'
         when 0 then
          'Inativo'
       end) as status,
       unidade.NOME as unidade,
       unidade.ID as id_unidade,
       estrutura_completa.NOME_COMPLETO as estrutura,
       recuperando.CONDICAO_ATUAL as condicao,
       regime.DESCRICAO as regime,
       passagem.TIPO_DESLIGAMENTO as tipo_desligamento,
       pessoa.sexo 
  from SIGEPEN_OWNER.PESSOA pessoa
  left outer join SIGEPEN_OWNER.RECUPERANDO recuperando
    on pessoa.ID = recuperando.PESSOA_ID
  left outer join SIGEPEN_OWNER.PASSAGEM passagem
    on recuperando.ID = passagem.RECUPERANDO_ID
  left outer join SIGEPEN_OWNER.PASSAGEM_LOTACAO lotacao
    on passagem.ID = lotacao.PASSAGEM_ID
  left outer join SIGEPEN_OWNER.ESTRUTURA estrutura
    on lotacao.ESTRUTURA_ID = estrutura.ID
  left outer join SIGEPEN_OWNER.UNIDADE_PRISIONAL unidade
    on estrutura.unidadePrisional_ID = unidade.ID
  left outer join SIGEPEN_OWNER.NOME_COMPLETO_ESTRUTURA estrutura_completa
    on estrutura.ID = estrutura_completa.ID
  left outer join SIGEPEN_OWNER.REGIME regime
    on recuperando.REGIME_ATUAL_ID = regime.ID
 where ((recuperando.ID is not null and
       (passagem.ID is null or
       (passagem.ID =
       (select max(passagem2.ID) as id_passagem2
              from SIGEPEN_OWNER.PASSAGEM passagem2
             where passagem2.RECUPERANDO_ID = recuperando.ID) and
       lotacao.ID =
       (select max(lotacao2.ID) as id_lotacao2
              from SIGEPEN_OWNER.PASSAGEM_LOTACAO lotacao2
             where lotacao2.PASSAGEM_ID = passagem.ID)))) or
       recuperando.ID is null);

grant select on lista_todos_recuperandos to SIGEPEN_USER;


-------------------------------------------------
--- SCRIPT DE ALTERA��O DA VIEW SITUACAO_CADASTRO_RECUPERANDO
-------------------------------------------------

CREATE OR REPLACE VIEW SITUACAO_CADASTRO_RECUPERANDO
AS SELECT R.ID,
       R.TEM_SINAL_TATUAGEM TEM_TATUAGEM,
       R.STATUS,
       (SELECT (CASE WHEN COUNT(P.ID) > 0 THEN 1 ELSE 0 END)
          FROM PARTICULARIDADE P
         WHERE (P.TIPO = 1 OR P.TIPO = 2 OR P.TIPO = 3 OR P.TIPO = 4 OR P.TIPO = 5)
           AND P.RECUPERANDO_ID = R.ID) TEM_FOTO_TATUAGEM,
       (SELECT (CASE WHEN COUNT(F.ID) > 0 THEN 1 ELSE 0 END) FROM FOTO F
         WHERE F.RECUPERANDO_ID = R.ID
           AND F.TIPO = 1) TEM_FOTO_FRENTE,
       (SELECT (CASE WHEN COUNT(F.ID) > 0 THEN 1 ELSE 0 END) FROM FOTO F
         WHERE F.RECUPERANDO_ID = R.ID
           AND F.TIPO = 2) TEM_FOTO_PERFIL,
       (SELECT (CASE WHEN COUNT(F.ID) > 0 THEN 1 ELSE 0 END) FROM FOTO F
         WHERE F.RECUPERANDO_ID = R.ID
           AND F.TIPO = 3) TEM_FOTO_CORPO_INTEIRO,
       (SELECT (CASE WHEN COUNT(RR.ID) > 0 THEN 1 ELSE 0 END) FROM RECUPERANDO RR
         WHERE RR.ID = R.ID
           AND RR.ORIENTACAO_SEXUAL_ID IS NOT NULL
           AND RR.ESTADO_CIVIL_ID IS NOT NULL
           AND RR.ESCOLARIDADE_ID IS NOT NULL
           AND RR.PROFISSAO IS NOT NULL
           AND RR.RELIGIAO_ID IS NOT NULL
           AND (RR.PNE = 0 OR (RR.PNE > 0 AND RR.RECEBE_BENEFICIO IS NOT NULL))
           AND ((trunc((trunc(sysdate) - PE.DATA_NASCIMENTO) / 365) >= 60 AND RR.APOSENTADO IS NOT NULL) OR (trunc((trunc(sysdate) - PE.DATA_NASCIMENTO) / 365) < 60 ))
           AND (
           (PE.SEXO = 'M') OR (PE.SEXO = 'F'
           AND (RR.GESTANTE = 0 OR (RR.GESTANTE = 1 AND RR.INICIO_GESTACAO IS NOT NULL ))
           AND (RR.LACTANTE = 1 OR (RR.LACTANTE = 0 AND RR.INICIO_LACTANTE IS NOT NULL))
           AND (RR.POSSUI_FILHO = 1 OR (RR.POSSUI_FILHO = 0 AND (RR.FILHO_PCD IS NOT NULL))) 
           )
           )) VIDA_SOCIAL_COMPLETA,
       (SELECT (CASE WHEN COUNT(PA.ID) > 0 THEN 1 ELSE 0 END) FROM PARENTE PA
         WHERE PA.RECUPERANDO_ID = R.ID) TEM_PARENTE
  FROM RECUPERANDO R, PESSOA PE
  WHERE PE.ID = R.PESSOA_ID;

CREATE OR REPLACE FORCE EDITIONABLE VIEW SGP_V_VISITANTE (ID_VISITANTE, ID_PESSOA_VISITANTE, DATA_HORA_CADASTRO, STAT_VISITANTE, ID_PESSOA, NOME_PESSOA, ULTIMA_APROVACAO, ULTIMA_FASE_CREDENCIAL, ULTIMO_STATUS_CREDENCIAL, TOTAL_CREDENCIAL, FASE_CREDENCIAL, STATUS_CREDENCIAL, CODG_FASE_ATUAL, CODG_SITUACAO_ATUAL, TIPO_DESLIGAMENTO, DATA_SAIDA, FIM_IMPEDIMENTO, ID_RECUPERANDO) AS 
  (
SELECT ID_VISITANTE,
ID_PESSOA_VISITANTE,
DATA_HORA_CADASTRO,
STAT_VISITANTE,
ID_PESSOA,
NOME_PESSOA,
MAX(ULTIMA_APROVACAO) ULTIMA_APROVACAO,
MAX(ULTIMA_FASE_CREDENCIAL) ULTIMA_FASE_CREDENCIAL,
MAX(ULTIMO_STATUS_CREDENCIAL) ULTIMO_STATUS_CREDENCIAL,
MAX(TOTAL_CREDENCIAL) TOTAL_CREDENCIAL,
MAX(FASE_CREDENCIAL) FASE_CREDENCIAL,
MAX(STATUS_CREDENCIAL) STATUS_CREDENCIAL,
CODG_FASE_ATUAL,
CODG_SITUACAO_ATUAL,
TIPO_DESLIGAMENTO,
DATA_SAIDA, 
FIM_IMPEDIMENTO,
ID_RECUPERANDO
 FROM (
 select DISTINCT visitante0_.ID_VISITANTE           as ID_VISITANTE,
       visitante0_.ID_PESSOA_VISITANTE             as ID_PESSOA_VISITANTE,
       visitante0_.DATA_HORA_CADASTRO              as DATA_HORA_CADASTRO,
       visitante0_.STAT_VISITANTE                  as STAT_VISITANTE,
       pessoa3_.ID 								   AS ID_PESSOA,
       pessoa3_.NOME	                           AS NOME_PESSOA,
       max(credenciam2_.DATA_HORA_APROVACAO)       as ULTIMA_APROVACAO,
       max(credenciam2_.CODG_FASE_CREDENCIAL)      as ULTIMA_FASE_CREDENCIAL,
       max(credenciam2_.CODG_SITUACAO_CREDENCIAL)  as ULTIMO_STATUS_CREDENCIAL,
       count(credenciam2_.ID_FASE_CREDENCIAMENTO) as TOTAL_CREDENCIAL,
       recuperand1_.CODG_FASE_CREDENCIAL           AS FASE_CREDENCIAL,
       recuperand1_.CODG_SITUACAO_CREDENCIAL	   AS STATUS_CREDENCIAL,
       visitante0_.CODG_FASE_ATUAL                 AS CODG_FASE_ATUAL,
       visitante0_.CODG_SITUACAO_ATUAL             AS CODG_SITUACAO_ATUAL,
       PASS.TIPO_DESLIGAMENTO                      AS TIPO_DESLIGAMENTO,
       PASS.DATA_SAIDA                             AS DATA_SAIDA,
       MAX(SI.DATA_FIM_IMPEDIMENTO)                AS FIM_IMPEDIMENTO,
       REC0.ID                                     AS ID_RECUPERANDO
 from SGP_VISITANTE visitante0_
         left outer join
     SGP_CREDENCIAMENTO recuperand1_ on visitante0_.ID_VISITANTE = recuperand1_.ID_VISITANTE
         left outer join
     SGP_CREDENCIAMENTO_FASE credenciam2_
     on recuperand1_.ID_CREDENCIAMENTO = credenciam2_.ID_CREDENCIAMENTO
         and (
                credenciam2_.CODG_SITUACAO_CREDENCIAL <> 5
            )
         inner join
     PESSOA pessoa3_ on visitante0_.ID_PESSOA_VISITANTE = pessoa3_.ID
         inner join
     SGP_TIPO_VISITA tipovisita4_ on visitante0_.ID_TIPO_VISITA = tipovisita4_.ID_TIPO_VISITA
     LEFT JOIN RECUPERANDO REC0 ON PESSOA3_.ID = REC0.PESSOA_ID
	 LEFT JOIN (SELECT RECUPERANDO_ID, MAX(ID) MAX_ID FROM PASSAGEM GROUP BY RECUPERANDO_ID) P ON P.RECUPERANDO_ID = REC0.ID
	 LEFT JOIN PASSAGEM PASS ON PASS.ID = P.MAX_ID
	 LEFT JOIN SGP_IMPEDIMENTO si ON SI.ID_VISITANTE = visitante0_.ID_VISITANTE AND SI.STAT_IMPEDIMENTO = 'A' AND SI.DATA_FIM_IMPEDIMENTO >= sysdate
 where 1 = 1
 group by visitante0_.ID_VISITANTE,
         visitante0_.ID_PESSOA_VISITANTE,
         visitante0_.DATA_HORA_CADASTRO,
         visitante0_.STAT_VISITANTE,
         pessoa3_.id,
         pessoa3_.NOME,
         recuperand1_.CODG_FASE_CREDENCIAL,
         recuperand1_.CODG_SITUACAO_CREDENCIAL,
         visitante0_.CODG_FASE_ATUAL,
         visitante0_.CODG_SITUACAO_ATUAL,
         PASS.TIPO_DESLIGAMENTO,
         PASS.DATA_SAIDA,
         REC0.ID
 UNION ALL
 SELECT 
 0 as ID_VISITANTE,
       re.PESSOA_ID as ID_PESSOA_VISITANTE,
       NULL              as DATA_HORA_CADASTRO,
       NULL AS  STAT_VISITANTE,
       pe.ID 								   AS ID_PESSOA,
       pe.NOME	                           AS NOME_PESSOA,
       NULL       as ULTIMA_APROVACAO,
       NULL      as ULTIMA_FASE_CREDENCIAL,
       NULL  as ULTIMO_STATUS_CREDENCIAL,
       NULL as TOTAL_CREDENCIAL,
       0 AS FASE_CREDENCIAL,
       0 AS STATUS_CREDENCIAL,
       0 AS CODG_FASE_ATUAL,
       0 AS CODG_SITUACAO_ATUAL,
       PASS.TIPO_DESLIGAMENTO                     AS TIPO_DESLIGAMENTO,
       PASS.DATA_SAIDA                            AS DATA_SAIDA,
       NULL AS FIM_IMPEDIMENTO,
       re.ID                       AS ID_RECUPERANDO
 FROM PESSOA pe
 LEFT JOIN RECUPERANDO re
 ON re.PESSOA_ID = pe.ID
	 LEFT JOIN (SELECT RECUPERANDO_ID, MAX(ID) MAX_ID FROM PASSAGEM GROUP BY RECUPERANDO_ID) P ON P.RECUPERANDO_ID = RE.ID
	 LEFT JOIN PASSAGEM PASS ON PASS.ID = P.MAX_ID
 WHERE NOT EXISTS (
	SELECT NULL FROM SGP_VISITANTE sv
	WHERE sv.ID_PESSOA_VISITANTE = pe.ID))
GROUP BY 
ID_VISITANTE,
ID_PESSOA_VISITANTE,
DATA_HORA_CADASTRO,
STAT_VISITANTE,
ID_PESSOA,
NOME_PESSOA,
CODG_FASE_ATUAL,
CODG_SITUACAO_ATUAL,
TIPO_DESLIGAMENTO,
DATA_SAIDA,
FIM_IMPEDIMENTO,
ID_RECUPERANDO);
  
grant select on SGP_V_VISITANTE to SIGEPEN_USER;

CREATE OR REPLACE VIEW CADASTRO_RECUPERANDO AS
SELECT X.*, (CASE
         WHEN (CASE WHEN TEM_TATUAGEM = 0 THEN 1 ELSE (CASE WHEN TEM_TATUAGEM = 1 AND TEM_FOTO_TATUAGEM = 1 THEN 1 ELSE 0 END) END) = 1 AND
              TEM_FOTO_FRENTE = 1 AND
              TEM_FOTO_PERFIL = 1 AND TEM_FOTO_CORPO_INTEIRO = 1 AND
              VIDA_SOCIAL_COMPLETA = 1 AND TEM_PARENTE = 1 THEN
          1
         ELSE
          0
       END) CADASTRO_COMPLETO
       
       FROM (SELECT PE.ID,
       R.TEM_SINAL_TATUAGEM TEM_TATUAGEM,
       R.STATUS,
       (SELECT (CASE WHEN COUNT(P.ID) > 0 THEN 1 ELSE 0 END)
          FROM PARTICULARIDADE P
         WHERE (P.TIPO = 1 OR P.TIPO = 2 OR P.TIPO = 3 OR P.TIPO = 4 OR P.TIPO = 5)
           AND P.RECUPERANDO_ID = R.ID) TEM_FOTO_TATUAGEM,
       (SELECT (CASE WHEN COUNT(F.ID) > 0 THEN 1 ELSE 0 END) FROM FOTO F
         WHERE F.RECUPERANDO_ID = R.ID
           AND F.TIPO = 1) TEM_FOTO_FRENTE,
       (SELECT (CASE WHEN COUNT(F.ID) > 0 THEN 1 ELSE 0 END) FROM FOTO F
         WHERE F.RECUPERANDO_ID = R.ID
           AND F.TIPO = 2) TEM_FOTO_PERFIL,
       (SELECT (CASE WHEN COUNT(F.ID) > 0 THEN 1 ELSE 0 END) FROM FOTO F
         WHERE F.RECUPERANDO_ID = R.ID
           AND F.TIPO = 3) TEM_FOTO_CORPO_INTEIRO,
       (SELECT (CASE WHEN COUNT(RR.ID) > 0 THEN 1 ELSE 0 END) FROM RECUPERANDO RR
         WHERE RR.ID = R.ID
           AND RR.ORIENTACAO_SEXUAL_ID IS NOT NULL
           AND RR.ESTADO_CIVIL_ID IS NOT NULL
           AND RR.ESCOLARIDADE_ID IS NOT NULL
           AND RR.PROFISSAO IS NOT NULL
           AND RR.RELIGIAO_ID IS NOT NULL
           AND (RR.PNE = 0 OR (RR.PNE > 0 AND RR.RECEBE_BENEFICIO IS NOT NULL))
           AND ((trunc((trunc(sysdate) - PE.DATA_NASCIMENTO) / 365) >= 60 AND RR.APOSENTADO IS NOT NULL) OR (trunc((trunc(sysdate) - PE.DATA_NASCIMENTO) / 365) < 60 ))
           AND (
           (PE.SEXO = 'M') OR (PE.SEXO = 'F'
           AND (RR.GESTANTE = 0 OR (RR.GESTANTE = 1 AND RR.INICIO_GESTACAO IS NOT NULL ))
           AND (RR.LACTANTE = 1 OR (RR.LACTANTE = 0 AND RR.INICIO_LACTANTE IS NOT NULL))
           AND (RR.POSSUI_FILHO = 1 OR (RR.POSSUI_FILHO = 0 AND (RR.FILHO_PCD IS NOT NULL)))
           )
           )) VIDA_SOCIAL_COMPLETA,
       (SELECT (CASE WHEN COUNT(PA.ID) > 0 THEN 1 ELSE 0 END) FROM PARENTE PA
         WHERE PA.RECUPERANDO_ID = R.ID) TEM_PARENTE
  FROM RECUPERANDO R, PESSOA PE
  WHERE PE.ID = R.PESSOA_ID) X;
  
grant select on CADASTRO_RECUPERANDO to SIGEPEN_USER;
  
create or replace view ultima_passagem as
select p.recuperando_id, p.id, p.data_entrada, (case when p.tipo_desligamento is not null and p.tipo_desligamento = 'OBITO' then 0 else 1 end) ta_vivo from passagem p
where p.id in (
select max(p2.id) from passagem p2
where p2.recuperando_id = p.recuperando_id);

create or replace view ULTIMA_LOTACAO as
select l.id, l.passagem_id, l.estrutura_id, l.data_entrada from passagem_lotacao l
where l.id in (
select max(l2.id) from passagem_lotacao l2
where l2.passagem_id = l.passagem_id);

grant select on ultima_passagem to SIGEPEN_USER;
grant select on ULTIMA_LOTACAO to SIGEPEN_USER;

CREATE OR REPLACE VIEW LISTAGEM_RECUPERANDOS AS
SELECT R.ID                       MATRICULA,
       P.ID                       ID_PESSOA,
       NULL                       ID_VISITANTE,
       P.NOME,
       P.NOME_SOCIAL,
       P.NOME_MAE,
       P.DATA_NASCIMENTO,
       RE.DESCRICAO               REGIME_ATUAL,
       R.REGIME_ATUAL_ID,
       R.CONDICAO_ATUAL,
       --CAST(R.STATUS AS VARCHAR2(5)) STATUS,
       R.STATUS,
       R.PERICULOSIDADE,
       U.SIGLA SIGLA_UNIDADE,
       U.NOME NOME_UNIDADE,
       U.ID ID_UNIDADE,
       UP.TA_VIVO,
       UL.ESTRUTURA_ID ID_ESTRUTURA
       --EN.NOME_COMPLETO ESTRUTURA_NOME_COMPLETO
  FROM PESSOA                         P,
       RECUPERANDO                    R,
       REGIME                         RE,
       ULTIMA_PASSAGEM UP,
       ULTIMA_LOTACAO UL,
       ESTRUTURA                      E,
       --NOME_COMPLETO_ESTRUTURA        EN,
       UNIDADE_PRISIONAL              U
 WHERE P.ID = R.PESSOA_ID
   AND RE.ID = R.REGIME_ATUAL_ID
   AND UP.RECUPERANDO_ID = R.ID
   AND UP.ID = UL.PASSAGEM_ID
   AND UL.ESTRUTURA_ID = E.ID
   AND E.UNIDADEPRISIONAL_ID = U.ID
   --AND E.ID = EN.ID
---------------------------------------------------
UNION ALL
---------------------------------------------------
--RECUPERANDOS SEM PASSAGEM
SELECT R.ID                       MATRICULA,
       P.ID                       ID_PESSOA,
       NULL                       ID_VISITANTE,
       P.NOME,
       P.NOME_SOCIAL,
       P.NOME_MAE,
       P.DATA_NASCIMENTO,
       NULL                       REGIME_ATUAL,
       NULL                       REGIME_ATUAL_ID,
       R.CONDICAO_ATUAL,
       --CAST(R.STATUS AS VARCHAR2(5)) STATUS,
       R.STATUS,
       R.PERICULOSIDADE,
       NULL SIGLA_UNIDADE,
       NULL NOME_UNIDADE,
       NULL ID_UNIDADE,
       1 TA_VIVO,
       NULL ID_ESTRUTURA
       --NULL ESTRUTURA_NOME_COMPLETO
  FROM PESSOA                         P,
       RECUPERANDO                    R
 WHERE P.ID = R.PESSOA_ID
   AND R.STATUS = 2

---------------------------------------------------
UNION ALL
---------------------------------------------------
--VISITANTE
SELECT NULL       MATRICULA,
       P.ID       ID_PESSOA,
       V.ID_VISITANTE ID_VISITANTE,
       P.NOME,
       P.NOME_SOCIAL,
       P.NOME_MAE,
       P.DATA_NASCIMENTO, 
       NULL       REGIME_ATUAL,
       NULL       REGIME_ATUAL_ID,
       NULL       CONDICAO_ATUAL,
       NULL       STATUS,
       NULL       PERICULOSIDADE,
       NULL       SIGLA_UNIDADE,
       NULL       ID_UNIDADE,
       NULL       NOME_UNIDADE,
       NULL       TA_VIVO,
       NULL       ID_ESTRUTURA
       --NULL       ESTRUTURA_NOME_COMPLETO
  FROM PESSOA P, SGP_VISITANTE V
 WHERE P.ID = V.ID_PESSOA_VISITANTE
--   AND P.ID NOT IN (SELECT R.PESSOA_ID FROM RECUPERANDO R)
  AND NOT EXISTS (SELECT R.PESSOA_ID FROM RECUPERANDO R WHERE R.PESSOA_ID = P.ID)
---------------------------------------------------
UNION ALL
---------------------------------------------------
--SOMENTE PESSOA
SELECT NULL       MATRICULA,
       P.ID       ID_PESSOA,
       NULL       ID_VISITANTE,
       P.NOME,
       P.NOME_SOCIAL,
       P.NOME_MAE,
       P.DATA_NASCIMENTO,     
       NULL       REGIME_ATUAL,
       NULL       REGIME_ATUAL_ID,
       NULL       CONDICAO_ATUAL,
       NULL       STATUS,
       NULL       PERICULOSIDADE,
       NULL       SIGLA_UNIDADE,
       NULL       NOME_UNIDADE,
       NULL       ID_UNIDADE,
       NULL       TA_VIVO,
       NULL       ID_ESTRUTURA
       --NULL       ESTRUTURA_NOME_COMPLETO
  FROM PESSOA P
 --WHERE P.ID NOT IN (SELECT R.PESSOA_ID FROM RECUPERANDO R)
 WHERE NOT EXISTS (SELECT R.PESSOA_ID FROM RECUPERANDO R WHERE R.PESSOA_ID = P.ID)
 --AND P.ID NOT IN (SELECT V.ID_PESSOA_VISITANTE FROM SGP_VISITANTE V)
   AND NOT EXISTS (SELECT V.ID_PESSOA_VISITANTE FROM SGP_VISITANTE V WHERE V.ID_PESSOA_VISITANTE = P.ID)
;

grant select on LISTAGEM_RECUPERANDOS to SIGEPEN_USER;
