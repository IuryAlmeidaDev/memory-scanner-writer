package br.gov.mt.sejudh.sigepen.enumerator;

public enum StatusRecuperandoRelatorio {
	TODOS(3, "Todos"),
	ATIVO(1, "Ativo"),
	INATIVO(0, "Inativo"),
	INATIVO_PARCIAL(2, "Inativo Parcial");
	
	private Integer id;
	private String descricao;
	
	private StatusRecuperandoRelatorio(Integer id, String descricao) {
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
	
	public static StatusRecuperando getValue(Integer id) {
		if (id == null) return null;
		
		for (StatusRecuperando status : StatusRecuperando.values()) {
			if (status.getId() == id) {
				return status;
			}
		}
		
		throw new IllegalArgumentException("Não existe status de recuperando com id: " + id);
	}
}
