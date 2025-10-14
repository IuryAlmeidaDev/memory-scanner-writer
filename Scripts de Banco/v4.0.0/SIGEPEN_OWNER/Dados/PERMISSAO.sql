insert into permissao (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID)
values ((SELECT COALESCE(MAX(ID), 0) + 1 FROM PERMISSAO), 'AbrirRollParente', 'Habilitar o Roll de parente.',
        (SELECT f.ID FROM FUNCIONALIDADE f WHERE f.NOME = 'Recuperando' FETCH FIRST 1 ROWS ONLY));