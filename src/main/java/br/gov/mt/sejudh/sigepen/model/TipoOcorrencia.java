package br.gov.mt.sejudh.sigepen.model;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Audited
@Entity
@Table(name = "SIGEPEN_TIPO_OCORRENCIA", schema = Sigepen.SEJUDH_SCHEMA)
public class TipoOcorrencia extends BaseModel<Long> {

    private static final long serialVersionUID = 123456789L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_TIPO_OCORRENCIA_GEN")
    @SequenceGenerator(
            name = "SIGEPEN_S_TIPO_OCORRENCIA_GEN",
            sequenceName = "SIGEPEN_S_TIPO_OCORRENCIA",
            allocationSize = 1,
            schema = Sigepen.SEJUDH_SCHEMA
    )
    @Column(name = "ID_TIPO_OCORRENCIA")
    private Long id;

    @Column(name = "DESCRICAO", nullable = false)
    private String descricao;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "tipoOcorrencia", orphanRemoval = true)
    private List<Ocorrencia> ocorrencias = new ArrayList<>();
    
    @Column(name = "TIPO")
    private String tipo;

    @Enumerated
	@Column(name = "STATUS")
	private Status status;
    
    @Column(name = "CLASSIFICACAO")
    private String classificacao;

    @Transient
	private AutoriaRegistro autorRegistro;

	@Transient
	private AutoriaRegistro autorUltimaAlteracao;

    public TipoOcorrencia() {}

    public TipoOcorrencia(Status status) {
    	this.setStatus(status);
    }

    public TipoOcorrencia(Long id) {
        this.id = id;
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

    public List<Ocorrencia> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(List<Ocorrencia> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }
    
    public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}	

	public String getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}

	public AutoriaRegistro getAutorRegistro() {
		return autorRegistro;
	}

	public void setAutorRegistro(AutoriaRegistro autorRegistro) {
		this.autorRegistro = autorRegistro;
	}

	public AutoriaRegistro getAutorUltimaAlteracao() {
		return autorUltimaAlteracao;
	}

	public void setAutorUltimaAlteracao(AutoriaRegistro autorUltimaAlteracao) {
		this.autorUltimaAlteracao = autorUltimaAlteracao;
	}


    @Override
    public String toString() {
        return "TipoOcorrencia{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TipoOcorrencia that = (TipoOcorrencia) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(id);
        return result;
    }
}