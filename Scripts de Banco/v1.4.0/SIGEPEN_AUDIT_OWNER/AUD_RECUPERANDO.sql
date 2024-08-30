ALTER TABLE AUD_RECUPERANDO add identidade_genero_id number(19);

update aud_recuperando r set r.identidade_genero_id = 2, r.orientacao_sexual_id = null
 where r.id in (select r.id from aud_recuperando r where r.orientacao_sexual_id = 4);