create table AUD_PASSAGEM_PROCEDENCIA
(
  id             NUMBER(19) not null,
  rev            NUMBER(19) not null,
  acao           NUMBER(3),
  observacao     VARCHAR2(250 CHAR),
  passagem_id    NUMBER(19),
  procedencia_id NUMBER(19)
)
tablespace TBS_SIGEPEN_OWNER_D;

alter table AUD_PASSAGEM_PROCEDENCIA
  add constraint PK_AUD_PASSAGEM_PROCEDENCIA primary key (ID, REV)
  using index 
  tablespace TBS_SIGEPEN_AUDIT_OWNER_I;
  
alter table AUD_PASSAGEM_PROCEDENCIA
  add constraint FK_AUD_PASSAGEM_PROC_REVISAO foreign key (REV)
  references REVISAO (ID);

grant select, insert, update, delete on AUD_PASSAGEM_PROCEDENCIA to SIGEPEN_USER;
