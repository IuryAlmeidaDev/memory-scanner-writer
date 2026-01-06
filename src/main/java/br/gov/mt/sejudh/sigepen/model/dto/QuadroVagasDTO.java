package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroQuadroVagas;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;

public class QuadroVagasDTO {

	private long id;
	private String sigla;
	private Long capacidadeTotal;
	private Long quantidadeCondenado;
	private Long quantidadeProvisorios;
	private Long totalRecuperando;
	private Long deficit;
	private Long vagas;
	private String nomeUnidade;
	private SituacaoUnidade situacaoUnidade;
	private String cidade;
	private FiltroQuadroVagas filtroQuadroVagas;

	public QuadroVagasDTO() {

	}

	public QuadroVagasDTO(Long quantidadeCondenado, Long quantidadeProvisorios, Long capacidadeTotal,
			Long totalRecuperando, Long vagas, Long deficit, long id, String sigla, String nomeUnidade,
			SituacaoUnidade situacaoUnidade, String cidade) {

		this.quantidadeCondenado = quantidadeCondenado;
		this.quantidadeProvisorios = quantidadeProvisorios;
		this.capacidadeTotal = capacidadeTotal;
		this.totalRecuperando = totalRecuperando;
		this.vagas = vagas;
		this.deficit = deficit;
		this.id = id;
		this.sigla = sigla;
		this.nomeUnidade = nomeUnidade;
		this.situacaoUnidade = situacaoUnidade;
		this.cidade = cidade;
	}

	public QuadroVagasDTO(Long capacidadeTotal, Long id) {
		this.capacidadeTotal = capacidadeTotal;
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Long getCapacidadeTotal() {
		return capacidadeTotal;
	}

	public void setCapacidadeTotal(Long capacidadeTotal) {
		this.capacidadeTotal = capacidadeTotal;
	}

	public FiltroQuadroVagas getFiltroQuadroVagas() {
		return this.filtroQuadroVagas;
	}

	public void setFiltroQuadroVagas(FiltroQuadroVagas filtroQuadroVagas) {
		this.filtroQuadroVagas = filtroQuadroVagas;
	}

	@Override
	public String toString() {
		return "Unidade Prisional [id=" + id + ", nome=" + sigla + "]";
	}

	public Long getQuantidadeCondenado() {
		return quantidadeCondenado;
	}

	public void setQuantidadeCondenado(Long quantidadeCondenado) {
		this.quantidadeCondenado = quantidadeCondenado;
	}

	public Long getQuantidadeProvisorios() {
		return quantidadeProvisorios;
	}

	public void setQuantidadeProvisorios(Long quantidadeProvisorios) {
		this.quantidadeProvisorios = quantidadeProvisorios;
	}

	public Long getTotalRecuperando() {
		return totalRecuperando;
	}

	public void setTotalRecuperando(Long totalRecuperando) {
		this.totalRecuperando = totalRecuperando;
	}

	public Long getDeficit() {
		return deficit;
	}

	public void setDeficit(Long deficit) {
		this.deficit = deficit;
	}

	public Long getVagas() {
		return vagas;
	}

	public void setVagas(Long vagas) {
		this.vagas = vagas;
	}

	public String getNomeUnidade() {
		return nomeUnidade;
	}

	public void setNomeUnidade(String nomeUnidade) {
		this.nomeUnidade = nomeUnidade;
	}

	public String getSituacaoUnidade() {
		return situacaoUnidade.getDescricao();
	}

	public void setSituacaoUnidade(SituacaoUnidade situacaoUnidade) {
		this.situacaoUnidade = situacaoUnidade;
	}

	public String getCidade() {
		return this.cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

}
