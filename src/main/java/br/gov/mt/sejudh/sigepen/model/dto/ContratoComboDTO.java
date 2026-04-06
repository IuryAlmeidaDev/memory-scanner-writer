package br.gov.mt.sejudh.sigepen.model.dto;

import java.io.Serializable;

public class ContratoComboDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String numContrato;

	public ContratoComboDTO(Long id, String numContrato) {
		this.id = id;
		this.numContrato = numContrato;
	}

	public Long getId() {
		return id;
	}

	public String getNumContrato() {
		return numContrato;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ContratoComboDTO that = (ContratoComboDTO) o;
		return id != null && id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		return numContrato;
	}
}