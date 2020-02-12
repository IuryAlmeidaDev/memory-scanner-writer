package br.gov.mt.sejudh.sigepen.bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.PrettyContext;

import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

public abstract class MetodosDePaginaBean {
	
	private String tituloPagina;
	
	protected boolean carregaPaginacao;
	
	@Inject
	private FacesUtil facesUtil;
	
	@PostConstruct
	public void inicio() {
		this.modificaTituloPagina();
	}
	
	public String getTituloPagina() {
		return tituloPagina;
	}

	public void setTituloPagina(String tituloPagina) {
		this.tituloPagina = tituloPagina;
	}

	public boolean getIsVisualizacao() {
		return getAcaoDeTela().equals(AcaoDeTela.VISUALIZAR);
	}
	
	public boolean getIsListagem() {
		return getAcaoDeTela().equals(AcaoDeTela.LISTAR);
	}
	
	protected AcaoDeTela getAcaoDeTela() {
		AcaoDeTela acao = null;
		
		if (PrettyContext.getCurrentInstance().getCurrentMapping().getPattern().contains("cadastrar")) acao = AcaoDeTela.CADASTRAR;
		else if (PrettyContext.getCurrentInstance().getCurrentMapping().getPattern().contains("alterar")) acao = AcaoDeTela.ALTERAR;
		else if (PrettyContext.getCurrentInstance().getCurrentMapping().getPattern().contains("visualizar")) acao = AcaoDeTela.VISUALIZAR;
		else if (PrettyContext.getCurrentInstance().getCurrentMapping().getPattern().contains("iniciar")) acao = AcaoDeTela.INICIAR;
		else if (PrettyContext.getCurrentInstance().getCurrentMapping().getPattern().contains("finalizar")) acao = AcaoDeTela.FINALIZAR;
		else if (PrettyContext.getCurrentInstance().getCurrentMapping().getPattern().contains("receber")) acao = AcaoDeTela.RECEBER;
		else acao = AcaoDeTela.LISTAR;
		
		return acao;
	}
	
	protected void modificaTituloPagina() {
		AcaoDeTela acao = this.getAcaoDeTela();		
		
		switch (acao) {			
			case CADASTRAR:
				this.setTituloPagina("Cadastrar");
				break;
				
			case VISUALIZAR:
				this.setTituloPagina("Visualizar");
				break;

			case ALTERAR:
				this.setTituloPagina("Alterar");
				break;
				
			case INICIAR:
				this.setTituloPagina("Iniciar");
				break;
				
			case FINALIZAR:
				this.setTituloPagina("Finalizar");
				break;
				
			default:
				break;
		}
	}
	
	protected void logDeAcesso(Logger log, Object obj) {
		AcaoDeTela acao = this.getAcaoDeTela();		
		
		switch (acao) {			
			case ALTERAR:
				LoggerUtil.logInfo(Acao.ALTERAR, obj, log);
				break;
				
			case VISUALIZAR:
				LoggerUtil.logInfo(Acao.VISUALIZAR, obj, log);
				break;
				
			case LISTAR:
				LoggerUtil.logInfo(Acao.ACESSAR, obj, log);
				break;
				
			default:
				break;
		}
	}
	
	protected boolean isDialogInvalida() {
		String dialogIdServidor = (String)facesUtil.getObjetoSessao("pfdlcid");
		
		if (dialogIdServidor == null) {
			return true;
		}
		
		String dialogIdcliente = PrettyContext.getCurrentInstance().getRequestQueryString().getParameter("pfdlgcid");
		
		if (dialogIdcliente == null || !dialogIdcliente.equals(dialogIdServidor)) {
			return true;
		}
		
		return false;
	}
	
	protected void configuraPaginacao(int rowCount, int first, int pageSize, String sortField, SortOrder sortOrder, Object filtro) throws NoSuchMethodException, SecurityException, NumberFormatException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DataTable dt = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("registrosForm:dataTable");
		
		if (first >= rowCount) {
			first = 0;
			dt.setFirst(first);
		}
		
		if (carregaPaginacao) {
			Method getPaginacaoIndexMetodo = filtro.getClass().getMethod("getPaginacaoIndex", ArrayUtils.EMPTY_CLASS_ARRAY);
			first = Integer.parseInt(getPaginacaoIndexMetodo.invoke(filtro, ArrayUtils.EMPTY_OBJECT_ARRAY).toString()); 
			
			Method getSortBy = filtro.getClass().getMethod("getValueExpression", ArrayUtils.EMPTY_CLASS_ARRAY);
			ValueExpression ve = (ValueExpression) getSortBy.invoke(filtro, ArrayUtils.EMPTY_OBJECT_ARRAY);
			
			Method getOrdenacao = filtro.getClass().getMethod("getSortOrder", ArrayUtils.EMPTY_CLASS_ARRAY);
			SortOrder ordenacao = (SortOrder) getOrdenacao.invoke(filtro, ArrayUtils.EMPTY_OBJECT_ARRAY);

			dt.setFirst(first);
			dt.setValueExpression("sortBy", ve);
			dt.setSortOrder(ordenacao.name());
			
			carregaPaginacao = false;
		} else {
			Method organizaFiltroMetodo = filtro.getClass().getMethod("organizaFiltro", int.class, int.class, String.class, SortOrder.class);
			organizaFiltroMetodo.invoke(filtro, first, pageSize, sortField, sortOrder);
		}
		
		Method setPaginacaoIndexMetodo = filtro.getClass().getMethod("setPaginacaoIndex", int.class);
		setPaginacaoIndexMetodo.invoke(filtro, first);
		
		Method setValueExpressionMetodo = filtro.getClass().getMethod("setValueExpression", ValueExpression.class);
		setValueExpressionMetodo.invoke(filtro, dt.getValueExpression("sortBy"));

		facesUtil.setObjetoSessao(filtro.getClass().getSimpleName(), filtro.getClass().cast(filtro));
	}

	public void carregarFiltroPaginacao(Class<?> filtroClasse, Object bean) throws Exception {
		carregarFiltroPaginacao(filtroClasse, bean, null);
	}
	
	public void carregarFiltroPaginacao(Class<?> filtroClasse, Object bean, Object filtro) throws Exception {
		if (getAcaoDeTela().equals(AcaoDeTela.LISTAR)) {
			String param = facesUtil.getQueryParameter("list");
			Object filtroSessao = facesUtil.getObjetoSessao(filtroClasse.getSimpleName());
			Method setFiltro = bean.getClass().getMethod("setFiltro", filtroClasse);
			
			if (filtroSessao != null && param != null && param.equals("1")) {
				setFiltro.invoke(bean, filtroClasse.cast(filtroSessao));
				carregaPaginacao = true;
			} else {
				if (filtro == null) {
					setFiltro.invoke(bean, filtroClasse.newInstance());
				} else {
					setFiltro.invoke(bean, filtro);
				}
			}
		}
	}
}
