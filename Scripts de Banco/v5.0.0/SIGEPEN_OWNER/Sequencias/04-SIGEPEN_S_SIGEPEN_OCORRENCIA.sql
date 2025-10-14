-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SIGEPEN_S_SIGEPEN_OCORRENCIA
-------------------------------------------------------

CREATE SEQUENCE SIGEPEN_S_SIGEPEN_OCORRENCIA
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SIGEPEN_S_SIGEPEN_OCORRENCIA TO sigepen_user;