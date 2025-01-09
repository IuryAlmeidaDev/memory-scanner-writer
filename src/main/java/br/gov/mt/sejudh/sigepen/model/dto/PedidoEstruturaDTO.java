package br.gov.mt.sejudh.sigepen.model.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

public class PedidoEstruturaDTO {

    private Integer nivel;
    private Integer estruturaId;
    private String estruturaNome;
    private Integer qtdPadrao;

    public PedidoEstruturaDTO(Object[] row) {
        nivel = ((BigDecimal) row[0]).intValue();
        estruturaId = ((BigDecimal) row[1]).intValue();
        estruturaNome = (String) row[2];
        qtdPadrao = ((BigDecimal) row[3]).intValue();
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public Integer getEstruturaId() {
        return estruturaId;
    }

    public void setEstruturaId(Integer estruturaId) {
        this.estruturaId = estruturaId;
    }

    public String getEstruturaNome() {
        return estruturaNome;
    }

    public void setEstruturaNome(String estruturaNome) {
        this.estruturaNome = estruturaNome;
    }

    public Integer getQtdPadrao() {
        return qtdPadrao;
    }

    public void setQtdPadrao(Integer qtdPadrao) {
        this.qtdPadrao = qtdPadrao;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("nivel", nivel)
                .append("estruturaId", estruturaId)
                .append("estruturaNome", estruturaNome)
                .append("qtdPadrao", qtdPadrao)
                .toString();
    }
}

