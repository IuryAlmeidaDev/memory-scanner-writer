begin
  for c in (select p.autorizacao_cad_id aut_id, r.id recup_id from pessoa p, recuperando r where p.autorizacao_cad_id is not null and p.id = r.pessoa_id)
  loop
    update autorizacao_cadastro_bio set recuperando_id = c.recup_id where id = c.aut_id;
  end loop;
end;
