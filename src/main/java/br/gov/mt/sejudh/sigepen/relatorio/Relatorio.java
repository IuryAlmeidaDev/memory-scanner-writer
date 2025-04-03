package br.gov.mt.sejudh.sigepen.relatorio;

public enum Relatorio {
	ListarEstabelecimentos("RELATÓRIO DE ESTABELECIMENTOS",1),
	ListarOrgaos("RELATÓRIO DE ÓRGÃOS",2),
	ListarCrimes("RELATÓRIO DE CRIMES",3),
	ListarServidor("RELATÓRIO DE SERVIDORES",4),
	RegistroServidor("REGISTRO DO SERVIDOR",5),
	ListarPedido("RELATÓRIO DE PEDIDOS DE TRÂNSITO", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM,6),
	RegistroPedido("PEDIDO DE TRÂNSITO",7),
	ListarCargos("RELATÓRIO DE CARGOS",8),
	ListarCategorias("RELATÓRIO DE CATEGORIAS",9),
	ListarCertidao("RELATÓRIO DE CERTIDÃO",10),
	ListarTipoVisita("RELATÓRIO DE TIPO VISITA",11),
	ListarVisitante("LISTA DE VISITANTES", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM,12),
	ListarUnidadePrisional("RELATÓRIO DE UNIDADES PRISIONAIS",13),
	ListarQuadroVagas("RELATÓRIO DE GESTÃO DE VAGAS", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO_PAISAGEM,14),
	ListarMovimentacaoInterna("RELATÓRIO DE MOVIMENTAÇÃO INTERNA",15),
	RegistroMovimentacaoInterna("MOVIMENTAÇÃO INTERNA",16),
	ListarMovimentacaoExterna("RELATÓRIO DE MOVIMENTAÇÃO EXTERNA",17),
	RegistroMovimentacaoExterna("MOVIMENTAÇÃO EXTERNA",18),
	ListarRecuperando("RELATÓRIO DE RECUPERANDOS", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM,19),
	RegistroRecuperando("PERFIL DO RECUPERANDO", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO, 20),
	ListarTransferencia("RELATÓRIO DE TRANSFERÊNCIAS", 21),
	RegistroTransferencia("TRANSFERÊNCIA", 22),
	RegistroRemanejamento("REMANEJAMENTO", 23),
	RegistroRecebimentoTransferencia("RECEBER A TRANSFERÊNCIA", 24),
	ListarReceberTransferencia("RELATÓRIO DE  RECEBIMENTO DE TRANSFERÊNCIAS", 25),
	ListarRemanejamento("RELATÓRIO DE  REMANEJAMENTO",  ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM, 26),
	RegistroReceberTransferencia("RECEBER TRANSFERÊNCIA", 27),
	ListaDeChamada("LISTA DE CHAMADA", 28),
	ListaDeChamadaTeste("LISTA DE CHAMADA", 29),
	ListaDeChamadaSintetica("LISTA SINTÉTICA", 30),
	GerarPedido("Pedido de alimentação", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO, 31),
	GerarPedidoUnificado("Pedido de alimentação", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO, 32),
	GerarPedidoDetalhado("Pedido detalhado de alimentação", 33),
	PedidoDetalhado("Pedido detalhado de alimentação", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO, 34),
	ConsolidadoMensal("Consolidado Mensal", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA,ModeloRodape.PADRAO_PAISAGEM, 35),
	ListaConsolidadoMensal("Consolidado Mensal", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA,ModeloRodape.PADRAO_PAISAGEM, 36),
	PedidoCardapio("Cardapio", 37),
	ListarRelatorioEntradaSaidaRecuperando("ENTRADA E SAÍDA DE RECUPERANDOS", 38),
	ListarFuncao("RELATÓRIO DE FUNÇÕES", 39),
	ListarLogs("RELATÓRIO DE LOGS", 40),
	RegistroOficioPedidoTransito("OFÍCIO PEDIDO DE TRÂNSITO", 41),
	RegistroAutorizacaoPedidoTransito("AUTORIZAÇÃO PEDIDO DE TRÂNSITO", 42),
	ListaDeChamadaComFoto("LISTA DE CHAMADA", 43),
	ListaDeChamadaComFotoNovo("LISTA DE CHAMADA", 44),
	ListarDietaEspecialRecuperando("Listar Dieta Especial Recuperando", 45),
	ExtratoRecuperando("EXTRATO DO RECUPERANDO", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM, 46),
	ListaDeConfere("LISTA DE CHAMADA", 47),
	ListaVisitantes("Credenciamentos", 48),
	HistoricoCredenciamentos("HISTÓRICO DO CREDENCIAMENTO", 49),
	ListarTipoImpedimento("RELATÓRIO DE TIPO IMPEDIMENTO", 50),
	ListarRelatorioSecaoJurisdicionalNovo("LISTA DE RECUPERANDO POR SEÇÃO JURISDICIONAL", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM, 51),
	ListarImpedimentoVisitante("RELATÓRIO DE IMPEDIMENTO VISITANTE", 52),
	Atestado("Atestado", 53),
	ListarTipoFaltaDisciplinar("RELATÓRIO TIPO FALTA DISCIPLINAR", 54),
	ListarFaltaDisciplinar("Relatório Falta Disciplinar Recuperando", 55),
	FaltaDisciplinarRecuperando("FALTA DISCIPLINAR DO RECUPERANDO", 56),
	ListarSolicitacaoVisita("RELATÓRIO DE SOLICITAÇÕES DE VISITA", 57),
	FichaVisitante("", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO, 58),
	ListarEntradaSaida("CONSULTAR VISITAS", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM, 59),
	HistoricoRecuperando("HISTÓRICO RECUPERANDO" ,60),

