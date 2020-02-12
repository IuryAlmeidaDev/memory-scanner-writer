-------------------------------------------------
--- SCRIPT DE ALTERAÇÃO DE PARÂMETRO DA BIOMETRIA
-------------------------------------------------

UPDATE "SIGEPEN_OWNER"."PARAMETRO" SET VALOR = '1.0.17' WHERE NOME = 'VERSAO_CHROME';

UPDATE "SIGEPEN_OWNER"."PARAMETRO" SET VALOR = '1.0.10' WHERE NOME = 'VERSAO_FIREFOX';
