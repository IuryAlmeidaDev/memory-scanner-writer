package br.gov.mt.sejudh.sigepen.enumerator;

import java.util.Objects;

public enum DiasSemana {
	DOM(0, "Domingo"),
    SEG(1, "Segunda-Feira"),
    TER(2, "Terça-Feira"),
    QUA(3, "Quarta-Feira"),
    QUI(4, "Quinta-Feira"),
    SEX(5, "Sexta-Feira"),
    SAB(6, "Sábado");
    
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
			if (Objects.equals(dia.getId(), id)) {
				return dia;
			}
		}
		
		throw new IllegalArgumentException("Não existe dia da semana com o id: " + id);
	}
}
