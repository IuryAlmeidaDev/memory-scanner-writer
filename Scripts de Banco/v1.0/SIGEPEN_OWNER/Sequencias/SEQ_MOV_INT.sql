-----------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SEQ_MOV_INT
-----------------------------------------------

CREATE SEQUENCE SEQ_MOV_INT
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_MOV_INT TO SIGEPEN_USER;