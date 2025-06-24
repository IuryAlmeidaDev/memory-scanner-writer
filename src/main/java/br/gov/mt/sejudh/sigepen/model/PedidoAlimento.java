package br.gov.mt.sejudh.sigepen.model;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Audited
@Entity
@Table(name = "SIGEPEN_PEDIDO_ALIMENTO", schema = Sigepen.SEJUDH_SCHEMA)
public class PedidoAlimento extends BaseModel<Long> {

    private static final long serialVersionUID = 123456789L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_PEDIDO_ALIMENTO_GEN")
    @SequenceGenerator(
            name = "SIGEPEN_S_PEDIDO_ALIMENTO_GEN",
            sequenceName = "SIGEPEN_S_PEDIDO_ALIMENTO_GEN",
            allocationSize = 1,
            schema = Sigepen.SEJUDH_SCHEMA
    )
    @Column(name = "ID_PEDIDO_ALIMENTO")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_EMISSAO_PEDIDO", nullable = false)
    private Date dataEmissao;

    @Column(name = "DATA_PEDIDO", nullable = false)
    private String dataPedido;

    @Column(name = "ID_USUARIO", nullable = false)
    private Long idUsuario;

    @Column(name = "ID_UNIDADE_PRISIONAL", nullable = false)
    private Long idUnidadePrisional;

    @Column(name = "ID_CONTRATO", nullable = false)
    private Long idContrato;

    @Column(name = "ID_TIPO_REFEICAO", nullable = false)
    private Long idTipoRefeicao;

    @OneToMany(mappedBy = "pedidoAlimento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedidoAlimento> itensPedido = new ArrayList<>();

    @OneToMany(mappedBy = "pedidoAlimento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietaEspecialPedidoAlimento> dietasEspeciais = new ArrayList<>();

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STAT_PEDIDO", nullable = false)
    private Status status;

    private PedidoAlimento(String dataPedido, Long idUsuario, Long idUnidadePrisional, Long idContrato, Long idTipoRefeicao) {
        this.dataEmissao = new Date();
        this.dataPedido = dataPedido;
        this.idUsuario = idUsuario;
        this.idUnidadePrisional = idUnidadePrisional;
        this.idContrato = idContrato;
        this.idTipoRefeicao = idTipoRefeicao;
        this.status = Status.ATIVO;
    }

    public PedidoAlimento() {}

    public static PedidoAlimento create(String dataPedido, Long idUsuario, Long idUnidadePrisional, Long idContrato, Long idTipoRefeicao) {
        return new PedidoAlimento(dataPedido, idUsuario, idUnidadePrisional, idContrato, idTipoRefeicao);
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

    public String getDataPedido() {
        return dataPedido;
    }

    public Long getUsuario() {
        return idUsuario;
    }

    public Long getUnidadePrisional() {
        return idUnidadePrisional;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Long getIdUnidadePrisional() {
        return idUnidadePrisional;
    }

    public Long getIdContrato() {
        return idContrato;
    }

    public Long getIdTipoRefeicao() {
        return idTipoRefeicao;
    }

    public List<ItemPedidoAlimento> getItensPedido() {
        return itensPedido;
    }

    public void setItensPedido(List<ItemPedidoAlimento> itensPedido) {
        this.itensPedido = itensPedido;
    }

    public List<DietaEspecialPedidoAlimento> getDietasEspeciais() {
        return dietasEspeciais;
    }

    public void setDietasEspeciais(List<DietaEspecialPedidoAlimento> dietasEspeciais) {
        this.dietasEspeciais = dietasEspeciais;
    }

    public Status getStatus() {
        return status;
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
                .append("dataPedido", dataPedido)
                .append("idUsuario", idUsuario)
                .append("idUnidadePrisional", idUnidadePrisional)
                .append("idContrato", idContrato)
                .append("idTipoRefeicao", idTipoRefeicao)
                .append("itensPedido", itensPedido)
                .append("dietasEspeciais", dietasEspeciais)
                .append("status", status)
                .toString();
    }
}
