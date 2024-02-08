-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SEQ_ALIMENTO
-------------------------------------------------------

CREATE SEQUENCE SEQ_ALIMENTO
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_ALIMENTO TO sigepen_user;