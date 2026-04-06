package br.gov.mt.sejudh.sigepen.dao.filter;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

public class FiltroHorarioRefeicao extends FiltroConsulta{

	private static final long serialVersionUID = 7243914701988061759L;
	
	private UnidadePrisional unidadePrisional;
	private Boolean listarTodasUnidades;

	@Override
	public void configurarFiltros() {
		getCriteria().createAlias("unidadePrisional", "unidadePrisional", JoinType.INNER_JOIN);
		
		Projection proj = Projections.distinct(Projections.projectionList().add(Projections.property("unidadePrisional.id").as("id"))
				.add(Projections.property("unidadePrisional.nome").as("nomeUnidade"))
				.add(Projections.property("dataInicio").as("dataInicio"))
				.add(Projections.property("dataFim").as("dataFim")));
		
		getCriteria().setProjection(proj);
		
		if (!this.isListarTodasUnidades()) {
			getCriteria().add(Restrictions.eq("unidadePrisional.id", SecurityUtil.getUnidadeSelecionada().getId()));
		} else if (this.getUnidadePrisional() != null) {
	        getCriteria().add(Restrictions.eq("unidadePrisional.id",
	                this.getUnidadePrisional().getId()));
	    }
	}
	
	public int contarRegistros(EntityManager em) {
	    Session session = em.unwrap(Session.class);
	    Criteria criteria = session.createCriteria(HorarioRefeicao.class);

	    criteria.createAlias("unidadePrisional", "unidadePrisional", JoinType.INNER_JOIN);

	   
	    criteria.setProjection(Projections.countDistinct("unidadePrisional.id"));

	  
	    if (!this.isListarTodasUnidades()) {
	        criteria.add(Restrictions.eq("unidadePrisional.id",
	                SecurityUtil.getUnidadeSelecionada().getId()));
	    } else if (this.getUnidadePrisional() != null) {
	        criteria.add(Restrictions.eq("unidadePrisional.id",
	                this.getUnidadePrisional().getId()));
	    }

	    return ((Number) criteria.uniqueResult()).intValue();
	}

	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}

	public Boolean getListarTodasUnidades() {
		return listarTodasUnidades;
	}

	public void setListarTodasUnidades(Boolean listarTodasUnidades) {
		this.listarTodasUnidades = listarTodasUnidades;
	}
	
	public boolean isListarTodasUnidades() {
		return listarTodasUnidades;
	}

}
