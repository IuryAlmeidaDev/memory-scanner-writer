package br.gov.mt.sejudh.sigepen.enumerator;

public enum SituacaoVisita {
	EM_ANDAMENTO(1, "Em Andamento"),
	FINALIZADA(2, "Finalizada"),
	CANCELADA(3, "Cancelada");
	
	private Integer id;
	private String descricao;
	
	private SituacaoVisita(Integer id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public static SituacaoVisita getValue(Integer id) {
		if (id == null) return null;
		
		for (SituacaoVisita status : SituacaoVisita.values()) {
			if (status.getId() == id) {
				return status;
			}
		}
		
		throw new IllegalArgumentException("Não existe status de recuperando com id: " + id);
	}
}
