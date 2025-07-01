package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoPlantao {
	A("A", 0),
	B("B", 1),
	C("C", 2),
	D("D", 3),
	ADMINISTRATIVO("ADMINISTRATIVO", 4),
	GESTAO("GESTAO", 5);
		
	
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
