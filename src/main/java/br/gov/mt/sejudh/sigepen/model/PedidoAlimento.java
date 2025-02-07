package br.gov.mt.sejudh.sigepen.model;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Audited
@Entity
@Table(name = "PEDIDO_ALIMENTO", schema = Sigepen.SEJUDH_SCHEMA)
public class PedidoAlimento extends BaseModel<Long> {

    private static final long serialVersionUID = 123456789L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PEDIDO_ALIMENTO_GENERATOR")
    @SequenceGenerator(
            name = "SEQ_PEDIDO_ALIMENTO_GENERATOR",
            sequenceName = "SEQ_PEDIDO_ALIMENTO",
            allocationSize = 1,
            schema = Sigepen.SEJUDH_SCHEMA
    )
    @Column(name = "ID")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_EMISSAO", nullable = false)
    private Date dataEmissao;

    @Column(name = "USUARIO", nullable = false, length = 100)
    private String usuario;

    @Lob
    @Column(name = "JSON_CONTEUDO", nullable = false)
    private String jsonConteudo;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATUS", nullable = false)
    private Status status;

    public PedidoAlimento() {
        this.status = Status.ATIVO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(final Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(final String usuario) {
        this.usuario = Sigepen.formatarEspacos(usuario);
    }

    public String getJsonConteudo() {
        return jsonConteudo;
    }

    public void setJsonConteudo(final String jsonConteudo) {
        this.jsonConteudo = jsonConteudo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        PedidoAlimento other = (PedidoAlimento) obj;
        if (id == null) {
            return other.id == null;
        } else return id.equals(other.id);
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("dataEmissao", dataEmissao)
                .append("usuario", usuario)
                .append("jsonConteudo", jsonConteudo)
                .append("status", status)
                .toString();
    }
}