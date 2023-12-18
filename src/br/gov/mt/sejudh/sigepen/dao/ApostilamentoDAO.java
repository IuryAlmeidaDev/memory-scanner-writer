package br.gov.mt.sejudh.sigepen.dao;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroApostilamento;
import br.gov.mt.sejudh.sigepen.model.Apostilamento;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.dto.ApostilamentoDTO;

@RequestScoped
public class ApostilamentoDAO extends BaseDAO<Apostilamento, Long> {

	private static final long serialVersionUID = -1870531035296087376L;

	@SuppressWarnings("unchecked")
	public List<Apostilamento> buscarAtivos() {
		String jpql = "select a from Apostilamento a where dataFim is null";
		Query query = em.createQuery(jpql, Apostilamento.class);
		List<Apostilamento> apostilamentos = query.getResultList();

		return apostilamentos;
	}
	
	@SuppressWarnings("unchecked")
	public List<Apostilamento> buscarAtivosUnidade(long unidadeId) {
		String jpql = "select a from Apostilamento a where dataFim is null and a.contrato.unidade.id = :unidadeId";
		Query query = em.createQuery(jpql, Apostilamento.class);
		query.setParameter("unidadeId", unidadeId);
		
		List<Apostilamento> apostilamentos = query.getResultList();

		return apostilamentos;
	}
	
	@SuppressWarnings("unchecked")
	public List<Apostilamento> existeApostilamentoUnidade(Long unidadeId) {
		StringBuilder jpql = new StringBuilder("select a from Apostilamento a ");
		jpql.append("join fetch a.contrato c ");
		jpql.append("join fetch c.unidades u ");
		jpql.append("where u.id = :id and c.status = 1 ");
		

		Query query = em.createQuery(jpql.toString(), Apostilamento.class);
		query.setParameter("id", unidadeId);
		
		List<Apostilamento> apostilamentos = new ArrayList<Apostilamento>();
		
		try {
			apostilamentos = (List<Apostilamento>) query.getResultList();
		} catch (NoResultException e) {
			apostilamentos = new ArrayList<Apostilamento>();
		}

		return apostilamentos;
	}

	public Apostilamento buscaPorId(Long id) {
		String jpql = "select a from Apostilamento a join fetch a.contrato c where a.id = :id ";
		Query query = em.createQuery(jpql, Apostilamento.class);
		query.setParameter("id", id);
		Apostilamento apostilamento = (Apostilamento) query.getSingleResult();
		Hibernate.initialize(apostilamento.getContrato());

		return apostilamento;
	}

	public Apostilamento buscarApostilamentoAnterior(Contrato contrato) {
		StringBuilder jpql = new StringBuilder("select a from Apostilamento a ");
		jpql.append("join fetch a.contrato c ");
		jpql.append("where c.id = :id and a.dataFim is null ");

		Query query = em.createQuery(jpql.toString(), Apostilamento.class);
		query.setParameter("id", contrato.getId());
		
		Apostilamento apostilamento = new Apostilamento();
		
		try {
			apostilamento = (Apostilamento) query.getSingleResult();
		} catch (NoResultException e) {
			apostilamento = new Apostilamento();
		}
		

		return apostilamento;
	}

	@SuppressWarnings("unchecked")
	public List<ApostilamentoDTO> buscaPorFiltro(FiltroApostilamento filtro) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Apostilamento.class, em);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(ApostilamentoDTO.class));
		return criteria.list();
	}

}