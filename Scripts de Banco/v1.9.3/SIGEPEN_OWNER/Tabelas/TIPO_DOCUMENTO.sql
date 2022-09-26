alter table TIPO_DOCUMENTO add tem_foto number(1) default 0 not null;

update tipo_documento set tem_foto = 1 where id in (6,7,11,15,14,9);
