package br.gov.mt.sejudh.sigepen.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.TypedQuery;

import br.gov.mt.sejudh.sigepen.dao.CredenciamentoDAO;
import br.gov.mt.sejudh.sigepen.dao.ParenteDAO;
import br.gov.mt.sejudh.sigepen.dao.PassagemDAO;
import br.gov.mt.sejudh.sigepen.dao.PedidoDAO;
import br.gov.mt.sejudh.sigepen.dao.ProcessoDAO;
import br.gov.mt.sejudh.sigepen.dao.RecuperandoDAO;
import br.gov.mt.sejudh.sigepen.dao.SolicitacaoVisitaDAO;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatusAlternativo;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDesligamento;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.Processo;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.SolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Stateless
public class PassagemService {

    @Inject
    private PassagemDAO dao;

    @Inject
    private PedidoDAO pedTransitoDao;

    @Inject
    private ProcessoDAO processoDao;

    @Inject
    private RecuperandoDAO recuperandoDao;

    @Inject
    private RecuperandoService recuperandoService;

    @Inject
    private CredenciamentoDAO credenciamentoDAO;

    @Inject
    private ParenteDAO parenteDAO;

    @Inject
    private Event<Recuperando> eventoRecuperando;
    
    @Inject
    private SolicitacaoVisitaDAO solicitacaoVisitaDAO;
    
    @Inject
    private DietaEspecialService dietaEspecialService;
    
    public Passagem buscaPassagemPorId(Passagem passagem) {
        return dao.buscaPassagemPorId(passagem);
    }

    public Recuperando buscarRecuperandoPorIdNovaPassagem(long id) {
        return dao.buscarRecuperandoPorIdNovaPassagem(id);
    }

    public Recuperando buscarRecuperandoPorIdDesligamento(long id) {
        return dao.buscarRecuperandoPorIdDesligamento(id);
    }
    
    @Interceptors(EJBExcInterceptor.class)
    public void excluirDesligar(Recuperando recuperando, Passagem passagem, PassagemLotacao lotacao) throws Exception {

        recuperando.setStatus(StatusRecuperando.ATIVO);
        
        passagem.setDataDocDesligamento(null);
        passagem.setDataSaida(null);
        passagem.setDocumentoDesligamento(null);
        passagem.setObsDesligamento(null);
        passagem.setTipoDesligamento(null);
        passagem.setNumeroDocumento(null);
        lotacao.setDataSaida(null);

        recuperando.getPassagens().remove(recuperando.getPassagemAtual());
        passagem.getLotacao().remove(passagem.getLotacaoAtual());
        passagem.getLotacao().add(lotacao);
        recuperando.getPassagens().add(passagem);

        recuperandoDao.salvar(recuperando);
    }

    @Interceptors(EJBExcInterceptor.class)
    public void desligar(Recuperando recuperando, Passagem passagem, PassagemLotacao lotacao) throws Exception {
    	
    	
        if (passagem.getDataSaida().after(Sigepen.getDataAtual())) {
            throw new ValidacaoException("Data de saída não pode ser maior que data atual.");
        }

        if (passagem.getDataSaida().before(lotacao.getDataEntrada()) || passagem.getDataSaida().equals(lotacao.getDataEntrada())) {
            throw new ValidacaoException("Data de saída menor ou igual que a data de entrada!");
        }

        if (pedTransitoDao.recuperandoEmMovimentacao(recuperando)) {
            throw new ValidacaoException("Recuperando em Movimentação, não pode ser Desligado!");
        }

        if (passagem.getTipoDesligamento() == TipoDesligamento.ALVARA_DE_SOLTURA) {
            recuperando = this.buscarProcessosERegimeDoRecuperando(recuperando);

            if (!recuperando.getRegimeAtual().getDescricao().equals("Aberto") && !recuperando.getRegimeAtual().getDescricao().equals("Réu Solto")) {
                throw new ValidacaoException("Recuperando com regime atual = \"" + recuperando.getRegimeAtual().getDescricao() + "\" não pode ser Desligado!");
            }
        }

        if (passagem.getTipoDesligamento() == TipoDesligamento.OUTRO_SISTEMA_PENITENCIARIO) {
            if (passagem.getEstabelecimentoDestino() == null) {
                throw new ValidacaoException("É necessário associar um estabelecimento.");
            }
        }

        recuperando.setStatus(StatusRecuperando.INATIVO);
        this.inativaParentes(recuperando);
        this.inativaCredenciamentoVisitante(recuperando);
        lotacao.setDataSaida(passagem.getDataSaida());

        recuperando.getPassagens().remove(recuperando.getPassagemAtual());
        passagem.getLotacao().remove(passagem.getLotacaoAtual());
        passagem.getLotacao().add(lotacao);
        recuperando.getPassagens().add(passagem);

        recuperandoDao.salvar(recuperando);
        
        dietaEspecialService.inativarDietasAtivasOuSuspensasDoRecuperando(
                recuperando.getId(), passagem.getDataSaida()
            );

        
        this.eventoRecuperando.fire(recuperando);
        
        this.inativarSolicitacaoVisita(recuperando);
    }

