package br.gov.mt.sejudh.sigepen.dao;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroHorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.dto.HorarioRefeicaoDTO;

@RequestScoped
public class HorarioRefeicaoDAO extends BaseDAO<HorarioRefeicao, Long>{

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
}
