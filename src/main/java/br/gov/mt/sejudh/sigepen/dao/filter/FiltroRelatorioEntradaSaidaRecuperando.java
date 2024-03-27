package br.gov.mt.sejudh.sigepen.dao.filter;


import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.mt.sejudh.sigepen.enumerator.CondicaoRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDesligamentoRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntradaRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoRelatorioEntradaSaidaRec;
import br.gov.mt.sejudh.sigepen.model.Regime;


public class FiltroRelatorioEntradaSaidaRecuperando extends FiltroConsulta {

	private static final long serialVersionUID = 5965563648229745957L;
	private TipoRelatorioEntradaSaidaRec tipoRelatorioEntradaSaidaRec;
	private Regime regime;
	private CondicaoRelatorio condicaoAtual;
	private long unidade;
	private Date dataInicial;
	private Date dataFinal;
	private TipoDesligamentoRelatorio tipoDesligamento;
	private TipoEntradaRelatorio tipoProcesso;

	@Override
	public void configurarFiltros() {
		
	}

	public TipoRelatorioEntradaSaidaRec getTipoRelatorioEntradaSaidaRec() {
		return tipoRelatorioEntradaSaidaRec;
	}

	public void setTipoRelatorioEntradaSaidaRec(TipoRelatorioEntradaSaidaRec tipoRelatorioEntradaSaidaRec) {
		this.tipoRelatorioEntradaSaidaRec = tipoRelatorioEntradaSaidaRec;
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

	public long getUnidade() {
		return unidade;
	}

	public void setUnidade(long unidade) {
		this.unidade = unidade;
	}
	
	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public TipoDesligamentoRelatorio getTipoDesligamento() {
		return tipoDesligamento;
	}

	public void setTipoDesligamento(TipoDesligamentoRelatorio tipoDesligamento) {
		this.tipoDesligamento = tipoDesligamento;
	}

	public String getDescricao() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String filtros = "";
		
		if(this.getTipoRelatorioEntradaSaidaRec() != null) 
			filtros += "Tipo Relatório: " + this.getTipoRelatorioEntradaSaidaRec().getDescricao() + ", ";

		if(this.getTipoProcesso() != null && !TipoEntradaRelatorio.TODAS.equals(this.getTipoProcesso()))
			filtros += "Tipo Entrada: " + this.getTipoProcesso().getDescricao() + ", ";
		
		if(this.getTipoDesligamento() != null)
			filtros += "Tipo Desligamento: " + this.getTipoDesligamento().getDescricao() + ", ";
		
		if(this.getDataInicial() != null && this.getDataFinal() != null)
			filtros += "Período: " + sdf.format(this.getDataInicial()) + " a " + sdf.format(this.getDataFinal()) + ", ";

		if(this.getRegime() != null)
			filtros += "Regime: " + this.getRegime().getDescricao() + ", ";
		
		if(this.getCondicaoAtual() != null && !CondicaoRelatorio.TODAS.equals(this.getCondicaoAtual()))
			filtros += "Condição atual: " + this.getCondicaoAtual().getDescricao() + ", ";
		
		if(!"".equals(filtros))
			return "Filtro utilizado: " + filtros;
		
		return null;
	}


	public TipoEntradaRelatorio getTipoProcesso() {
		return tipoProcesso;
	}


	public void setTipoProcesso(TipoEntradaRelatorio tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
	}

}
