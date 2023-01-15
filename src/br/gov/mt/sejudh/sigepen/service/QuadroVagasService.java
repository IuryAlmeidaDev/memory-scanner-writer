package br.gov.mt.sejudh.sigepen.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.QuadroVagasDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnidadePrisional;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.QuadroVagasDTO;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;


@Stateless
public class QuadroVagasService {
	
	@Inject
	private QuadroVagasDAO dao;	
	
	@Inject
	private AuditoriaDAO auditDAO;

	@Interceptors(EJBExcInterceptor.class)
	public UnidadePrisional salvar(UnidadePrisional unidade) throws ValidacaoException {

		if(unidade.getNome() == null || unidade.getSigla() == null || unidade.getTipoUnidade() == null){
			throw new ValidacaoException("Preencha os campos obrigatórios!");
		}

		if(jaExisteUnidadePrisionalComMesmoNome(unidade)){
			throw new ValidacaoException("Já existe uma Unidade Prisional com o mesmo Nome (" + unidade.getNome() + "), e não está INATIVA.");
		}
		Estrutura estrutura;
		if(unidade.getEstrutura() == null){
			estrutura = new Estrutura();
			estrutura.setSituacaoUnidade(SituacaoUnidade.ATIVO);
			estrutura.setUnidadePrisional(unidade);
			estrutura.setEstruturaPai(null);
		} else{
			estrutura = unidade.getEstrutura();
		}
		
		estrutura.setNome(unidade.getNome());
		unidade.setEstrutura(estrutura);
		return dao.salvar(unidade);
	}
	
	private boolean jaExisteUnidadePrisionalComMesmoNome(UnidadePrisional unidadePrisional) {
		return dao.jaExisteUnidadePrisionalComMesmoNome(unidadePrisional);
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void alterar(UnidadePrisional unidade){
		dao.alterar(unidade);
	}

	public void excluir(long idUnidadePrisional) {
		dao.excluir(idUnidadePrisional);		
	}
	
	public UnidadePrisional buscaPorId(Long id) {
		UnidadePrisional u = dao.buscaPorId(id);
		
		if (u != null) {
			u.setQuantidadeAtual(quantidadeAtual(id));
			u.setAutorRegistro(auditDAO.autorRegistro(u, u.getId()));
			u.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(u, u.getId()));
		}
		
		return u;
	}
	
	public UnidadePrisional buscaSimplesPorId(Long id) {		
		return dao.buscaSimplesPorId(id);
	}
	
	public List<QuadroVagasDTO> buscarPorFiltro(FiltroUnidadePrisional filtro, boolean isRelatorio){
		return dao.buscaPorFiltro(filtro, isRelatorio);
	}
	
	public List<QuadroVagasDTO> buscaQuantidadeRecuperados(FiltroUnidadePrisional filtroUnidadePrisional){
		
		return addCapacidadeTotalUnidade(dao.buscaQuantidadeRecuperados(filtroUnidadePrisional));
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
	
	public int quantidadeFiltrados(FiltroUnidadePrisional filtro){
		return dao.quantidadeFiltrados(filtro);
	}	
	
	public List<UnidadePrisional> listarUnidadesNaoInativas(){
		return dao.listarUnidadesNaoInativas();
	}
	
	public Long quantidadeAtual(Long id){
		return dao.quantidadeAtual(id);
	}

	public boolean possuiServidorLotado(long unidadeId) {
		return dao.possuiServidorLotado(unidadeId);
	}

	public Boolean verificarSeBiometriaEhObrigatoria(long unidadeId) {
		return dao.verificarSeBiometriaEhObrigatoria(unidadeId);
	}
	
	public List<UnidadePrisional> listarUnidadesComParamVisitanteAtivo(boolean listarTodasUnidades){
		return dao.listarUnidadesComParamVisitanteAtivo(listarTodasUnidades);
	}
}
