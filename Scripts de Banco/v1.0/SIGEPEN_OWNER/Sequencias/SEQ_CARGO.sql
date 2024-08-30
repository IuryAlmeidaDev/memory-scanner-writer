-----------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SEQ_CARGO
-----------------------------------------------

CREATE SEQUENCE SEQ_CARGO
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_CARGO TO SIGEPEN_USER;