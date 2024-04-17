package br.gov.mt.sejudh.sigepen.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.EntradaSaidaDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaSaida;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoVisita;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.EntradaSaida;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.ParametroVisita;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.model.dto.EntradaSaidaDTO;

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
		
		if (!e.getSituacao().equals(SituacaoVisita.EM_ANDAMENTO)) 
			throw new ValidacaoException("Esta visita não está mais em andamento.");
		
		e.setDataSaida(entrada.getDataSaida());
		e.setSituacao(entrada.getSituacao());
		
		salvar(e);
		
		return entrada;
	}

	public EntradaSaida salvar(EntradaSaida entrada) {
		return this.dao.salvar(entrada);
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
}


