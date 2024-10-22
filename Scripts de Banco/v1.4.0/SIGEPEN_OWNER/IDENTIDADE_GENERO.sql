create table IDENTIDADE_GENERO
(
  id        NUMBER(19) not null,
  descricao VARCHAR2(80 CHAR) not null
) tablespace TBS_SIGEPEN_OWNER_D;

alter table IDENTIDADE_GENERO
  add constraint PK_IDENTIDADE_GENERO primary key (ID)
  using index tablespace TBS_SIGEPEN_OWNER_I;

grant select, insert, update, delete on IDENTIDADE_GENERO to SIGEPEN_USER;

insert into identidade_genero (id, descricao) values (1, 'Travesti');
insert into identidade_genero (id, descricao) values (2, 'Transexual');