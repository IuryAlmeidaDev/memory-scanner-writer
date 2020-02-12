package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;

import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;


public class PedidoTransitoDTO {


	private long id;
	
	private TipoTransito tipoTransito;

	private long idRec;
	
	private String recuperando;	
	
	private Date dataPrevista;
		
	private String estruturaNome;
	
	private String estruturaOrigemNome;
	
	private String nomeUnidadeOrigem;
	
	private String nomeUnidadeDestino;
	
	private String estabelecimentoNome;	
	
	private StatusPedido status;
	
	private Long idTransferencia;
	
	private String estruturaNomeCompleto;
	
	private String estruturaDestinoNomeCompleto;
	
	@SuppressWarnings("unused")
	private String destinoParaExibicaoDataTable;

	public String getEstruturaNomeCompleto() {
		return estruturaNomeCompleto;
	}

	public void setEstruturaNomeCompleto(String estruturaNomeCompleto) {
		this.estruturaNomeCompleto = estruturaNomeCompleto;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TipoTransito getTipoTransito() {
		return tipoTransito;
	}

	public void setTipoTransito(TipoTransito tipoTransito) {
		this.tipoTransito = tipoTransito;
	}

	public long getIdRec() {
		return idRec;
	}

	public void setIdRec(long idRec) {
		this.idRec = idRec;
	}

	public String getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(String recuperando) {
		this.recuperando = recuperando;
	}

	public Date getDataPrevista() {
		return dataPrevista;
	}

	public void setDataPrevista(Date dataPrevista) {
		this.dataPrevista = dataPrevista;
	}
	

	public String getEstruturaNome() {
		return estruturaNome;
	}

	public void setEstruturaNome(String estruturaNome) {
		this.estruturaNome = estruturaNome;
	}

	public String getNomeUnidadeDestino() {
		return nomeUnidadeDestino;
	}

	public void setNomeUnidadeDestino(String nomeUnidadeDestino) {
		this.nomeUnidadeDestino = nomeUnidadeDestino;
	}

	public String getEstabelecimentoNome() {
		return estabelecimentoNome;
	}

	public void setEstabelecimentoNome(String estabelecimentoNome) {
		this.estabelecimentoNome = estabelecimentoNome;
	}

	public StatusPedido getStatus() {
		return status;
	}

	public void setStatus(StatusPedido status) {
		this.status = status;
	}

	public String getNomeUnidadeOrigem() {
		return nomeUnidadeOrigem;
	}

	public void setNomeUnidadeOrigem(String nomeUnidadeOrigem) {
		this.nomeUnidadeOrigem = nomeUnidadeOrigem;
	}

	public Long getIdTransferencia() {
		return idTransferencia;
	}

	public void setIdTransferencia(Long idTransferencia) {
		this.idTransferencia = idTransferencia;
	}

	public String getEstruturaOrigemNome() {
		return estruturaOrigemNome;
	}

	public void setEstruturaOrigemNome(String estruturaOrigemNome) {
		this.estruturaOrigemNome = estruturaOrigemNome;
	}

	public String getEstruturaDestinoNomeCompleto() {
		return estruturaDestinoNomeCompleto;
	}

	public void setEstruturaDestinoNomeCompleto(String estruturaDestinoNomeCompleto) {
		this.estruturaDestinoNomeCompleto = estruturaDestinoNomeCompleto;
	}

	public String getDestinoParaExibicaoDataTable() {
		switch (tipoTransito) {
			case EXTERNA:
				return estabelecimentoNome;
			case TRANSFERENCIA:
				return nomeUnidadeDestino;
			default:
				return null;
		}
	}

	@Override
	public String toString() {
		return "Pedido Transito [id=" + id + "]";
	}		
		
}