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
    private String unidadeMedidaSigla;
    private String dietaEstrutura;
    private Integer estruturaNivel;
    private String tipoDieta;
    private String tipoCardapio;

    public DetalheItemEstruturaCardapioDTO(String item, double quantidade, String unidadeMedidaSigla, String dietaEstrutura, Integer estruturaNivel, boolean isEspecial, String tipoCardapio) {
        this.item = item;
        this.quantidade = quantidade;
        this.unidadeMedidaSigla = unidadeMedidaSigla;
        this.dietaEstrutura = dietaEstrutura;
        this.estruturaNivel = estruturaNivel;
        this.tipoDieta = isEspecial ? "Especial" : "Padrão";
        this.tipoCardapio = tipoCardapio;
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

    public String getUnidadeMedidaSigla() {
        return unidadeMedidaSigla;
    }

    public void setUnidadeMedidaSigla(String unidadeMedidaSigla) {
        this.unidadeMedidaSigla = unidadeMedidaSigla;
    }

    public String getDietaEstrutura() {
        return dietaEstrutura;
    }

    public void setDietaEstrutura(String dietaEstrutura) {
        this.dietaEstrutura = dietaEstrutura;
    }

    public Integer getEstruturaNivel() {
        return estruturaNivel;
    }

    public void setEstruturaNivel(Integer estruturaNivel) {
        this.estruturaNivel = estruturaNivel;
    }

    public String getTipoDieta() {
        return tipoDieta;
    }

    public void setTipoDieta(String tipoDieta) {
        this.tipoDieta = tipoDieta;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("item", item)
                .append("quantidade", quantidade)
                .append("unidadeMedidaSigla", unidadeMedidaSigla)
                .append("dietaEstrutura", dietaEstrutura)
                .append("tipoDieta", tipoDieta)
                .append("tipoCardapio", tipoCardapio)
                .toString();
    }

    public String getTipoCardapio() {
        return tipoCardapio;
    }

    public void setTipoCardapio(String tipoCardapio) {
        this.tipoCardapio = tipoCardapio;
    }

    public static List<DetalheItemEstruturaCardapioDTO> combinarPedidos(List<PedidoEstruturaDTO> estruturas, List<RefeicaoDetalhadaDTO> pedidosConsulta) {
        List<DetalheItemEstruturaCardapioDTO> combinados = new ArrayList<>();

        for (RefeicaoDetalhadaDTO pedido : pedidosConsulta) {
            for (PedidoEstruturaDTO estrutura : estruturas) {
                if (possuiRecuperando(pedido, estrutura)) {
                    String estruturaNome = StringUtils.stripStart(estrutura.getEstruturaNome(), " >");
                    String dietaEstrutura = pedido.getTipoCardapio() + " - " + estruturaNome;
                    combinados.add(criaPedidoEstruturaDetalhadoDTO(pedido, estrutura, dietaEstrutura));
                }
            }
        }

        combinados.sort(
                Comparator.comparing(DetalheItemEstruturaCardapioDTO::getDietaEstrutura, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(DetalheItemEstruturaCardapioDTO::getItem, String.CASE_INSENSITIVE_ORDER)
        );

        return combinados.stream()
                .sorted(Comparator.comparing(DetalheItemEstruturaCardapioDTO::getTipoDieta)
                        .thenComparing(DetalheItemEstruturaCardapioDTO::getDietaEstrutura))
                .collect(Collectors.toList());
    }

    private static boolean possuiRecuperando(RefeicaoDetalhadaDTO pedido, PedidoEstruturaDTO estrutura) {
        return (pedido.isEspecial() ? estrutura.getQtdEspecial() : estrutura.getQtdPadrao()) > 0;
    }

    private static DetalheItemEstruturaCardapioDTO criaPedidoEstruturaDetalhadoDTO(RefeicaoDetalhadaDTO pedido, PedidoEstruturaDTO estrutura, String dietaEstrutura) {
        String unidadeMedidaSigla = pedido.getSiglaUnidadeMedida();
        Integer nivel = estrutura.getNivel();
        double quantidadeRecuperando = pedido.isEspecial() ? estrutura.getQtdEspecial() : estrutura.getQtdPadrao();
        double quantidadeItem = quantidadeRecuperando * ("M".equalsIgnoreCase(pedido.getSexo()) ? pedido.getMasc() : pedido.getFem());
        return new DetalheItemEstruturaCardapioDTO(
                pedido.getItem(),
                quantidadeItem,
                unidadeMedidaSigla,
                dietaEstrutura,
                nivel,
                pedido.isEspecial(),
                pedido.getTipoCardapio());
    }
}

