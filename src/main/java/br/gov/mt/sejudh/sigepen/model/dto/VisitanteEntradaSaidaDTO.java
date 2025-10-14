package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Acesso;
import br.gov.mt.sejudh.sigepen.model.Perfil;
import br.gov.mt.sejudh.sigepen.model.Visitante;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VisitanteEntradaSaidaDTO {

	private long id;
	private String nomeVisitante;
	private String nomeRecuperando;
	private Date dataHoraEntrada;

	public VisitanteEntradaSaidaDTO() {}

	public VisitanteEntradaSaidaDTO(Visitante visitante) {
		this.id = visitante.getId();
		this.nomeVisitante = visitante.getPessoa().getNome();
	}

	public VisitanteEntradaSaidaDTO(long id, String nomeVisitante, String nomeRecuperando, Date dataHoraEntrada) {
		this.id = id;
		this.nomeVisitante = nomeVisitante;
		this.nomeRecuperando = nomeRecuperando;
		this.dataHoraEntrada = dataHoraEntrada;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNomeVisitante() {
		return nomeVisitante;
	}

	public void setNomeVisitante(String nomeVisitante) {
		this.nomeVisitante = nomeVisitante;
	}

	public String getNomeRecuperando() {
		return nomeRecuperando;
	}

	public void setNomeRecuperando(String nomeRecuperando) {
		this.nomeRecuperando = nomeRecuperando;
	}

	public Date getDataHoraEntrada() {
		return dataHoraEntrada;
	}

	public void setDataHoraEntrada(Date dataHoraEntrada) {
		this.dataHoraEntrada = dataHoraEntrada;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		VisitanteEntradaSaidaDTO that = (VisitanteEntradaSaidaDTO) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}
}
