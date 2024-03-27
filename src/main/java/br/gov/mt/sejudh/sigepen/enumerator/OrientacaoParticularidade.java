package br.gov.mt.sejudh.sigepen.enumerator;

public enum OrientacaoParticularidade {
	FRENTE(1, "Frente"),
	TRAS(2, "Trás");
	
	private int id;
	private String descricao;
	
	private OrientacaoParticularidade(int id, String descricao) {
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
	
	public static OrientacaoParticularidade getValue(Integer id) {
		if (id == null || id == 0) return null;
		
		for (OrientacaoParticularidade ori : OrientacaoParticularidade.values()) {
			if (ori.getId() == id) {
				return ori;
			}
		}
		
		throw new IllegalArgumentException("Não existe orientação de particularidade com id: " + id);
	}
}
