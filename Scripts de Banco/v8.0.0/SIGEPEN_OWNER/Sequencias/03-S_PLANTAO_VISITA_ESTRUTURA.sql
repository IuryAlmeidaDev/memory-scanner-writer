-------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA SEQUENCE S_PLANTAO_VISITA_ESTRUTURA
-------------------------------------------------------

CREATE SEQUENCE S_PLANTAO_VISITA_ESTRUTURA
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON S_PLANTAO_VISITA_ESTRUTURA TO sigepen_user;