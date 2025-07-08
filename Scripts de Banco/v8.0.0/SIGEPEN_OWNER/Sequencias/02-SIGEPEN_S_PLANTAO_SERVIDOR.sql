-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SIGEPEN_S_PLANTAO_SERVIDOR
-------------------------------------------------------

CREATE SEQUENCE SIGEPEN_S_PLANTAO_SERVIDOR
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SIGEPEN_S_PLANTAO_SERVIDOR TO sigepen_user;