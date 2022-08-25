package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.EntradaSaida;
import br.gov.mt.sejudh.sigepen.model.EntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;

@RequestScoped
public class EntradaSaidaRecuperandoDAO {

	@Inject
	private EntityManager em;

	public int buscarQuantidadeFiltrada(FiltroEntradaSaidaRecuperando filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(EntradaSaidaRecuperando.class, em);
		criteria.setProjection(Projections.countDistinct("id"));
		return ((Number) criteria.uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<EntradaSaidaRecuperando> buscarEntradasPorFiltro(FiltroEntradaSaidaRecuperando filtro, boolean isRelatorio) {
		
		Criteria criteria = filtro.criarCriteriaParaBusca(EntradaSaidaRecuperando.class, em, isRelatorio);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		
		List<EntradaSaidaRecuperando> entradas = criteria.list();
		for(EntradaSaidaRecuperando e : entradas) {
			Hibernate.initialize(e.getEntrada().getVisitante());
			Hibernate.initialize(e.getEntrada().getVisitante().getIdentidadeDeGenero());
			Hibernate.initialize(e.getEntrada().getRecuperandos());
        	Hibernate.initialize(e.getEntrada().getVisitante().getFotos());
        	Hibernate.initialize(e.getEntrada().getVisitante().getPessoa().getNacionalidade());
        	Hibernate.initialize(e.getEntrada().getVisitante().getPessoa().getNaturalidade());
        	Hibernate.initialize(e.getEntrada().getVisitante().getPessoa().getPais());
        	Hibernate.initialize(e.getEntrada().getVisitante().getPessoa().getBiometria());
        	Hibernate.initialize(e.getEntrada().getVisitante().getResponsavel());
        	if(e.getEntrada().getVisitante().getResponsavel() != null) {
        		Hibernate.initialize(e.getEntrada().getVisitante().getResponsavel().getIdentidadeDeGenero());
        		Hibernate.initialize(e.getEntrada().getVisitante().getResponsavel().getPessoa());
            	Hibernate.initialize(e.getEntrada().getVisitante().getResponsavel().getFotos());
            	Hibernate.initialize(e.getEntrada().getVisitante().getResponsavel().getPessoa().getNacionalidade());
            	Hibernate.initialize(e.getEntrada().getVisitante().getResponsavel().getPessoa().getNaturalidade());
            	Hibernate.initialize(e.getEntrada().getVisitante().getResponsavel().getPessoa().getPais());
        		
        	}
        	Hibernate.initialize(e.getEntrada().getVisitante().getCredenciamentos());
        	for(Credenciamento vr : e.getEntrada().getVisitante().getCredenciamentos()) {
	    		Hibernate.initialize(vr.getFasesDoCredenciamento());
	    		if(vr.getRecuperando() != null) {
		    		Hibernate.initialize(vr.getRecuperando().getFaltas());
		    		Hibernate.initialize(vr.getRecuperando().getPassagens());
		    		for(Passagem p : vr.getRecuperando().getPassagens()) {
		    			Hibernate.initialize(p.getLotacao());
		    		}
	    		}
        	}
        	
		}
		return entradas;
	}

	public List<TipoVisita> listarTipoVisitas(TipoEntrada te) {
		if(te == null) {
			TypedQuery<TipoVisita> query = em.createQuery("select f from TipoVisita f order by f.descricao asc",
					TipoVisita.class);
			return query.getResultList();
		} else {
			TypedQuery<TipoVisita> query = em.createQuery("select f from TipoVisita f where f.tipoEntrada = :te order by f.descricao asc",
					TipoVisita.class);
			query.setParameter("te", te);
			return query.getResultList();
		}
	}

	public EntradaSaida salvar(EntradaSaida entrada) {
		if(entrada.getId() == null)
			this.em.persist(entrada);
		else
			entrada = this.em.merge(entrada);
		return entrada;
	}
	
}
