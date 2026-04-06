package br.gov.mt.sejudh.sigepen.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.ContratoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroContrato;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.MultiplosResultadosException;
import br.gov.mt.sejudh.sigepen.exception.NaoEncontradoException;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.dto.ContratoDTO;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;
import org.hibernate.Hibernate;

@Stateless
public class ContratoService {

	@Inject
	private ContratoDAO dao;
	
	@Inject
	private AuditoriaDAO auditDao;

	@Interceptors(EJBExcInterceptor.class)
	public Contrato salvar(Contrato contrato) throws Exception {
		if (contrato.getStatus() == Status.INATIVO) {
			contrato.getJustificativa().setContrato(contrato);
		}
		return dao.salvar(contrato);
	}

	public Contrato buscaPorId(long id) {
		Contrato contrato = dao.buscaPorId(id);
		
		if (contrato != null) {
			contrato.setAutorRegistro(auditDao.autorRegistro(contrato, contrato.getId()));
			contrato.setAutorUltimaAlteracao(auditDao.autorUltimaAlteracao(contrato, contrato.getId()));
		}		
		
		return contrato;
	}

	public int quantidadeFiltrados(FiltroContrato filtro) {
		return dao.quantidadeFiltrados(filtro);
	}

	public List<ContratoDTO> buscarPorFiltro(FiltroContrato filtro) {
		return dao.buscaPorFiltro(filtro);
	}

	public List<Contrato> listarAtivos() {
		return dao.listarAtivo();
	}
	
	public List<Contrato> listarTodos(){
		return dao.listarTodos();
	}

	public boolean validarContratoUnidade(Contrato contrato) {
		List<Contrato> contratos = dao.validarContratoUnidade(contrato);
        return contratos.isEmpty();
    }

	public Contrato buscarContratoPorNumero(String numeroContrato) {
		return dao.buscarContratoPorNumero(numeroContrato);
	}
	
	public void inativarContratosPorFornecedor(long idFornecedor){
		dao.inativarContratosPorFornecedor(idFornecedor);
	}
	
	public String buscarHorarioRefeiracao(Long idUnidade,Long tipoRefeiracao) {
		String horarioRefeicao = this.horarioRefeiracaoPorUnidade(idUnidade, tipoRefeiracao);
		if(horarioRefeicao == null || horarioRefeicao.isEmpty()) {
			horarioRefeicao = this.horarioRefeiracaoPorContratoUnidade(idUnidade,tipoRefeiracao);
		}
		return horarioRefeicao;
	}
	
	public List<HorarioRefeicao> buscarHorarioRefeiracao(Long idUnidade) {
		List<HorarioRefeicao> horarioRefeicao = this.horarioRefeiracaoPorUnidade(idUnidade);
		if(horarioRefeicao == null || horarioRefeicao.isEmpty()) {
			horarioRefeicao = this.horarioRefeiracaoPorContratoUnidade(idUnidade);
		}
		return horarioRefeicao;
	}
	
	public String horarioRefeiracaoPorUnidade(Long idUnidade,Long tipoRefeiracao) {
		return dao.horarioRefeiracaoPorUnidade(idUnidade, tipoRefeiracao);
	}
	
	public List<HorarioRefeicao> horarioRefeiracaoPorUnidade(Long idUnidade) {
		return dao.horarioRefeiracaoPorUnidade(idUnidade);
	}
	
	public List<HorarioRefeicao> horarioRefeiracaoPorContratoUnidade(Long idUnidade) {
		return dao.horarioRefeiracaoPorContratoUnidade(idUnidade);
	}
	
	public String horarioRefeiracaoPorContratoUnidade(Long idUnidade,Long tipoRefeiracao) {
		return dao.horarioRefeiracaoPorContratoUnidade(idUnidade, tipoRefeiracao);
	}
	
	public Contrato buscarContratoVigenteEAtivoPorUnidade(long id, Date data) {
		List<Contrato> contratos = dao.buscarContratoVigenteEAtivoPorUnidade(id, data);
		if (contratos.isEmpty()) {
			throw new NaoEncontradoException("Nenhum contrato vigente e ativo encontrado para a unidade na data específicada.");
		} else if (contratos.size() > 1) {
			throw new MultiplosResultadosException("Múltiplos contratos vigentes e ativos encontrados para a unidade na data específicada. Esperado apenas um.");
		} else {
			Contrato contrato = contratos.get(0);
			Hibernate.initialize(contrato.getFornecedor());
			Hibernate.initialize(contrato.getUnidades());
			return contrato;
		}
	}

	public List<Contrato> buscarContratoVigente(long idUnidade, String dataInicio, String dataFim) throws ParseException {
		return dao.buscarContratoVigente(idUnidade, dataInicio, dataFim);
	}

	public int contarFiltrados(FiltroContrato filtro) {
	    return dao.contarFiltrados(filtro);
	}
	
}
