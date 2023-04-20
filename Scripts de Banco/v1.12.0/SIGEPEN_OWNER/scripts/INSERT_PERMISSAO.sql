INSERT INTO SIGEPEN_OWNER_DES.PERMISSAO
(ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES(222,'ListarFornecedor', 'Permite acesso ao menu e a tela de listar Fornecedores.',
(SELECT ID FROM SIGEPEN_OWNER_DES.FUNCIONALIDADE f
WHERE f.nome LIKE '%Mapa Alimentacao%')
);

INSERT INTO SIGEPEN_OWNER_DES.PERMISSAO
(ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES(223,'CadastrarFornecedor', 'Permite acesso ao menu e a tela de cadastro Fornecedores.',
(SELECT ID FROM SIGEPEN_OWNER_DES.FUNCIONALIDADE f
WHERE f.nome LIKE '%Mapa Alimentacao%')
);


INSERT INTO SIGEPEN_OWNER_DES.PERMISSAO
(ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES(224,'Visualizarfornecedor', 'Permite acesso ao menu e a tela de visualizar Fornecedores.',
(SELECT ID FROM SIGEPEN_OWNER_DES.FUNCIONALIDADE f
WHERE f.nome LIKE '%Mapa Alimentacao%')
);

INSERT INTO SIGEPEN_OWNER_DES.PERMISSAO
(ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
VALUES(225,'Alterarfornecedor', 'Permite acesso ao menu e a tela de alterar Fornecedores.',
(SELECT ID FROM SIGEPEN_OWNER_DES.FUNCIONALIDADE f
WHERE f.nome LIKE '%Mapa Alimentacao%')
);