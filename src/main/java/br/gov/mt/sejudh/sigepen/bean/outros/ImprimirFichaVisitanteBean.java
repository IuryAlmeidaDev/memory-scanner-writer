package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.primefaces.context.RequestContext;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.bean.FileDownloadHandlerBean;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperando;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperandoView;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.model.Certidao;
import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.model.CredencialCertidao;
import br.gov.mt.sejudh.sigepen.model.CredencialDocumento;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.Documento;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.FaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.model.VisitanteCertidao;
import br.gov.mt.sejudh.sigepen.model.VisitanteDocumento;
import br.gov.mt.sejudh.sigepen.model.VisitanteResponsavel;
import br.gov.mt.sejudh.sigepen.model.dto.CertidaoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.ContatoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.DocumentoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.EnderecoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.FaseDoCredenciamentoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.FichaVisitanteDTO;
import br.gov.mt.sejudh.sigepen.model.dto.VisitanteDTO;
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.CredenciamentoService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.service.VisitanteResponsavelService;
import br.gov.mt.sejudh.sigepen.service.VisitanteService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;


@ManagedBean
@ViewScoped
@URLMapping(id = "FichaVisitante", pattern = "/visitante/ficha", viewId = "/WEB-INF/pages/_dialogs/ImprimirFichaVisitante.xhtml")
public class ImprimirFichaVisitanteBean extends MetodosDialogBean {
	private Integer idRecuperando=-1;
	private Integer tipRel = 0;
	private List<Credenciamento> credenciamentos = new ArrayList<Credenciamento>();
	private List<RecuperandoView> recuperandos = new ArrayList<RecuperandoView>();
	private Visitante visitante;
	private Credenciamento credenciamento;

    @Inject
    private VisitanteService service;
    
    @Inject
    private VisitanteResponsavelService responsavelService;

	@Inject
    private CredenciamentoService credenciamentoService;

	@Inject
	private RecuperandoService serviceRecuperando;
	
    @Inject
    private FileDownloadHandlerBean fileDownload;
	
