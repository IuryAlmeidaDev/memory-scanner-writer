package br.gov.mt.sejudh.sigepen.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.dto.VisitanteListaDTO;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDocumentoPedidoTransito;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import org.hibernate.transform.Transformers;


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

//    public PedidoTransito buscarPedidoCompletoPorId(long id) {
//        try {
//            String jpql="select p from PedidoTransito p where p.id = :id";
//            TypedQuery<PedidoTransito> query = entityManager.createQuery(jpql, PedidoTransito.class);
//            query.setParameter("id", id);
//
//            PedidoTransito p = query.getSingleResult();
//
//            Hibernate.initialize(p.getAutorizador());
//            Hibernate.initialize(p.getUnidadeOrigem());
//            Hibernate.initialize(p.getUnidadeOrigem().getResponsavel());
//
//            if (p.getUnidadeOrigem().getResponsavel() != null) {
//                Hibernate.initialize(p.getUnidadeOrigem().getResponsavel().getFuncao());
//            }
//
//            if(p.getTipoDocumentoPedidoTransito() == TipoDocumentoPedidoTransito.OFICIO || p.getTipoDocumentoPedidoTransito() == TipoDocumentoPedidoTransito.AUTORIZACAO){
//                Hibernate.initialize(p.getRecuperando().getFotos());
//                Hibernate.initialize(p.getEstruturaOrigem());
//                Hibernate.initialize(p.getEstruturaDestino());
//                Hibernate.initialize(p.getEstabelecimento());
//                Hibernate.initialize(p.getResponsavelDestino());
//                Hibernate.initialize(p.getUnidadeOrigem().getEndereco().getCidade());
//            }
//
//            if(p.getTipoTransito() == TipoTransito.INTERNA){
//                Hibernate.initialize(p.getEstruturaOrigem());
//                Hibernate.initialize(p.getEstruturaDestino());
//                Hibernate.initialize(p.getEstruturaDestino().getEstruturaComplemento());
//                Hibernate.initialize(p.getMovimentacaoInterna());
//            }
//            if(p.getTipoTransito() == TipoTransito.EXTERNA){
//                Hibernate.initialize(p.getEstabelecimento());
//                Hibernate.initialize(p.getMovimentacaoExterna());
//                Hibernate.initialize(p.getRecuperando().getPassagens());
//
//                for (Passagem pa : p.getRecuperando().getPassagens()) {
//                    Hibernate.initialize(pa.getLotacao());
//                }
//            }
//            if(p.getTipoTransito() == TipoTransito.TRANSFERENCIA){
//                Hibernate.initialize(p.getUnidadeDestino());
//                Hibernate.initialize(p.getEstruturaDestino());
//                Hibernate.initialize(p.getTransferencia());
//                Hibernate.initialize(p.getRecuperando().getPassagens());
//
//                for (Passagem pa : p.getRecuperando().getPassagens()) {
//                    Hibernate.initialize(pa.getLotacao());
//                }
//            }
//
//            return p;
//        } catch (NoResultException e) {
//            return null;
//        }
//    }


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
                    "LEFT JOIN FETCH rec.fotos foto " + // Apenas se o tipo de documento for OFICIO ou AUTORIZACAO
                    "LEFT JOIN FETCH p.estruturaOrigem eo " +
                    "LEFT JOIN FETCH p.estruturaDestino ed " +
                    "LEFT JOIN FETCH p.estabelecimento e " + // Apenas se o tipo de documento for OFICIO ou AUTORIZACAO
                    "LEFT JOIN p.movimentacaoInterna mi " + // Apenas se o tipo de transito for INTERNA
                    "LEFT JOIN p.movimentacaoExterna me " + // Apenas se o tipo de transito for EXTERNA
                    "LEFT JOIN rec.passagens passagem " + // Apenas se o tipo de transito for EXTERNA ou TRANSFERENCIA
                    "LEFT JOIN passagem.lotacao l " + // Apenas se o tipo de transito for EXTERNA ou TRANSFERENCIA
                    "WHERE p.id = :id";

            TypedQuery<PedidoTransito> query = entityManager.createQuery(jpql, PedidoTransito.class);
            query.setParameter("id", id);

            PedidoTransito p = query.getSingleResult();

            estruturaDAO.buscarEstruturasCompleta(p.getEstruturaDestino());
            estruturaDAO.buscarEstruturasCompleta(p.getEstruturaOrigem());

//            getEstrutura(p.getEstruturaDestino());
//            getEstrutura(p.getEstruturaOrigem());


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
}
