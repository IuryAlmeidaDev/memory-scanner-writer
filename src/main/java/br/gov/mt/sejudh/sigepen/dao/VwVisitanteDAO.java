package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVwVisitante;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.view.VwVisitante;

@RequestScoped
public class VwVisitanteDAO extends BaseDAO<VwVisitante, Long> {


	private static final long serialVersionUID = -8941606440978113368L;

	public int quantidadeVwVisitante(FiltroVwVisitante filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Pessoa.class, em);
        criteria.setProjection(Projections.countDistinct("id"));
        return ((Number) criteria.uniqueResult()).intValue();
    }

	@SuppressWarnings("unchecked")
	public List<VwVisitante> buscarPorFiltro(FiltroVwVisitante filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Pessoa.class, em, isBuscaRelatorio);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(VwVisitante.class));
		List<VwVisitante> lista = criteria.list();
		for(VwVisitante x : lista) {
			if(x.getId() == null)
				x.setId(0L);
		}
		return lista;
	}
}
