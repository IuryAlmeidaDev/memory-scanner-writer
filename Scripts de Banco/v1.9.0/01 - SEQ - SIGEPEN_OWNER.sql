CREATE SEQUENCE SEQ_ENTRADA_SAIDA
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_ENTRADA_SAIDA TO SIGEPEN_USER;

CREATE SEQUENCE SEQ_ENTRADA_SAIDA_RECUPERANDO
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_ENTRADA_SAIDA_RECUPERANDO TO SIGEPEN_USER;

CREATE SEQUENCE SEQ_PARAMETRO_VISITA
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_PARAMETRO_VISITA TO SIGEPEN_USER;

CREATE SEQUENCE SEQ_PARAMETRO_VISITA_BLOQUEIO
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_PARAMETRO_VISITA_BLOQUEIO TO SIGEPEN_USER;

CREATE SEQUENCE SEQ_PARAMETRO_VISITA_ESTRUTURA
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_PARAMETRO_VISITA_ESTRUTURA TO SIGEPEN_USER;

create sequence SEQ_SGP_PESSOA_SOLICITACAO_VISITA start with 1 increment by 1 nomaxvalue nocycle nocache;

GRANT SELECT ON SEQ_SGP_PESSOA_SOLICITACAO_VISITA TO SIGEPEN_USER;

create sequence SGP_S_CREDENCIAL_CERTIDAO start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SGP_S_CREDENCIAL_CERTIDAO to sigepen_user;

create sequence SGP_S_CREDENCIAL_DOCUMENTO start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SGP_S_CREDENCIAL_DOCUMENTO to sigepen_user;

create sequence SIGEPEN_S_CERTIDAO start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SIGEPEN_S_CERTIDAO to sigepen_user;

CREATE SEQUENCE SIGEPEN_S_FALTA_DISCIPINA
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

grant select on SIGEPEN_S_FALTA_DISCIPINA to sigepen_user;

create sequence SIGEPEN_S_IMPEDIMENTO start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SIGEPEN_S_IMPEDIMENTO to sigepen_user;

create sequence SIGEPEN_S_PARAMETRO_UNIDADE start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SIGEPEN_S_PARAMETRO_UNIDADE to sigepen_user;

create sequence SIGEPEN_S_SOLICITACAO_VISITA start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SIGEPEN_S_SOLICITACAO_VISITA to sigepen_user;

CREATE SEQUENCE SIGEPEN_S_TIPO_FALTA_DISCIPINA
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

grant select on SIGEPEN_S_TIPO_FALTA_DISCIPINA to sigepen_user;

create sequence SIGEPEN_S_TIPO_IMPEDIMENTO start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SIGEPEN_S_TIPO_IMPEDIMENTO to sigepen_user;

create sequence SIGEPEN_S_TIPO_VISITA start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SIGEPEN_S_TIPO_VISITA to sigepen_user;

create sequence SIGEPEN_S_TIPO_VISITA_CERT start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SIGEPEN_S_TIPO_VISITA_CERT to sigepen_user;

create sequence SIGEPEN_S_TIPO_VISITA_DOC start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SIGEPEN_S_TIPO_VISITA_DOC to sigepen_user;

create sequence SIGEPEN_S_VISITANT_RECUPERANDO start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SIGEPEN_S_VISITANT_RECUPERANDO to sigepen_user;

create sequence SIGEPEN_S_VISITANTE start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SIGEPEN_S_VISITANTE to sigepen_user;

create sequence SIGEPEN_S_VISITANTE_CERTIDAO start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SIGEPEN_S_VISITANTE_CERTIDAO to sigepen_user;

create sequence SIGEPEN_S_VISITANTE_CREDENCIAL start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SIGEPEN_S_VISITANTE_CREDENCIAL to sigepen_user;

create sequence SIGEPEN_S_VISITANTE_DOCUMENTO start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SIGEPEN_S_VISITANTE_DOCUMENTO to sigepen_user;

create sequence SIGEPEN_S_VISITANTE_FOTO start with 1 increment by 1 nomaxvalue nocycle nocache;

grant select on SIGEPEN_S_VISITANTE_FOTO to sigepen_user;

create sequence SEQ_AUT_VALID_SEM_BIO
minvalue 1
maxvalue 9999999999999999999999999999
start with 1
increment by 1
nocache;

grant select on SEQ_AUT_VALID_SEM_BIO to sigepen_user;