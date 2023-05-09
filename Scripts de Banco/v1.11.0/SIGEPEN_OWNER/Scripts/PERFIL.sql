-----------------------------------------------------------
--- SCRIPT DE INSERÇÃO DE DADOS NA TABELA PERMISSAO
-----------------------------------------------------------

--Permissões de Gestão de vagas

INSERT INTO SIGEPEN_OWNER_DES.PERMISSAO
(ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES(221,'ListarQuadroVagas', 'Permite acesso ao menu e a tela de listar quadro de vagas.', 2);