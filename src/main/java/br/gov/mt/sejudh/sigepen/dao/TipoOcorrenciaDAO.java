package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.TipoOcorrencia;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;


@RequestScoped
public class TipoOcorrenciaDAO extends BaseDAO<TipoOcorrencia, Long> {


	private static final long serialVersionUID = 1635530084073694819L;
	
	@Inject
	private EntityManager entityManager;


	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public List<TipoOcorrencia> listarAtivosPorClassificacao(String classificacao) {
		TypedQuery<TipoOcorrencia> query = em.createQuery(
				"select to from TipoOcorrencia to where to.status = :status and to.classificacao = :classificacao ",
				TipoOcorrencia.class
		);
		query.setParameter("classificacao", classificacao);
		query.setParameter("status", Status.ATIVO);

		return query.getResultList();
	}

}
