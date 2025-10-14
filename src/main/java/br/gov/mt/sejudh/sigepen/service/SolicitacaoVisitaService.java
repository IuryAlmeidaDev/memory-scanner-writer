package br.gov.mt.sejudh.sigepen.service;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.bean.ComboHelperBean;
import br.gov.mt.sejudh.sigepen.dao.ParenteDAO;
import br.gov.mt.sejudh.sigepen.dao.SolicitacaoVisitaDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroSolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatusAlternativo;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.PessoaSolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.SolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.model.dto.SolicitacaoVisitaDTO;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@Stateless
public class SolicitacaoVisitaService extends BaseService<SolicitacaoVisita, Long> {

	private static final long serialVersionUID = 15596283275599462L;

	private SolicitacaoVisitaDAO solicitacaoVisitaDAO;

	@Inject
	private ParenteDAO parenteDAO;
	@Inject
	private ComboHelperBean comboHelperBean;
	@Inject
	private PessoaService pessoaService;
	@Inject
	private RecuperandoService recuperandoService;

	@Inject
	private SolicitacaoVisitaDAO solicitacaoDAO;
	
	public SolicitacaoVisitaService() {

	}

	@Inject
	public void setSolicitacaoVisitaDAO(SolicitacaoVisitaDAO solicitacaoVisitaDAO) {
		this.solicitacaoVisitaDAO = solicitacaoVisitaDAO;
		this.setDao(solicitacaoVisitaDAO);
	}

	@Override
	public SolicitacaoVisita salvar(SolicitacaoVisita solicitacaoVisita) throws ValidacaoException {
		if (solicitacaoVisita.getRecuperando() == null) {
			throw new ValidacaoException("Selecione um Recuperando.");
		}

		Recuperando recuperando = recuperandoService.buscarRecuperandoPorId(solicitacaoVisita.getRecuperando().getId(), false);
		solicitacaoVisita.setRecuperando(recuperando);

		this.tratarDadosCadastro(solicitacaoVisita);
		this.validarDadosCadastro(solicitacaoVisita);


		if (solicitacaoVisita.getId() != null) {
			solicitacaoVisita.getParente().setNome(solicitacaoVisita.getPessoaSolicitacao().getNome());
			solicitacaoVisita.getParente().setDataNascimento(solicitacaoVisita.getPessoaSolicitacao().getDataNascimento());
			solicitacaoVisita.getParente().setParentesco(solicitacaoVisita.getParentesco());
			solicitacaoVisita.getParente().setStatus(solicitacaoVisita.getStatus());
		}
		
		
		return super.salvar(solicitacaoVisita);
	}

	private void tratarDadosCadastro(SolicitacaoVisita solicitacaoVisita) {
		if (solicitacaoVisita.getPessoaSolicitacao().getNomeMae() == null) {
			solicitacaoVisita.getPessoaSolicitacao().setNomeMae("ND");
		}
		if (solicitacaoVisita.getStatus() == null) {
			solicitacaoVisita.setStatus(SgpStatusAlternativo.P);
		}
		if (solicitacaoVisita.getPossuiDecisaoJudicial() == null) {
			solicitacaoVisita.setPossuiDecisaoJudicial(SimNao.N);
		}
		if (solicitacaoVisita.getUnidadePrisional() == null) {
			solicitacaoVisita.setUnidadePrisional(SecurityUtil.getUnidadeSelecionada());
		}

		this.tratarDadosCpf(solicitacaoVisita);
		this.tratarDadosParente(solicitacaoVisita);
	}

	private void tratarDadosCpf(SolicitacaoVisita solicitacaoVisita) {
		if (solicitacaoVisita.getCpf() == null || "".equals(solicitacaoVisita.getCpf())) {
			return;
		}
	}

	private void tratarDadosParente(SolicitacaoVisita solicitacaoVisita) {
		if (solicitacaoVisita.getParentesco() == null) {
			solicitacaoVisita.setParente(null);
			return;
		}

		Parente parente = verificaNomeParenteRecuperadoComNomeDaSolicitacao(solicitacaoVisita);

		if (solicitacaoVisita.getParente() == null) {
			solicitacaoVisita.setParente(parente);
			solicitacaoVisita.getParente().setStatus(null);
		}

		solicitacaoVisita.getParente().setNome(solicitacaoVisita.getPessoaSolicitacao().getNome());
		solicitacaoVisita.getParente().setRecuperando(solicitacaoVisita.getRecuperando());
		solicitacaoVisita.getParente().setDataNascimento(solicitacaoVisita.getPessoaSolicitacao().getDataNascimento());
		solicitacaoVisita.getParente().setJudicial(solicitacaoVisita.getPossuiDecisaoJudicial() == SimNao.S);
		solicitacaoVisita.getParente().setParentesco(solicitacaoVisita.getParentesco());

		switch (solicitacaoVisita.getStatus()) {
		case A:
			solicitacaoVisita.getParente().setStatus(SgpStatusAlternativo.A);
			break;
		case P:
			solicitacaoVisita.getParente().setStatus(SgpStatusAlternativo.P);
			break;
		case I:
			solicitacaoVisita.getParente().setStatus(SgpStatusAlternativo.I);
			break;
		}

	}

