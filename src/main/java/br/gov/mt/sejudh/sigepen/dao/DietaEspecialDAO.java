package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroDietaEspecial;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.DietaRecuperando;
import br.gov.mt.sejudh.sigepen.model.dto.DietaEspecialDTO;

@RequestScoped
public class DietaEspecialDAO {

	@Inject
	private EntityManager entityManager;

	public DietaRecuperando salvar(DietaRecuperando dietaEspecial) {
		this.inativarContratosPorFornecedor(dietaEspecial.getRecuperando().getId());
		if (dietaEspecial.getId() > 0) {
			entityManager.merge(dietaEspecial);
		} else {
			entityManager.persist(dietaEspecial);
		}
		return dietaEspecial;
	}
	
	@SuppressWarnings("unchecked")
	public List<DietaRecuperando> buscarDietaPeloRecuperando(long idRecuperando) {
		
		StringBuilder s = new StringBuilder("select d from DietaRecuperando d ");
		s.append("where d.recuperando.id = :idFornecedor and d.status = 1");
		
		Query query = entityManager.createQuery(s.toString(), DietaRecuperando.class);
		query.setParameter("idFornecedor", idRecuperando);

		List<DietaRecuperando> dietasRecuperando = query.getResultList();
		return dietasRecuperando;
	}

	public void inativarContratosPorFornecedor(long idFornecedor) {
		List<DietaRecuperando> dietasRecuperando = buscarDietaPeloRecuperando(idFornecedor);
		dietasRecuperando.forEach(dieta -> {
			dieta.setStatus(Status.INATIVO);
			salvar(dieta);
		});
	}

	public DietaRecuperando buscaPorId(Long id) {

		String jpql = "select d from DietaRecuperando d join fetch d.recuperando join fetch d.tipoDieta where d.id = :id";
		Query query = entityManager.createQuery(jpql, DietaRecuperando.class);
		query.setParameter("id", id);
		DietaRecuperando dietaEspecial = (DietaRecuperando) query.getSingleResult();
		Hibernate.initialize(dietaEspecial.getRecuperando());

		return dietaEspecial;
	}

	public int quantidadeFiltrados(FiltroDietaEspecial filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(DietaRecuperando.class, entityManager);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<DietaEspecialDTO> buscaPorFiltro(FiltroDietaEspecial filtro) {
		Criteria criteria = filtro.criarCriteriaParaBusca(DietaRecuperando.class, entityManager);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(DietaEspecialDTO.class));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<DietaRecuperando> buscarPorTipoDieta(Long TipoDietaId){		
		String jpql="select d from DietaRecuperando d join fetch d.recuperando r join fetch d.tipoDieta t where t.id = :id";
		Query query = entityManager.createQuery(jpql, DietaRecuperando.class);
		query.setParameter("id", TipoDietaId);
		
		List<DietaRecuperando> dietaRecuperandos = (List<DietaRecuperando>) query.getResultList();		
		return dietaRecuperandos;
	}
	
}
