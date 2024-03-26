package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoEntradaRelatorio {
	TODAS("Todas"),
	ACAO_PENAL("Ação Penal"),
	GUIA_DE_EXECUCAO("Guia de Execução"),
	MANDADO_SEGURANÇA("Mandado de Prisão"),	
	NOTA_CULPA("Nota de Culpa"),
	OFICIO("Ofício");
	
	private String descricao;

	private TipoEntradaRelatorio(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
