create table AUD_AUTORIZACAO_VALIDACAO_SEM_BIO
(
  id                NUMBER(19) not null,
  rev               number(19) not null,
  acao              number(3)  not null,
  data_autorizacao  DATE not null,
  observacao        VARCHAR2(250 CHAR),
  usuario_id        NUMBER(19),
  versao            NUMBER(19),
  passagem_id       NUMBER(19),
  entrada_saida_id  NUMBER(19),
  tipo              VARCHAR2(50 CHAR)
) TABLESPACE TBS_SIGEPEN_AUDIT_OWNER_D;

ALTER TABLE AUD_AUTORIZACAO_VALIDACAO_SEM_BIO ADD CONSTRAINT PK_AUD_AUT_VALID_SEM_BIO PRIMARY KEY (ID, REV) USING INDEX TABLESPACE TBS_SIGEPEN_AUDIT_OWNER_I;
ALTER TABLE AUD_AUTORIZACAO_VALIDACAO_SEM_BIO ADD CONSTRAINT FK_AUD_AUT_VALID_SEM_BIO_REV FOREIGN KEY (REV) REFERENCES REVISAO (ID);

grant select, insert on AUD_AUTORIZACAO_VALIDACAO_SEM_BIO to SIGEPEN_USER;