	/* Relatórios Infopen Estatístico */

	EstatisticoInfopen("Relatório Estatístico Infopen" ,61),
	EstatisticoInfopenPorData("Relatório Estatístico Infopen Por Data" ,62),

	EstatisticoInfopenUnificado("Relatório Estatístico Infopen Unificado", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO ,63),
	EstatisticoInfopenUnificadoPorData("Relatório Estatístico Infopen Unificado Por Data", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO ,64),

	EstatisticoInfopenSequenciado("Relatório Estatístico Infopen Sequenciado", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO ,65),
	EstatisticoInfopenSequenciadoPorData("Relatório Estatístico Infopen Sequenciado Por Data", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO ,66),


	/* Relatórios Infopen Detalhado */

	InfopenDetalhado("Relatório de Recuperandos Infopen Detalhado", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM ,67),
	InfopenDetalhadoPorData("Relatório de Recuperandos Infopen Detalhado Por Data", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM ,68),

	InfopenDetalhadoSequenciado("Relatório de Recuperandos Infopen Detalhado Sequenciado", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO_PAISAGEM ,69),
	InfopenDetalhadoSequenciadoPorData("Relatório de Recuperandos Infopen Detalhado Sequenciado Por Data", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO_PAISAGEM ,70),

	InfopenDetalhadoUnificado("Relatório de Recuperandos Infopen Detalhado Unificado", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO_PAISAGEM, 71),
	InfopenDetalhadoUnificadoPorData("Relatório de Recuperandos Infopen Detalhado Unificado Por Data", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO_PAISAGEM, 72),



	Biometrias("Biometrias", 73);
	
	private Relatorio(String nome) {
		this.setNome(nome);
		this.setCabecalho(ModeloCabecalho.PADRAO);
		this.setRodape(ModeloRodape.PADRAO);
	}

	private Relatorio(String nome,Integer id) {
		this.setNome(nome);
		this.setCabecalho(ModeloCabecalho.PADRAO);
		this.setRodape(ModeloRodape.PADRAO);
		this.setId(id);
	}
	
	private Relatorio(String nome, ModeloCabecalho cabecalho, ModeloRodape rodape) {
		this.setNome(nome);
		this.setCabecalho(cabecalho);
		this.setRodape(rodape);
	}

	private Relatorio(String nome, ModeloCabecalho cabecalho, ModeloRodape rodape, Integer id) {
		this.setNome(nome);
		this.setCabecalho(cabecalho);
		this.setRodape(rodape);
		this.setId(id);
	}
	
	private String nome;
	private ModeloCabecalho cabecalho;
	private ModeloRodape rodape;
	private Integer id;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
