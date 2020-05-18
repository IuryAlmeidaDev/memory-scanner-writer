alter table revisao add data date;
update revisao r set r.data = TO_DATE('01/01/1970 00:00:00','DD/MM/YYYY HH24:MI:SS') + (r.datahora /1000/60/60/24);
