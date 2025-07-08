package br.gov.mt.sejudh.sigepen.service;


import java.time.LocalDate;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.RegistroPlantaoDAO;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPlantao;
import br.gov.mt.sejudh.sigepen.model.Plantao;
import br.gov.mt.sejudh.sigepen.model.RegistroPlantao;
import br.gov.mt.sejudh.sigepen.model.RegistroPlantaoServidor;


@Stateless
public class RegistroPlantaoService extends BaseService<RegistroPlantao, Long>{

	private static final long serialVersionUID = 1L;

	@Inject
	private RegistroPlantaoDAO dao;
	
	@Inject
	public void setRegistroPlantãoDAO(RegistroPlantaoDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}
	
	
	/* ------------------------- Métodos ------------------------- */
	
	public List<RegistroPlantao> listarRegistroPlantaoPorStatus() {
        return dao.listarRegistroPlantaoPorStatus(StatusPlantao.ANDAMENTO);
    }
	
	
	public List<Plantao> listarPlantaoPorStatus() {
        return dao.listarPlantaoPorStatus(Status.ATIVO);
    }
	
	public RegistroPlantao buscarUltimoRegistro() {
        return dao.buscarUltimoRegistro();
    }
	
	public RegistroPlantaoServidor salvarEquipePlantao(RegistroPlantaoServidor registroPlantaoServidor) {
        return dao.salvarEquipePlantao(registroPlantaoServidor);
    }
	
	public void atualizar(RegistroPlantaoServidor registroPlantaoServidor) {
	    dao.atualizar(registroPlantaoServidor);
	}	
	
	public RegistroPlantaoServidor buscarPorNome(String nomeServidor) {
	    return dao.buscarPorNome(nomeServidor);
	}
	
	public RegistroPlantao buscarUltimoRegistroPlantao() {
	    return dao.buscarUltimoRegistroPlantao();
	}
	
	public RegistroPlantao buscarUltimoRegistroPlantaoComPlantao() {
	    return dao.buscarUltimoRegistroPlantaoComPlantao();
	}
	
	public boolean existeMaisDeUmRegistro(LocalDate dataRegistro) {
	    return dao.existeMaisDeUmRegistro(dataRegistro);
	}
	
	public boolean existeRegistro(String nomeServidor, LocalDate dataRegistro) {
	    return dao.existeRegistro(nomeServidor, dataRegistro);
	}

}
