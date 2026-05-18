package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.FornecedorDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroFornecedor;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Fornecedor;
import br.gov.mt.sejudh.sigepen.model.dto.FornecedorDTO;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;

@Stateless
public class FornecedorService {

	@Inject
	private FornecedorDAO dao;
	@Inject
	private AuditoriaDAO auditDao;
	
	@Inject
	private ContratoService contratoService;

	@Interceptors(EJBExcInterceptor.class)
	public Fornecedor salvar(Fornecedor fornecedor) throws Exception {
		if (fornecedor.getStatus() == Status.INATIVO) {
			fornecedor.getJustificativa().setFornecedor(fornecedor);
			contratoService.inativarContratosPorFornecedor(fornecedor.getId());
		}
		return dao.salvar(fornecedor);
	}

	public Fornecedor buscaPorId(long id) {
		Fornecedor fornecedor = dao.buscaPorId(id);
		
		if (fornecedor != null) {
			fornecedor.setAutorRegistro(auditDao.autorRegistro(fornecedor, fornecedor.getId()));
			fornecedor.setAutorUltimaAlteracao(auditDao.autorUltimaAlteracao(fornecedor, fornecedor.getId()));
		}	
		
		return fornecedor;
	}

	public int quantidadeFiltrados(FiltroFornecedor filtro) {
		return dao.quantidadeFiltrados(filtro);
	}

	public List<FornecedorDTO> buscarPorFiltro(FiltroFornecedor filtro) {
		return dao.buscaPorFiltro(filtro);
	}

	public List<Fornecedor> listar() {
		return dao.listar();
	}

	public List<Fornecedor> listarAtivo() {
		return dao.listarAtivo();
	}

	public List<String> listarRazoesSociaisParaFiltro(String consulta, int limite) {
		return dao.listarRazoesSociaisParaFiltro(consulta, limite);
	}

	public List<String> listarRazoesSociaisParaFiltro() {
		return dao.listarRazoesSociaisParaFiltro();
	}

	public List<String> listarNomesFantasiaParaFiltro(String consulta, int limite) {
		return dao.listarNomesFantasiaParaFiltro(consulta, limite);
	}

	public List<String> listarNomesFantasiaParaFiltro() {
		return dao.listarNomesFantasiaParaFiltro();
	}

}
