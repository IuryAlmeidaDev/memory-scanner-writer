package br.gov.mt.sejudh.sigepen.model;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.dto.DetalheItemEstruturaCardapioDTO;
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

    private ItemPedidoAlimento(Long id, PedidoAlimento pedidoAlimento, String item, Double qtdPorItem, Integer qtdRecuperando, String siglaUnidadeMedida, String nomeEstrutura, Integer nivelEstrutura, Long itemCardapioId, Long estruturaId, Status status) {
        this.id = id;
        this.pedidoAlimento = pedidoAlimento;
        this.item = item;
        this.qtdPorItem = qtdPorItem;
        this.qtdRecuperando = qtdRecuperando;
        this.siglaUnidadeMedida = siglaUnidadeMedida;
        this.nomeEstrutura = nomeEstrutura;
        this.nivelEstrutura = nivelEstrutura;
        this.itemCardapioId = itemCardapioId;
        this.estruturaId = estruturaId;
        this.status = status;
    }

    public static ItemPedidoAlimento newInstance(PedidoAlimento pedidoAlimento, DetalheItemEstruturaCardapioDTO itemDTO) {
        return new ItemPedidoAlimento(null, pedidoAlimento, itemDTO.getItem(), itemDTO.getQtdPorItem(), itemDTO.getQtdRecuperando(), itemDTO.getSiglaUnidadeMedida(), itemDTO.getNomeEstrutura(), itemDTO.getNivelEstrutura(), itemDTO.getItemCardapioId(), itemDTO.getEstruturaId(), Status.ATIVO);
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

    public String getItem() {
        return item;
    }

    public Double getQtdPorItem() {
        return qtdPorItem;
    }

    public Integer getQtdRecuperando() {
        return qtdRecuperando;
    }

    public String getSiglaUnidadeMedida() {
        return siglaUnidadeMedida;
    }

    public String getNomeEstrutura() {
        return nomeEstrutura;
    }

    public Integer getNivelEstrutura() {
        return nivelEstrutura;
    }

    public Long getItemCardapioId() {
        return itemCardapioId;
    }

    public Long getEstruturaId() {
        return estruturaId;
    }

    public Status getStatus() {
        return status;
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