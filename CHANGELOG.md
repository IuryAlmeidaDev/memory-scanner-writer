# Changelog

Todas as mudanças relevantes deste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/)
e este projeto adota [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [Não lançado]

### Alterado

- **Biometria — falha de licença Neurotec:** mensagem amigável no lugar de
  `IllegalStateException` com stacktrace cru ao operador.
- **Biometria — `BiometriaSDKCliente`:** `System.out.println` substituído por log
  estruturado, permitindo correlação no APM e captura no log centralizado.
- **`LoggerUtil`:** detecção de pool JCA esgotado passa a emitir log estruturado
  em stderr, em vez de `printStackTrace` solto.
- **`VisitanteBean#associarRecuperando`:** removido `print` de debug residual.
- **`GerenciarAssinaturaDAO`:** `NoResultException` (resultado vazio do JPA, fluxo
  esperado) deixa de poluir o log com stacktrace.

## [1.37.0] - 04/05/2026

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
- **Visitas:** limite de visitas por recuperando configurável por unidade prisional.
- **Biometria v2 — logs defensivos (Onda 1):** logs de contexto da estação no startup,
  status HTTP e payload em `ConexaoWebService`, parsing defensivo no
  `BiometriaWebServiceClient` e mensagem amigável no popup de erro de biometria
  (substitui stacktrace cru).

### Corrigido

- **Alimentação:** consulta de itens para refeições futuras.
- **Alimentação:** bloqueio por corte e duplicidade de pedidos automáticos.
- **Alimentação:** validação automática de data, refeição, unidade e contrato.
- **Contrato:** paginação da listagem.
- **Contrato:** filtros de unidade prisional, status e contrato na listagem.
- **Mapa consolidado:** cálculo de refeições especiais passa a considerar a
  sobreposição diária entre permanência do recuperando, vigência da dieta especial,
  horários de refeição e período do relatório.
- **Mapa consolidado:** busca de dietas especiais passa a considerar dietas vigentes
  que sobrepõem o período do relatório, inclusive iniciadas antes do mês consultado.
- **Mapa consolidado:** cálculo de refeições especiais passa a considerar a
  sobreposição diária entre permanência do recuperando, vigência da dieta especial,
  horários de refeição e período do relatório.
- **Mapa consolidado:** busca de dietas especiais passa a considerar dietas vigentes
  que sobrepõem o período do relatório, inclusive iniciadas antes do mês consultado.
- **Login:** remoção de import `org.omg.CORBA.portable.ApplicationException`
  (incompatível com Java 11+).
- **CI:** `code_check` ajustado para JDK 17 + mirror HTTP, com `sonar-maven-plugin`
  pinado em `3.11.0.3922` para compatibilidade.
- **Biometria v2 — parsing defensivo em PESQUISAR e REGISTRAR:** quando o webservice
  retornava texto não-JSON (ex.: HTML de erro de proxy/nginx, resposta vazia, plain
  text), o cliente lançava exceção. Agora o parsing é tolerante e o erro é tratado.
  Distribuído via FXLauncher na atualização do `sigepen-biometria.exe`.

### Alterado

- **Dieta especial — trânsito:** início de transferência suspende dietas vigentes dos
  recuperandos vinculados, e cancelamento de transferência em andamento reativa as dietas
  suspensas.
- **Dieta especial — desligamento:** saída de passagem inativa dietas ativas ou suspensas
  usando a data de saída como data de encerramento.
- **Pedido de transferência:** conclusão do pedido deixa de encerrar dieta especial; o ciclo
  de suspensão/reativação passa a ser tratado no fluxo de transferência.
- **Mapa consolidado — relatório:** ajustes de nomenclatura e apresentação das colunas
  de horário, refeições normais/especiais, total e saída sem data.
- **Mapa consolidado — relatório:** coluna `Nome Social` centralizada no consolidado
  mensal.
- **Mapa consolidado — relatório:** valores monetários padronizados no formato
  brasileiro, com vírgula decimal e ponto de milhar.
- **Mapa consolidado — relatório:** coluna `Situação` adicionada ao lado da matrícula,
  exibindo o desligamento quando houver ou a condição atual do recuperando.
- **Mapa consolidado — relatório:** ajuste de layout para exibir horários completos,
  acomodar nomes longos com linha fixa e deixar a saída em branco quando não houver data.
- **Mapa consolidado — relatório:** colunas `Nome Social` e `Matrícula` ampliadas
  para reduzir quebras de linha no consolidado mensal.
- **Mapa consolidado — tela:** ação principal renomeada para `Imprimir`, com ícone de
  impressão.
- **Docker:** normalização de quebra de linha no `docker-compose`.
- **FXLauncher:** sincronização de `app.xml`, `sigepen-biometria.exe`, `LiveMedia.dll`
  e `msvcp140.dll` em `Resources/aplicativo/` com o hotfix `v2.2.1` do
  `sigepen-biometria-v2` (parsing defensivo). Os 2 últimos arquivos corrigem
  inconsistência preexistente entre o `app.xml` declarado e o que era servido aos
  clientes pelo Wildfly.

### Notas da versão

- **Scripts de banco:** executar os scripts da pasta
  [`Scripts de Banco/v17.0.0/`](./Scripts%20de%20Banco/v17.0.0) na ordem:
  1. `SIGEPEN_OWNER/01-INSERT_PERMISSAO_JUMBO.sql` — cria a permissão do Relatório Jumbo.
  2. `SIGEPEN_AUDIT_OWNER/01-AUD_INSERT_PERMISSAO_JUMBO.sql` — registro de auditoria correspondente.
  3. `SIGEPEN_OWNER/02-ALTERAR_UNIDADE_PRISIONAL_QTD_VISITA.sql` — adiciona quantidade
     de visitas por recuperando na unidade prisional.
  4. `SIGEPEN_AUDIT_OWNER/02-ALTERAR_AUD_CARDAPIO_UNIDADE_PRISIONAL.sql` — adiciona
     unidade prisional na auditoria de cardápio.
  5. `SIGEPEN_AUDIT_OWNER/03-ALTERAR_AUD_UNIDADE_PRISIONAL_QTD_VISITA.sql` — adiciona
     quantidade de visitas por recuperando na auditoria de unidade prisional.
- **Migrações em homologação/produção:** abrir chamado no Conecta conforme padrão PDS.

[1.37.0]: https://gitlab.sesp.mt.gov.br/sesp/sti/sistemas/source/sigepen/-/compare/v1.36.0...v1.37.0
