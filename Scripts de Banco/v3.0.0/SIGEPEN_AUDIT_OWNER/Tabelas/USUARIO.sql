------------------------------------------------------------------
--- SCRIPT DE ALTERAÇÃO DA TABELA AUD_USUARIO
------------------------------------------------------------------

ALTER TABLE AUD_USUARIO ADD IS_RESET_SENHA NUMBER(1) DEFAULT(0) NOT NULL;