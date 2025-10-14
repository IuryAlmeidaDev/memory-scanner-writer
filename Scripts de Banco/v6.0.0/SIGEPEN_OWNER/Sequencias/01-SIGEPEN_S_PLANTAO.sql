-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SIGEPEN_S_PLANTAO
-------------------------------------------------------

DROP SEQUENCE SEQ_PLANTAO_GENERATOR

CREATE SEQUENCE SEQ_PLANTAO
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_PLANTAO TO sigepen_user;