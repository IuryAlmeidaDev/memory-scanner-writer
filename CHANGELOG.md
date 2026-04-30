# Changelog

Todas as mudanças relevantes deste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/)
e este projeto adota [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [1.37.0] - A definir

### Adicionado
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
- **Login:** remoção de import `org.omg.CORBA.portable.ApplicationException`
  (incompatível com Java 11+).
- **CI:** `code_check` ajustado para JDK 17 + mirror HTTP, com `sonar-maven-plugin`
  pinado em `3.11.0.3922` para compatibilidade.

### Alterado
- **Docker:** normalização de quebra de linha no `docker-compose`.
- **FXLauncher:** sincronização dos artefatos com o `sigepen-biometria-v2`.

### Notas da versão
- **Scripts de banco:** executar os scripts da pasta
  [`Scripts de Banco/v17.0.0/`](./Scripts%20de%20Banco/v17.0.0) na ordem:
  1. `SIGEPEN_OWNER/01-INSERT_PERMISSAO_JUMBO.sql` — cria a permissão do Relatório Jumbo.
  2. `SIGEPEN_AUDIT_OWNER/01-AUD_INSERT_PERMISSAO_JUMBO.sql` — registro de auditoria correspondente.
- **Migrações em homologação/produção:** abrir chamado no Conecta conforme padrão PDS.

[1.37.0]: https://gitlab.sesp.mt.gov.br/sesp/sti/sistemas/source/sigepen/-/compare/v1.36.0...v1.37.0
