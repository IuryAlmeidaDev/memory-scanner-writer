package br.gov.mt.sejudh.sigepen.dao;

import java.util.ArrayList;
import java.util.List;

import br.gov.mt.sejudh.sigepen.model.dto.PedidoItemCardapioDTO;

public class CardapioPedidoDTO {

	private String nome;
	private String estrutura;
	private String especial;
	private List<PedidoItemCardapioDTO> itens = new ArrayList<>();
	
	public CardapioPedidoDTO(CardapioPedidoDTO cardapioPedidoDTO) {
		List<PedidoItemCardapioDTO>itens = new ArrayList<>();
		cardapioPedidoDTO.getItens().forEach(item -> {
			itens.add(item);
		});
		this.nome = cardapioPedidoDTO.getNome();
		this.itens = itens;
		this.estrutura = cardapioPedidoDTO.getEstrutura();
		this.especial = cardapioPedidoDTO.getEspecial();
	}
	
	public CardapioPedidoDTO() {
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getEstrutura() {
		return estrutura;
	}

	public void setEstrutura(String estrutura) {
		this.estrutura = estrutura;
	}
	
	public String getEspecial() {
		return especial;
	}

	public void setEspecial(String especial) {
		this.especial = especial;
	}	
	
	public void setEspecial(boolean especial) {
		this.especial = especial ? "SIM" : "NÂO";
	}	
	
	
	public List<PedidoItemCardapioDTO> getItens() {
		return itens;
	}

	public void setItens(List<PedidoItemCardapioDTO> itens) {
		this.itens = itens;
	}
	
	@Override
	public String toString() {
		return "CardapioPedidoDTO [N=" + nome + "]";
	}

}
