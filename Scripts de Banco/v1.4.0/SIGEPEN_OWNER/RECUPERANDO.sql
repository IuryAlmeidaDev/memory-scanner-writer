alter table recuperando add identidade_genero_id number(19);

ALTER TABLE RECUPERANDO ADD CONSTRAINT FK_RECUPERANDO_ID_GENERO FOREIGN KEY (IDENTIDADE_GENERO_ID) REFERENCES IDENTIDADE_GENERO (ID);

CREATE INDEX FK_RECUPERANDO_ID_GENERO ON RECUPERANDO(IDENTIDADE_GENERO_ID) TABLESPACE TBS_SIGEPEN_OWNER_I;

update recuperando r set r.identidade_genero_id = 2, r.orientacao_sexual_id = null
 where r.id in (select r.id from sigepen_owner.recuperando r where r.orientacao_sexual_id = 4);