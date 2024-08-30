alter table SERVIDOR
  drop constraint FK_SERVIDOR_ESCOLARIDADE;
alter table SERVIDOR
  add constraint FK_SERVIDOR_ESCOLARIDADE foreign key (ESCOLARIDADE_ID)
  references escolaridade (ID);
alter table SERVIDOR
  drop constraint FK_SERVIDOR_ESTADO_CIVIL;
alter table SERVIDOR
  add constraint FK_SERVIDOR_ESTADO_CIVIL foreign key (ESTADOCIVIL_ID)
  references estado_civil (ID);
