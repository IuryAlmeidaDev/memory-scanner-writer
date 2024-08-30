-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SEQ_CONTRATO
-------------------------------------------------------

CREATE SEQUENCE SEQ_CONTRATO
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_CONTRATO TO sigepen_user;