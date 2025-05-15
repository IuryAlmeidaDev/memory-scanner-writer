package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.Date;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.enumerator.TipoPlantao;
import br.gov.mt.sejudh.sigepen.model.Cidade;
import br.gov.mt.sejudh.sigepen.model.Estado;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroPLantao extends FiltroConsulta {

	private static final long serialVersionUID = 1L;

	private String nome;
	private TipoPlantao plantao;
    private Date dataPlantao;
    private UnidadePrisional unidadePrisional;
    private Status status;

    
    
    public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public TipoPlantao getPlantao() {
        return plantao;
    }

    public void setPlantao(TipoPlantao plantao) {
        this.plantao = plantao;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override   
    public void configurarFiltros() {
    	
    	if (this.getNome()!= null && !this.getNome().equalsIgnoreCase("") ) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nome", this.getNome(), MatchMode.ANYWHERE));
		}
    	
        if (this.getPlantao() != null) {
            getCriteria().add(Restrictions.eq("plantao", this.getPlantao()));
        }

        if (this.getDataPlantao() != null) {
            getCriteria().add(Restrictions.eq("dataPlantao", this.getDataPlantao()));
        }

        if (this.getUnidadePrisional() != null) {
            getCriteria().add(Restrictions.eq("unidadePrisional", this.getUnidadePrisional()));
        }

        if (this.getStatus() != null) {
            getCriteria().add(Restrictions.eq("status", this.getStatus()));
        }
    }
}