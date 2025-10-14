--- SCRIPT DE INSERÇÃO DA TABELA PERMISSÃO
------------------------------------------------------------------

INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES (SEQ_PERMISSAO.NEXTVAL, 'ListarTodosPlantoes', 'Permite listar todos plantoes de todas unidades.',
        (SELECT f.ID FROM FUNCIONALIDADE f WHERE f.NOME = 'Seguranca e Disciplina' FETCH FIRST 1 ROWS ONLY));