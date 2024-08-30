-----------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SEQ_LOG
-----------------------------------------------

CREATE SEQUENCE SEQ_LOG
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_LOG TO SIGEPEN_USER;