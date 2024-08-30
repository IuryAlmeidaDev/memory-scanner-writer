alter table PESSOA
  drop constraint FK_PESSOA_CIDADE;
alter table PESSOA
  add constraint FK_PESSOA_CIDADE foreign key (CIDADE_ID)
  references municipio (ID);
alter table PESSOA
  drop constraint FK_PESSOA_NACIONALIDADE;
alter table PESSOA
  add constraint FK_PESSOA_NACIONALIDADE foreign key (NACIONALIDADE_ID)
  references nacionalidade (ID);
alter table PESSOA
  drop constraint FK_PESSOA_PAIS;
alter table PESSOA
  add constraint FK_PESSOA_PAIS foreign key (PAIS_ID)
  references pais (ID);
