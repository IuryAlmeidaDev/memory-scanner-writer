package br.gov.mt.sejudh.sigepen.bean;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.CredencialCertidao;
import br.gov.mt.sejudh.sigepen.model.CredencialDocumento;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.FaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.model.VisitanteCertidao;
import br.gov.mt.sejudh.sigepen.model.VisitanteDocumento;
import br.gov.mt.sejudh.sigepen.model.VisitanteResponsavel;
import br.gov.mt.sejudh.sigepen.service.CredenciamentoService;
import br.gov.mt.sejudh.sigepen.service.FaseDoCredenciamentoService;
import br.gov.mt.sejudh.sigepen.service.TipoVisitaService;
import br.gov.mt.sejudh.sigepen.service.VisitanteResponsavelService;
import br.gov.mt.sejudh.sigepen.service.VisitanteService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "CadastrarFasesCredenciamento", pattern = "/credenciar/#{id}", viewId = "/WEB-INF/pages/visitante/CredenciarVisitante.xhtml"),
        @URLMapping(id = "AlterarFasesCredenciamento", pattern = "/alterar/#{id}", viewId = "/WEB-INF/pages/visitante/CredenciarVisitante.xhtml"),
        @URLMapping(id = "VisualizarFasesCredenciamento", pattern = "/visualizar/#{id}", viewId = "/WEB-INF/pages/visitante/CredenciarVisitante.xhtml")
})
public class FaseDoCredenciamentoBean extends MetodosDePaginaBean {
	private Visitante visitante;
    private Credenciamento credenciamento;
    private FaseDoCredenciamento faseDoCredenciamento;

    @Inject
    private CredenciamentoService service;

    @Inject
    private VisitanteService visitanteService;

    @Inject
    private FaseDoCredenciamentoService faseDoCredenciamentoService;
    
    @Inject
    private TipoVisitaService tipoVisitaService;

    @Inject
    private FacesUtil facesUtil;

    @Inject
    private Logger log;
    
    @Inject
    private VisitanteResponsavelService visitanteResponsavelService;
    
    private Integer telaOrigem;
    
    private String AcaoDefault = "CREDENCIAMENTO";
    
    private String acaoCredenciamento = AcaoDefault; 
    
    private boolean mostrarPanelVisitante = false;
	
	private boolean mostrarPanelResponsavel = false;
	
	String acaoAnterior = null;

	@Inject
    private SecurityUtil securityUtil;
	
	private List<Visitante> visitantesResponsaveis = new ArrayList<>();

