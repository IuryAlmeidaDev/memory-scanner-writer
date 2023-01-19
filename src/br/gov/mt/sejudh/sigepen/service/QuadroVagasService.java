package br.gov.mt.sejudh.sigepen.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.QuadroVagasDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroQuadroVagas;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.QuadroVagasDTO;


@Stateless
public class QuadroVagasService {
	
	@Inject
	private QuadroVagasDAO dao;	
	
	@Inject
	private AuditoriaDAO auditDAO;

	public UnidadePrisional buscaPorId(Long id) {
		UnidadePrisional u = dao.buscaPorId(id);
		
		if (u != null) {
			u.setQuantidadeAtual(quantidadeAtual(id));
			u.setAutorRegistro(auditDAO.autorRegistro(u, u.getId()));
			u.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(u, u.getId()));
		}
		
		return u;
	}
	
	public List<QuadroVagasDTO> buscarPorFiltro(FiltroUnidadePrisional filtro, boolean isRelatorio){
		return dao.buscaPorFiltro(filtro, isRelatorio);
	}
	
	public List<QuadroVagasDTO> buscaQuantidadeRecuperados(FiltroQuadroVagas filtroUnidadePrisional, boolean relatorio){
		
		return addCapacidadeTotalUnidade(dao.buscaQuantidadeRecuperados(filtroUnidadePrisional, false, relatorio));
	}
	
	private List<QuadroVagasDTO> addCapacidadeTotalUnidade(List<QuadroVagasDTO> recuperados){
		List<QuadroVagasDTO> quadroVagas = dao.buscaCapacidadeTotalUnidade();
		List<QuadroVagasDTO> quadroVagasNovo = new ArrayList<>();
		
		for(QuadroVagasDTO recuperando :  recuperados) {
			for(QuadroVagasDTO quadroVaga :  quadroVagas) {
				if(recuperando.getId() == quadroVaga.getId()) {
					recuperando.setCapacidadeTotal(quadroVaga.getCapacidadeTotal());
					recuperando.calculoQntDeficit(quadroVaga.getCapacidadeTotal(), recuperando.getTotalRecuperando());
					recuperando.calculoQntVagas(quadroVaga.getCapacidadeTotal(), recuperando.getTotalRecuperando());
					quadroVagasNovo.add(recuperando);
				}
			}
		}
		return quadroVagasNovo;
	}
	
	public int quantidadeFiltrados(FiltroQuadroVagas filtro){
		return dao.quantidadeFiltrados(filtro);
	}	

	public Long quantidadeAtual(Long id){
		return dao.quantidadeAtual(id);
	}

}
