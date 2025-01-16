package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.ArrayList;
import java.util.List;

import br.gov.mt.sejudh.sigepen.dao.CardapioPedidoDTO;

public class PedidoDetalhadoDTO {

	private Long id = 1L;
	private String dataPedido;
	private String refeicao;
	private String estrutura;
	private String unidadePrisionalSigla;
	private Integer qtdPadrao;
	private Integer qtdEspecial;
	private List<CardapioPedidoDTO> cardapio = new ArrayList<>();

	public PedidoDetalhadoDTO() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDataPedido() {
		return dataPedido;
	}

	public void setDataPedido(String data) {
		this.dataPedido = data;
	}
	
	public String getRefeicao() {
		return refeicao;
	}

	public void setRefeicao(String refeicao) {
		this.refeicao = refeicao;
	}

	public String getEstrutura() {
		return estrutura;
	}

	public void setEstrutura(String estrutura) {
		this.estrutura = estrutura;
	}

	public Integer getQtdPadrao() {
		return qtdPadrao;
	}

	public void setQtdPadrao(Integer qtdPadrao) {
		this.qtdPadrao = qtdPadrao;
	}

	public Integer getQtdEspecial() {
		return qtdEspecial;
	}

	public void setQtdEspecial(Integer qtdEspecial) {
		this.qtdEspecial = qtdEspecial;
	}

	public List<CardapioPedidoDTO> getCardapio() {
		return cardapio;
	}

	public void setCardapio(List<CardapioPedidoDTO> cardapio) {
		this.cardapio = cardapio;
	}

	public String getUnidadePrisionalSigla() {
		return unidadePrisionalSigla;
	}

	public void setUnidadePrisionalSigla(String unidadePrisionalSigla) {
		this.unidadePrisionalSigla = unidadePrisionalSigla;
	}

	@Override
	public String toString() {
		return "PedidoDetalhadoDTO [dataPedido=" + dataPedido + ", estrutura=" + estrutura
				+ ", qtdPadrao=" + qtdPadrao + ", qtdEspecial=" + qtdEspecial + ", unidadePrisionalSigla=" + unidadePrisionalSigla + "]";
	}
}
