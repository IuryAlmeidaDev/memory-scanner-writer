package br.gov.mt.sejudh.sigepen.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.transaction.Transactional;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.CredenciamentoDAO;
import br.gov.mt.sejudh.sigepen.dao.ParenteDAO;
import br.gov.mt.sejudh.sigepen.dao.PessoaDAO;
import br.gov.mt.sejudh.sigepen.dao.ProcessoDAO;
import br.gov.mt.sejudh.sigepen.dao.RecuperandoDAO;
import br.gov.mt.sejudh.sigepen.dao.SolicitacaoVisitaDAO;
import br.gov.mt.sejudh.sigepen.dao.UnificarRecuperandoDAO;
import br.gov.mt.sejudh.sigepen.dao.VisitanteDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperando;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperandoView;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnificarRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatusAlternativo;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.StatusProcesso;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Alcunha;
import br.gov.mt.sejudh.sigepen.model.Biometria;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.Etnia;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.model.OutroNome;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Particularidade;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Processo;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.SolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.model.dto.DadosRecuperandoUnificacaoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.ExtratoRecuperandoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.ExtratoRecuperandoDTO.PassagemExtratoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RecuperandoDTO;
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@Stateless
public class RecuperandoService {

	@Inject
	private RecuperandoDAO dao;
	
	@Inject
	private PessoaDAO pessoaDao;
	
	@Inject
	private ProcessoDAO processoDao;
	
	@Inject
	private AuditoriaDAO auditDao;

	@Inject
	private ParenteDAO parenteDAO;

	@Inject
	private PassagemService passagemService;
	
	@Inject
	private PedidoService pedidoService;
	
	@Inject
	private AtestadosService atestadosService;
	
	@Inject
	private HistoricoRecuperandoService historicoService;

	@Inject
	private SolicitacaoVisitaDAO solicitacaoVisitaDAO;
	
	private static final Integer LIMITE_VISITANTE_MAIOR_IDADE = 5;

	@Inject
	private CredenciamentoDAO credenciamentoDAO;

	@Inject
	private VisitanteDAO visitanteDAO;

    @Inject
    private VisitanteService visitanteService;

    @Inject
    private FaseDoCredenciamentoService faseDoCredenciamentoService;

	@Inject
	private UnificarRecuperandoDAO unificarDao;
	
	@Inject
	private FacesUtil facesUtil;

	public void buscarListaFaltaDisciplinarPorRecuperandoId (Long id) {
		
	}
	
	public int buscarQuantidadeFiltrada(FiltroRecuperando filtro) {
		return dao.buscarQuantidadeFiltrada(filtro);
	}
	
	public List<RecuperandoDTO> buscarRecuperandosPorFiltro(FiltroRecuperando filtro, boolean isBuscaRelatorio) {
		List<RecuperandoDTO> recuperandos = dao.buscarRecuperandosPorFiltro(filtro, isBuscaRelatorio);

		return recuperandos;
	}
	
    public int buscarQuantidadeFiltrada(FiltroRecuperandoView filtro) {
		return dao.buscarQuantidadeFiltrada(filtro);
	}

    public List<RecuperandoView> buscarRecuperandosPorFiltroEAtestados(FiltroRecuperandoView filtro, boolean isBuscaRelatorio) {
    	List<RecuperandoView> recuperandos = dao.buscarRecuperandosPorFiltro(filtro, isBuscaRelatorio);
    	
    	for (RecuperandoView r : recuperandos) {
    		r.setPossuiAtestadoUnidade(atestadosService.verificaRecuperandoPossuiPassagemNaUnidadeLogada(r.getId()));
		}
    	
    	return recuperandos;
    }
    
