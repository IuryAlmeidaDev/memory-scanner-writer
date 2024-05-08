package br.gov.mt.sejudh.sigepen.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.annotations.QueryHints;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroConsulta;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroImpedimento;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.model.Impedimento;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.model.dto.ImpedimentoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.VisitanteDTO;
import br.gov.mt.sejudh.sigepen.service.VisitanteService;

@RequestScoped
public class ImpedimentoDAO extends BaseDAO<Impedimento, Long> {


	private static final long serialVersionUID = -7435299268060112130L;
	
	@Inject
	private VisitanteDAO visitanteDao;
	
	@Inject
	private VisitanteService visitanteService;
	
	@Inject
	private CredenciamentoDAO credenciamentoDao;

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
	
	public void inativarImpedimentosVencidos() {
		Criteria crit = em.unwrap(Session.class).createCriteria(Impedimento.class);
		crit.createAlias("visitante", "visitante", JoinType.INNER_JOIN);
				
		ProjectionList proj = Projections.projectionList();
				
        proj.add(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("visitante.id"), "idVisitante"));
				
		crit.setProjection(proj);
		crit.setResultTransformer(new AliasToBeanResultTransformer(ImpedimentoDTO.class));
		crit.addQueryHint(QueryHints.READ_ONLY);
		
		crit.add(Restrictions.eq("status", SgpStatus.A))
			.add(Restrictions.lt("fimImpedimento", java.sql.Date.valueOf(LocalDate.now())));

		@SuppressWarnings("unchecked")
		List<ImpedimentoDTO> listaImpedimento = (List<ImpedimentoDTO>)crit.list();
		
		if (listaImpedimento.size() > 0) {
			List<Long> idsImpedimentos = listaImpedimento.stream().map(i -> i.getId()).collect(Collectors.toList());
			List<Long> idsVisitantes = listaImpedimento.stream().map(i -> i.getIdVisitante()).collect(Collectors.toList());
			
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaUpdate<Impedimento> updateImpedimento = builder.createCriteriaUpdate(Impedimento.class);
			Root<Impedimento> impedimento = updateImpedimento.from(Impedimento.class);
			
			updateImpedimento.set("status", SgpStatus.I);
			updateImpedimento.where(impedimento.get("id").in(idsImpedimentos));
			
			em.createQuery(updateImpedimento).executeUpdate();

			idsVisitantes.stream().forEach(vId -> {
				CriteriaUpdate<Visitante> updateVisitante = builder.createCriteriaUpdate(Visitante.class);
				Root<Visitante> visitante = updateVisitante.from(Visitante.class);

				VisitanteDTO visitanteDto = visitanteDao.buscarVisitanteDTO(vId);
				credenciamentoDao.buscarCredenciamentosEFasesDTO(visitanteDto);
				//verificarSeVisitantePossuiImpedimentoAtivo(visitanteDto);
				
				visitanteService.calculaSituacaoVisitante(visitanteDto);

				updateVisitante.set(visitante.get("situacaoAtual"), visitanteDto.getSituacaoAtual().getId());
				updateVisitante.set(visitante.get("faseAtual"), visitanteDto.getFaseAtual().getId());
				updateVisitante.set(visitante.get("status"), visitanteDto.getStatus());
				updateVisitante.where(builder.equal(visitante.get("id"), visitanteDto.getId()));
				
				em.createQuery(updateVisitante).executeUpdate();
			});
		}
	}

	public void verificarSeVisitanteEstaImpedido(VisitanteDTO visitanteDto) {
		Criteria crit = em.unwrap(Session.class).createCriteria(Impedimento.class);
		crit.createAlias("visitante", "visitante", JoinType.INNER_JOIN);
		crit.setProjection(Projections.count("id"));
		crit.addQueryHint(QueryHints.READ_ONLY);
		
		crit.add(Restrictions.eq("visitante.id", visitanteDto.getId()));
		crit.add(Restrictions.eq("status", SgpStatus.A));

		boolean possuiImpedimento = ((Integer) crit.uniqueResult()) > 0;
		visitanteDto.setImpedido(possuiImpedimento);
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
