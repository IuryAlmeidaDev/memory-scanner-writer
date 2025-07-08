package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.bean.FileDownloadHandlerBean;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDocumentoPedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.PedidoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.MaskUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "ImprimirPedidoTransitoTipoDocumento", pattern = "/pedido-de-transito/imprimir", viewId = "/WEB-INF/pages/_dialogs/ImprimirPedidoTransitoTipoDocumento.xhtml")
public class ImprimirPedidoTransitoTipoDocumentoBean extends MetodosDialogBean {
	PedidoTransitoDTO pedidoTransito;
	
	PedidoTransito pedido;
	
	List<PedidoTransito> lista;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private PedidoService pedidoService;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;
	
	@Inject
	private Logger log;
	
	@URLAction(onPostback=false)
	public String init() {
		if (this.isDialogInvalida()) {
			return "pretty:dashboard";
		}
		
		pedidoTransito = (PedidoTransitoDTO) facesUtil.getObjetoSessao("pedidoTransito", true);
		pedido = new PedidoTransito();
		lista = new ArrayList<PedidoTransito>();
		
		return null;
	}
	
	public void imprimirRegistro() {
		try {
			this.pedido = pedidoService.buscaPedidoTransitoImpressaoPorId(this.pedidoTransito.getId());
			
			lista.add(this.pedido);
			
			LoggerUtil.logInfo(Acao.IMPRIMIR, pedidoTransito, log);
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.RegistroPedido));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, pedidoTransito, log);
			lista.clear();
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir registro do pedido (" + this.pedido.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, pedidoTransito, e, log);
		}
	}
	
	public void selecionarOficioAutoridade() {
		this.pedido = pedidoService.buscaPedidoTransitoImpressaoPorId(this.pedidoTransito.getId());
		
		if (this.pedido.getTipoDocumentoPedidoTransito() == TipoDocumentoPedidoTransito.OFICIO) {
			this.imprimirOficioPedidoTransito();
		} else {
			this.imprimirAutorizacaoPedidoTransito();
		}
	}
	
	public void imprimirOficioPedidoTransito() {
		try {
			boolean temPai = (this.pedido.getRecuperando().getPessoa().getNomePai() != null);
			StringBuilder buildResultado = new StringBuilder();
			buildResultado.append("Em atenção aos termos de Ofício [numeroDocumento], datado de [dataDocumento], apresento o(a) interno(a) [nome], que atende pelo nome social, [nomeSocial]"
					+ ", prontuário número [recuperandoId], filho(a) de [nomeMae]");
			
			if (temPai)
				buildResultado.append(" e de [nomePai]");
			
			buildResultado.append(", nascido(a) em [dataNascimento], devidamente escoltado(a) em [dataPrevista] a fim de [motivo].");
			
			String resultado = buildResultado.toString();
			resultado = resultado.replace("[numeroDocumento]", this.pedido.getNumeroDocumento());
			resultado = resultado.replace("[nome]", this.pedido.getRecuperando().getPessoa().getNome().toUpperCase());
			resultado = resultado.replace("[nomeSocial]", this.pedido.getRecuperando().getPessoa().getNomeSocial().toUpperCase());
			resultado = resultado.replace("[recuperandoId]", String.valueOf(this.pedido.getRecuperando().getId()));
			resultado = resultado.replace("[nomeMae]", this.pedido.getRecuperando().getPessoa().getNomeMae().toUpperCase());
			resultado = resultado.replace("[dataNascimento]", MaskUtil.getFormatoData().format(this.pedido.getRecuperando().getPessoa().getDataNascimento()));
			resultado = resultado.replace("[dataDocumento]", MaskUtil.getFormatoData().format(this.pedido.getDataDocumento()));
			resultado = resultado.replace("[dataPrevista]", MaskUtil.getFormatoDataHora().format(this.pedido.getDataPrevista()));
			resultado = resultado.replace("[motivo]", this.pedido.getMotivo());
			
			if (temPai)
				resultado = resultado.replace("[nomePai]", this.pedido.getRecuperando().getPessoa().getNomePai().toUpperCase());
			
			this.pedido.setResultadoParaRelatorio(resultado);
			
			Foto fotoAtual = this.pedido.getRecuperando().getFotoFrenteAtual();
			
			this.pedido.getRecuperando().getFotos().clear();
			this.pedido.getRecuperando().getFotos().add(fotoAtual);
			
			lista.add(this.pedido);
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista,Relatorio.RegistroOficioPedidoTransito));
			lista.clear();
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Ofício Pedido de Trânsito", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Ofício Pedido de Trânsito", e, log);
		}
	}
	
	public void imprimirAutorizacaoPedidoTransito() {
		try {
			boolean temPai = (this.pedido.getRecuperando().getPessoa().getNomePai() != null);
			boolean temEstabelecimento = (this.pedido.getEstabelecimento() != null);
			StringBuilder buildResultado = new StringBuilder();
			buildResultado.append("Autorizo o interno(a), [nome], que atende pelo nome social, [nomeSocial], prontuário número [recuperandoId], filho(a) de [nomeMae]");

			if(this.pedido.getRecuperando().getPessoa().getNomeSocial() != null) {
			    buildResultado.append("Autorizo o interno(a), [nome], que atende pelo nome social, [nomeSocial], prontuário número [recuperandoId], filho(a) de [nomeMae]");
			} else {
			    buildResultado.append("Autorizo o interno(a), [nome], prontuário número [recuperandoId], filho(a) de [nomeMae]");
			}

			if (temPai)
				buildResultado.append(" e de [nomePai]");
			
			buildResultado.append(", nascido(a) em [dataNascimento], a mudança de Estrutura (Cela) desta Unidade devidamente escoltado(a) em [dataPrevista] fim de [motivo]");
			
			if (temEstabelecimento)
				buildResultado.append(" no seguinte local, [estabelecimento]");
			
			buildResultado.append(".");
			
			String resultado = buildResultado.toString();

			if (this.pedido.getRecuperando().getPessoa().getNomeSocial() != null) {
			    resultado = resultado.replace("[nome]", this.pedido.getRecuperando().getPessoa().getNome().toUpperCase());
			    resultado = resultado.replace("[nomeSocial]", this.pedido.getRecuperando().getPessoa().getNomeSocial().toUpperCase());
			} else {
			    resultado = resultado.replace("[nome]", this.pedido.getRecuperando().getPessoa().getNome().toUpperCase());
			}


			resultado = resultado.replace("[recuperandoId]", String.valueOf(this.pedido.getRecuperando().getId()));
			resultado = resultado.replace("[nomeMae]", this.pedido.getRecuperando().getPessoa().getNomeMae().toUpperCase());
			
			resultado = resultado.replace("[dataNascimento]", MaskUtil.getFormatoData().format(this.pedido.getRecuperando().getPessoa().getDataNascimento()));
			resultado = resultado.replace("[dataPrevista]", MaskUtil.getFormatoDataHora().format(this.pedido.getDataPrevista()));
			resultado = resultado.replace("[motivo]", this.pedido.getMotivo());
			
			if (temPai)
				resultado = resultado.replace("[nomePai]", this.pedido.getRecuperando().getPessoa().getNomePai().toUpperCase());
			
			if (temEstabelecimento)
				resultado = resultado.replace("[estabelecimento]", WordUtils.capitalizeFully(this.pedido.getEstabelecimento().getNome()));

			this.pedido.setResultadoParaRelatorio(resultado);

			Foto fotoAtual = this.pedido.getRecuperando().getFotoFrenteAtual();
			
			this.pedido.getRecuperando().getFotos().clear();
			this.pedido.getRecuperando().getFotos().add(fotoAtual);
			
			lista.add(this.pedido);
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista,Relatorio.RegistroAutorizacaoPedidoTransito));
			lista.clear();
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Autorização Pedido de Trânsito", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Autorização Pedido de Trânsito", e, log);
		}
	}
}
