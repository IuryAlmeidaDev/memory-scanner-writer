-- Inserções de permissões relacionadas à funcionalidade 'Plantao'
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES (
           SEQ_PERMISSAO.NEXTVAL,
           'CadastrarPlantao',
           'Permite cadastrar um plantão.',
           (SELECT ID FROM FUNCIONALIDADE WHERE NOME = 'Plantao' FETCH FIRST 1 ROWS ONLY)
       );

INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES (
           SEQ_PERMISSAO.NEXTVAL,
           'ListarPlantao',
           'Permite listar os plantões.',
           (SELECT ID FROM FUNCIONALIDADE WHERE NOME = 'Plantao' FETCH FIRST 1 ROWS ONLY)
       );

INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES (
           SEQ_PERMISSAO.NEXTVAL,
           'ExcluirPlantao',
           'Permite excluir os plantões.',
           (SELECT ID FROM FUNCIONALIDADE WHERE NOME = 'Plantao' FETCH FIRST 1 ROWS ONLY)
       );

INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES (
           SEQ_PERMISSAO.NEXTVAL,
           'VisualizarPlantao',
           'Permite acesso ao ícone dos plantões.',
           (SELECT ID FROM FUNCIONALIDADE WHERE NOME = 'Plantao' FETCH FIRST 1 ROWS ONLY)
       );

INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES (
           SEQ_PERMISSAO.NEXTVAL,
           'CadastrarRegistroPlantao',
           'Permite cadastrar um registro no plantão.',
           (SELECT ID FROM FUNCIONALIDADE WHERE NOME = 'Plantao' FETCH FIRST 1 ROWS ONLY)
       );

INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES (
           SEQ_PERMISSAO.NEXTVAL,
           'ListarRegistroPlantao',
           'Permite listar os registros dos plantões.',
           (SELECT ID FROM FUNCIONALIDADE WHERE NOME = 'Plantao' FETCH FIRST 1 ROWS ONLY)
       );

INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES (
           SEQ_PERMISSAO.NEXTVAL,
           'AlterarPlantao',
           'Permite alterar um plantão.',
           (SELECT ID FROM FUNCIONALIDADE WHERE NOME = 'Plantao' FETCH FIRST 1 ROWS ONLY)
       );

INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES (
           SEQ_PERMISSAO.NEXTVAL,
           'ListarTodosPlantoes',
           'Permite listar todos os plantões de todas as unidades.',
           (SELECT ID FROM FUNCIONALIDADE WHERE NOME = 'Plantao' FETCH FIRST 1 ROWS ONLY)
       );
