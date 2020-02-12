package br.gov.mt.sejudh.sigepen.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "AUTORIZACAO_CADASTRO_BIO", schema = Sigepen.SEJUDH_SCHEMA)
public class AutorizacaoCadastroSemBiometria {

	@SequenceGenerator(name = "SEQ_AUT_CAD_BIO_GENERATOR", sequenceName = "SEQ_AUT_CAD_BIO", initialValue = 1,  allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_AUT_CAD_BIO_GENERATOR")
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
}
