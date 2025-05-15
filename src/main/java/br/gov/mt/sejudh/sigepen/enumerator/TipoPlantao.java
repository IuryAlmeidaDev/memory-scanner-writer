package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoPlantao {
	A("A", 1),
	B("B", 2),
	C("C", 3),
	D("D", 4),
	ADMINIDTRATIIVO("Administrativo", 5),
	GESTAO("Gestão", 6);
		
	
	private String descricao;
	private Integer id;
	
	private TipoPlantao(String descricao, Integer id) {
		this.descricao = descricao;
		this.id = id;
	}

	
	
	public String getName() {
		return name();
	}
	
	public static TipoPlantao getFromOrdinal(int ordinal) {
		for(TipoPlantao p : values()) {
			if (p.ordinal() == ordinal) {
				return p;
			}
		}
		
		return null;
	}



	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}	
}
