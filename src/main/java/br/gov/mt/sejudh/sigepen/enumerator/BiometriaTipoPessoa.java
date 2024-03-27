package br.gov.mt.sejudh.sigepen.enumerator;

public enum BiometriaTipoPessoa {
	RECUPERANDO(1),
	VISITANTE(2);

	private Integer id;
	
	BiometriaTipoPessoa(Integer id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public static BiometriaTipoPessoa getValue(Integer id) {
		for (BiometriaTipoPessoa b : BiometriaTipoPessoa.values()) {
			if (b.getId() == id)
				return b;
		}
		
		throw new IllegalArgumentException("Não existe tipo pessoa: " + id);
	}
}
