alter table ESTABELECIMENTO
  drop constraint FK_ESTABELECIMENTO_CIDADE;
alter table ESTABELECIMENTO
  add constraint FK_ESTABELECIMENTO_CIDADE foreign key (CIDADE_ID)
  references municipio (ID);