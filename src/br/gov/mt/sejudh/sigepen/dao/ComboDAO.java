package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.gov.mt.sejudh.sigepen.model.Cidade;
import br.gov.mt.sejudh.sigepen.model.Escolaridade;
import br.gov.mt.sejudh.sigepen.model.Estado;
import br.gov.mt.sejudh.sigepen.model.EstadoCivil;
import br.gov.mt.sejudh.sigepen.model.Funcionalidade;
import br.gov.mt.sejudh.sigepen.model.IdentidadeDeGenero;
import br.gov.mt.sejudh.sigepen.model.Nacionalidade;
import br.gov.mt.sejudh.sigepen.model.OrientacaoSexual;
import br.gov.mt.sejudh.sigepen.model.Pais;
import br.gov.mt.sejudh.sigepen.model.Parentesco;
import br.gov.mt.sejudh.sigepen.model.Raca;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.Religiao;
import br.gov.mt.sejudh.sigepen.model.TipoDocumento;
import br.gov.mt.sejudh.sigepen.model.TipoHistorico;
import br.gov.mt.sejudh.sigepen.model.TipoSanguineo;
import br.gov.mt.sejudh.sigepen.model.TipoServidor;
import br.gov.mt.sejudh.sigepen.model.TipoUnidade;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;

@RequestScoped
public class ComboDAO {

	@Inject
	private EntityManager entityManager;

