package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
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
	
	private String estruturaDestinoNomeCompletoComUnidade;
	
	private Date dataInicio;
	
	private Date dataConclusao;
	
	private String observacao;
	
	private Date dataTerminoMovInt;
	
	private Date dataTerminoMovExt;
	
	private Date dataTerminoTransf;
	
	private Date dataDocumento;
	
	private String numeroDocumento;
	
	private String motivo;
	
	private String resultado;
		
	private FiltroPedTransito filtro;
	
	@SuppressWarnings("unused")
	private String destinoParaExibicaoDataTable;
	
	private String nomeSocial;

	
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

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataConclusao() {
		return dataConclusao;
	}

	public void setDataConclusao(Date dataConclusao) {
		this.dataConclusao = dataConclusao;
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

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Date getDataTerminoMovInt() {
		return dataTerminoMovInt;
	}

	public void setDataTerminoMovInt(Date dataTerminoMovInt) {
		this.dataTerminoMovInt = dataTerminoMovInt;
	}

	public Date getDataTerminoMovExt() {
		return dataTerminoMovExt;
	}

	public void setDataTerminoMovExt(Date dataTerminoMovExt) {
		this.dataTerminoMovExt = dataTerminoMovExt;
	}

	public Date getDataTerminoTransf() {
		return dataTerminoTransf;
	}

	public void setDataTerminoTransf(Date dataTerminoTransf) {
		this.dataTerminoTransf = dataTerminoTransf;
	}

	public String getEstruturaDestinoNomeCompletoComUnidade() {
		return estruturaDestinoNomeCompletoComUnidade;
	}

	public void setEstruturaDestinoNomeCompletoComUnidade(String estruturaDestinoNomeCompletoComUnidade) {
		this.estruturaDestinoNomeCompletoComUnidade = estruturaDestinoNomeCompletoComUnidade;
	}
	
	
	@Override
	public String toString() {
		return "Pedido Transito [id=" + id + "]";
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public Date getDataDocumento() {
		return dataDocumento;
	}

	public void setDataDocumento(Date dataDocumento) {
		this.dataDocumento = dataDocumento;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

	public FiltroPedTransito getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroPedTransito filtro) {
		this.filtro = filtro;
	}

	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}
		

}