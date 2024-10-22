alter table DOCUMENTO
  drop constraint FK_DOCUMENTO_TIPO_DOCUMENTO;
alter table DOCUMENTO
  add constraint FK_DOCUMENTO_TIPO_DOCUMENTO foreign key (TIPO_DOCUMENTO_ID)
  references tipo_documento (ID);
