-- Criando novos perfis com base nas funcionalidades
BEGIN
    -- Perfil Segurança e Disciplina
    INSERT INTO SIGEPEN_OWNER.PERFIL (ID, NOME, DESCRICAO, STATUS)
    VALUES (SEQ_PERFIL.NEXTVAL, 'SEGURANCA_DISCIPLINA', 'Perfil para gerenciar Segurança e Disciplina', 1);

    -- Perfil Saúde
    INSERT INTO SIGEPEN_OWNER.PERFIL (ID, NOME, DESCRICAO, STATUS)
    VALUES (SEQ_PERFIL.NEXTVAL, 'SAUDE', 'Perfil para gerenciar Saúde', 1);

    -- Perfil Assistência Social
    INSERT INTO SIGEPEN_OWNER.PERFIL (ID, NOME, DESCRICAO, STATUS)
    VALUES (SEQ_PERFIL.NEXTVAL, 'ASSISTENCIA_SOCIAL', 'Perfil para gerenciar Assistência Social', 1);

    -- Perfil Psicologia
    INSERT INTO SIGEPEN_OWNER.PERFIL (ID, NOME, DESCRICAO, STATUS)
    VALUES (SEQ_PERFIL.NEXTVAL, 'PSICOLOGIA', 'Perfil para gerenciar Psicologia', 1);

    -- Perfil Produção
    INSERT INTO SIGEPEN_OWNER.PERFIL (ID, NOME, DESCRICAO, STATUS)
    VALUES (SEQ_PERFIL.NEXTVAL, 'PRODUCAO', 'Perfil para gerenciar Produção', 1);

    -- Perfil Educação
    INSERT INTO SIGEPEN_OWNER.PERFIL (ID, NOME, DESCRICAO, STATUS)
    VALUES (SEQ_PERFIL.NEXTVAL, 'EDUCACAO', 'Perfil para gerenciar Educação', 1);

    -- Perfil Jurídico
    INSERT INTO SIGEPEN_OWNER.PERFIL (ID, NOME, DESCRICAO, STATUS)
    VALUES (SEQ_PERFIL.NEXTVAL, 'JURIDICO', 'Perfil para gerenciar Jurídico', 1);

    -- Perfil Trabalho
    INSERT INTO SIGEPEN_OWNER.PERFIL (ID, NOME, DESCRICAO, STATUS)
    VALUES (SEQ_PERFIL.NEXTVAL, 'TRABALHO', 'Perfil para gerenciar Trabalho', 1);
END;
/