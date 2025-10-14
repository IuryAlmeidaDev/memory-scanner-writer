package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.model.Ocorrencia;
import br.gov.mt.sejudh.sigepen.util.TextUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class OcorrenciaRelatorioRegistroPlantaoDTO {
    private final String codigo;
    private final String autor;
    private final String classificacao;
    private final Date dataHora;
    private final String descricao;
    private final String resumo;

    private OcorrenciaRelatorioRegistroPlantaoDTO(String codigo, String autor, String classificacao, Date dataHora, String descricao, String resumo) {
        this.codigo = codigo;
        this.autor = TextUtil.toTitleCase(autor);
        this.classificacao = TextUtil.toTitleCase(classificacao);
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.resumo = resumo;
    }

    public static OcorrenciaRelatorioRegistroPlantaoDTO from(Ocorrencia ocorrencia) {
        return new OcorrenciaRelatorioRegistroPlantaoDTO(
                ocorrencia.getId().toString(),
                ocorrencia.getAutorRegistro().getUsuario(),
                ocorrencia.getTipoOcorrencia().getClassificacao(),
                ocorrencia.getDataOcorrencia(),
                ocorrencia.getDescricao(),
                ocorrencia.getResumo()
        );
    }

    public static OcorrenciaRelatorioRegistroPlantaoDTO criar(String codigo, String autor, String classificacao, Date dataHora, String descricao, String resumo) {
        return new OcorrenciaRelatorioRegistroPlantaoDTO(
                codigo,
                autor,
                classificacao,
                dataHora,
                descricao,
                resumo
        );
    }

    public String getCodigo() {
        return codigo;
    }

    public String getAutor() {
        return autor;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getResumo() {
        return resumo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("codigo", codigo)
                .append("classificacao", classificacao)
                .append("dataHora", dataHora)
                .append("descricao", descricao)
                .append("resumo", resumo)
                .toString();
    }
}