	private void inativaParentes(Recuperando recuperando) {
		List<Parente> parentes = this.parenteDAO.listarPorRecuperando(recuperando);
		for (Parente par : parentes) {
			if (par != null && par.getStatus().equals(SgpStatusAlternativo.A)) {
				par.setStatus(SgpStatusAlternativo.I);
			} else
				par.setStatus(par.getStatus());
		}
	}

    private void inativaCredenciamentoVisitante(Recuperando recuperando) {
    	this.credenciamentoDAO.inativarVisitantesRecuperando(recuperando);
    }

    private Recuperando buscarProcessosERegimeDoRecuperando(Recuperando recuperando) {
        List<Processo> processos = processoDao.processosPorRecuperando(recuperando.getId());

        if (!processos.isEmpty()) {
            Regime regime = recuperandoService.regimeAtualRecuperando(processos);

            if (regime != null) {
                recuperando.setRegimeAtual(regime);
            }
        }

        return recuperando;
    }

    public boolean possuiPasagemAberta(Recuperando rec) {
        return dao.possuiPassagemAberta(rec);
    }

    public boolean validarDataNovaPasagem(Recuperando rec, Date dataEntrada) {
        if (this.dao.possuiPassagem(rec)) {
            Date ultimaDataSaida = dao.dataUltimaSaida(rec);
            if (dataEntrada.before(ultimaDataSaida)) {
                return false;
            }
        }

        return true;
    }

    public List<Passagem> buscarPassagensDoRecuperando(Long id) {
        return dao.buscarPassagensDoRecuperando(id);
    }
    
	private void inativarSolicitacaoVisita(Recuperando recuperando) {
		List<Parente> parentes = this.parenteDAO.listarPorRecuperando(recuperando);
		for (Parente p : parentes) {
			Optional<SolicitacaoVisita> solicitacaoVisita = this.solicitacaoVisitaDAO.buscarPorParente(p);
			if (solicitacaoVisita.isPresent()) {
				SolicitacaoVisita solicitacaoVisitaRecuperada = solicitacaoVisita.get();
				if (solicitacaoVisitaRecuperada.getStatus().equals(SgpStatusAlternativo.A))
					solicitacaoVisitaRecuperada.setStatus(SgpStatusAlternativo.I);
				this.solicitacaoVisitaDAO.salvar(solicitacaoVisitaRecuperada);
			}
		}
	}

	public boolean buscarPrimeiraPassagem(long idRecuperando) {
		return this.dao.buscarPrimeiraPassagem(idRecuperando);
	}

	public Passagem buscarPassagemPorId(long idPassagem) {
		  return dao.buscaPassagemPorId(idPassagem);
	}
	
	public Passagem alterar(Passagem passagem) {
		if (passagem.getFoto().getId() == 0)
			recuperandoDao.salvarFoto(passagem.getFoto());
		
		return dao.salvar(passagem);
	}

	public Passagem salvar(Passagem passagem) {
		return dao.salvar(passagem);
	}
	
	public Date buscarDataEntradaAtualPorPessoa(Long pessoaId) {
	    Optional<Passagem> passagemOpt =
	        dao.buscaPassagemPorIdPessoa(pessoaId);

	    if (!passagemOpt.isPresent()) {
	        return null;
	    }

	    Passagem p = passagemOpt.get();

	    if (p.getDataSaida() != null) {
	        return null; // 
	    }

	    return p.getDataEntrada();
	}
	
	public Map<Long, Date> buscarDatasEntradaPorPessoas(List<Long> pessoaIds) {
	    if (pessoaIds == null || pessoaIds.isEmpty()) {
	        return new HashMap<>();
	    }
	    
	    Map<Long, Date> resultado = new HashMap<>();
	    
	    for (int i = 0; i < pessoaIds.size(); i += 1000) {
	        int end = Math.min(i + 1000, pessoaIds.size());
	        List<Long> chunk = pessoaIds.subList(i, end);
	        
	        String jpql = "SELECT p.recuperando.pessoa.id, p.dataEntrada " +
	                     "FROM Passagem p " +
	                     "WHERE p.recuperando.pessoa.id IN :pessoaIds " +
	                     "AND p.dataSaida IS NULL";
	        
	        TypedQuery<Object[]> query = dao.getEntityManager().createQuery(jpql, Object[].class);
	        query.setParameter("pessoaIds", chunk);
	        
	        List<Object[]> results = query.getResultList();
	        for (Object[] row : results) {
	            resultado.put((Long) row[0], (Date) row[1]);
	        }
	    }
	    
	    return resultado;
	}


}