	private Parente verificaNomeParenteRecuperadoComNomeDaSolicitacao(SolicitacaoVisita solicitacaoVisita) {
		Optional<Parente> parenteRecuperado = this.getParenteInativoPorParentescoRecuperado(solicitacaoVisita);
		if (parenteRecuperado.isPresent()) {
			if (parenteRecuperado.get().getNome().equalsIgnoreCase(solicitacaoVisita.getPessoaSolicitacao().getNome())) {
				return parenteRecuperado.get();
			}
		}

		return new Parente();
	}

	private Optional<Parente> getParenteInativoPorParentescoRecuperado(SolicitacaoVisita solicitacaoVisita) {
		return this.parenteDAO.obtemPorParentescoRecuperandoInativo(solicitacaoVisita);
	}

	private void validarDadosCadastro(SolicitacaoVisita solicitacaoVisita) throws ValidacaoException {
		List<Parente> parentes = this.parenteDAO.listarPorRecuperando(solicitacaoVisita.getRecuperando());
		for (Parente parente : parentes) {
			if (solicitacaoVisita.getParente() != null && !solicitacaoVisita.getParente().equals(parente)
					&& parente.getNome().equalsIgnoreCase(solicitacaoVisita.getPessoaSolicitacao().getNome())
					&& parente.getParentesco().equals(solicitacaoVisita.getParentesco())
					&& (parente.getStatus().equals(SgpStatusAlternativo.A))){
				throw new ValidacaoException(
						"Visitante já está no rol de membros do recuperando, realize o cadastro do visitante!");
			}
		}

		List<SolicitacaoVisita> solicitacoes = this.listarPorRecuperando(solicitacaoVisita.getRecuperando());
		for (SolicitacaoVisita solicitacao : solicitacoes) {
			if (solicitacao.getStatus() == SgpStatusAlternativo.I || solicitacao.getId().equals(solicitacaoVisita.getId()))
				continue;
			if (solicitacao.getPessoaSolicitacao() != null && solicitacao.getPessoaSolicitacao().getNome().equalsIgnoreCase(solicitacaoVisita.getPessoaSolicitacao().getNome())
					&& (solicitacao.getParentesco() != null && solicitacaoVisita.getParentesco() != null
							&& solicitacao.getParentesco().equals(solicitacaoVisita.getParentesco()))
					) {
				throw new ValidacaoException("Solicitação de visita já cadastrada para o visitante informado!");
			}
		}
	}

	public List<SolicitacaoVisita> listarPorRecuperando(Recuperando recuperando) {
		return dao.buscaPorCampo("recuperando", recuperando);
	}

	public SolicitacaoVisita buscarUltimaSolicitacaoAtiva(Credenciamento cred) {
		if (cred.getParente() == null || cred.getRecuperando() == null) {
			return null;
		}

		FiltroSolicitacaoVisita filtro = new FiltroSolicitacaoVisita();
		filtro.setStatus(SgpStatusAlternativo.A);
		filtro.setParente(cred.getParente());
		filtro.setRecuperandoO(cred.getRecuperando());

		List<SolicitacaoVisita> list = solicitacaoDAO.buscarUltimaSolicitacaoAtiva(filtro);
		
		SolicitacaoVisita solicitacaoVisita = list.stream().findFirst().orElse(null);
		if (solicitacaoVisita != null && solicitacaoVisita.getPessoaSolicitacao() != null) {
			solicitacaoVisita.setPessoaSolicitacao(this.solicitacaoDAO.buscarPessoaSolicitacaoPorId(solicitacaoVisita.getPessoaSolicitacao().getId()));
		}

		return solicitacaoVisita;
	}
	
	@Override
	public SolicitacaoVisita buscarPorId(Long id, Boolean buscarAuditoria) {
		return super.buscarPorId(id, buscarAuditoria);
	}
	
	public PessoaSolicitacaoVisita buscarPessoaSolicitacaoPorId(long pessoaId) {
		PessoaSolicitacaoVisita pessoa = solicitacaoDAO.buscarPessoaSolicitacaoPorId(pessoaId);
		
		return pessoa;
	}

	public int buscarQuantidadeFiltrada(FiltroSolicitacaoVisita filtro) {
		return solicitacaoDAO.buscarQuantidadeFiltrada(filtro);
	}

	public List<SolicitacaoVisitaDTO> buscarSolicitacoesPorFiltro(FiltroSolicitacaoVisita filtro, boolean isBuscaRelatorio) {
		return solicitacaoDAO.buscarSolicitacoesPorFiltro(filtro, isBuscaRelatorio);
	}

	public void excluir(Long id) throws ValidacaoException {
		solicitacaoDAO.excluir(id);
	}

	public SolicitacaoVisita buscarSolicitacaoVisitaPorId(long id, boolean b) {
		return solicitacaoDAO.buscarSolicitacaoVisitaPorId(id);
	}
}
