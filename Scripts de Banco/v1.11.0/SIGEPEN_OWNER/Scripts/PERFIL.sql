-----------------------------------------------------------
--- SCRIPT DE INSERÇÃO DE DADOS NA TABELA PERMISSAO
-----------------------------------------------------------

--Permissões de Gestão de vagas

INSERT INTO SIGEPEN_OWNER_DES.PERMISSAO
(ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES(221,'ListarQuadroVagas', 'Permite acesso ao menu e a tela de listar quadro de vagas.', 2);

INSERT INTO SIGEPEN_OWNER_DES.PERMISSAO
(ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES(222,'VisualizarQuadroVagas', 'Permite visualizar quadro de vagas.', 2);

INSERT INTO SIGEPEN_OWNER_DES.PERMISSAO
(ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES(223,'ListarTodosQuadroVagas', 'Permite listar todas os quadro de vagas.', 2);