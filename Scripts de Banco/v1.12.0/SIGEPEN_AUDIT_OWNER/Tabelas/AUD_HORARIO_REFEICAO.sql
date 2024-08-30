-------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA TABELA AUD_HORARIO_REFEICAO
-------------------------------------------------

CREATE TABLE AUD_HORARIO_REFEICAO
(
    ID                      NUMBER(19) NOT NULL,
    REV                     NUMBER(19) NOT NULL,
    ACAO                    NUMBER(3),
    TIPO_REFEICAO_ID	    NUMBER(19,0),
    CONTRATO_ID	            NUMBER(19,0),
    UNIDADE_PRISIONAL_ID    NUMBER(19,0),
	DATA_INICIO		        DATE,
	DATA_FIM			    DATE,
    HORARIO                 VARCHAR2(55)
);

ALTER TABLE AUD_HORARIO_REFEICAO ADD CONSTRAINT PK_AUD_HORARIO_REFEICAO PRIMARY KEY (ID, REV);

GRANT SELECT, INSERT ON AUD_HORARIO_REFEICAO TO sigepen_user;