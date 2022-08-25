package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;

public class FiltroFaseDoCredenciamento extends FiltroConsulta {

	private static final long serialVersionUID = -1612469481547946491L;
	private TipoVisita tipoVisita;
    private StatusCredenciamento status;
    private UnidadePrisional unidadePrisional;
    private FaseCredenciamento faseCredenciamento;
    private String recuperando;

    @Override
    public void configurarFiltros() {
        if (this.getStatus() != null) {
            getCriteria().add(Restrictions.eq("statusCredenciamento", this.getStatus()));
        }
    }

    public StatusCredenciamento getStatus(){
        return this.status;
    }

    public void setStatus(StatusCredenciamento status) {
        this.status = status;
    }

    public TipoVisita getTipoVisita() {
        return tipoVisita;
    }

    public void setTipoVisita(TipoVisita tipoVisita) {
        this.tipoVisita = tipoVisita;
    }

    public UnidadePrisional getUnidadePrisional() {
        return unidadePrisional;
    }

    public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
        this.unidadePrisional = unidadePrisional;
    }

    public FaseCredenciamento getFaseCredenciamento() {
        return faseCredenciamento;
    }

    public void setFaseCredenciamento(FaseCredenciamento faseCredenciamento) {
        this.faseCredenciamento = faseCredenciamento;
    }

    public String getRecuperando() {
        return recuperando;
    }

    public void setRecuperando(String recuperando) {
        this.recuperando = recuperando;
    }
}
