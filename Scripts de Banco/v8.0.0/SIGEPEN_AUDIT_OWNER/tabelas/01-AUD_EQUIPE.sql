--------------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA TABELA DE AUDITORIA AUD_EQUIPE
--------------------------------------------------------------

CREATE TABLE AUD_EQUIPE 
(
  ID                   NUMBER(19,0) NOT NULL,
  REV                  NUMBER(19) NOT NULL,
  ACAO                 NUMBER(3),
  TIPO_EQUIPE          VARCHAR(50),  
  ID_PLANTAO           NUMBER(19)
);

ALTER TABLE AUD_EQUIPE ADD CONSTRAINT PK_AUD_EQUIPE PRIMARY KEY (ID, REV);

GRANT SELECT, INSERT ON AUD_EQUIPE TO SIGEPEN_USER;