create table USERTBL
(
  id       NUMBER(19) not null,
  dbid     VARCHAR2(100),
  template BLOB
)
tablespace TBS_SIGEPEN_OWNER_D;

create index SUBJECTID_IDX on USERTBL (DBID) tablespace TBS_SIGEPEN_OWNER_D;
 
alter table USERTBL add primary key (ID) using index tablespace TBS_SIGEPEN_OWNER_D;

grant select, insert, update, delete on USERTBL to SIGEPEN_USER;
