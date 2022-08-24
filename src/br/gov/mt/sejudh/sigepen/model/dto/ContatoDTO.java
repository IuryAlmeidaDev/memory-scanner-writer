package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.enumerator.TipoContato;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Servidor;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;

public class ContatoDTO {
	private long id;
	private Pessoa pessoa;
	private Servidor servidor;
	private UnidadePrisional unidadePrisional;
	private TipoContato tipoContato;	
	private String valor;
	private String nome;
	private String observacao;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Pessoa getPessoa() {
		return pessoa;
	}
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	public Servidor getServidor() {
		return servidor;
	}
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}
	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}
	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}
	public TipoContato getTipoContato() {
		return tipoContato;
	}
	public void setTipoContato(TipoContato tipoContato) {
		this.tipoContato = tipoContato;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	
}
