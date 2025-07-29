--------------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA TABELA DE AUDITORIA AUD_PLANTAO_VISITA_ESTRUTURA
--------------------------------------------------------------

CREATE TABLE AUD_PLANTAO_VISITA_ESTRUTURA 
(
  ID                   NUMBER(19,0) NOT NULL,
  REV                  NUMBER(19) NOT NULL,
  ACAO                 NUMBER(3),
  ESTRUTURA            VARCHAR(200),
  HOMEM                NUMBER(19),
  MULHER               NUMBER(19),
  MENOR                NUMBER(19),
  OCORRENCIA                NUMBER(19),
  RECUPERANDO          NUMBER(19),
  HORA_INICIO          VARCHAR(10),
  HORA_FIM             VARCHAR(10),
  ID_PLANTAO_VISITA    NUMBER(19)
);

ALTER TABLE AUD_PLANTAO_VISITA_ESTRUTURA ADD CONSTRAINT PK_AUD_VISITA_ESTRUTURA PRIMARY KEY (ID, REV);

GRANT SELECT, INSERT ON AUD_PLANTAO_VISITA_ESTRUTURA TO SIGEPEN_USER;