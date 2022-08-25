package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Immutable
@Table(name="CADASTRO_RECUPERANDO", schema=Sigepen.SEJUDH_SCHEMA)
public class SituacaoCadastroRecuperando implements Serializable {

	private static final long serialVersionUID = -4191186100920382978L;

	@Id
	@Column(name = "id", updatable = false, nullable = false)
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
	
	@Column(name="CADASTRO_COMPLETO")
	private boolean cadastroCompleto;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ID")
	private RecuperandoView recuperando;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean getTemTatuagem() {
		return temTatuagem;
	}

	public void setTemTatuagem(boolean temTatuagem) {
		this.temTatuagem = temTatuagem;
	}

	public boolean getTemFotoTatuagem() {
		return temFotoTatuagem;
	}

	public void setTemFotoTatuagem(boolean temFotoTatuagem) {
		this.temFotoTatuagem = temFotoTatuagem;
	}

	public boolean getTemFotoFrente() {
		return temFotoFrente;
	}

	public void setTemFotoFrente(boolean temFotoFrente) {
		this.temFotoFrente = temFotoFrente;
	}

	public boolean getTemFotoPerfil() {
		return temFotoPerfil;
	}

	public void setTemFotoPerfil(boolean temFotoPerfil) {
		this.temFotoPerfil = temFotoPerfil;
	}

	public boolean getTemFotoCorpoInteiro() {
		return temFotoCorpoInteiro;
	}

	public void setTemFotoCorpoInteiro(boolean temFotoCorpoInteiro) {
		this.temFotoCorpoInteiro = temFotoCorpoInteiro;
	}

	public boolean getVidaSocialCompleta() {
		return vidaSocialCompleta;
	}

	public void setVidaSocialCompleta(boolean vidaSocialCompleta) {
		this.vidaSocialCompleta = vidaSocialCompleta;
	}

	public boolean getTemParente() {
		return temParente;
	}

	public void setTemParente(boolean temParente) {
		this.temParente = temParente;
	}

	public boolean getCadastroCompleto() {
		return cadastroCompleto;
	}

	public void setCadastroCompleto(boolean cadastroCompleto) {
		this.cadastroCompleto = cadastroCompleto;
	}

	public RecuperandoView getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(RecuperandoView recuperando) {
		this.recuperando = recuperando;
	}
}
