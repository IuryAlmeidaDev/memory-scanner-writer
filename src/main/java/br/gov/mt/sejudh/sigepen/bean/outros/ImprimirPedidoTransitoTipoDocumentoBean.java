package br.gov.mt.sejudh.sigepen.bean.outros;

import br.gov.mt.sejudh.sigepen.bean.FileDownloadHandlerBean;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDocumentoPedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.PedidoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.MaskUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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
			Pessoa pessoa = this.pedido.getRecuperando().getPessoa();
			boolean temPai = pessoa.getNomePai() != null && !pessoa.getNomePai().trim().isEmpty();
			boolean temNomeSocial = pessoa.getNomeSocial() != null && !pessoa.getNomeSocial().trim().isEmpty();

			StringBuilder buildResultado = new StringBuilder();
			buildResultado.append(String.format(
					"Em atenção aos termos de Ofício %s, datado de %s, apresento o(a) interno(a) %s",
					this.pedido.getNumeroDocumento(),
					MaskUtil.getFormatoDataHora().format(this.pedido.getDataDocumento()),
					pessoa.getNome().toUpperCase()
			));

			if (temNomeSocial) {
				buildResultado.append(String.format(", que atende pelo nome social %s", pessoa.getNomeSocial().toUpperCase()));
			}

			buildResultado.append(String.format(", prontuário número %s, filho(a) de %s",
					this.pedido.getRecuperando().getId(),
					pessoa.getNomeMae().toUpperCase()
			));

			if (temPai) {
				buildResultado.append(String.format(" e de %s", pessoa.getNomePai().toUpperCase()));
			}

			buildResultado.append(String.format(", nascido(a) em %s, devidamente escoltado(a) em %s a fim de %s.",
					MaskUtil.getFormatoData().format(pessoa.getDataNascimento()),
					MaskUtil.getFormatoDataHora().format(this.pedido.getDataPrevista()),
					this.pedido.getMotivo()
			));

			this.pedido.setResultadoParaRelatorio(buildResultado.toString());

			Foto fotoAtual = this.pedido.getRecuperando().getFotoFrenteAtual();
			this.pedido.getRecuperando().getFotos().clear();
			this.pedido.getRecuperando().getFotos().add(fotoAtual);

			lista.add(this.pedido);

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.RegistroOficioPedidoTransito));
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
			Pessoa pessoa = this.pedido.getRecuperando().getPessoa();
			boolean temPai = pessoa.getNomePai() != null && !pessoa.getNomePai().trim().isEmpty();
			boolean temEstabelecimento = this.pedido.getEstabelecimento() != null;
			boolean temNomeSocial = pessoa.getNomeSocial() != null && !pessoa.getNomeSocial().trim().isEmpty();

			StringBuilder buildResultado = new StringBuilder();

			buildResultado.append(String.format("Autorizo o interno(a), %s", pessoa.getNome().toUpperCase()));

			if (temNomeSocial) {
				buildResultado.append(String.format(", que atende pelo nome social %s", pessoa.getNomeSocial().toUpperCase()));
			}

			buildResultado.append(String.format(", prontuário número %s, filho(a) de %s",
					this.pedido.getRecuperando().getId(),
					pessoa.getNomeMae().toUpperCase()
			));

			if (temPai) {
				buildResultado.append(String.format(" e de %s", pessoa.getNomePai().toUpperCase()));
			}

			buildResultado.append(String.format(", nascido(a) em %s, a mudança de Estrutura (Cela) desta Unidade devidamente escoltado(a) em %s fim de %s",
					MaskUtil.getFormatoData().format(pessoa.getDataNascimento()),
					MaskUtil.getFormatoDataHora().format(this.pedido.getDataPrevista()),
					this.pedido.getMotivo()
			));

			if (temEstabelecimento) {
				buildResultado.append(String.format(" no seguinte local, %s",
						WordUtils.capitalizeFully(this.pedido.getEstabelecimento().getNome())
				));
			}

			buildResultado.append(".");

			this.pedido.setResultadoParaRelatorio(buildResultado.toString());

			Foto fotoAtual = this.pedido.getRecuperando().getFotoFrenteAtual();
			this.pedido.getRecuperando().getFotos().clear();
			this.pedido.getRecuperando().getFotos().add(fotoAtual);

			lista.add(this.pedido);

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.RegistroAutorizacaoPedidoTransito));
			lista.clear();
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Autorização Pedido de Trânsito", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Autorização Pedido de Trânsito", e, log);
		}
	}
}
