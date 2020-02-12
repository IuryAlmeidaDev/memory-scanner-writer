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
@Table(name="SITUACAO_CADASTRO_RECUPERANDO", schema=Sigepen.SEJUDH_SCHEMA)
public class SituacaoCadastroRecuperando {
	
	@Id
	private Integer id;

	@Column(name="TEM_TATUAGEM")
	private boolean temTatuagem;
	
	@Column(name="TEM_FOTO_TATUAGEM")
	private boolean temFotoTatuagem;
	
	@Column(name="TEM_FOTO_FRENTE")
	private boolean temFotoFrente;
	
	@Column(name="TEM_FOTO_PERFIL")
	private boolean temFotoPerfil;
	
	@Column(name="TEM_FOTO_CORPO_INTEIRO")
	private boolean temFotoCorpoInteiro;
	
	@Column(name="VIDA_SOCIAL_COMPLETA")
	private boolean vidaSocialCompleta;
	
	@Column(name="TEM_PARENTE")
	private boolean temParente;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ID")
	private Recuperando recuperando;

	public boolean getIsTemTatuagem() {
		return temTatuagem;
	}

	public void setTemTatuagem(boolean temTatuagem) {
		this.temTatuagem = temTatuagem;
	}

	public boolean getIsTemFotoTatuagem() {
		return temFotoTatuagem;
	}

	public void setTemFotoTatuagem(boolean temFotoTatuagem) {
		this.temFotoTatuagem = temFotoTatuagem;
	}

	public boolean getIsTemFotoFrente() {
		return temFotoFrente;
	}

	public void setTemFotoFrente(boolean temFotoFrente) {
		this.temFotoFrente = temFotoFrente;
	}

	public boolean getIsTemFotoPerfil() {
		return temFotoPerfil;
	}

	public void setTemFotoPerfil(boolean temFotoPerfil) {
		this.temFotoPerfil = temFotoPerfil;
	}

	public boolean getIsTemFotoCorpoInteiro() {
		return temFotoCorpoInteiro;
	}

	public void setTemFotoCorpoInteiro(boolean temFotoCorpoInteiro) {
		this.temFotoCorpoInteiro = temFotoCorpoInteiro;
	}

	public boolean getIsVidaSocialCompleta() {
		return vidaSocialCompleta;
	}

	public void setVidaSocialCompleta(boolean vidaSocialCompleta) {
		this.vidaSocialCompleta = vidaSocialCompleta;
	}

	public boolean getIsTemParente() {
		return temParente;
	}

	public void setTemParente(boolean temParente) {
		this.temParente = temParente;
	}

	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
	}
}
