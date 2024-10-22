create or replace view ULTIMA_LOTACAO as
select l.id, l.passagem_id, l.estrutura_id, l.data_entrada from passagem_lotacao l
where l.id in (
select max(l2.id) from passagem_lotacao l2
where l2.passagem_id = l.passagem_id)
--and l.passagem_id = 21335
;