-------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA TABELA AUD_PEDIDO_ALIMENTO
-------------------------------------------------

CREATE TABLE AUD_PEDIDO_ALIMENTO
(
    ID                  NUMBER(19) NOT NULL,
    REV                 NUMBER(19) NOT NULL,
    ACAO                NUMBER(3),
    DATA_EMISSAO        TIMESTAMP,
    USUARIO             VARCHAR2(100 CHAR),
    JSON_CONTEUDO       CLOB,
    STATUS              NUMBER(3)
);

ALTER TABLE AUD_PEDIDO_ALIMENTO ADD CONSTRAINT PK_AUD_PEDIDO_ALIMENTO PRIMARY KEY (ID, REV);

-------------------------------------------------
--- PERMISSÕES
-------------------------------------------------

GRANT SELECT, INSERT ON AUD_PEDIDO_ALIMENTO TO sigepen_user;