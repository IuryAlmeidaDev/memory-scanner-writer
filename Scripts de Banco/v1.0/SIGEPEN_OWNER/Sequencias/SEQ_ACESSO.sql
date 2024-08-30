-----------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SEQ_ACESSO
-----------------------------------------------

CREATE SEQUENCE SEQ_ACESSO
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_ACESSO TO SIGEPEN_USER;


