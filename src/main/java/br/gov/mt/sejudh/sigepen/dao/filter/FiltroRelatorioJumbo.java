package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.ArrayList;
import java.util.List;

public class FiltroRelatorioJumbo extends FiltroConsulta {

    private static final long serialVersionUID = 1L;

    private List<Long> unidadesSelecionadas = new ArrayList<>();

    @Override
    public void configurarFiltros() {
    }

    public List<Long> getUnidadesSelecionadas() {
        return unidadesSelecionadas;
    }

    public void setUnidadesSelecionadas(List<Long> unidadesSelecionadas) {
        this.unidadesSelecionadas = unidadesSelecionadas;
    }
}
