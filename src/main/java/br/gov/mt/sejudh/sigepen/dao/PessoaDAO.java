package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPessoa;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.dto.PessoaDTO;


@RequestScoped
public class PessoaDAO {
	
	@Inject
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public Pessoa salvar(Pessoa pessoa) {
		if (pessoa.getId()>0) {
			pessoa = entityManager.merge(pessoa);
		} else {
			entityManager.persist(pessoa);
		}
		return pessoa;
	}

	public void excluir(long pessoaId) {
		Pessoa pessoa = buscarPessoaPorId(pessoaId);
		entityManager.remove(entityManager.merge(pessoa));
	}

	@SuppressWarnings("unchecked")
	public List<PessoaDTO> buscaPorFiltro(FiltroPessoa filtro) {		
		Criteria criteria = filtro.criarCriteriaParaBusca(Pessoa.class, entityManager);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(PessoaDTO.class));
		return criteria.list();
	}		
	
	public int quantidadeFiltrados(FiltroPessoa filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Pessoa.class, entityManager);		
	    criteria.setProjection(Projections.rowCount());		
	    return ((Number) criteria.uniqueResult()).intValue();
	}

	public Pessoa buscarPessoaPorId(long id) {
		try {
			String jpql="select p from Pessoa p where p.id = :id";
			TypedQuery<Pessoa> query = entityManager.createQuery(jpql, Pessoa.class);
			query.setParameter("id", id);
			Pessoa p = query.getSingleResult();
			Hibernate.initialize(p.getNacionalidade());
			Hibernate.initialize(p.getPais());
			Hibernate.initialize(p.getNaturalidade());
			Hibernate.initialize(p.getDocumentos());
			Hibernate.initialize(p.getEnderecos());
			Hibernate.initialize(p.getContatos());
			if(p.getRecuperando() != null) {
				Hibernate.initialize(p.getRecuperando().getTipoSanguineo());
				Hibernate.initialize(p.getRecuperando().getRaca());
			}
			Hibernate.initialize(p.getBiometria());
			return p;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public Pessoa buscarPessoaComBiometriaPorId(long id) {
		try {
			String jpql="select p from Pessoa p left join fetch p.biometria b where p.id = :id and b.id = p.biometria.id";
			TypedQuery<Pessoa> query = entityManager.createQuery(jpql, Pessoa.class);
			query.setParameter("id", id);
			Pessoa p = query.getSingleResult();
			return p;
		} catch (NoResultException e) {
			return null;
		}
	}

	public boolean isPessoaAssociadaAUmRecuperando(long pessoa) {
		Query query = entityManager.createQuery("SELECT COUNT(r) FROM Recuperando r WHERE r.pessoa.id = :pessoa");
		query.setParameter("pessoa", pessoa);
		return (long) query.getSingleResult() > 0;
	}
}
