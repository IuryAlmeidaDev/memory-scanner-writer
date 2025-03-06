-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SIGEPEN_S_PEDIDO_ALIMENTO_GEN
-------------------------------------------------------

CREATE SEQUENCE SIGEPEN_S_PEDIDO_ALIMENTO_GEN
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SIGEPEN_S_PEDIDO_ALIMENTO_GEN TO sigepen_user;