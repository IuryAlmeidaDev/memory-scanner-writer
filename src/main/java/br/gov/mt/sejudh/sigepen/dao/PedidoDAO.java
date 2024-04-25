package br.gov.mt.sejudh.sigepen.dao;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.gov.mt.sejudh.sigepen.enumerator.TipoMovimentacao;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;


@RequestScoped
public class PedidoDAO {

    @Inject
    private EntityManager entityManager;

    @Inject
    private EstruturaDAO estruturaDAO;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void salvar(PedidoTransito pedido) {
        if (pedido.getId() > 0) {
            pedido = entityManager.merge(pedido);
        } else {
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

    public void atualizaPedidosTransitoVinculandoComTransferencia(List<Long> ids, StatusPedido statusPedido, Long idTransferencia) {
        Query query = entityManager.createQuery(
                "UPDATE PedidoTransito p SET p.status = :status, p.transferencia.id = :idTransferencia " +
                        "WHERE p.id IN :ids"
        );

        query.setParameter("status", statusPedido);
        query.setParameter("idTransferencia", idTransferencia);
        query.setParameter("ids", ids);
        query.executeUpdate();

    }

    public void atualizaPedidosParaEmAndamentoTransitoPorTransferenciaId(Long idTransferencia, StatusPedido statusPedido, Date data) {
        Query query = entityManager.createQuery(
                "UPDATE PedidoTransito p SET p.status = :status, p.dataInicio = :data " +
                        "WHERE p.transferencia.id = :idTransferencia"
        );

        executaUpdatePedidosTransferencia(idTransferencia, statusPedido, data, query);

    }

    public void atualizaPedidosParaCanceladoTransitoPorTransferenciaId(Long idTransferencia, StatusPedido statusPedido, Date data) {
        Query query = entityManager.createQuery(
                "UPDATE PedidoTransito p SET p.status = :status, p.dataCancelamento = :data " +
                        "WHERE p.transferencia.id = :idTransferencia"
        );

        executaUpdatePedidosTransferencia(idTransferencia, statusPedido, data, query);

    }


    public void atualizaPedidosParaFinalizadoTransitoPorTransferenciaId(Long idTransferencia, StatusPedido statusPedido, Date data) {
        Query query = entityManager.createQuery(
                "UPDATE PedidoTransito p SET p.status = :status, p.dataConclusao = :data " +
                        "WHERE p.transferencia.id = :idTransferencia"
        );

        executaUpdatePedidosTransferencia(idTransferencia, statusPedido, data, query);

    }

    private void executaUpdatePedidosTransferencia(Long idTransferencia, StatusPedido statusPedido, Date data,
                                                   Query query) {
        query.setParameter("status", statusPedido);
        query.setParameter("idTransferencia", idTransferencia);
        query.setParameter("data", data);
        query.executeUpdate();
    }

    public PedidoTransito buscarPedidoPorId(long id, TipoMovimentacao tipoMovimentacao) {
        try {
            StringBuilder jpql = new StringBuilder();
            jpql.append("SELECT p FROM PedidoTransito p " +
                    "LEFT JOIN  p.autorizador " +
                    "LEFT JOIN FETCH p.unidadeOrigem uo " +
                    "LEFT JOIN  uo.responsavel " +
                    "LEFT JOIN  uo.responsavel.funcao ");
            joinTabelas(jpql, tipoMovimentacao);

            jpql.append("WHERE p.id = :id");

            TypedQuery<PedidoTransito> query = entityManager.createQuery(jpql.toString(), PedidoTransito.class);
            query.setParameter("id", id);

            PedidoTransito p = query.getSingleResult();
            return p;
        } catch (NoResultException e) {
            return null;
        }
    }

    private void joinTabelas(StringBuilder jpql, TipoMovimentacao tipoMovimentacao) {
        switch (tipoMovimentacao){
            case TRANSFERENCIA:
                jpql.append(
                        "LEFT JOIN FETCH p.unidadeDestino " +
                        "LEFT JOIN p.estruturaDestino " +
                        "LEFT JOIN FETCH p.transferencia " +
                        "LEFT JOIN FETCH p.recuperando r " +
                        "left join fetch r.pessoa "
                        
                		);
                break;
            case TRANSITO_EXTERNO:
                jpql.append(
                        "LEFT JOIN p.estabelecimento " +
                        "LEFT JOIN p.movimentacaoExterna " +
                        "LEFT JOIN p.recuperando r " +
                        "LEFT JOIN r.passagens " +
                        "LEFT JOIN r.passagens pa " +
                        "LEFT JOIN pa.lotacao ");
                break;
            case TRANSITO_INTERNO:
                jpql.append(
                        "LEFT JOIN p.estruturaOrigem " +
                        "LEFT JOIN p.estruturaDestino " +
                        "LEFT JOIN p.estruturaDestino estruturaDestinoComplemento " +
                        "LEFT JOIN estruturaDestinoComplemento.estruturaComplemento " +
                        "LEFT JOIN p.movimentacaoInterna ");
                break;
            case PEDIDO_TRANSITO:
                jpql.append(
                        "LEFT JOIN p.recuperando r " +
                        "LEFT JOIN r.fotos " +
                        "LEFT JOIN p.estruturaOrigem " +
                        "LEFT JOIN p.estruturaDestino " +
                        "LEFT JOIN p.estabelecimento " +
                        "LEFT JOIN p.responsavelDestino " +
                        "LEFT JOIN p.unidadeOrigem uo " +
                        "LEFT JOIN uo.endereco endereco " +
                        "LEFT JOIN endereco.cidade ");
        }
    }

    public PedidoTransito buscarPedidoTransferenciaPorId(long id) {
        try {

            String jpql = "SELECT p FROM PedidoTransito p " +
                    "LEFT JOIN FETCH p.unidadeDestino " +
                    "LEFT JOIN FETCH p.unidadeOrigem " +
                    "LEFT JOIN FETCH p.estruturaDestino " +
                    "LEFT JOIN FETCH p.transferencia " +
                    "LEFT JOIN FETCH p.recuperando r " +
                    "LEFT JOIN FETCH r.passagens pa " +
                    "LEFT JOIN FETCH pa.lotacao " +
                    "LEFT JOIN FETCH p.estruturaOrigem " +
                    "LEFT JOIN p.foto " +
                    "WHERE p.id = :id";


            TypedQuery<PedidoTransito> query = entityManager.createQuery(jpql, PedidoTransito.class);
            query.setParameter("id", id);

            PedidoTransito p = query.getSingleResult();

            estruturaDAO.buscarEstruturasCompleta(p.getEstruturaOrigem());
            estruturaDAO.buscarEstruturasCompleta(p.getEstruturaDestino());

            return p;
        } catch (NoResultException e) {
            return null;
        }
    }

   

    public PedidoTransito buscarPedidoCompletoPorId(long id) {
        try {

            String jpql = "SELECT p FROM PedidoTransito p " +
                    "LEFT JOIN FETCH p.autorizador " +
                    "LEFT JOIN FETCH p.unidadeOrigem uOrigem " +
                    "LEFT JOIN FETCH uOrigem.responsavel resp " +
                    "LEFT JOIN FETCH resp.funcao " +
                    "LEFT JOIN FETCH p.unidadeDestino uDestino " +
                    "LEFT JOIN uOrigem.responsavel uor " +
                    "LEFT JOIN FETCH p.recuperando rec " +
                    "LEFT JOIN rec.pessoa pessoa " +
                    "LEFT JOIN uOrigem.endereco cidade " +
                    "LEFT JOIN FETCH rec.fotos foto " +
                    "LEFT JOIN FETCH p.estruturaOrigem eo " +
                    "LEFT JOIN FETCH p.estruturaDestino ed " +
                    "LEFT JOIN FETCH p.estabelecimento e " +
                    "LEFT JOIN p.movimentacaoInterna mi " +
                    "LEFT JOIN p.movimentacaoExterna me " +
                    "LEFT JOIN FETCH rec.passagens passagem " +
                    "LEFT JOIN passagem.lotacao l " +
                    "LEFT JOIN p.transferencia " +
                    "WHERE p.id = :id";

            TypedQuery<PedidoTransito> query = entityManager.createQuery(jpql, PedidoTransito.class);
            query.setParameter("id", id);

            PedidoTransito p = query.getSingleResult();

            estruturaDAO.buscarEstruturasCompleta(p.getEstruturaDestino());
            estruturaDAO.buscarEstruturasCompleta(p.getEstruturaOrigem());

            return p;
        } catch (NoResultException e) {
            return null;
        }
    }

    public PedidoTransito buscaPedidoTransitoImpressaoPorId(long id) {
        try {

            String jpql = "SELECT p FROM PedidoTransito p " +
                    "LEFT JOIN FETCH p.autorizador " +
                    "LEFT JOIN FETCH p.unidadeOrigem uOrigem " +
                    "LEFT JOIN FETCH uOrigem.responsavel resp " +
                    "LEFT JOIN FETCH resp.funcao " +
                    "LEFT JOIN FETCH p.recuperando rec " +
                    "LEFT JOIN rec.pessoa pessoa " +
                    "LEFT JOIN FETCH p.estabelecimento e " +
                    "LEFT JOIN p.movimentacaoInterna mi " +
                    "LEFT JOIN p.movimentacaoExterna me " +
                    "LEFT JOIN rec.passagens passagem " +
                    "LEFT JOIN passagem.lotacao l " +
                    "LEFT JOIN FETCH p.estruturaOrigem eo " +
                    "LEFT JOIN FETCH rec.fotos foto " +
                    "WHERE p.id = :id";

            TypedQuery<PedidoTransito> query = entityManager.createQuery(jpql, PedidoTransito.class);
            query.setParameter("id", id);

            PedidoTransito p = query.getSingleResult();

            estruturaDAO.buscarEstruturasCompleta(p.getEstruturaDestino());
            estruturaDAO.buscarEstruturasCompleta(p.getEstruturaOrigem());

            return p;
        } catch (NoResultException e) {
            return null;
        }
    }

    public PedidoTransito buscaPedidoTransitoPorId(long id) {
        try {

            String jpql = "SELECT p FROM PedidoTransito p " +
                    "LEFT JOIN FETCH p.autorizador " +
                    "LEFT JOIN FETCH p.recuperando rec " +
                    "LEFT JOIN rec.pessoa pessoa " +
                    "LEFT JOIN FETCH p.estabelecimento e " +
                    "LEFT JOIN p.movimentacaoInterna mi " +
                    "LEFT JOIN p.movimentacaoExterna me " +
                    "LEFT JOIN rec.passagens passagem " +
                    "LEFT JOIN passagem.lotacao l " +
                    "LEFT JOIN FETCH p.estruturaOrigem eo " +
                    "LEFT JOIN FETCH p.unidadeOrigem uOrigem " +
                    "LEFT JOIN FETCH p.unidadeDestino uDestino " +
                    "LEFT JOIN FETCH rec.fotos foto " +
                    "WHERE p.id = :id";

            TypedQuery<PedidoTransito> query = entityManager.createQuery(jpql, PedidoTransito.class);
            query.setParameter("id", id);

            PedidoTransito p = query.getSingleResult();
            estruturaDAO.buscarEstruturasCompleta(p.getEstruturaOrigem());


            return p;
        } catch (NoResultException e) {
            return null;
        }
    }

    public PedidoTransito buscaPedidoRemanejamentoPorId(long id) {
        try {

            String jpql = "SELECT p FROM PedidoTransito p " +
                    "LEFT JOIN FETCH p.unidadeOrigem uOrigem " +
                    "LEFT JOIN FETCH p.recuperando rec " +
                    "LEFT JOIN rec.pessoa pessoa " +
                    "LEFT JOIN FETCH rec.passagens passagem " +
                    "LEFT JOIN FETCH passagem.lotacao l " +
                    "WHERE p.id = :id";

            TypedQuery<PedidoTransito> query = entityManager.createQuery(jpql, PedidoTransito.class);
            query.setParameter("id", id);

            PedidoTransito p = query.getSingleResult();

            estruturaDAO.buscarEstruturasCompleta(p.getEstruturaDestino());
            estruturaDAO.buscarEstruturasCompleta(p.getEstruturaOrigem());
            return p;
        } catch (NoResultException e) {
            return null;
        }
    }

    public void expirarPedidosTransito(Date dataExpirar) {
        String jpql = "update PedidoTransito p set p.status = :status,  p.dataCancelamento = :data, p.resultado = :resultado where p.dataPrevista < :dataExpirar and (p.status = :solicitado or p.status = :autorizado)";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("status", StatusPedido.CANCELADO);
        query.setParameter("data", new Date());
        query.setParameter("resultado", "Pedido expirado.");
        query.setParameter("dataExpirar", dataExpirar);
        query.setParameter("solicitado", StatusPedido.SOLICITADO);
        query.setParameter("autorizado", StatusPedido.AUTORIZADO);
        query.executeUpdate();
    }

    public boolean recuperandoEmMovimentacao(Recuperando recuperando) {
        try {
            String jpql = "select COUNT(p) from PedidoTransito p where p.recuperando = :rec and (p.status = :status or p.status = :statusSuspenso)";
            Query query = entityManager.createQuery(jpql);
            query.setParameter("rec", recuperando);
            query.setParameter("status", StatusPedido.EM_ANDAMENTO);
            query.setParameter("statusSuspenso", StatusPedido.SUSPENSO);
            long quantidade = (Long) query.getSingleResult();
            return quantidade > 0;
        } catch (NoResultException e) {
            return true;
        }
    }

    public boolean recuperandoEmMovimentacaoInterna(Recuperando recuperando) {
        try {
            ;
            String jpql = "select COUNT(1) from PedidoTransito p where p.recuperando = :rec AND p.status = :status AND p.tipoTransito = :tipoTransito";
            Query query = entityManager.createQuery(jpql);
            query.setParameter("rec", recuperando);
            query.setParameter("status", StatusPedido.EM_ANDAMENTO);
            query.setParameter("tipoTransito", TipoTransito.INTERNA);
            long quantidade = (Long) query.getSingleResult();
            return quantidade > 0;
        } catch (NoResultException e) {
            return true;
        }
    }

    public boolean recuperandoEmMovimentacaoExterna(Recuperando recuperando) {
        try {
            String jpql = "select COUNT(1) from PedidoTransito p where p.recuperando = :rec AND p.tipoTransito = :tipoTransito AND (p.status = :status or p.status = :statusSuspenso)";
            Query query = entityManager.createQuery(jpql);
            query.setParameter("rec", recuperando);
            query.setParameter("status", StatusPedido.EM_ANDAMENTO);
            query.setParameter("statusSuspenso", StatusPedido.SUSPENSO);
            query.setParameter("tipoTransito", TipoTransito.EXTERNA);
            long quantidade = (Long) query.getSingleResult();
            return quantidade > 0;
        } catch (NoResultException e) {
            return true;
        }
    }

    public boolean recuperandoEmTransferencia(Recuperando recuperando) {
        try {
            String jpql = "select COUNT(1) from PedidoTransito p where p.recuperando = :rec and p.status = :status AND p.tipoTransito = :tipoTransito";
            Query query = entityManager.createQuery(jpql);
            query.setParameter("rec", recuperando);
            query.setParameter("status", StatusPedido.EM_ANDAMENTO);
            query.setParameter("tipoTransito", TipoTransito.TRANSFERENCIA);
            long quantidade = (Long) query.getSingleResult();
            return quantidade > 0;
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

    @SuppressWarnings("unchecked")
    public List<PedidoTransito> buscarPedidosPorTransferencia(final long transferenciaId) {
        Query query = entityManager.createQuery("select p from PedidoTransito p where p.transferencia.id = :transferenciaId");
        query.setParameter("transferenciaId", transferenciaId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<PedidoTransito> buscarPedidosDoRecuperando(long recuperandoId) {
        Query query = entityManager.createQuery("select p from PedidoTransito p where p.recuperando.id = :recuperandoId");
        query.setParameter("recuperandoId", recuperandoId);
        List<PedidoTransito> lista = query.getResultList();
        for (PedidoTransito p : lista) {
            Hibernate.initialize(p.getUnidadeOrigem());
            Hibernate.initialize(p.getUnidadeDestino());
        }
        return lista;
    }

    public void cancelar(PedidoTransito pedido) {
        Query query = entityManager.createQuery(
                "UPDATE PedidoTransito p SET p.status = :status, p.dataCancelamento = :dataCancelamento " +
                        "WHERE p.id = :id"
        );

        query.setParameter("status", pedido.getStatus());
        query.setParameter("dataCancelamento", pedido.getDataCancelamento());
        query.setParameter("id", pedido.getId());
        query.executeUpdate();
    }
    
    
    public PedidoTransito buscarPedidoTransferenciaImpressaoPorId(long id) {
        try {

            String jpql = "SELECT p FROM PedidoTransito p " +
                    "LEFT JOIN FETCH p.autorizador " +
                    "LEFT JOIN FETCH p.unidadeDestino " +
                    "LEFT JOIN FETCH p.unidadeOrigem " +
                    "LEFT JOIN FETCH p.estruturaDestino " +
                    "LEFT JOIN FETCH p.transferencia " +
                    "LEFT JOIN FETCH p.recuperando r " +
                    "LEFT JOIN FETCH r.passagens pa " +
                    "LEFT JOIN FETCH pa.lotacao " +
                    "LEFT JOIN FETCH p.estruturaOrigem " +
                    "LEFT JOIN p.foto " +
                    "WHERE p.id = :id";


            TypedQuery<PedidoTransito> query = entityManager.createQuery(jpql, PedidoTransito.class);
            query.setParameter("id", id);

            PedidoTransito p = query.getSingleResult();

            estruturaDAO.buscarEstruturasCompleta(p.getEstruturaOrigem());
            estruturaDAO.buscarEstruturasCompleta(p.getEstruturaDestino());

            return p;
        } catch (NoResultException e) {
            return null;
        }
    }
}
