INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
values ((SELECT COALESCE(MAX(ID), 0) + 1 FROM PERMISSAO), 'PermiteCadastrarPlantao',
        'Permite acesso a cadastrar o plantão do servidor.',
        (SELECT f.ID FROM FUNCIONALIDADE f WHERE f.NOME = 'Tabelas Auxiliares' FETCH FIRST 1 ROWS ONLY));