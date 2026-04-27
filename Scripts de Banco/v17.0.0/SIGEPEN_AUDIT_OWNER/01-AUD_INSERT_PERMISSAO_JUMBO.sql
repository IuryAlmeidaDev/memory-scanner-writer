-- ================================================================
-- Script: 01-AUD_INSERT_PERMISSAO_JUMBO.sql  (v17.0.0)
-- Schema alvo: SIGEPEN_AUDIT_OWNER
--
-- Objetivo:
--   Registrar manualmente, nas tabelas de auditoria do Hibernate Envers,
--   as inclusões realizadas pelo script de OWNER:
--     SIGEPEN_OWNER/01-INSERT_PERMISSAO_JUMBO.sql
--
--   Itens auditados:
--     1) Perfil  PLANTONISTA_SEDE_JUMBO       -> AUD_PERFIL
--     2) Vínculo perfil x GerarRelatorioJumbo -> AUD_PERFIL_PERMISSOES
--     3) Vínculo perfil x Relatorio (id 210)  -> AUD_PERFIL_PERMISSOES
--
-- Por que NÃO há registro para a permissão GerarRelatorioJumbo:
--   A entidade br.gov.mt.sejudh.sigepen.model.Permissao NÃO é @Audited
--   (não existe a tabela AUD_PERMISSAO). Apenas Perfil e a coleção
--   PERFIL_PERMISSOES são auditadas — esta última via
--   @Audited(targetAuditMode = NOT_AUDITED) no relacionamento.
--
-- Observações:
--   - Todas as linhas usam ACAO = 0 (RevType.ADD do Envers).
--   - 1 (uma) nova linha em REVISAO representa a execução deste script,
--     com USUARIO_ID = 1 (SISTEMA), padrão do CustomRevListener para
--     processos sem contexto FacesContext (background).
--   - REVISAO tem DATAHORA (NUMBER, ms desde epoch) e DATA (DATE) —
--     ambas preenchidas para compatibilidade total com o sistema.
--   - Idempotente: se a auditoria já tiver sido lançada para este
--     perfil (ACAO = 0), o bloco não faz nada e não duplica linhas.
--   - Pré-requisito: o script 01 do SIGEPEN_OWNER deve ter sido
--     executado antes (este script depende dos IDs de PERFIL e PERMISSAO).
-- ================================================================

DECLARE
    v_rev               NUMBER(19);
    v_perfil_id         NUMBER(19);
    v_permissao_jumbo   NUMBER(19);
    v_permissao_rel     NUMBER(19);
    v_ja_auditado       NUMBER;
BEGIN
    -- Recupera os IDs criados pelo script de OWNER
    SELECT ID INTO v_perfil_id
      FROM SIGEPEN_OWNER.PERFIL
     WHERE NOME = 'PLANTONISTA_SEDE_JUMBO';

    SELECT ID INTO v_permissao_jumbo
      FROM SIGEPEN_OWNER.PERMISSAO
     WHERE NOME = 'GerarRelatorioJumbo';

    SELECT ID INTO v_permissao_rel
      FROM SIGEPEN_OWNER.PERMISSAO
     WHERE NOME = 'Relatorio';

    -- Verifica se a auditoria de inclusão já foi lançada
    SELECT COUNT(*) INTO v_ja_auditado
      FROM SIGEPEN_AUDIT_OWNER.AUD_PERFIL
     WHERE ID = v_perfil_id
       AND ACAO = 0;

    IF v_ja_auditado = 0 THEN
        -- Nova revisão (1 só, para os 3 registros relacionados)
        v_rev := SIGEPEN_AUDIT_OWNER.SEQ_REVISAO.NEXTVAL;

        INSERT INTO SIGEPEN_AUDIT_OWNER.REVISAO
            (ID, HOST, IP, DATAHORA, DATA, URL, USUARIO_ID)
        VALUES
            (v_rev,
             'LOCALHOST',
             '127.0.0.1',
             (SYSDATE - TO_DATE('01/01/1970','DD/MM/YYYY')) * 86400000,
             SYSDATE,
             'SCRIPT/v17.0.0/INSERT_PERMISSAO_JUMBO',
             1); -- USUARIO_ID = 1 (SISTEMA), padrão do CustomRevListener

        -- 1) AUD_PERFIL — ADD do perfil PLANTONISTA_SEDE_JUMBO
        INSERT INTO SIGEPEN_AUDIT_OWNER.AUD_PERFIL
            (ID, REV, ACAO, DESCRICAO, NOME, STATUS)
        VALUES
            (v_perfil_id,
             v_rev,
             0, -- ADD
             'Plantonistas da sede autorizados a gerar o relatório de Jumbo.',
             'PLANTONISTA_SEDE_JUMBO',
             1); -- Status.ATIVO

        -- 2) AUD_PERFIL_PERMISSOES — ADD perfil x GerarRelatorioJumbo
        INSERT INTO SIGEPEN_AUDIT_OWNER.AUD_PERFIL_PERMISSOES
            (REV, PERFIL_ID, PERMISSAO_ID, ACAO)
        VALUES
            (v_rev, v_perfil_id, v_permissao_jumbo, 0);

        -- 3) AUD_PERFIL_PERMISSOES — ADD perfil x Relatorio (id 210)
        INSERT INTO SIGEPEN_AUDIT_OWNER.AUD_PERFIL_PERMISSOES
            (REV, PERFIL_ID, PERMISSAO_ID, ACAO)
        VALUES
            (v_rev, v_perfil_id, v_permissao_rel, 0);
    END IF;
END;
/

COMMIT;

-- ================================================================
-- Verificação pós-execução (opcional)
-- ================================================================
-- SELECT * FROM SIGEPEN_AUDIT_OWNER.AUD_PERFIL
--  WHERE NOME = 'PLANTONISTA_SEDE_JUMBO';
--
-- SELECT app.REV, app.PERFIL_ID, app.PERMISSAO_ID, app.ACAO,
--        pm.NOME AS permissao
--   FROM SIGEPEN_AUDIT_OWNER.AUD_PERFIL_PERMISSOES app
--   JOIN SIGEPEN_OWNER.PERMISSAO pm ON pm.ID = app.PERMISSAO_ID
--  WHERE app.PERFIL_ID = (SELECT ID FROM SIGEPEN_OWNER.PERFIL
--                          WHERE NOME = 'PLANTONISTA_SEDE_JUMBO');
--
-- SELECT * FROM SIGEPEN_AUDIT_OWNER.REVISAO
--  WHERE URL = 'SCRIPT/v17.0.0/INSERT_PERMISSAO_JUMBO';
