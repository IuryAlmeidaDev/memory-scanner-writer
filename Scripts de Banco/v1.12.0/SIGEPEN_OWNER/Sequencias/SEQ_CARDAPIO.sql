-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SEQ_CARDAPIO
-------------------------------------------------------

CREATE SEQUENCE SEQ_CARDAPIO
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_CARDAPIO TO sigepen_user;