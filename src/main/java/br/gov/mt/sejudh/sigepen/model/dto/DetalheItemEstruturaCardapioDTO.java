package br.gov.mt.sejudh.sigepen.model.dto;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DetalheItemEstruturaCardapioDTO {

    private String item;
    private double quantidade;
    private String siglaUnidadeMedida;
    private String nomeEstrutura;
    private Integer nivelEstrutura;

    public DetalheItemEstruturaCardapioDTO(String item, double quantidade, String siglaUnidadeMedida, String nomeEstrutura, Integer nivelEstrutura) {
        this.item = item;
        this.quantidade = quantidade;
        this.siglaUnidadeMedida = siglaUnidadeMedida;
        this.nomeEstrutura = nomeEstrutura;
        this.nivelEstrutura = nivelEstrutura;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
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

    public static List<DetalheItemEstruturaCardapioDTO> combinarPedidos(List<PedidoEstruturaDTO> estruturas, List<RefeicaoDetalhadaDTO> pedidosConsulta) {
        List<DetalheItemEstruturaCardapioDTO> combinados = new ArrayList<>();
        for (RefeicaoDetalhadaDTO pedido : pedidosConsulta) {
            for (PedidoEstruturaDTO estrutura : estruturas) {
                if (possuiRecuperando(pedido, estrutura)) {
                    combinados.add(criaPedidoEstruturaDetalhadoDTO(pedido, estrutura));
                }
            }
        }

        return combinados.stream()
                .sorted(Comparator.comparing(DetalheItemEstruturaCardapioDTO::getNomeEstrutura))
                .collect(Collectors.toList());
    }

    private static boolean possuiRecuperando(RefeicaoDetalhadaDTO pedido, PedidoEstruturaDTO estrutura) {
        return estrutura.getQtdPadrao() > 0;
    }

    private static DetalheItemEstruturaCardapioDTO criaPedidoEstruturaDetalhadoDTO(RefeicaoDetalhadaDTO pedido, PedidoEstruturaDTO estrutura) {
        String unidadeMedidaSigla = pedido.getSiglaUnidadeMedida();
        Integer nivel = estrutura.getNivel();
        String nomeEstrutura = StringUtils.stripStart(estrutura.getEstruturaNome(), " >");
        double quantidadeRecuperando = estrutura.getQtdPadrao();
        double quantidadeItem = quantidadeRecuperando * ("M".equalsIgnoreCase(pedido.getSexo()) ? pedido.getMasc() : pedido.getFem());
        return new DetalheItemEstruturaCardapioDTO(
                pedido.getItem(),
                quantidadeItem,
                unidadeMedidaSigla,
                nomeEstrutura,
                nivel
        );
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("item", item)
                .append("quantidade", quantidade)
                .append("siglaUnidadeMedida", siglaUnidadeMedida)
                .append("nomeEstrutura", nomeEstrutura)
                .append("nivelEstrutura", nivelEstrutura)
                .toString();
    }
}

