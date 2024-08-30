create or replace trigger usertbl_autonumber
before insert on usertbl
for each row
  
begin
    select usertbl_seq.nextval into :new.id from dual;
end;