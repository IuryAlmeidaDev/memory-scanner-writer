package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.model.PlantaoServidor;
import br.gov.mt.sejudh.sigepen.util.TextUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ComunicadoDTO {
    private String matricula;
    private String servidorNome;
    private String ocorrencia;
    private String descricao;

    private ComunicadoDTO(String matricula, String servidorNome, String ocorrencia, String descricao) {
        this.matricula = matricula;
        this.servidorNome = TextUtil.toTitleCase(servidorNome);
        this.ocorrencia = TextUtil.toTitleCase(ocorrencia);
        this.descricao = TextUtil.toTitleCase(descricao);
    }

    public static ComunicadoDTO criarComunicado(PlantaoServidor ps) {
        return new ComunicadoDTO(ps.getServidor().getMatricula().toString(), ps.getServidor().getNome(), ps.getOcorrencia(), ps.getJustificativa());
    }

    // Fábrica auxiliar para permitir criação de placeholders sem PlantaoServidor
    public static ComunicadoDTO criar(String matricula, String servidorNome, String ocorrencia, String descricao) {
        return new ComunicadoDTO(matricula, servidorNome, ocorrencia, descricao);
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getServidorNome() {
        return servidorNome;
    }

    public void setServidorNome(String servidorNome) {
        this.servidorNome = servidorNome;
    }

    public String getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(String ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("matricula", matricula)
                .append("servidorNome", servidorNome)
                .append("ocorrencia", ocorrencia)
                .append("descricao", descricao)
                .toString();
    }
}
