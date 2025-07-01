------------------------------------------------------------------
--- SCRIPT DE ALTERAÇÃO DA TABELA AUD_SERVIDOR
------------------------------------------------------------------

ALTER TABLE AUD_SERVIDOR ADD DATA_DESLIGAMENTO  DATE NULL;
ALTER TABLE AUD_SERVIDOR ADD MOTIVO_DESLIGAMENTO VARCHAR2(50);
ALTER TABLE AUD_SERVIDOR ADD JUSTIFICATIVA VARCHAR2(250);


