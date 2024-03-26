package br.gov.mt.sejudh.sigepen.enumerator;

import java.util.Arrays;

public enum SituacaoFaltaDisciplinar {

	PENDENTE    (1, "Pendente"),
	EM_ANDAMENTO(2, "Em Andamento"),
	CONCLUIDA   (3, "Concluída"),
	FINALIZADA  (4, "Finalizada"),
	CANCELADA   (0, "Cancelada"),
    ;

    private final String descricao;
	private final Integer id;

    SituacaoFaltaDisciplinar(Integer id, String descricao) {
        this.descricao = descricao;
        this.id = id;
    }

	public String getDescricao() {
		return descricao;
	}

    public Integer getId() {
        return id;
    }

    public static SituacaoFaltaDisciplinar getValue(final Integer id) {
        return Arrays.stream(values()).filter(value -> value.getId().equals(id)).findFirst().orElse(null);
    }


    public boolean isSituacaoPendente(){
        return this.equals(SituacaoFaltaDisciplinar.PENDENTE);
    }

    public boolean isSituacaoEmAndamento(){
        return this.equals(SituacaoFaltaDisciplinar.EM_ANDAMENTO);
    }

    public boolean isSituacaoConcluida(){
        return this.equals(SituacaoFaltaDisciplinar.CONCLUIDA);
    }

    public boolean isSituacaoCancelada(){
        return this.equals(SituacaoFaltaDisciplinar.CANCELADA);
    }

    public boolean isSituacaoFinalizada(){
        return this.equals(SituacaoFaltaDisciplinar.FINALIZADA);
    }
}
