-----------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SEQ_FOTO
-----------------------------------------------

CREATE SEQUENCE SEQ_FOTO
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_FOTO TO SIGEPEN_USER;