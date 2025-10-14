package br.gov.mt.sejudh.sigepen.model;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Objects;

@Audited
@Entity
@Table(name = "GERENCIAR_ASSINATURA_SERV", schema = Sigepen.SEJUDH_SCHEMA)
public class GerenciarAssinaturaServidor extends BaseModel<Long> {

    private static final long serialVersionUID = 123456789L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_GERENC_ASS_SERVIDOR_GEN")
    @SequenceGenerator(
            name = "SIGEPEN_S_GERENC_ASS_SERVIDOR_GEN",
            sequenceName = "SIGEPEN_S_GERENC_ASS_SERVIDOR",
            allocationSize = 1,
            schema = Sigepen.SEJUDH_SCHEMA
    )
    @Column(name = "ID_GERENCIAR_ASSINATURA_SERV")
    private Long id;

    @Column(name = "ORDEM", nullable = false)
    private String ordem;

    @ManyToOne
    @JoinColumn(name = "ID_GERENCIAR_ASSINATURA")
    private GerenciarAssinatura gerenciarAssinatura;

    @ManyToOne
    @JoinColumn(name = "ID_SERVIDOR")
    private Servidor servidor;

    public GerenciarAssinaturaServidor() {
    }

    public GerenciarAssinaturaServidor(Long id, String ordem, GerenciarAssinatura gerenciarAssinatura, Servidor servidor) {
        this.id = id;
        this.ordem = ordem;
        this.gerenciarAssinatura = gerenciarAssinatura;
        this.servidor = servidor;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrdem() {
        return ordem;
    }

    public void setOrdem(String order) {
        this.ordem = order;
    }

    public GerenciarAssinatura getGerenciarAssinatura() {
        return gerenciarAssinatura;
    }

    public void setGerenciarAssinatura(GerenciarAssinatura gerenciarAssinatura) {
        this.gerenciarAssinatura = gerenciarAssinatura;
    }

    public Servidor getServidor() {
        return servidor;
    }

    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GerenciarAssinaturaServidor that = (GerenciarAssinaturaServidor) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(id);
        return result;
    }

    @Override
    public String toString() {
        return "GerenciarAssinaturaServidor{" +
                "id=" + id +
                ", order='" + ordem + '\'' +
                '}';
    }
}