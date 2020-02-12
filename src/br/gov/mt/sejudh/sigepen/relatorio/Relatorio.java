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
	ListarUnidadePrisional("RELATÓRIO DE UNIDADES PRISIONAIS"),
	ListarMovimentacaoInterna("RELATÓRIO DE MOVIMENTAÇÃO INTERNA"),
	RegistroMovimentacaoInterna("MOVIMENTAÇÃO INTERNA"),
	ListarMovimentacaoExterna("RELATÓRIO DE MOVIMENTAÇÃO EXTERNA"),
	RegistroMovimentacaoExterna("MOVIMENTAÇÃO EXTERNA"),
	ListarRecuperando("RELATÓRIO DE RECUPERANDOS", ModeloCabecalho.PADRAO_PAISAGEM, ModeloRodape.PADRAO_PAISAGEM),
	RegistroRecuperando("PERFIL DO RECUPERANDO", ModeloCabecalho.PADRAO_SEM_UNIDADE_LOGADA, ModeloRodape.PADRAO),
	ListarTransferencia("RELATÓRIO DE TRANSFERÊNCIAS"),
	RegistroTransferencia("TRANSFERÊNCIA"),
	ListaDeChamada("LISTA DE CHAMADA"),
	ListaDeChamadaSintetica("LISTA SINTÉTICA"),
	ListarFuncao("RELATÓRIO DE FUNÇÕES"),
	ListarLogs("RELATÓRIO DE LOGS"),
	RegistroOficioPedidoTransito("OFÍCIO PEDIDO DE TRÂNSITO"),
	RegistroAutorizacaoPedidoTransito("AUTORIZAÇÃO PEDIDO DE TRÂNSITO"),
	AtestadoDeComparecimentoMensal("ATESTADO DE COMPARECIMENTO MENSAL"), 
	ListaDeChamadaComFoto("LISTA DE CHAMADA");
	
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
