create table AUTORIZACAO_VALIDACAO_SEM_BIO
(
  id                NUMBER(19) not null,
  data_autorizacao  DATE not null,
  observacao        VARCHAR2(250 CHAR),
  usuario_id        NUMBER(19),
  versao            NUMBER(19),
  passagem_id       NUMBER(19),
  entrada_saida_id  NUMBER(19),
  tipo              VARCHAR2(50 CHAR)
) TABLESPACE TBS_SIGEPEN_OWNER_D;

alter table AUTORIZACAO_VALIDACAO_SEM_BIO add constraint PK_AUT_VALID_BIO primary key (ID) using index tablespace TBS_SIGEPEN_OWNER_I;

alter table AUTORIZACAO_VALIDACAO_SEM_BIO add constraint FK_AUT_VALID_BIO_USUARIO foreign key (USUARIO_ID) references USUARIO (ID);
create index FK_AUT_VALID_BIO_USUARIO on AUTORIZACAO_VALIDACAO_SEM_BIO (USUARIO_ID) tablespace TBS_SIGEPEN_OWNER_I;

alter table AUTORIZACAO_VALIDACAO_SEM_BIO add constraint FK_AUT_VALID_BIO_PASS foreign key (PASSAGEM_ID) references PASSAGEM (ID);
create index FK_AUT_VALID_BIO_PASS on AUTORIZACAO_VALIDACAO_SEM_BIO (PASSAGEM_ID) tablespace TBS_SIGEPEN_OWNER_I;

alter table AUTORIZACAO_VALIDACAO_SEM_BIO add constraint FK_AUT_VALID_BIO_ES foreign key (ENTRADA_SAIDA_ID) references SGP_ENTRADA_SAIDA (ID);
create index FK_AUT_VALID_BIO_ES on AUTORIZACAO_VALIDACAO_SEM_BIO (ENTRADA_SAIDA_ID) tablespace TBS_SIGEPEN_OWNER_I;

grant select, insert, update, delete on AUTORIZACAO_VALIDACAO_SEM_BIO to SIGEPEN_USER;
