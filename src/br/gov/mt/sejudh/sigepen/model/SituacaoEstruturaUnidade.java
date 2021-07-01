package br.gov.mt.sejudh.sigepen.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.enumerator.TipoInterdicao;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name="SITUACAO_ESTRUTURA_UNIDADE", schema = Sigepen.SEJUDH_SCHEMA)
public class SituacaoEstruturaUnidade {
	@SequenceGenerator(name = "SEQ_SITUACAO_ESTRUTURA_UNIDADE_GENERATOR", sequenceName = "SEQ_SITUACAO_ESTRUTURA_UNIDADE", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SITUACAO_ESTRUTURA_UNIDADE_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private Long versao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_REGISTRO" , nullable = false)
	private Date dataRegistro;

	@Column(name = "MOTIVO")
	private String motivo;

	@ManyToOne(fetch = FetchType.LAZY)
	private UnidadePrisional unidadePrisional;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Estrutura estrutura;
	
	@Column(name = "LOGIN_USUARIO")
	private String loginUsuario;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "SITUACAO")
	private SituacaoUnidade situacao;

	@Enumerated
	@Column(name = "TIPO_INTERDICAO")
	private TipoInterdicao tipoInterdicao;
	
	@Column(name = "ORIGEM_ALTERACAO")
	private boolean origemAlteracao;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDataRegistro() {
		return dataRegistro;
	}

	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = Sigepen.formatarEspacos(motivo);
	}

	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}

	public SituacaoUnidade getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoUnidade situacao) {
		this.situacao = situacao;
	}

	public TipoInterdicao getTipoInterdicao() {
		return tipoInterdicao;
	}

	public void setTipoInterdicao(TipoInterdicao tipoInterdicao) {
		this.tipoInterdicao = tipoInterdicao;
	}

	public Estrutura getEstrutura() {
		return estrutura;
	}

	public void setEstrutura(Estrutura estrutura) {
		this.estrutura = estrutura;
	}

	public boolean isOrigemAlteracao() {
		return origemAlteracao;
	}

	public void setOrigemAlteracao(boolean origemAlteracao) {
		this.origemAlteracao = origemAlteracao;
	}

	public String getLoginUsuario() {
		return loginUsuario;
	}

	public void setLoginUsuario(String loginUsuario) {
		this.loginUsuario = Sigepen.formatarEspacos(loginUsuario);
	}

	@Override
	public String toString() {
		return "Situação da Estrutura [id=" + id + ", estrutura="
				+ estrutura + ", situacao=" + situacao + "]";
	}

}
