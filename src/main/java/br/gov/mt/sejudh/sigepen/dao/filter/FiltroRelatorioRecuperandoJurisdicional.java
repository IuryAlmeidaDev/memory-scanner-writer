package br.gov.mt.sejudh.sigepen.dao.filter;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.gov.mt.sejudh.sigepen.enumerator.CondicaoRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.Esfera;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperandoRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDesligamentoRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntradaRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoRelatorioEntradaSaidaRec;
import br.gov.mt.sejudh.sigepen.enumerator.UfRelatorio;
import br.gov.mt.sejudh.sigepen.model.Estabelecimento;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;


public class FiltroRelatorioRecuperandoJurisdicional extends FiltroConsulta {
	
	private static final long serialVersionUID = 1L;
	
	private TipoRelatorioEntradaSaidaRec tipoRelatorioEntradaSaidaRec;
	private StatusRecuperandoRelatorio  situacaoRecuperando;
	private UfRelatorio estadoEstabelecimento;
	private Esfera esferaEstabelecimento;
	private Estabelecimento estabelecimento;
	private Regime regime;
	private CondicaoRelatorio condicaoAtual;
	private long unidade;
	private Date dataInicial;
	private Date dataFinal;
	private String numeroProcesso;
	private String numeroDocumento;
	private boolean somenteEntradas;
	private boolean somenteDesligamentos;
	private TipoDesligamentoRelatorio tipoDesligamento;
	private TipoEntradaRelatorio tipoProcesso;

	private UnidadePrisional unidadePrisional;
	
	@Override
	public void configurarFiltros() {
		
	}

	public TipoRelatorioEntradaSaidaRec getTipoRelatorioEntradaSaidaRec() {
		return tipoRelatorioEntradaSaidaRec;
	}

	public void setTipoRelatorioEntradaSaidaRec(TipoRelatorioEntradaSaidaRec tipoRelatorioEntradaSaidaRec) {
		this.tipoRelatorioEntradaSaidaRec = tipoRelatorioEntradaSaidaRec;
	}

	public StatusRecuperandoRelatorio getSituacaoRecuperando() {
		return situacaoRecuperando;
	}

	public void setSituacaoRecuperando(StatusRecuperandoRelatorio situacaoRecuperando) {
		this.situacaoRecuperando = situacaoRecuperando;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(dataInicial);
	    calendar.add(Calendar.HOUR_OF_DAY, 0);
	    calendar.add(Calendar.MINUTE, 0);
	    calendar.add(Calendar.SECOND, 0);
	    
		this.dataInicial = calendar.getTime();
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(dataFinal);
	    calendar.add(Calendar.HOUR_OF_DAY, 23);
	    calendar.add(Calendar.MINUTE, 23);
	    calendar.add(Calendar.SECOND, 23);
	    
		this.dataFinal = calendar.getTime();
	}

	public Regime getRegime() {
		return regime;
	}

	public void setRegime(Regime regime) {
		this.regime = regime;
	}

	public CondicaoRelatorio getCondicaoAtual() {
		return condicaoAtual;
	}

	public void setCondicaoAtual(CondicaoRelatorio condicaoAtual) {
		this.condicaoAtual = condicaoAtual;
	}
	
	public Esfera getEsferaEstabelecimento() {
		return esferaEstabelecimento;
	}

	public void setEsferaEstabelecimento(Esfera esferaEstabelecimento) {
		this.esferaEstabelecimento = esferaEstabelecimento;
	}

	public UfRelatorio getEstadoEstabelecimento() {
		return estadoEstabelecimento;
	}

	public void setEstadoEstabelecimento(UfRelatorio estadoEstabelecimento) {
		this.estadoEstabelecimento = estadoEstabelecimento;
	}

	public String getNumeroProcesso() {
		return numeroProcesso;
	}

	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}
	
	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public long getUnidade() {
		return unidade;
	}

	public void setUnidade(long unidade) {
		this.unidade = unidade;
	}
	
	public String getDescricao() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String filtros = "";
		
		if(this.getTipoRelatorioEntradaSaidaRec() != null) 
			filtros += "Tipo Relatório: " + this.getTipoRelatorioEntradaSaidaRec().getDescricao() + ", ";
		
		if(this.getTipoProcesso() != null && !TipoEntradaRelatorio.TODAS.equals(this.getTipoProcesso()))
			filtros += "Tipo Entrada: " + this.getTipoProcesso().getDescricao() + ", ";
		
		if(this.getTipoDesligamento() != null && !TipoDesligamentoRelatorio.TODOS.equals(this.getTipoDesligamento()))
			filtros += "Tipo Desligamento: " + this.getTipoDesligamento().getDescricao() + ", ";
		
		if(this.getDataInicial() != null && this.getDataFinal() != null)
			filtros += "Período: " + sdf.format(this.getDataInicial()) + " a " + sdf.format(this.getDataFinal()) + ", ";

		if(this.estabelecimento != null)
		filtros += "Seção Jurisdicional: " + this.estabelecimento.getNome() + ", ";
		
		if(this.getEstadoEstabelecimento() != null && !UfRelatorio.TT.equals(this.getEstadoEstabelecimento()))
			filtros += "Estado do Estabelecimento: " + this.getEstadoEstabelecimento().getDescricao() + ", ";
		
		if(this.getEsferaEstabelecimento() != null)
			filtros += "Esfera do Estabelecimento: " + this.getEsferaEstabelecimento().getDescricao() + ", ";
		
		
		if(this.getRegime() != null)
			filtros += "Regime: " + this.getRegime().getDescricao() + ", ";
		
		if(this.numeroProcesso != null)
			filtros += "Número Processo: " + this.getNumeroProcesso() + ", ";
			
		if(this.numeroDocumento != null)
			filtros += "Número Documento: " + this.getNumeroDocumento() + ", ";
		
		if(this.getCondicaoAtual() != null && !CondicaoRelatorio.TODAS.equals(this.getCondicaoAtual()))
			filtros += "Condição atual: " + this.getCondicaoAtual().getDescricao() + ", ";
		
		if(this.getSituacaoRecuperando() != null && !StatusRecuperandoRelatorio.TODOS.equals(this.getSituacaoRecuperando()))
			filtros += "Situação do Recuperando: " + this.getSituacaoRecuperando().getDescricao() + ", ";
		
		if(!"".equals(filtros))
			return "Filtro utilizado: " + filtros;
		
		return null;
	}

	public boolean isSomenteEntradas() {
		return somenteEntradas;
	}

	public void setSomenteEntradas(boolean somenteEntradas) {
		this.somenteEntradas = somenteEntradas;
	}

	public boolean isSomenteDesligamentos() {
		return somenteDesligamentos;
	}

	public void setSomenteDesligamentos(boolean somenteDesligamentos) {
		this.somenteDesligamentos = somenteDesligamentos;
	}

	public Estabelecimento getEstabelecimento() {
		return estabelecimento;
	}

	public void setEstabelecimento(Estabelecimento estabelecimento) {
		this.estabelecimento = estabelecimento;
	}

	public TipoDesligamentoRelatorio getTipoDesligamento() {
		return tipoDesligamento;
	}

	public void setTipoDesligamento(TipoDesligamentoRelatorio tipoDesligamento) {
		this.tipoDesligamento = tipoDesligamento;
	}

	public TipoEntradaRelatorio getTipoProcesso() {
		return tipoProcesso;
	}

	public void setTipoProcesso(TipoEntradaRelatorio tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
	}

	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}

}
