package br.gov.mt.sejudh.sigepen.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.FaseDoCredenciamentoDAO;
import br.gov.mt.sejudh.sigepen.dao.ImpedimentoDAO;
import br.gov.mt.sejudh.sigepen.dao.RecuperandoDAO;
import br.gov.mt.sejudh.sigepen.dao.VisitanteDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVisitanteImpedimento;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVwVisitanteRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Biometria;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.FaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.model.Impedimento;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.TipoVisitaCertidao;
import br.gov.mt.sejudh.sigepen.model.TipoVisitaDocumento;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.model.VisitanteCertidao;
import br.gov.mt.sejudh.sigepen.model.VisitanteDocumento;
import br.gov.mt.sejudh.sigepen.model.VisitanteFoto;
import br.gov.mt.sejudh.sigepen.model.view.VwVisitante;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@Stateless
public class VisitanteService extends BaseService<Visitante, Long> {

	
	private static final long serialVersionUID = 7230046305068303409L;
	

    private VisitanteDAO visitanteDAO;
    
	@Inject
    private PessoaService pessoaService;
	@Inject
    private CredenciamentoService credenciamentoService;
	@Inject
    private RecuperandoService recuperandoService;

    @Inject
    private BiometriaService biometriaService;

    @Inject
    private RecuperandoDAO recuperandoDAO;
    private Optional<Passagem> passagemRecuperada;

    @Inject
    private ImpedimentoDAO impedimentoDAO;

    @Inject
    private FaseDoCredenciamentoDAO faseDoCredenciamentoDAO;

    @Inject
    public void setVisitanteDAO(VisitanteDAO visitanteDAO) {
		this.visitanteDAO = visitanteDAO;
		this.setDao(visitanteDAO);
	}

    public Visitante incluir(Visitante visitante, Recuperando recuperando, boolean salvarFasesAuto, boolean tornarVisitante) throws Exception {
    	
    	List<Credenciamento> recs = new ArrayList<Credenciamento>();
    	recs.addAll(visitante.getCredenciamentos());
    	
    	visitante.getCredenciamentos().clear();
    	boolean already = false;
    	for(Credenciamento x : recs) {
    		if(x.getId() == null && !already) {
    			visitante.getCredenciamentos().add(x);
    			already = true;
    		}
    		else
    			visitante.getCredenciamentos().add(x);
    	}
    	if(visitante.getPessoa() != null && visitante.getPessoa().getVisitante() == null)
    		visitante.getPessoa().setVisitante(new Visitante());
    	visitante.getPessoa().setVisitante(visitante);
        this.prepararVisitanteCadastro(visitante);
        
        if(visitante.getPessoa().getId() == 0)
        	this.pessoaService.salvarPessoaVisitante(visitante.getPessoa());
        
        
        if(tornarVisitante) {
	        Pessoa p = this.pessoaService.buscarPessoaPorId(visitante.getPessoa().getId());
	        visitante.setPessoa(p);
	        for(VisitanteFoto ft : visitante.getFotos()) {
	        	Imagem imagem = new Imagem();
	        	imagem.setDataInclusao(new Date());
	        	imagem.setArquivo(ft.getImagem().getArquivo());
	        	ft.setImagem(imagem);
	        }
        }

        List<Credenciamento> vrsAux = new ArrayList<Credenciamento>();
        
        for(Credenciamento x : visitante.getCredenciamentos()) {
        	Credenciamento y = new Credenciamento();
        	y.setId(x.getId());
        	vrsAux.add(y);
        }
        
        Visitante visitanteSalvo = super.salvar(visitante);
        Credenciamento novo = null;

        for(Credenciamento x : visitanteSalvo.getCredenciamentos()) {
        	boolean achou = false;
        	for(Credenciamento vr : vrsAux) {
        		if(x.getId().equals(vr.getId()))
        			achou = true;
        	}
        	if(!achou)
        		novo = x;
        }
        
        if(novo != null)
        	visitanteSalvo.setCadastro(novo);
        	
        this.validarVisitanteJaFoiRecuperando(visitante);
        
        visitanteSalvo = this.salvarFasesAutomaticamenteQdoCredenciamentoTipoVisitaIgualNao(visitanteSalvo);
        
        return visitanteSalvo;
    }

