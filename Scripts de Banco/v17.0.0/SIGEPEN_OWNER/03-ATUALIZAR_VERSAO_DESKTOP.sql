-- ================================================================
-- Script: 03-ATUALIZAR_VERSAO_DESKTOP.sql  (v17.0.0)
-- Ambiente: HOMOLOGAÇÃO e PRODUÇÃO
-- Banco:    Oracle
-- Schema:   SIGEPEN_OWNER
--
-- Objetivo:
--   Atualizar o parâmetro VERSAO_DESKTOP da tabela
--   SIGEPEN_OWNER.PARAMETRO para '2.3', acompanhando a release do
--   sigepen-biometria-v2 que promove Cliente.VERSAO de "2.2" para
--   "2.3".
--   - Executar SIMULTANEAMENTE ao deploy da WAR / promoção do exe novo.
--
-- Rollback:
--   UPDATE SIGEPEN_OWNER.PARAMETRO
--      SET VALOR = '2.2'
--    WHERE NOME = 'VERSAO_DESKTOP';
--   COMMIT;
--   - Em caso de rollback, reverter também a WAR e o exe para a
--     versão anterior; caso contrário, todas as estações ficam
--     bloqueadas pelo descasamento de versão.
-- ================================================================

UPDATE SIGEPEN_OWNER.PARAMETRO
   SET VALOR = '2.3'
 WHERE NOME = 'VERSAO_DESKTOP';

COMMIT;

-- ================================================================
-- Verificação pós-execução
-- ================================================================
-- SELECT NOME, VALOR
--   FROM SIGEPEN_OWNER.PARAMETRO
--  WHERE NOME = 'VERSAO_DESKTOP';
-- Esperado: VALOR = '2.3'
