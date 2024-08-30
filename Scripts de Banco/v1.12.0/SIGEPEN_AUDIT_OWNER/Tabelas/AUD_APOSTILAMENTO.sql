-------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA TABELA AUD_APOSTILAMENTO
-------------------------------------------------

CREATE TABLE AUD_APOSTILAMENTO
(
    ID                  NUMBER(19) NOT NULL,
    REV                 NUMBER(19) NOT NULL,
    ACAO                NUMBER(3),
    DATA_INICIO  		DATE NULL,
	DATA_FIM			DATE NULL,
	CONTRATO_ID		    NUMBER(19,0) NULL
);

ALTER TABLE AUD_APOSTILAMENTO ADD CONSTRAINT PK_AUD_APOSTILAMENTO PRIMARY KEY (ID, REV);

GRANT SELECT, INSERT ON AUD_APOSTILAMENTO TO sigepen_user;