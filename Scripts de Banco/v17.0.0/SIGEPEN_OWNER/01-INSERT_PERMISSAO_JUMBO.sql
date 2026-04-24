-- ================================================================
-- Script: 01-INSERT_PERMISSAO_JUMBO.sql  (v17.0.0)
-- Objetivo:
--   1) Criar a permissão "GerarRelatorioJumbo" na funcionalidade
--      "Recuperando" (FUNCIONALIDADE_ID = 3).
--   2) Criar o perfil "PLANTONISTA_SEDE_JUMBO".
--   3) Associar ao perfil a permissão nova E a permissão existente
--      "Relatorio" (id 210). A permissão "Relatorio" é obrigatória
--      para que o submenu "Relatórios" renderize na UI.
--
-- Observações:
--   - As sequences SEQ_PERMISSAO e SEQ_PERFIL não existem neste
--     schema; IDs são calculados com NVL(MAX(ID),0)+1.
--   - Script idempotente: re-execuções não duplicam registros nem
--     quebram por unique constraint.
--
-- Pós-execução:
--   - Administrador atribui o perfil PLANTONISTA_SEDE_JUMBO aos
--     plantonistas da sede pela tela de cadastro de servidor.
-- ================================================================

-- 1) Permissão GerarRelatorioJumbo  (FUNCIONALIDADE_ID = 3 - Recuperando)
INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
SELECT
    (SELECT NVL(MAX(ID), 0) + 1 FROM SIGEPEN_OWNER.PERMISSAO),
    'GerarRelatorioJumbo',
    'Permite gerar o relatório de recuperandos ativos nas unidades com Jumbo.',
    3
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM SIGEPEN_OWNER.PERMISSAO WHERE NOME = 'GerarRelatorioJumbo'
);

-- 2) Perfil PLANTONISTA_SEDE_JUMBO
INSERT INTO SIGEPEN_OWNER.PERFIL (ID, NOME, DESCRICAO, STATUS)
SELECT
    (SELECT NVL(MAX(ID), 0) + 1 FROM SIGEPEN_OWNER.PERFIL),
    'PLANTONISTA_SEDE_JUMBO',
    'Plantonistas da sede autorizados a gerar o relatório de Jumbo.',
    1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM SIGEPEN_OWNER.PERFIL WHERE NOME = 'PLANTONISTA_SEDE_JUMBO'
);

-- 3) Associação perfil x permissão nova
INSERT INTO SIGEPEN_OWNER.PERFIL_PERMISSOES (PERFIL_ID, PERMISSAO_ID)
SELECT
    (SELECT ID FROM SIGEPEN_OWNER.PERFIL    WHERE NOME = 'PLANTONISTA_SEDE_JUMBO'),
    (SELECT ID FROM SIGEPEN_OWNER.PERMISSAO WHERE NOME = 'GerarRelatorioJumbo')
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM SIGEPEN_OWNER.PERFIL_PERMISSOES pp
    WHERE pp.PERFIL_ID    = (SELECT ID FROM SIGEPEN_OWNER.PERFIL    WHERE NOME = 'PLANTONISTA_SEDE_JUMBO')
      AND pp.PERMISSAO_ID = (SELECT ID FROM SIGEPEN_OWNER.PERMISSAO WHERE NOME = 'GerarRelatorioJumbo')
);

-- 4) Associação perfil x permissão "Relatorio" (id 210)  --- necessário
--    para o submenu "Relatórios" aparecer para este perfil.
INSERT INTO SIGEPEN_OWNER.PERFIL_PERMISSOES (PERFIL_ID, PERMISSAO_ID)
SELECT
    (SELECT ID FROM SIGEPEN_OWNER.PERFIL    WHERE NOME = 'PLANTONISTA_SEDE_JUMBO'),
    (SELECT ID FROM SIGEPEN_OWNER.PERMISSAO WHERE NOME = 'Relatorio')
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM SIGEPEN_OWNER.PERFIL_PERMISSOES pp
    WHERE pp.PERFIL_ID    = (SELECT ID FROM SIGEPEN_OWNER.PERFIL    WHERE NOME = 'PLANTONISTA_SEDE_JUMBO')
      AND pp.PERMISSAO_ID = (SELECT ID FROM SIGEPEN_OWNER.PERMISSAO WHERE NOME = 'Relatorio')
);

COMMIT;

-- ================================================================
-- Verificação pós-execução (opcional)
-- ================================================================
-- SELECT * FROM SIGEPEN_OWNER.PERMISSAO  WHERE NOME = 'GerarRelatorioJumbo';
-- SELECT * FROM SIGEPEN_OWNER.PERFIL     WHERE NOME = 'PLANTONISTA_SEDE_JUMBO';
-- SELECT p.NOME AS perfil, pm.NOME AS permissao
--   FROM SIGEPEN_OWNER.PERFIL p
--   JOIN SIGEPEN_OWNER.PERFIL_PERMISSOES pp ON pp.PERFIL_ID = p.ID
--   JOIN SIGEPEN_OWNER.PERMISSAO pm ON pm.ID = pp.PERMISSAO_ID
--  WHERE p.NOME = 'PLANTONISTA_SEDE_JUMBO';
