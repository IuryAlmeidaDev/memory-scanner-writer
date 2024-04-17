package br.gov.mt.sejudh.sigepen.model.view;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.gov.mt.sejudh.sigepen.util.Sigepen;


@Entity
@Immutable
@Table(name = "LISTAGEM_ASSOCIAR_RESP", schema = Sigepen.SEJUDH_SCHEMA)
public class AssociarVisitanteResponsavelView {
	
	@Id
	@Column(name = "ID_VISITANTE", insertable = false, updatable = false, nullable = false)
	private Long visitanteId;
	
	@Column(name = "NOME_PESSOA", insertable = false, updatable = false, nullable = false)
	private String nome;
	
	@Column(name = "CPF_PESSOA", insertable = false, updatable = false, nullable = false)
	private String cpf;
	
	@Column(name = "DATA_NASCIMENTO_PESSOA", insertable = false, updatable = false, nullable = false)
	private Date dataNascimento;

	public Long getVisitanteId() {
		return visitanteId;
	}

	public void setVisitanteId(Long visitanteId) {
		this.visitanteId = visitanteId;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

}
