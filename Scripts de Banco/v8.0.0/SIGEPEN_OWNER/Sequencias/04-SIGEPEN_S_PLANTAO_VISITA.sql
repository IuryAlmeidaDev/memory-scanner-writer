-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SIGEPEN_S_PLANTAO_VISITA
-------------------------------------------------------

CREATE SEQUENCE SIGEPEN_S_PLANTAO_VISITA
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SIGEPEN_S_PLANTAO_VISITA TO sigepen_user;