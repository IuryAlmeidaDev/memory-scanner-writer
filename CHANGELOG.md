# Changelog

Todas as mudanças relevantes deste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/)
e este projeto adota [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [1.38.0] - 20/05/2026

### Adicionado

- **Alimentação - filtros de listagem:** sugestões e filtros pesquisáveis nas telas de
  alimento, tipo de dieta, dieta especial, contrato, fornecedor, geração de pedido,
  cardápio e histórico de pedido de alimento, com reset da paginação ao pesquisar.
- **Pedido de alimento:** persistência da origem de geração (`MANUAL`/`AUTOMATICO`)
  no pedido e na auditoria, permitindo distinguir emissão manual e automática nos
  fluxos de histórico e relatório.
- **Contrato:** opções de filtro por número de contrato e fornecedor com sugestões,
  atualização das unidades prisionais conforme o contrato selecionado e restrição das
  opções conforme permissão/unidade do usuário.
- **Fornecedor:** opções filtráveis por razão social e nome fantasia na listagem.
- **Dieta especial:** autocomplete de recuperando com limite de resultados e respeito à
  unidade/permissão do usuário.
- **Testes unitários:** cobertura para filtros de alimento, tipo de dieta, dieta especial,
  contrato, fornecedor, cardápio, histórico de pedido de alimento, geração de pedido,
  permissões, credenciamentos de visitante e normalização de CNPJ em buscas parciais.

### Corrigido

- **Pedido de alimento:** validação de existência de cardápio passa a considerar a unidade
  prisional na geração manual, automática e na montagem da estrutura do pedido.
- **Cardápio e histórico de pedido de alimento:** combos de contrato passam a respeitar a
  unidade do usuário quando ele não possui permissão para listar todas as unidades.
- **Recuperando:** filtro de recuperando usado por dieta especial passa a ser gravado em
  sessão também para usuários sem permissão global, evitando consulta ampla indevida.
- **Gerar pedido:** fluxo de listagem passa a falhar fechado quando não houver unidade
  selecionada na sessão, listagem de unidades com contrato ativo passa a usar contagem e
  paginação no banco e o tratamento de erro de relatório deixa de depender de mensagem com
  formato específico.
- **Contrato e fornecedor:** filtros de CNPJ nas listagens deixam de exigir CNPJ completo,
  aceitam busca parcial com ou sem pontuação e normalizam o valor antes de aplicar o filtro.
- **Visitantes - credenciamento:** validação e correção do fluxo de credenciamento para os
  diferentes tipos de visita e exclusão de credenciamentos inválidos independente da fase.
- **Dieta especial - relatório:** impressão passa a processar os filtros atuais do
  formulário, usar consulta própria para relatório, corrigir o mapeamento de `tipoDieta`
  no DTO e exibir mensagem quando não houver registros para imprimir.
- **Histórico de pedido de alimento:** relatório de emissão passa a diferenciar pedidos
  manuais e automáticos; pedidos automáticos exibem emissão automática, pedidos manuais
  ativos preservam o rodapé de assinatura e a identificação do emissor passa a usar a
  matrícula do usuário responsável.

### Alterado

- **Dieta especial:** filtro de recuperando na listagem passa de combo carregado no início
  para autocomplete incremental.
- **Dieta especial - relatório:** layout passa a usar cabeçalho e rodapé institucionais em
  paisagem, datas formatadas, colunas reajustadas e coluna de nome social exibida somente
  quando houver valor preenchido nos registros impressos.
- **Dieta especial - relatório por estrutura:** cabeçalho do subrelatório foi ampliado,
  passou a aceitar quebra de linha no nome da estrutura e ganhou espaçamento entre grupos
  para melhorar a leitura do PDF.
- **Contrato, fornecedor e geração de pedido:** campos livres de consulta foram padronizados
  para seletores filtráveis ou autocomplete conforme o volume e a natureza dos dados.
- **Testes unitários:** ampliada a cobertura para origem de geração do pedido, seleção do
  relatório de emissão no histórico e busca da matrícula do emissor.
- **Build:** versão do projeto promovida para `1.38.0` e removida declaração duplicada da
  dependência `br.gov.mt.seguranca:sesp-audit-report`.

### Notas da versão

- **Scripts de banco:** executar também os scripts da pasta
  [`Scripts de Banco/v18.0.0/`](./Scripts%20de%20Banco/v18.0.0) para adicionar a coluna
  `ORIGEM_GERACAO` nas tabelas de pedido de alimento e auditoria.

## [1.37.0] - 13/05/2026

### Adicionado

- **Dieta especial:** novo status suspenso para representar dietas pausadas durante
  trânsito de recuperandos.
- **Dieta especial — listagem:** filtro por status com opções de ativos, suspensos,
  encerrados e todos.
- **Gestão de alimentação — automação de pedidos:** disparo automático da próxima refeição
  com base no horário de entrega do contrato, cronograma de pedidos e horários configuráveis.
- **Gestão de alimentação — pedido manual:** ajuste do fluxo de pedido manual e centralização
  da lógica de geração (manual e automática) na camada de Service.
- **Gestão de alimentação — desunificação de cardápio:** cardápio passa a ser por unidade,
  garantindo integridade dos dados.
- **Gestão de alimentação — parâmetros globais:** novos parâmetros para o agendamento
  automático de pedidos.
- **Relatório Jumbo self-service:** novo relatório de recuperandos nas unidades com Jumbo,
  com permissão dedicada.
- **Visitas:** limite de visitas por recuperando configurável por unidade prisional.
- **Biometria v2 — logs defensivos:** logs de contexto da estação no startup,
  status HTTP e payload em `ConexaoWebService` para facilitar diagnóstico no APM.
- **Histórico do recuperando:** nova permissão `ExcluirHistoricoRecuperando` para controlar
  a exclusão de registros de histórico por unidade logada.

### Corrigido

- **Alimentação:** consulta de itens para refeições futuras, bloqueio por corte
  e duplicidade de pedidos automáticos, validação automática de data, refeição,
  unidade e contrato.
- **Alimentação - pedidos automáticos:** proteção contra duplicidade de pedido ativo
  em concorrência, com `flush` explícito, tratamento de violação de unicidade e
  índice único `UK_SGP_PED_ALIM_ATIVO` por dia do pedido, tipo de refeição, unidade
  e contrato; o script de banco valida ordinais de status, saneia duplicidades e
  mantém o pedido mais recente.
- **Alimentação - agendador dinâmico:** isolamento de falhas por unidade no rearme,
  validação de rollout no disparo e recuperação de janelas recentes por catch-up no
  startup/rearme.
- **Contrato:** paginação da listagem.
- **Contrato:** filtros de unidade prisional, status e contrato na listagem.
- **Mapa consolidado — dietas especiais:** busca e cálculo de refeições especiais
  passam a considerar a sobreposição diária entre permanência do recuperando,
  vigência da dieta (inclusive iniciada antes do mês consultado), horários de
  refeição e período do relatório.
- **Login:** remoção de import `org.omg.CORBA.portable.ApplicationException`
  (incompatível com Java 11+).
- **CI:** `code_check` ajustado para JDK 17 + mirror HTTP, com `sonar-maven-plugin`
  pinado em `3.11.0.3922` para compatibilidade.
- **Biometria v2 — parsing defensivo em PESQUISAR e REGISTRAR:** quando o webservice
  retornava texto não-JSON (HTML de erro de proxy/nginx, resposta vazia, plain
  text), o cliente lançava exceção. Agora o parsing é tolerante, o erro é tratado
  e o operador vê mensagem amigável no popup em vez de stacktrace cru. Distribuído
  via FXLauncher na atualização do `sigepen-biometria.exe`.
- **Biometria v2 — packaging do `launcher.jar` (uber-jar):** descritor do
  assembly corrigido. O `launcher.jar` distribuído pelo FXLauncher passa a
  conter todas as classes e dependências do projeto, eliminando a necessidade
  do jar antigo hospedado em produção pra completar o classpath em runtime.
  Distribuído via atualização do `sigepen-biometria.exe` em `Resources/aplicativo/`.
- **Biometria v2 — `Cliente.VERSAO` alinhada com `pom.xml`:** constante
  promovida de `"2.2"` para `"2.3"`. Acompanhada de script SQL que atualiza
  `SIGEPEN_OWNER.PARAMETRO.VALOR` na linha `nome='VERSAO_DESKTOP'` (ver
  _Notas da versão_). Execução simultânea ao deploy é obrigatória — caso
  contrário, as estações que atualizarem o exe ficam bloqueadas pelo popup
  de versão.
- **Histórico do recuperando:** cadastro com data anterior à entrada da passagem
  atual passa a ser bloqueado; mensagens de validação aparecem como aviso na
  tela, preservando os dados do formulário; exclusão passa a respeitar a
  permissão `ExcluirHistoricoRecuperando` e a ação fica oculta para usuários
  sem autorização, recuperandos inativos ou fora da unidade do servidor logado.
- **Recuperando:** validações de cadastro reforçadas (impedem gravação sem
  pessoa, nome, nome da mãe ou data de nascimento) e ajustes defensivos em
  filtros/listagens para valores opcionais nulos ou vazios.
- **Histórico de pedido de alimento — listagem:** coluna `Data` passa a exibir a
  data do pedido (`dataPedido`) em vez da data de emissão, com formato
  `dd/MM/yyyy HH:mm`.

### Alterado

- **Alimentação - agendador dinâmico:** processamento da geração automática delegado
  para componente assíncrono, reduzindo trabalho executado diretamente no `@Timeout`.
- **Alimentação - pedidos automáticos:** geração automática passa a inativar pedidos
  `PENDENTE` da mesma chave antes de salvar o novo pedido `ATIVO`, sem substituir
  pedido ativo existente.
- **Testes unitários:** adicionada cobertura para agendador dinâmico, processador
  assíncrono, violação de unicidade, fluxo automático ativo e cenários de catch-up.
- **Dieta especial — trânsito:** início de transferência suspende dietas vigentes dos
  recuperandos vinculados, e cancelamento de transferência em andamento reativa as dietas
  suspensas.
- **Dieta especial — desligamento:** saída de passagem inativa dietas ativas ou suspensas
  usando a data de saída como data de encerramento.
- **Pedido de transferência:** conclusão do pedido deixa de encerrar dieta especial; o ciclo
  de suspensão/reativação passa a ser tratado no fluxo de transferência.
- **Mapa consolidado — relatório:** revisão geral de layout e apresentação —
  colunas de horário, refeições normais/especiais e total reorganizadas; coluna
  `Situação` adicionada ao lado da matrícula (exibe desligamento ou condição
  atual); colunas `Nome Social` e `Matrícula` ampliadas e `Nome Social`
  centralizada; valores monetários no formato brasileiro (vírgula decimal,
  ponto de milhar); saída em branco quando não houver data.
- **Mapa consolidado — tela:** ação principal renomeada para `Imprimir`, com
  ícone de impressão.
- **Docker:** normalização de quebra de linha no `docker-compose`.
- **FXLauncher:** sincronização de `app.xml`, `sigepen-biometria.exe`, `LiveMedia.dll`
  e `msvcp140.dll` em `Resources/aplicativo/` com o hotfix `v2.2.1` do
  `sigepen-biometria-v2` (parsing defensivo). Os 2 últimos arquivos corrigem
  inconsistência preexistente entre o `app.xml` declarado e o que era servido aos
  clientes pelo Wildfly.
- **Biometria — servidor de licenças Neurotec parametrizável:** endereço e porta
  do license server deixam de ser hardcoded e passam a ser lidos de
  `application.properties` (`license.server` / `license.port`), com valores por
  ambiente (dev/homolog/produção).
- **Biometria — falha de licença Neurotec:** mensagem amigável no lugar de
  `IllegalStateException` com stacktrace cru ao operador.
- **Logs e stdout:** higienização inicial de pontos com `System.out.println`,
  `printStackTrace` e exceções de fluxo esperado poluindo o log —
  `BiometriaSDKCliente` passa a usar log estruturado (correlação no APM),
  `LoggerUtil` emite log estruturado em stderr ao detectar pool JCA esgotado,
  `GerenciarAssinaturaDAO` deixa de logar stacktrace de `NoResultException`
  (resultado vazio do JPA é fluxo esperado) e `VisitanteBean#associarRecuperando`
  perde um `print` de debug residual.
- **Histórico do recuperando — tela:** campo de data passa a usar a data de entrada da
  passagem atual como data mínima.
- **Recuperando:** lista de históricos passa a ser inicializada e sincronizada mantendo o
  vínculo bidirecional entre recuperando e histórico.

### Notas da versão

- **Scripts de banco:** executar os scripts da pasta
  [`Scripts de Banco/v17.0.0/`](./Scripts de Banco/v17.0.0) na ordem:
  1. `SIGEPEN_OWNER/01-INSERT_PERMISSAO_JUMBO.sql` — cria a permissão do
     Relatório Jumbo.
  2. `SIGEPEN_AUDIT_OWNER/01-AUD_INSERT_PERMISSAO_JUMBO.sql` — registro de
     auditoria correspondente.
  3. `SIGEPEN_OWNER/02-ALTERAR_UNIDADE_PRISIONAL_QTD_VISITA.sql` — adiciona
     quantidade de visitas por recuperando na unidade prisional.
  4. `SIGEPEN_AUDIT_OWNER/02-ALTERAR_AUD_CARDAPIO_UNIDADE_PRISIONAL.sql` —
     adiciona unidade prisional na auditoria de cardápio.
  5. `SIGEPEN_AUDIT_OWNER/03-ALTERAR_AUD_UNIDADE_PRISIONAL_QTD_VISITA.sql` —
     adiciona quantidade de visitas por recuperando na auditoria de unidade
     prisional.
  6. `SIGEPEN_OWNER/03-ATUALIZAR_VERSAO_DESKTOP.sql` — **executar
     simultaneamente ao deploy da WAR**: promove
     `SIGEPEN_OWNER.PARAMETRO.VALOR` de `'2.2'` para `'2.3'` na linha
     `nome='VERSAO_DESKTOP'`, acompanhando o novo `Cliente.VERSAO` do
     `sigepen-biometria-v2`. Sem este UPDATE, as estações que atualizarem o
     exe pelo FXLauncher ficam bloqueadas pelo popup de versão. Idempotente,
     com rollback documentado no cabeçalho do script.
  7. `SIGEPEN_OWNER/04-ALTERAR_CARDAPIO_UNIDADE_PRISIONAL.sql` — adiciona
     coluna `UNIDADE_PRISIONAL_ID` na tabela `CARDAPIO`, suportando a
     desunificação de cardápio por unidade.
  8. `SIGEPEN_OWNER/05-CRIAR_INDICE_UNICO_PEDIDO_ALIMENTO_ATIVO.sql` —
     inativa duplicidades de pedidos ativos mantendo o mais recente e cria
     índice único para impedir nova duplicidade por data, refeição, unidade
     e contrato.
  9. `SIGEPEN_OWNER/06-INSERT_PERMISSAO_EXCLUIR_HISTORICO_RECUPERANDO.sql` —
     cria a permissão `ExcluirHistoricoRecuperando`.
- **Migrações em homologação/produção:** abrir chamado no Conecta conforme padrão PDS.

[1.38.0]: https://gitlab.sesp.mt.gov.br/sesp/sti/sistemas/source/sigepen/-/compare/v1.37.0...v1.38.0
[1.37.0]: https://gitlab.sesp.mt.gov.br/sesp/sti/sistemas/source/sigepen/-/compare/v1.36.0...v1.37.0
