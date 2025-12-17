package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroInfopen;
import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;


public class InfopenRecuperandoDTO {

	private Long id;
	private String nome;
	private String nomeMae;
	private StatusRecuperando status;
	private String regime;
	private Condicao condicao;
	private String estrutura;
	private String descricaoFiltro;
	private String descricaoTitulo;
	private String dataEntrada;
	private String dataSaida;
	private String unidade;

	
	private FiltroInfopen filtro;

	public InfopenRecuperandoDTO() {
	}

	public InfopenRecuperandoDTO(Long id, String nome, String nomeMae, String regime, String condicao,
			String status, FiltroInfopen filtro, String descricaoFiltro, String descricaoTitulo, String estrutura, String dataEntrada, String dataSaida, String unidade) {
		this.id = id;
		this.nome = nome;
		this.nomeMae = nomeMae;
		this.regime = regime;
		this.condicao = Condicao.valueOf(condicao);
		this.status = StatusRecuperando.valueOf(status);
		this.filtro = filtro;
		this.descricaoFiltro = descricaoFiltro;
		this.descricaoTitulo = descricaoTitulo;
		this.estrutura = estrutura;
		this.dataEntrada = dataEntrada;
		this.dataSaida = dataSaida;
		this.unidade = unidade;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getStatus() {
		return (status != null) ? status.getDescricao() : null;
	}

	public void setStatus(StatusRecuperando status) {
		this.status = (status == null) ? StatusRecuperando.INATIVO : status;
	}

	public String getRegime() {
		return regime;
	}

	public void setRegime(String regime) {
		this.regime = regime;
	}

	public Boolean getIsRecuperando() {
		return (getId() != null);
	}

	public Boolean getIsRecuperandoInativo() {
		return (!getIsRecuperando() || this.status == StatusRecuperando.INATIVO);
	}

	public Boolean getIsRecuperandoAtivo() {
		return (this.status == StatusRecuperando.ATIVO);
	}

	public Boolean getIsRecuperandoParcial() {
		return (this.status == StatusRecuperando.INATIVO_PARCIAL);
	}

	@Override
	public String toString() {
		return "Recuperando [id=" + id + ", nome=" + nome + "]";
	}

	public FiltroInfopen getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroInfopen filtro) {
		this.filtro = filtro;
	}

	public String getDescricaoFiltro() {
		return descricaoFiltro;
	}

	public void setDescricaoFiltro(String descricaoFiltro) {
		this.descricaoFiltro = descricaoFiltro;
	}

	public String getDescricaoTitulo() {
		return descricaoTitulo;
	}

	public void setDescricaoTitulo(String descricaoTitulo) {
		this.descricaoTitulo = descricaoTitulo;
	}

	public String getEstrutura() {
		return estrutura;
	}

	public void setEstrutura(String estrutura) {
		this.estrutura = estrutura;
	}

	public Condicao getCondicao() {
		return condicao;
	}

	public void setCondicao(Condicao condicao) {
		this.condicao = condicao;
	}

	public String getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(String dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public String getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(String dataSaida) {
		this.dataSaida = dataSaida;
	}

	public String getUnidade() {
		return unidade;
	}
	
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

}
