package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoParticularidade {
	TATUAGEM(1, "Tatuagem"),
	CICATRIZ(2, "Cicatriz"),
	MARCA_DE_NASCENCA(3, "Marca de Nascença"),
	MANCHA(4, "Mancha"),
	AMPUTACAO(5, "Amputação");
	
	private int id;
	private String descricao;
	
	private TipoParticularidade(int id, String descricao) {
		this.setDescricao(descricao);
		this.setId(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public static TipoParticularidade getValue(Integer id) {
		if (id == null || id == 0) return null;
		
		for (TipoParticularidade tipo : TipoParticularidade.values()) {
			if (tipo.getId() == id) {
				return tipo;
			}
		}
		
		throw new IllegalArgumentException("Não existe tipo de particularidade com id: " + id);
	}
}
