package br.gov.mt.sejudh.sigepen.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.FaseDoCredenciamentoDAO;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.ParametroVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.FaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.ParametroUnidade;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.util.MaskUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@Stateless
public class FaseDoCredenciamentoService extends BaseService<FaseDoCredenciamento, Long> {

	private static final long serialVersionUID = 6316034311469834087L;

    private FaseDoCredenciamentoDAO faseDoCredenciamentoDAO;
	
	@Inject
    private VisitanteService visitanteService;
    
	@Inject
	private ParametroUnidadeService parametroUnidadeService;
    
	@Inject
	private CredencialCertidaoService credencialCertidaoService;
    
	@Inject
	private CredencialDocumentoService credencialDocumentoService;
    
	@Inject
	private CredenciamentoService credenciamentoService;

	@Inject
    public void setFaseDoCredenciamentoDAO(FaseDoCredenciamentoDAO faseDoCredenciamentoDAO) {
		this.faseDoCredenciamentoDAO = faseDoCredenciamentoDAO;
		this.setDao(faseDoCredenciamentoDAO);
	}

    @Transactional
    @Override
    public FaseDoCredenciamento salvar(FaseDoCredenciamento fase) throws ValidacaoException {
    	
        if (fase.getStatusCredenciamento().equals(StatusCredenciamento.APROVADO)) {
            credenciamentoService.salvar(fase.getCredenciamento());
        }

        return super.salvar(fase);

    }

    public boolean isVerificaSeCredenciamentoEhParaMenorQueIraCompletar18Anos(FaseDoCredenciamento fase) {
        LocalDate dataNascimentoVisitante = this.converteDataNascimentoVisitanteToLocalDate(fase.getCredenciamento().getVisitante());
        		
        Optional<LocalDate> dataValidadeInformada =  this.converteDataValidadeToLocalDate(fase);
        if (dataNascimentoVisitante != null && dataValidadeInformada.isPresent()) {
            LocalDate dataAtual = LocalDate.now();
            Period idade = Period.between(dataNascimentoVisitante, dataAtual);

            if (idadeDoCredenciadoEhIgualA17Anos(idade.getYears())) {
                LocalDate dataValidade = Instant.ofEpochMilli(fase.getCredenciamento().getDataValidade().getTime()).atZone(ZoneId.systemDefault()).toLocalDate(); 

            	if(dataValidade.isAfter(dataNascimentoVisitante.plusYears(18))) {
            		return true;
            	}
            }
        }

        return false;
    }

    private boolean dataValidadeEhIgualDataNascimento(LocalDate dataValidadeInformada, LocalDate dataNascimentoVisitante) {
        return  dataValidadeInformada.getMonth().getValue() == dataNascimentoVisitante.getMonth().getValue() &&
                dataValidadeInformada.getDayOfMonth() == dataNascimentoVisitante.getDayOfMonth();
    }

    private boolean idadeDoCredenciadoEhIgualA17Anos( int idadeMenorCredenciado) {
        return idadeMenorCredenciado == 17;
    }

