alter table RECUPERANDO
  drop constraint FK_RECUPERANDO_ESCOLARIDADE;
alter table RECUPERANDO
  add constraint FK_RECUPERANDO_ESCOLARIDADE foreign key (ESCOLARIDADE_ID)
  references escolaridade (ID);
alter table RECUPERANDO
  drop constraint FK_RECUPERANDO_ESTADO_CIVIL;
alter table RECUPERANDO
  add constraint FK_RECUPERANDO_ESTADO_CIVIL foreign key (ESTADO_CIVIL_ID)
  references estado_civil (ID);
