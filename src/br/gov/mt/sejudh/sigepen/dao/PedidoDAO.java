package br.gov.mt.sejudh.sigepen.dao;

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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDocumentoPedidoTransito;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;


@RequestScoped
public class PedidoDAO {
	
	@Inject
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void salvar(PedidoTransito pedido) {
		if(pedido.getId()>0){
			pedido = entityManager.merge(pedido);
		}else{
			entityManager.persist(pedido);	
		}
	}

	public void excluir(PedidoTransito pedido) {
		entityManager.remove(entityManager.merge(pedido));
	}	
		
	@SuppressWarnings("unchecked")
	public List<PedidoTransitoDTO> buscaPorFiltro(FiltroPedTransito filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(PedidoTransito.class, entityManager, isBuscaRelatorio);			
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(PedidoTransitoDTO.class));
		return criteria.list();
	}	
	
	public int quantidadeFiltrados(FiltroPedTransito filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(PedidoTransito.class, entityManager);		
	    criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	public PedidoTransito buscarPedidoCompletoPorId(long id) {
		try {
			String jpql="select p from PedidoTransito p where p.id = :id";
			TypedQuery<PedidoTransito> query = entityManager.createQuery(jpql, PedidoTransito.class);
			query.setParameter("id", id);
			
			PedidoTransito p = query.getSingleResult();
			
			Hibernate.initialize(p.getAutorizador());
			Hibernate.initialize(p.getUnidadeOrigem());
			Hibernate.initialize(p.getUnidadeOrigem().getResponsavel());
			
			if (p.getUnidadeOrigem().getResponsavel() != null) {
				Hibernate.initialize(p.getUnidadeOrigem().getResponsavel().getFuncao());
			}
			
			if(p.getTipoDocumentoPedidoTransito() == TipoDocumentoPedidoTransito.OFICIO || p.getTipoDocumentoPedidoTransito() == TipoDocumentoPedidoTransito.AUTORIZACAO){
				Hibernate.initialize(p.getRecuperando().getFotos());
				Hibernate.initialize(p.getEstruturaOrigem());
				Hibernate.initialize(p.getEstruturaDestino());
				Hibernate.initialize(p.getEstabelecimento());
				Hibernate.initialize(p.getResponsavelDestino());
				Hibernate.initialize(p.getUnidadeOrigem().getEndereco().getCidade());
			}
						
			if(p.getTipoTransito() == TipoTransito.INTERNA){
				Hibernate.initialize(p.getEstruturaOrigem());
				Hibernate.initialize(p.getEstruturaDestino());
				Hibernate.initialize(p.getMovimentacaoInterna());
			}
			if(p.getTipoTransito() == TipoTransito.EXTERNA){
				Hibernate.initialize(p.getEstabelecimento());
				Hibernate.initialize(p.getMovimentacaoExterna());
				Hibernate.initialize(p.getRecuperando().getPassagens());
				
				for (Passagem pa : p.getRecuperando().getPassagens()) {
					Hibernate.initialize(pa.getLotacao());
				}
			}
			if(p.getTipoTransito() == TipoTransito.TRANSFERENCIA){
				Hibernate.initialize(p.getUnidadeDestino());
				Hibernate.initialize(p.getEstruturaDestino());
				Hibernate.initialize(p.getTransferencia());
				Hibernate.initialize(p.getRecuperando().getPassagens());
				
				for (Passagem pa : p.getRecuperando().getPassagens()) {
					Hibernate.initialize(pa.getLotacao());
				}
			}
			
			return p;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public void expirarPedidosTransito(Date dataExpirar){
		String jpql="update PedidoTransito p set p.status = :status,  p.dataCancelamento = :data, p.resultado = :resultado where p.dataPrevista < :dataExpirar and (p.status = :solicitado or p.status = :autorizado)";
		Query query = entityManager.createQuery(jpql);
		query.setParameter("status", StatusPedido.CANCELADO);
		query.setParameter("data", new Date());
		query.setParameter("resultado", "Pedido expirado.");
		query.setParameter("dataExpirar", dataExpirar);
		query.setParameter("solicitado", StatusPedido.SOLICITADO);
		query.setParameter("autorizado", StatusPedido.AUTORIZADO);
		query.executeUpdate();
	}
	
	public boolean recuperandoEmMovimentacao(Recuperando recuperando){
		try {
			String jpql="select COUNT(p) from PedidoTransito p where p.recuperando = :rec and (p.status = :status or p.status = :statusSuspenso)";
			 Query query = entityManager.createQuery(jpql);		
			query.setParameter("rec",recuperando);
			query.setParameter("status", StatusPedido.EM_ANDAMENTO);
			query.setParameter("statusSuspenso", StatusPedido.SUSPENSO);
			long quantidade = (Long)query.getSingleResult();
			return quantidade>0;
		} catch (NoResultException e) {
			return true;
		}		
	}
	
	public boolean recuperandoEmMovimentacaoInterna(Recuperando recuperando){
		try {;
			String jpql="select COUNT(p) from PedidoTransito p where p.recuperando = :rec AND p.status = :status AND p.tipoTransito = :tipoTransito";
			 Query query = entityManager.createQuery(jpql);		
			query.setParameter("rec",recuperando);
			query.setParameter("status", StatusPedido.EM_ANDAMENTO);
			query.setParameter("tipoTransito", TipoTransito.INTERNA);
			long quantidade = (Long)query.getSingleResult();
			return quantidade>0;
		} catch (NoResultException e) {
			return true;
		}		
	}
	
	public boolean recuperandoEmMovimentacaoExterna(Recuperando recuperando){
		try {
			String jpql="select COUNT(p) from PedidoTransito p where p.recuperando = :rec AND p.tipoTransito = :tipoTransito AND (p.status = :status or p.status = :statusSuspenso)";
			 Query query = entityManager.createQuery(jpql);		
			query.setParameter("rec",recuperando);
			query.setParameter("status", StatusPedido.EM_ANDAMENTO);
			query.setParameter("statusSuspenso", StatusPedido.SUSPENSO);
			query.setParameter("tipoTransito", TipoTransito.EXTERNA);
			long quantidade = (Long)query.getSingleResult();
			return quantidade>0;
		} catch (NoResultException e) {
			return true;
		}
	}
	
	public boolean recuperandoEmTransferencia(Recuperando recuperando){
		try {
			String jpql="select COUNT(p) from PedidoTransito p where p.recuperando = :rec and p.status = :status AND p.tipoTransito = :tipoTransito";
			 Query query = entityManager.createQuery(jpql);		
			query.setParameter("rec",recuperando);
			query.setParameter("status", StatusPedido.EM_ANDAMENTO);
			query.setParameter("tipoTransito", TipoTransito.TRANSFERENCIA);
			long quantidade = (Long)query.getSingleResult();
			return quantidade>0;
		} catch (NoResultException e) {
			return true;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public List<PedidoTransito> buscarPedidosDoRecuperandoPorTipo(TipoTransito tipo, long recuperandoId) {
		Query query = entityManager.createQuery("select p from PedidoTransito p where p.tipoTransito = :tipo and p.recuperando.id = :recuperandoId");
		query.setParameter("tipo", tipo);
		query.setParameter("recuperandoId", recuperandoId);
		
		return query.getResultList();
	}
}