    @URLAction(onPostback = false)
    public String init() {
        try {
            super.carregarFiltroPaginacao(FiltroVisitante.class, this);
            if (this.getAcaoDeTela() != AcaoDeTela.CREDENCIAR && this.getAcaoDeTela() != AcaoDeTela.ALTERAR && this.getAcaoDeTela() != AcaoDeTela.VISUALIZAR) {
                return "pretty:ListarVisitante";
            }
            
            if(this.getAcaoDeTela() == AcaoDeTela.CREDENCIAR) 
            	this.telaOrigem = 1;
            
                        
            if (this.getAcaoDeTela() ==  AcaoDeTela.VISUALIZAR) 
            	this.telaOrigem = 3;
            
            if (this.getAcaoDeTela() == AcaoDeTela.ALTERAR ) 
            	this.telaOrigem = 2;
            
            long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
            long idCredenciamento = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("idCredenciamento"));
            String acao = facesUtil.getQueryParameter("acaoCredenciamento");
            if(acao != null)
            	this.setAcaoCredenciamento(acao);

            if (!this.buscarVisitante(id, idCredenciamento)) {
                facesUtil.exibirMensagemErro("Visitante não encontrado.", true);
                return "pretty:ListarVisitante";
            }
            
            acaoAnterior = facesUtil.getQueryParameter("acaoAtual");

            this.iniciarDadosCredenciar(idCredenciamento);
            this.isVerificaPermissaoEtapaCredenciamento();
            if(this.getAcaoDeTela() == AcaoDeTela.CREDENCIAR)
            	this.desmarcarValidados();
            
            LoggerUtil.logInfo(Acao.ACESSAR, "Credenciamento: " + this.credenciamento, log);
        } catch (ValidacaoException ve) {
            facesUtil.exibirMensagemErro(ve.getMessage(), true);
            return "pretty:ListarVisitante";
        }  catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
            LoggerUtil.logErro(e, log);
        }

        return null;
    }
    
    public String getRetornoTela() {
    	if (acaoAnterior == null) 
    		return "pretty:ListarVisitante";
    	
    	switch(acaoAnterior) {
    		case "VISUALIZAR":
    			return "pretty:VisualizarVisitante";
    		
    		case "ALTERAR":
    			return "pretty:AlterarVisitante";
    		
    		case "CREDENCIAR":
    			return "pretty:ListarCredenciamentoVisitante";
    			
			default:
				return "pretty:ListarVisitante";
    	}
    }

    private void desmarcarValidados() {
    	for(VisitanteDocumento vd : this.visitante.getVisitanteDocumentos()) {
    		vd.setValidado(SimNao.N);
    	}
    	for(VisitanteCertidao vc : this.visitante.getCertidoes()) {
    		vc.setValidado(SimNao.N);
    	}
		
	}

	private void iniciarDadosCredenciar(long idCredenciamento) {
		
    	if(idCredenciamento > 0) { 
    		this.faseDoCredenciamento = faseDoCredenciamentoService.buscarCredenciamento(idCredenciamento);
    	}
    	else {
    		this.faseDoCredenciamento = faseDoCredenciamentoService.atribuirProximaFaseCredenciamento(this.credenciamento);

            if(this.faseDoCredenciamento.getId() == null)
            	atribuirVisitanteDocumentos();
            else {
                this.faseDoCredenciamento = faseDoCredenciamentoService.buscarCredenciamento(this.faseDoCredenciamento.getId());
                if(this.faseDoCredenciamento.getCredencialDocumentos().size() == 0)
                	atribuirVisitanteDocumentos();
            }
    	}
    	
        this.faseDoCredenciamento.setCredenciamento(credenciamento);
    }

    private boolean buscarVisitante(long id, long idCredenciamento) throws ValidacaoException {
        if (id <= 0) {
            return false;
        }

        this.credenciamento = service.buscarPorId(id, false);

        if (this.credenciamento == null) {
            return false;
        }

        this.visitante = visitanteService.buscarPorId(this.credenciamento.getVisitante().getId(), true);
        
        
        List<VisitanteResponsavel> listaResponsaveis = visitanteResponsavelService.buscarListaVisitantesReponsaveis(this.visitante);
        
        if(!listaResponsaveis.isEmpty()) {
        	for(VisitanteResponsavel vr : listaResponsaveis) {
        		Visitante v = new Visitante();
        		v = visitanteService.buscarPorId(vr.getVisitanteResponsavel().getId());
        		this.visitantesResponsaveis.add(v);
        	}
        	
        	this.visitante.setVisitantesReponsaveis(this.visitantesResponsaveis);
        }
        
        if(!this.visitantesResponsaveis.isEmpty()) {
        	this.mostrarPanelVisitante = false;
        	this.mostrarPanelResponsavel = true;
        }
        else {
        	this.mostrarPanelResponsavel = false;
        	this.mostrarPanelVisitante = true;
        	
        }
        
        service.setDocumentosValidados(id, idCredenciamento, this.visitante);
        
        this.credenciamento.setVisitante(this.visitante);
        
        TipoVisita tv = tipoVisitaService.buscarPorId(this.credenciamento.getTipoVisita().getId());
        
        if (this.getAcaoDeTela() !=  AcaoDeTela.VISUALIZAR) {
        	List<String> documentos = new ArrayList<String>();
        	List<String> certidoes = new ArrayList<String>();
        	
		    if (visitanteService.validarDocumentosObrigatorios(documentos, tv, visitante) || 
		    		visitanteService.validarCertidoesObrigatorias(certidoes, tv, visitante)) {
		    	
		    	String msg = "";
		    	
		    	if (documentos.size() > 0) {
		    		msg = String.join(", ", documentos);
		    	}
		    	
		    	if (certidoes.size() > 0) {
		    		msg = String.join(", ", certidoes);
		    	}
		    	
		    	throw new ValidacaoException(msg);
		    }
        }

        return true;
    }

    public String salvar() {
        try {
        	this.validarDataRecebimento();
        	this.validarDateDespacho();
        	this.validarDataValidade();
            this.validaDataValidadeMenorQueDataAtual();
            this.validaCredecimantoMenorQueIraCompletar18Anos();
            this.validaDataMinima();
            this.valida180Dias();

            this.faseDoCredenciamento.setDataAprovacao(Calendar.getInstance().getTime());
            
            FaseDoCredenciamento fase = this.faseDoCredenciamentoService.salvar(this.faseDoCredenciamento);
            
            if(AcaoDefault.equals(this.getAcaoCredenciamento()) || ("ALTERAR_CREDENCIAMENTO".equals(this.getAcaoCredenciamento()) && fase.getFaseCredenciamento() == FaseCredenciamento.APROVACAO)) {
            	this.visitanteService.atribuirStatusVisitantePosCredenciamento(fase.getCredenciamento().getVisitante());
            }
            
            List<FaseDoCredenciamento> dependentes = this.faseDoCredenciamentoService.lancarCredenciamentoDoDependente(fase);

            this.atribuirStatusDependentes(dependentes);
            LoggerUtil.logInfo(Acao.SALVAR, this.faseDoCredenciamento, log);
            this.exibirMensagemSucesso();
            return "pretty:ListarVisitante";
        } catch (ValidacaoException e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "Erro", "Erro ao salvar Visitante (" + this.visitante.getPessoa().getNome() + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.SALVAR, this.faseDoCredenciamento, e, log);
        }

        return "";
    }

	

	private void atribuirStatusDependentes(List<FaseDoCredenciamento> dependentes) {
        if (dependentes != null) {
            for (FaseDoCredenciamento credencial : dependentes ) {
                this.visitanteService.atribuirStatusVisitantePosCredenciamento(credencial.getCredenciamento().getVisitante());
            }
        }
    }

    private void validaCredecimantoMenorQueIraCompletar18Anos() throws ValidacaoException {
        if(this.faseDoCredenciamentoService.isVerificaSeCredenciamentoEhParaMenorQueIraCompletar18Anos(this.faseDoCredenciamento)) {
            this.faseDoCredenciamento.getCredenciamento().setDataValidade(null);
            throw new ValidacaoException("Visitante completará 18 anos em menos de 12 meses! Favor informar data de validade com a data onde o mesmo completa 18 anos.");
        }
        return;
    }

    private void validaDataValidadeMenorQueDataAtual() throws ValidacaoException {
        if(this.faseDoCredenciamento.getCredenciamento().getDataValidade() != null) {
            LocalDate dataAtual = LocalDate.now();
            LocalDate dataValidade = Instant.ofEpochMilli(this.faseDoCredenciamento.getCredenciamento().getDataValidade().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            if (dataValidade.isBefore(dataAtual)) {
                this.faseDoCredenciamento.getCredenciamento().setDataValidade(null);
                throw new ValidacaoException("A Data de validade não pode ser menor que a data atual!");
            }
        }
        return;
    }

    private void exibirMensagemSucesso() {
        if (this.faseDoCredenciamento.getFaseCredenciamento() == FaseCredenciamento.APROVACAO
                && this.faseDoCredenciamento.getStatusCredenciamento() == StatusCredenciamento.APROVADO) {
            facesUtil.exibirMensagemInfo("Credenciamento do "
                    + this.faseDoCredenciamento.getCredenciamento().getVisitante().getPessoa().getNome()
                    +  " aprovado com sucesso!", true);
            return;
        }

        facesUtil.exibirMensagemInfo("Credenciamento " + this.faseDoCredenciamento.getFaseCredenciamento().getDescricao() +  " concluída com sucesso!", true);
    }

    private void isVerificaPermissaoEtapaCredenciamento() throws ValidacaoException {
    	if (getAcaoDeTela().equals(AcaoDeTela.VISUALIZAR)) {
    		if (!SecurityUtils.getSubject().isPermitted("VisualizarFasesCredenciamento"))
    			throw new ValidacaoException("Usuário sem permissão para visualizar fases.");
    		else
    			return;
    	}
    	
        boolean permitted = SecurityUtils.getSubject().isPermitted(this.faseDoCredenciamento.getFaseCredenciamento().getPermissao());
        if (!permitted) {
            throw new ValidacaoException("Usuário sem permissão de acesso a etapa de ".
                                        concat(this.faseDoCredenciamento.getFaseCredenciamento().getDescricao()));
        } else {
            if(FaseCredenciamento.APROVACAO.equals(this.faseDoCredenciamento.getFaseCredenciamento())) {
            	if((TipoEntrada.FAMILIA.equals(this.faseDoCredenciamento.getCredenciamento().getTipoVisita().getTipoEntrada())
            			&& !securityUtil.possuiPermissao("CredenciamentoAprovacaoFamilia")) ||
            		(TipoEntrada.ATENDIMENTO.equals(this.faseDoCredenciamento.getCredenciamento().getTipoVisita().getTipoEntrada())
            			&& !securityUtil.possuiPermissao("CredenciamentoAprovacaoAtendimento")) ||
            		(TipoEntrada.ENTRADA_SAIDA.equals(this.faseDoCredenciamento.getCredenciamento().getTipoVisita().getTipoEntrada())
                		&& !securityUtil.possuiPermissao("CredenciamentoAprovacaoEntradaSaida"))) {
                    throw new ValidacaoException("Usuário sem permissão para aprovar " + this.faseDoCredenciamento.getCredenciamento().getTipoVisita().getTipoEntrada().getDescricao());
            	} 
            }
        }
        return;
    }

    public FaseDoCredenciamento getFaseDoCredenciamento() {
        return faseDoCredenciamento;
    }

    public void setFaseDoCredenciamento(FaseDoCredenciamento faseDoCredenciamento) {
        this.faseDoCredenciamento = faseDoCredenciamento;
    }

    public Visitante getVisitante() {
        return visitante;
    }


    public List<StatusCredenciamento> getStatusCredenciamentos() {
        return this.faseDoCredenciamentoService.statusCredenciamentosDisponiveis(this.faseDoCredenciamento);
    }

    private void validaDataMinima() throws ValidacaoException {
    	this.faseDoCredenciamentoService.validarDataMinima(this.faseDoCredenciamento);
    }
    
    private void valida180Dias()throws ValidacaoException {
        if(faseDoCredenciamento.getFaseCredenciamento() == FaseCredenciamento.APROVACAO &&
        		faseDoCredenciamento.getCredenciamento().getVisitante().getPne() == SimNao.S && 
        		faseDoCredenciamento.getCredenciamento().getVisitante().getPnePermanente() == SimNao.N) {
        	
            if(this.faseDoCredenciamento.getCredenciamento().getDataValidade() != null) {
                LocalDate dataLimite = LocalDate.now().plusDays(180);
                DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String dateLimiteFormatada = formatador.format(dataLimite);
                LocalDate dataValidade = Instant.ofEpochMilli(this.faseDoCredenciamento.getCredenciamento().getDataValidade().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                if (dataValidade.isAfter(dataLimite)) {
                    this.faseDoCredenciamento.getCredenciamento().setDataValidade(null);
                    throw new ValidacaoException(" Visitante PNE não permanente! " +  " Data de validade da visita não pode ser superior a " + dateLimiteFormatada);
                }
            }
        }
        return;
    }

	public Integer getTelaOrigem() {
		return telaOrigem;
	}

	public void setTelaOrigem(Integer telaOrigem) {
		this.telaOrigem = telaOrigem;
	}

	public void atribuirVisitanteDocumentos() {
		this.faseDoCredenciamento.setCredencialDocumentos(new ArrayList<CredencialDocumento>());
		this.faseDoCredenciamento.setCredencialCertidaos(new ArrayList<CredencialCertidao>());
		for(VisitanteDocumento documento : this.faseDoCredenciamento.getCredenciamento().getVisitante().getVisitanteDocumentos()) {
			CredencialDocumento credencialDocumento = new CredencialDocumento();
			credencialDocumento.setValidado(SimNao.getByBool(documento.getValueValidado()));
			credencialDocumento.setFaseDoCredenciamento(this.faseDoCredenciamento);
			credencialDocumento.setVisitanteDocumento(documento);
			credencialDocumento.setValidado(SimNao.N);
			this.faseDoCredenciamento.getCredencialDocumentos().add(credencialDocumento);
		}
		
        for (VisitanteCertidao certidao : this.faseDoCredenciamento.getCredenciamento().getVisitante().getCertidoes()) {
            CredencialCertidao credencialCertidao = new CredencialCertidao();
            credencialCertidao.setValidado(SimNao.getByBool(certidao.getValueValidado()));
            credencialCertidao.setFaseDoCredenciamento(faseDoCredenciamento);
            credencialCertidao.setVisitanteCertidao(certidao);
            credencialCertidao.setValidado(SimNao.N);
            this.faseDoCredenciamento.getCredencialCertidaos().add(credencialCertidao);
        }
	}

	public String getAcaoCredenciamento() {
		return acaoCredenciamento;
	}

	public void setAcaoCredenciamento(String acaoCredenciamento) {
		this.acaoCredenciamento = acaoCredenciamento;
	}
	
    private void validarDataValidade() throws ValidacaoException {
        if(this.faseDoCredenciamento.getFaseCredenciamento() == FaseCredenciamento.APROVACAO && this.faseDoCredenciamento.getCredenciamento().getDataValidade() != null) {
            LocalDate dataLimite = LocalDate.now().plusDays(365);

            LocalDate dataValidade = Instant.ofEpochMilli(this.faseDoCredenciamento.getCredenciamento().getDataValidade().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            if (dataValidade.isAfter(dataLimite)) {
                this.faseDoCredenciamento.getCredenciamento().setDataValidade(null);
                throw new ValidacaoException("A data de validade da visita não pode exceder 365 dias.");
            }
        }
        return;
	}
    
	private void validarDateDespacho() throws ValidacaoException {
		
		if (this.faseDoCredenciamento.getDataRecebimento() != null && this.faseDoCredenciamento.getDataDespacho() != null) {
			if (this.faseDoCredenciamento.getDataDespacho().before(this.faseDoCredenciamento.getDataRecebimento())) {
				throw new ValidacaoException("A data de despacho não pode ser menor que a data de recebimento.");
			}

		}
		return;
	}

    private void validarDataRecebimento() throws ValidacaoException {
    	    	
        if(this.faseDoCredenciamento.getDataRecebimento() != null) {
            LocalDate dataRecebimento = Instant.ofEpochMilli(this.faseDoCredenciamento.getDataRecebimento().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            if (dataRecebimento.isAfter(LocalDate.now())) {
                this.faseDoCredenciamento.setDataRecebimento(null);
                throw new ValidacaoException("A data de recebimento não pode ser após a data atual.");
            }
        }
        return;
	}
    
    public boolean isMostrarPanelVisitante() {
		return mostrarPanelVisitante;
	}

	public void setMostrarPanelVisitante(boolean mostrarPanelVisitante) {
		this.mostrarPanelVisitante = mostrarPanelVisitante;
	}

	public boolean isMostrarPanelResponsavel() {
		return mostrarPanelResponsavel;
	}

	public void setMostrarPanelResponsavel(boolean mostrarPanelResponsavel) {
		this.mostrarPanelResponsavel = mostrarPanelResponsavel;
	}
	
	public Credenciamento getCredenciamento() {
		return credenciamento;
	}

	public void setCredenciamento(Credenciamento credenciamento) {
		this.credenciamento = credenciamento;
	}

	public List<Visitante> getVisitantesResponsaveis() {
		return visitantesResponsaveis;
	}

	public void setVisitantesResponsaveis(List<Visitante> visitantesResponsaveis) {
		this.visitantesResponsaveis = visitantesResponsaveis;
	}



	
}