	public ComboDAO() {
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	public List<Regime> listarRegime() {
		String jpql = "select r from Regime r ";
		Query query = entityManager.createQuery(jpql, Regime.class);
		List<Regime> regimes = query.getResultList();
		return regimes;
	}

	@SuppressWarnings("unchecked")
	public List<Pais> listarPais() {
		String jpql = "select p from Pais p where p.status = 'A' order by p.nome";
		Query query = entityManager.createQuery(jpql, Pais.class);
		List<Pais> paises = query.getResultList();
		return paises;
	}

	@SuppressWarnings("unchecked")
	public List<Religiao> listarReligiao() {
		String jpql = "select r from Religiao r ";
		Query query = entityManager.createQuery(jpql, Religiao.class);
		List<Religiao> religioes = query.getResultList();
		return religioes;
	}

	@SuppressWarnings("unchecked")
	public List<Raca> listarRaca() {
		String jpql = "select r from Raca r ";
		Query query = entityManager.createQuery(jpql, Raca.class);
		List<Raca> racas = query.getResultList();
		return racas;
	}
	
	@SuppressWarnings("unchecked")
	public List<EstadoCivil> listarEstadoCivil() {
		String jpql = "select e from EstadoCivil e";
		Query query = entityManager.createQuery(jpql, EstadoCivil.class);
		List<EstadoCivil> estadoCivil = query.getResultList();
		return estadoCivil;
	}
	
	@SuppressWarnings("unchecked")
	public List<OrientacaoSexual> listarOrientacaoSexual() {
		String jpql = "select o from OrientacaoSexual o ";
		Query query = entityManager.createQuery(jpql, OrientacaoSexual.class);
		List<OrientacaoSexual> orientacaoSexual = query.getResultList();
		return orientacaoSexual;
	}
	
	@SuppressWarnings("unchecked")
	public List<IdentidadeDeGenero> listarIdentidadeDeGenero() {
		String jpql = "select i from IdentidadeDeGenero i ";
		Query query = entityManager.createQuery(jpql, IdentidadeDeGenero.class);
		List<IdentidadeDeGenero> identidadeDeGenero = query.getResultList();
		return identidadeDeGenero;
	}

	@SuppressWarnings("unchecked")
	public List<TipoSanguineo> listarTipoSanguineo() {
		String jpql = "select t from TipoSanguineo t order by t.descricao";
		Query query = entityManager.createQuery(jpql, TipoSanguineo.class);
		List<TipoSanguineo> tipoSanguineo = query.getResultList();
		return tipoSanguineo;
	}

	@SuppressWarnings("unchecked")
	public List<Escolaridade> listarEscolaridade() {
		String jpql = "select e from Escolaridade e ";
		Query query = entityManager.createQuery(jpql, Escolaridade.class);
		List<Escolaridade> escolaridades = query.getResultList();
		return escolaridades;
	}

	@SuppressWarnings("unchecked")
	public List<Parentesco> listarParentesco() {
		String jpql = "select p from Parentesco p ";
		Query query = entityManager.createQuery(jpql, Parentesco.class);
		List<Parentesco> parentescos = query.getResultList();
		return parentescos;
	}

	@SuppressWarnings("unchecked")
	public List<TipoServidor> listarTipoServidor() {
		String jpql = "select t from TipoServidor t ";
		Query query = entityManager.createQuery(jpql, TipoServidor.class);
		List<TipoServidor> tipoServidor = query.getResultList();
		return tipoServidor;
	}

	@SuppressWarnings("unchecked")
	public List<TipoHistorico> listarTipoHistorico() {
		String jpql = "select h from TipoHistorico h ";
		Query query = entityManager.createQuery(jpql, TipoHistorico.class);
		List<TipoHistorico> tipoHistorico = query.getResultList();
		return tipoHistorico;
	}

	@SuppressWarnings("unchecked")
	public List<Estado> listarEstado() {
		String jpql = "select e from Estado e where e.status = :status  order by e.nome";
		Query query = entityManager.createQuery(jpql, Estado.class);
		query.setParameter("status", "A");
		List<Estado> estados = query.getResultList();
		return estados;
	}
	
	public Estado buscarEstado(String nomeEstado) {
		String jpql = "select e from Estado e where e.nome = :nome";
		Query query = entityManager.createQuery(jpql, Estado.class);
		query.setParameter("nome", "nomeEstado");
		Estado estado = (Estado) query.getSingleResult();
		return estado;
	}

	@SuppressWarnings("unchecked")
	public List<Cidade> listarCidade(Estado estado) {
		String jpql = "select c from Cidade c WHERE c.estado= :id and c.status = :status order by c.nome";
		Query query = entityManager.createQuery(jpql, Cidade.class);
		query.setParameter("id", estado);
		query.setParameter("status", "A");
		List<Cidade> cidades = query.getResultList();
		return cidades;
	}

	@SuppressWarnings("unchecked")
	public List<TipoUnidade> listarTipoUnidade() {
		String jpql = "select t from TipoUnidade t";
		Query query = entityManager.createQuery(jpql, TipoUnidade.class);
		List<TipoUnidade> tipoUnidades = query.getResultList();
		return tipoUnidades;
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoDocumento> listarTipoDocumento() {
		String jpql = "select t from TipoDocumento t where t.status = :status order by t.descricao desc";
		Query query = entityManager.createQuery(jpql, TipoDocumento.class);
		query.setParameter("status", "A");
		List<TipoDocumento> tipoDocumentos = query.getResultList();
		return tipoDocumentos;
	}
	
	@SuppressWarnings("unchecked")
	public List<Nacionalidade> listarNacionalidades() {
		String jpql = "select n from Nacionalidade n";
		Query query = entityManager.createQuery(jpql, Nacionalidade.class);
		List<Nacionalidade> nacionalidades = query.getResultList();
		return nacionalidades;
	}

	public List<Funcionalidade> listarFuncionalidades() {
		TypedQuery<Funcionalidade> query = entityManager
				.createQuery("select f from Funcionalidade f order by f.id asc", Funcionalidade.class);
		return query.getResultList();
	}

	public List<UnidadePrisional> listarUnidadesPrisionais() {
		try {
			String jpql="select new br.gov.mt.sejudh.sigepen.model.UnidadePrisional(u.id, u.nome) from UnidadePrisional u ";
			TypedQuery<UnidadePrisional> query = entityManager.createQuery(jpql, UnidadePrisional.class);			
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
}
