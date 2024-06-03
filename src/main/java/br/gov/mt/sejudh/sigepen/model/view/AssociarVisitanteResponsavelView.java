package br.gov.mt.sejudh.sigepen.model.view;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
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

	@Enumerated(EnumType.STRING)
	@Column(name = "FLAG_EMANCIPADO")
	private SimNao emancipado;

	@OneToMany(orphanRemoval = true, mappedBy = "visitante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Credenciamento> credenciamentos;

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

	public SimNao getEmancipado() {
		return emancipado;
	}

	public void setEmancipado(SimNao emancipado) {
		this.emancipado = emancipado;
	}

	public List<Credenciamento> getCredenciamentos() {
		return credenciamentos;
	}

	public void setCredenciamentos(List<Credenciamento> credenciamentos) {
		this.credenciamentos = credenciamentos;
	}
}
