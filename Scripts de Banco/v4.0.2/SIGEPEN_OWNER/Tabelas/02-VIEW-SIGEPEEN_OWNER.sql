------------------------------------------------------------------
--- SCRIPT DE ALTERAÇÃO DA VIEW LISTAGEM_RECUPERANDOS
------------------------------------------------------------------

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
       UL.ESTRUTURA_ID ID_ESTRUTURA,       
       --EN.NOME_COMPLETO ESTRUTURA_NOME_COMPLETO
       R.PERICULOSIDADE_ADAPTATIVA
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
       NULL ID_ESTRUTURA,
       --NULL ESTRUTURA_NOME_COMPLETO
       R.PERICULOSIDADE_ADAPTATIVA
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
       NULL       ID_ESTRUTURA,
       --NULL       ESTRUTURA_NOME_COMPLETO
       NULL		  PERICULOSIDADE_ADAPTATIVA  
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
       NULL       ID_ESTRUTURA,
       --NULL       ESTRUTURA_NOME_COMPLETO
       NULL		  PERICULOSIDADE_ADAPTATIVA
  FROM PESSOA P
 --WHERE P.ID NOT IN (SELECT R.PESSOA_ID FROM RECUPERANDO R)
 WHERE NOT EXISTS (SELECT R.PESSOA_ID FROM RECUPERANDO R WHERE R.PESSOA_ID = P.ID)
 --AND P.ID NOT IN (SELECT V.ID_PESSOA_VISITANTE FROM SGP_VISITANTE V)
   AND NOT EXISTS (SELECT V.ID_PESSOA_VISITANTE FROM SGP_VISITANTE V WHERE V.ID_PESSOA_VISITANTE = P.ID)



