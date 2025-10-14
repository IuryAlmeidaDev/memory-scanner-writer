package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRelatorioEntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoRelatorioEntradaSaidaRec;
import br.gov.mt.sejudh.sigepen.util.TextUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class RelatorioEntradaSaidaRecuperandoDTO {
	
	FiltroRelatorioEntradaSaidaRecuperando filtro;

	private TipoRelatorioEntradaSaidaRec tipoRelatorioEntradaSaidaRec;

	private String regime;

	private String condicaoAtual;

	private Date dataInicial;

	private Date dataFinal;

	private String nome;
	
	private String nomeSocial;
	
	private Long matricula;
	
	private String nomeMae;
	
	private Date dataEntrada;
	
	private Date dataSaida;

	private Date dataNascimento;
	
	private String tipoEntrada;
	
	private String tipoDesligamento;
	
	private String situacao;

    public RelatorioEntradaSaidaRecuperandoDTO() {
    }

    public RelatorioEntradaSaidaRecuperandoDTO(Long matricula, String nome, String nomeSocial, String nomeMae, Date dtNascimento,
                                               Date dtEntrada, Date dtSaida, String tipoEntrada, String tipoDesligamento, String regime,
                                               String condicao, String situacao, FiltroRelatorioEntradaSaidaRecuperando filtro) {

		this.matricula = matricula;
        this.nome = TextUtil.toTitleCase(nome);
        this.nomeSocial = TextUtil.toTitleCase(nomeSocial);
        this.nomeMae = TextUtil.toTitleCase(nomeMae);
		this.dataNascimento = dtNascimento;
		this.dataEntrada = dtEntrada;
		this.dataSaida = dtSaida;
        this.tipoEntrada = TextUtil.toTitleCase(tipoEntrada);
        this.tipoDesligamento = TextUtil.toTitleCase(tipoDesligamento);
        this.regime = TextUtil.toTitleCase(regime);
        this.condicaoAtual = TextUtil.toTitleCase(condicao);
        this.situacao = TextUtil.toTitleCase(situacao);
		this.filtro = filtro;
	}

    public FiltroRelatorioEntradaSaidaRecuperando getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioEntradaSaidaRecuperando filtro) {
        this.filtro = filtro;
    }

    public TipoRelatorioEntradaSaidaRec getTipoRelatorioEntradaSaidaRec() {
        return tipoRelatorioEntradaSaidaRec;
    }

    public void setTipoRelatorioEntradaSaidaRec(TipoRelatorioEntradaSaidaRec tipoRelatorioEntradaSaidaRec) {
        this.tipoRelatorioEntradaSaidaRec = tipoRelatorioEntradaSaidaRec;
    }

    public String getRegime() {
        return regime;
    }

    public void setRegime(String regime) {
        this.regime = regime;
    }

    public String getCondicaoAtual() {
        return condicaoAtual;
    }

    public void setCondicaoAtual(String condicaoAtual) {
        this.condicaoAtual = condicaoAtual;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeSocial() {
        return nomeSocial;
    }

    public void setNomeSocial(String nomeSocial) {
        this.nomeSocial = nomeSocial;
    }

    public Long getMatricula() {
        return matricula;
    }

    public void setMatricula(Long matricula) {
        this.matricula = matricula;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public Date getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public Date getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(Date dataSaida) {
        this.dataSaida = dataSaida;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTipoEntrada() {
        return tipoEntrada;
    }

    public void setTipoEntrada(String tipoEntrada) {
        this.tipoEntrada = tipoEntrada;
    }

    public String getTipoDesligamento() {
        return tipoDesligamento;
    }

    public void setTipoDesligamento(String tipoDesligamento) {
        this.tipoDesligamento = tipoDesligamento;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataEntrada == null) ? 0 : dataEntrada.hashCode());
		result = prime * result + ((dataSaida == null) ? 0 : dataSaida.hashCode());
		result = prime * result + ((matricula == null) ? 0 : matricula.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + ((nomeMae == null) ? 0 : nomeMae.hashCode());
		result = prime * result + ((nomeSocial == null) ? 0 : nomeSocial.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelatorioEntradaSaidaRecuperandoDTO other = (RelatorioEntradaSaidaRecuperandoDTO) obj;
		if (dataEntrada == null) {
			if (other.dataEntrada != null)
				return false;
		} else if (!dataEntrada.equals(other.dataEntrada))
			return false;
		if (dataSaida == null) {
			if (other.dataSaida != null)
				return false;
		} else if (!dataSaida.equals(other.dataSaida))
			return false;
		if (matricula == null) {
			if (other.matricula != null)
				return false;
		} else if (!matricula.equals(other.matricula))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (nomeMae == null) {
			if (other.nomeMae != null)
				return false;
		} else if (!nomeMae.equals(other.nomeMae))
			return false;
		if (nomeSocial == null) {
            return other.nomeSocial == null;
        } else return nomeSocial.equals(other.nomeSocial);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("filtro", filtro)
                .append("tipoRelatorioEntradaSaidaRec", tipoRelatorioEntradaSaidaRec)
                .append("regime", regime)
                .append("condicaoAtual", condicaoAtual)
                .append("dataInicial", dataInicial)
                .append("dataFinal", dataFinal)
                .append("nome", nome)
                .append("nomeSocial", nomeSocial)
                .append("matricula", matricula)
                .append("nomeMae", nomeMae)
                .append("dataEntrada", dataEntrada)
                .append("dataSaida", dataSaida)
                .append("dataNascimento", dataNascimento)
                .append("tipoEntrada", tipoEntrada)
                .append("tipoDesligamento", tipoDesligamento)
                .append("situacao", situacao)
                .toString();
    }
}
