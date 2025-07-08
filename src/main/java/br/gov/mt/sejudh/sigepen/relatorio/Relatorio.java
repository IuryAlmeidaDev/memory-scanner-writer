package br.gov.mt.sejudh.sigepen.relatorio;

public enum Relatorio {
	ListarEstabelecimentos("RELATÓRIO DE ESTABELECIMENTOS", 1),
	ListarOrgaos("RELATÓRIO DE ÓRGÃOS", 2),
	ListarCrimes("RELATÓRIO DE CRIMES", 3),
	ListarServidor("RELATÓRIO DE SERVIDORES", 4),
	RegistroServidor("REGISTRO DO SERVIDOR", 5),
	ListarPedido("RELATÓRIO DE PEDIDOS DE TRÂNSITO", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM, 6),
	RegistroPedido("PEDIDO DE TRÂNSITO", 7),
	ListarCargos("RELATÓRIO DE CARGOS", 8),
	ListarCategorias("RELATÓRIO DE CATEGORIAS", 9),
	ListarPerfilUsuario("RELATÓRIO PERFIL USUARIO", 10),
	ListarCertidao("RELATÓRIO DE CERTIDÃO", 11),
	ListarTipoVisita("RELATÓRIO DE TIPO VISITA", 12),
	ListarVisitante("LISTA DE VISITANTES", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM, 13),
	ListarUnidadePrisional("RELATÓRIO DE UNIDADES PRISIONAIS", 14),
	ListarQuadroVagas("RELATÓRIO DE GESTÃO DE VAGAS", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO_PAISAGEM, 15),
	ListarMovimentacaoInterna("RELATÓRIO DE MOVIMENTAÇÃO INTERNA", 16),
	RegistroMovimentacaoInterna("MOVIMENTAÇÃO INTERNA", 17),
	ListarMovimentacaoExterna("RELATÓRIO DE MOVIMENTAÇÃO EXTERNA", 18),
	RegistroMovimentacaoExterna("MOVIMENTAÇÃO EXTERNA", 19),
	ListarRecuperando("RELATÓRIO DE RECUPERANDOS", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM, 20),
	RegistroRecuperando("PERFIL DO RECUPERANDO", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO, 21),
	ListarTransferencia("RELATÓRIO DE TRANSFERÊNCIAS", 22),
	RegistroTransferencia("TRANSFERÊNCIA", 23),
	RegistroRemanejamento("REMANEJAMENTO", 24),
	RegistroRecebimentoTransferencia("RECEBER A TRANSFERÊNCIA", 25),
	ListarReceberTransferencia("RELATÓRIO DE  RECEBIMENTO DE TRANSFERÊNCIAS", 26),
	ListarRemanejamento("RELATÓRIO DE  REMANEJAMENTO",  ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM, 27),
	RegistroReceberTransferencia("RECEBER TRANSFERÊNCIA", 28),
	ListaDeChamada("LISTA DE CHAMADA", 29),
	ListaDeChamadaTeste("LISTA DE CHAMADA", 30),
	ListaDeChamadaSintetica("LISTA SINTÉTICA", 31),
	GerarPedido("Pedido de alimentação", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO, 32),
	GerarPedidoUnificado("Pedido de alimentação", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO, 33),
	GerarPedidoDetalhado("Pedido detalhado de alimentação", 34),
	PedidoDetalhado("Pedido detalhado de alimentação", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO, 35),
	ConsolidadoMensal("Consolidado Mensal", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA,ModeloRodape.PADRAO_PAISAGEM, 36),
	ListaConsolidadoMensal("Consolidado Mensal", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA,ModeloRodape.PADRAO_PAISAGEM, 37),
	PedidoCardapio("Cardapio", 38),
	ListarRelatorioEntradaSaidaRecuperando("ENTRADA E SAÍDA DE RECUPERANDOS", 39),
	ListarFuncao("RELATÓRIO DE FUNÇÕES", 40),
	ListarLogs("RELATÓRIO DE LOGS", 41),
	RegistroOficioPedidoTransito("OFÍCIO PEDIDO DE TRÂNSITO", 42),
	RegistroAutorizacaoPedidoTransito("AUTORIZAÇÃO PEDIDO DE TRÂNSITO", 43),
	ListaDeChamadaComFoto("LISTA DE CHAMADA", 44),
	ListaDeChamadaComFotoNovo("LISTA DE CHAMADA", 45),
	ListarDietaEspecialRecuperando("Listar Dieta Especial Recuperando", 46),
	ExtratoRecuperando("EXTRATO DO RECUPERANDO", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM, 47),
	ListaDeConfere("LISTA DE CHAMADA", 48),
	ListaVisitantes("Credenciamentos", 49),
	HistoricoCredenciamentos("HISTÓRICO DO CREDENCIAMENTO", 50),
	ListarTipoImpedimento("RELATÓRIO DE TIPO IMPEDIMENTO", 51),
	ListarRelatorioSecaoJurisdicionalNovo("LISTA DE RECUPERANDO POR SEÇÃO JURISDICIONAL", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM, 52),
	ListarImpedimentoVisitante("RELATÓRIO DE IMPEDIMENTO VISITANTE", 53),
	Atestado("Atestado", 54),
	ListarTipoFaltaDisciplinar("RELATÓRIO TIPO FALTA DISCIPLINAR", 55),
	ListarFaltaDisciplinar("Relatório Falta Disciplinar Recuperando", 56),
	FaltaDisciplinarRecuperando("FALTA DISCIPLINAR DO RECUPERANDO", 57),
	ListarSolicitacaoVisita("RELATÓRIO DE SOLICITAÇÕES DE VISITA", 58),
	FichaVisitante("", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO, 59),
	ListarEntradaSaida("CONSULTAR VISITAS", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM, 60),
	HistoricoRecuperando("HISTÓRICO RECUPERANDO", 61),

	/* Relatórios Infopen Estatístico */

	EstatisticoInfopen("Relatório Estatístico Infopen", 62),
	EstatisticoInfopenPorData("Relatório Estatístico Infopen Por Data", 63),

	EstatisticoInfopenUnificado("Relatório Estatístico Infopen Unificado", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO, 64),
	EstatisticoInfopenUnificadoPorData("Relatório Estatístico Infopen Unificado Por Data", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO, 65),

	EstatisticoInfopenSequenciado("Relatório Estatístico Infopen Sequenciado", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO, 66),
	EstatisticoInfopenSequenciadoPorData("Relatório Estatístico Infopen Sequenciado Por Data", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO, 67),

	/* Relatórios Infopen Detalhado */

	InfopenDetalhado("Relatório de Recuperandos Infopen Detalhado", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM, 68),
	InfopenDetalhadoPorData("Relatório de Recuperandos Infopen Detalhado Por Data", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM, 69),

	InfopenDetalhadoSequenciado("Relatório de Recuperandos Infopen Detalhado Sequenciado", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO_PAISAGEM, 70),
	InfopenDetalhadoSequenciadoPorData("Relatório de Recuperandos Infopen Detalhado Sequenciado Por Data", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO_PAISAGEM, 71),

	InfopenDetalhadoUnificado("Relatório de Recuperandos Infopen Detalhado Unificado", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO_PAISAGEM, 72),
	InfopenDetalhadoUnificadoPorData("Relatório de Recuperandos Infopen Detalhado Unificado Por Data", ModeloCabecalho.PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO_PAISAGEM, 73),

	Biometrias("Biometrias", 74),

    /* Relatórios de Plantoes */
    ListarPlantoes("Relatório de Plantões", 75),
	
    ListarTipoOcorrencia("Relatório Tipo Ocorrência", 76);


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
