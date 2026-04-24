package br.gov.mt.sejudh.sigepen.model.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import br.gov.mt.sejudh.sigepen.util.TextUtil;

public class RelatorioJumboDTO {

    private Long matricula;
    private String nomeRecuperando;
    private String nomeMae;
    private String unidadePrisional;
    private String localizacao;

    public RelatorioJumboDTO() {
    }

    public RelatorioJumboDTO(Long matricula, String nomeRecuperando, String nomeMae,
                             String unidadePrisional, String localizacao) {
        this.matricula = matricula;
        this.nomeRecuperando = TextUtil.toTitleCase(nomeRecuperando);
        this.nomeMae = TextUtil.toTitleCase(nomeMae);
        this.unidadePrisional = TextUtil.toTitleCase(unidadePrisional);
        this.localizacao = TextUtil.toTitleCase(localizacao);
    }

    public Long getMatricula() {
        return matricula;
    }

    public void setMatricula(Long matricula) {
        this.matricula = matricula;
    }

    public String getNomeRecuperando() {
        return nomeRecuperando;
    }

    public void setNomeRecuperando(String nomeRecuperando) {
        this.nomeRecuperando = nomeRecuperando;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public String getUnidadePrisional() {
        return unidadePrisional;
    }

    public void setUnidadePrisional(String unidadePrisional) {
        this.unidadePrisional = unidadePrisional;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((matricula == null) ? 0 : matricula.hashCode());
        result = prime * result + ((unidadePrisional == null) ? 0 : unidadePrisional.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        RelatorioJumboDTO other = (RelatorioJumboDTO) obj;

        if (matricula == null) {
            if (other.matricula != null) return false;
        } else if (!matricula.equals(other.matricula)) {
            return false;
        }

        if (unidadePrisional == null) {
            return other.unidadePrisional == null;
        }
        return unidadePrisional.equals(other.unidadePrisional);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("matricula", matricula)
                .append("nomeRecuperando", nomeRecuperando)
                .append("nomeMae", nomeMae)
                .append("unidadePrisional", unidadePrisional)
                .append("localizacao", localizacao)
                .toString();
    }
}
