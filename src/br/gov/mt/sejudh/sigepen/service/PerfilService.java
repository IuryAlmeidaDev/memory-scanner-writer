package br.gov.mt.sejudh.sigepen.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.primefaces.model.DualListModel;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.PerfilDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPerfil;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Perfil;
import br.gov.mt.sejudh.sigepen.model.Permissao;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;

@Stateless
public class PerfilService {
	
	@Inject
	private PerfilDAO dao;
	
	@Inject
	private AuditoriaDAO auditDAO;

	@Interceptors(EJBExcInterceptor.class)
	public void salvar(Perfil perfil) throws Exception {
		if (jaExistePerfilComMesmoNome(perfil)) {
			throw new ValidacaoException("Já existe um perfil com o mesmo nome (" + perfil.getNome() + ").");
		}
		
		if (perfilEstaSemPermissao(perfil)) {
			throw new ValidacaoException("É necessário associar pelo menos uma permissão ao perfil.");
		}
		
		dao.salvar(perfil);
	}

	public void excluir(Perfil perfil) throws Exception {
		if (isPerfilAssociadoAUmUsuario(perfil.getId())) {
			throw new ValidacaoException("Não foi possível excluir \"" + perfil.getNome() + "\". O perfil está associado a um ou mais usuários.");
		}
		
		dao.excluir(perfil);
	}

	private boolean perfilEstaSemPermissao(Perfil perfil) {
		return perfil.getPermissoes().size() <= 0;
	}
	
	private boolean isPerfilAssociadoAUmUsuario(long id) {
		return dao.isPerfilAssociadoAUmUsuario(id);
	}
	
	private boolean jaExistePerfilComMesmoNome(Perfil perfil) {
		Perfil perf = dao.jaExistePerfilComMesmoNome(perfil.getNome());
		
		if (perf == null) {
			return false;
		}
		
		return perf.getId() != perfil.getId();
	}

	public DualListModel<Permissao> listarPermissoesModel(Perfil perfil, long idFuncionalidade) {	
		List<Permissao> permissoesConcedidas = new ArrayList<Permissao>();
		List<Permissao> permissoesNegadas = new ArrayList<Permissao>();
		DualListModel<Permissao> lista = new DualListModel<Permissao>(permissoesNegadas, permissoesConcedidas);
		
		if(perfil.getId() > 0) {
			permissoesConcedidas = dao.buscarPermissoesPorFuncionalidadeEPerfil(idFuncionalidade, perfil.getId());
		}
		
		permissoesNegadas = dao.listarPermissoesNegadas(permissoesConcedidas, idFuncionalidade);
		
		lista.setSource(permissoesNegadas);
		lista.setTarget(permissoesConcedidas);
		
		return lista;
	}
	
	public DualListModel<Perfil> listarPerfisModel(List<Perfil> perfis) {
		List<Perfil> perfisDisponiveis = new ArrayList<Perfil>();
		List<Perfil> perfisDoAcesso = new ArrayList<Perfil>();
		DualListModel<Perfil> lista = new DualListModel<Perfil>();
		
		perfisDisponiveis = dao.listarPerfisDisponiveis(perfis);
		
		lista.setSource(perfisDisponiveis);
		
		if (perfis != null) {
			lista.setTarget(perfis);
		} else {
			lista.setTarget(perfisDoAcesso);
		}
		
		return lista;
	}
	
	public List<Perfil> buscarPerfisPorFiltro(FiltroPerfil filtro) {
		return dao.buscarPerfisPorFiltro(filtro);
	}

	public int buscarQuantidadeFiltrada(FiltroPerfil filtro) {
		return dao.buscarQuantidadeFiltrada(filtro);
	}
	
	public List<Permissao> buscarPermissoesDoPerfil(Perfil perfil) {
		return dao.buscarPermissoesDoPerfil(perfil);
	}

	public Perfil buscarPorId(long id) {
		Perfil perfil = dao.buscarPorId(id);
		
		if (perfil != null) {
			perfil.setAutorRegistro(auditDAO.autorRegistro(perfil, perfil.getId()));
			perfil.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(perfil, perfil.getId()));
		}
		
		return perfil;
	}
}
