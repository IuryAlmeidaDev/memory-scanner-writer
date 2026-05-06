package br.gov.mt.sejudh.sigepen.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.EntradaSaidaDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaSaida;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoVisita;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.*;
import br.gov.mt.sejudh.sigepen.model.dto.EntradaSaidaDTO;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Stateless
public class EntradaSaidaService {

	@Inject
	private EntradaSaidaDAO dao;

	@Inject
	private AuditoriaDAO auditDao;

	public void buscaTodasAsCelasDaEstruturaId(Long id) {
		this.dao.buscaTodasAsCelasDaEstruturaId(id);
	}

	public int buscarQuantidadeFiltrada(FiltroEntradaSaida filtro) {
		return this.dao.buscarQuantidadeFiltrada(filtro);
	}

	public List<EntradaSaida> buscarEntradasPorFiltro(FiltroEntradaSaida filtro, boolean isRelatorio) {
		return this.dao.buscarEntradasPorFiltro(filtro, isRelatorio);
	}

	public List<Estrutura> buscarEstruturaDisponivel(long idUnidade, Date hora) throws ValidacaoException {
		return this.dao.buscarEstruturaDisponivel(idUnidade, hora);
	}

	public List<ParametroVisita> buscarParametrosVisita(long idUnidade, Date hora) throws ValidacaoException {
		return this.dao.buscarParametrosVisita(idUnidade, hora);
	}

	public Visitante buscarVisitantePorPessoa(FiltroEntradaSaida filtro) {
		return this.dao.buscarVisitantePorPessoa(filtro);
	}

	public EntradaSaidaDTO salvar(EntradaSaidaDTO entrada) throws Exception {
		EntradaSaida e = dao.buscarPorId(entrada.getId());
		
		validarEntrada(entrada, e);
		
			if (!e.getSituacao().equals(SituacaoVisita.EM_ANDAMENTO))
				throw new ValidacaoException("Esta visita não está mais em andamento.");

		e.setDataSaida(entrada.getDataSaida());
		e.setSituacao(entrada.getSituacao());

		salvar(e);

		return entrada;
	}
	
	public void validarEntrada(EntradaSaidaDTO entrada, EntradaSaida e) throws ValidacaoException {
		if(e == null) {
			throw new ValidacaoException("Registro de Entrada/Saida não pode ser vazio");
		}
		
		if(e.getSituacao() == null) {
			throw new ValidacaoException("Está visita está com situação vazia.");
		}
	}

	public EntradaSaida salvar(EntradaSaida entrada) {
		return this.dao.salvar(entrada);
	}

	public EntradaSaida iniciarVisita(EntradaSaida entrada) throws ValidacaoException {
		validarLimiteVisitantesAdultosPorRecuperando(entrada, null);
		return salvar(entrada);
	}

	public EntradaSaida alterarVisita(EntradaSaida entrada) throws ValidacaoException {
		validarLimiteVisitantesAdultosPorRecuperando(entrada, entrada != null ? entrada.getId() : null);
		return salvar(entrada);
	}

	private void validarLimiteVisitantesAdultosPorRecuperando(EntradaSaida entrada, Long entradaIgnoradaId) throws ValidacaoException {
		if (entrada == null || entrada.getVisitante() == null || entrada.getUnidadePrisional() == null) {
			return;
		}

		if (!isVisitanteAdulto(entrada.getVisitante(), new Date())) {
			return;
		}

		Long unidadeId = entrada.getUnidadePrisional().getId();
		if (unidadeId == null) {
			return;
		}

		UnidadePrisional unidade = dao.buscarUnidadePrisionalParaControleVisita(unidadeId);
		if (unidade == null) {
			return;
		}

		Integer limite = unidade.getQuantidadeVisitaPorRecuperando();
		if (limite == null || limite <= 0) {
			return;
		}

		List<Long> recuperandoIds = extrairRecuperandoIds(entrada);
		if (recuperandoIds.isEmpty()) {
			return;
		}

		for (Long recuperandoId : recuperandoIds) {
			dao.bloquearRecuperandoParaControleVisita(recuperandoId);
		}

		Date hoje = new Date();
		Date inicioDia = Sigepen.getDataConsultaMaiorIgual(hoje);
		Date fimDia = Sigepen.getDataConsultaMenorIgual(hoje);
		Date dataMaioridade = calcularDataMaioridade(hoje);

		for (Long recuperandoId : recuperandoIds) {
			Long total = dao.contarVisitantesAdultosPorRecuperandoNoDia(
					unidadeId, recuperandoId, inicioDia, fimDia, dataMaioridade, entradaIgnoradaId);

			if (total >= limite) {
				throw new ValidacaoException("Limite de visitas para este PPL ja foi excedido para o dia de hoje.");
			}
		}
	}

