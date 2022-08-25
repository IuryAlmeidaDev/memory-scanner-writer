package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.SituacaoEstruturaUnidade;
import br.gov.mt.sejudh.sigepen.model.dto.EstruturaDto;
import br.gov.mt.sejudh.sigepen.util.Sigepen;


@RequestScoped
public class EstruturaDAO {
	
	@Inject
	private EntityManager entityManager;
	
	public EstruturaDAO(){
		
	}
	

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}


	public Estrutura salvar(Estrutura estrutura) {
		if(estrutura.getId()>0){
			estrutura = entityManager.merge(estrutura);
		}else{
			entityManager.persist(estrutura);	
		}
		return estrutura;
	}
	
	public void alterar(Estrutura estrutura){
		estrutura = entityManager.merge(estrutura);		
	}


	public void excluir(Estrutura estrutura) {
		entityManager.remove(entityManager.merge(estrutura));		
	}
	
	public Estrutura buscaPorId(Long id) {
		String jpql="select e from Estrutura e where e.id = :id";
				Query query = entityManager.createQuery(jpql, Estrutura.class);
				query.setParameter("id", id);
				Estrutura estrutura = (Estrutura) query.getSingleResult();
		return estrutura;
	}
	
	public Estrutura buscaEstraturaRaizUnidade(Long id) {
		String jpql="select e from Estrutura e, UnidadePrisional u where u.id = :id and u.estrutura = e";
				Query query = entityManager.createQuery(jpql, Estrutura.class);
				query.setParameter("id", id);
				Estrutura estrutura = (Estrutura) query.getSingleResult();
		return estrutura;
	}
	
	public Estrutura buscaEstruturaCompletaPorId(Long id) {
		String jpql="select e from Estrutura e join fetch e.estruturas where e.id = :id";
				Query query = entityManager.createQuery(jpql, Estrutura.class);
				query.setParameter("id", id);
				Estrutura estrutura = (Estrutura) query.getSingleResult();
		return estrutura;
	}
	
	@SuppressWarnings("unchecked")
	public List<Estrutura> buscaEstruturaFilho(Estrutura estrutura) {
		String jpql="select e from Estrutura e where e.estruturaPai = :id ORDER BY e.id";
		Query query = entityManager.createQuery(jpql, Estrutura.class);
		query.setParameter("id", estrutura);
		return (List<Estrutura>) query.getResultList();
	}
	
	public void salvarSituacaoEstrutura(SituacaoEstruturaUnidade situacaoUnidadePrisional) {
		try {
			entityManager.persist(situacaoUnidadePrisional);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<SituacaoEstruturaUnidade> listarPorUnidade(Long unidadePrisional) {
		String jpql="select s from SituacaoEstruturaUnidade s left join fetch s.unidadePrisional u left join fetch s.estrutura e where u.id = :id AND s.origemAlteracao = 1 ORDER BY s.dataRegistro ASC";
		Query query = entityManager.createQuery(jpql, SituacaoEstruturaUnidade.class);
		query.setParameter("id", unidadePrisional);
		return (List<SituacaoEstruturaUnidade>) query.getResultList();
	}
	
	public long quantidadeRecuperandosLotados(Estrutura estrutura) {
		Query query = entityManager.createQuery("select count(p) from PassagemLotacao p where p.estrutura = :estrutura and p.dataSaida is null");
		query.setParameter("estrutura", estrutura);
		return (long) query.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<EstruturaDto> buscarEstruturasUnidade(Long idUnidade) {
		Query query = entityManager.createNativeQuery("select level nivel,"
				+ "       e.id,"
				+ "       e.estruturapai_id,"
				+ "       e.nome,"
				+ "       e.capacidade,"
				+ "       e.cela"
				+ " from  " + Sigepen.SEJUDH_SCHEMA + ".estrutura e"
				+ " start with e.estruturapai_id is null"
				+ " and e.unidadeprisional_id = :idUnidade"
				+ " connect by prior e.id = e.estruturapai_id"
				+ " and e.situacao <> 0"
				+ " order siblings by e.id", EstruturaDto.class);
		query.setParameter("idUnidade", idUnidade);
		return query.getResultList();
	}
}
