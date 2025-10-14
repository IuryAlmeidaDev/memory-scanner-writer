package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.util.TextUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class PlantaoServidorDTO {
    private String equipeNome;
    private List<ComunicadoDTO> equipeComunicados;
    private String servidorNome;
    private String servidorAtividade;
    private String servidorSituacao;
    private String servidorOcorrencia;

    private PlantaoServidorDTO(String equipeNome, List<ComunicadoDTO> equipeComunicados, String servidorNome, String servidorAtividade, String servidorSituacao, String servidorOcorrencia) {
        this.equipeNome = TextUtil.toTitleCase(equipeNome);
        this.equipeComunicados = equipeComunicados;
        this.servidorNome = TextUtil.toTitleCase(servidorNome);
        this.servidorAtividade = TextUtil.toTitleCase(servidorAtividade);
        this.servidorSituacao = TextUtil.toTitleCase(servidorSituacao);
        this.servidorOcorrencia = TextUtil.toTitleCase(servidorOcorrencia);
    }

    public static PlantaoServidorDTO criar(String descricao, List<ComunicadoDTO> comunicadosDaEquipe, String servidorNome, String servidorAtividade, String servidorSituacao, String servidorOcorrencia) {
        return new PlantaoServidorDTO(descricao, comunicadosDaEquipe, servidorNome, servidorAtividade, servidorSituacao, servidorOcorrencia);
    }

    public String getEquipeNome() {
        return equipeNome;
    }

    public void setEquipeNome(String equipeNome) {
        this.equipeNome = equipeNome;
    }

    public List<ComunicadoDTO> getEquipeComunicados() {
        return equipeComunicados;
    }

    public void setEquipeComunicados(List<ComunicadoDTO> equipeComunicados) {
        this.equipeComunicados = equipeComunicados;
    }

    public String getServidorNome() {
        return servidorNome;
    }

    public void setServidorNome(String servidorNome) {
        this.servidorNome = servidorNome;
    }

    public String getServidorAtividade() {
        return servidorAtividade;
    }

    public void setServidorAtividade(String servidorAtividade) {
        this.servidorAtividade = servidorAtividade;
    }

    public String getServidorSituacao() {
        return servidorSituacao;
    }

    public void setServidorSituacao(String servidorSituacao) {
        this.servidorSituacao = servidorSituacao;
    }

    public String getServidorOcorrencia() {
        return servidorOcorrencia;
    }

    public void setServidorOcorrencia(String servidorOcorrencia) {
        this.servidorOcorrencia = servidorOcorrencia;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("equipeNome", equipeNome)
                .append("equipeComunicados", equipeComunicados)
                .append("servidorNome", servidorNome)
                .append("servidorAtividade", servidorAtividade)
                .append("servidorSituacao", servidorSituacao)
                .append("servidorOcorrencia", servidorOcorrencia)
                .toString();
    }
}
