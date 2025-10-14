package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.*;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroAssociarResponsavel;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroConsulta;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVisitanteImpedimento;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVwVisitanteRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.*;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.*;
import br.gov.mt.sejudh.sigepen.model.dto.AutorizacaoSemBiometriaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.CredenciamentoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.FaseDoCredenciamentoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.VisitanteDTO;
import br.gov.mt.sejudh.sigepen.model.view.AssociarVisitanteResponsavelView;
import br.gov.mt.sejudh.sigepen.model.view.VwVisitante;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import org.apache.commons.lang3.time.DateUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class GerenciarAssinaturaService extends BaseService<GerenciarAssinatura, Long> {

	
	private static final long serialVersionUID = 7230046305068303409L;
	
	@Inject
    private GerenciarAssinaturaDAO gerenciarAssinaturaDAO;

	@Inject
	public void setGerenciarAssinaturaDAO(GerenciarAssinaturaDAO gerenciarAssinaturaDAO) {
		this.gerenciarAssinaturaDAO = gerenciarAssinaturaDAO;
		this.setDao(gerenciarAssinaturaDAO);
	}

	public List<GerenciarAssinatura> listar() {
		return this.gerenciarAssinaturaDAO.listarTodos();
	}

	public List<GerenciarAssinatura> listar(UnidadePrisional unidade) {
		return this.gerenciarAssinaturaDAO.listarTodos(unidade);
	}

	public boolean verificaGerenciarAssinaturaExistente(GerenciarAssinatura gerenciarAssinatura) {
		return gerenciarAssinaturaDAO.buscarGerenciarAssinatura(gerenciarAssinatura) != null;
	}

	public GerenciarAssinatura buscar(UnidadePrisional unidade, Relatorio relatorio) {
		return this.gerenciarAssinaturaDAO.buscar(unidade,relatorio);
	}

}
