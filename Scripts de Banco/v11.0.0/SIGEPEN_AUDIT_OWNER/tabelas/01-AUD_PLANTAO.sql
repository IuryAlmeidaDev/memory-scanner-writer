--------------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA TABELA DE AUDITORIA AUD_PLANTAO
--------------------------------------------------------------

ALTER TABLE AUD_PLANTAO ADD USUARIO_INICIO_ID NUMBER;
            
GRANT SELECT, INSERT, UPDATE, DELETE ON AUD_PLANTAO TO sigepen_user;

ALTER TABLE AUD_PLANTAO ADD USUARIO_FIM_ID NUMBER;
            
GRANT SELECT, INSERT, UPDATE, DELETE ON AUD_PLANTAO TO sigepen_user;