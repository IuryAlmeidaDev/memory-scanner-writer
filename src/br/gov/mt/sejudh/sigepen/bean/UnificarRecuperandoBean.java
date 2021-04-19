package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.biometrics.BiometriaSDKDao;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnificarRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.model.Alcunha;
import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.model.Documento;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.HistoricoRecuperando;
import br.gov.mt.sejudh.sigepen.model.OutroNome;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Particularidade;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Processo;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.dto.DadosRecuperandoUnificacaoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.ExtratoRecuperandoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RecuperandoDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.BiometriaService;
import br.gov.mt.sejudh.sigepen.service.HistoricoRecuperandoService;
import br.gov.mt.sejudh.sigepen.service.PassagemService;
import br.gov.mt.sejudh.sigepen.service.PedidoService;
import br.gov.mt.sejudh.sigepen.service.PessoaService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

@ManagedBean
@SessionScoped
@URLMappings(mappings = {
		@URLMapping(id = "ListarUnificarRecuperando", pattern = "/recuperando/listar", viewId = "/WEB-INF/pages/recuperando/ListarUnificarRecuperando.xhtml"),
		@URLMapping(id = "UnificarRecuperando", pattern = "/recuperando/unificar", viewId = "/WEB-INF/pages/recuperando/UnificarRecuperando.xhtml")

})
public class UnificarRecuperandoBean extends MetodosDePaginaBean {

    private List<RecuperandoDTO> recuperandosSelecionados;
    
	private RecuperandoDTO recuperandoSelecionado1;
	
	private Pessoa pessoaRecuperandoSelecionado1;
	
	private Recuperando recuperando1;
	
	private List<Documento> documentosRecuperando1;
    
    private RecuperandoDTO recuperandoSelecionado2;
    
	private Pessoa pessoaRecuperandoSelecionado2;
	
	private Recuperando recuperando2;
	
	private List<Documento> documentosRecuperando2;
    
	private DadosRecuperandoUnificacaoDTO dadosRecuperandoUnificacaoDTO;

	@Inject
	private PedidoService pedidoService;
	
	@Inject
	private HistoricoRecuperandoService historicoRecuperandoService;
	
	@Inject
	private BiometriaSDKDao bioDao;
	
	private FiltroUnificarRecuperando filtro;
	
	private LazyDataModel<RecuperandoDTO> recuperandos;
	
	private List<RecuperandoDTO> recuperandoSimilares;
			
	private Passagem passagemSelecionada;
		
	private Processo processo;
	
	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;

	@Inject
	private RecuperandoService service;

	@Inject
	private PessoaService pessoaService;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;
		
	@Inject
	private PassagemService passagemService;
			
	@Inject
	private BiometriaService bioService;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void configurarFiltroPaginacao() throws Exception {
		if (getAcaoDeTela().equals(AcaoDeTela.LISTAR)) {
			FiltroUnificarRecuperando filtroDash = (FiltroUnificarRecuperando)facesUtil.getFlashParameter("filtroRecuperandoDash");
			
			if (filtroDash != null) {
				setFiltro(filtroDash);
			} else {
				super.carregarFiltroPaginacao(FiltroUnificarRecuperando.class, this);
			}
		}
	}
	
	public String executarUnificacao() {
		//RN01 - O Sistema deve automaticamente selecionar o número de matrícula que for mais antigo (menor número de matrícula)
		this.dadosRecuperandoUnificacaoDTO = new DadosRecuperandoUnificacaoDTO();
		if(recuperandoSelecionado1.getId() < recuperandoSelecionado2.getId()) {
			dadosRecuperandoUnificacaoDTO.setIdSelecionado(recuperandoSelecionado1.getId());
		}else {
			dadosRecuperandoUnificacaoDTO.setIdSelecionado(recuperandoSelecionado2.getId());
		}
		
		pessoaRecuperandoSelecionado1 = pessoaService.buscarDadosPessoaRecuperandoPorId(recuperandoSelecionado1.getPessoaId());
		pessoaRecuperandoSelecionado2 = pessoaService.buscarDadosPessoaRecuperandoPorId(recuperandoSelecionado2.getPessoaId());
		
		this.recuperando1 = service.buscarRecuperandoPorId(recuperandoSelecionado1.getId(), false);
		this.recuperando2 = service.buscarRecuperandoPorId(recuperandoSelecionado2.getId(), false);
		
		this.documentosRecuperando1 = new ArrayList<Documento>();
		this.documentosRecuperando2 = new ArrayList<Documento>();
		
		//E03. Recuperando com passagens ativas
		if(recuperando1.getStatus().equals(StatusRecuperando.ATIVO) && recuperando2.getStatus().equals(StatusRecuperando.ATIVO)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Não é permitido unificar o cadastro de recuperando com passagens ativas! Favor verificar!");
			return "";
		}	
		
//		//E04. Recuperando Com Biometria Distintas
		String msg = validaBiometriaDistintas();
		if(msg != null) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",msg);
			return "";
		}
		
