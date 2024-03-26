package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.TipoFaltaDisciplinarDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTipoFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.TipoFaltaDisciplinar;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;


@Stateless
public class TipoFaltaDisciplinarService {

	@Inject
	private TipoFaltaDisciplinarDAO dao;

	@Inject
	private AuditoriaDAO auditDao;
	
	public void salvar(TipoFaltaDisciplinar tipoFaltaDisciplinar) throws ValidacaoException {
		if (tipoFaltaDisciplinar.getId() == 0 && jaExisteTipoFaltaDisciplinarComMesmaDescricao(tipoFaltaDisciplinar)) {
			throw new ValidacaoException("Já existe um Tipo de Falta Disciplinar com a Descrição informada (" + tipoFaltaDisciplinar.getDescricao() + ").");
		}
		
		dao.salvar(tipoFaltaDisciplinar);
	}

	private boolean jaExisteTipoFaltaDisciplinarComMesmaDescricao(TipoFaltaDisciplinar tipoFaltaDisciplinar) {
		return dao.jaExisteTipoFaltaDisciplinarComMesmaDescricao(tipoFaltaDisciplinar);
	}

	public void excluir(TipoFaltaDisciplinar tipoFaltaDisciplinar) throws ValidacaoException {
		if (isTipoFaltaDisciplinarAssociadoAUmaFaltaDisciplinar(tipoFaltaDisciplinar)) {
			throw new ValidacaoException("Tipo falta disciplinar está vinculada a lançamento de falta disciplinar.");
		}
		
		dao.excluir(tipoFaltaDisciplinar);
	}
	
	private boolean isTipoFaltaDisciplinarAssociadoAUmaFaltaDisciplinar(TipoFaltaDisciplinar tipoFaltaDisciplinar) {
		return dao.isTipoFaltaDisciplinarAssociadoAUmaFaltaDisciplinar(tipoFaltaDisciplinar);
	}

	public List<TipoFaltaDisciplinar> buscaPorFiltro(FiltroTipoFaltaDisciplinar filtro, boolean isRelatorio) {
		return dao.buscaPorFiltro(filtro, isRelatorio);
    }

	public int quantidadeFiltrados(FiltroTipoFaltaDisciplinar filtro){
		return dao.quantidadeFiltrados(filtro);
	}

	public List<TipoFaltaDisciplinar> listarTipoFaltaDisciplinarAtivo() {
		return dao.listarTipoFaltaDisciplinarAtivo();
    }
	
	public TipoFaltaDisciplinar buscarPorId(long id) {
		TipoFaltaDisciplinar tipoFaltaDisciplinar = dao.buscarPorId(id);
		
		if (tipoFaltaDisciplinar != null) {
			tipoFaltaDisciplinar.setAutorRegistro(auditDao.autorRegistro(tipoFaltaDisciplinar, tipoFaltaDisciplinar.getId()));
			tipoFaltaDisciplinar.setAutorUltimaAlteracao(auditDao.autorUltimaAlteracao(tipoFaltaDisciplinar, tipoFaltaDisciplinar.getId()));
		}
		
		return tipoFaltaDisciplinar;
	}

}
