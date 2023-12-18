-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SEQ_TIPO_DIETA
-------------------------------------------------------

CREATE SEQUENCE SEQ_TIPO_DIETA
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_TIPO_DIETA TO SIGEPEN_USER_DES;