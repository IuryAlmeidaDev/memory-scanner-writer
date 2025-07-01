package br.gov.mt.sejudh.sigepen.model;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Classe de entidade para armazenar itens de pedidos de alimentos
 */
@Audited
@Entity
@Table(name = "SGP_ITEM_PEDIDO_ALIMENTO", schema = Sigepen.SEJUDH_SCHEMA)
public class ItemPedidoAlimento extends BaseModel<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SGP_S_ITEM_PEDIDO_ALIMENTO_GEN")
    @SequenceGenerator(
            name = "SGP_S_ITEM_PEDIDO_ALIMENTO_GEN",
            sequenceName = "SGP_S_ITEM_PEDIDO_ALIMENTO_GEN",
            allocationSize = 1,
            schema = Sigepen.SEJUDH_SCHEMA
    )
    @Column(name = "ID_ITEM_PEDIDO_ALIMENTO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PEDIDO_ALIMENTO", nullable = false)
    private PedidoAlimento pedidoAlimento;

    @Column(name = "ITEM_NOME", nullable = false)
    private String item;

    @Column(name = "QTD_POR_ITEM", nullable = false)
    private Double qtdPorItem;

    @Column(name = "QTD_RECUPERANDO", nullable = false)
    private Integer qtdRecuperando;

    @Column(name = "SIGLA_UNIDADE_MEDIDA", nullable = false)
    private String siglaUnidadeMedida;

    @Column(name = "NOME_ESTRUTURA", nullable = false)
    private String nomeEstrutura;

    @Column(name = "NIVEL_ESTRUTURA", nullable = false)
    private Integer nivelEstrutura;

    @Column(name = "ID_ITEM_CARDAPIO")
    private Long itemCardapioId;

    @Column(name = "ID_ESTRUTURA")
    private Long estruturaId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATUS", nullable = false)
    private Status status;

    public ItemPedidoAlimento() {
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

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Double getQtdPorItem() {
        return qtdPorItem;
    }

    public void setQtdPorItem(Double qtdPorItem) {
        this.qtdPorItem = qtdPorItem;
    }

    public Integer getQtdRecuperando() {
        return qtdRecuperando;
    }

    public void setQtdRecuperando(Integer qtdRecuperando) {
        this.qtdRecuperando = qtdRecuperando;
    }

    public String getSiglaUnidadeMedida() {
        return siglaUnidadeMedida;
    }

    public void setSiglaUnidadeMedida(String siglaUnidadeMedida) {
        this.siglaUnidadeMedida = siglaUnidadeMedida;
    }

    public String getNomeEstrutura() {
        return nomeEstrutura;
    }

    public void setNomeEstrutura(String nomeEstrutura) {
        this.nomeEstrutura = nomeEstrutura;
    }

    public Integer getNivelEstrutura() {
        return nivelEstrutura;
    }

    public void setNivelEstrutura(Integer nivelEstrutura) {
        this.nivelEstrutura = nivelEstrutura;
    }

    public Long getItemCardapioId() {
        return itemCardapioId;
    }

    public void setItemCardapioId(Long itemCardapioId) {
        this.itemCardapioId = itemCardapioId;
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
                .append("item", item)
                .append("qtdPorItem", qtdPorItem)
                .append("qtdRecuperando", qtdRecuperando)
                .append("siglaUnidadeMedida", siglaUnidadeMedida)
                .append("nomeEstrutura", nomeEstrutura)
                .append("nivelEstrutura", nivelEstrutura)
                .append("itemCardapioId", itemCardapioId)
                .append("estruturaId", estruturaId)
                .append("status", status)
                .toString();
    }
}