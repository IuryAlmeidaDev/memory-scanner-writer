alter table ALCUNHA add versao number(19);
update ALCUNHA set versao = 0;

alter table AUTORIZACAO_CADASTRO_BIO add versao number(19);
update AUTORIZACAO_CADASTRO_BIO set versao = 0;

alter table BIOMETRIA add versao number(19);
update BIOMETRIA set versao = 0;

alter table BIOMETRIA_AMOSTRA add versao number(19);
update BIOMETRIA_AMOSTRA set versao = 0;

alter table BIOMETRIA_DEDO add versao number(19);
update BIOMETRIA_DEDO set versao = 0;

alter table CONTATO add versao number(19);
update CONTATO set versao = 0;

alter table ESTABELECIMENTO add versao number(19);
update ESTABELECIMENTO set versao = 0;

alter table ESTRUTURA add versao number(19);
update ESTRUTURA set versao = 0;

alter table DOCUMENTO add versao number(19);
update DOCUMENTO set versao = 0;

alter table FOTO add versao number(19);
update FOTO set versao = 0;

alter table IMAGEM add versao number(19);
update IMAGEM set versao = 0;

alter table OUTROS_NOMES add versao number(19);
update OUTROS_NOMES set versao = 0;

alter table PASSAGEM add versao number(19);
update PASSAGEM set versao = 0;

alter table PESSOA add versao number(19);
update PESSOA set versao = 0;

alter table ENDERECO add versao number(19);
update ENDERECO set versao = 0;

alter table PROCESSO_CRIME add versao number(19);
update PROCESSO_CRIME set versao = 0;

alter table PARTICULARIDADE add versao number(19);
update PARTICULARIDADE set versao = 0;

alter table PASSAGEM_PROCEDENCIA add versao number(19);
update PASSAGEM_PROCEDENCIA set versao = 0;

alter table PASSAGEM_LOTACAO add versao number(19);
update PASSAGEM_LOTACAO set versao = 0;

alter table RECUPERANDO add versao number(19);
update RECUPERANDO set versao = 0;

alter table UNIDADE_PRISIONAL add versao number(19);
update UNIDADE_PRISIONAL set versao = 0;

alter table PARENTE add versao number(19);
update PARENTE set versao = 0;

alter table PROCESSO add versao number(19);
update PROCESSO set versao = 0;

alter table PROCESSO_REGIME_CONDICAO add versao number(19);
update PROCESSO_REGIME_CONDICAO set versao = 0;

alter table SITUACAO_ESTRUTURA_UNIDADE add versao number(19);
update SITUACAO_ESTRUTURA_UNIDADE set versao = 0;

alter table SERVIDOR add versao number(19);
update SERVIDOR set versao = 0;
