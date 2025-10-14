-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SIGEPEN_S_SIGEPEN_EQUIPE
-------------------------------------------------------

CREATE SEQUENCE SIGEPEN_S_SIGEPEN_EQUIPE
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SIGEPEN_S_SIGEPEN_EQUIPE TO sigepen_user;