package br.gov.mt.sejudh.sigepen.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.conversores.EnumCustomConverter;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_CREDENCIAMENTO_FASE", schema = Sigepen.SEJUDH_SCHEMA)
public class FaseDoCredenciamento extends BaseModel<Long> {

	private static final long serialVersionUID = 2774703420405320123L;

	@Id
    @SequenceGenerator(name = "SIGEPEN_S_VISITANTE_CREDENCIAL", sequenceName = "SIGEPEN_S_VISITANTE_CREDENCIAL",
            allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_VISITANTE_CREDENCIAL")
    @Column(name = "ID_FASE_CREDENCIAMENTO")
    private Long id;

    @Column(name = "OBSERVACAO_CREDENCIAL")
    private String observacao;

    @Convert(converter = EnumCustomConverter.class, disableConversion = true)
    @Type(type = "br.gov.mt.sejudh.sigepen.conversores.EnumCustomType")
    @Column(name = "CODG_SITUACAO_CREDENCIAL")
    private StatusCredenciamento statusCredenciamento;

    @Convert(converter = EnumCustomConverter.class, disableConversion = true)
    @Type(type = "br.gov.mt.sejudh.sigepen.conversores.EnumCustomType")
    @Column(name = "CODG_FASE_CREDENCIAL")
    private FaseCredenciamento faseCredenciamento;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_HORA_RECEBIMENTO")
    private Date dataRecebimento;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_HORA_APROVACAO")
    private Date dataAprovacao;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_DESPACHO")
    private Date dataDespacho;

    @ManyToOne
    @JoinColumn(name = "ID_CREDENCIAMENTO")
    private Credenciamento credenciamento;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO_APROVACAO")
    private Usuario usuarioAprovacao;

    @OneToMany(orphanRemoval = true, mappedBy = "faseDoCredenciamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CredencialDocumento> credencialDocumentos;

    @OneToMany(orphanRemoval = true, mappedBy = "faseDoCredenciamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CredencialCertidao> credencialCertidaos;

    @Transient
    private Date dataMinima;

    public FaseDoCredenciamento() {
    }

    public FaseDoCredenciamento(FaseCredenciamento faseCredenciamento) {
        this.faseCredenciamento = faseCredenciamento;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = Sigepen.formatarEspacos(observacao);
    }

    public StatusCredenciamento getStatusCredenciamento() {
        return statusCredenciamento;
    }

    public void setStatusCredenciamento(StatusCredenciamento statusCredenciamento) {
        this.statusCredenciamento = statusCredenciamento;
    }

    public FaseCredenciamento getFaseCredenciamento() {
        return faseCredenciamento;
    }

    public void setFaseCredenciamento(FaseCredenciamento faseCredenciamento) {
        this.faseCredenciamento = faseCredenciamento;
    }

    public Date getDataRecebimento() {
        return dataRecebimento;
    }

    public void setDataRecebimento(Date dataRecebimento) {
        this.dataRecebimento = dataRecebimento;
    }

    public Date getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public Date getDataDespacho() {
        return dataDespacho;
    }

    public void setDataDespacho(Date dataDespacho) {
        this.dataDespacho = dataDespacho;
    }

    public Credenciamento getCredenciamento() {
        return credenciamento;
    }

	public void setCredenciamento(Credenciamento credenciamento) {
        this.credenciamento = credenciamento;
    }

    public Usuario getUsuarioAprovacao() {
        return usuarioAprovacao;
    }

    public void setUsuarioAprovacao(Usuario usuarioAprovacao) {
        this.usuarioAprovacao = usuarioAprovacao;
    }

    public Date getDataMinima() {
        return dataMinima;
    }

    public void setDataMinima(Date dataMinima) {
        this.dataMinima = dataMinima;
    }

	public List<CredencialDocumento> getCredencialDocumentos() {
		return credencialDocumentos;
	}

	public void setCredencialDocumentos(List<CredencialDocumento> credencialDocumentos) {
		this.credencialDocumentos = credencialDocumentos;
	}

	public List<CredencialCertidao> getCredencialCertidaos() {
		return credencialCertidaos;
	}

	public void setCredencialCertidaos(List<CredencialCertidao> credencialCertidaos) {
		this.credencialCertidaos = credencialCertidaos;
	}
	
	  @Override
		public String toString() {
			return "FaseDoCredenciamento [id=" + id + ", faseCredenciamento=" + faseCredenciamento
					+ ", credenciamento=" + credenciamento + "]";
		}
    
}