    private Optional<LocalDate> converteDataValidadeToLocalDate(FaseDoCredenciamento fase) {
        if(fase.getCredenciamento().getDataValidade() != null ) {
            Date dataValidade = fase.getCredenciamento().getDataValidade();
            LocalDate dataValidadeConvertida = Instant.ofEpochMilli(dataValidade.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            return Optional.of(dataValidadeConvertida);
        }

        return Optional.empty();
    }

    private LocalDate converteDataNascimentoVisitanteToLocalDate(Visitante visitante) {
        Date dataNascimento = visitante.getPessoa().getDataNascimento();
        LocalDate dataNascimentoConvertida = Instant.ofEpochMilli(dataNascimento.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        return dataNascimentoConvertida;
    }

   
    
    public List<FaseDoCredenciamento> lancarCredenciamentoDoDependente(FaseDoCredenciamento fase) throws ValidacaoException {
    	this.validaFase(fase);
    	
        if (this.getStatusCredenciamentoInativoOuNaoAprovado(fase)) {
            Visitante responsavel = fase.getCredenciamento().getVisitante();
            List<Visitante> dependentes = this.visitanteService.buscarDependentesDoVisitanteResponsavel(responsavel);

            for (Visitante dependente : dependentes.stream().distinct().collect(Collectors.toList())) {
                return this.lancaCredenciamentoDependenteQdoRespInativoOuNaoAprovado(dependente);
            }
        }
        return null;
    }

    private List<FaseDoCredenciamento> lancaCredenciamentoDependenteQdoRespInativoOuNaoAprovado(Visitante dependente) throws ValidacaoException {
    	this.validaResponsavelCredenciamento(dependente);
    	
        dependente = visitanteService.buscarPorId(dependente.getId());
        Visitante responsavel = dependente.getResponsavel();

        List<FaseDoCredenciamento> dependentes = new ArrayList<>();
        for (Credenciamento vrDoResponsavel : responsavel.getCredenciamentos()) {
            for (Credenciamento vrDoDependente : dependente.getCredenciamentos()) {
                if (vrDoResponsavel.getRecuperando().getId() == vrDoDependente.getRecuperando().getId()) {
                	FaseDoCredenciamento credenciamentoResp = vrDoResponsavel.getUltimoCredenciamento();

                    if (getStatusCredenciamentoInativoOuNaoAprovado(vrDoResponsavel.getUltimoCredenciamento())) {
                        if (vrDoDependente.getUltimoCredenciamento() == null) {
                        	FaseDoCredenciamento vc = new FaseDoCredenciamento();
                            vc.setStatusCredenciamento(credenciamentoResp.getStatusCredenciamento());
                            vc.setCredenciamento(vrDoDependente);
                            vc.setDataAprovacao(credenciamentoResp.getDataAprovacao());
                            vc.setUsuarioAprovacao(credenciamentoResp.getUsuarioAprovacao());
                            vc.setFaseCredenciamento(credenciamentoResp.getFaseCredenciamento());
                            vc.setDataRecebimento(credenciamentoResp.getDataRecebimento());
                            vc.setObservacao(credenciamentoResp.getObservacao());
                            vc.setDataDespacho(credenciamentoResp.getDataDespacho());

                            FaseDoCredenciamento vcSalvo = faseDoCredenciamentoDAO.salvar(vc);
                            dependentes.add(vcSalvo);
                        }else if (getStatusCredenciamentoInativoOuNaoAprovado(vrDoDependente.getUltimoCredenciamento())) {
                            if (getStatusCredenciamentoInativoOuNaoAprovado(vrDoResponsavel.getUltimoCredenciamento())) {
                                List<FaseDoCredenciamento> credencialList = faseDoCredenciamentoDAO.buscaPorCampo("credenciamento", vrDoDependente);
                                FaseDoCredenciamento fase = this.faseDoCredenciamentoDAO.salvar(credencialList.get(0));
                                fase.setStatusCredenciamento(vrDoResponsavel.getUltimoCredenciamento().getStatusCredenciamento());
                                fase = this.faseDoCredenciamentoDAO.salvar(fase);

                                dependentes.add(fase);
                            }
                        }
                    }

                }
            }
        }

        return dependentes;
    }

    private boolean getStatusCredenciamentoInativoOuNaoAprovado(FaseDoCredenciamento ultimoCredenciamento) {
        return ultimoCredenciamento.getStatusCredenciamento().equals(StatusCredenciamento.NAO_APROVADO) ||
                ultimoCredenciamento.getStatusCredenciamento().equals(StatusCredenciamento.INATIVO);
    }

    private void atribuirFaseCredenciamento(FaseDoCredenciamento fase) {
        if (fase.getFaseCredenciamento() == null) {
            fase.setFaseCredenciamento(FaseCredenciamento.RECEBIMENTO);
            return;
        }

        List<ParametroUnidade> parametros = parametroUnidadeService.listarPorUnidade(SecurityUtil.getUnidadeSelecionada());

        switch (fase.getFaseCredenciamento()) {
            case RECEBIMENTO:
                this.atribuirFaseRecebimento(fase, parametros);
                break;
            case CHECAGEM:
                this.atribuirFaseChecagem(fase, parametros);
                break;
            case CONFERENCIA:
                this.atribuirFaseConferencia(fase, parametros);
                break;
            case ANALISE:
                this.atribuirFaseAnalise(fase);
                break;
            case APROVACAO:
                this.atribuirFaseAprovacao(fase);
                break;
            default:
                break;
        }
    }

    private void atribuirFaseRecebimento(FaseDoCredenciamento fase, List<ParametroUnidade> parametros) {
        if (parametros.stream().noneMatch(
                param -> param.getNome() == ParametroVisitante.CHECAGEM_DOCUMENTO && param.getValidar() == SimNao.S)
        ) {
            this.atribuirFaseChecagem(fase, parametros);
            return;
        }

        fase.setFaseCredenciamento(FaseCredenciamento.CHECAGEM);
    }

    private void atribuirFaseChecagem(FaseDoCredenciamento fase, List<ParametroUnidade> parametros) {
        if (parametros.stream().noneMatch(
                param -> param.getNome() == ParametroVisitante.CONFERENCIA_AUTENTICACAO && param.getValidar() == SimNao.S)
        ) {
            this.atribuirFaseConferencia(fase, parametros);
            return;
        }

        fase.setFaseCredenciamento(FaseCredenciamento.CONFERENCIA);
    }

    private void atribuirFaseConferencia(FaseDoCredenciamento fase, List<ParametroUnidade> parametros) {
        if (parametros.stream().noneMatch(
                param -> param.getNome() == ParametroVisitante.ANALISE_CERTIDAO && param.getValidar() == SimNao.S)
        ) {
            this.atribuirFaseAnalise(fase);
            return;
        }

        fase.setFaseCredenciamento(FaseCredenciamento.ANALISE);
    }

    private void atribuirFaseAnalise(FaseDoCredenciamento fase) {
        fase.setFaseCredenciamento(FaseCredenciamento.APROVACAO);
    }

    private void atribuirFaseAprovacao(FaseDoCredenciamento fase) {
        fase.setFaseCredenciamento(FaseCredenciamento.RECEBIMENTO);
    }

    public List<StatusCredenciamento> statusCredenciamentosDisponiveis(FaseDoCredenciamento fase) {
        if (fase == null || fase.getFaseCredenciamento() != FaseCredenciamento.APROVACAO) {
            return Arrays.asList(StatusCredenciamento.REGULAR, StatusCredenciamento.IRREGULAR);
        }

        return Arrays.asList(StatusCredenciamento.APROVADO, StatusCredenciamento.NAO_APROVADO, StatusCredenciamento.INATIVO);
    }

    public void inativarCredenciamentoPorRecuperando(Recuperando recuperando) {
        List<Credenciamento>  credenciamentos = this.credenciamentoService.buscaPorFiltro("recuperando", recuperando);

        for (Credenciamento credenciamento : credenciamentos) {
            faseDoCredenciamentoDAO.inativarCredenciamentoRecuperando(credenciamento);
        }
    }
    
    public void inativarUltimoCredenciamentoVR(Credenciamento vr) {
    	faseDoCredenciamentoDAO.inativarUltimoCredenciamentoVR(vr);
    }

    public void excluirCredenciamento(FaseDoCredenciamento fase) {
    	FaseDoCredenciamento t = faseDoCredenciamentoDAO.buscarPorId(fase.getId());
    	faseDoCredenciamentoDAO.excluirFaseCredenciamento(t);
    }
    public FaseDoCredenciamento buscarCredencialPorId(long id) {
    	return faseDoCredenciamentoDAO.buscarCredencialPorId(id);
    }

	public void validarDataMinima(FaseDoCredenciamento vc) throws ValidacaoException {
		
		FaseDoCredenciamento credenciamentoAnterior = vc.getCredenciamento().getFasesDoCredenciamento().stream()
                .filter(x -> x.getStatusCredenciamento() != StatusCredenciamento.INATIVO && x.getFaseCredenciamento().getId() < vc.getFaseCredenciamento().getId())
                .max(Comparator.comparing(o -> o.getFaseCredenciamento().getId())).orElse(new FaseDoCredenciamento());

        if (credenciamentoAnterior != null && credenciamentoAnterior.getId() != null &&
        		vc.getDataRecebimento().before(credenciamentoAnterior.getDataRecebimento())
        		) {
            throw new ValidacaoException("A Data de recebimento não pode ser anterior a " + MaskUtil.getFormatoData().format(credenciamentoAnterior.getDataRecebimento()) + "!");
        }
	}

	public FaseDoCredenciamento buscarDocumentosCertidoes(FaseDoCredenciamento c) {
		return this.faseDoCredenciamentoDAO.buscarDocumentosCertidoes(c);
	}

	public FaseDoCredenciamento buscarCredenciamento(long idCredenciamento) {
		return this.faseDoCredenciamentoDAO.buscarCredenciamento(idCredenciamento);
	}

	public FaseDoCredenciamento atribuirProximaFaseCredenciamento(Credenciamento credenciamento) {
		return this.faseDoCredenciamentoDAO.atribuirProximaFaseCredenciamento(credenciamento);
	}
	
	private void validaResponsavelCredenciamento(Visitante responsavel) {
    	if(responsavel == null) {
    		throw new IllegalArgumentException("O objeto responsavel não pode ser vazio.");
    	}
    	
    	if(responsavel.getCredenciamentos() == null) {
    		throw new IllegalArgumentException("O objeto credenciamento do responsavel não pode ser vazio.");
    	}
    }
	
	 public void validaFase(FaseDoCredenciamento fase) {
	    	if(fase == null) {
	    		throw new IllegalArgumentException("Fase do credenciamento está vazia.");
	    	}
	    	
	    	if(fase.getCredenciamento() == null) {
	    		throw new IllegalArgumentException("Credenciamento está vazia.");
	    	}
	    	
	    	if(fase.getCredenciamento().getVisitante() == null) {
	    		throw new IllegalArgumentException("Credenciamento do visitante está vazia.");
	    	}
	    }
}
