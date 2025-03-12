package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroSolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.PessoaSolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.model.SolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.model.dto.SolicitacaoVisitaDTO;

@RequestScoped
public class SolicitacaoVisitaDAO extends BaseDAO<SolicitacaoVisita, Long> {

	private static final long serialVersionUID = 5535628352115469694L;

	@Override
    public SolicitacaoVisita buscarPorId(Long id, Boolean buscarAuditoria) {
        SolicitacaoVisita solicitacaoVisita =  super.buscarPorId(id, buscarAuditoria);
        if (solicitacaoVisita == null) return null;
        Hibernate.initialize(solicitacaoVisita.getPessoaSolicitacao());
        Hibernate.initialize(solicitacaoVisita.getRecuperando().getParentes());

        return solicitacaoVisita;
    }

    @Override
    public SolicitacaoVisita buscarPorId(Long id) {
        return this.buscarPorId(id, false);
    }

    public Optional<SolicitacaoVisita> buscarPorParente(Parente parente) {
        TypedQuery<SolicitacaoVisita> query = em.createQuery("SELECT s FROM SolicitacaoVisita s WHERE s.parente = :parente ", SolicitacaoVisita.class);
        query.setParameter("parente", parente);
        try {
            return Optional.of(query.getSingleResult());
        }catch (NoResultException ex) {
            return Optional.empty();
        }
    }


    
	public PessoaSolicitacaoVisita buscarPessoaSolicitacaoPorId(long id) {
		try {
			String jpql="select p from PessoaSolicitacaoVisita p where p.id = :id";
			TypedQuery<PessoaSolicitacaoVisita> query = em.createQuery(jpql, PessoaSolicitacaoVisita.class);
			query.setParameter("id", id);
			PessoaSolicitacaoVisita p = query.getSingleResult();
			return p;
		} catch (NoResultException e) {
			return null;
		}
	}

	public int buscarQuantidadeFiltrada(FiltroSolicitacaoVisita filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Pessoa.class, em);
		criteria.setProjection(Projections.rowCount());

		int teste = ((Number) criteria.uniqueResult()).intValue();
        return teste;
	}

	@SuppressWarnings("unchecked")
	public List<SolicitacaoVisitaDTO> buscarSolicitacoesPorFiltro(FiltroSolicitacaoVisita filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Pessoa.class, em, isBuscaRelatorio);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(SolicitacaoVisitaDTO.class));

        return (List<SolicitacaoVisitaDTO>) criteria.list();

	}
	
    public List<SolicitacaoVisita> buscarPorParente2(Parente parente) {
        TypedQuery<SolicitacaoVisita> query = em.createQuery("SELECT s FROM SolicitacaoVisita s WHERE s.parente = :parente ", SolicitacaoVisita.class);
        query.setParameter("parente", parente);
        return query.getResultList();
    }

    public List<SolicitacaoVisita> buscarUltimaSolicitacaoAtiva(FiltroSolicitacaoVisita filtro) {
        TypedQuery<SolicitacaoVisita> query = em.createQuery("SELECT s FROM SolicitacaoVisita s WHERE s.parente = :parente AND s.recuperando = :recuperando AND s.status = :status", SolicitacaoVisita.class);
        query.setParameter("parente", filtro.getParente());
        query.setParameter("recuperando", filtro.getRecuperandoO());
        query.setParameter("status", filtro.getStatus());
        return query.getResultList();
    }

	public void excluir(Long id) throws ValidacaoException {
		SolicitacaoVisita certidao = this.em.find(SolicitacaoVisita.class, id);
        try {
        	if(certidao.getParente() != null) {
        		throw new ValidacaoException("Registro vinculado a outro. Não pode ser excluido.");
        	}
        	this.em.remove(this.em.merge(certidao));
        } catch (ValidacaoException e) {
            throw new ValidacaoException(e.getMessage());
        } catch (Exception e) {
            if (e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause() instanceof ConstraintViolationException) {
            	throw new ValidacaoException("Registro vinculado a outro. Não pode ser excluido.");
            }
            throw new ValidacaoException("Erro ao excluir SolicitacaoVisita (" + certidao.getPessoaSolicitacao().getNome() + "): " + e.getMessage());
        }
	}

	public SolicitacaoVisita buscarSolicitacaoVisitaPorId(long id) {
        String sql = "select s from SolicitacaoVisita s " +
                "left join fetch s.recuperando " +
                "left join fetch s.recuperando.pessoa " +
                "left join fetch s.pessoaSolicitacao " +
                "left join fetch s.parente " +
                "WHERE s.id = :id ";


        TypedQuery<SolicitacaoVisita> query = em.createQuery(sql, SolicitacaoVisita.class);
        query.setParameter("id", id);
        return query.getSingleResult();
		
	}

}
