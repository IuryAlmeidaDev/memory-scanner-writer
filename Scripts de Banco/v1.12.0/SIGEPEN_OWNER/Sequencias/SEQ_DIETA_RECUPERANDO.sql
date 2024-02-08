-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SEQ_DIETA_RECUPERANDO
-------------------------------------------------------

CREATE SEQUENCE SEQ_DIETA_RECUPERANDO
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_DIETA_RECUPERANDO TO sigepen_user;