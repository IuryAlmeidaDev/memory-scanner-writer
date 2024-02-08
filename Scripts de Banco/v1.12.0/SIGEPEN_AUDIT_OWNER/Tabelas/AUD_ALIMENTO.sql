-------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA TABELA AUD_ALIMENTO
-------------------------------------------------

CREATE TABLE AUD_ALIMENTO
(
    ID                  NUMBER(19) NOT NULL,
    REV                 NUMBER(19) NOT NULL,
    ACAO                NUMBER(3),
    DESCRICAO      		VARCHAR2(80 char),
    STATUS              NUMBER(1,0)
);

ALTER TABLE AUD_ALIMENTO ADD CONSTRAINT PK_AUD_ALIMENTO PRIMARY KEY (ID, REV)

GRANT SELECT, INSERT ON AUD_ALIMENTO TO sigepen_user;