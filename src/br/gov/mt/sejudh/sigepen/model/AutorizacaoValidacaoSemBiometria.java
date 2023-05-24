package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.enumerator.TipoAutorizacaoValidacaoSemBio;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited 
@Entity
@Table(name = "AUT_VALIDACAO_SEM_BIO", schema = Sigepen.SEJUDH_SCHEMA)
public class AutorizacaoValidacaoSemBiometria implements Serializable {

	private static final long serialVersionUID = 1L;

	@SequenceGenerator(name = "SEQ_AUT_VALID_SEM_BIO_GENERATOR", sequenceName = "SEQ_AUT_VALID_SEM_BIO", initialValue = 1,  allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_AUT_VALID_SEM_BIO_GENERATOR")
	@Column(name = "ID")
	private Long id;

	@Column(name = "DATA_AUTORIZACAO", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataAutorizacao;
	
	@Column(name = "OBSERVACAO", length = 250)
	private String observacao;
	
	@OneToOne
	@JoinColumn(name = "USUARIO_ID")
	private Usuario usuario;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "PASSAGEM_ID")
	private Passagem passagem;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "ENTRADA_SAIDA_ID")
	private EntradaSaida entradaSaida;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO")
	private TipoAutorizacaoValidacaoSemBio tipoAutorizacaoValidacaoSemBio;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataAutorizacao() {
		return dataAutorizacao;
	}

	public void setDataAutorizacao(Date dataAutorizacao) {
		this.dataAutorizacao = dataAutorizacao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Passagem getPassagem() {
		return passagem;
	}

	public void setPassagem(Passagem passagem) {
		this.passagem = passagem;
	}

	public EntradaSaida getEntradaSaida() {
		return entradaSaida;
	}

	public void setEntradaSaida(EntradaSaida entradaSaida) {
		this.entradaSaida = entradaSaida;
	}

	public TipoAutorizacaoValidacaoSemBio getTipoAutorizacaoValidacaoSemBio() {
		return tipoAutorizacaoValidacaoSemBio;
	}

	public void setTipoAutorizacaoValidacaoSemBio(TipoAutorizacaoValidacaoSemBio tipoAutorizacaoValidacaoSemBio) {
		this.tipoAutorizacaoValidacaoSemBio = tipoAutorizacaoValidacaoSemBio;
	}
	
	public static AutorizacaoValidacaoSemBiometria getAutorizacaoPorTipo(List<AutorizacaoValidacaoSemBiometria> lista, TipoAutorizacaoValidacaoSemBio tipo) {
		return lista.stream().filter(a -> a.getTipoAutorizacaoValidacaoSemBio().equals(tipo)).findFirst().get();
	}
}
