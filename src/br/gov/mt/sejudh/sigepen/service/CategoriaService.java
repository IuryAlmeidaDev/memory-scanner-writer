package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.CategoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroCategoria;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Categoria;


@Stateless
public class CategoriaService {
	
	@Inject
	private CategoriaDAO dao;

	@Inject
	private AuditoriaDAO auditDao;
	
	public void salvar(Categoria categoria) throws ValidacaoException {
		if (jaExisteCategoriaComMesmoNome(categoria)) {
			throw new ValidacaoException("Já existe uma categoria com o mesmo nome (" + categoria.getNome() + ").");
		}
		dao.salvar(categoria);
	}
	
	private boolean jaExisteCategoriaComMesmoNome(Categoria categoria) {
		return dao.jaExisteCategoriaComMesmoNome(categoria);
	}

	public void excluir(Categoria categoria) throws ValidacaoException {
		if (isCategoriaAssociadaAUmCargo(categoria)) {
			throw new ValidacaoException("A Categoria " + categoria.getNome() + " já está associada a um cargo.");
		}
		
		dao.excluir(categoria);
	}	
		
	private boolean isCategoriaAssociadaAUmCargo(Categoria categoria) {
		return dao.isCategoriaAssociadaAUmCargo(categoria);
	}

	public List<Categoria> buscaPorFiltro(FiltroCategoria filtro, boolean isRelatorio) {		 
		return dao.buscaPorFiltro(filtro, isRelatorio);
    }
	
	public List<Categoria> listarCategoriaAtiva() {		 
		return dao.listarCategoriaAtiva();
    }

	public int quantidadeFiltrados(FiltroCategoria filtro){
		return dao.quantidadeFiltrados(filtro);
	}

	public Categoria buscarPorId(long id) {
		Categoria categoria = dao.buscarPorId(id);
		
		if (categoria != null) {
			categoria.setAutorRegistro(auditDao.autorRegistro(categoria, categoria.getId()));
			categoria.setAutorUltimaAlteracao(auditDao.autorUltimaAlteracao(categoria, categoria.getId()));
		}
		
		return categoria;
	}
}
