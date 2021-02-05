alter table PARAMETRO add id number null;
update parametro set id = 1 where nome = 'VERSAO_DESKTOP';
update parametro set id = 2 where nome = 'VERSAO_CHROME';
update parametro set id = 3 where nome = 'VERSAO_FIREFOX';
alter table PARAMETRO modify id number not null;
