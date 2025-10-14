package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.enumerator.UnidadeMedida;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DetalheItemEstruturaCardapioDTO {

    private String item;
    private double qtdPorItem;
    private Integer qtdRecuperando;
    private String siglaUnidadeMedida;
    private String nomeEstrutura;
    private Integer nivelEstrutura;
    private Long itemCardapioId;
    private Long estruturaId;
    private double total;
    private String unidadeMedidaTotal;

    public DetalheItemEstruturaCardapioDTO(String item, double qtdPorItem, Integer qtdRecuperando, String siglaUnidadeMedida, String nomeEstrutura, Integer nivelEstrutura, Long itemCardapioId, Long estruturaId) {
        this.item = item;
        this.qtdPorItem = qtdPorItem;
        this.qtdRecuperando = qtdRecuperando;
        this.siglaUnidadeMedida = siglaUnidadeMedida;
        this.nomeEstrutura = nomeEstrutura;
        this.nivelEstrutura = nivelEstrutura;
        this.itemCardapioId = itemCardapioId;
        this.estruturaId = estruturaId;
        calculaTotalEUnidadeDeMedida();
    }

    private void calculaTotalEUnidadeDeMedida() {
        this.total = this.qtdPorItem * (this.qtdRecuperando != null ? this.qtdRecuperando : 0);
        this.unidadeMedidaTotal = this.siglaUnidadeMedida;

        if (this.total >= 1000) {
            if (UnidadeMedida.MILIGRAMA.getSigla().equalsIgnoreCase(this.siglaUnidadeMedida)) {
                this.total = this.total / 1000;
                this.unidadeMedidaTotal = UnidadeMedida.GRAMA.getSigla();
            } else if (UnidadeMedida.GRAMA.getSigla().equalsIgnoreCase(this.siglaUnidadeMedida)) {
                this.total = this.total / 1000;
                this.unidadeMedidaTotal = UnidadeMedida.KILOGRAMA.getSigla();
            } else if (UnidadeMedida.MILILITRO.getSigla().equalsIgnoreCase(this.siglaUnidadeMedida)) {
                this.total = this.total / 1000;
                this.unidadeMedidaTotal = UnidadeMedida.LITRO.getSigla();
            }
        }
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getQtdPorItem() {
        return qtdPorItem;
    }

    public void setQtdPorItem(double qtdPorItem) {
        this.qtdPorItem = qtdPorItem;
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

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getUnidadeMedidaTotal() {
        return unidadeMedidaTotal;
    }

    public void setUnidadeMedidaTotal(String unidadeMedidaTotal) {
        this.unidadeMedidaTotal = unidadeMedidaTotal;
    }

    public static List<DetalheItemEstruturaCardapioDTO> combinarPedidos(List<PedidoEstruturaDTO> estruturas, List<RefeicaoDetalhadaDTO> pedidosConsulta) {
        List<DetalheItemEstruturaCardapioDTO> combinados = new ArrayList<>();
        for (RefeicaoDetalhadaDTO pedido : pedidosConsulta) {
            for (PedidoEstruturaDTO estrutura : estruturas) {
                if (possuiRecuperando(estrutura)) {
                    combinados.add(criaPedidoEstruturaDetalhadoDTO(pedido, estrutura));
                }
            }
        }

        return combinados.stream()
                .sorted(Comparator.comparing(DetalheItemEstruturaCardapioDTO::getNomeEstrutura))
                .collect(Collectors.toList());
    }

    public Integer getQtdRecuperando() {
        return qtdRecuperando;
    }

    public void setQtdRecuperando(Integer qtdRecuperando) {
        this.qtdRecuperando = qtdRecuperando;
    }

    private static boolean possuiRecuperando(PedidoEstruturaDTO estrutura) {
        return estrutura.getQtdPadrao() > 0;
    }

    private static DetalheItemEstruturaCardapioDTO criaPedidoEstruturaDetalhadoDTO(RefeicaoDetalhadaDTO pedido, PedidoEstruturaDTO estrutura) {
        String unidadeMedidaSigla = pedido.getSiglaUnidadeMedida();
        Integer nivel = estrutura.getNivel();
        String nomeEstrutura = StringUtils.stripStart(estrutura.getEstruturaNome(), " >");
        double qtdPorItem = "M".equalsIgnoreCase(pedido.getSexo()) ? pedido.getMasc() : pedido.getFem();
        return new DetalheItemEstruturaCardapioDTO(
                pedido.getItem(),
                qtdPorItem,
                estrutura.getQtdPadrao(),
                unidadeMedidaSigla,
                nomeEstrutura,
                nivel,
                pedido.getItemCardapioId(),
                estrutura.getEstruturaId() != null ? estrutura.getEstruturaId().longValue() : null
        );
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("item", item)
                .append("qtdPorItem", qtdPorItem)
                .append("qtdRecuperando", qtdRecuperando)
                .append("siglaUnidadeMedida", siglaUnidadeMedida)
                .append("nomeEstrutura", nomeEstrutura)
                .append("nivelEstrutura", nivelEstrutura)
                .append("itemCardapioId", itemCardapioId)
                .append("estruturaId", estruturaId)
                .append("total", total)
                .append("unidadeMedidaTotal", unidadeMedidaTotal)
                .toString();
    }
}
