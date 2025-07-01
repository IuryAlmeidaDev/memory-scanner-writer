------------------------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA TABELA SIGEPEN_ATIVIDADE --- 
------------------------------------------------------------------

CREATE TABLE ATIVIDADE 
(
  ID            	   NUMBER(19,0) NOT NULL,
  DESCRICAO     	   VARCHAR2(50 CHAR),           
  SITUACAO_PLANTAO     NUMBER(1),
  TIPO                 NUMBER(1)
);

ALTER TABLE ATIVIDADE ADD CONSTRAINT PK_ATIVIDADE PRIMARY KEY (ID);
GRANT SELECT, INSERT, UPDATE, DELETE ON ATIVIDADE TO SIGEPEN_USER;