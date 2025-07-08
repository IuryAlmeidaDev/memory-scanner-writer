package br.gov.mt.sejudh.sigepen.dao;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.shiro.SecurityUtils;
import org.hibernate.annotations.QueryHints;

import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Certidao;
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
import br.gov.mt.sejudh.sigepen.model.Plantao;
import br.gov.mt.sejudh.sigepen.model.Raca;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.RegistroPlantao;
import br.gov.mt.sejudh.sigepen.model.Religiao;
import br.gov.mt.sejudh.sigepen.model.TipoDocumento;
import br.gov.mt.sejudh.sigepen.model.TipoHistorico;
import br.gov.mt.sejudh.sigepen.model.TipoImpedimento;
import br.gov.mt.sejudh.sigepen.model.TipoSanguineo;
import br.gov.mt.sejudh.sigepen.model.TipoServidor;
import br.gov.mt.sejudh.sigepen.model.TipoUnidade;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
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
	public List<TipoHistorico> listarTipoHistoricoPermitidoParaCadastrar() {
		String jpql = "select th from TipoHistorico th " +
				"join th.funcionalidade f " +
				"join f.permissoes p " +
				"where p.nome like 'CadastrarHistoricoRecuperando%'";
		Query query = entityManager.createQuery(jpql, TipoHistorico.class);
		List<TipoHistorico> tipoHistorico = query.getResultList();

		// Filtrar apenas os tipos de histórico para os quais o usuário tem permissão de adicionar
		List<TipoHistorico> tipoHistoricoFiltrado = new ArrayList<>();
		for (TipoHistorico tipo : tipoHistorico) {
			if (tipo.getFuncionalidade() != null &&
				tipo.getFuncionalidade().getPermissoes() != null) {
				for (Permissao permissao : tipo.getFuncionalidade().getPermissoes()) {
					if (permissao.getNome().startsWith("CadastrarHistoricoRecuperando") &&
						SecurityUtils.getSubject().isPermitted(permissao.getNome())) {
						tipoHistoricoFiltrado.add(tipo);
						break;
					}
				}
			}
		}

		return tipoHistoricoFiltrado;
	}

	@SuppressWarnings("unchecked")
	public List<TipoHistorico> listarTipoHistoricoParaVisualizar() {
		String jpql = "select th from TipoHistorico th " +
				"join th.funcionalidade f " +
				"join f.permissoes p " +
				"where p.nome like 'Visualizar%'";
		Query query = entityManager.createQuery(jpql, TipoHistorico.class);
		List<TipoHistorico> tipoHistorico = query.getResultList();

		// Filtrar apenas os tipos de histórico para os quais o usuário tem permissão de visualizar
		List<TipoHistorico> tipoHistoricoFiltrado = new ArrayList<>();
		for (TipoHistorico tipo : tipoHistorico) {
			if (tipo.getFuncionalidade() != null &&
				tipo.getFuncionalidade().getPermissoes() != null) {
				for (Permissao permissao : tipo.getFuncionalidade().getPermissoes()) {
					if (permissao.getNome().startsWith("Visualizar") &&
						SecurityUtils.getSubject().isPermitted(permissao.getNome())) {
						tipoHistoricoFiltrado.add(tipo);
						break;
					}
				}
			}
		}

		return tipoHistoricoFiltrado;
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
			query.setHint(QueryHints.READ_ONLY, true);
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<RegistroPlantao> listarRegistrosPlantao() {
	    try {
	        String jpql = "SELECT rp FROM RegistroPlantao rp "
	                    + "JOIN FETCH rp.plantao p " // se quiser garantir o fetch de dados relacionados
	                    + "ORDER BY rp.dataPlantao DESC";

	        TypedQuery<RegistroPlantao> query = entityManager.createQuery(jpql, RegistroPlantao.class);
	        query.setHint(QueryHints.READ_ONLY, true);
	        return query.getResultList();
	    } catch (NoResultException e) {
	        return null;
	    }
	}

	public List<Plantao> listarPlantoesCadastrados() {
	    try {
	        String jpql = "SELECT p FROM Plantao p "
	                    + "WHERE p.status = :status " // opcional: apenas os ativos
	                    + "ORDER BY p.dataPlantao DESC";

	        TypedQuery<Plantao> query = entityManager.createQuery(jpql, Plantao.class);
	        query.setParameter("status", Status.ATIVO); // ajuste conforme sua enum
	        query.setHint(QueryHints.READ_ONLY, true);
	        return query.getResultList();

	    } catch (NoResultException e) {
	        return Collections.emptyList();
	    }
	}



    public List<TipoVisita> listarTipoVisitas() {
        TypedQuery<TipoVisita> query = entityManager
                .createQuery("select f from TipoVisita f order by f.descricao asc", TipoVisita.class);
        return query.getResultList();
    }

    public List<Certidao> listarCertidoes() {
        TypedQuery<Certidao> query = entityManager.createQuery(
                "select f from Certidao f where f.status = :status order by f.descricao asc",
                Certidao.class
        );
        query.setParameter("status", SgpStatus.A);

        return query.getResultList();
    }

    public List<TipoImpedimento> listarTipoImpedimentos() {
        TypedQuery<TipoImpedimento> query = entityManager.createQuery(
                "select f from TipoImpedimento f where f.status = :status order by f.descricao asc",
                TipoImpedimento.class
        );
        query.setParameter("status", SgpStatus.A);

        return query.getResultList();
    }
}
