-------------------------------------------------
--- ADICIONAR COLUNA HASH_IMPRESSAO NA TABELA LOG
-------------------------------------------------

ALTER TABLE SIGEPEN_AUDIT_OWNER.LOG ADD HASH_IMPRESSAO VARCHAR2(100 CHAR);

COMMENT ON COLUMN SIGEPEN_AUDIT_OWNER.LOG.HASH_IMPRESSAO IS 'Hash para identificação de impressões de relatórios';
