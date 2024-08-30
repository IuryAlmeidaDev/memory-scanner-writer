create view lista_todos_recuperandos as
select pessoa.ID as id_pessoa,
       recuperando.ID as matricula,
       pessoa.NOME as nome,
       pessoa.NOME_MAE as nome_mae,
       pessoa.DATA_NASCIMENTO as data_nascimento,
       (case recuperando.Status
         when 1 then
          'Ativo'
         when 2 then
          'Inativo Parcial'
         when 0 then
          'Inativo'
       end) as status,
       unidade.NOME as unidade,
       unidade.ID as id_unidade,
       estrutura_completa.NOME_COMPLETO as estrutura,
       recuperando.CONDICAO_ATUAL as condicao,
       regime.DESCRICAO as regime,
       passagem.TIPO_DESLIGAMENTO as tipo_desligamento
  from SIGEPEN_OWNER_DES.PESSOA pessoa
  left outer join SIGEPEN_OWNER_DES.RECUPERANDO recuperando
    on pessoa.ID = recuperando.PESSOA_ID
  left outer join SIGEPEN_OWNER_DES.PASSAGEM passagem
    on recuperando.ID = passagem.RECUPERANDO_ID
  left outer join SIGEPEN_OWNER_DES.PASSAGEM_LOTACAO lotacao
    on passagem.ID = lotacao.PASSAGEM_ID
  left outer join SIGEPEN_OWNER_DES.ESTRUTURA estrutura
    on lotacao.ESTRUTURA_ID = estrutura.ID
  left outer join SIGEPEN_OWNER_DES.UNIDADE_PRISIONAL unidade
    on estrutura.unidadePrisional_ID = unidade.ID
  left outer join SIGEPEN_OWNER_DES.NOME_COMPLETO_ESTRUTURA estrutura_completa
    on estrutura.ID = estrutura_completa.ID
  left outer join SIGEPEN_OWNER_DES.REGIME regime
    on recuperando.REGIME_ATUAL_ID = regime.ID
 where ((recuperando.ID is not null and
       (passagem.ID is null or
       (passagem.ID =
       (select max(passagem2.ID) as id_passagem2
              from SIGEPEN_OWNER_DES.PASSAGEM passagem2
             where passagem2.RECUPERANDO_ID = recuperando.ID) and
       lotacao.ID =
       (select max(lotacao2.ID) as id_lotacao2
              from SIGEPEN_OWNER_DES.PASSAGEM_LOTACAO lotacao2
             where lotacao2.PASSAGEM_ID = passagem.ID)))) or
       recuperando.ID is null);

grant select on lista_todos_recuperandos to sigepen_user;
