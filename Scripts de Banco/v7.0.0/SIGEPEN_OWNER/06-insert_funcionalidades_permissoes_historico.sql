-- Criar sequência para a tabela FUNCIONALIDADE
DECLARE
    v_max_func_id NUMBER;
BEGIN
    SELECT NVL(MAX(ID), 0) INTO v_max_func_id FROM SIGEPEN_OWNER.FUNCIONALIDADE;
    EXECUTE IMMEDIATE 'DROP SEQUENCE SIGEPEN_OWNER.SEQ_FUNCIONALIDADE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN
            RAISE;
        END IF;
END;
/

CREATE SEQUENCE SIGEPEN_OWNER.SEQ_FUNCIONALIDADE
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Criar sequência para a tabela PERMISSAO 
DECLARE
    v_max_perm_id NUMBER;
BEGIN
    SELECT NVL(MAX(ID), 0) INTO v_max_perm_id FROM SIGEPEN_OWNER.PERMISSAO;
    EXECUTE IMMEDIATE 'DROP SEQUENCE SIGEPEN_OWNER.SEQ_PERMISSAO';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN
            RAISE;
        END IF;
END;
/

CREATE SEQUENCE SIGEPEN_OWNER.SEQ_PERMISSAO
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Definir valores das sequências para iniciar após os IDs máximos
DECLARE
    v_max_func_id NUMBER;
    v_max_perm_id NUMBER;
    v_dummy       NUMBER;
BEGIN
    SELECT NVL(MAX(ID), 0) INTO v_max_func_id FROM SIGEPEN_OWNER.FUNCIONALIDADE;
    SELECT NVL(MAX(ID), 0) INTO v_max_perm_id FROM SIGEPEN_OWNER.PERMISSAO;

    EXECUTE IMMEDIATE 'ALTER SEQUENCE SIGEPEN_OWNER.SEQ_FUNCIONALIDADE INCREMENT BY ' || v_max_func_id;
    SELECT SEQ_FUNCIONALIDADE.NEXTVAL INTO v_dummy FROM DUAL;
    EXECUTE IMMEDIATE 'ALTER SEQUENCE SIGEPEN_OWNER.SEQ_FUNCIONALIDADE INCREMENT BY 1';

    EXECUTE IMMEDIATE 'ALTER SEQUENCE SIGEPEN_OWNER.SEQ_PERMISSAO INCREMENT BY ' || v_max_perm_id;
    SELECT SEQ_PERMISSAO.NEXTVAL INTO v_dummy FROM DUAL;
    EXECUTE IMMEDIATE 'ALTER SEQUENCE SIGEPEN_OWNER.SEQ_PERMISSAO INCREMENT BY 1';
END;
/

-- Criar novas funcionalidades para cada departamento com os nomes ajustados
INSERT INTO SIGEPEN_OWNER.FUNCIONALIDADE (ID, NOME)
VALUES (SEQ_FUNCIONALIDADE.NEXTVAL, 'Seguranca e Disciplina');
INSERT INTO SIGEPEN_OWNER.FUNCIONALIDADE (ID, NOME)
VALUES (SEQ_FUNCIONALIDADE.NEXTVAL, 'Saude');
INSERT INTO SIGEPEN_OWNER.FUNCIONALIDADE (ID, NOME)
VALUES (SEQ_FUNCIONALIDADE.NEXTVAL, 'Assistencia Social');
INSERT INTO SIGEPEN_OWNER.FUNCIONALIDADE (ID, NOME)
VALUES (SEQ_FUNCIONALIDADE.NEXTVAL, 'Psicologia');
INSERT INTO SIGEPEN_OWNER.FUNCIONALIDADE (ID, NOME)
VALUES (SEQ_FUNCIONALIDADE.NEXTVAL, 'Producao');
INSERT INTO SIGEPEN_OWNER.FUNCIONALIDADE (ID, NOME)
VALUES (SEQ_FUNCIONALIDADE.NEXTVAL, 'Educacao');
INSERT INTO SIGEPEN_OWNER.FUNCIONALIDADE (ID, NOME)
VALUES (SEQ_FUNCIONALIDADE.NEXTVAL, 'Juridico');
INSERT INTO SIGEPEN_OWNER.FUNCIONALIDADE (ID, NOME)
VALUES (SEQ_FUNCIONALIDADE.NEXTVAL, 'Trabalho');

-- Criar variáveis para armazenar os IDs das novas funcionalidades
DECLARE
    v_seg_disc_id      NUMBER;
    v_saude_id         NUMBER;
    v_assist_social_id NUMBER;
    v_psicologia_id    NUMBER;
    v_producao_id      NUMBER;
    v_educacao_id      NUMBER;
    v_juridico_id      NUMBER;
    v_trabalho_id      NUMBER;
