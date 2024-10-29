package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mt.sejudh.sigepen.util.Sigepen;


@Entity
@Table(name = "LOG_ACESSO", schema= Sigepen.SEJUDH_SCHEMA)
public class LogAcesso implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "SEQ_LOG_ACESSO_GENERATOR", sequenceName = "SEQ_LOG_ACESSO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_LOG_ACESSO_GENERATOR")
	@Column(name = "CODG_LOG_ACESSO")
	private Integer idLogAcesso;

	@Column(name = "LOGIN", length = 50, nullable = false)
	private String login;

	@Column(name = "TIPO_LOG_ACESSO", length = 50, nullable = false)
	private String tipoLogAcesso;

	@Column(name = "TIPO_BLOQUEIO", length = 50, nullable = false)
	private String tipoBloqueio;

	@Column(name = "IP", length = 15, nullable = false)
	private String ip;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_HORA_ACESSO", nullable = false)
	private Date dataHoraAcesso;

	@ManyToOne
	@JoinColumn(name = "ID_USUARIO", nullable = true)
	private Usuario usuario;

	public Integer getIdLogAcesso() {
		return idLogAcesso;
	}

	public void setIdLogAcesso(Integer idLogAcesso) {
		this.idLogAcesso = idLogAcesso;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Date getDataHoraAcesso() {
		return dataHoraAcesso;
	}

	public void setDataHoraAcesso(Date dataHoraAcesso) {
		this.dataHoraAcesso = dataHoraAcesso;
	}

	public String getTipoLogAcesso() {
		return tipoLogAcesso;
	}

	public void setTipoLogAcesso(String tipoLogAcesso) {
		this.tipoLogAcesso = tipoLogAcesso;
	}

	public String getTipoBloqueio() {
		return tipoBloqueio;
	}

	public void setTipoBloqueio(String tipoBloqueio) {
		this.tipoBloqueio = tipoBloqueio;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
