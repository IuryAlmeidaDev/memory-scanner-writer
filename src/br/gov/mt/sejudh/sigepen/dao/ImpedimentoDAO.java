package br.gov.mt.sejudh.sigepen.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroConsulta;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroImpedimento;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.model.Impedimento;
import br.gov.mt.sejudh.sigepen.model.Visitante;

@RequestScoped
public class ImpedimentoDAO extends BaseDAO<Impedimento, Long> {


	private static final long serialVersionUID = -7435299268060112130L;
	
	@Inject
	private VisitanteDAO visitanteDao;

	public Optional<List<Impedimento>> buscarImpedimentosPorIdVisitante(Long id) {
        try{
            String jpql = "SELECT imp FROM Impedimento imp WHERE imp.visitante.id = :id and imp.status = 'A'";
            Query query = this.em.createQuery(jpql, Impedimento.class);
            query.setParameter("id", id);

            return Optional.of(query.getResultList());
        }catch (NoResultException r) {
            return Optional.empty();
        }
    }

	public List<Impedimento> buscarImpedimentosVencidos() {
		String jpql = "SELECT imp FROM Impedimento imp WHERE imp.status = :status AND imp.fimImpedimento < :data";
        Query query = this.em.createQuery(jpql, Impedimento.class);
        query.setParameter("status", SgpStatus.A);
        query.setParameter("data", new Date());
        return query.getResultList();
	}
	
	public void inativarImpedimentosVencidos() {
		List<Impedimento> impedimentos = this.buscarImpedimentosVencidos();
		for(Impedimento impedimento : impedimentos) {
			impedimento.setStatus(SgpStatus.I);
			this.em.merge(impedimento);
			Visitante o = this.visitanteDao.calcularStatusAtual(impedimento.getVisitante());
			this.em.merge(o);
		}
	}

	public int buscarQuantidadeFiltrada(FiltroImpedimento filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Impedimento.class, em);
		criteria.setProjection(Projections.countDistinct("id"));
		return ((Number) criteria.uniqueResult()).intValue();
	}

	public List<Impedimento> buscarImpedimentosPorFiltro(FiltroImpedimento filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Impedimento.class, em, isBuscaRelatorio);
		List<Impedimento> lista = criteria.list();
		for (Impedimento i : lista) {
			Hibernate.initialize(i.getVisitante());
			Hibernate.initialize(i.getVisitante().getPessoa());
		}
		return lista;
	}
	
    public List<Impedimento> buscaPorFiltro(FiltroConsulta filtro, boolean isRelatorio) {
        Criteria criteria = filtro.criarCriteriaParaBusca(Impedimento.class, em, isRelatorio);
        if (filtro.isProjetarDados()) {
            criteria.setResultTransformer(new AliasToBeanResultTransformer(Impedimento.class));
        }
        List<Impedimento> lista = criteria.list();
        for(Impedimento i : lista) {
        	Hibernate.initialize(i.getVisitante());
        	if(i.getVisitante() != null) {
        		Hibernate.initialize(i.getVisitante().getPessoa());
        	}
        }
        return lista;
    }
}
