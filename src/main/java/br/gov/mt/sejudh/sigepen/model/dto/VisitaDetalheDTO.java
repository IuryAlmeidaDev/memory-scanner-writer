package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.model.PlantaoVisitaEstrutura;
import br.gov.mt.sejudh.sigepen.util.TextUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class VisitaDetalheDTO {
    private String tipoEntrada;
    private String estruturaNome;
    private Long qtdHomens;
    private Long qtdMulheres;
    private Long qtdCriancas;
    private Long qtdOcorrencias;
    private String horarioInicio;
    private String horarioFim;

    private VisitaDetalheDTO(String tipoEntrada, String estruturaNome, Long qtdHomens, Long qtdMulheres, Long qtdCriancas, Long qtdOcorrencias, String horarioInicio, String horarioFim) {
        this.tipoEntrada = TextUtil.toTitleCase(tipoEntrada);
        this.estruturaNome = TextUtil.toTitleCase(estruturaNome);
        this.qtdHomens = qtdHomens;
        this.qtdMulheres = qtdMulheres;
        this.qtdCriancas = qtdCriancas;
        this.qtdOcorrencias = qtdOcorrencias;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
    }

    public static VisitaDetalheDTO criarVisitaDetalheDTO(String tipoEntrada, PlantaoVisitaEstrutura pve) {
        return new VisitaDetalheDTO(tipoEntrada, pve.getEstrutura(), pve.getHomem(), pve.getMulher(), pve.getMenor(), pve.getOcorrencia(), pve.getHoraInicio(), pve.getHoraFim());
    }

    public static VisitaDetalheDTO criarPlaceholder(String tipoEntrada, String estruturaNome) {
        return new VisitaDetalheDTO(tipoEntrada, estruturaNome, 0L, 0L, 0L, 0L, "--:--", "--:--");
    }

    public String getTipoEntrada() {
        return tipoEntrada;
    }

    public void setTipoEntrada(String tipoEntrada) {
        this.tipoEntrada = tipoEntrada;
    }

    public String getEstruturaNome() {
        return estruturaNome;
    }

    public void setEstruturaNome(String estruturaNome) {
        this.estruturaNome = estruturaNome;
    }

    public Long getQtdHomens() {
        return qtdHomens;
    }

    public void setQtdHomens(Long qtdHomens) {
        this.qtdHomens = qtdHomens;
    }

    public Long getQtdMulheres() {
        return qtdMulheres;
    }

    public void setQtdMulheres(Long qtdMulheres) {
        this.qtdMulheres = qtdMulheres;
    }

    public Long getQtdCriancas() {
        return qtdCriancas;
    }

    public void setQtdCriancas(Long qtdCriancas) {
        this.qtdCriancas = qtdCriancas;
    }

    public Long getQtdOcorrencias() {
        return qtdOcorrencias;
    }

    public void setQtdOcorrencias(Long qtdOcorrencias) {
        this.qtdOcorrencias = qtdOcorrencias;
    }

    public String getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(String horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public String getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(String horarioFim) {
        this.horarioFim = horarioFim;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("tipoEntrada", tipoEntrada)
                .append("estruturaNome", estruturaNome)
                .append("qtdHomens", qtdHomens)
                .append("qtdMulheres", qtdMulheres)
                .append("qtdCriancas", qtdCriancas)
                .append("qtdOcorrencias", qtdOcorrencias)
                .append("horarioInicio", horarioInicio)
                .append("horarioFim", horarioFim)
                .toString();
    }
}
