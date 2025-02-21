package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.model.Pessoa;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVwVisitante;
import br.gov.mt.sejudh.sigepen.model.view.VwVisitante;

import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.primefaces.model.SortOrder;

@RequestScoped
public class VwVisitanteDAO extends BaseDAO<VwVisitante, Long> {
   @Inject
    private BuscarVwVisitanteDAO dao ;



    private static final long serialVersionUID = -8941606440978113368L;

    public int quantidadeVwVisitante(FiltroVwVisitante filtro) {
        List<VwVisitante> object = dao.buscarVwVisitante(filtro, true, 0 , 0, null , null);
        return Math.toIntExact(object.get(0).getCredenciamentosAtivos());
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


    public List<VwVisitante> buscarPorFiltroVW(FiltroVwVisitante filtro, boolean isBuscaRelatorio, int first, int pageSize, String sortField, SortOrder sortOrder) {
        
        List<VwVisitante> lista = dao.buscarVwVisitante(filtro, false, first, pageSize,  sortField,  sortOrder);
        return lista;
    }
}
