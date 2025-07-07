DECLARE
    -- Variáveis para os IDs dos perfis
    v_perfil_seg_disc_id NUMBER;
    v_perfil_saude_id NUMBER;
    v_perfil_assist_social_id NUMBER;
    v_perfil_psicologia_id NUMBER;
    v_perfil_producao_id NUMBER;
    v_perfil_educacao_id NUMBER;
    v_perfil_juridico_id NUMBER;
    v_perfil_trabalho_id NUMBER;
BEGIN
    -- Recuperar os IDs dos novos perfis
    SELECT ID INTO v_perfil_seg_disc_id FROM SIGEPEN_OWNER.PERFIL WHERE NOME = 'SEGURANCA_DISCIPLINA';
    SELECT ID INTO v_perfil_saude_id FROM SIGEPEN_OWNER.PERFIL WHERE NOME = 'SAUDE';
    SELECT ID INTO v_perfil_assist_social_id FROM SIGEPEN_OWNER.PERFIL WHERE NOME = 'ASSISTENCIA_SOCIAL';
    SELECT ID INTO v_perfil_psicologia_id FROM SIGEPEN_OWNER.PERFIL WHERE NOME = 'PSICOLOGIA';
    SELECT ID INTO v_perfil_producao_id FROM SIGEPEN_OWNER.PERFIL WHERE NOME = 'PRODUCAO';
    SELECT ID INTO v_perfil_educacao_id FROM SIGEPEN_OWNER.PERFIL WHERE NOME = 'EDUCACAO';
    SELECT ID INTO v_perfil_juridico_id FROM SIGEPEN_OWNER.PERFIL WHERE NOME = 'JURIDICO';
    SELECT ID INTO v_perfil_trabalho_id FROM SIGEPEN_OWNER.PERFIL WHERE NOME = 'TRABALHO';

    -- Associando permissões ao perfil "SEGURANÇA E DISCIPLINA"
    FOR perm IN (SELECT ID FROM SIGEPEN_OWNER.PERMISSAO WHERE FUNCIONALIDADE_ID = (SELECT ID FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Segurança e Disciplina'))
        LOOP
            INSERT INTO SIGEPEN_OWNER.PERFIL_PERMISSOES (PERFIL_ID, PERMISSAO_ID)
            VALUES (v_perfil_seg_disc_id, perm.ID);
        END LOOP;

    -- Associando permissões ao perfil "SAÚDE"
    FOR perm IN (SELECT ID FROM SIGEPEN_OWNER.PERMISSAO WHERE FUNCIONALIDADE_ID = (SELECT ID FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Saúde'))
        LOOP
            INSERT INTO SIGEPEN_OWNER.PERFIL_PERMISSOES (PERFIL_ID, PERMISSAO_ID)
            VALUES (v_perfil_saude_id, perm.ID);
        END LOOP;

    -- Associando permissões ao perfil "ASSISTÊNCIA SOCIAL"
    FOR perm IN (SELECT ID FROM SIGEPEN_OWNER.PERMISSAO WHERE FUNCIONALIDADE_ID = (SELECT ID FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Assistência Social'))
        LOOP
            INSERT INTO SIGEPEN_OWNER.PERFIL_PERMISSOES (PERFIL_ID, PERMISSAO_ID)
            VALUES (v_perfil_assist_social_id, perm.ID);
        END LOOP;

    -- Associando permissões ao perfil "PSICOLOGIA"
    FOR perm IN (SELECT ID FROM SIGEPEN_OWNER.PERMISSAO WHERE FUNCIONALIDADE_ID = (SELECT ID FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Psicologia'))
        LOOP
            INSERT INTO SIGEPEN_OWNER.PERFIL_PERMISSOES (PERFIL_ID, PERMISSAO_ID)
            VALUES (v_perfil_psicologia_id, perm.ID);
        END LOOP;

    -- Associando permissões ao perfil "PRODUÇÃO"
    FOR perm IN (SELECT ID FROM SIGEPEN_OWNER.PERMISSAO WHERE FUNCIONALIDADE_ID = (SELECT ID FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Produção'))
        LOOP
            INSERT INTO SIGEPEN_OWNER.PERFIL_PERMISSOES (PERFIL_ID, PERMISSAO_ID)
            VALUES (v_perfil_producao_id, perm.ID);
        END LOOP;

    -- Associando permissões ao perfil "EDUCAÇÃO"
    FOR perm IN (SELECT ID FROM SIGEPEN_OWNER.PERMISSAO WHERE FUNCIONALIDADE_ID = (SELECT ID FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Educação'))
        LOOP
            INSERT INTO SIGEPEN_OWNER.PERFIL_PERMISSOES (PERFIL_ID, PERMISSAO_ID)
            VALUES (v_perfil_educacao_id, perm.ID);
        END LOOP;

    -- Associando permissões ao perfil "JURÍDICO"
    FOR perm IN (SELECT ID FROM SIGEPEN_OWNER.PERMISSAO WHERE FUNCIONALIDADE_ID = (SELECT ID FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Jurídico'))
        LOOP
            INSERT INTO SIGEPEN_OWNER.PERFIL_PERMISSOES (PERFIL_ID, PERMISSAO_ID)
            VALUES (v_perfil_juridico_id, perm.ID);
        END LOOP;

    -- Associando permissões ao perfil "TRABALHO"
    FOR perm IN (SELECT ID FROM SIGEPEN_OWNER.PERMISSAO WHERE FUNCIONALIDADE_ID = (SELECT ID FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Trabalho'))
        LOOP
            INSERT INTO SIGEPEN_OWNER.PERFIL_PERMISSOES (PERFIL_ID, PERMISSAO_ID)
            VALUES (v_perfil_trabalho_id, perm.ID);
        END LOOP;
END;
/