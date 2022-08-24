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
import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.*;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaSaida;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaVisitante;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperando;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroSolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnificarRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.*;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.*;
import br.gov.mt.sejudh.sigepen.model.dto.DadosRecuperandoUnificacaoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.ExtratoRecuperandoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.ExtratoRecuperandoDTO.PassagemExtratoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RecuperandoDTO;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@Stateless
public class EntradaSaidaService {

	@Inject
	private EntradaSaidaDAO dao;
	
	@Inject
	private AuditoriaDAO auditDao;

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


