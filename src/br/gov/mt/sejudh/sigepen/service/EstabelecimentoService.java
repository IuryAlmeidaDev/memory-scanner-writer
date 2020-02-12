package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.EstabelecimentoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEstabelecimento;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Estabelecimento;


@Stateless
public class EstabelecimentoService {
	
	@Inject
	private EstabelecimentoDAO dao;
	
	@Inject
	private AuditoriaDAO auditDAO;

	public void salvar(Estabelecimento estabelecimento) throws ValidacaoException {
		if (jaExisteProcedenciaComMesmoNome(estabelecimento)) {
			throw new ValidacaoException("Já existe uma procedência com o mesmo nome (" + estabelecimento.getNome() + ").");
		}
		
		dao.salvar(estabelecimento);
	}

	private boolean jaExisteProcedenciaComMesmoNome(Estabelecimento estabelecimento) {
		return dao.jaExisteEstabelecimentoComMesmoNome(estabelecimento);
	}

	public void excluir(Estabelecimento estabelecimento) throws ValidacaoException {
		if(dao.isProcedenciaAssociadaAUmaPassagemProcedencia(estabelecimento)){
			throw new ValidacaoException("A Procedência \"" + estabelecimento.getNome() + "\" não pode ser excluída pois já está associada a uma passagem."); 
		}
		dao.excluir(estabelecimento);
	}	
		
	public List<Estabelecimento> buscaPorFiltro(FiltroEstabelecimento filtro, boolean isBuscaRelatorio) {		 
		return dao.buscaPorFiltro(filtro, isBuscaRelatorio);
    }
	
	public int quantidadeFiltrados(FiltroEstabelecimento filtro){
		return dao.quantidadeFiltrados(filtro);
	}

	public Estabelecimento buscarPorId(long id) {
		Estabelecimento estabelecimento = dao.buscarPorId(id);
		
		if (estabelecimento != null) {
			estabelecimento.setAutorRegistro(auditDAO.autorRegistro(estabelecimento, estabelecimento.getId()));
			estabelecimento.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(estabelecimento, estabelecimento.getId()));
		}
		
		return estabelecimento;
	}
}
