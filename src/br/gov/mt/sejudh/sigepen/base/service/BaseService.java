package br.gov.mt.sejudh.sigepen.base.service;

import java.io.Serializable;
import java.util.List;

import javax.transaction.Transactional;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroConsulta;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;

@Transactional
public class BaseService<T extends BaseModel<I>, I extends Number> implements Serializable {


	private static final long serialVersionUID = -7340952756343693599L;
	
	protected BaseDAO<T, I> dao;
    
	public void setDao(BaseDAO<T, I> dao) {
		this.dao = dao;
	}
	
    public T buscarPorId(I id) {
        return this.dao.buscarPorId(id);
    }

    public T buscarPorId(I id, Boolean buscarAuditoria) {
        return this.dao.buscarPorId(id, buscarAuditoria);
    }

    public T salvar(T t) throws ValidacaoException {
        this.validaDuplicidade(t);
        return this.dao.salvar(t);
    }

    public void excluir(T t) throws ValidacaoException {
        this.validarExclusao(t);
        this.dao.excluir(t);
    }

    public List<T> buscaPorFiltro(FiltroConsulta filtro) {
        return this.dao.buscaPorFiltro(filtro, false);
    }

    public List<T> buscaPorFiltro(FiltroConsulta filtro, boolean isRelatorio) {
        return this.dao.buscaPorFiltro(filtro, isRelatorio);
    }

    public List<T> buscaPorFiltro(String campo, Object valor) {
        return this.dao.buscaPorCampo(campo, valor);
    }

    public int quantidadeFiltrados(FiltroConsulta filtro){
        return this.dao.quantidadeFiltrados(filtro);
    }

    protected void validaDuplicidade(T t) throws ValidacaoException {
    }

    protected void validarExclusao(T t) throws ValidacaoException {
    }
}
