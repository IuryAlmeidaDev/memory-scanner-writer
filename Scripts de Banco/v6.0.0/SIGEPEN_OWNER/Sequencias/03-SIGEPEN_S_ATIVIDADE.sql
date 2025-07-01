-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SIGEPEN_S_SEQ_ATIVIDADE
-------------------------------------------------------

DROP SEQUENCE SEQ_ATIVIDADE

CREATE SEQUENCE SEQ_ATIVIDADE
    START WITH 34
    INCREMENT BY 1
    NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_ATIVIDADE TO sigepen_user;