BEGIN
    -- Obter os IDs das novas funcionalidades
    SELECT ID INTO v_seg_disc_id FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Seguranca e Disciplina';
    SELECT ID INTO v_saude_id FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Saude';
    SELECT ID INTO v_assist_social_id FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Assistencia Social';
    SELECT ID INTO v_psicologia_id FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Psicologia';
    SELECT ID INTO v_producao_id FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Producao';
    SELECT ID INTO v_educacao_id FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Educacao';
    SELECT ID INTO v_juridico_id FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Juridico';
    SELECT ID INTO v_trabalho_id FROM SIGEPEN_OWNER.FUNCIONALIDADE WHERE NOME = 'Trabalho';

    -- Criar permissões para cada funcionalidade com os novos nomes ajustados
    -- Seguranca e Disciplina
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'CadastrarHistoricoRecuperandoSegurancaEDisciplina',
            'Permite cadastrar um historico de Seguranca e Disciplina do recuperando',
            v_seg_disc_id);
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'VisualizarHistoricoRecuperandoSegurancaEDisciplina',
            'Permite visualizar o historico de Seguranca e Disciplina do recuperando',
            v_seg_disc_id);

    -- Saude
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'CadastrarHistoricoRecuperandoSaude',
            'Permite cadastrar um historico de Saude do recuperando', v_saude_id);
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'VisualizarHistoricoRecuperandoSaude',
            'Permite visualizar o historico de Saude do recuperando', v_saude_id);

    -- Assistencia Social
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'CadastrarHistoricoRecuperandoAssistenciaSocial',
            'Permite cadastrar um historico de Assistencia Social do recuperando',
            v_assist_social_id);
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'VisualizarHistoricoRecuperandoAssistenciaSocial',
            'Permite visualizar o historico de Assistencia Social do recuperando',
            v_assist_social_id);

    -- Psicologia
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'CadastrarHistoricoRecuperandoPsicologia',
            'Permite cadastrar um historico de Psicologia do recuperando',
            v_psicologia_id);
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'VisualizarHistoricoRecuperandoPsicologia',
            'Permite visualizar o historico de Psicologia do recuperando',
            v_psicologia_id);

    -- Producao
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'CadastrarHistoricoRecuperandoProducao',
            'Permite cadastrar um historico de Producao do recuperando', v_producao_id);
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'VisualizarHistoricoRecuperandoProducao',
            'Permite visualizar o historico de Producao do recuperando', v_producao_id);

    -- Educacao
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'CadastrarHistoricoRecuperandoEducacao',
            'Permite cadastrar um historico de Educacao do recuperando', v_educacao_id);
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'VisualizarHistoricoRecuperandoEducacao',
            'Permite visualizar o historico de Educacao do recuperando', v_educacao_id);

    -- Juridico
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'CadastrarHistoricoRecuperandoJuridico',
            'Permite cadastrar um historico Juridico do recuperando', v_juridico_id);
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'VisualizarHistoricoRecuperandoJuridico',
            'Permite visualizar o historico Juridico do recuperando', v_juridico_id);

    -- Trabalho
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'CadastrarHistoricoRecuperandoTrabalho',
            'Permite cadastrar um historico de Trabalho do recuperando', v_trabalho_id);
    INSERT INTO SIGEPEN_OWNER.PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
    VALUES (SEQ_PERMISSAO.NEXTVAL, 'VisualizarHistoricoRecuperandoTrabalho',
            'Permite visualizar o historico de Trabalho do recuperando', v_trabalho_id);
    -- Vincular TipoHistorico com Funcionalidade
    UPDATE SIGEPEN_OWNER.TIPO_HISTORICO SET FUNCIONALIDADE_ID = v_seg_disc_id WHERE ID = 1;
    UPDATE SIGEPEN_OWNER.TIPO_HISTORICO SET FUNCIONALIDADE_ID = v_saude_id WHERE ID = 2;
    UPDATE SIGEPEN_OWNER.TIPO_HISTORICO SET FUNCIONALIDADE_ID = v_assist_social_id WHERE ID = 3;
    UPDATE SIGEPEN_OWNER.TIPO_HISTORICO SET FUNCIONALIDADE_ID = v_psicologia_id WHERE ID = 4;
    UPDATE SIGEPEN_OWNER.TIPO_HISTORICO SET FUNCIONALIDADE_ID = v_producao_id WHERE ID = 5;
    UPDATE SIGEPEN_OWNER.TIPO_HISTORICO SET FUNCIONALIDADE_ID = v_educacao_id WHERE ID = 6;
    UPDATE SIGEPEN_OWNER.TIPO_HISTORICO SET FUNCIONALIDADE_ID = v_juridico_id WHERE ID = 7;
    UPDATE SIGEPEN_OWNER.TIPO_HISTORICO SET FUNCIONALIDADE_ID = v_trabalho_id WHERE ID = 8;
END;
/

COMMIT;