    private Visitante salvarFasesAutomaticamenteQdoCredenciamentoTipoVisitaIgualNao(Visitante visitante) throws ValidacaoException {
    	visitante = this.visitanteDAO.buscarPorId(visitante.getId());
    	for(Credenciamento credenciamento : visitante.getCredenciamentos()) {
            if (credenciamento.getTipoVisita().getObrigatorioCredenciamento().equals(SimNao.N) && credenciamento != null && (credenciamento.getFasesDoCredenciamento() == null || (credenciamento.getFasesDoCredenciamento() != null && credenciamento.getFasesDoCredenciamento().size() == 0))) {
                for(Credenciamento vr : visitante.getCredenciamentos()) {
                	List<FaseDoCredenciamento> lista = new ArrayList<FaseDoCredenciamento>();
                	if(vr.getId() == credenciamento.getId()) {
                		FaseDoCredenciamento vc = new FaseDoCredenciamento();
                        vc.setCredenciamento(vr);
                        vc.setFaseCredenciamento(FaseCredenciamento.APROVACAO);
                        vc.setStatusCredenciamento(StatusCredenciamento.PENDENTE);
                        vc.setDataRecebimento(new Date());
                        vc.setUsuarioAprovacao(SecurityUtil.getUsuario());
                        lista.add(vc);
                        vr.setFaseCredenciamento(FaseCredenciamento.APROVACAO.getId());
                        vr.setStatusCredenciamento(StatusCredenciamento.PENDENTE.getId());
                        this.faseDoCredenciamentoDAO.salvar(vc);
                        return visitante;
                	}
                }
            }
    	}
        return visitante;
    }
    
    public void reativarVisitante(Visitante visitante) throws ValidacaoException {
        visitante.setCredenciamentos(this.credenciamentoService.listarCredenciamentoPorVisitante(visitante));
        if (visitante.getCredenciamentos().stream().noneMatch(
                credenciamento -> credenciamento.getRecuperando().getStatus() == StatusRecuperando.ATIVO)) {
            throw new ValidacaoException("O Recuperando vinculado não está ativo!");
        }
        ;

        visitante.setStatus(null);
        this.prepararStatusVisitante(visitante);

        super.salvar(visitante);
    }

    private void prepararVisitanteCadastro(Visitante visitante) {
        this.prepararStatusVisitante(visitante);

        if (visitante.getEmancipado() == SimNao.S)
            visitante.setResponsavel(null);

        if (visitante.getUnidadePrisional() == null)
            visitante.setUnidadePrisional(SecurityUtil.getUnidadeSelecionada());

        if (visitante.getDataHoraCadastro() == null)
            visitante.setDataHoraCadastro(new Date());

        if (visitante.getUsuarioCadastro() == null)
            visitante.setUsuarioCadastro(SecurityUtil.getUsuario());
    }

    public Visitante atribuirStatusVisitantePosCredenciamento(Visitante visitante) {
    	return this.visitanteDAO.atribuirStatusVisitantePosCredenciamento(visitante);
    }

    public Visitante atribuirStatusVisitantePosCredenciamentoInativar(Visitante visitante) {
    	return this.visitanteDAO.atribuirStatusVisitantePosCredenciamentoInativar(visitante);
    }

    public Visitante calcularStatusAtual(Visitante visitante) {
    	return this.visitanteDAO.calcularStatusAtual(visitante);
    }
    
    private void prepararStatusVisitante(Visitante visitante) {
    	
        if (visitante.getTipoVisita().getObrigatorioCredenciamento() == SimNao.N) {
            visitante.setFaseAtual(FaseCredenciamento.APROVACAO.getId());
            visitante.setSituacaoAtual(StatusCredenciamento.PENDENTE.getId());
        }

        if (visitante.getStatus() == null) {
            visitante.setStatus(StatusVisitante.P);
        }
    }

