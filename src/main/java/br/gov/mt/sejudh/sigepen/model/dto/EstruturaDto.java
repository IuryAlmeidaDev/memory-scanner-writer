package br.gov.mt.sejudh.sigepen.model.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

@Immutable
@Entity
public class EstruturaDto {
	
	@Column(name = "NIVEL")
	Long nivel;

	@Id
	@Column(name = "ID")
	Long id;

	@Column(name = "ESTRUTURAPAI_ID")
	Long idEstruturaPai;

	@Column(name = "NOME")
    String estruturaNome;

	@Column(name = "CELA")
	Boolean cela;
	
	@Transient
	Boolean temEstruturaFilho;
	
	@Transient
    private int capacidadeTotal;

	public EstruturaDto() {
		
	}
	
	public Long getNivel() {
		return nivel;
	}

	public void setNivel(Long nivel) {
		this.nivel = nivel;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdEstruturaPai() {
		return idEstruturaPai;
	}

	public void setIdEstruturaPai(Long idEstruturaPai) {
		this.idEstruturaPai = idEstruturaPai;
	}

	public String getEstruturaNome() {
		return estruturaNome;
	}

	public void setEstruturaNome(String estruturaNome) {
		this.estruturaNome = estruturaNome;
	}

	public Boolean getCela() {
		return cela;
	}

	public void setCela(Boolean cela) {
		this.cela = cela;
	}

	public Boolean getTemEstruturaFilho() {
		return temEstruturaFilho;
	}

	public void setTemEstruturaFilho(Boolean temEstruturaFilho) {
		this.temEstruturaFilho = temEstruturaFilho;
	}

	public int getCapacidadeTotal() {
		return capacidadeTotal;
	}

	public void setCapacidadeTotal(int capacidadeTotal) {
		this.capacidadeTotal = capacidadeTotal;
	}
}
