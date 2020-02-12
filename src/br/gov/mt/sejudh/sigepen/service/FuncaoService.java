package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.FuncaoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroFuncao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Cargo;
import br.gov.mt.sejudh.sigepen.model.Funcao;


@Stateless
public class FuncaoService {
	
	@Inject
	private FuncaoDAO dao;
	
	@Inject
	private AuditoriaDAO auditDao;

	public void salvar(Funcao funcao) throws ValidacaoException {
		if (jaExisteFuncaoComMesmoNome(funcao)) {
			throw new ValidacaoException("Já existe uma função com o mesmo nome (" + funcao.getNome() + ") no cargo selecionado.");
		}
		
		dao.salvar(funcao);
	}

	private boolean jaExisteFuncaoComMesmoNome(Funcao funcao) {
		return dao.jaExisteFuncaoComMesmoNomeEmCargo(funcao);
	}

	public void excluir(Funcao funcao) throws ValidacaoException {
		if (isFuncaoAssociadoAUmServidor(funcao)) {
			throw new ValidacaoException("A Função " + funcao.getNome() + " já está associada a um servidor.");
		}
		
		dao.excluir(funcao);
	}	
		
	private boolean isFuncaoAssociadoAUmServidor(Funcao funcao) {
		return dao.isFuncaoAssociadoAUmServidor(funcao);
	}

	public List<Funcao> buscaPorFiltro(FiltroFuncao filtro) {		
		return dao.buscaPorFiltro(filtro);
    }
	
	public int quantidadeFiltrados(FiltroFuncao filtro){
		return dao.quantidadeFiltrados(filtro);
	}
	
	public List<Funcao> listarFuncaoAtiva() {		 
		return dao.listarFuncaoAtiva();
    }

	public List<Funcao> buscaPorFiltro(FiltroFuncao filtro, boolean b) {
		return dao.buscaPorFiltro(filtro, b);
	}

	public Funcao buscarPorId(long id) {
		Funcao funcao = dao.buscarPorId(id);
		
		if (funcao != null) {
			funcao.setAutorRegistro(auditDao.autorRegistro(funcao, funcao.getId()));
			funcao.setAutorUltimaAlteracao(auditDao.autorUltimaAlteracao(funcao, funcao.getId()));			
		}
		
		return funcao;
	}

	public List<Funcao> listarFuncoesPorCargo(Cargo cargo) {
		return dao.listarFuncoesPorCargo(cargo);
	}
}
