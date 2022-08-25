package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.List;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroInfopen;

public class InfopenDTO {

	List<InfopenRecuperandoDTO> recuperandos;
	FiltroInfopen filtro;
	
	public List<InfopenRecuperandoDTO> getRecuperandos() {
		return recuperandos;
	}
	public void setRecuperandos(List<InfopenRecuperandoDTO> recuperandos) {
		this.recuperandos = recuperandos;
	}
	public FiltroInfopen getFiltro() {
		return filtro;
	}
	public void setFiltro(FiltroInfopen filtro) {
		this.filtro = filtro;
	}

}
