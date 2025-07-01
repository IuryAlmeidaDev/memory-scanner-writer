------------------------------------------------------------------
--- SCRIPT DE ALTERAÇÃO DA TABELA SERVIDOR
------------------------------------------------------------------

ALTER TABLE SERVIDOR ADD DATA_DESLIGAMENTO  DATE NULL;
ALTER TABLE SERVIDOR ADD MOTIVO_DESLIGAMENTO VARCHAR2(50);
ALTER TABLE SERVIDOR ADD JUSTIFICATIVA VARCHAR2(250);

