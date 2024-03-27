package br.gov.mt.sejudh.sigepen.enumerator;

public enum Mes {
	JANEIRO(1, "Janeiro"),
    FEVEREIRO(2, "Fevereiro"),
    MARCO(3, "Março"),
    ABRIL(4, "Abril"),
    MAIO(5, "Maio"),
    JUNHO(6, "Junho"),
    JULHO(7, "Julho"),
    AGOSTO(8, "Agosto"),
	SETEMBRO(9, "Setembro"),
	OUTRUBRO(10, "Outubro"),
	NOVEMBRO(11, "Novembro"),
	DEZEMBRO(12, "Dezembro");
    
	private final Integer id;
    private final String descricao;

    Mes(Integer id, String descricao){
    	this.id = id;
        this.descricao = descricao;
    }
    
    public Integer getId() {
		return id;
	}

    public String getDescricao() {
        return descricao;
    }
    
    public static Mes getValue(Integer id) {
		if (id == null) return null;
		
		for (Mes dia : Mes.values()) {
			if (dia.getId() == id) {
				return dia;
			}
		}
		
		throw new IllegalArgumentException("Não existe dia da semana com o id: " + id);
	}
}
