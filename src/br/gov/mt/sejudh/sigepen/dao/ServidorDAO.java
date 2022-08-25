package br.gov.mt.sejudh.sigepen.dao;

import java.sql.SQLException;
import java.util.Date;
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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroServidor;
import br.gov.mt.sejudh.sigepen.model.Lotacao;
import br.gov.mt.sejudh.sigepen.model.Servidor;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;



@RequestScoped
public class ServidorDAO {
	
	@Inject
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void salvar(Servidor servidor) throws SQLException {
		if (servidor.getId() > 0) {
			servidor = entityManager.merge(servidor);
		} else {
			entityManager.persist(servidor);	
		}
	}

	public void excluir(long idServidor) {
		Servidor s = buscarServidorCompletoPorId(idServidor);
		entityManager.remove(entityManager.merge(s));
	}	
	
	@SuppressWarnings("unchecked")
	public List<ServidorDTO> buscaPorFiltro(FiltroServidor filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Servidor.class, entityManager, isBuscaRelatorio);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(ServidorDTO.class));
		return criteria.list();
	}	
	
	public int quantidadeFiltrados(FiltroServidor filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Servidor.class, entityManager);	
	    criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	public Servidor buscaServidorSimplesPorCpf(String cpf) {
		try {
			String jpql="select new br.gov.mt.sejudh.sigepen.model.Servidor(s.id, s.matricula, s.nome, s.cpf, s.email) from Servidor s where s.cpf = :cpf";
			TypedQuery<Servidor> query = entityManager.createQuery(jpql, Servidor.class);
			query.setParameter("cpf", cpf);
			return query.getSingleResult();
		} catch(NoResultException e) {
			return null;
		}
	}
	
	public boolean buscaServidorSimplesPorMatriculaOrgao(Servidor servidor) {
		Query query = entityManager.createQuery("SELECT COUNT(s) FROM Servidor s WHERE s.matricula = :matricula AND s.orgao.id = :orgaoId AND s.id <> :servidorId");
		query.setParameter("matricula", servidor.getMatricula());
		query.setParameter("orgaoId", servidor.getOrgao().getId());
		query.setParameter("servidorId", servidor.getId());
		return (long) query.getSingleResult() > 0;
		
	}

	public boolean validarLotacaoBloqueada(long id) {
		String jpql="select l.dataFim from Lotacao l where l.id = :id";
		Query query = entityManager.createQuery(jpql);
		query.setParameter("id", id);
		return (query.getSingleResult() != null);
	}

	public Servidor buscarServidorCompletoPorId(long id) {
		try {
			String jpql="select s from Servidor s where s.id = :id";
			TypedQuery<Servidor> query = entityManager.createQuery(jpql, Servidor.class);
			query.setParameter("id", id);
			Servidor s = query.getSingleResult();
			Hibernate.initialize(s.getRaca());
			Hibernate.initialize(s.getReligiao());
			Hibernate.initialize(s.getTipoSanguineo());
			Hibernate.initialize(s.getEstadoCivil());
			Hibernate.initialize(s.getDocumentos());
			Hibernate.initialize(s.getEnderecos());
			Hibernate.initialize(s.getEscolaridade());
			Hibernate.initialize(s.getTipoServidor());
			Hibernate.initialize(s.getOrgao());
			Hibernate.initialize(s.getCargo());
			Hibernate.initialize(s.getFuncao());
			Hibernate.initialize(s.getContatos());
			Hibernate.initialize(s.getLotacao());
			return s;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public Servidor buscarServidorSimplesPorId(long id) {
		try {
			String jpql="select new br.gov.mt.sejudh.sigepen.model.Servidor(s.id, s.matricula, s.nome, s.cpf, s.email) from Servidor s where s.id = :id";
			TypedQuery<Servidor> query = entityManager.createQuery(jpql, Servidor.class);
			query.setParameter("id", id);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public boolean isServidorAssociadoAUmUsuario(long idServidor) {
		Query query = entityManager.createQuery("select count(u) from Usuario u where u.servidor.id = :idServidor");
		query.setParameter("idServidor", idServidor);
		return (long) query.getSingleResult() > 0;
	}

	public boolean isServidorAssociadoAUmaUnidade(long idServidor) {
		Query query = entityManager.createQuery("select count(u) from UnidadePrisional u where u.responsavel.id = :idServidor");
		query.setParameter("idServidor", idServidor);
		return (long) query.getSingleResult() > 0;
	}
	
	public boolean unidadePossuiServidoresLotados(UnidadePrisional unidade) {
		Query query = entityManager.createQuery("select count(l) from Lotacao l where l.unidade = :unidade and (l.dataFim is null or l.dataFim > :hoje)");
		query.setParameter("unidade", unidade);
		query.setParameter("hoje", new Date());
		return (long) query.getSingleResult() > 0;
	}


	public boolean servidorEhResponsavelDaUnidade(Lotacao l) {
		Query query = entityManager.createQuery("select count(l) from Lotacao l, UnidadePrisional u "
				+ "where l.unidade = u "
				+ "and l.servidor = u.responsavel "
				+ "and l.servidor.id = :servidorId "
				+ "and l.unidade.id = :unidadeId "
				+ "and l.dataFim = null");
		
		query.setParameter("servidorId", l.getServidor().getId());
		query.setParameter("unidadeId", l.getUnidade().getId());
		
		return (long) query.getSingleResult() > 0;
	}
}
