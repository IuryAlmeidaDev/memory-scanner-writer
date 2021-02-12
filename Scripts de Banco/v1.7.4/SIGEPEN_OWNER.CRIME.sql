CREATE SEQUENCE SEQ_CRIME
START WITH 1318
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

GRANT SELECT ON SEQ_CRIME TO SIGEPEN_USER;

alter table CRIME modify id_legislacao null;

insert into crime(id,
                  detalhamento,
                  artigo,
                  paragrafo,
                  inciso,
                  alinea,
                  id_legislacao,
                  nome_legislacao) 
                  values (seq_crime.nextval, 
                  'Utilizar, como prova de propriedade ou de direitos a ela relativos, documento expedido pelo IBRA para fins cadastrais ou tributários, em prejuízo de outrem ou em proveito próprio ou alheio.', 
                  '19', null, null, null, null, 'LEI REFORMA AGRARIA (LEI Nº 4.947/66)');
                  
insert into crime(id,
                  detalhamento,
                  artigo,
                  paragrafo,
                  inciso,
                  alinea,
                  id_legislacao,
                  nome_legislacao) 
                  values (seq_crime.nextval, 
                  'Se o agente é funcionário público e comete o crime prevalecendo-se do cargo, aumenta-se a pena de sexta parte.', 
                  '19', 'UNICO', null, null, null, 'LEI REFORMA AGRARIA (LEI Nº 4.947/66)');
                  
insert into crime(id,
                  detalhamento,
                  artigo,
                  paragrafo,
                  inciso,
                  alinea,
                  id_legislacao,
                  nome_legislacao) 
                  values (seq_crime.nextval, 
                  'Invadir, com intenção de ocupá-las, terras da União, dos Estados e dos Municípios.', 
                  '20', null, null, null, null, 'LEI REFORMA AGRARIA (LEI Nº 4.947/66)');
                  
insert into crime(id,
                  detalhamento,
                  artigo,
                  paragrafo,
                  inciso,
                  alinea,
                  id_legislacao,
                  nome_legislacao) 
                  values (seq_crime.nextval, 
                  'Na mesma pena incorre quem, com idêntico propósito, invadir terras de órgãos ou entidades federais, estaduais ou municipais, destinadas à Reforma Agrária.', 
                  '20', 'UNICO', null, null, null, 'LEI REFORMA AGRARIA (LEI Nº 4.947/66)');