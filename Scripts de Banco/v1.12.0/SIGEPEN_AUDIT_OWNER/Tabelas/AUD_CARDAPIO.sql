-------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA TABELA AUD_CARDAPIO
-------------------------------------------------

CREATE TABLE AUD_CARDAPIO
(
	ID             		NUMBER(19) NOT NULL,
	REV            		NUMBER(19) NOT NULL,
	ACAO           		NUMBER(3),
	SEMANA         		NUMBER(19,0),
	CODG_DIA_SEMANA	    NUMBER(19,0),
	TIPO_REFEICAO_ID	NUMBER(19,0),
    CONTRATO_ID	        NUMBER(19,0),
    TIPO_DIETA_ID	    NUMBER(19,0),
    FESTIVO             NUMBER(1,0),
    "DATA"              DATE

);

ALTER TABLE AUD_CARDAPIO ADD CONSTRAINT PK_AUD_CARDAPIO PRIMARY KEY (ID, REV);

GRANT SELECT, INSERT ON AUD_CARDAPIO TO sigepen_user;