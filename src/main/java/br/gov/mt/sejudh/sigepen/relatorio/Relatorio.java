package br.gov.mt.sejudh.sigepen.relatorio;

public enum Relatorio {
	ListarEstabelecimentos("RELATÓRIO DE ESTABELECIMENTOS"),
	ListarOrgaos("RELATÓRIO DE ÓRGÃOS"),
	ListarCrimes("RELATÓRIO DE CRIMES"),
	ListarServidor("RELATÓRIO DE SERVIDORES"),
	RegistroServidor("REGISTRO DO SERVIDOR"),
	ListarPedido("RELATÓRIO DE PEDIDOS DE TRÂNSITO", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM),
	RegistroPedido("PEDIDO DE TRÂNSITO"),
	ListarCargos("RELATÓRIO DE CARGOS"),
	ListarCategorias("RELATÓRIO DE CATEGORIAS"),
	ListarCertidao("RELATÓRIO DE CERTIDÃO"),
	ListarTipoVisita("RELATÓRIO DE TIPO VISITA"),
	ListarVisitante("LISTA DE VISITANTES", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM),
	ListarUnidadePrisional("RELATÓRIO DE UNIDADES PRISIONAIS"),
	ListarQuadroVagas("RELATÓRIO DE GESTÃO DE VAGAS", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO_PAISAGEM),
	ListarMovimentacaoInterna("RELATÓRIO DE MOVIMENTAÇÃO INTERNA"),
	RegistroMovimentacaoInterna("MOVIMENTAÇÃO INTERNA"),
	ListarMovimentacaoExterna("RELATÓRIO DE MOVIMENTAÇÃO EXTERNA"),
	RegistroMovimentacaoExterna("MOVIMENTAÇÃO EXTERNA"),
	ListarRecuperando("RELATÓRIO DE RECUPERANDOS", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM),
	RegistroRecuperando("PERFIL DO RECUPERANDO", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO),
	ListarTransferencia("RELATÓRIO DE TRANSFERÊNCIAS"),
	RegistroTransferencia("TRANSFERÊNCIA"),
	RegistroRemanejamento("REMANEJAMENTO"),
	RegistroRecebimentoTransferencia("RECEBER A TRANSFERÊNCIA"),
	ListarReceberTransferencia("RELATÓRIO DE  RECEBIMENTO DE TRANSFERÊNCIAS"),
	ListarRemanejamento("RELATÓRIO DE  REMANEJAMENTO",  ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM),
	RegistroReceberTransferencia("RECEBER TRANSFERÊNCIA"),
	ListaDeChamada("LISTA DE CHAMADA"),
	ListaDeChamadaTeste("LISTA DE CHAMADA"),
	ListaDeChamadaSintetica("LISTA SINTÉTICA"),
	GerarPedido("Pedido de alimentação", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO),
	GerarPedidoUnificado("Pedido de alimentação", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO),
	GerarPedidoDetalhado("Pedido detalhado de alimentação"),
	PedidoDetalhado("Pedido detalhado de alimentação", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO),
	ConsolidadoMensal("Consolidado Mensal", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA,ModeloRodape.PADRAO_PAISAGEM),
	ListaConsolidadoMensal("Consolidado Mensal", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA,ModeloRodape.PADRAO_PAISAGEM),
	PedidoCardapio("Cardapio"),
	ListarRelatorioEntradaSaidaRecuperando("ENTRADA E SAÍDA DE RECUPERANDOS"),
	ListarFuncao("RELATÓRIO DE FUNÇÕES"),
	ListarLogs("RELATÓRIO DE LOGS"),
	RegistroOficioPedidoTransito("OFÍCIO PEDIDO DE TRÂNSITO"),
	RegistroAutorizacaoPedidoTransito("AUTORIZAÇÃO PEDIDO DE TRÂNSITO"),
	ListaDeChamadaComFoto("LISTA DE CHAMADA"),
	ListaDeChamadaComFotoNovo("LISTA DE CHAMADA"),
	ListarDietaEspecialRecuperando("Listar Dieta Especial Recuperando"),
	ExtratoRecuperando("EXTRATO DO RECUPERANDO", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM),
	EstatisticoInfopen("Relatório Estatístico Infopen"),
	EstatisticoInfopenPorData("Relatório Estatístico Infopen Por Data"),
	ListaDeConfere("LISTA DE CHAMADA"),
	ListaVisitantes("Credenciamentos"),
	HistoricoCredenciamentos("HISTÓRICO DO CREDENCIAMENTO"),
	ListarTipoImpedimento("RELATÓRIO DE TIPO IMPEDIMENTO"),
	ListarRelatorioSecaoJurisdicionalNovo("LISTA DE RECUPERANDO POR SEÇÃO JURISDICIONAL", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM),
	ListarImpedimentoVisitante("RELATÓRIO DE IMPEDIMENTO VISITANTE"),
	Atestado("Atestado"),
	ListarTipoFaltaDisciplinar("RELATÓRIO TIPO FALTA DISCIPLINAR"),
	ListarFaltaDisciplinar("Relatório Falta Disciplinar Recuperando"),
	FaltaDisciplinarRecuperando("FALTA DISCIPLINAR DO RECUPERANDO"),
	ListarSolicitacaoVisita("RELATÓRIO DE SOLICITAÇÕES DE VISITA"),
	FichaVisitante("", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO),
	ListarEntradaSaida("CONSULTAR VISITAS", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM),
	HistoricoRecuperando("HISTÓRICO RECUPERANDO"),
	InfopenDetalhado("Relatório de Recuperandos Infopen Detalhado", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM),
	InfopenDetalhadoPorData("Relatório de Recuperandos Infopen Detalhado", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM),
	Biometrias("Biometrias");
	
	private Relatorio(String nome) {
		this.setNome(nome);
		this.setCabecalho(ModeloCabecalho.PADRAO);
		this.setRodape(ModeloRodape.PADRAO);
	}
	
	private Relatorio(String nome, ModeloCabecalho cabecalho, ModeloRodape rodape) {
		this.setNome(nome);
		this.setCabecalho(cabecalho);
		this.setRodape(rodape);
	}
	
	private String nome;
	private ModeloCabecalho cabecalho;
	private ModeloRodape rodape;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public ModeloCabecalho getCabecalho() {
		return cabecalho;
	}

	public void setCabecalho(ModeloCabecalho cabecalho) {
		this.cabecalho = cabecalho;
	}

	public ModeloRodape getRodape() {
		return rodape;
	}

	public void setRodape(ModeloRodape rodape) {
		this.rodape = rodape;
	}
}
