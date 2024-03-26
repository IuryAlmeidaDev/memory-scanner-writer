package br.gov.mt.sejudh.sigepen.enumerator;

public enum BiometriaTipoDedo {
	
	MINIMO_ESQUERDO(1),
	ANELAR_ESQUERDO(2),
	MEDIO_ESQUERDO(3),
	INDICADOR_ESQUERDO(4),
	POLEGAR_ESQUERDO(5),
	POLEGAR_DIREITO(6),
	INDICADOR_DIREITO(7),
	MEDIO_DIREITO(8),
	ANELAR_DIREITO(9),
	MINIMO_DIREITO(10);
	
	private Integer id;
	
	BiometriaTipoDedo(Integer id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public static BiometriaTipoDedo getValue(Integer tipo) {
		for (BiometriaTipoDedo b : BiometriaTipoDedo.values()) {
			if (b.getId() == tipo)
				return b;
		}
		
		throw new IllegalArgumentException("Não existe dedo: " + tipo);
	}
}
