package br.gov.mt.sejudh.sigepen.dao.filter;

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.el.ValueExpression;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.primefaces.model.SortOrder;

public abstract class FiltroConsulta implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int primeiroRegistro;
	private int quantidadeRegistros;
	private String propriedadeOrdenacao;
	private boolean ascendente;
	private Criteria criteria;
	private Order ordenacao;
	private int paginacaoIndex;
	private ValueExpression valueExpression;
	private SortOrder sortOrder;
	
	public int getPrimeiroRegistro() {
		return primeiroRegistro;
	}
	public void setPrimeiroRegistro(int primeiroRegistro) {
		this.primeiroRegistro = primeiroRegistro;
	}
	public int getQuantidadeRegistros() {
		return quantidadeRegistros;
	}
	public void setQuantidadeRegistros(int quantidadeRegistros) {
		this.quantidadeRegistros = quantidadeRegistros;
	}
	public String getPropriedadeOrdenacao() {
		return propriedadeOrdenacao;
	}
	public void setPropriedadeOrdenacao(String propriedadeOrdenacao) {
		this.propriedadeOrdenacao = propriedadeOrdenacao;
	}
	
	public boolean isAscendente() {
		return ascendente;
	}
	
	public void setAscendente(boolean ascendente) {
		this.ascendente = ascendente;
	}
	
	public Criteria getCriteria() {
		return criteria;
	}
	
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	public Order getOrdenacao() {
		return ordenacao;
	}
	
	public void setOrdenacao(Order ordenacao) {
		this.ordenacao = ordenacao;
	}

	public int getPaginacaoIndex() {
		return paginacaoIndex;
	}
	
	public void setPaginacaoIndex(int paginacaoIndex) {
		this.paginacaoIndex = paginacaoIndex;
	}
	
	public ValueExpression getValueExpression() {
		return valueExpression;
	}
	
	public void setValueExpression(ValueExpression valueExpression) {
		this.valueExpression = valueExpression;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}
	
	public <T> Criteria criarCriteriaParaQuantidade(Class<T> classe, EntityManager em) {
		return criarCriteria(classe, em, false, false);
	}
	
	public <T> Criteria criarCriteriaParaBusca(Class<T> classe, EntityManager em) {
		return criarCriteria(classe, em, true, false);
	}
	
	public <T> Criteria criarCriteriaParaBusca(Class<T> classe, EntityManager em, boolean isBuscaRelatorio) {
		return criarCriteria(classe, em, true, isBuscaRelatorio);
	}
	
	private <T> Criteria criarCriteria(Class<T> classe, EntityManager em, boolean isBuscaRegistro, boolean isBuscaRelatorio) {
		Session session = em.unwrap(Session.class);
		setCriteria(session.createCriteria(classe));

		if (isBuscaRegistro) {
			if (getPropriedadeOrdenacao() != null && getPropriedadeOrdenacao().contains(".")) {
				String objeto = getPropriedadeOrdenacao();
				String propriedade = objeto.substring(objeto.lastIndexOf(".") + 1, objeto.length());
				String nomeAlias = getPropriedadeOrdenacao().substring(0, getPropriedadeOrdenacao().lastIndexOf("."));
				String[] lista = nomeAlias.split(Pattern.quote("."));
				
				setPropriedadeOrdenacao(lista[lista.length-1] + "." + propriedade);
			}
			
			if (!isBuscaRelatorio) {
				getCriteria().setFirstResult(this.getPrimeiroRegistro());
				getCriteria().setMaxResults(this.getQuantidadeRegistros());
			}

			configurarOrdenacao();
		}
		
		configurarFiltros();

		return getCriteria();
	}
	
	public void configurarOrdenacao() {
		if (this.ordenacao != null) {
			getCriteria().addOrder(this.ordenacao);
		}
	}
	
	public void organizaFiltro(int first, int pageSize, String sortField, SortOrder sortOrder){
		this.setPrimeiroRegistro(first);
		this.setQuantidadeRegistros(pageSize);
		this.setAscendente(SortOrder.ASCENDING.equals(sortOrder));
		this.setPropriedadeOrdenacao(sortField);
		this.sortOrder = sortOrder;
		
		if (isAscendente() && getPropriedadeOrdenacao() != null) {
			this.ordenacao = Order.asc(getPropriedadeOrdenacao());
		} else if(getPropriedadeOrdenacao() != null){
			this.ordenacao = Order.desc(getPropriedadeOrdenacao());
		}
	}
	
	/**
	 * Método onde se adiciona as cláusulas "wheres" da criteria.
	 */
	public abstract void configurarFiltros();
}
