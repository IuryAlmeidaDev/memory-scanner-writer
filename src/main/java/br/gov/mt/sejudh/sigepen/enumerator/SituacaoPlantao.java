package br.gov.mt.sejudh.sigepen.enumerator;

public enum SituacaoPlantao {
	
	PRESENTE("Presente"),
	FOLGA_MENSAL("Folga Mensal"),
	FERIAS("Férias"),
	LICENCA_MEDICA("Licença Médica"),	
	LICENCA_PREMIO("Licença Premio"),
	AFASTAMENTO_JUDICIAL("Afastamento Judicial"),
	CERTIDAO_PARA_OUTRA_UNIDADE_POR_ORDEM_DE_SERVICO("Certidão para outra unidade por Ordem de serviço");
	
	private String descricao;

	private SituacaoPlantao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
