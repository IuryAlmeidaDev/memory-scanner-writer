-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SEQ_PEDIDO_ALIMENTO
-------------------------------------------------------

CREATE SEQUENCE SEQ_PEDIDO_ALIMENTO
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_PEDIDO_ALIMENTO TO sigepen_user;

DROP SEQUENCE SEQ_PEDIDO_ALIMENTO;