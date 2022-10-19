package br.gov.mt.sejudh.sigepen.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_IMPEDIMENTO", schema = Sigepen.SEJUDH_SCHEMA)
public class Impedimento extends BaseModel<Long> {

	private static final long serialVersionUID = -440562587270273389L;

	@Id
    @SequenceGenerator(name = "SIGEPEN_S_IMPEDIMENTO", sequenceName = "SIGEPEN_S_IMPEDIMENTO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_IMPEDIMENTO")
    @Column(name = "ID_IMPEDIMENTO")
    private Long id;

    @Column(name = "OBSERVACAO_IMPEDIMENTO", length = 4000)
    private String observacao;
    
    @Column(name="NOME_DOCUMENTO", length = 250)
	private String nomeDocumento;

    
	@Column(name="NUMERO_DOCUMENTO", length = 250)
	private String numeroDocumento;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_IMPEDIMENTO")
    private Date dataImpedimento;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_FIM_IMPEDIMENTO")
    private Date fimImpedimento;
    
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_IMPEDIMENTO")
    private TipoImpedimento tipoImpedimento;

    @ManyToOne
    @JoinColumn(name = "ID_VISITANTE")
    private Visitante visitante;

    @Enumerated(EnumType.STRING)
    @Column(name = "STAT_IMPEDIMENTO")
    private SgpStatus status;
    
    public Impedimento() {
    	
    }

    public Impedimento(Long id) {
		super();
		this.id = id;
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
        this.observacao =  Sigepen.formatarEspacos(observacao);
    }

    public Date getDataImpedimento() {
        return dataImpedimento;
    }

    public void setDataImpedimento(Date dataImpedimento) {
        this.dataImpedimento = dataImpedimento;
    }

    public Date getFimImpedimento() {
        return fimImpedimento;
    }

    public void setFimImpedimento(Date fimImpedimento) {
        this.fimImpedimento = fimImpedimento;
    }

    public TipoImpedimento getTipoImpedimento() {
        return tipoImpedimento;
    }

    public void setTipoImpedimento(TipoImpedimento tipoImpedimento) {
        this.tipoImpedimento = tipoImpedimento;
    }

    public Visitante getVisitante() {
        return visitante;
    }

    public void setVisitante(Visitante visitante) {
        this.visitante = visitante;
    }

    public SgpStatus getStatus() {
        return status;
    }

    public void setStatus(SgpStatus status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Impedimento [id=" + id + "]";
    }
    
    public String getNomeDocumento() {
		return nomeDocumento;
	}

	public void setNomeDocumento(String nomeDocumento) {
		this.nomeDocumento = nomeDocumento;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}


}
