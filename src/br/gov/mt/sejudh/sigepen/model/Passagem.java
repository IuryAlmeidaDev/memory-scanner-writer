package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.enumerator.TipoDesligamento;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "PASSAGEM", schema = Sigepen.SEJUDH_SCHEMA)
public class Passagem implements Serializable {

	private static final long serialVersionUID = -1273557139320143420L;
	
	@SequenceGenerator(name = "SEQ_PASSAGEM_GENERATOR", sequenceName = "SEQ_PASSAGEM", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PASSAGEM_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private Long versao;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_PRISAO", nullable = false)
	private Date dataPrisao;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ENTRADA", nullable = false)
	private Date dataEntrada;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_SAIDA")
	private Date dataSaida;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_INCLUSAO_SISTEMA", nullable = false)
	private Date dataInclusaoNoSistema;
	
	@ManyToOne(optional = false, fetch=FetchType.LAZY)
	@JoinColumn(name = "RECUPERANDO_ID")
	private Recuperando recuperando;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FOTO_ID")
	private Foto foto;
	
	@OneToMany(orphanRemoval=true, mappedBy="passagem", fetch=FetchType.LAZY, cascade = {CascadeType.ALL})
	private List<PassagemLotacao> lotacao;
	
	@Transient
	private boolean isPassagemAtual;
	
	@OneToOne(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="passagem")
	private PassagemProcedencia passagemProcedencia;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_DESLIGAMENTO")
	private TipoDesligamento tipoDesligamento;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESTABELECIMENTO_ID")
	private Estabelecimento estabelecimentoDestino;
	
	@Column(name = "DOCUMENTO_DESLIGAMENTO", length = 50)
	private String documentoDesligamento;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_DOC_DESLIG")
	private Date dataDocDesligamento;
	
	@Column(name = "OBS_DESLIGAMENTO", length = 250)
	private String obsDesligamento;
	
	@Column(name = "NUMERO_DOCUMENTO", length = 80)
	private String numeroDocumento;

	public Passagem() {
		this.lotacao = new ArrayList<PassagemLotacao>();
	}
	
	public Passagem(Recuperando recuperando) {
		this.lotacao = new ArrayList<PassagemLotacao>();
		this.recuperando = recuperando;
	}
	
	public PassagemLotacao getLotacaoAtual() {
		for (PassagemLotacao l : this.lotacao) {
			if (l.getDataSaida() == null) {
				return l;
			}
		}
		
		return null;
	}
	
	public PassagemLotacao getUltimaLotacao() {
		return this.lotacao.stream().max(Comparator.comparing(PassagemLotacao::getId)).get();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDataPrisao() {
		return dataPrisao;
	}

	public void setDataPrisao(Date dataPrisao) {
		this.dataPrisao = dataPrisao;
	}

	public Date getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public Date getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}

	public Date getDataInclusaoNoSistema() {
		return dataInclusaoNoSistema;
	}

	public void setDataInclusaoNoSistema(Date dataInclusaoNoSistema) {
		this.dataInclusaoNoSistema = dataInclusaoNoSistema;
	}

	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
	}

	public boolean isPassagemAtual() {
		return isPassagemAtual;
	}

	public void setPassagemAtual(boolean isPassagemAtual) {
		this.isPassagemAtual = isPassagemAtual;
	}
	
	public List<PassagemLotacao> getLotacao() {
		return lotacao;
	}

	public void setLotacao(List<PassagemLotacao> lotacao) {
		this.lotacao = lotacao;
	}

	public PassagemProcedencia getPassagemProcedencia() {
		return passagemProcedencia;
	}

	public void setPassagemProcedencia(PassagemProcedencia passagemProcedencia) {
		this.passagemProcedencia = passagemProcedencia;
	}

	public TipoDesligamento getTipoDesligamento() {
		return tipoDesligamento;
	}

	public void setTipoDesligamento(TipoDesligamento tipoDesligamento) {
		this.tipoDesligamento = tipoDesligamento;
	}

	public Estabelecimento getEstabelecimentoDestino() {
		return estabelecimentoDestino;
	}

	public void setEstabelecimentoDestino(Estabelecimento estabelecimentoDestino) {
		this.estabelecimentoDestino = estabelecimentoDestino;
	}

	public String getDocumentoDesligamento() {
		return documentoDesligamento;
	}

	public void setDocumentoDesligamento(String documentoDesligamento) {
		this.documentoDesligamento = Sigepen.formatarEspacos(documentoDesligamento);
	}

	public Date getDataDocDesligamento() {
		return dataDocDesligamento;
	}

	public void setDataDocDesligamento(Date dataDocDesligamento) {
		this.dataDocDesligamento = dataDocDesligamento;
	}

	public String getObsDesligamento() {
		return obsDesligamento;
	}

	public void setObsDesligamento(String obsDesligamento) {
		this.obsDesligamento = Sigepen.formatarEspacos(obsDesligamento);
	}

	public Foto getFoto() {
		return foto;
	}

	public void setFoto(Foto foto) {
		this.foto = foto;
	}
	
	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataEntrada == null) ? 0 : dataEntrada.hashCode());
		result = prime
				* result
				+ ((dataInclusaoNoSistema == null) ? 0 : dataInclusaoNoSistema
						.hashCode());
		result = prime * result
				+ ((dataPrisao == null) ? 0 : dataPrisao.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (isPassagemAtual ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Passagem))
			return false;
		Passagem other = (Passagem) obj;
		if (dataEntrada == null) {
			if (other.dataEntrada != null)
				return false;
		} else if (!dataEntrada.equals(other.dataEntrada))
			return false;
		if (dataInclusaoNoSistema == null) {
			if (other.dataInclusaoNoSistema != null)
				return false;
		} else if (!dataInclusaoNoSistema.equals(other.dataInclusaoNoSistema))
			return false;
		if (dataPrisao == null) {
			if (other.dataPrisao != null)
				return false;
		} else if (!dataPrisao.equals(other.dataPrisao))
			return false;
		if (id != other.id)
			return false;
		if (isPassagemAtual != other.isPassagemAtual)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Passagem [id=" + id + "]";
	}
}
