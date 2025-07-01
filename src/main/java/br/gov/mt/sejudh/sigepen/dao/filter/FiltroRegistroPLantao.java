package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.Date;
import org.hibernate.criterion.Restrictions;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPlantao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoPlantao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;

public class FiltroRegistroPLantao extends FiltroConsulta{
	private static final long serialVersionUID = 1L;
	
	
	private TipoPlantao tipoPlantao;
	private Date dataPlantao;
	private UnidadePrisional unidadePrisional;
	private StatusPlantao statusPlantao;
	

	@Override
	public void configurarFiltros() {
		
        if (this.getTipoPlantao() != null) {
            getCriteria().add(Restrictions.eq("tipoPlantao", this.getTipoPlantao()));
        }

        if (this.getDataPlantao() != null) {
            getCriteria().add(Restrictions.eq("dataPlantao", this.getDataPlantao()));
        }

        if (this.getUnidadePrisional() != null) {
            getCriteria().add(Restrictions.eq("unidadePrisional", this.getUnidadePrisional()));
        }

        if (this.getStatusPlantao() != null) {
            getCriteria().add(Restrictions.eq("statusPlantao", this.getStatusPlantao()));
        }
	}


	public TipoPlantao getTipoPlantao() {
		return tipoPlantao;
	}

	public void setTipoPlantao(TipoPlantao tipoPlantao) {
		this.tipoPlantao = tipoPlantao;
	}

	public Date getDataPlantao() {
		return dataPlantao;
	}

	public void setDataPlantao(Date dataPlantao) {
		this.dataPlantao = dataPlantao;
	}


	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}

	public StatusPlantao getStatusPlantao() {
		return statusPlantao;
	}


	public void setStatusPlantao(StatusPlantao statusPlantao) {
		this.statusPlantao = statusPlantao;
	}
	
}
