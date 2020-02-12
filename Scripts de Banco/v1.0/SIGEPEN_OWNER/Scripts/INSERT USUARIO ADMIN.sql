--$$3JUDH%__
insert into usuario
  (id,
   login,
   salt,
   senha,
   status,
   data_expiracao,
   data_cadastro,
   servidor_id,
   is_primeiro_acesso)
values
  (0,
   'ADMIN',
   'dQZYvQY+5QjUovj5c0admA==',
   'vd6ur14mGzmt2tRmBYch+TFqKgCfCewPgYAGTfgIB3s=',
   1,
   to_date('01/01/2025', 'dd/MM/yyyy'),
   trunc(sysdate),
   null,
   0);


insert into perfil
  (id, nome, descricao, status)
values
  (0, 'Administrador', 'Administrador', null);

insert into perfil_permissoes (perfil_id, permissao_id) values (0, 1);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 2);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 3);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 4);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 5);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 6);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 7);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 8);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 9);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 10);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 16);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 17);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 18);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 19);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 20);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 36);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 37);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 38);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 39);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 40);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 41);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 42);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 43);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 44);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 45);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 46);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 47);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 48);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 49);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 50);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 51);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 52);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 53);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 54);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 55);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 56);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 57);
insert into perfil_permissoes (perfil_id, permissao_id) values (0, 102);
  
insert into acesso (id, unidade_id, usuario_id) values (0, null, 0);
insert into acesso_perfil (acesso_id, perfil_id) values (0, 0);
