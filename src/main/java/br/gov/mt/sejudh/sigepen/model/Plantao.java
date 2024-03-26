package br.gov.mt.sejudh.sigepen.model;

import java.util.Date;

import br.gov.mt.sejudh.sigepen.enumerator.TipoPlantao;

public class Plantao {

	private TipoPlantao plantao;
	private Date dataPlantao;
	
	public Plantao(TipoPlantao plantao, Date dataPlantao) {
		this.plantao = plantao;
		this.dataPlantao = dataPlantao;
	}

	public TipoPlantao getPlantao() {
		return plantao;
	}

	public void setPlantao(TipoPlantao plantao) {
		this.plantao = plantao;
	}

	public Date getDataPlantao() {
		return dataPlantao;
	}

	public void setDataPlantao(Date dataPlantao) {
		this.dataPlantao = dataPlantao;
	}
}
