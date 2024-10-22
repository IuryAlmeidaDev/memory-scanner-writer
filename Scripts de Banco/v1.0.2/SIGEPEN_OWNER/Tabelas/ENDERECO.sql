alter table ENDERECO
  drop constraint FK_ENDERECO_CIDADE;
alter table ENDERECO
  add constraint FK_ENDERECO_CIDADE foreign key (CIDADE_ID)
  references municipio (ID);