		//E05. Recuperando com passagens concomitantes
		if(!validaPassagensConcomitantes()) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Não é permitido unificar cadastros de recuperando que possuem passagens concomitantes!");
			return "";
		}
		
		//E06. Recuperando com pedido de trânsito em andamento (Recuperando 1)
		if(!validaPedidoTransitoAndamento(recuperando1.getId())) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Não é possível unificar os cadastros deste recuperando pois o recuperando "+recuperando1.getPessoa().getNome()+" possui pedido de trânsito em andamento!");
			return "";
		}
		//E06. Recuperando com pedido de trânsito em andamento (Recuperando 2)
		if(!validaPedidoTransitoAndamento(recuperando2.getId())) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Não é possível unificar os cadastros deste recuperando pois o recuperando "+recuperando2.getPessoa().getNome()+" possui pedido de trânsito em andamento!");
			return "";
		}
		
		return "pretty:UnificarRecuperando";
	}
	
	public boolean validaPedidoTransitoAndamento(Long recuperandoId) {
		FiltroPedTransito filtroPedTransito = new FiltroPedTransito();
		filtroPedTransito.setRecuperandoId(recuperandoId);
		filtroPedTransito.setStatus(StatusPedido.EM_ANDAMENTO);
		List<PedidoTransitoDTO> listPedidoTransito = pedidoService.buscaPorFiltro(filtroPedTransito, false);
		
		return !(listPedidoTransito != null && !listPedidoTransito.isEmpty() && listPedidoTransito.size() > 0);
	}
	
	private String validaBiometriaDistintas() {
		try {
			if (pessoaRecuperandoSelecionado1.getBiometria() == null || pessoaRecuperandoSelecionado2.getBiometria() == null)
				return null;
			else {
			
				if(!bioDao.verificarPorTemplate(pessoaRecuperandoSelecionado1.getBiometria().getTemplate(),pessoaRecuperandoSelecionado2.getBiometria().getTemplate())) {
					return "Não é permitido unificar cadastros de recuperando que possuem biometrias diferentes!";
				}
				else {
					return null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Não foi possível verificar as biometrias.";
		}
		
	}

	
	/**
	 * Validação de passagens concomitantes
	 * 
	 * É considerado passagen concomitante quando a data de entrada ou saída de uma passagem de um cadastro de recuperando estiver em interseção com as datas de entrada e saída de uma passagem do outro cadastro (ou seja, uma data não pode estar contida na outra).
	 * 
	 * @return false se existem passagens concomitantes e true se não existem passagens concomitantes
	 */
	private boolean validaPassagensConcomitantes() {
		for(Passagem passagemRecuperando1 : recuperando1.getPassagens()) {
			for(Passagem passagemRecuperando2 : recuperando2.getPassagens()) {
				if (passagemRecuperando1.getDataSaida() != null && (passagemRecuperando2.getDataEntrada().after(passagemRecuperando1.getDataEntrada()) && passagemRecuperando2.getDataEntrada().before(passagemRecuperando1.getDataSaida()))) {
					return false;
				}else if (passagemRecuperando2.getDataSaida() != null && passagemRecuperando1.getDataSaida() != null && (passagemRecuperando2.getDataSaida().after(passagemRecuperando1.getDataEntrada()) && passagemRecuperando2.getDataSaida().before(passagemRecuperando1.getDataSaida()))) {
					return false;
				}
			}
		}
		for(Passagem passagemRecuperando2 : recuperando2.getPassagens()) {
			for(Passagem passagemRecuperando1 : recuperando1.getPassagens()) {
				if (passagemRecuperando2.getDataSaida() != null && (passagemRecuperando1.getDataEntrada().after(passagemRecuperando2.getDataEntrada()) && passagemRecuperando1.getDataEntrada().before(passagemRecuperando2.getDataSaida()))) {
					return false;
				}else if (passagemRecuperando1.getDataSaida() != null && passagemRecuperando2.getDataSaida() != null && (passagemRecuperando1.getDataSaida().after(passagemRecuperando2.getDataEntrada()) && passagemRecuperando1.getDataSaida().before(passagemRecuperando2.getDataSaida()))) {
					return false;
				}
			}
		}
		return true;
	}
	
	public List<Documento> getDocumentosRecuperando1() {
		if (this.recuperando1.getId() > 0) {
			this.documentosRecuperando1.clear();
			this.documentosRecuperando1.addAll(this.recuperando1.getPessoa().getDocumentos());
			this.documentosRecuperando1.addAll(this.recuperando1.getDocumentos());
		}

		return documentosRecuperando1;
	}
	
	public List<Documento> getDocumentosRecuperando2() {
		if (this.recuperando2.getId() > 0) {
			this.documentosRecuperando2.clear();
			this.documentosRecuperando2.addAll(this.recuperando2.getPessoa().getDocumentos());
			this.documentosRecuperando2.addAll(this.recuperando2.getDocumentos());
		}

		return documentosRecuperando2;
	}
	
	public String onFlowProcess(FlowEvent event) {
            return event.getNewStep();
    }
	
	public String salvarUnificacao() {        
        
		//RN01 - O Sistema deve automaticamente selecionar o número de matrícula que for mais antigo (menor número de matrícula)
		if (getRecuperando1().getId() < getRecuperando2().getId()) {
			unificarDadosPessoais(getRecuperando1());
			unificarAlcunha(getRecuperando1(), getRecuperando2());
			unificarPassagens(getRecuperando1());
			unificarVidaSocial(getRecuperando1());
			unificarFotos(getRecuperando1());
			unificarParticularidades(getRecuperando1());
			unificarOutrosNomes(getRecuperando1());
			unificarDocumentos(getRecuperando1(), getRecuperando2());
			unificarEnderecos(getRecuperando1());
			unificarContatos(getRecuperando1());
			unificarParentes(getRecuperando1());
			unificarPedidoTransito(getRecuperando1());
			unificarHistoricoRecuperando(getRecuperando1());
			unificarAutorizacaoCadastro(getRecuperando1());
			unificarStatusRecuperando(getRecuperando1());

			try {
				service.salvarRecuperandoUnificacao(getRecuperando1());
				service.excluirRecuperandoUnificacao(getRecuperando2());
				if(getRecuperando1().getPessoa().getBiometria() != null && getRecuperando1().getPessoa().getBiometria().getTemplate() != null)
					bioService.excluirBiometriaSDK(getRecuperando1().getPessoa().getBiometria());
				if(getRecuperando2().getPessoa().getBiometria() != null && getRecuperando2().getPessoa().getBiometria().getTemplate() != null)
					bioService.excluirBiometriaSDK(getRecuperando2().getPessoa().getBiometria());
				bioService.salvarBiometriaSDK(getRecuperando1().getPessoa().getBiometria());
				service.atualizarRegimeCondicaoAtualRecuperando(getRecuperando1());
				limparRecuperandosUnificacao();

				facesUtil.exibirMensagemInfo("Recuperando Unificado com Sucesso!", true);

				return "pretty:ListarUnificarRecuperando";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			unificarDadosPessoais(getRecuperando2());
			unificarAlcunha(getRecuperando2(), getRecuperando1());
			unificarPassagens(getRecuperando2());
			unificarVidaSocial(getRecuperando2());
			unificarFotos(getRecuperando2());
			unificarParticularidades(getRecuperando2());
			unificarOutrosNomes(getRecuperando2());
			unificarDocumentos(getRecuperando2(), getRecuperando1());
			unificarEnderecos(getRecuperando2());
			unificarContatos(getRecuperando2());
			unificarParentes(getRecuperando2());
			unificarPedidoTransito(getRecuperando2());
			unificarHistoricoRecuperando(getRecuperando2());
			unificarAutorizacaoCadastro(getRecuperando2());
			unificarStatusRecuperando(getRecuperando2());

			try {
				service.salvarRecuperandoUnificacao(getRecuperando2());
				service.excluirRecuperandoUnificacao(getRecuperando1());
				if(getRecuperando1().getPessoa().getBiometria() != null && getRecuperando1().getPessoa().getBiometria().getTemplate() != null)
					bioService.excluirBiometriaSDK(getRecuperando1().getPessoa().getBiometria());
				if(getRecuperando2().getPessoa().getBiometria() != null && getRecuperando2().getPessoa().getBiometria().getTemplate() != null)
					bioService.excluirBiometriaSDK(getRecuperando2().getPessoa().getBiometria());
				bioService.salvarBiometriaSDK(getRecuperando2().getPessoa().getBiometria());
				service.atualizarRegimeCondicaoAtualRecuperando(getRecuperando2());
				limparRecuperandosUnificacao();

				facesUtil.exibirMensagemInfo("Recuperando Unificado com Sucesso!", true);
				
				return "pretty:ListarUnificarRecuperando";

			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erro", "Erro ao salvar unificação do Recuperando!");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		
		return "";
		
    }
	
	public void limparRecuperandosUnificacao() {
	    recuperandosSelecionados = new ArrayList<RecuperandoDTO>();
		recuperandoSelecionado1 = null;
		pessoaRecuperandoSelecionado1 = new Pessoa();
		recuperando1 = new Recuperando();
		documentosRecuperando1 = new ArrayList<Documento>();
		
	    recuperandoSelecionado2 = null;
	    pessoaRecuperandoSelecionado2 = new Pessoa();
		recuperando2 = new Recuperando();
		documentosRecuperando2 = new ArrayList<Documento>();
	}
	
	private void unificarStatusRecuperando(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId() && !recuperando.getStatus().equals(StatusRecuperando.ATIVO) && getRecuperando1().getStatus().equals(StatusRecuperando.ATIVO)) {
			recuperando.setStatus(StatusRecuperando.ATIVO);
		} else if (getRecuperando2().getId() != recuperando.getId() && !recuperando.getStatus().equals(StatusRecuperando.ATIVO) && getRecuperando2().getStatus().equals(StatusRecuperando.ATIVO)) {
			recuperando.setStatus(StatusRecuperando.ATIVO);
		}
	}
	private void unificarPedidoTransito(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			FiltroPedTransito filtroPedTransito = new FiltroPedTransito();
			filtroPedTransito.setRecuperandoId(getRecuperando1().getId());
			List<PedidoTransitoDTO> listPedidoTransito = pedidoService.buscaPorFiltro(filtroPedTransito, false);
			
			for(PedidoTransitoDTO pedidoTransitoDTO : listPedidoTransito) {
				PedidoTransito pedidoTransito = pedidoService.buscaPedidoPorId(pedidoTransitoDTO.getId());
				pedidoTransito.setRecuperando(recuperando);
				pedidoService.salvar(pedidoTransito);
			}
			
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			FiltroPedTransito filtroPedTransito = new FiltroPedTransito();
			filtroPedTransito.setRecuperandoId(getRecuperando2().getId());
			List<PedidoTransitoDTO> listPedidoTransito = pedidoService.buscaPorFiltro(filtroPedTransito, false);
			
			for(PedidoTransitoDTO pedidoTransitoDTO : listPedidoTransito) {
				PedidoTransito pedidoTransito = pedidoService.buscaPedidoPorId(pedidoTransitoDTO.getId());
				pedidoTransito.setRecuperando(recuperando);
				pedidoService.salvar(pedidoTransito);
			}
		}
	}
	
	private void unificarHistoricoRecuperando(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			List<HistoricoRecuperando> listHistoricoRecuperandos = historicoRecuperandoService.listarHistoricoRecuperando(getRecuperando1().getId());
			
			for(HistoricoRecuperando historicoRecuperando : listHistoricoRecuperandos) {
				historicoRecuperando.setRecuperando(recuperando);
				historicoRecuperandoService.salvar(historicoRecuperando);
			}
			
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			List<HistoricoRecuperando> listHistoricoRecuperandos = historicoRecuperandoService.listarHistoricoRecuperando(getRecuperando2().getId());
			
			for(HistoricoRecuperando historicoRecuperando : listHistoricoRecuperandos) {
				historicoRecuperando.setRecuperando(recuperando);
				historicoRecuperandoService.salvar(historicoRecuperando);
			}
		}
	}
	
	private void unificarAutorizacaoCadastro(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			if(recuperando.getPessoa().getAutorizacaoCadastro() == null) {
				recuperando.getPessoa().setAutorizacaoCadastro(getRecuperando1().getPessoa().getAutorizacaoCadastro());
			}
			
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			if(recuperando.getPessoa().getAutorizacaoCadastro() == null) {
				recuperando.getPessoa().setAutorizacaoCadastro(getRecuperando2().getPessoa().getAutorizacaoCadastro());
			}
		}
	}
	
	private void unificarDadosPessoais(Recuperando recuperando) {
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdNomeNomeMaeDataNascimento()) {
			recuperando.getPessoa().setNome(getRecuperando1().getPessoa().getNome());
			recuperando.getPessoa().setNomeMae(getRecuperando1().getPessoa().getNomeMae());
			recuperando.getPessoa().setDataNascimento(getRecuperando1().getPessoa().getDataNascimento());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdNomeNomeMaeDataNascimento()) {
			recuperando.getPessoa().setNome(getRecuperando2().getPessoa().getNome());
			recuperando.getPessoa().setNomeMae(getRecuperando2().getPessoa().getNomeMae());
			recuperando.getPessoa().setDataNascimento(getRecuperando2().getPessoa().getDataNascimento());
		}
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdSexo()) {
			recuperando.getPessoa().setSexo(getRecuperando1().getPessoa().getSexo());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdSexo()) {
			recuperando.getPessoa().setSexo(getRecuperando2().getPessoa().getSexo());
		}
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdNacionalidade()) {
			recuperando.getPessoa().setNacionalidade(getRecuperando1().getPessoa().getNacionalidade());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdNacionalidade()) {
			recuperando.getPessoa().setNacionalidade(getRecuperando2().getPessoa().getNacionalidade());
		}
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdNaturalidade()) {
			recuperando.getPessoa().setNaturalidade(getRecuperando1().getPessoa().getNaturalidade());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdNaturalidade()) {
			recuperando.getPessoa().setNaturalidade(getRecuperando2().getPessoa().getNaturalidade());
		}

		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdSinalTatuagem()) {
			recuperando.setTemSinalOuTatuagem(getRecuperando1().getTemSinalOuTatuagem());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdSinalTatuagem()) {
			recuperando.setTemSinalOuTatuagem(getRecuperando2().getTemSinalOuTatuagem());
		}

		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdAltura()) {
			recuperando.setAltura(getRecuperando1().getAltura());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdAltura()) {
			recuperando.setAltura(getRecuperando2().getAltura());
		}

		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdPeso()) {
			recuperando.setPeso(getRecuperando1().getPeso());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdPeso()) {
			recuperando.setPeso(getRecuperando2().getPeso());
		}

		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdTipoSanguineo()) {
			recuperando.setTipoSanguineo(getRecuperando1().getTipoSanguineo());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdTipoSanguineo()) {
			recuperando.setTipoSanguineo(getRecuperando2().getTipoSanguineo());
		}

		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdRaca()) {
			recuperando.setRaca(getRecuperando1().getRaca());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdRaca()) {
			recuperando.setRaca(getRecuperando2().getRaca());
		}
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdCorOlhos()) {
			recuperando.setCorOlho(getRecuperando1().getCorOlho());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdCorOlhos()) {
			recuperando.setCorOlho(getRecuperando2().getCorOlho());
		}
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdCorCabelo()) {
			recuperando.setCorCabelo(getRecuperando1().getCorCabelo());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdCorCabelo()) {
			recuperando.setCorCabelo(getRecuperando2().getCorCabelo());
		}

	}
	
	private void unificarFotos(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Foto foto : getRecuperando1().getFotos()) {
				foto.setRecuperando(recuperando);
				recuperando.getFotos().add(foto);
			}
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Foto foto : getRecuperando2().getFotos()) {
				foto.setRecuperando(recuperando);
				recuperando.getFotos().add(foto);
			}
		}
	}
	
	private void unificarParticularidades(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Particularidade particularidade : getRecuperando1().getParticularidades()) {
				particularidade.setRecuperando(recuperando);
				recuperando.getParticularidades().add(particularidade);
			}
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Particularidade particularidade : getRecuperando2().getParticularidades()) {
				particularidade.setRecuperando(recuperando);
				recuperando.getParticularidades().add(particularidade);
			}
		}
	}

	private void unificarPassagens(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Passagem passagem : getRecuperando1().getPassagens()) {
				passagem.setRecuperando(recuperando);
				recuperando.getPassagens().add(passagem);
			}
			
			for (Processo processo : getRecuperando1().getProcessos()) {
				processo.setRecuperando(recuperando);
				recuperando.getProcessos().add(processo);
			}
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Passagem passagem : getRecuperando2().getPassagens()) {
				passagem.setRecuperando(recuperando);
				recuperando.getPassagens().add(passagem);
			}
			
			for (Processo processo : getRecuperando2().getProcessos()) {
				processo.setRecuperando(recuperando);
				recuperando.getProcessos().add(processo);
			}
		}
	}
	
	private void unificarVidaSocial(Recuperando recuperando) {
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdOrientacaoSexual()) {
			recuperando.setOrientacaoSexual(getRecuperando1().getOrientacaoSexual());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdOrientacaoSexual()) {
			recuperando.setOrientacaoSexual(getRecuperando2().getOrientacaoSexual());
		}
		
		if (dadosRecuperandoUnificacaoDTO.getIdIdentidadeDeGenero() != null) {
			
			if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdIdentidadeDeGenero()) {
				recuperando.setIdentidadeDeGenero(getRecuperando1().getIdentidadeDeGenero());
			}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdIdentidadeDeGenero()) {
				recuperando.setIdentidadeDeGenero(getRecuperando2().getIdentidadeDeGenero());
			}
		}
		
		if (dadosRecuperandoUnificacaoDTO.getIdNomeSocial() != null) {
			
			if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdNomeSocial()) {
				recuperando.getPessoa().setNomeSocial(getRecuperando1().getPessoa().getNomeSocial());
			}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdNomeSocial()) {
				recuperando.getPessoa().setNomeSocial(getRecuperando2().getPessoa().getNomeSocial());
			}
		}
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdEstadoCivil()) {
			recuperando.setEstadoCivil(getRecuperando1().getEstadoCivil());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdEstadoCivil()) {
			recuperando.setEstadoCivil(getRecuperando2().getEstadoCivil());
		}
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdEscolaridade()) {
			recuperando.setEscolaridade(getRecuperando1().getEscolaridade());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdEscolaridade()) {
			recuperando.setEscolaridade(getRecuperando2().getEscolaridade());
		}
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdProfissao()) {
			recuperando.setProfissao(getRecuperando1().getProfissao());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdProfissao()) {
			recuperando.setProfissao(getRecuperando2().getProfissao());
		}
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdReligiao()) {
			recuperando.setReligiao(getRecuperando1().getReligiao());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdReligiao()) {
			recuperando.setReligiao(getRecuperando2().getReligiao());
		}
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdPne()) {
			recuperando.setPne(getRecuperando1().getPne());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdPne()) {
			recuperando.setPne(getRecuperando2().getPne());
		}
		
	}
	
    /* Verifica se o recuperando tem uma passagem mais recente que rec_retirar.
     */
	private boolean verificaPassagemMaisRecente(Recuperando recuperando, Recuperando rec_retirar) {
		boolean isRecente = true;
		for (Passagem passagem1 : recuperando.getPassagens()) {
			isRecente = true;
			for (Passagem passagem2 : rec_retirar.getPassagens()) {
				if(passagem1.getDataEntrada().before(passagem2.getDataEntrada())) {
					isRecente = false;
				}
			}
			if(isRecente) {
				break;
			}
		}
		
		return isRecente;
	}
	
	private void unificarAlcunha(Recuperando recuperando, Recuperando rec_retirar) {
		
		for (Alcunha alcunha1 : rec_retirar.getAlcunhas()) {
			boolean duplicada = false;
			Alcunha alcunha_dupl = null;
			
			for(Alcunha alcunha2 : recuperando.getAlcunhas()) {
				
				if(alcunha1.isPrincipal() && alcunha2.isPrincipal()) {
					if(verificaPassagemMaisRecente(recuperando, rec_retirar)) {
						alcunha1.setPrincipal(false);
					}else {
						alcunha2.setPrincipal(false);
					}
				}
				
				if(alcunha1.getNome().equalsIgnoreCase(alcunha2.getNome())) {
					duplicada = true;
					alcunha_dupl = alcunha2;
				}
			}
			
			if(!duplicada) {
				alcunha1.setRecuperando(recuperando);
				recuperando.getAlcunhas().add(alcunha1);
			}else if(alcunha1.isPrincipal()) {
				alcunha_dupl.setPrincipal(true);
			}
			
		}
	}
	
	private void unificarOutrosNomes(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (OutroNome outroNome1 : getRecuperando1().getOutrosNomes()) {
				Boolean duplicada = false;
				for(OutroNome outroNome2 : getRecuperando2().getOutrosNomes()) {
					if(outroNome1.getNome().equalsIgnoreCase(outroNome2.getNome()) && outroNome1.getNomeMae().equalsIgnoreCase(outroNome2.getNomeMae()) && outroNome1.getDataNascimento().compareTo(outroNome2.getDataNascimento()) == 0 && !duplicada) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					outroNome1.setRecuperando(recuperando);
					recuperando.getOutrosNomes().add(outroNome1);
				}
			}
			
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (OutroNome outroNome2 : getRecuperando2().getOutrosNomes()) {
				Boolean duplicada = false;
				for(OutroNome outroNome1 : getRecuperando1().getOutrosNomes()) {
					if(outroNome2.getNome().equalsIgnoreCase(outroNome1.getNome()) && outroNome2.getNomeMae().equalsIgnoreCase(outroNome1.getNomeMae()) && outroNome2.getDataNascimento().compareTo(outroNome1.getDataNascimento()) == 0 && !duplicada) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					outroNome2.setRecuperando(recuperando);
					recuperando.getOutrosNomes().add(outroNome2);
				}
			}
		}
	}
	
	
	private void unificarDocumentosList(boolean isPessoaList, Recuperando recuperando, List<Documento> listManter, Recuperando rec_retirar, List<Documento> listRetirar) {
		
		for (Documento documento1 : listRetirar) {
			boolean duplicada = false;
			for(Documento documento2 : listManter) {
					
				if(documento1.getTipoDocumento().getId() == documento2.getTipoDocumento().getId() && documento1.getNumero().equalsIgnoreCase(documento2.getNumero()) && !duplicada) {
					duplicada = true;
				}
			}
			if(!duplicada) {
								
				if(isPessoaList) {
					documento1.setPessoa(recuperando.getPessoa());
					if(recuperando.getPessoa().getDocumentos() != null) {
						recuperando.getPessoa().getDocumentos().add(documento1);						
					}else {
						recuperando.getPessoa().setDocumentos(new ArrayList<Documento>());
						recuperando.getPessoa().getDocumentos().add(documento1);						
					}
					
				}else {
					documento1.setRecuperando(recuperando);
					if(recuperando.getDocumentos() != null) {
						recuperando.getDocumentos().add(documento1);						
					}else {
						recuperando.setDocumentos(new ArrayList<Documento>());
						recuperando.getDocumentos().add(documento1);						
					}
				}
							
			}
		}
	}
	
	private void unificarDocumentos(Recuperando recuperando, Recuperando rec_retirar) {
		
		unificarDocumentosList(false, recuperando, recuperando.getDocumentos(), rec_retirar, rec_retirar.getDocumentos());
		unificarDocumentosList(true, recuperando, recuperando.getPessoa().getDocumentos(), rec_retirar, rec_retirar.getPessoa().getDocumentos());
		

	}
	
	private void unificarEnderecos(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Endereco endereco1 : getRecuperando1().getPessoa().getEnderecos()) {
				Boolean duplicada = false;
				for(Endereco endereco2 : getRecuperando2().getPessoa().getEnderecos()) {
					if(endereco1.getCidade().getEstado().getNome().equalsIgnoreCase(endereco2.getCidade().getEstado().getNome()) && endereco1.getCidade().getId() == endereco2.getCidade().getId()
							&& endereco1.getBairro().equalsIgnoreCase(endereco2.getBairro()) && endereco1.getLogradouro().equalsIgnoreCase(endereco2.getLogradouro())
							&& ((endereco1.getNumero() == null && endereco2.getNumero() == null) || (endereco1.getNumero().equalsIgnoreCase(endereco2.getNumero())))
							&& ((endereco1.getComplemento() == null && endereco2.getComplemento() == null) || (endereco1.getComplemento().equalsIgnoreCase(endereco2.getComplemento()))) ) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					endereco1.setPessoa(recuperando.getPessoa());
					if(recuperando.getPessoa().getEnderecos() != null) {
						recuperando.getPessoa().getEnderecos().add(endereco1);						
					}else {
						recuperando.getPessoa().setEnderecos(new ArrayList<Endereco>());
						recuperando.getPessoa().getEnderecos().add(endereco1);						
					}
				}
			}
			
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Endereco endereco2 : getRecuperando2().getPessoa().getEnderecos()) {
				Boolean duplicada = false;
				for(Endereco endereco1 : getRecuperando1().getPessoa().getEnderecos()) {
					if(endereco2.getCidade().getEstado().getNome().equalsIgnoreCase(endereco1.getCidade().getEstado().getNome()) && endereco2.getCidade().getId() == endereco1.getCidade().getId()
							&& endereco2.getBairro().equalsIgnoreCase(endereco1.getBairro()) && endereco2.getLogradouro().equalsIgnoreCase(endereco1.getLogradouro())
							&& ((endereco2.getNumero() == null && endereco1.getNumero() == null) || (endereco2.getNumero().equalsIgnoreCase(endereco1.getNumero())))
							&& ((endereco2.getComplemento() == null && endereco1.getComplemento() == null) || (endereco2.getComplemento().equalsIgnoreCase(endereco1.getComplemento()))) ) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					endereco2.setPessoa(recuperando.getPessoa());
					if(recuperando.getPessoa().getEnderecos() != null) {
						recuperando.getPessoa().getEnderecos().add(endereco2);						
					}else {
						recuperando.getPessoa().setEnderecos(new ArrayList<Endereco>());
						recuperando.getPessoa().getEnderecos().add(endereco2);						
					}
				}
			}
		}
	}
	
	private void unificarContatos(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Contato contato1 : getRecuperando1().getPessoa().getContatos()) {
				Boolean duplicada = false;
				for(Contato contato2 : getRecuperando2().getPessoa().getContatos()) {
					if(contato1.getNome().equalsIgnoreCase(contato2.getNome()) && contato1.getTipoContato().getDescricao().equalsIgnoreCase(contato2.getTipoContato().getDescricao()) && contato1.getValor().equalsIgnoreCase(contato2.getValor())  && !duplicada) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					contato1.setPessoa(recuperando.getPessoa());
					if(recuperando.getPessoa().getContatos() != null) {
						recuperando.getPessoa().getContatos().add(contato1);
					}else {
						recuperando.getPessoa().setContatos(new ArrayList<Contato>());
						recuperando.getPessoa().getContatos().add(contato1);
					}
				}
			}
			
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Contato contato2 : getRecuperando2().getPessoa().getContatos()) {
				Boolean duplicada = false;
				for(Contato contato1 : getRecuperando1().getPessoa().getContatos()) {
					if(contato2.getNome().equalsIgnoreCase(contato1.getNome()) && contato2.getTipoContato().getDescricao().equalsIgnoreCase(contato1.getTipoContato().getDescricao()) && contato2.getValor().equalsIgnoreCase(contato1.getValor())  && !duplicada) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					contato2.setPessoa(recuperando.getPessoa());
					if(recuperando.getPessoa().getContatos() != null) {
						recuperando.getPessoa().getContatos().add(contato2);
					}else {
						recuperando.getPessoa().setContatos(new ArrayList<Contato>());
						recuperando.getPessoa().getContatos().add(contato2);
					}
				}
			}
		}
	}
	
	private void unificarParentes(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Parente parente : getRecuperando1().getParentes()) {
				parente.setRecuperando(recuperando);
				recuperando.getParentes().add(parente);
			}
			if (recuperando.getAlteracaoParentes() != null && getRecuperando1().getAlteracaoParentes() != null && 
					recuperando.getAlteracaoParentes().before(getRecuperando1().getAlteracaoParentes())) {
				recuperando.setAlteracaoParentes(getRecuperando1().getAlteracaoParentes());
			}
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Parente parente : getRecuperando2().getParentes()) {
				parente.setRecuperando(recuperando);
				recuperando.getParentes().add(parente);
			}
			if (recuperando.getAlteracaoParentes() != null && getRecuperando2().getAlteracaoParentes() != null && 
					recuperando.getAlteracaoParentes().before(getRecuperando2().getAlteracaoParentes())) {
				recuperando.setAlteracaoParentes(getRecuperando2().getAlteracaoParentes());
			}
		}
	}
	


	/*
	 * Métodos da Listagem
	 */
	public void pesquisar() {
		if(getFiltro() != null && (getFiltro().getNome() == null || getFiltro().getNome().isEmpty()) && (getFiltro().getNomeMae() == null || getFiltro().getNomeMae().isEmpty())  && (getFiltro().getDataNascimento() == null) ) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Na consulta de recuperando é obrigatório informar pelo menos um dos filtros de consulta (Nome do Recuperando ou Nome da mãe ou Data de Nascimento)");
		}else {
		this.recuperandos = new LazyDataModel<RecuperandoDTO>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Object getRowKey(RecuperandoDTO object) {
				return object != null ? object.getId() : null;
			}

			@Override
			public RecuperandoDTO getRowData(String rowKey) {
				if (rowKey == null || rowKey.isEmpty()) {
					return null;
				}

				for (RecuperandoDTO rec : recuperandos) {
					if (rec.getId().equals(Long.valueOf(rowKey))) {
						service.buscarAlcunhasEOutrosNomes(rec);
						return rec;
					}
				}
				
				return null;
			}
			
			@Override
			public List<RecuperandoDTO> load(int first, int pageSize,
					String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				List<RecuperandoDTO> recuperandos = new ArrayList<RecuperandoDTO>();
				
				if(getFiltro() != null && (getFiltro().getNome() == null || getFiltro().getNome().isEmpty()) && (getFiltro().getNomeMae() == null || getFiltro().getNomeMae().isEmpty())  && (getFiltro().getDataNascimento() == null) ) {
					setRowCount(0);

					try {
						configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}else {
					
				setRowCount(service.buscarQuantidadeFiltrada(getFiltro()));
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

					recuperandos = service.buscarRecuperandosPorFiltro(getFiltro(), false);
				}
				
				return recuperandos;
			}
		};
		}
	}
	
	/**
	 * seleciona recuperando para unificação 
	 * 
	 * @param recuperandoDTO
	 */
	public void selecionarRecuperandoUnificar(RecuperandoDTO recuperandoDTO) {
		if (recuperandoSelecionado1 != null && recuperandoSelecionado1.equals(recuperandoDTO)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Não é possível selecionar duas vezes o mesmo recuperando para unificação!");
		}else if (recuperandoSelecionado2 != null && recuperandoSelecionado2.equals(recuperandoDTO)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Não é possível selecionar duas vezes o mesmo recuperando para unificação!");
		}else if (recuperandoSelecionado1 != null && recuperandoSelecionado2 != null) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Selecione no máximo dois registros para unificação!");
		} else if (recuperandoSelecionado1 == null) {
			this.recuperandoSelecionado1 = recuperandoDTO;
		} else if (recuperandoSelecionado2 == null) {
			this.recuperandoSelecionado2 = recuperandoDTO;
		}
	}
	
	/**
	 * Limpa seleção de recuperando para unificação
	 * 
	 * @param recuperandoDTO
	 */
	public void limparSelecaoRecuperando(RecuperandoDTO recuperandoDTO) {
		if(recuperandoSelecionado1 != null && recuperandoSelecionado1.equals(recuperandoDTO)) {
			this.recuperandoSelecionado1 = null;
		}else if(recuperandoSelecionado2 != null && recuperandoSelecionado2.equals(recuperandoDTO)) {
			this.recuperandoSelecionado2 = null;
		}
	}
	
	public void imprimirExtratoRecuperando(Recuperando recuperando) {
		gerarExtratoRecuperando(service.gerarEntidadeExtratoRecuperando(recuperando, null));
	}
	
	private void gerarExtratoRecuperando(List<ExtratoRecuperandoDTO> lista) {
		try {
			List<ExtratoRecuperandoDTO> listaRec = lista;
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(listaRec, Relatorio.ExtratoRecuperando));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Recuperandos", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir extrato do recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Extrato do Recuperando", e, log);
		}
	}


	public void visualizarLotacao(Passagem passagem) {
		LoggerUtil.logInfo(Acao.VISUALIZAR, passagem, log);
		this.passagemSelecionada = passagemService.buscaPassagemPorId(passagem);
	}
	
	private String acaoDialogProcesso(Processo proc, boolean isVisualizar) {
		if (proc == null) {
			return "pretty:CadastrarProcesso";
		}
		
		if (isVisualizar) {
			return "pretty:VisualizarProcesso";
		} 
		
		return "pretty:AlterarProcesso";
	}

	public void abrirDialogProcesso(Processo processo, boolean isVisualizar) {		
		if (processo != null) {
			facesUtil.setObjetoSessao("processo", processo);
		}
		
		facesUtil.exibirPaginaDialog(acaoDialogProcesso(processo, isVisualizar), null, 1300);
	}

	/*
	 * Getter e Setters
	 */
	public FiltroUnificarRecuperando getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroUnificarRecuperando filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<RecuperandoDTO> getRecuperandos() {
		return recuperandos;
	}

	public void setRecuperandos(LazyDataModel<RecuperandoDTO> recuperandos) {
		this.recuperandos = recuperandos;
	}

	public List<RecuperandoDTO> getRecuperandoSimilares() {
		return recuperandoSimilares;
	}

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public Passagem getPassagemSelecionada() {
		return passagemSelecionada;
	}

	public void setPassagemSelecionada(Passagem passagemSelecionada) {
		this.passagemSelecionada = passagemSelecionada;
	}

    public void setRecuperandosSelecionados(List<RecuperandoDTO> recuperandosSelecionados) {
		this.recuperandosSelecionados = recuperandosSelecionados;
	}

	public List<RecuperandoDTO> getRecuperandosSelecionados() {
		return recuperandosSelecionados;
	}

	public RecuperandoDTO getRecuperandoSelecionado1() {
		return recuperandoSelecionado1;
	}

	public void setRecuperandoSelecionado1(RecuperandoDTO recuperandoSelecionado1) {
		this.recuperandoSelecionado1 = recuperandoSelecionado1;
	}

	public RecuperandoDTO getRecuperandoSelecionado2() {
		return recuperandoSelecionado2;
	}

	public void setRecuperandoSelecionado2(RecuperandoDTO recuperandoSelecionado2) {
		this.recuperandoSelecionado2 = recuperandoSelecionado2;
	}
	

	public DadosRecuperandoUnificacaoDTO getDadosRecuperandoUnificacaoDTO() {
		return dadosRecuperandoUnificacaoDTO;
	}

	public void setDadosRecuperandoUnificacaoDTO(DadosRecuperandoUnificacaoDTO dadosRecuperandoUnificacaoDTO) {
		this.dadosRecuperandoUnificacaoDTO = dadosRecuperandoUnificacaoDTO;
	}

	public Pessoa getPessoaRecuperandoSelecionado1() {
		return pessoaRecuperandoSelecionado1;
	}

	public void setPessoaRecuperandoSelecionado1(Pessoa pessoaRecuperandoSelecionado1) {
		this.pessoaRecuperandoSelecionado1 = pessoaRecuperandoSelecionado1;
	}

	public Pessoa getPessoaRecuperandoSelecionado2() {
		return pessoaRecuperandoSelecionado2;
	}

	public void setPessoaRecuperandoSelecionado2(Pessoa pessoaRecuperandoSelecionado2) {
		this.pessoaRecuperandoSelecionado2 = pessoaRecuperandoSelecionado2;
	}

	public Recuperando getRecuperando1() {
		return recuperando1;
	}

	public void setRecuperando1(Recuperando recuperando1) {
		this.recuperando1 = recuperando1;
	}

	public Recuperando getRecuperando2() {
		return recuperando2;
	}

	public void setRecuperando2(Recuperando recuperando2) {
		this.recuperando2 = recuperando2;
	}
	
	public void setDocumentosRecuperando1(List<Documento> documentosRecuperando1) {
		this.documentosRecuperando1 = documentosRecuperando1;
	}

	public void setDocumentosRecuperando2(List<Documento> documentosRecuperando2) {
		this.documentosRecuperando2 = documentosRecuperando2;
	}

}