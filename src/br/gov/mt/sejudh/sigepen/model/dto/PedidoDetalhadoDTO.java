package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.ArrayList;
import java.util.List;

import br.gov.mt.sejudh.sigepen.dao.CardapioPedidoDTO;

public class PedidoDetalhadoDTO {

	private Long id = 1L;
	private String dataPedido;
	private String refeicao;
	private String estrutura;
	private Integer qtdPadrao;
	private Integer qtdEspecial;
	private List<CardapioPedidoDTO> cardapio = new ArrayList<>();

	public PedidoDetalhadoDTO() {
		
	}
	
	public PedidoDetalhadoDTO(List<CardapioPedidoDTO> cardapio, String data) {
		this.cardapio = cardapio;		
	}

	public PedidoDetalhadoDTO(String dataPedido, String refeicao, String estrutura, Integer qtdPadrao,
			Integer qtdEspecial) {
		this.dataPedido = dataPedido;
		this.estrutura = estrutura;
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

	@Override
	public String toString() {
		return "PedidoDetalhadoDTO [dataPedido=" + dataPedido + ", estrutura=" + estrutura
				+ ", qtdPadrao=" + qtdPadrao + ", qtdEspecial=" + qtdEspecial + "]";
	}

}
