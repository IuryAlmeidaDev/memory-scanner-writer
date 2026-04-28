package br.gov.mt.sejudh.sigepen.dao;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroHorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.dto.HorarioRefeicaoDTO;

@RequestScoped
public class HorarioRefeicaoDAO extends BaseDAO<HorarioRefeicao, Long>{
	
	@Inject
	private EntityManager em;
	
	private static final long serialVersionUID = 291027809840436611L;
	
	@SuppressWarnings("unchecked")
	public List<HorarioRefeicao> buscarPorUnidade(long unidadeId) {
		String jpql = "select e from HorarioRefeicao e where e.unidadePrisional.id = :unidadeId";
		Query query = em.createQuery(jpql, HorarioRefeicao.class);
		query.setParameter("unidadeId", unidadeId);
		
		List<HorarioRefeicao> horariosRefeicao = query.getResultList();

		return horariosRefeicao;
	}
	
	@SuppressWarnings("unchecked")
	public List<HorarioRefeicao> buscarPorContrato(long contratoId) {
		String jpql = "select e from HorarioRefeicao e where e.contrato.id = :contratoId";
		Query query = em.createQuery(jpql, HorarioRefeicao.class);
		query.setParameter("contratoId", contratoId);
		
		List<HorarioRefeicao> horariosRefeicao = query.getResultList();

		return horariosRefeicao;
	}

	@SuppressWarnings("unchecked")
	public HorarioRefeicao buscarVigentePorUnidadeETipo(Long unidadeId, Long tipoRefeicaoId, Date dataReferencia) {
		String jpql = "select h from HorarioRefeicao h "
				+ "where h.unidadePrisional.id = :unidadeId "
				+ "and h.tipoRefeicao.id = :tipoRefeicaoId "
				+ "and h.dataInicio <= :dataReferencia "
				+ "and (h.dataFim is null or h.dataFim >= :dataReferencia) "
				+ "order by h.dataInicio desc, h.id desc";
		Query query = em.createQuery(jpql, HorarioRefeicao.class);
		query.setParameter("unidadeId", unidadeId);
		query.setParameter("tipoRefeicaoId", tipoRefeicaoId);
		query.setParameter("dataReferencia", dataReferencia);
		query.setMaxResults(1);
		List<HorarioRefeicao> result = query.getResultList();
		return result.isEmpty() ? null : result.get(0);
	}

	@SuppressWarnings("unchecked")
	public HorarioRefeicao buscarVigentePorContratoETipo(Long contratoId, Long tipoRefeicaoId, Date dataReferencia) {
		String jpql = "select h from HorarioRefeicao h "
				+ "where h.contrato.id = :contratoId "
				+ "and h.unidadePrisional is null "
				+ "and h.tipoRefeicao.id = :tipoRefeicaoId "
				+ "and h.dataInicio <= :dataReferencia "
				+ "and (h.dataFim is null or h.dataFim >= :dataReferencia) "
				+ "order by h.dataInicio desc, h.id desc";
		Query query = em.createQuery(jpql, HorarioRefeicao.class);
		query.setParameter("contratoId", contratoId);
		query.setParameter("tipoRefeicaoId", tipoRefeicaoId);
		query.setParameter("dataReferencia", dataReferencia);
		query.setMaxResults(1);
		List<HorarioRefeicao> result = query.getResultList();
		return result.isEmpty() ? null : result.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public List<HorarioRefeicao> buscarPorUnidadeDataInicio(long unidadeId, Date dataInicio) {
		String jpql = "select e from HorarioRefeicao e where e.unidadePrisional.id = :unidadeId and e.dataInicio = :dataInicio";
		Query query = em.createQuery(jpql, HorarioRefeicao.class);
		query.setParameter("unidadeId", unidadeId);
		query.setParameter("dataInicio", dataInicio);
		
		List<HorarioRefeicao> horariosRefeicao = query.getResultList();

		return horariosRefeicao;
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean temHorarioRefeicao(Long unidadeId) {
		
		String jpql = "select e from HorarioRefeicao e where e.unidadePrisional.id = :unidadeId";
		Query query = em.createQuery(jpql, HorarioRefeicao.class);
		query.setParameter("unidadeId", unidadeId);
		
		List<HorarioRefeicao> horariosRefeicao = query.getResultList();
		
		return !horariosRefeicao.isEmpty();
		
	}
	
	@SuppressWarnings("unchecked")
    public List<HorarioRefeicaoDTO> buscaPorFiltro(FiltroHorarioRefeicao filtro, boolean isRelatorio) {
        Criteria criteria = filtro.criarCriteriaParaBusca(HorarioRefeicao.class, em, isRelatorio);
        criteria.setResultTransformer(new AliasToBeanResultTransformer(HorarioRefeicaoDTO.class));
        return criteria.list();
    }
	
	public int contarFiltrados(FiltroHorarioRefeicao filtro) {
		return filtro.contarRegistros(em);
	}
}
