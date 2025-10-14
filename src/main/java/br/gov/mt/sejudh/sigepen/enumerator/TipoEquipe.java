package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoEquipe {
	A("GESTÃO", 0),
	B("PLANTÃO", 1),
	C("ADMINISTRATIVA", 2);

	private String descricao;
	private Integer id;

    TipoEquipe(String descricao, Integer id) {
		this.descricao = descricao;
		this.id = id;
	}

	public String getName() {
		return name();
	}
	
	public static TipoEquipe getFromOrdinal(int ordinal) {
		for(TipoEquipe p : values()) {
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
