package br.gov.mt.sejudh.sigepen.service;

import java.sql.SQLException;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.ServidorDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroServidor;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Lotacao;
import br.gov.mt.sejudh.sigepen.model.Servidor;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;


@Stateless
public class ServidorService {
	
	@Inject
	private ServidorDAO dao;
	
	@Inject
	private AuditoriaDAO auditDAO;

	@Interceptors(EJBExcInterceptor.class)
	public void salvar(Servidor servidor) throws ValidacaoException, SQLException{
		if (jaExisteCpf(servidor)) {
			throw new ValidacaoException("Já existe um Servidor com o mesmo CPF (" + servidor.getCpf() + ").");
		}
		
		dao.salvar(servidor);
	}

	public void excluir(ServidorDTO servidor) throws ValidacaoException {
		if (isServidorAssociadoAUmUsuario(servidor.getId())) {
			throw new ValidacaoException("O(a) Servidor(a) " + servidor.getNome() + " está associado a um usuário.");
		}
		
		if (isServidorAssociadoAUmaUnidade(servidor.getId())) {
			throw new ValidacaoException("O(a) Servidor(a) " + servidor.getNome() + " está associado(a) a uma unidade.");
		}
		
		dao.excluir(servidor.getId());
	}	
		
	private boolean isServidorAssociadoAUmaUnidade(long idServidor) {
		return dao.isServidorAssociadoAUmaUnidade(idServidor);
	}

	private boolean isServidorAssociadoAUmUsuario(long idServidor) {
		return dao.isServidorAssociadoAUmUsuario(idServidor);
	}

	public List<ServidorDTO> buscaPorFiltro(FiltroServidor filtro, boolean isBuscaRelatorio) {		 
		return dao.buscaPorFiltro(filtro, isBuscaRelatorio);
    }
	
	public int quantidadeFiltrados(FiltroServidor filtro){
		return dao.quantidadeFiltrados(filtro);
	}

	public boolean jaExisteCpf(Servidor servidor){
		Servidor serv = dao.buscaServidorSimplesPorCpf(servidor.getCpf());
		
		if (serv==null) {
			return false;
		}
		
		return !serv.equals(servidor);
	}

	public boolean validarLotacaoBloqueada(long id) {
		return dao.validarLotacaoBloqueada(id);
	}

	public Servidor buscarServidorCompletoPorId(long id) {
		Servidor s = dao.buscarServidorCompletoPorId(id);
		
		if (s != null) {
			s.setAutorRegistro(auditDAO.autorRegistro(s, s.getId()));
			s.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(s, s.getId()));
		}
		
		return s;
	}
	
	public Servidor buscarServidorSimplesPorId(long id) {
		return dao.buscarServidorSimplesPorId(id);
	}	
	
	public boolean jaExisteServidorComMatricula(Servidor servidor){
		return dao.buscaServidorSimplesPorMatriculaOrgao(servidor);
	}

	public boolean servidorEhResponsavelDaUnidade(Lotacao l) {
		return dao.servidorEhResponsavelDaUnidade(l);
	}
	
}
