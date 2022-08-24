create or replace view ultima_passagem as
select p.recuperando_id, p.id, p.data_entrada, (case when p.tipo_desligamento is not null and p.tipo_desligamento = 'OBITO' then 0 else 1 end) ta_vivo from passagem p
where p.id in (
select max(p2.id) from passagem p2
where p2.recuperando_id = p.recuperando_id)
--and p.recuperando_id = 4616
;
