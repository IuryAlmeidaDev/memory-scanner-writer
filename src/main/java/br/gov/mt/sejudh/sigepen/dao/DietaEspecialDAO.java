package br.gov.mt.sejudh.sigepen.dao;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
		Hibernate.initialize(dietaEspecial.getRecuperando().getPessoa());

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

	public List<String> listarNomesRecuperandosParaFiltro(boolean listarTodasUnidades, Long unidadeId) {
		return listarNomesRecuperandosParaFiltro(listarTodasUnidades, unidadeId, null, 0);
	}

	public List<String> listarNomesRecuperandosParaFiltro(boolean listarTodasUnidades, Long unidadeId,
			String consulta, int limite) {
		if (!listarTodasUnidades && unidadeId == null) {
			return Collections.emptyList();
		}

		String consultaNormalizada = consulta != null ? consulta.trim().toLowerCase() : null;
		if (consulta != null && consultaNormalizada.isEmpty()) {
			return Collections.emptyList();
		}

		StringBuilder jpql = new StringBuilder();
		jpql.append("select distinct pessoa.nome ");
		jpql.append("from DietaRecuperando d ");
		jpql.append("join d.recuperando recuperando ");
		jpql.append("join recuperando.pessoa pessoa ");
		jpql.append("join recuperando.passagens passagem ");
		jpql.append("join passagem.lotacao lotacao ");
		jpql.append("join lotacao.estrutura estrutura ");
		jpql.append("join estrutura.unidadePrisional unidade ");
		jpql.append("where pessoa.nome is not null ");
		jpql.append("and passagem.dataSaida is null ");
		jpql.append("and lotacao.dataSaida is null ");

		if (!listarTodasUnidades && unidadeId != null) {
			jpql.append("and unidade.id = :unidadeId ");
		}
		if (consultaNormalizada != null) {
			jpql.append("and lower(pessoa.nome) like :consulta ");
		}

		jpql.append("order by pessoa.nome");

		javax.persistence.TypedQuery<String> query = entityManager.createQuery(jpql.toString(), String.class);

		if (!listarTodasUnidades && unidadeId != null) {
			query.setParameter("unidadeId", unidadeId);
		}
		if (consultaNormalizada != null) {
			query.setParameter("consulta", "%" + consultaNormalizada + "%");
		}
		if (limite > 0) {
			query.setMaxResults(limite);
		}

		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<DietaRecuperando> buscarPorTipoDieta(Long TipoDietaId){		
		String jpql="select d from DietaRecuperando d join fetch d.recuperando r join fetch d.tipoDieta t where t.id = :id";
		Query query = entityManager.createQuery(jpql, DietaRecuperando.class);
		query.setParameter("id", TipoDietaId);
		
		List<DietaRecuperando> dietaRecuperandos = (List<DietaRecuperando>) query.getResultList();		
		return dietaRecuperandos;
	}
	
	public void encerrarDietasAtivasDoRecuperando(long idRecuperando, Date dataFim) {
	    inativarDietasAtivasOuSuspensasDoRecuperando(idRecuperando, dataFim);
	}

	public void suspenderDietasAtivasDoRecuperando(long idRecuperando) {
	    String jpql = "UPDATE DietaRecuperando d SET d.status = :statusSuspenso " +
	                  "WHERE d.recuperando.id = :idRecuperando " +
	                  "AND d.status = :statusAtivo " +
	                  "AND d.inicio <= :dataReferencia " +
	                  "AND (d.fim IS NULL OR d.fim > :dataReferencia)";
	    entityManager.createQuery(jpql)
	        .setParameter("statusSuspenso", Status.SUSPENSO)
	        .setParameter("statusAtivo", Status.ATIVO)
	        .setParameter("dataReferencia", new Date())
	        .setParameter("idRecuperando", idRecuperando)
	        .executeUpdate();
	}

	public void reativarDietasSuspensasDoRecuperando(long idRecuperando) {
	    String jpql = "UPDATE DietaRecuperando d SET d.status = :statusAtivo " +
	                  "WHERE d.recuperando.id = :idRecuperando " +
	                  "AND d.status = :statusSuspenso " +
	                  "AND (d.fim IS NULL OR d.fim > :dataReferencia)";
	    entityManager.createQuery(jpql)
	        .setParameter("statusAtivo", Status.ATIVO)
	        .setParameter("statusSuspenso", Status.SUSPENSO)
	        .setParameter("dataReferencia", new Date())
	        .setParameter("idRecuperando", idRecuperando)
	        .executeUpdate();
	}

	public void inativarDietasAtivasOuSuspensasDoRecuperando(long idRecuperando, Date dataFim) {
	    String jpql = "UPDATE DietaRecuperando d SET d.status = :statusInativo, d.fim = :dataFim " +
	                  "WHERE d.recuperando.id = :idRecuperando " +
	                  "AND d.status IN (:statusVigentes)";
	    entityManager.createQuery(jpql)
	        .setParameter("statusInativo", Status.INATIVO)
	        .setParameter("statusVigentes", Arrays.asList(Status.ATIVO, Status.SUSPENSO))
	        .setParameter("dataFim", dataFim)
	        .setParameter("idRecuperando", idRecuperando)
	        .executeUpdate();
	}
	
}
