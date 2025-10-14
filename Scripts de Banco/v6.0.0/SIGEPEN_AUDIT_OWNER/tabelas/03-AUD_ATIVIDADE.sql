--------------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA TABELA DE AUDITORIA AUD_ATIVIDADE
--------------------------------------------------------------

CREATE TABLE AUD_ATIVIDADE 
(
  ID                   NUMBER(19,0) NOT NULL,
  REV                  NUMBER(19) NOT NULL,
  ACAO                 NUMBER(3),  
  DESCRICAO     	   VARCHAR2(50 CHAR),           
  SITUACAO_PLANTAO     NUMBER(1),
  TIPO                 NUMBER(1)
);

ALTER TABLE AUD_ATIVIDADE ADD CONSTRAINT PK_AUD_ATIVIDADE   PRIMARY KEY (ID, REV);
GRANT SELECT, INSERT ON AUD_ATIVIDADE TO SIGEPEN_USER;

