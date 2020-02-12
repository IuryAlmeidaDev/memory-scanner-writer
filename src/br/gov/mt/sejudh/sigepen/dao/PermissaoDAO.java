package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPermissao;
import br.gov.mt.sejudh.sigepen.model.Permissao;

@RequestScoped
public class PermissaoDAO {

	@Inject
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<Permissao> buscarPermissoesPorFiltro(FiltroPermissao filtro) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Permissao.class, em);
		return criteria.list();
	}
	
	public int buscarQuantidadeFiltrada(FiltroPermissao filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Permissao.class, em);		
		criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}
}
