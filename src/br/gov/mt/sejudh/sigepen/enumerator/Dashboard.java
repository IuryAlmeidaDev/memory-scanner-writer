package br.gov.mt.sejudh.sigepen.enumerator;

// Descrição do enum é o nome da permissão para visualizar o dashboard.
public enum Dashboard {
	RECUPERANDOS_ATIVOS_SISTEMA("TotalRecuperandosAtivos"),
	RECUPERANDOS_ATIVOS_UNIDADE("TotalRecuperandosAtivosNaUnidade"),
	RECUPERANDOS_ATIVOS_CADASTRO_INCOMPLETO_UNIDADE("TotalRecuperandosComCadastroIncompleto"),
	RECUPERANDOS_TEMPORARIOS_NA_UNIDADE_SOLTURA_A_VENCER("TemporariosNaUnidadeAVencer"),
	RECUPERANDOS_TEMPORARIOS_NA_UNIDADE_SOLTURA_VENCIDA("TemporariosNaUnidadeVencido"),
	RECUPERANDOS_TEMPORARIOS_NA_UNIDADE_SOLTURA_VENCEHOJE("TemporariosNaUnidadeVenceHoje"),
	RECUPERANDOS_TEMPORARIOS_NO_SISTEMA_SOLTURA_A_VENCER("TemporariosNoSistemaAVencer"),
	RECUPERANDOS_TEMPORARIOS_NO_SISTEMA_SOLTURA_VENCIDA("TemporariosNoSistemaVencido"),
	RECUPERANDOS_TEMPORARIOS_NO_SISTEMA_SOLTURA_VENCEHOJE("TemporariosNoSistemaVenceHoje");
	
	private String descricao;
	
	Dashboard(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public static Dashboard getValue(String descricao) {
		if (descricao == null || descricao == "") return null;
		
		for (Dashboard cat : Dashboard.values()) {
			if (cat.getDescricao().equals(descricao)) {
				return cat;
			}
		}
		
		throw new IllegalArgumentException("Não existe dashboard com a descricao: " + descricao);
	}
}
