
---------------------------------------------------------------------------------------------------------------------------- 
                                           -- SCRIPT DE CRIAÇÃO PARA ISERÇÃO DAS PERMISSÕES  --
----------------------------------------------------------------------------------------------------------------------------


INSERT INTO FUNCIONALIDADE p (ID,NOME) values (10,'Plantao');
INSERT INTO PERMISSAO p (ID,NOME,DESCRICAO,FUNCIONALIDADE_ID) values (291,'CadastrarPlantao',	'Permite Cadastrar um Plantao.',10);
INSERT INTO PERMISSAO p (ID,NOME,DESCRICAO,FUNCIONALIDADE_ID) values (292,'ListarPlantao',	'Permite listar os plantoes.',10);
INSERT INTO PERMISSAO p (ID,NOME,DESCRICAO,FUNCIONALIDADE_ID) values (293,'ExcluirPlantao',	'Permite excluir os plantoes.',10);
INSERT INTO PERMISSAO p (ID,NOME,DESCRICAO,FUNCIONALIDADE_ID) values (294,'VisualizarPlantao',	'Permite acesso ao icone dos plantoes.',10);
INSERT INTO PERMISSAO p (ID,NOME,DESCRICAO,FUNCIONALIDADE_ID) values (300,'CadastrarRegistroPlantao',	'Permite Cadastrar um Registro no Plantao.',10);
INSERT INTO PERMISSAO p (ID,NOME,DESCRICAO,FUNCIONALIDADE_ID) values (301,'ListarRegistroPlantao',	'Permite listar os registros dos plantoes.',10);