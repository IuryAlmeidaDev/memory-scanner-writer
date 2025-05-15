--------------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA TABELA DE AUDITORIA AUD_PLANTAO
--------------------------------------------------------------

CREATE TABLE AUD_PLANTAO 
(
  ID                   NUMBER(19,0) NOT NULL,
  REV                  NUMBER(19) NOT NULL,
  ACAO                 NUMBER(3),
  PLANTAO              NUMBER(1) NOT NULL,  
  DATA_PLANTAO         DATE NOT NULL,
  UNIDADE_PRISIONAL_ID NUMBER(19,0) NOT NULL,
  STATUS               NUMBER(19,0)
);

ALTER TABLE AUD_PLANTAO ADD CONSTRAINT PK_AUD_PLANTAO PRIMARY KEY (ID, REV);

GRANT SELECT, INSERT ON AUD_PLANTAO TO SIGEPEN_USER;