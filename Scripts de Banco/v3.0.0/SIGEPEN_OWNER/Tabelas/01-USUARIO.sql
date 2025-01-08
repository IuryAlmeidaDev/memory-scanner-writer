------------------------------------------------------------------
--- SCRIPT DE ALTERAÇÃO DA TABELA USUARIO
------------------------------------------------------------------

ALTER TABLE USUARIO ADD IS_RESET_SENHA NUMBER(1) DEFAULT(0) NOT NULL;