    public Boolean exibirEmancipado(Visitante visitante) {
    	if(visitante == null)
    		return false;
    	
    	if(visitante.getPessoa() == null)
    		return false;
    	
        if (visitante.getPessoa().getDataNascimento() == null) {
            return false;
        }

        Calendar max = Calendar.getInstance();
        max.add(Calendar.YEAR, -18);

        return visitante.getPessoa().getDataNascimento().after(max.getTime());
    }

    public void validarVisitanteJaFoiRecuperando(Visitante visitante) throws ValidacaoException {
        if (visitante.getId() != null || visitante.getPessoa().getId() == 0) {
            return;
        }
        Date ultimaPassagem = this.recuperandoService.buscarUltimaPassagemRecuperandoPessoa(visitante.getPessoa().getId());
        if (ultimaPassagem == null) {
            return;
        }
        if (DateUtils.addMonths(ultimaPassagem, 6).after(DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE))) {
            throw new ValidacaoException("Visitante foi recuperando a menos de 6 meses, não permitido o cadastro");
        }
    }


    public boolean isDataSaidaMenorSeisMeses(Date paramDataSaida) {
        LocalDate dataAtual = LocalDate.now();
        LocalDate dataSaida = paramDataSaida.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period intervalo = Period.between(dataSaida, dataAtual);
        int meses = intervalo.getMonths();

        if (meses < 6) {
            return true;
        }
        return false;
    }

    public Optional<Recuperando> buscarExRecuperando(Long idPessoa) {
        return this.recuperandoDAO.buscarRecuperandoPorIdPessoa(idPessoa);
    }

    public Optional<Passagem> getPassagemRecuperada() {
        return passagemRecuperada;
    }

    public Optional<Visitante> buscarPorIdPessoa(Long id) {
        return this.visitanteDAO.buscarPorIdPessoa(id);
    }

    public Optional<List<Impedimento>> obtemImpedimentos(Long id) {
        return this.impedimentoDAO.buscarImpedimentosPorIdVisitante(id);
    }

    public void atualizaStatusVisitante(Long id) {
        Visitante visitante = this.dao.buscarPorId(id);
        visitante.setStatus(StatusVisitante.A);
    }

    public void atualizaStatusImpedimento(Long id) {
        Impedimento impedimento = impedimentoDAO.buscarPorId(id);
        impedimento.setStatus(SgpStatus.I);
    }

    public List<Visitante> buscarDependentesDoVisitanteResponsavel(Visitante visitante) {
        return this.visitanteDAO.buscarDependenteVisitanteResponsavel(visitante);
    }

	public List<Visitante> listarRelatorio(FiltroVwVisitanteRelatorio filtro, boolean isBuscaRelatorio) {
		
		return this.visitanteDAO.listarRelatorio(filtro);
	}
	
	@Override
	public void excluir(Visitante _visitante) throws ValidacaoException {
		List<Visitante> dependentes = this.buscarDependentesDoVisitanteResponsavel(_visitante);
		if (dependentes.size() > 0) {
			dependentes.stream().forEach(dep -> {
				this.excluirBiometria(dep.getPessoa());
				this.visitanteDAO.excluir(dep);
			});
		}
		Visitante visitante = this.visitanteDAO.buscarPorId(_visitante.getId());
		this.excluirBiometria(visitante.getPessoa());
		
		super.excluir(visitante);
	}
	
	public Visitante buscarVisitante(VwVisitante visitante){
		return this.visitanteDAO.buscarVisitante(visitante);
	}
	
	public void inativarVisitante(Visitante visitante) {
		this.visitanteDAO.inativarVisitante(visitante);
		
		this.visitanteDAO.inativarVisitantesSobResponsabilidade(visitante);
	}
	
	public Visitante removeDocumentosDuplicados(Visitante visitante) throws ValidacaoException {
		return this.visitanteDAO.removeDocumentosDuplicados(visitante);
	}
	
	public Visitante salvarDocumentos(Visitante visitante) throws ValidacaoException {
		return this.visitanteDAO.salvarDocumentos(visitante);
	}

	public Visitante salvarEnderecos(Visitante visitante) throws ValidacaoException {
		return this.visitanteDAO.salvarEnderecos(visitante);
	}

	public Visitante salvarContatos(Visitante visitante) throws ValidacaoException {
		return this.visitanteDAO.salvarContatos(visitante);
	}

	public Visitante salvarCertidoes(Visitante visitante) throws ValidacaoException {
		return this.visitanteDAO.salvarCertidoes(visitante);
	}
	
	public void excluirDocumento(VisitanteDocumento vd) throws ValidacaoException {
		this.visitanteDAO.excluirDocumento(vd);
	}

	public int validarConjugeCadastro(Recuperando _recuperando, Visitante _visitante) throws Exception {
		return this.visitanteDAO.validarConjugeCadastro(_recuperando, _visitante);
	}

	public void inativarVisitantes() {
		this.visitanteDAO.inativarVisitantes();
	}

	public void sincronizar() {
		this.visitanteDAO.sincronizar();
	}
	
	public List<Visitante> buscarPorFiltro(FiltroVisitanteImpedimento filtro, boolean isRelatorio) {		 
		return this.visitanteDAO.buscarPorFiltro(filtro, isRelatorio);
    }
	
	public Visitante buscarVisitantePorIdPessoa(Long id) {
		return this.visitanteDAO.buscarVisitantePorIdPessoa(id);
	}

	public boolean existeCredencimento(VisitanteDocumento visitanteDocumento) {
		return this.visitanteDAO.existeCredencimento(visitanteDocumento);
	}

	public boolean existeCredencimento(VisitanteCertidao certidao) {
		return this.visitanteDAO.existeCredencimento(certidao);
	}

	public FaseCredenciamento[] carregarFasesCredenciamento(long idUnidade, boolean todosRecuperandos) {
		return this.visitanteDAO.carregarFasesCredenciamento(idUnidade, todosRecuperandos);
	}
	
	private void excluirBiometria(Pessoa pessoa) {
		if(visitanteDAO.foiRecuperando(pessoa.getId()))
			return;
		if(pessoa != null && pessoa.getBiometria() != null) {
			Biometria b = pessoa.getBiometria();
			b.setIdPessoa(pessoa.getId());
			biometriaService.excluirBiometriaSDK(b);
		}
	}
	
	@Override
    public Visitante buscarPorId(Long id, Boolean buscarAuditoria) {
        return this.dao.buscarPorId(id, buscarAuditoria);
    }

	public Imagem buscarImagemFrente(Long visitanteId) {
		return visitanteDAO.buscarImagemFrente(visitanteId);
	}

	public void excluirCredenciamentoDoVisitante(Credenciamento cred) {
		visitanteDAO.excluirCredenciamentoDoVisitante(cred);
	}

	public void buscarRecuperandosDoVisitante(Visitante visitante) {
		visitanteDAO.buscarRecuperandosDoVisitante(visitante);
	}
    
    public boolean validarCertidoesObrigatorias(List<String> certidoesDescricao, TipoVisita tipoVisita, Visitante visitante) {
    	List<TipoVisitaCertidao> certidoesObrigatorias = tipoVisita.getTipoVisitaCertidao();
    	List<VisitanteCertidao> certidoesDoVisitante = visitante.getCertidoes();
    	
    	certidoesObrigatorias.stream().forEach(d -> {
    		Optional<VisitanteCertidao> optVisitanteCert = null;
    		
    		switch (d.getTipoObrigatoriedade()) {
				case EMANCIPADO:
					if (visitante.getEmancipado() != null && visitante.getEmancipado().equals(SimNao.S)) {
						optVisitanteCert = certidoesDoVisitante.stream().filter(v -> v.getCertidao().getId().equals(d.getCertidao().getId())).findFirst();
					}
					break;
				case MAIOR_IDADE:
					if (!visitante.getPessoa().getMenorIdade()) {
						optVisitanteCert = certidoesDoVisitante.stream().filter(v -> v.getCertidao().getId().equals(d.getCertidao().getId())).findFirst();
					}
					break;
				case MENOR_IDADE:
					if (visitante.getPessoa().getMenorIdade()) {
						optVisitanteCert = certidoesDoVisitante.stream().filter(v -> v.getCertidao().getId().equals(d.getCertidao().getId())).findFirst();
					}
					break;
				case TODOS:
					optVisitanteCert = certidoesDoVisitante.stream().filter(v -> v.getCertidao().getId().equals(d.getCertidao().getId())).findFirst();
					break;
    		}
    		
			if (optVisitanteCert != null && !optVisitanteCert.isPresent() && certidoesDescricao != null) {
				certidoesDescricao.add(d.getCertidao().getDescricao());
			}
    	});
    	
    	return certidoesDescricao.size() > 0;
    }
    
    //Obrigatório ter adicionado pelo menos um documento com foto
    public boolean validarDocumentosObrigatorios(List<String> documentosDescricao, TipoVisita tipoVisita, Visitante visitante) {
    	List<TipoVisitaDocumento> documentosObrigatorios = tipoVisita.getTipoVisitaDocumento();
    	List<VisitanteDocumento> documentosDoVisitante = visitante.getVisitanteDocumentos();

    	//Docs com Foto
    	List<String> docsObrigatoriosComFoto = documentosObrigatorios.stream().filter(d -> d.getTipoDocumento().getPossuiFoto()).map(m -> m.getTipoDocumento().getDescricao()).collect(Collectors.toList());
        List<String> docsDoVisitanteComFoto = documentosDoVisitante.stream().filter(d -> d.getDocumento().getTipoDocumento().getPossuiFoto()).map(m -> m.getDocumento().getTipoDocumento().getDescricao()).collect(Collectors.toList());
 
        boolean anyMatch = docsObrigatoriosComFoto.stream().anyMatch(e -> docsDoVisitanteComFoto.contains(e));
        
        if (!anyMatch) {
        	documentosDescricao.add("Adicione pelo menos um dos seguintes documentos: " + String.join(", ", docsObrigatoriosComFoto));
        }
    	
    	//Docs sem Foto
        List<String> docs = new ArrayList<String>();
        
    	documentosObrigatorios.stream().filter(d -> !d.getTipoDocumento().getPossuiFoto()).forEach(d -> {
    		Optional<VisitanteDocumento> optVisitanteDoc = null;
    		
    		switch (d.getTipoObrigatoriedade()) {
				case EMANCIPADO:
					if (visitante.getEmancipado() != null && visitante.getEmancipado().equals(SimNao.S)) {
						optVisitanteDoc = documentosDoVisitante.stream().filter(v -> v.getDocumento().getTipoDocumento().equals(d.getTipoDocumento()) && 
												!v.getDocumento().getTipoDocumento().getPossuiFoto()).findFirst();
					}
					break;
				case MAIOR_IDADE:
					if (!visitante.getPessoa().getMenorIdade()) {
						optVisitanteDoc = documentosDoVisitante.stream().filter(v -> v.getDocumento().getTipoDocumento().equals(d.getTipoDocumento()) && 
								!v.getDocumento().getTipoDocumento().getPossuiFoto()).findFirst();
					}
					break;
				case MENOR_IDADE:
					if (visitante.getPessoa().getMenorIdade()) {
						optVisitanteDoc = documentosDoVisitante.stream().filter(v -> v.getDocumento().getTipoDocumento().equals(d.getTipoDocumento()) && 
								!v.getDocumento().getTipoDocumento().getPossuiFoto()).findFirst();
					}
					break;
				case TODOS:
					optVisitanteDoc = documentosDoVisitante.stream().filter(v -> v.getDocumento().getTipoDocumento().equals(d.getTipoDocumento()) && 
							!v.getDocumento().getTipoDocumento().getPossuiFoto()).findFirst();
					break;
    		}
    		
			if (optVisitanteDoc != null && !optVisitanteDoc.isPresent()) {
				docs.add(d.getTipoDocumento().getDescricao());
			}
    	});
    	
    	if (docs.size() > 0) {
    		documentosDescricao.add("Adicione o(s) seguinte(s) documento(s): " + String.join(", ", docs));
    	}
    	
    	return documentosDescricao.size() > 0;
    }

	public boolean verificaVisitanteResponsavelPossuiCredenciamento(Visitante visitante, Recuperando recuperando, TipoVisita tipoVisita) {
		return visitanteDAO.verificaVisitanteResponsavelPossuiCredenciamento(visitante, recuperando, tipoVisita);
	}
}
