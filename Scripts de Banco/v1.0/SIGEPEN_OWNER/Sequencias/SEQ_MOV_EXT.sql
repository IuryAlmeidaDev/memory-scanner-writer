-----------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SEQ_MOV_EXT
-----------------------------------------------

CREATE SEQUENCE SEQ_MOV_EXT
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_MOV_EXT TO SIGEPEN_USER;