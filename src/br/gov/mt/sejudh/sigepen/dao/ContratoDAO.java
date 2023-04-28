package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroContrato;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.dto.ContratoDTO;

@RequestScoped
public class ContratoDAO {

	@Inject
	private EntityManager entityManager;

	public Contrato salvar(Contrato fornecedor) {
		if (fornecedor.getId() > 0) {
			entityManager.merge(fornecedor);
		} else {
			entityManager.persist(fornecedor);
		}
		return fornecedor;
	}

	public Contrato buscaPorId(Long id) {

		String jpql = "select f from Contrato f join fetch f.endereco join fetch f.contatos where f.id = :id";
		Query query = entityManager.createQuery(jpql, Contrato.class);
		query.setParameter("id", id);
		Contrato fornecedor = (Contrato) query.getSingleResult();
//		Hibernate.initialize(fornecedor.getEndereco());
//		Hibernate.initialize(fornecedor.getContatos());

		return fornecedor;
	}

	public int quantidadeFiltrados(FiltroContrato filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Contrato.class, entityManager);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<ContratoDTO> buscaPorFiltro(FiltroContrato filtro) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Contrato.class, entityManager);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(ContratoDTO.class));
		return criteria.list();
	}
}
