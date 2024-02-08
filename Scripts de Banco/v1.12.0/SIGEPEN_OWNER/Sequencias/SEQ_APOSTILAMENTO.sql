-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SEQ_APOSTILAMENTO
-------------------------------------------------------

CREATE SEQUENCE SEQ_APOSTILAMENTO
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_APOSTILAMENTO TO sigepen_user;