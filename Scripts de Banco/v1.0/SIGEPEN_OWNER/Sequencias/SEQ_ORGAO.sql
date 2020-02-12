-----------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SEQ_ORGAO
-----------------------------------------------

CREATE SEQUENCE SEQ_ORGAO
START WITH 3
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_ORGAO TO SIGEPEN_USER;