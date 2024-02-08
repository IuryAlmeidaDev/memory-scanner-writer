-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE SEQ_REFEICAO
-------------------------------------------------------

CREATE SEQUENCE SEQ_REFEICAO
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_REFEICAO TO sigepen_user;