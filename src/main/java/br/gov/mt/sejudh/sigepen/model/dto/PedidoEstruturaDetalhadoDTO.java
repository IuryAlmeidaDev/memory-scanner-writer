package br.gov.mt.sejudh.sigepen.model.dto;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PedidoEstruturaDetalhadoDTO {

    private String item;
    private double quantidade;
    private String unidadeMedidaSigla;
    private String estruturaNome;
    private Integer estruturaNivel;

    public PedidoEstruturaDetalhadoDTO(String item, double quantidade, String unidadeMedidaSigla, String estruturaNome, Integer estruturaNivel) {
        this.item = item;
        this.quantidade = quantidade;
        this.unidadeMedidaSigla = unidadeMedidaSigla;
        this.estruturaNome = estruturaNome;
        this.estruturaNivel = estruturaNivel;
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

    public String getEstruturaNome() {
        return estruturaNome;
    }

    public void setEstruturaNome(String estruturaNome) {
        this.estruturaNome = estruturaNome;
    }

    public Integer getEstruturaNivel() {
        return estruturaNivel;
    }

    public void setEstruturaNivel(Integer estruturaNivel) {
        this.estruturaNivel = estruturaNivel;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("item", item)
                .append("quantidade", quantidade)
                .append("unidadeMedidaSigla", unidadeMedidaSigla)
                .append("estruturaNome", estruturaNome)
                .toString();
    }

    public static List<PedidoEstruturaDetalhadoDTO> combinarPedidos(List<PedidoEstruturaDTO> estruturas, List<PedidoConsultaDTO> pedidosConsulta, String unidadePrisionalSigla) {
        List<PedidoEstruturaDetalhadoDTO> combinados = new ArrayList<>();

        for (PedidoConsultaDTO pedido : pedidosConsulta) {
            for (PedidoEstruturaDTO estrutura : estruturas) {
                    String estruturaNome = buildEstruturaNome(unidadePrisionalSigla, estrutura);
                PedidoEstruturaDetalhadoDTO combinado = criaPedidoEstruturaDetalhadoDTO(pedido, estrutura, estruturaNome);
                combinados.add(combinado);
            }
        }

        // Ordena a lista por item e depois por nivel
        combinados.sort(Comparator.comparing(PedidoEstruturaDetalhadoDTO::getItem).thenComparing(PedidoEstruturaDetalhadoDTO::getEstruturaNivel));

        return combinados;
    }

    private static PedidoEstruturaDetalhadoDTO criaPedidoEstruturaDetalhadoDTO(PedidoConsultaDTO pedido, PedidoEstruturaDTO estrutura, String estruturaNome) {
        double quantidade = estrutura.getQtdPadrao() * ("M".equalsIgnoreCase(pedido.getSexo()) ? pedido.getMasc() : pedido.getFem());
        String unidadeMedidaSigla = pedido.getSiglaUnidadeMedida();
        Integer nivel = estrutura.getNivel();
        return new PedidoEstruturaDetalhadoDTO(
                pedido.getItem(),
                quantidade,
                unidadeMedidaSigla,
                estruturaNome,
                nivel
        );
    }

    private static String buildEstruturaNome(String unidadePrisionalSigla, PedidoEstruturaDTO estrutura) {
        String nomeEstruturaTratado = StringUtils.stripStart(estrutura.getEstruturaNome(), " >");
        if (estrutura.getNivel() > 1) {
            return unidadePrisionalSigla + " - " + nomeEstruturaTratado;
        } else {
            return unidadePrisionalSigla;
        }
    }
}

