package br.gov.mt.sejudh.sigepen.service;

import java.util.Comparator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.CredenciamentoDAO;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.FaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.model.dto.CredenciamentoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.FaseDoCredenciamentoDTO;

@Stateless
public class CredenciamentoService extends BaseService<Credenciamento, Long> {

	private static final long serialVersionUID = 6611700174660923936L;
	
    private CredenciamentoDAO credenciamentoDAO;
	
	@Inject
	private ParametroUnidadeService parametroUnidadeService;

    @Inject
    public void setCredenciamentoDAO(CredenciamentoDAO credDAO) {
		this.credenciamentoDAO = credDAO;
		this.setDao(credDAO);
	}

    public List<Credenciamento> listarCredenciamentoPorVisitante(Visitante visitante) {
        return this.credenciamentoDAO.buscarPorVisitante(visitante);
    }
    
    public List<Credenciamento> buscarCredenciamentoEntidadesRelacionadasPorVisitante(Visitante visitante) {
        return this.credenciamentoDAO.buscarCredenciamentoEntidadesRelacionadasPorVisitante(visitante);
    }
    
    public Credenciamento listarCredenciamentoVisitante(long id) {
    	return this.credenciamentoDAO.listarCredenciamentoVisitante(id);
    }
    public List<FaseDoCredenciamento> listaFasesDoCredenciamento(Credenciamento credenciamento) {
        return this.credenciamentoDAO.listaFasesDoCredenciamento(credenciamento);
    }
    public List<Credenciamento> buscarPorVisitanteId(Long id) {
        return this.credenciamentoDAO.buscarPorVisitanteId(id);
    }
    
    public List<Credenciamento> buscarCredenciamentoComFasesPorVisitanteId(Long id) {
        return this.credenciamentoDAO.buscarCredenciamentoComFasesPorVisitanteId(id);
    }
    
    public Credenciamento buscarCredenciamentos(Credenciamento cred) {
    	return this.credenciamentoDAO.buscarCredenciamentos(cred);
    }

    public void setDocumentosValidados(long id, long idCredenciamento, Visitante visitante) {
    	this.credenciamentoDAO.setDocumentosValidados(id, idCredenciamento, visitante);
    }

	public void atualizarStatusCredenciamento(FaseDoCredenciamento faseDoCredenciamento) {
		this.credenciamentoDAO.atualizarStatusCredenciamento(faseDoCredenciamento);
	}
	
	public void atualizarStatusAprovacao(Credenciamento credenciamento, StatusCredenciamento status) {
		this.credenciamentoDAO.atualizarStatusAprovacao(credenciamento, status);
	}
	
	public void atualizarStatusCredenciamento(Credenciamento credenciamento) {
		this.credenciamentoDAO.atualizarStatusCredenciamento(credenciamento);
	}
	
	public void atualizarStatusCredenciamento(Visitante visitante) {
		this.credenciamentoDAO.atualizarStatusCredenciamento(visitante);
	}
	
	public Credenciamento buscarUltimoCredenciamento(Visitante visitante) {
		return this.credenciamentoDAO.buscarUltimoCredenciamento(visitante);
	}

	public FaseDoCredenciamentoDTO getProximaFaseCredenciamento(CredenciamentoDTO cred) {
		FaseDoCredenciamentoDTO fase = cred.getFases().stream()
                .max(Comparator.comparing(o -> o.getFaseCredenciamento().getId())).orElse(new FaseDoCredenciamentoDTO());

		FaseDoCredenciamentoDTO retorno = new FaseDoCredenciamentoDTO();
		
		if(fase.getId() == null) {
			retorno.setFaseCredenciamento(FaseCredenciamento.RECEBIMENTO);
			retorno.setStatusCredenciamento(StatusCredenciamento.PENDENTE);
		} else {
			retorno.setFaseCredenciamento(parametroUnidadeService.buscarFase(cred, fase));
			
			if(FaseCredenciamento.APROVACAO == fase.getFaseCredenciamento()) {
				retorno.setStatusCredenciamento(fase.getStatusCredenciamento());
			} else {
				retorno.setStatusCredenciamento(StatusCredenciamento.PENDENTE);
			}
		}
		
		return retorno;
	}

	public Credenciamento buscarCredenciamentoVisitante(Long id) {
		return this.credenciamentoDAO.buscarCredenciamentoVisitante(id);
	}
}