	private boolean isVisitanteAdulto(Visitante visitante, Date dataReferencia) {
		if (visitante.getPessoa() == null || visitante.getPessoa().getDataNascimento() == null) {
			return false;
		}
		return !visitante.getPessoa().getDataNascimento().after(calcularDataMaioridade(dataReferencia));
	}

	private Date calcularDataMaioridade(Date dataReferencia) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataReferencia);
		calendar.add(Calendar.YEAR, -18);
		return calendar.getTime();
	}

	private List<Long> extrairRecuperandoIds(EntradaSaida entrada) {
		if (entrada.getRecuperandos() == null) {
			return Collections.emptyList();
		}

		TreeSet<Long> recuperandoIds = new TreeSet<Long>();
		for (EntradaSaidaRecuperando entradaRecuperando : entrada.getRecuperandos()) {
			if (entradaRecuperando != null
					&& entradaRecuperando.getCredenciamento() != null
					&& entradaRecuperando.getCredenciamento().getRecuperando() != null) {
				Long recuperandoId = entradaRecuperando.getCredenciamento().getRecuperando().getId();
				if (recuperandoId != null && recuperandoId > 0) {
					recuperandoIds.add(recuperandoId);
				}
			}
		}
		return new ArrayList<Long>(recuperandoIds);
	}

	public Long buscarQuantidadeEmAndamento(FiltroEntradaSaida f) {
		return this.dao.buscarQuantidadeEmAndamento(f);
	}

	public Long buscarQuantidadeFinalizada(FiltroEntradaSaida f) {
		return this.dao.buscarQuantidadeFinalizada(f);
	}

	public Long buscarEntradaResponsavel(FiltroEntradaSaida f) {
		return this.dao.buscarEntradaResponsavel(f);
	}

	public void excluir(EntradaSaida entrada) {
		this.dao.excluir(entrada);
	}

	public EntradaSaida buscarPorId(Long id) {
		return this.dao.buscarPorId(id);
	}

	public Visitante buscarBiometriaVisitante(List<Long> ids) {
		return this.dao.buscarBiometriaVisitante(ids);
	}

	public Visitante buscarPessoaVisitante(List<Long> ids) {
		return this.dao.buscarPessoaVisitante(ids);
	}

	public EntradaSaida buscarAuditoria(EntradaSaida entrada) {
		if (entrada != null) {
			entrada.setAutorRegistro(auditDao.autorRegistro(entrada, entrada.getId()));
			entrada.setAutorUltimaAlteracao(auditDao.autorUltimaAlteracao(entrada, entrada.getId()));
		}
		return entrada;
	}

	public int buscarQuantidadeFiltrada(FiltroEntradaVisitante filtro) {
		return this.dao.buscarQuantidadeFiltrada(filtro);
	}

	public List<Visitante> buscarEntradasPorFiltro(FiltroEntradaVisitante filtro, boolean isRelatorio) {
		return this.dao.buscarEntradasPorFiltro(filtro, isRelatorio);
	}

	public String validarEntrada(FiltroEntradaSaida f, Long unidade) {
		return this.dao.validarEntrada(f, unidade);
	}

	public Visitante responsavelDeuEntrada(Long id) {
		return dao.responsavelDeuEntrada(id);
	}

	public Recuperando buscarRecuperandoPorVisitanteCredenciado(Long idVisitante){
		return dao.buscarRecuperandoPorVisitanteCredenciado(idVisitante);
	}

	public String getNomeRecuperando(Long idVisitante) {
		Recuperando rec = buscarRecuperandoPorVisitanteCredenciado(idVisitante);
		return rec != null && rec.getPessoa() != null ? rec.getPessoa().getNome() : "";
	}
	
	public EntradaSaida buscarUltimaEntradaSaidaAtivaPorVisitante(Long idVisitante) {
		return dao.buscarUltimaEntradaSaidaAtivaPorVisitante(idVisitante);
	}
}
