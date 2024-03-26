package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoFoto {
	FRENTE(1, "Frente"),
	PERFIL(2, "Perfil"),
	CORPO_INTEIRO(3, "Corpo Inteiro");
	
	private int id;
	private String descricao;
	
	private TipoFoto(int id, String descricao) {
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
	
	public static TipoFoto getValue(Integer id) {
		if (id == null || id == 0) return null;
		
		for (TipoFoto cat : TipoFoto.values()) {
			if (cat.getId() == id) {
				return cat;
			}
		}
		
		throw new IllegalArgumentException("Não existe tipo de foto com id: " + id);
	}
}
