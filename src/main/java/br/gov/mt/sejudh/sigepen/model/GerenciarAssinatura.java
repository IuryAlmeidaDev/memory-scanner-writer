package br.gov.mt.sejudh.sigepen.model;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Audited
@Entity
@Table(name = "GERENCIAR_ASSINATURA", schema = Sigepen.SEJUDH_SCHEMA)
public class GerenciarAssinatura extends BaseModel<Long> {

    private static final long serialVersionUID = 123456789L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_GERENCIAR_ASSINATURA_GEN")
    @SequenceGenerator(
            name = "SIGEPEN_S_GERENCIAR_ASSINATURA_GEN",
            sequenceName = "SIGEPEN_S_GERENCIAR_ASSINATURA",
            allocationSize = 1,
            schema = Sigepen.SEJUDH_SCHEMA
    )
    @Column(name = "ID_GERENCIAR_ASSINATURA")
    private Long id;

    @Column(name = "DESCRICAO", nullable = false)
    private String descricao;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "RELATORIO", nullable = false)
    private Relatorio relatorio;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATUS", nullable = false)
    private Status status;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_UNIDADE_PRISIONAL")
    private UnidadePrisional unidadePrisional;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "gerenciarAssinatura", orphanRemoval = true)
    private List<GerenciarAssinaturaServidor> gerenciarAssinaturaServidores = new ArrayList<GerenciarAssinaturaServidor>();

    public GerenciarAssinatura() {}

    public GerenciarAssinatura(Long id, String descricao, Relatorio relatorio, Status status,UnidadePrisional unidadePrisional) {
        this.id = id;
        this.descricao = descricao;
        this.relatorio = relatorio;
        this.status = status;
        this.unidadePrisional = unidadePrisional;
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

    public Relatorio getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(Relatorio relatorio) {
        this.relatorio = relatorio;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public UnidadePrisional getUnidadePrisional() {
        return unidadePrisional;
    }

    public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
        this.unidadePrisional = unidadePrisional;
    }

    public List<GerenciarAssinaturaServidor> getGerenciarAssinaturaServidores() {
        return gerenciarAssinaturaServidores;
    }

    public void setGerenciarAssinaturaServidores(List<GerenciarAssinaturaServidor> gerenciarAssinaturaServidores) {
        this.gerenciarAssinaturaServidores = gerenciarAssinaturaServidores;
    }

    @Override
    public String toString() {
        return "GerenciarAssinatura{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", relatorio='" + relatorio + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GerenciarAssinatura that = (GerenciarAssinatura) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(id);
        return result;
    }
}