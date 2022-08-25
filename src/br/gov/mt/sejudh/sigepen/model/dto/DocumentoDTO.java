package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;

import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Servidor;
import br.gov.mt.sejudh.sigepen.model.TipoDocumento;

public class DocumentoDTO {
	private long id;
	private Pessoa pessoa;
	private Servidor servidor;
	private Recuperando recuperando;
	private TipoDocumento tipoDocumento;
	private String numero;
	private Date dataEmissao;
	private Date dataValidade;
	private String descricao;
    private SimNao obrigatorio;
    private SimNao validado;
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
	public Recuperando getRecuperando() {
		return recuperando;
	}
	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
	}
	public TipoDocumento getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(TipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Date getDataEmissao() {
		return dataEmissao;
	}
	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}
	public Date getDataValidade() {
		return dataValidade;
	}
	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public SimNao getObrigatorio() {
		return obrigatorio;
	}
	public void setObrigatorio(SimNao obrigatorio) {
		this.obrigatorio = obrigatorio;
	}
	public SimNao getValidado() {
		return validado;
	}
	public void setValidado(SimNao validado) {
		this.validado = validado;
	}	
	
	
}

