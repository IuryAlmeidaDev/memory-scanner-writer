package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.annotations.QueryHints;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.model.EntradaSaida;
import br.gov.mt.sejudh.sigepen.model.EntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.dto.EntradaSaidaDTO;

@RequestScoped
public class EntradaSaidaRecuperandoDAO {

	@Inject
	private EntityManager em;

	public int buscarQuantidadeFiltrada(FiltroEntradaSaidaRecuperando filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(EntradaSaidaRecuperando.class, em);
		criteria.addQueryHint(QueryHints.READ_ONLY);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<EntradaSaidaDTO> buscarEntradasPorFiltro(FiltroEntradaSaidaRecuperando filtro, boolean isRelatorio) {
		
		Criteria criteria = filtro.criarCriteriaParaBusca(EntradaSaidaRecuperando.class, em, isRelatorio);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(EntradaSaidaDTO.class));
		criteria.addQueryHint(QueryHints.READ_ONLY);
		
		List<EntradaSaidaDTO> entradas = criteria.list();

		return entradas;
	}

	public List<TipoVisita> listarTipoVisitas(TipoEntrada te) {
		if(te == null) {
			TypedQuery<TipoVisita> query = em.createQuery("select f from TipoVisita f order by f.descricao asc",
					TipoVisita.class);
			return query.getResultList();
		} else {
			TypedQuery<TipoVisita> query = em.createQuery("select f from TipoVisita f where f.tipoEntrada = :te order by f.descricao asc",
					TipoVisita.class);
			query.setParameter("te", te);
			return query.getResultList();
		}
	}

	public EntradaSaida salvar(EntradaSaida entrada) {
		if(entrada.getId() == null)
			this.em.persist(entrada);
		else
			entrada = this.em.merge(entrada);
		return entrada;
	}
	
}
