package br.gov.mt.sejudh.sigepen.bean;

import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.StatusProcesso;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDesligamento;
import br.gov.mt.sejudh.sigepen.enumerator.TipoFoto;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Estabelecimento;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.PassagemProcedencia;
import br.gov.mt.sejudh.sigepen.model.Processo;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.service.PassagemService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "CadastrarNovaPassagem", pattern = "/passagem/iniciar/#{id}", viewId = "/WEB-INF/pages/recuperando/CriarPassagem.xhtml"),
		@URLMapping(id = "DesligarRecuperando", pattern = "/passagem/finalizar/#{id}", viewId = "/WEB-INF/pages/recuperando/DesligarRecuperando.xhtml")
})
public class PassagemBean extends MetodosDePaginaBean {
	
	private Passagem passagem;

	private PassagemLotacao lotacao;

	private PassagemProcedencia passagemProcedencia;
	
	private Estabelecimento procedencia;
	
	private Recuperando recuperando;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private PassagemService passagemService;
	
	@Inject
	private RecuperandoService recuperandoService;
	
	@Inject
	private UnidadePrisionalService unidadeService;

	@URLAction(onPostback=false)
	public String init() {
		try {
			this.modificaTituloPagina();

			long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

			if (!buscarRecuperando(id)) {
				facesUtil.exibirMensagemErro("Recuperando não encontrado.", true);
				return "pretty:ListarRecuperando";
			}
			
			switch(this.getAcaoDeTela()) {
				case INICIAR:
					if (!validarRecuperandoEntrada()) {
						facesUtil.exibirMensagemErro("Operação não permitida.", true);
						return "pretty:ListarRecuperando";
					}
					
					this.passagem = new Passagem(this.recuperando);
					this.passagem.setPassagemAtual(true);
					this.lotacao = new PassagemLotacao(this.passagem, TipoTransito.ENTRADA);
					this.passagemProcedencia = new PassagemProcedencia();
					
					validarBiometriaAtualizadaDoRecuperando();
					break;
					
				case FINALIZAR:
					if (!validarRecuperandoSaida()) {
						facesUtil.exibirMensagemErro("Operação não permitida.", true);
						return "pretty:ListarRecuperando";
					}
					
					this.passagem = this.recuperando.getPassagemAtual();
					this.lotacao = this.passagem.getLotacaoAtual();
					break;
			
				default:
					break;
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
			e.printStackTrace();
		}
		
		return null;
	}
	
	private Boolean verificarSeBiometriaEhObrigatoria() {
		return unidadeService.verificarSeBiometriaEhObrigatoria(SecurityUtil.getUnidadeSelecionada().getId());
	}
	
	public void validarBiometriaAtualizadaDoRecuperando() {
		if (verificarSeBiometriaEhObrigatoria()) {
			boolean recuperandoPossuiBiometriaAtualizada = this.recuperandoService.recuperandoPossuiBiometriaAtualizada(this.recuperando.getId());
			
			if (!recuperandoPossuiBiometriaAtualizada) {
				if (this.getAcaoDeTela() == AcaoDeTela.FINALIZAR) {
					RequestContext.getCurrentInstance().execute("PF('atualizacaoBiometria').show()");
				} else if (this.getAcaoDeTela() == AcaoDeTela.INICIAR) {
					facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ATUALIZAÇÃO CADASTRAL NECESSÁRIA", "A biometria deste recuperando está DESATUALIZADA.");
				}
			}
		}
	}
	
	private boolean buscarRecuperando(long id) {
		if (id <= 0) {
			return false;
		}
		
		switch(this.getAcaoDeTela()) {
			case INICIAR:
				this.recuperando = passagemService.buscarRecuperandoPorIdNovaPassagem(id);
				break;
			case FINALIZAR:
				this.recuperando = passagemService.buscarRecuperandoPorIdDesligamento(id);
				break;
			default:
				break;
		}
		
		if (this.recuperando == null) {
			return false;
		}
		
		return true;
	}
	
	private boolean validarRecuperandoEntrada() {
		boolean recuperandoInativo = (this.recuperando.getStatus().equals(StatusRecuperando.INATIVO) || this.recuperando.getStatus().equals(StatusRecuperando.INATIVO_PARCIAL));
		
		if (recuperandoInativo) {
			for (Passagem p : this.recuperando.getPassagens()) {
				if (p.getTipoDesligamento() != null && p.getTipoDesligamento().equals(TipoDesligamento.OBITO)) {
					return false;
				}
			}
		}

		return recuperandoInativo;
	}
	
	private boolean validarRecuperandoSaida() {
		return (this.recuperando.getStatus().equals(StatusRecuperando.ATIVO) && 
				this.recuperando.getPassagemAtual().getLotacaoAtual().getEstrutura().getUnidadePrisional().getId() == SecurityUtil.getUnidadeSelecionada().getId());
	}
	
	private boolean possuiProcessoNaoFinalizado(Recuperando recuperando) {
		for (Processo p : recuperando.getProcessos()) {
			if (p.getStatusProcesso() != StatusProcesso.FINALIZADO) {
				return true;
			}
		}
		
		return false;
	}
	
	public String salvar() throws ValidacaoException {
		try {
			if (this.lotacao.getEstrutura() == null) {
				throw new ValidacaoException("É necessário adicionar uma lotação (estrutura) para a passagem do recuperando.");
			}

			if (this.passagemProcedencia.getProcedencia() == null) {
				throw new ValidacaoException("É necessário adicionar uma procedência para o recuperando.");
			}

			if (!this.possuiProcessoNaoFinalizado(this.recuperando)) {
				throw new ValidacaoException("É necessário adicionar um processo para o recuperando.");
			}

			if (this.passagem.getDataEntrada().before(this.passagem.getDataPrisao())) {
				throw new ValidacaoException("A data da Entrada não pode ser anterior a data da Prisão.");
			}
			
			if (this.passagem.getDataEntrada().after(Sigepen.getDataAtual())) {
				throw new ValidacaoException("A data da Entrada não pode ser maior que a data atual.");
			}
			
			if (passagemService.possuiPasagemAberta(this.recuperando)) {
				throw new ValidacaoException("Recuperndo possui uma passagem não finalizada!");
			}
			
			if (!passagemService.validarDataNovaPasagem(this.recuperando, this.passagem.getDataEntrada())) {
				throw new ValidacaoException("A data da Entrada não pode ser anterior a saída da última passagem!");
			}
			
			this.passagem.setFoto(this.recuperando.getFotoFrenteAtual());
			this.lotacao.setDataEntrada(this.passagem.getDataEntrada());
			this.passagem.getLotacao().add(this.lotacao);
			this.passagem.setPassagemProcedencia(this.passagemProcedencia);
			this.passagemProcedencia.setPassagem(this.passagem);
			this.recuperando.getPassagens().add(this.passagem);
			this.recuperando.setStatus(StatusRecuperando.ATIVO);

			if (this.recuperando.getPassagemAtual().getDataInclusaoNoSistema() == null) {
				this.recuperando.getPassagemAtual().setDataInclusaoNoSistema(Sigepen.getDataAtual());
			}

			recuperandoService.salvar(this.recuperando);
			
			recuperandoService.atualizarRegimeCondicaoAtualRecuperando(this.recuperando);
			LoggerUtil.logInfo(Acao.INICIAR, this.recuperando, log);
			facesUtil.exibirMensagemInfo("Entrada do recuperando(a) " + this.recuperando.getPessoa().getNome() + " realizada com sucesso!", true);
			
			return "pretty:ListarRecuperando";
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(null, this.recuperando, e, log);
		}

		return null;
	}
	
	public String desligar() {
		try {
			passagemService.desligar(this.recuperando, this.passagem, this.lotacao);
			LoggerUtil.logInfo(Acao.FINALIZAR, this.recuperando, log);
			facesUtil.exibirMensagemInfo("Desligamento do(a) recuperando(a) " + this.recuperando.getPessoa().getNome() + " realizado com sucesso!", true);
			
			return "pretty:ListarRecuperando";
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(null, this.recuperando, e, log);
		}
		
		return null;
	}
	
	public void abrirDialogFoto(ActionEvent event) {
		facesUtil.exibirPaginaDialog("AssociarFoto", 800, 900);
	}
	
	public void associarFoto(SelectEvent event) {
		Object retorno = event.getObject();

		if (retorno == null) {
			return;
		}

		Foto foto = new Foto();
		foto.setRecuperando(this.recuperando);
		foto.setTipo(TipoFoto.FRENTE);
		foto.setIsAtual(true);
		foto.setDataInclusao(Sigepen.getDataAtual());
		foto.setImagem((Imagem) retorno);

		this.recuperando.adicionarFotoFrente(foto);
		LoggerUtil.logInfo(Acao.ADICIONAR, foto + " " + this.recuperando, log);
	}
	
	public void abrirDialogEstrutura(ActionEvent event) {
		facesUtil.exibirPaginaDialog("AssociarEstrutura");
	}

	public void associarEstrutura(SelectEvent event) {
		Estrutura est = (Estrutura) event.getObject();
		
		if (est == null)
			return;
		
		this.lotacao.setEstrutura(est);
	}
	
	public void abrirDialogProcedencia(ActionEvent event) {
		facesUtil.setObjetoSessao("proced", true);
		facesUtil.exibirPaginaDialog("AssociarEstabelecimento", null, 1000);
	}

	public void associarProcedencia(SelectEvent event) {
		this.procedencia = (Estabelecimento) event.getObject();
		this.passagemProcedencia.setProcedencia(this.procedencia);
	}
	
	public void abrirDialogEstabelecimento(ActionEvent event) {
		facesUtil.exibirPaginaDialog("pretty:AssociarEstabelecimento", null, 1000);
	}
	
	public void associarEstabelecimento(SelectEvent event) {
		Estabelecimento estabelecimento = (Estabelecimento) event.getObject();
		
		if (estabelecimento != null) {
			this.passagem.setEstabelecimentoDestino(estabelecimento);
		}		
	}
	
	public void alterarTipoDesligamento() {
		if (this.passagem.getTipoDesligamento() != TipoDesligamento.OUTRO_SISTEMA_PENITENCIARIO) {
			this.passagem.setEstabelecimentoDestino(null);
		}
	}
	
	public void abrirDialogProcesso(Processo processo, boolean isVisualizar) {
		facesUtil.setObjetoSessao("recuperandoProcesso", this.recuperando);
		facesUtil.setObjetoSessao("isPassagem", true);
		
		if (processo != null) {
			facesUtil.setObjetoSessao("processo", processo);
		}
		
		facesUtil.exibirPaginaDialog(acaoDialogProcesso(processo, isVisualizar), null, 1000);
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
	
	public void atualizarProcessos(SelectEvent event) {
		Processo processo = (Processo) event.getObject();
		
		if (processo != null) {
			if (processo.getId() > 0) {
				for (Processo p : this.recuperando.getProcessos()) {
					if (p.getId() == processo.getId()) {
						this.recuperando.getProcessos().remove(p);
						this.recuperando.getProcessos().add(processo);
					}
				}
			} else {
				this.recuperando.getProcessos().remove(processo);
				this.recuperando.getProcessos().add(processo);
			}
		}
	}
	
	public void excluirProcesso(Processo processo) {
		if (processo != null) {
			this.recuperando.getProcessos().remove(processo);
		}
	}

	public Date getDataMaximaDesligamento() {
		Calendar c = Calendar.getInstance();
		return c.getTime();
	}
	
	public Date getDataMinima() {
		return new Date();
	}

	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
	}

	public Passagem getPassagem() {
		return passagem;
	}

	public void setPassagem(Passagem passagem) {
		this.passagem = passagem;
	}

	public PassagemLotacao getLotacao() {
		return lotacao;
	}

	public void setLotacao(PassagemLotacao lotacao) {
		this.lotacao = lotacao;
	}

	public PassagemProcedencia getPassagemProcedencia() {
		return passagemProcedencia;
	}

	public void setPassagemProcedencia(PassagemProcedencia passagemProcedencia) {
		this.passagemProcedencia = passagemProcedencia;
	}

	public Estabelecimento getProcedencia() {
		return procedencia;
	}

	public void setProcedencia(Estabelecimento procedencia) {
		this.procedencia = procedencia;
	}
}
