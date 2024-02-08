-------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA TABELA AUD_TIPO_DIETA
-------------------------------------------------

CREATE TABLE AUD_TIPO_DIETA
(
    ID                  NUMBER(19) NOT NULL,
    REV                 NUMBER(19) NOT NULL,
    ACAO                NUMBER(3),
    DESCRICAO      		VARCHAR2(80 char),
    STATUS              NUMBER(1,0),
    ESPECIAL            NUMBER(1,0)
);

ALTER TABLE AUD_TIPO_DIETA ADD CONSTRAINT PK_AUD_TIPO_DIETA PRIMARY KEY (ID, REV);

GRANT SELECT, INSERT ON AUD_TIPO_DIETA TO sigepen_user;