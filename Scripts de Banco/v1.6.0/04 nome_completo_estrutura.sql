create or replace view nome_completo_estrutura as
select e.id, buscaestruturanomecompleto(e.id) nome_completo, buscaestruturanomecompletocomunidade(e.id) nome_completo_unidade,
       (case when e.estruturapai_id is null then 1 else 0 end) is_unidade
  from estrutura e;
