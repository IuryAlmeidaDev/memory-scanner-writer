package br.gov.mt.sejudh.sigepen.model;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.StatusOcorrencia;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Audited
@Entity
@Table(name = "SIGEPEN_OCORRENCIA", schema = Sigepen.SEJUDH_SCHEMA)
public class Ocorrencia extends BaseModel<Long> {

    private static final long serialVersionUID = 123456789L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_SIGEPEN_OCORRENCIA_GEN")
    @SequenceGenerator(
            name = "SIGEPEN_S_SIGEPEN_OCORRENCIA_GEN",
            sequenceName = "SIGEPEN_S_SIGEPEN_OCORRENCIA",
            allocationSize = 1,
            schema = Sigepen.SEJUDH_SCHEMA
    )
    @Column(name = "ID_OCORRENCIA")
    private Long id;

    @Column(name = "DESCRICAO", nullable = false)
    private String descricao;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_OCORRENCIA", nullable = false)
    private Date dataOcorrencia = new Date();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_TIPO_OCORRENCIA")
    private TipoOcorrencia tipoOcorrencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SGP_VISITANTE")
    private Visitante visitante;
    
    @Column(name = "RESUMO")
    private String resumo;
    
    @Column(name = "DETALHAMENTO")
    private String detalhamento;
    
    @Enumerated(EnumType.STRING)
	@Column(name = "STATUS_OCORRENCIA")
	private StatusOcorrencia statusOcorrencia = StatusOcorrencia.ANDAMENTO;

    public Ocorrencia() {}

    public Ocorrencia(Long id, String descricao, TipoOcorrencia tipoOcorrencia, Visitante visitante) {
        this.id = id;
        this.descricao = descricao;
        this.tipoOcorrencia = tipoOcorrencia;
        this.visitante = visitante;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(Date dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public TipoOcorrencia getTipoOcorrencia() {
        return tipoOcorrencia;
    }

    public void setTipoOcorrencia(TipoOcorrencia tipoOcorrencia) {
        this.tipoOcorrencia = tipoOcorrencia;
    }

    public Visitante getVisitante() {
        return visitante;
    }

    public void setVisitante(Visitante visitante) {
        this.visitante = visitante;
    }
    
    public String getResumo() {
		return resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	public String getDetalhamento() {
		return detalhamento;
	}

	public void setDetalhamento(String detalhamento) {
		this.detalhamento = detalhamento;
	}

	public StatusOcorrencia getStatusOcorrencia() {
		return statusOcorrencia;
	}

	public void setStatusOcorrencia(StatusOcorrencia statusOcorrencia) {
		this.statusOcorrencia = statusOcorrencia;
	}

	@Override
    public String toString() {
        return "Ocorrencia{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", tipoOcorrencia=" + tipoOcorrencia +
                ", visitante=" + visitante +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Ocorrencia that = (Ocorrencia) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(id);
        return result;
    }
}