package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.CargoDAO;
import br.gov.mt.sejudh.sigepen.dao.CategoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroCargo;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Cargo;
import br.gov.mt.sejudh.sigepen.model.Categoria;


@Stateless
public class CargoService {
	
	@Inject
	private CargoDAO dao;
	
	@Inject
	private CategoriaDAO daoCategoria;
	
	@Inject
	private AuditoriaDAO auditDao;
	
	public void salvar(Cargo cargo) throws ValidacaoException {
		if (cargo.getId() == 0 && jaExisteCargoComMesmoNome(cargo)) {
			throw new ValidacaoException("Já existe um cargo com o mesmo nome (" + cargo.getNome() + ").");
		}
		
		dao.salvar(cargo);
	}

	private boolean jaExisteCargoComMesmoNome(Cargo cargo) {
		return dao.jaExisteCargoComMesmoNome(cargo);
	}

	public void excluir(Cargo cargo) throws ValidacaoException {
		if (isCargoAssociadoAUmServidor(cargo)) {
			throw new ValidacaoException("O Cargo " + cargo.getNome() + " já está associado a um servidor.");
		}
		
		dao.excluir(cargo);
	}
	
	private boolean isCargoAssociadoAUmServidor(Cargo cargo) {
		return dao.isCargoAssociadoAUmServidor(cargo);
	}

	public List<Cargo> buscaPorFiltro(FiltroCargo filtro, boolean isRelatorio) {		 
		return dao.buscaPorFiltro(filtro, isRelatorio);
    }
	
	public List<Cargo> listarCargosPorCategoria(Categoria categoria) {		 
		return dao.listarCargosPorCategoria(categoria);
    }
	
	public int quantidadeFiltrados(FiltroCargo filtro){
		return dao.quantidadeFiltrados(filtro);
	}
	
	public List<Categoria> listarCategoriaAtiva() {		 
		return daoCategoria.listarCategoriaAtiva();
    }
	
	public List<Cargo> listarCargoAtivo() {		 
		return dao.listarCargosAtivo();
    }
	
	public Cargo buscarPorId(long id) {
		Cargo cargo = dao.buscarPorId(id);
		
		if (cargo != null) {
			cargo.setAutorRegistro(auditDao.autorRegistro(cargo, cargo.getId()));
			cargo.setAutorUltimaAlteracao(auditDao.autorUltimaAlteracao(cargo, cargo.getId()));
		}
		
		return cargo;
	}
}
