package br.gov.mt.sejudh.sigepen.model.dto;

import java.math.BigDecimal;
import java.util.Date;

public class RelatorioMapaAlimentacaoRecuperandoDTO {
	
	private String nomeRecuperando;
	private String nomeSocialRecuperando;
	private String matriculaRecuperando;
	private Date entradaRecuperando;
	private Date saidaRecuperando;
	private Date inicioEspecial;
	private Date fimEspecial;

	public RelatorioMapaAlimentacaoRecuperandoDTO() {
		
	}
	
	public RelatorioMapaAlimentacaoRecuperandoDTO(Object[] row) {
		this.nomeRecuperando = ((String) row[0]);
		this.matriculaRecuperando = ((BigDecimal) row[1]).toString();
		this.entradaRecuperando = ((Date) row[2]);
		this.saidaRecuperando = (Date) row[3];
		this.inicioEspecial = (Date) row[6];
		this.fimEspecial = (Date) row[7];
		this.nomeSocialRecuperando = (String) row[8];
	}
	
	
	public RelatorioMapaAlimentacaoRecuperandoDTO(String nomeUnidade, Date virgenciaContartoInicio, Date virgenciaContartoFim,
			String numeroContrato, String nomeFornecedor, String mesReferencia, String valorRefeicao,
			String nomeRecuperando, String matriculaRecuperando, Date entradaRecuperando, Date saidaRecuperando,
			Integer diasRecuperando, Integer cafeManha, Integer almoco, Integer janta, Integer cafeManhaEspecial,
			Integer almocoEspecial, Integer jantaEspecial, Double soma) {
		this.nomeRecuperando = nomeRecuperando;
		this.matriculaRecuperando = matriculaRecuperando;
		this.entradaRecuperando = entradaRecuperando;
		this.saidaRecuperando = saidaRecuperando;
	}

	public String getNomeRecuperando() {
		return nomeRecuperando;
	}

	public void setNomeRecuperando(String nomeRecuperando) {
		this.nomeRecuperando = nomeRecuperando;
	}

	public String getMatriculaRecuperando() {
		return matriculaRecuperando;
	}

	public void setMatriculaRecuperando(String matriculaRecuperando) {
		this.matriculaRecuperando = matriculaRecuperando;
	}

	public Date getEntradaRecuperando() {
		return entradaRecuperando;
	}

	public void setEntradaRecuperando(Date entradaRecuperando) {
		this.entradaRecuperando = entradaRecuperando;
	}

	public Date getSaidaRecuperando() {
		return saidaRecuperando;
	}

	public void setSaidaRecuperando(Date saidaRecuperando) {
		this.saidaRecuperando = saidaRecuperando;
	}

	public Date getInicioEspecial() {
		return inicioEspecial;
	}

	public void setInicioEspecial(Date inicioEspecial) {
		this.inicioEspecial = inicioEspecial;
	}

	public Date getFimEspecial() {
		return fimEspecial;
	}

	public void setFimEspecial(Date fimEspecial) {
		this.fimEspecial = fimEspecial;
	}

	@Override
	public String toString() {
		return "RelatorioMapaAlimentacaoRecuperandoDTO [nomeRecuperando=" + nomeRecuperando + ", matriculaRecuperando="
				+ matriculaRecuperando + ", entradaRecuperando=" + entradaRecuperando + ", saidaRecuperando="
				+ saidaRecuperando + ", inicioEspecial=" + inicioEspecial + ", fimEspecial=" + fimEspecial + "]";
	}

    public String getNomeSocialRecuperando() {
        return nomeSocialRecuperando;
    }

    public void setNomeSocialRecuperando(String nomeSocialRecuperando) {
        this.nomeSocialRecuperando = nomeSocialRecuperando;
    }
}
