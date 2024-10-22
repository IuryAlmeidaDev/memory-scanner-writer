-----------------------------------------------------------
--- SCRIPT DE INSERÇÃO DE DADOS NA TABELA PERMISSAO
-----------------------------------------------------------

--Permissões de Categoria
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (1, 'ListarCategoria', 'Permite acesso ao menu e a tela de listar categorias.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (2, 'VisualizarCategoria', 'Permite visualizar uma categoria.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (3, 'CadastrarCategoria', 'Permite cadastrar uma nova categoria.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (4, 'AlterarCategoria', 'Permite alterar uma categoria.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (5, 'ExcluirCategoria', 'Permite excluir uma categoria.', 2);

--Permissões de Cargo
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (6, 'ListarCargo', 'Permite acesso ao menu e a tela de listar cargos.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (7, 'VisualizarCargo', 'Permite visualizar um cargo.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (8, 'CadastrarCargo', 'Permite cadastrar um novo cargo.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (9, 'AlterarCargo', 'Permite alterar um cargo.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (10, 'ExcluirCargo', 'Permite excluir um cargo.', 2);

--Permissçoes de Crime
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (11, 'ListarCrime', 'Permite acesso ao menu e a tela de listar crimes.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (12, 'VisualizarCrime', 'Permite visualizar um crime.', 2);


--Permissões de Função
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (16, 'ListarFuncao', 'Permite acesso ao menu e a tela de listar funções.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (17, 'VisualizarFuncao', 'Permite visualizar uma função.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (18, 'CadastrarFuncao', 'Permite cadastrar uma nova função.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (19, 'AlterarFuncao', 'Permite alterar uma função.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (20, 'ExcluirFuncao', 'Permite excluir uma função.', 2);

--Permissões de Orgão
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (21, 'ListarOrgao', 'Permite acesso ao menu e a tela de listar orgãos.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (22, 'VisualizarOrgao', 'Permite visualizar um orgão.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (23, 'CadastrarOrgao', 'Permite cadastrar um novo orgão.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (24, 'AlterarOrgao', 'Permite alterar um orgão.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (25, 'ExcluirOrgao', 'Permite excluir um orgão.', 2);

--Permissões de Estabelecimento
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (26, 'ListarEstabelecimento', 'Permite acesso ao menu e a tela de listar estabelecimento.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (27, 'VisualizarEstabelecimento', 'Permite visualizar um estabelecimento.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (28, 'CadastrarEstabelecimento', 'Permite cadastrar um novo estabelecimento.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (29, 'AlterarEstabelecimento', 'Permite alterar um estabelecimento.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (30, 'ExcluirEstabelecimento', 'Permite excluir um estabelecimento.', 2);

--Permissões de Pessoa
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (31, 'ListarPessoa', 'Permite acesso ao menu e a tela de listar pessoas.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (32, 'VisualizarPessoa', 'Permite visualizar uma pessoa.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (33, 'CadastrarPessoa', 'Permite cadastrar uma nova pessoa.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (34, 'AlterarPessoa', 'Permite alterar uma pessoa.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (35, 'ExcluirPessoa', 'Permite excluir uma pessoa.', 2);

--Permissões de Unidade Prisional
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (36, 'ListarUnidade', 'Permite acesso ao menu e a tela de listar unidades.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (37, 'VisualizarUnidade', 'Permite visualizar uma unidade.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (38, 'CadastrarUnidade', 'Permite cadastrar uma nova unidade.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (39, 'AlterarUnidade', 'Permite alterar uma unidade.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (102, 'ListarTodasUnidades', 'Permite listar todas as unidades.', 2);

--Permissões de Perfil
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (41, 'ListarPerfil', 'Permite acesso ao menu e a tela de listar perfis.', 1);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (42, 'VisualizarPerfil', 'Permite visualizar um perfil.', 1);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (43, 'CadastrarPerfil', 'Permite cadastrar um novo perfil.', 1);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (44, 'AlterarPerfil', 'Permite alterar um perfil.', 1);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (45, 'ExcluirPerfil', 'Permite excluir um perfil.', 1);

--Permissões de Servidor
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (46, 'ListarServidor', 'Permite acesso ao menu e a tela de listar servidores.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (47, 'VisualizarServidor', 'Permite visualizar um servidor.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (48, 'CadastrarServidor', 'Permite cadastrar um novo servidor.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (49, 'AlterarServidor', 'Permite alterar um servidor.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (50, 'ExcluirServidor', 'Permite excluir um servidor.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (110, 'ListarTodosServidores', 'Permite acesso ao menu e a tela de listar servidores.', 2);

--Permissões de Usuário
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (51, 'ListarUsuario', 'Permite acesso ao menu e a tela de listar usuarios.', 1);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (52, 'VisualizarUsuario', 'Permite visualizar um usuário.', 1);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (53, 'CadastrarUsuario', 'Permite cadastrar um novo usuário.', 1);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (54, 'AlterarUsuario', 'Permite alterar um usuário.', 1);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (55, 'ExcluirUsuario', 'Permite excluir um usuário.', 1);

--Outras
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (56, 'ListarPermissao', 'Permite listar as permissões do sistema.', 1);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (57, 'AdicionarLotacaoServidor', 'Permite adicionar lotação a um servidor.', 2);

--Permissões de Estrutura
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (58, 'CadastrarEstrutura', 'Permite cadastrar uma nova estrutura.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (59, 'AlterarEstrutura', 'Permite alterar uma estrutura.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (60, 'ExcluirEstrutura', 'Permite excluir uma estrutura.', 2);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (61, 'CadastrarSituacaoEstrutura', 'Permite cadastrar uma nova situação da estrutura.', 2);

--Permissões de Recuperando
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (62, 'ListarRecuperando', 'Permite acesso ao menu e a tela de listar recuperandos.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (63, 'CadastrarRecuperando', 'Permite dar entrada no recuperando em alguma unidade, fazendo cadastro preliminar ou reentrada.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (64, 'ExcluirRecuperando', 'Permite excluir um recuperando.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (65, 'VisualizarRecuperando', 'Permite visualizar um recuperando.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (66, 'AlterarRecuperando', 'Permite alterar um recuperando.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (67, 'ExcluirFoto', 'Permite excluir uma imagem de fotos do recuperando.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (68, 'ExcluirParticularidade', 'Permite excluir uma imagem de particularidades do recuperando.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (69, 'AlterarFotoAtual', 'Permite alterar a foto atual de um tipo do recuperando.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (13, 'CadastrarHistoricoRecuperando', 'Permite cadastrar um histórico do recuperando.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (14, 'DesligarRecuperando', 'Permite desligar um recuperando.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (15, 'AlterarPericulosidade', 'Permite alterar a periculosidade de um recuperando.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (103, 'VisualizarHistoricoRecuperando', 'Permite visualizar o histórico do recuperando.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (104, 'CadastrarNovaPassagem', 'Permite cadastrar uma nova pasasgem.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (40, 'AlterarPessoaRecuperando', 'Permite editar os dados de pessoa em recuperando.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (111, 'AutorizarCadastroSemBiometria', 'Permite salvar recuperando sem biometria.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (113, 'AlterarNomePrincipalRecuperando', 'Permite tornar um "Outro Nome" como nome Principal.', 3);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (114, 'TornarDocumentoPrincipal', 'Permite tornar um segundo documento como principal.', 3);

--Permissões de Pedido de Trânsito
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (70, 'ListarPedidoTransito', 'Permite acesso ao menu e a tela de listar Pedido de Trânsito.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (71, 'CadastrarPedidoTransito', 'Permite cadastrar um Pedido de Trânsito.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (72, 'VisualizarPedidoTransito', 'Permite visualizar um Pedido de Trânsito.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (73, 'AlterarPedidoTransito', 'Permite alterar um Pedido de Trânsito.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (74, 'AutorizarPedidoTransito', 'Permite autorizar um Pedido de Trânsito.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (75, 'ExcluirPedidoTransito', 'Permite excluir um Pedido de Trânsito.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (105, 'CancelarPedidoTransito', 'Permite cancelar um Pedido de Trânsito.', 5);

--Permissões de Movimentação Interna
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (76, 'ListarMovimentacaoInterna', 'Permite acesso ao menu e a tela de listar Movimentação Interna.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (77, 'CadastrarMovimentacaoInterna', 'Permite cadastrar uma Movimentação Interna.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (78, 'VisualizarMovimentacaoInterna', 'Permite visualizar uma Movimentação Interna.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (79, 'AlterarMovimentacaoInterna', 'Permite alterar uma Movimentação Interna.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (80, 'ExcluirMovimentacaoInterna', 'Permite excluir uma Movimentação Interna.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (81, 'IniciarMovimentacaoInterna', 'Permite iniciar uma Movimentação Interna.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (82, 'FinalizarMovimentacaoInterna', 'Permite finalizar uma Movimentação Interna.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (106, 'CancelarMovimentacaoInterna', 'Permite cancelar uma Movimentação Interna.', 5);

--Permissões de Movimentação Externa
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (83, 'ListarMovimentacaoExterna', 'Permite acesso ao menu e a tela de listar Movimentação Externa.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (84, 'CadastrarMovimentacaoExterna', 'Permite cadastrar uma Movimentação Externa.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (85, 'VisualizarMovimentacaoExterna', 'Permite visualizar uma Movimentação Externa.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (86, 'AlterarMovimentacaoExterna', 'Permite alterar uma Movimentação Externa.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (87, 'IniciarMovimentacaoExterna', 'Permite iniciar uma Movimentação Externa.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (88, 'FinalizarMovimentacaoExterna', 'Permite finalizar uma Movimentação Externa.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (89, 'ExcluirMovimentacaoExterna', 'Permite excluir uma Movimentação Externa.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (107, 'CancelarMovimentacaoExterna', 'Permite cancelar uma Movimentação Externa.', 5);

--Permissões de Remanejamento
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (90, 'ListarRemanejamento', 'Permite acesso ao menu e a tela de listar Remanejamento.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (91, 'CadastrarRemanejamento', 'Permite cadastrar um Remanejamento.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (92, 'VisualizarRemanejamento', 'Permite visualizar um Remanejamento.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (93, 'FinalizarRemanejamento', 'Permite finalizar um Remanejamento.', 5);

--Permissões de Transferência
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (94, 'ListarTransferencia', 'Permite acesso ao menu e a tela de listar Transferência.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (95, 'CadastrarTransferencia', 'Permite cadastrar uma Transferência.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (96, 'VisualizarTransferencia', 'Permite visualizar uma Transferência.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (97, 'AlterarTransferencia', 'Permite alterar uma Transferência.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (98, 'IniciarTransferencia', 'Permite iniciar uma Transferência.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (99, 'FinalizarTransferencia', 'Permite finalizar uma Transferência.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (100, 'ExcluirTransferencia', 'Permite excluir uma Transferência.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (101, 'ListarReceberTransferencia', 'Permite acesso ao menu e a tela de listar Receber Transferência.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (108, 'CancelarTransferencia', 'Permite cancelar uma Transferência.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (109, 'ReceberTransferencia', 'Permite receber ou cancelar uma Transferência no destino.', 5);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (118, 'VisualizarReceberTransferencia', 'Permite visualizar o recebimento de uma transferência.', 5);

--Permissões de Auditoria
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (112, 'ListarLog', 'Permite acesso ao menu e a tela de listar Log.', 6);

--Permissões de Dashboard
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (115, 'TotalRecuperandosAtivos', 'Exibe total de recuperandos ativos no sistema.', 4);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (116, 'TotalRecuperandosAtivosNaUnidade', 'Exibe total de recuperandos ativos na unidade logada.', 4);
INSERT INTO PERMISSAO (ID, NOME, DESCRICAO, FUNCIONALIDADE_ID) VALUES (117, 'TotalRecuperandosComCadastroIncompleto', 'Exibe total de recuperandos com cadastro incompleto na unidade.', 4);

--Último ID 118
