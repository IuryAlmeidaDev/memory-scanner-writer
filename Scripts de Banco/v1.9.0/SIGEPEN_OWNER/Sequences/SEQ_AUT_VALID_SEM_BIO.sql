create sequence SEQ_AUT_VALID_SEM_BIO
minvalue 1
maxvalue 9999999999999999999999999999
start with 1
increment by 1
nocache;

grant select on SEQ_AUT_VALID_SEM_BIO to sigepen_user;
