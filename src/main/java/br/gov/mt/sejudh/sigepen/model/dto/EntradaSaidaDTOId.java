package br.gov.mt.sejudh.sigepen.model.dto;

import java.io.Serializable;
import java.util.Objects;

public class EntradaSaidaDTOId implements Serializable {

	private static final long serialVersionUID = 1L;
	 
    private Long id;
    private Long idCredenciamento;

    public EntradaSaidaDTOId() {}

    public EntradaSaidaDTOId(Long id, Long idCredenciamento) {
        this.id = id;
        this.idCredenciamento = idCredenciamento;
    }
    
    

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdCredenciamento() {
		return idCredenciamento;
	}

	public void setIdCredenciamento(Long idCredenciamento) {
		this.idCredenciamento = idCredenciamento;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntradaSaidaDTOId)) return false;
        EntradaSaidaDTOId that = (EntradaSaidaDTOId) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(idCredenciamento, that.idCredenciamento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idCredenciamento);
    }
}