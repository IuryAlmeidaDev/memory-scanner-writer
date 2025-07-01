package br.gov.mt.sejudh.sigepen.model;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Classe de entidade para armazenar itens de pedidos de alimentos com dieta especial.
 */
@Audited
@Entity
@Table(name = "SGP_DIETA_ESP_PEDIDO", schema = Sigepen.SEJUDH_SCHEMA)
public class DietaEspecialPedidoAlimento extends BaseModel<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SGP_S_DIETA_ESP_PEDIDO_GEN")
    @SequenceGenerator(
            name = "SGP_S_DIETA_ESP_PEDIDO_GEN",
            sequenceName = "SGP_S_DIETA_ESP_PEDIDO_GEN",
            allocationSize = 1,
            schema = Sigepen.SEJUDH_SCHEMA
    )
    @Column(name = "ID_DIETA_ESP_PEDIDO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PEDIDO_ALIMENTO", nullable = false)
    private PedidoAlimento pedidoAlimento;

    @Column(name = "NOME_ESTRUTURA", nullable = false)
    private String nomeEstrutura;

    @Column(name = "TIPO_DIETA", nullable = false)
    private String tipoDieta;

    @Column(name = "QTD_RECUPERANDO", nullable = false)
    private Integer qtdRecuperando;

    @Column(name = "NOMES_RECUPERANDOS")
    private String nomesRecuperandos;

    @Column(name = "ID_ESTRUTURA")
    private Long estruturaId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATUS", nullable = false)
    private Status status;

    public DietaEspecialPedidoAlimento() {
        this.status = Status.ATIVO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PedidoAlimento getPedidoAlimento() {
        return pedidoAlimento;
    }

    public void setPedidoAlimento(PedidoAlimento pedidoAlimento) {
        this.pedidoAlimento = pedidoAlimento;
    }

    public String getNomeEstrutura() {
        return nomeEstrutura;
    }

    public void setNomeEstrutura(String nomeEstrutura) {
        this.nomeEstrutura = nomeEstrutura;
    }

    public String getTipoDieta() {
        return tipoDieta;
    }

    public void setTipoDieta(String tipoDieta) {
        this.tipoDieta = tipoDieta;
    }

    public Integer getQtdRecuperando() {
        return qtdRecuperando;
    }

    public void setQtdRecuperando(Integer qtdRecuperando) {
        this.qtdRecuperando = qtdRecuperando;
    }

    public String getNomesRecuperandos() {
        return nomesRecuperandos;
    }

    public void setNomesRecuperandos(String nomesRecuperandos) {
        this.nomesRecuperandos = nomesRecuperandos;
    }

    public Long getEstruturaId() {
        return estruturaId;
    }

    public void setEstruturaId(Long estruturaId) {
        this.estruturaId = estruturaId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("nomeEstrutura", nomeEstrutura)
                .append("tipoDieta", tipoDieta)
                .append("qtdRecuperando", qtdRecuperando)
                .append("nomesRecuperandos", nomesRecuperandos)
                .append("estruturaId", estruturaId)
                .append("status", status)
                .toString();
    }
}