	private FiltroRecuperando filtro;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			if (this.isDialogInvalida()) {
				return "pretty:dashboard";
			}
            this.setVisitante((Visitante) facesUtil.getObjetoSessao("filtroVisitante"));
//    		List<Credenciamento> swaprecuperandos = credenciamentoService.buscarCredenciamentoEntidadesRelacionadasPorVisitante(this.getVisitante());
    		
//        	for(Credenciamento vr : swaprecuperandos){
//        		Credenciamento vr2 = credenciamentoService.buscarPorId(vr.getId());
//        		Hibernate.initialize(vr2.getFasesDoCredenciamento());
//        		this.getCredenciamentos().add(vr);
//        		RecuperandoView r = new RecuperandoView();
//        		if(vr.getRecuperando() != null) {
//            		r.setId(vr.getRecuperando().getId());
//            		r.setNome(vr.getRecuperando().getPessoa().getNome());
//            		this.getRecuperandos().add(r);
//        		}
//        	}
			
			
            this.credenciamentos =  credenciamentoService.buscarCredenciamentoEntidadesRelacionadasPorVisitante(this.getVisitante());
            
            
		} catch (Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}
	
	
	public void selecionarRecuperando(Credenciamento recuperando) {
		this.setCredenciamento(recuperando);
		imprimir();
	}
	
	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}
	
	public FiltroRecuperando getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroRecuperando filtro) {
		this.filtro = filtro;
	}

	public Integer getTipRel() {
		return tipRel;
	}

	public void setTipRel(Integer tipRel) {
		this.tipRel = tipRel;
	}

	public List<Credenciamento> getCredenciamentos() {
		return credenciamentos;
	}

	public void setCredenciamentos(List<Credenciamento> credenciamentos) {
		this.credenciamentos = credenciamentos;
	}

	public Visitante getVisitante() {
		return visitante;
	}

	public void setVisitante(Visitante visitante) {
		this.visitante = visitante;
	}
	
	public void selecionarTipRel(AjaxBehaviorEvent event) {
	}

	public Credenciamento getCredenciamento() {
		return credenciamento;
	}

	public void setCredenciamento(Credenciamento credenciamento) {
		this.credenciamento = credenciamento;
	}

	public List<RecuperandoView> getRecuperandos() {
		return recuperandos;
	}

	public void setRecuperandos(List<RecuperandoView> recuperandos) {
		this.recuperandos = recuperandos;
	}
	
    public void imprimir() {
    	if (this.getTipRel() == null){
  		  facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Selecione o Tipo de Relatório!");
  		  return;
    	}
    	if(this.getTipRel() == 0 && this.getCredenciamento() == null) {
    		  facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Selecione o Recuperando!");
      		  return;
    	}
    	
    	switch(this.getTipRel()) {
	  	  case 0:
	  	    this.imprimirFicha();
	  	    break;
		  case 1:
	  	    this.imprimirListaCredenciamentos();
	  	    break;
    	  default:
    		  facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Selecione o Tipo de Relatório!");
    		  return;
    	}  
    }

	public Integer getIdRecuperando() {
		return idRecuperando;
	}

	public void setIdRecuperando(Integer idRecuperando) {
		this.idRecuperando = idRecuperando;
	}

    public void imprimirListaCredenciamentos() {
        try {
            List<Visitante> lista = new ArrayList<>();
            lista.add(this.getVisitante());

            this.imprimirRelatorio(lista);
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de Visitantes: " + e.getMessage());
            LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Visitantes", e, log);
        }
    }

    private void imprimirRelatorio(List<Visitante> lista) {
        try {
            if (lista.size() == 0) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("SUBREPORT_DIR", RelatorioUtil.getSubReportPath(Relatorio.ListaVisitantes));

            lista.forEach(v -> v.getCredenciamentos().forEach(vr -> {
                if (vr.getRecuperando() == null) vr.setRecuperando(new Recuperando(new Pessoa()));
            }));

            if (lista.size() == 1) {
                Relatorio.ListarVisitante.setNome("FICHA DO VISITANTE");
            }
            fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarVisitante, params));
            RequestContext.getCurrentInstance().execute("download()");
            LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Visitantes", log);
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de Visitantes: " + e.getMessage());
            LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Visitantes", e, log);
        }
    }
    
    private VisitanteDTO getDadosVisitante(Visitante visitante){
        VisitanteDTO pessoa_visitante = new VisitanteDTO();
        pessoa_visitante.setNome(visitante.getPessoa().getNome());
        pessoa_visitante.setNomeSocial(visitante.getPessoa().getNomeSocial());
        pessoa_visitante.setNomeMae(visitante.getPessoa().getNomeMae());
        pessoa_visitante.setNomePai(visitante.getPessoa().getNomePai());
        pessoa_visitante.setDataNascimento(visitante.getPessoa().getDataNascimento());
        pessoa_visitante.setImagemAtual(visitante.getImagemAtual());
        
        
        if(visitante.getEmancipado() != null && visitante.getEmancipado().equals(SimNao.N)) {
        	  List<VisitanteResponsavel> listaResponsaveis = responsavelService.buscarListaVisitantesReponsaveis(this.visitante);

        	  List<Visitante> visitantesResponsaveis = new ArrayList<>();
                            
              if(!listaResponsaveis.isEmpty()) {
              	for(VisitanteResponsavel vr : listaResponsaveis) {
              		Visitante v = new Visitante();
              		v = service.buscarPorId(vr.getVisitanteResponsavel().getId());             		
              		Hibernate.initialize(v.getImagemAtual());
              		Hibernate.initialize(v.getPessoa());
              		visitantesResponsaveis.add(v);
              	}              	        	
              	pessoa_visitante.setVisitanteResponsavelRelatorio(visitantesResponsaveis);
              } 
        }
        

        Credenciamento vr = credenciamentoService.buscarCredenciamentos(this.getCredenciamento());
    	if(vr.getRecuperando() != null && vr.getParente() != null && vr.getParente().getParentesco() != null && vr.getId() == this.getCredenciamento().getId())
    		pessoa_visitante.setGrauParentesco(vr.getParente().getParentesco().getDescricao());
        
    	return pessoa_visitante;
    }

    private RecuperandoView getDadosRecuperando(){
    	if(this.getCredenciamento().getRecuperando() != null) {
        	Recuperando recup = this.getCredenciamento().getRecuperando();
        	FiltroRecuperandoView fr = new FiltroRecuperandoView();
        	fr.setId(recup.getId());
        	fr.setIsBuscarFotoFrente(true);
        	List<RecuperandoView> recuperandos = serviceRecuperando.buscarRecuperandosPorFiltro(fr, false);
        	fr.setIsBuscarFotoFrente(false);
        	for (RecuperandoView r : recuperandos){
        		recup.setUnidadeAtual(r.getNomeUnidade());
        	}
        	if (recuperandos.size() > 0)
        		return recuperandos.get(0);
    	}
    	return null;
    }

	private List<DocumentoDTO> getVisitanteDocumentos(Visitante visitante) {
		List<DocumentoDTO> docs = new ArrayList<DocumentoDTO>(); 
        for (VisitanteDocumento vd : visitante.getVisitanteDocumentos()){
        	Documento d = vd.getDocumento();
        	DocumentoDTO doc = new DocumentoDTO();
        	doc.setId(d.getId());
        	doc.setTipoDocumento(d.getTipoDocumento());
        	doc.setNumero(d.getNumero());
        	doc.setDescricao(d.getDescricao());
        	doc.setDataEmissao(d.getDataEmissao());
        	doc.setDataValidade(d.getDataValidade());
        	docs.add(doc);
        	
        }
        return docs;
	}
	
	private List<CertidaoDTO> getVisitanteCertidoes(Visitante visitante){
		List<CertidaoDTO> certs = new ArrayList<CertidaoDTO>();
        for (VisitanteCertidao vc : visitante.getCertidoes()) {
        	Certidao c = vc.getCertidao();
        	CertidaoDTO cert = new CertidaoDTO();
        	cert.setDescricao(c.getDescricao());
        	cert.setStatus(c.getStatus());
        	cert.setEntregue(vc.getEntregue());
        	cert.setObrigatorio(vc.getObrigatorio());
        	certs.add(cert);
        }
		return certs;
	}

	private List<EnderecoDTO> getVisitanteEnderecos(Visitante visitante){
		List<EnderecoDTO> ends = new ArrayList<EnderecoDTO>();
        for (Endereco e : visitante.getPessoa().getEnderecos()) {
        	EnderecoDTO end = new EnderecoDTO();
        	end.setLogradouro(e.getLogradouro());
        	end.setNumero(e.getNumero());
        	end.setBairro(e.getBairro());
        	end.setComplemento(e.getComplemento());
        	end.setCidade(e.getCidade());
        	ends.add(end);
        }
        return ends;		
	}
	
	private List<ContatoDTO> getVisitanteContatos(Visitante visitante){
		List<ContatoDTO> cts = new ArrayList<ContatoDTO>();
        for (Contato c : visitante.getPessoa().getContatos()) {
        	ContatoDTO ct = new ContatoDTO();
        	ct.setNome(c.getNome());
        	ct.setTipoContato(c.getTipoContato());
        	ct.setValor(c.getValor());
        	ct.setObservacao(c.getObservacao());
        	cts.add(ct);
        }
		return cts;
	}
	
	private List<FaseDoCredenciamentoDTO> getVisitanteCredenciamentos(Visitante visitante){
		List<FaseDoCredenciamentoDTO> creds = new ArrayList<FaseDoCredenciamentoDTO>();
		Credenciamento o = credenciamentoService.buscarCredenciamentos(this.getCredenciamento());
		if(o.getId() == this.getCredenciamento().getId()){

			for (FaseDoCredenciamento x : o.getFasesDoCredenciamento()){
    			FaseDoCredenciamentoDTO cred = new FaseDoCredenciamentoDTO();
    			cred.setFaseCredenciamento(x.getFaseCredenciamento());
    			cred.setDataRecebimento(x.getDataRecebimento());
    			cred.setDataAprovacao(x.getDataAprovacao());
    			cred.setDataDespacho(x.getDataDespacho());
    			cred.setStatusCredenciamento(x.getStatusCredenciamento());
    			cred.setUsuarioAprovacao(x.getUsuarioAprovacao());
    			cred.setObservacao(x.getObservacao());
    			
    			List<DocumentoDTO> docs = new ArrayList<DocumentoDTO>();
    	        for (CredencialDocumento cd : x.getCredencialDocumentos()){
    	        	if(cd.getFaseDoCredenciamento().getCredenciamento().getId() == this.getCredenciamento().getId()){
        	        	Documento d = cd.getVisitanteDocumento().getDocumento();
        	        	DocumentoDTO doc = new DocumentoDTO();
        	        	doc.setId(d.getId());
        	        	doc.setTipoDocumento(d.getTipoDocumento());
        	        	doc.setNumero(d.getNumero());
        	        	doc.setDescricao(d.getDescricao());
        	        	doc.setDataEmissao(d.getDataEmissao());
        	        	doc.setDataValidade(d.getDataValidade());
        	        	doc.setValidado(cd.getValidado());
        	        	docs.add(doc);
    	        	}
    	        }
    		    List<CertidaoDTO> cers = new ArrayList<CertidaoDTO>();
    		    for(CredencialCertidao cc : x.getCredencialCertidaos()){
    		    	if(cc.getFaseDoCredenciamento().getCredenciamento().getId() == this.getCredenciamento().getId()){
        	        	Certidao c = cc.getVisitanteCertidao().getCertidao();
        	        	CertidaoDTO cert = new CertidaoDTO();
        	        	cert.setDescricao(c.getDescricao());
        	        	cert.setStatus(c.getStatus());
        	        	cert.setEntregue(cc.getVisitanteCertidao().getEntregue());
        	        	cert.setObrigatorio(cc.getVisitanteCertidao().getObrigatorio());
        	        	cert.setValidado(cc.getValidado());
        		    	cers.add(cert);
    		    	}
    		    }
    		    
    	        cred.setCredencialDocumentos(docs);
    		    cred.setCredencialCertidoes(cers);
        		creds.add(cred);
			}
		}
		
		return creds;
	}

	public void imprimirFicha() {
        Visitante visitante = this.service.buscarPorId(this.getVisitante().getId());
        visitante.getCredenciamentos().clear();
    	try {
	        List<FichaVisitanteDTO> lista = new ArrayList<FichaVisitanteDTO>();
	        FichaVisitanteDTO ficha = new FichaVisitanteDTO();
	        ficha.setTipoVisita(this.credenciamento.getTipoVisita());
	        ficha.setVisitante(this.getDadosVisitante(visitante));
	        RecuperandoView recuperando = this.getDadosRecuperando();
	        if (recuperando != null)
	        	ficha.getRecuperando().add(recuperando);	        
	        ficha.setDocumentos(this.getVisitanteDocumentos(visitante));
	        ficha.setCertidoes(this.getVisitanteCertidoes(visitante));
	        ficha.setEnderecos(this.getVisitanteEnderecos(visitante));
	        ficha.setContatos(this.getVisitanteContatos(visitante));
	        ficha.setCredenciamentos(this.getVisitanteCredenciamentos(visitante));
	        ficha.setDataValidade(this.credenciamento.getDataValidade());
	        lista.add(ficha);
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.FichaVisitante));
			RequestContext.getCurrentInstance().execute("download()");
    	
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir Ficha de Visitantes: " + e.getMessage());
            LoggerUtil.logErro(Acao.IMPRIMIR, "Ficha de Visitantes", e, log);
        }
    }
}

