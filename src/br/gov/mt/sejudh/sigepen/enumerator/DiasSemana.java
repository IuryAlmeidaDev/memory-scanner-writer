package br.gov.mt.sejudh.sigepen.enumerator;

public enum DiasSemana {
	DOM(1, "Domingo"),
    SEG(2, "Segunda-Feira"),
    TER(3, "Terça-Feira"),
    QUA(4, "Quarta-Feira"),
    QUI(5, "Quinta-Feira"),
    SEX(6, "Sexta-Feira"),
    SAB(7, "Sábado");
    
	private final Integer id;
    private final String descricao;

    DiasSemana(Integer id, String descricao){
    	this.id = id;
        this.descricao = descricao;
    }
    
    public Integer getId() {
		return id;
	}

    public String getDescricao() {
        return descricao;
    }
    
    public static DiasSemana getValue(Integer id) {
		if (id == null) return null;
		
		for (DiasSemana dia : DiasSemana.values()) {
			if (dia.getId() == id) {
				return dia;
			}
		}
		
		throw new IllegalArgumentException("Não existe dia da semana com o id: " + id);
	}
}
