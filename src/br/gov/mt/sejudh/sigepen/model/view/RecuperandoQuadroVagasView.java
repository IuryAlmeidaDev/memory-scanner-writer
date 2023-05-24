package br.gov.mt.sejudh.sigepen.model.view;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Immutable
@Table(name = "LISTAGEM_RECUPERANDOS", schema = Sigepen.SEJUDH_SCHEMA)
public class RecuperandoQuadroVagasView {

	@Id
	@Column(name = "CONDENADO", insertable = false, updatable = false, nullable = false)
	Long QuantidadeCondenado;
    
    @Column(name = "PROVISORIO", insertable = false, updatable = false, nullable = false)
	Long QuantidadeProvisorios;
	
	@Column(name = "TOTAL_RECUPERANDO")
	Long totalRecuperando;

	public RecuperandoQuadroVagasView() {

	}

	public Long getQuantidadeCondenado() {
		return QuantidadeCondenado;
	}

	public void setQuantidadeCondenado(Long quantidadeCondenado) {
		QuantidadeCondenado = quantidadeCondenado;
	}

	public Long getQuantidadeProvisorios() {
		return QuantidadeProvisorios;
	}

	public void setQuantidadeProvisorios(Long quantidadeProvisorios) {
		QuantidadeProvisorios = quantidadeProvisorios;
	}

	public Long getTotalRecuperando() {
		return totalRecuperando;
	}

	public void setTotalRecuperando(Long totalRecuperando) {
		this.totalRecuperando = totalRecuperando;
	}

}
