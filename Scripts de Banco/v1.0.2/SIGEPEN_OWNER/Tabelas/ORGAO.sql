alter table ORGAO
  drop constraint FK_ORGAO_CIDADE;
alter table ORGAO
  add constraint FK_ORGAO_CIDADE foreign key (CIDADE_ID)
  references municipio (ID);