	public List<RecuperandoView> buscarRecuperandosPorFiltro(FiltroRecuperandoView filtro, boolean isBuscaRelatorio) {
		List<RecuperandoView> recuperandos = dao.buscarRecuperandosPorFiltro(filtro, isBuscaRelatorio);
		
		if (filtro.getIsBuscarFotoFrente()) {
			for (RecuperandoView r : recuperandos) {
				if (r.getId() != null)
					r.setLotacaoAtual(dao.buscarLotacaoAtual(r.getId(), true));
					r.setImagemFrente(dao.buscarImagemFrente(r.getId()));
				
				if (r.getVisitanteId() != null) 
					r.setImagemFrente(visitanteService.buscarImagemFrente(r.getVisitanteId()));
			}
		}

		return recuperandos;
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public Recuperando atualizarRegimeCondicaoAtualRecuperando(Recuperando recuperando){
		List<Processo> processos = processoDao.processosPorRecuperando(recuperando.getId());
		
		if (!processos.isEmpty()) {
			Regime regime = this.regimeAtualRecuperando(processos);
			recuperando.setRegimeAtual(regime);
			Condicao condicao = this.condicaoAtualRecuperando(processos);
			recuperando.setCondicaoAtual(condicao);
			dao.atualizarRegimeCondicaoAtualRecuperando(recuperando, regime, condicao);
			
			recuperando.setProcessos(processos);
		}
		
		return recuperando;
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public Recuperando salvar(Recuperando recuperando) throws Exception {
		if (recuperando.getPessoa() == null) {
			throw new ValidacaoException("É necessário associar uma pessoa.");
		}
		
		if (!isMaioridade(recuperando.getPessoa().getDataNascimento())) {
			throw new ValidacaoException("Recuperando não possui maioridade!");
		}
		
		Long idRecup = recuperandoJaExisteNoSistema(recuperando);
        
        if (idRecup != null) {
            throw new ValidacaoException("O Recuperando " + recuperando.getPessoa().getNome() + ", matrícula " + idRecup + " já existe no sistema.");
        }
		
		if (recuperando.getId() > 0 && recuperando.getFotoFrenteAtual().getId() == 0) {
			recuperando.setFotos(salvarFoto(recuperando.getFotoFrenteAtual()));
		}

		Biometria b = recuperando.getPessoa().getBiometria();
		if(recuperando.isTornarRecuperando() && recuperando.getPessoa() != null && recuperando.getPessoa().getId() > 0) {
			Long idPessoa = recuperando.getPessoa().getId();
			Pessoa p = pessoaDao.buscarPessoaPorId(idPessoa);
			p.setRecuperando(recuperando);
			recuperando.setPessoa(p);
		}
		
		Long idVisitante = recuperando.getTornarRecuperandoIdVisitante(); 
		if(recuperando.getId() > 0) {
			List<Parente> parentes = this.parenteDAO.listarPorRecuperando(recuperando);
			if (parentes.size() > 0){
				parentes.stream().forEach(p -> p.setStatus(SgpStatusAlternativo.P));
			}
		}
		
		dao.salvar(recuperando);

		if(recuperando.isTornarRecuperando() && recuperando.getPessoa() != null && recuperando.getPessoa().getId() > 0) {
			Long idPessoa = recuperando.getPessoa().getId();
			Pessoa p = pessoaDao.buscarPessoaPorId(idPessoa);
			
			if (b != null) {
				p.setBiometria(b);
			}
			
			pessoaDao.salvar(p);
		}
		
		if(idVisitante != null) {
			desativarCredenciamentos(idVisitante);
		}
		
		String novaPassagem = (String) facesUtil.getObjetoSessao("NOVA_PASSAGEM");
		if(novaPassagem != null)
			reativarSolicitacaoVisita(recuperando);
		
		reativarVisitante(recuperando);
		recuperando = dao.buscarRecuperandoPorId(recuperando.getId());
		
		//GAMB
		if (b != null) {
			recuperando.getPessoa().getBiometria().setBiometriaFoiAlterada(b.getBiometriaFoiAlterada());
		}

		return recuperando;
	}

	private void reativarVisitante(Recuperando recuperando) {
		Optional<List<Credenciamento>> credenciamentos = this.credenciamentoDAO.listaVisitantes(recuperando);
		if (credenciamentos.get().size() > 0) {
			credenciamentos.get().stream().forEach(vr -> {
				Visitante visitante = this.visitanteDAO.buscarPorId(vr.getVisitante().getId());
				if(visitante.getStatus() == StatusVisitante.I){
					visitante.setStatus(StatusVisitante.P);
					this.visitanteDAO.salvar(visitante);
				}
			});
		}
	}

	private void ativaStatusParenteNaSolicitacaoDeVisita(Parente parente) {
		Optional<SolicitacaoVisita> solicitacaoVisita = this.solicitacaoVisitaDAO.buscarPorParente(parente);
		if (solicitacaoVisita.isPresent()) {
			SolicitacaoVisita solicitacaoVisitaRecuperada = solicitacaoVisita.get();
			solicitacaoVisitaRecuperada.setStatus(parente.getStatus());
		}

	}

	public Long recuperandoJaExisteNoSistema(Pessoa p, Long idRecuperando) {
        return dao.recuperandoJaExisteNoSistema(p.getNome(), p.getNomeMae(), p.getDataNascimento(), idRecuperando);
    }
    
    public Long recuperandoJaExisteNoSistema(Recuperando r) {
        return dao.recuperandoJaExisteNoSistema(r.getPessoa().getNome(), r.getPessoa().getNomeMae(), r.getPessoa().getDataNascimento(), r.getId());
    }

	public void excluir(RecuperandoView recuperando) {
		dao.excluir(recuperando.getId());
	}

	public Recuperando buscarRecuperandoPorId(long id, boolean buscarAuditoria) {
		Recuperando recuperando = dao.buscarRecuperandoPorId(id);
		
		if (recuperando != null && buscarAuditoria) {
			recuperando.setAutorRegistro(auditDao.autorRegistro(recuperando, recuperando.getId()));
			recuperando.setAutorUltimaAlteracao(auditDao.autorUltimaAlteracao(recuperando, recuperando.getId()));
		}
		
		return recuperando;
	}
	
	public Recuperando buscarRecuperandoSimplesPorId(long id) {
		return dao.buscarRecuperandoSimplesPorId(id);
	}

    public Recuperando buscarRecuperandoDocumentosPorId(long id) {
        return dao.buscarRecuperandoDocumentosPorId(id);
    }
	
	public Recuperando buscarRecuperandoSimplesPorIdPessoa(long idPessoa) throws ValidacaoException {
		return dao.buscarRecuperandoSimplesPorIdPessoa(idPessoa);
	}

    public Date buscarUltimaPassagemRecuperandoPessoa(long idPessoa) {
        return dao.buscarUltimaPassagemRecuperandoPessoa(idPessoa);
    }
	
	@Interceptors(EJBExcInterceptor.class)
	public void salvarOutroNome(OutroNome outroNome){		
		dao.salvarOutroNome(outroNome);
	}
	
	public void excluirOutroNome(OutroNome outroNome){
		dao.excluirOutroNome(outroNome);
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void tornarNomePrincipal(Recuperando recuperando, OutroNome outroNome){
		String nome = recuperando.getPessoa().getNome();
		String nomeMae = recuperando.getPessoa().getNomeMae();
		String nomePai = recuperando.getPessoa().getNomePai();
		Date dataNascimento = recuperando.getPessoa().getDataNascimento();
		
		recuperando.getPessoa().setNome(outroNome.getNome());
		recuperando.getPessoa().setNomeMae(outroNome.getNomeMae());
		recuperando.getPessoa().setNomePai(outroNome.getNomePai());
		recuperando.getPessoa().setDataNascimento(outroNome.getDataNascimento());
		
		outroNome.setNome(nome);
		outroNome.setNomeMae(nomeMae);
		outroNome.setNomePai(nomePai);
		outroNome.setDataNascimento(dataNascimento);
		
		pessoaDao.salvar( recuperando.getPessoa());
		dao.salvarOutroNome(outroNome);
		
	}

	@Transactional
	@Interceptors(EJBExcInterceptor.class)
	public void salvarParente(Parente parente){
		Calendar data = Calendar.getInstance();
		data.add(Calendar.MONTH, 4);
		data.set(Calendar.HOUR_OF_DAY, 00);
		data.set(Calendar.SECOND, 00);
		data.set(Calendar.MINUTE, 00);
		data.set(Calendar.MILLISECOND, 00);
		
		dao.atualizarAlteracaoParentes(data.getTime(), parente.getRecuperando().getId());
		if(parente.getRecuperando().getDataCadastroParente() == null)
			dao.atualizarDataCadastroParente(new Date(), parente.getRecuperando().getId());
		dao.salvarParente(parente);
		ativaStatusParenteNaSolicitacaoDeVisita(parente);
	}
	
	public void excluirParente(Parente parente) throws ValidacaoException {
		dao.excluirParente(parente);
	}

	public int quantidadeParentesAtivos(Recuperando recuperando){
		int total =0;
		for(Parente parente : recuperando.getParentes()){
			if(parente.getStatus() == SgpStatusAlternativo.A){
				total++;
			}
		}
		return total;
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void salvarAlcunha(Alcunha alcunha){
		if(alcunha.isPrincipal()){
		dao.atualizarAlcunhaPrincipal(false, alcunha.getRecuperando().getId());
		}
		dao.salvarAlcunha(alcunha);
	}
	
	public void excluirAlcunha(Alcunha alcunha){
		dao.excluirAlcunha(alcunha);
	}
	
	public boolean limiteParentes(Recuperando recuperando, Parente p){
		int limite = 0;
		
		for(Parente parente : recuperando.getParentes()){
			if (parente.getId() != p.getId() && parente.getStatus() == SgpStatusAlternativo.A && !parente.getMenorIdade()){
				limite++;
			}
		}
		
		return limite < LIMITE_VISITANTE_MAIOR_IDADE;		
	}
	
	public boolean habilitaAlteracaoParente(Recuperando recuperando) {
		if (recuperando.getAlteracaoParentes() == null) {
			return true;
		}
		
		Calendar dataAtual = Calendar.getInstance();
		Calendar dataAlteracaoRecuperando = Calendar.getInstance();
		Calendar dataAlteracao = Calendar.getInstance();
		Calendar dataAlteracao72Horas = Calendar.getInstance();
		
		dataAtual.set(Calendar.HOUR_OF_DAY, 0);
		dataAtual.set(Calendar.MINUTE, 0);
		dataAtual.set(Calendar.SECOND, 0);
		dataAtual.set(Calendar.MILLISECOND, 0);

		dataAlteracaoRecuperando.setTime(recuperando.getAlteracaoParentes());
		dataAlteracao.setTime(recuperando.getAlteracaoParentes());
		dataAlteracao.add(Calendar.MONTH, -4);
		
		dataAlteracao72Horas.setTime(dataAlteracao.getTime());
		dataAlteracao72Horas.add(Calendar.HOUR, 72);

		if (dataAlteracaoRecuperando.compareTo(dataAtual) == 0 || dataAlteracaoRecuperando.compareTo(dataAtual) < 0 || 
				dataAlteracao.compareTo(dataAtual) == 0 || dataAtual.compareTo(dataAlteracao72Horas) < 0 || dataAtual.compareTo(dataAlteracao72Horas) == 0) {
			return true;
		}
		
		return false;
	}	
	
	@Interceptors(EJBExcInterceptor.class)
	public List<Foto> salvarFoto(Foto foto) {
		foto = dao.salvarFoto(foto);
		List<Foto> fotos = dao.listarFotos(foto.getRecuperando().getId());
		List<Foto> fotosAlteradas = new ArrayList<Foto>();
		
		if (foto.getIsAtual()) {
			for (Iterator<Foto> i = fotos.iterator(); i.hasNext();) {
				Foto f = i.next();
				
				if (f.getTipo().equals(foto.getTipo()) && f.getId() != foto.getId() && f.getIsAtual()) {
					f.setIsAtual(false);
					fotosAlteradas.add(f);
					i.remove();
				}
			}
			
			for (Foto f : fotosAlteradas) {
				fotos.add(dao.salvarFoto(f));
			}
		}
		
		return fotos;
	}
	
	public Particularidade salvarParticularidade(Particularidade p) {
		return dao.salvarParticularidade(p);
	}
	
	public void substituirFoto(Foto foto, Foto fotoAntiga) throws ValidacaoException {
		dao.substituirFoto(foto,fotoAntiga);
	}
	
	public void excluirFoto(Foto f) throws ValidacaoException {
		if(dao.fotoVinculada(f)){
			throw new ValidacaoException("Foto vinculada a uma movimentação ou a uma passagem. Exclusão não permitida!");
		}	
		dao.excluirFoto(f);
	}
	
	public void excluirParticularidade(Particularidade p) {
		dao.excluirParticularidade(p);
	}
	
	public Estrutura PedidoTransitolotacaoAtual(Recuperando recuperando){		
		return dao.PedidoTransitolotacaoAtual(recuperando);
	}
	
	public Estrutura lotacaoAtual(Recuperando recuperando){		
		return dao.lotacaoAtual(recuperando);
	}
	
	public StatusRecuperando statusAtualRecuperando(long id){
		return dao.statusAtualRecuperando(id);
	}
	
	public StatusRecuperando buscarStatusRecuperandoPorIdVisitante(long idVisitante) {
		return dao.buscarStatusRecuperandoPorIdVisitante(idVisitante);
	}
	
	public Regime regimeAtualRecuperando(List<Processo> processos) {
		Regime reg = null;
		for (Processo processo : processos) {
			if (processo.getStatusProcesso() != StatusProcesso.FINALIZADO) {

				if (processo.getRegimeAtual() != null) {
					if (processo.getRegimeAtual().getDescricao().equals("Fechado")) {
						return processo.getRegimeAtual();

					} else if (reg != null) {
						if (!reg.equals(processo.getRegimeAtual()) && processo.getRegimeAtual().getDescricao().equals("Semiaberto")) {
							reg = processo.getRegimeAtual();

						} else if (processo.getRegimeAtual().getDescricao().equals("Aberto")) {
							reg = processo.getRegimeAtual();

						} else if (processo.getRegimeAtual().getDescricao().equals("Réu Solto")) {
							reg = processo.getRegimeAtual();
						}
					}else {
						reg = processo.getRegimeAtual();
					}
				}
			}
		}
		return reg;
	}
	
	public Condicao condicaoAtualRecuperando(List<Processo> processos){
		Condicao cond = null;
		for (Processo processo : processos) {
			if (processo.getStatusProcesso() != StatusProcesso.FINALIZADO) {

				if (processo.getCondicaoAtual() != null) {
					if (processo.getCondicaoAtual() == Condicao.CONDENADO) {
						return Condicao.CONDENADO;

					} else if (cond != null) {
						if (cond == processo.getCondicaoAtual() && processo.getCondicaoAtual() == Condicao.PROVISORIO) {
							cond = Condicao.PROVISORIO;

						} else if (processo.getCondicaoAtual() == Condicao.MEDIDA_SEGURANCA) {
							cond = Condicao.MEDIDA_SEGURANCA;

						} 
					}else {
						cond = processo.getCondicaoAtual();
					}
				}
			}
		}
		return cond;
	}	
	
	public boolean isMaioridade(Date nascimento){
		Calendar maioridade = Calendar.getInstance();
		maioridade.add(Calendar.YEAR, -18);		
		return nascimento.before(maioridade.getTime());
	}

	public void buscarAlcunhasEOutrosNomes(RecuperandoDTO rec) {
		rec.setAlcunhas(dao.buscarAlcunhas(rec.getId()));
		rec.setOutrosNomes(dao.buscarOutrosNomes(rec.getId()));
	}

	public boolean recuperandoPossuiBiometriaAtualizada(long id) {
		return dao.recuperandoPossuiBiometriaAtualizada(id);
	}

	public List<ExtratoRecuperandoDTO> gerarEntidadeExtratoRecuperando(Recuperando recuperando, Long passagemId) {
		ExtratoRecuperandoDTO extrato = new ExtratoRecuperandoDTO(recuperando);
		extrato.setHistoricos(historicoService.listarHistoricoRecuperando(recuperando.getId()));
		
		if (passagemId != null) {
			Passagem passagem = recuperando.getPassagens().stream().filter(y->y.getId() == passagemId).findFirst().get();
			gerarMovimentacoesExtrato(passagem, extrato);
		} else {
			for (Passagem passagem : recuperando.getPassagens().stream().sorted(Comparator.comparing(Passagem::getId)).collect(Collectors.toList())) {
				gerarMovimentacoesExtrato(passagem, extrato);
			}
		}
		
		return Arrays.asList(extrato);
	}
	
	private void gerarMovimentacoesExtrato(Passagem passagem, ExtratoRecuperandoDTO extrato) {
		Passagem p = passagemService.buscaPassagemPorId(passagem);
		PassagemExtratoDTO passagemDto = extrato.new PassagemExtratoDTO(p);
		List<PedidoTransitoDTO> pedidos = buscarMovimentacoes(p);
		
		passagemDto.criarMovimentacoes(pedidos, pedidoService);
		
		extrato.getPassagens().add(passagemDto);
		
		if (p.getDataSaida() == null) {
			Optional<PassagemLotacao> optLotacaoAtual = p.getLotacao().stream().filter(y->y.getDataSaida() == null).findFirst();
			
			if (optLotacaoAtual.isPresent()) {
				extrato.setLotacao(optLotacaoAtual.get().getEstrutura().getNomeCompleto());
			}
		}
		
		Optional<PassagemLotacao> optPrimeiraLotacao = p.getLotacao().stream().min(Comparator.comparing(PassagemLotacao::getId));
		Optional<PassagemLotacao> optUltimaLotacao = p.getLotacao().stream().max(Comparator.comparing(PassagemLotacao::getId));
		
		if (optPrimeiraLotacao.isPresent()) {
			passagemDto.setEstruturaEntrada(optPrimeiraLotacao.get().getEstrutura().getNomeCompleto());
		}

		if (optUltimaLotacao.isPresent()) {
			passagemDto.setEstruturaSaida(optUltimaLotacao.get().getEstrutura().getNomeCompleto());
		}
	}
	
	private List<PedidoTransitoDTO> buscarMovimentacoes(Passagem passagem) {
		FiltroPedTransito filtro = new FiltroPedTransito();
		
		filtro.setDataInicio(passagem.getDataEntrada());
		filtro.setDataFim(passagem.getDataSaida());
		filtro.setRecuperandoId(passagem.getRecuperando().getId());
		filtro.setListaTipoTransito(Arrays.asList(TipoTransito.INTERNA, TipoTransito.EXTERNA, TipoTransito.TRANSFERENCIA, TipoTransito.REMANEJAMENTO));
		filtro.setStatus(StatusPedido.FINALIZADO);
		filtro.setIsExtratoRecuperando(true);
		
		return pedidoService.buscaPorFiltro(filtro, false);
	}

	public Recuperando buscarRecuperandoComPassagens(Long recuperandoId) {
		return dao.buscarRecuperandoComPassagens(recuperandoId);
	}

	public Boolean verificaRecuperandoPossuiPassagemNaUnidadeLogada(Long recuperandoId) {
		return dao.verificaRecuperandoPossuiPassagemNaUnidadeLogada(recuperandoId, SecurityUtil.getUnidadeSelecionada().getId());
	}

    public Boolean selecionarParentescoRecuperado(Recuperando recuperando, TipoVisita tipoVisita) {
	    if (tipoVisita.getTipoEntrada() != null && tipoVisita.getTipoEntrada() == TipoEntrada.FAMILIA) {
            recuperando.setParentes(this.parenteDAO.listarPorRecuperando(recuperando));
            return true;
        }

	    return false;
    }
    
    public Recuperando buscarPessoaRecuperando(Pessoa pessoa){
    	return dao.buscarPessoaRecuperando(pessoa);
    }

    public List<Parente> listarPorRecuperando(Recuperando recuperando){
    	return this.parenteDAO.listarPorRecuperando(recuperando);
    }
    
	private void desativarCredenciamentos(Long idVisitante) {
		Visitante visitante = this.visitanteService.buscarPorId(idVisitante, true);
		
		for (Credenciamento vr : visitante.getCredenciamentos()) {
			this.faseDoCredenciamentoService.inativarUltimoCredenciamentoVR(vr);
		}
		this.visitanteService.inativarVisitante(visitante);
	}

	public Recuperando salvarRecuperandoUnificacao(Recuperando recuperando) {
		dao.salvar(recuperando);
		recuperando = dao.buscarRecuperandoPorId(recuperando.getId());
		return recuperando;
	}
	
	public void excluirRecuperandoUnificacao(Recuperando recuperando) {
		dao.excluirUnificacao(recuperando);
	}

	public void unificarRecuperando(Recuperando recuperando1, Recuperando recuperando2, DadosRecuperandoUnificacaoDTO dados) throws Exception {
		unificarDao.unificarRecuperando(recuperando1, recuperando2, dados);
	}
	
	public int buscarQuantidadeFiltrada(FiltroUnificarRecuperando filtro) {
		return dao.buscarQuantidadeFiltrada(filtro);
	}
	
	public List<RecuperandoDTO> buscarRecuperandosPorFiltro(FiltroUnificarRecuperando filtro, boolean isBuscaRelatorio) {
		List<RecuperandoDTO> recuperandos = dao.buscarRecuperandosPorFiltro(filtro, isBuscaRelatorio);
		
		return recuperandos;
	}
	
	public void resetarDataCadastroParente(Recuperando recuperando) {
		dao.resetarDataCadastroParente(recuperando.getId());
	}
	
    private void reativarSolicitacaoVisita(Recuperando recuperando) {
        List<Parente> parentes = this.parenteDAO.listarPorRecuperando(recuperando);
        for(Parente p : parentes) {
    		Optional<SolicitacaoVisita> solicitacaoVisita = this.solicitacaoVisitaDAO.buscarPorParente(p);
    		if (solicitacaoVisita.isPresent()) {
				SolicitacaoVisita solicitacaoVisitaRecuperada = solicitacaoVisita.get();
				solicitacaoVisitaRecuperada.setStatus(SgpStatusAlternativo.P);
				this.solicitacaoVisitaDAO.salvar(solicitacaoVisitaRecuperada);
    		}
        }
    }

	public List<Etnia> listarEtnias() {
		return dao.listarEtnias();
	}

	public void expirarGestante() {
		Calendar dataExpirar = Calendar.getInstance();
		dataExpirar.add(Calendar.MONTH, -9);
		dao.expirarGestante(dataExpirar.getTime());
	}

	public void expirarLactante() {
		Calendar dataExpirar = Calendar.getInstance();
		dataExpirar.add(Calendar.MONTH, -6);
		dao.expirarLactante(dataExpirar.getTime());
	}
	
	public RecuperandoView buscarRecuperandoSelecionado(Long id) {
		RecuperandoView r = dao.buscarRecuperandoSelecionado(id);

		if (r.getId() != null) {
			r.setAlcunhas(dao.buscarAlcunhas(r.getId()));
			r.setOutrosNomes(dao.buscarOutrosNomes(r.getId()));
			r.setLotacaoAtual(dao.buscarLotacaoAtual(r.getId(), false));
			
			Imagem imgRec = dao.buscarImagemFrente(r.getId());
			
			if (imgRec != null)
				r.setImagemFrente(imgRec);
		}
		
		if (r.getVisitanteId() != null) {
			Imagem imgVis = visitanteService.buscarImagemFrente(r.getVisitanteId());
			
			if (imgVis != null)
				r.setVisitanteImagemFrente(imgVis);
		}
		
		return r;
	}

	public Recuperando alterar(Recuperando recuperando) {
		return this.dao.alterar(recuperando);
	}

	public String buscarLotacaoAtual(long idRecuperando, boolean isTrazerUnidade) {
		return this.dao.buscarLotacaoAtual(idRecuperando, isTrazerUnidade);
	}
}


