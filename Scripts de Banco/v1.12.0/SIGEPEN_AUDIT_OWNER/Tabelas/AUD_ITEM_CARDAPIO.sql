-------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA TABELA AUD_ITEM_CARDAPIO
-------------------------------------------------

CREATE TABLE AUD_ITEM_CARDAPIO
(
	ID             		NUMBER(19) NOT NULL,
	REV            		NUMBER(19) NOT NULL,
	ACAO           		NUMBER(3),
	STATUS 			    NUMBER(10),
	MASC_MG		        NUMBER(17,2),
	FEM_MG	            NUMBER(17,2),
	ALIMENTO_ID 	    NUMBER(19,0),
    CARDAPIO_ID 	    NUMBER(19,0)

);

ALTER TABLE AUD_ITEM_CARDAPIO ADD CONSTRAINT PK_AUD_ITEM_CARDAPIO PRIMARY KEY (ID, REV);

GRANT SELECT, INSERT ON AUD_ITEM_CARDAPIO TO sigepen_user;