package br.gov.mt.sejudh.sigepen.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Table(name="SITUACAO_CADASTR_RECUP_SIMPLES", schema=Sigepen.SEJUDH_SCHEMA)
public class SituacaoCadastroRecuperandoSimples {

	@Id
	private Integer id;

	@Column(name="CADASTRO_COMPLETO")
	private boolean cadastroCompleto;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ID")
	private Recuperando recuperando;

	public boolean isCadastroCompleto() {
		return cadastroCompleto;
	}

	public void setCadastroCompleto(boolean cadastroCompleto) {
		this.cadastroCompleto = cadastroCompleto;
	}

	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
	}
}
