package br.gov.mt.sejudh.sigepen.dao.filter;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;

public class FiltroGerarPedido extends FiltroConsulta {

	private static final long serialVersionUID = 5302934507717899462L;
		
	private Date dataInicio;
	private TipoRefeicao tipoRefeicao;
	private TipoVisita tipoVisita;
	
	@Override
	public void configurarFiltros() {
	}
	
	public String getDataFormadata() {
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        return formatador.format(this.dataInicio);
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public TipoRefeicao getTipoRefeicao() {
		return tipoRefeicao;
	}

	public void setTipoRefeicao(TipoRefeicao tipoRefeicao) {
		this.tipoRefeicao = tipoRefeicao;
	}

	public TipoVisita getTipoVisita() {
		return tipoVisita;
	}

	public void setTipoVisita(TipoVisita tipoVisita) {
		this.tipoVisita = tipoVisita;
	}

}
