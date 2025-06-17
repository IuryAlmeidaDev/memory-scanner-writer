package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.AcessoDAO;
import br.gov.mt.sejudh.sigepen.model.Acesso;

import javax.inject.Inject;
import java.util.List;

public class AcessoService extends BaseService<Acesso, Long> {

	private static final long serialVersionUID = -53621150481705661L;

	@Inject
	private AcessoDAO acessoDAO;

	@Inject
	public AcessoService(AcessoDAO acessoDAO) {
		this.acessoDAO = acessoDAO;
		this.setDao(acessoDAO);
	}

	public List<Acesso> buscarAcessosPorUsuarioId(long idUsuario) {
		return acessoDAO.buscarAcessosPorUsuarioId(idUsuario);
	}

	public List<Acesso> buscarAcessosPorUsuarioIdAndUnidadeId(long idUsuario,long idUnidade) {
		return acessoDAO.buscarAcessosPorUsuarioIdAndUnidadeId(idUsuario,idUnidade);
	}
}
