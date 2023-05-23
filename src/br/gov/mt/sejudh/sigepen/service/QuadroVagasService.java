package br.gov.mt.sejudh.sigepen.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.QuadroVagasDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroQuadroVagas;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnidadePrisional;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
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
	
	public List<QuadroVagasDTO> buscaQuantidadeRecuperados(FiltroQuadroVagas filtroQuadroVagas, boolean relatorio){
		List<QuadroVagasDTO> quadroVagasDTOs = addCapacidadeTotalUnidade(dao.buscaQuantidadeRecuperados(filtroQuadroVagas, false, relatorio), relatorio);
		quadroVagasDTOs.forEach
		(x -> x.setFiltroQuadroVagas(filtroQuadroVagas));
		return quadroVagasDTOs;
	}
	
	private List<QuadroVagasDTO> countAllTotais(List<QuadroVagasDTO> listaQuadroVagas){
		QuadroVagasDTO countTotais = new QuadroVagasDTO(0L, 0L, 0L,0L, "", "TOTAL", SituacaoUnidade.ATIVO , "");
		QuadroVagasDTO vazio = new QuadroVagasDTO(0L, 0L, 0L,0L, "", "", SituacaoUnidade.ATIVO , "");
		countTotais.setCapacidadeTotal(0L);
		countTotais.setVagas("0");
		countTotais.setDeficit("0");
		vazio.setCapacidadeTotal(0L);
		vazio.setVagas("0");
		vazio.setDeficit("0");
		for(QuadroVagasDTO quadroVaga :  listaQuadroVagas) {
			if(quadroVaga.getCapacidadeTotal() != null) {
				countTotais.setCapacidadeTotal(countTotais.getCapacidadeTotal() + quadroVaga.getCapacidadeTotal());				
			}
			
			if(quadroVaga.getQuantidadeCondenado() != null) {								
				countTotais.setQuantidadeCondenado(countTotais.getQuantidadeCondenado() + quadroVaga.getQuantidadeCondenado());
			}
			
			if(quadroVaga.getQuantidadeProvisorios() != null) {								
				countTotais.setQuantidadeProvisorios(countTotais.getQuantidadeProvisorios() + quadroVaga.getQuantidadeProvisorios());
			}
			if(quadroVaga.getTotalRecuperando() != null) {								
				countTotais.setTotalRecuperando(countTotais.getTotalRecuperando() + quadroVaga.getTotalRecuperando());
			}
			if(quadroVaga.getVagas() != null || !quadroVaga.getVagas().isEmpty()) {				
				long soma = Long.parseLong(countTotais.getVagas()) +  Long.parseLong(quadroVaga.getVagas() == "" ? "0" : quadroVaga.getVagas());
				countTotais.setVagas(String.valueOf(soma));
			}
			if(quadroVaga.getDeficit() != null || !quadroVaga.getDeficit().isEmpty()) {				
				long soma = Long.parseLong(countTotais.getDeficit()) +  Long.parseLong(quadroVaga.getDeficit() == "" ? "0" : quadroVaga.getDeficit());
				countTotais.setDeficit(String.valueOf(soma));
			}
		}
		listaQuadroVagas.add(vazio);
		listaQuadroVagas.add(countTotais);
		return listaQuadroVagas;
	}
	
	private List<QuadroVagasDTO> addCapacidadeTotalUnidade(List<QuadroVagasDTO> recuperados, boolean relatorio){
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
		if(relatorio) {			
			return countAllTotais(quadroVagasNovo);
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
