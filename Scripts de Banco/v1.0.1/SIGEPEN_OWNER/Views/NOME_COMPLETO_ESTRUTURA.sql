-------------------------------------------------
--- SCRIPT DE CRIAÇÃO DA VIEW NOME_COMPLETO_ESTRUTURA
-------------------------------------------------

create or replace view nome_completo_estrutura as
select e.id, sigepen_owner.buscaestruturanomecompleto(e.id) nome_completo, 
       (case when e.estruturapai_id is null then 1 else 0 end) is_unidade 
  from estrutura e;
