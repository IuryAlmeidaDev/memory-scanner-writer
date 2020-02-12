package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.OrgaoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroOrgao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Orgao;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Stateless
public class OrgaoService {

	@Inject
	private OrgaoDAO dao;
	
	@Inject
	private AuditoriaDAO auditDAO;

	public void salvar(Orgao orgao) throws ValidacaoException {
		if (orgao.getId() == Sigepen.SEJUDH_ID) {
			throw new ValidacaoException("Operação não permitida.");
		}
		
		if (jaExisteOrgaoComMesmoNome(orgao)) {
			throw new ValidacaoException("Já existe um órgão com o mesmo nome (" + orgao.getNome() + ").");
		}
		
		dao.salvar(orgao);
	}

	private boolean jaExisteOrgaoComMesmoNome(Orgao orgao) {
		return dao.jaExisteOrgaoComMesmoNome(orgao);
	}

	public void excluir(Orgao orgao) throws ValidacaoException {
		if (isOrgaoAssociadoAUmServidor(orgao)) {
			throw new ValidacaoException("O Órgão " + orgao.getNome() + " já está associado a um servidor.");
		}
		
		dao.excluir(orgao);
	}

	private boolean isOrgaoAssociadoAUmServidor(Orgao orgao) {
		return dao.isOrgaoAssociadoAUmServidor(orgao);
	}

	public List<Orgao> buscaPorFiltro(FiltroOrgao filtro, boolean isBuscaRelatorio) {
		return dao.buscaPorFiltro(filtro, isBuscaRelatorio);
	}

	public int quantidadeFiltrados(FiltroOrgao filtro) {
		return dao.quantidadeFiltrados(filtro);
	}
	
	public List<Orgao> listarOrgaoAtivo() {
		return dao.listarOrgaoAtivo();
	}

	public Orgao buscarPorId(long id) {
		Orgao orgao = dao.buscarPorId(id);
		
		if (orgao != null) {
			orgao.setAutorRegistro(auditDAO.autorRegistro(orgao, orgao.getId()));
			orgao.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(orgao, orgao.getId()));
		}
		
		return orgao;
	}
}
