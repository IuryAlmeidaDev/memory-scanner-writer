-------------------------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SIGEPEN_S_SEQ_REGISTRO_PLANTAO_SERVIDOR
-------------------------------------------------------------------------

DROP SEQUENCE SEQ_REGISTRO_PLANTAO_SERVIDOR

CREATE SEQUENCE SEQ_REGISTRO_PLANTAO_SERVIDOR
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_REGISTRO_PLANTAO_SERVIDOR TO sigepen_user;
