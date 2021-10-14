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

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.PessoaDAO;
import br.gov.mt.sejudh.sigepen.dao.ProcessoDAO;
import br.gov.mt.sejudh.sigepen.dao.RecuperandoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.StatusProcesso;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Alcunha;
import br.gov.mt.sejudh.sigepen.model.Biometria;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.OutroNome;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Particularidade;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Processo;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.dto.ExtratoRecuperandoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.ExtratoRecuperandoDTO.PassagemExtratoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RecuperandoDTO;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;
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
	private BiometriaService biometriaService;
	
	@Inject
	private PassagemService passagemService;
	
	@Inject
	private PedidoService pedidoService;
	
	@Inject
	private HistoricoRecuperandoService historicoService;
		
	public int buscarQuantidadeFiltrada(FiltroRecuperando filtro) {
		return dao.buscarQuantidadeFiltrada(filtro);
	}

	public List<RecuperandoDTO> buscarRecuperandosPorFiltro(FiltroRecuperando filtro, boolean isBuscaRelatorio) {
		List<RecuperandoDTO> recuperandos = dao.buscarRecuperandosPorFiltro(filtro, isBuscaRelatorio);
		
		/* mecanismo de atribuição dinâmica do regime e condição na listagem de recuperando.
		 * 
		for(RecuperandoDTO rec: recuperandos){
			List<Processo> processos = processoDao.processosPorRecuperando(rec.getId());
			if(!processos.isEmpty()){
				Regime regime = this.regimeAtualRecuperando(processos);
				Condicao condicao = this.condicaoAtualRecuperando(processos);
				rec.setRegimeAtual(regime != null ? regime.getDescricao() : "");
				dao.atualizarRegimeAtualRecuperando(rec, regime);
				rec.setCondicaoAtual(condicao != null ? condicao.getDescricao() : "");
				dao.atualizarCondicaoAtualRecuperando(rec, condicao);
			}
		}*/
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
		boolean possuiBio = recuperando.getPessoa().getBiometria() != null;
		boolean isNovaBio = false;
		boolean isBioAlterada = false;
		
		Biometria bio = null;
		
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
		
		if (possuiBio) {
			bio = recuperando.getPessoa().getBiometria();
			isNovaBio =  bio.getId() == 0;
			isBioAlterada = bio.isBiometriaFoiAlterada();
		}
		
		if (recuperando.getId() > 0 && recuperando.getFotoFrenteAtual().getId() == 0) {
			recuperando.setFotos(salvarFoto(recuperando.getFotoFrenteAtual()));
		}
		
		dao.salvar(recuperando);
		recuperando = dao.buscarRecuperandoPorId(recuperando.getId());

		if (possuiBio) {
			if (isBioAlterada) {
				biometriaService.alterarBiometriaSDK(bio);
			}
		
			if (isNovaBio) {
				bio.setIdPessoa(recuperando.getPessoa().getId());
				biometriaService.salvarBiometriaSDK(bio);
			}
		}
		
		return recuperando;
	}
	
    public Long recuperandoJaExisteNoSistema(Pessoa p, Long idRecuperando) {
        return dao.recuperandoJaExisteNoSistema(p.getNome(), p.getNomeMae(), p.getDataNascimento(), idRecuperando);
    }
    
    public Long recuperandoJaExisteNoSistema(Recuperando r) {
        return dao.recuperandoJaExisteNoSistema(r.getPessoa().getNome(), r.getPessoa().getNomeMae(), r.getPessoa().getDataNascimento(), r.getId());
    }

	public void excluir(RecuperandoDTO recuperando) {
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
	
	public Recuperando buscarRecuperandoSimplesPorIdPessoa(long idPessoa) throws ValidacaoException {
		return dao.buscarRecuperandoSimplesPorIdPessoa(idPessoa);
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
		Date dataNascimento = recuperando.getPessoa().getDataNascimento();
		
		recuperando.getPessoa().setNome(outroNome.getNome());
		recuperando.getPessoa().setNomeMae(outroNome.getNomeMae());
		recuperando.getPessoa().setDataNascimento(outroNome.getDataNascimento());
		
		outroNome.setNome(nome);
		outroNome.setNomeMae(nomeMae);
		outroNome.setDataNascimento(dataNascimento);
		
		pessoaDao.salvar( recuperando.getPessoa());
		dao.salvarOutroNome(outroNome);
		
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void salvarParente(Parente parente){
		Calendar data = Calendar.getInstance();
		data.add(Calendar.MONTH, 4);
		dao.atualizarAlteracaoParentes(data.getTime(), parente.getRecuperando().getId());
		dao.salvarParente(parente);
	}
	
	public void excluirParente(Parente parente){
		dao.excluirParente(parente);
	}
	
	public int quantidadeParentesAtivos(Recuperando recuperando){
		int total =0;
		for(Parente parente : recuperando.getParentes()){
			if(parente.getStatus() == Status.ATIVO){
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
	
	public boolean limiteParentes(Recuperando recuperando){
		int limite = 0;
		
		for(Parente parente : recuperando.getParentes()){
			if (parente.getStatus() == Status.ATIVO){
				limite++;
			}
		}
		
		return limite<4;		
	}
	
	public boolean habilitaAlteracaoParente(Recuperando recuperando){
		Calendar data = Calendar.getInstance();
		data.add(Calendar.MONTH, 4);
		data.set(Calendar.HOUR_OF_DAY, 00);
		data.set(Calendar.SECOND, 00);
		data.set(Calendar.MINUTE, 00);
		data.set(Calendar.MILLISECOND, 00);

		if (recuperando.getAlteracaoParentes() == null){		
			return true;
		}else if(recuperando.getAlteracaoParentes().before(new Date()) || recuperando.getAlteracaoParentes().equals(data.getTime())){
			return true;
		}else{
			return false;
		}
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
	
	public void excluirFoto(Foto f) throws ValidacaoException {
		if(dao.fotoVinculada(f)){
			throw new ValidacaoException("Foto vinculada a uma movimentação ou a uma passagem. Exclusão não permitida!");
		}	
		dao.excluirFoto(f);
	}
	
	public void excluirParticularidade(Particularidade p) {
		dao.excluirParticularidade(p);
	}
	
	public Estrutura lotacaoAtual(Recuperando recuperando){		
		return dao.lotacaoAtual(recuperando);
	}
	
	public StatusRecuperando statusAtualRecuperando(long id){
		return dao.statusAtualRecuperando(id);
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
		
		passagemDto.criarMovimentacoes(pedidos);
		
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
}


