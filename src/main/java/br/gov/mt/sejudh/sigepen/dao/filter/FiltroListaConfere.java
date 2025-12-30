package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.Date;

import br.gov.mt.sejudh.sigepen.enumerator.TipoPlantao;

public class FiltroListaConfere {

	private Date dataInicial;
	private Date dataFinal;
	private TipoPlantao tipoPlantaoInicial;
	
	public Date getDataInicial() {
		return dataInicial;
	}
	
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}
	
	public Date getDataFinal() {
		return dataFinal;
	}
	
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	
	public TipoPlantao getTipoPlantaoInicial() {
		return tipoPlantaoInicial;
	}

	public void setTipoPlantaoInicial(TipoPlantao tipoPlantaoInicial) {
		this.tipoPlantaoInicial = tipoPlantaoInicial;
	}
}
