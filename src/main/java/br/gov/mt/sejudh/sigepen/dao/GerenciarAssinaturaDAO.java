package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.GerenciarAssinatura;
import br.gov.mt.sejudh.sigepen.model.GerenciarAssinaturaServidor;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

@RequestScoped
public class GerenciarAssinaturaDAO extends BaseDAO<GerenciarAssinatura, Long> {


	private static final long serialVersionUID = 1635530084073694819L;

    @Inject
    private Logger log;

    @Override
	public List<GerenciarAssinatura> listarTodos(){


		TypedQuery<GerenciarAssinatura> query = em.createQuery("FROM " + GerenciarAssinatura.class.getName(), GerenciarAssinatura.class);

		return getGerenciarAssinaturas(query);
	}

	private List<GerenciarAssinatura> getGerenciarAssinaturas(TypedQuery<GerenciarAssinatura> query) {
		List<GerenciarAssinatura> result = query.getResultList();

		for(GerenciarAssinatura gerenciarAssinatura : result){
			Hibernate.initialize(gerenciarAssinatura.getGerenciarAssinaturaServidores());
			for(GerenciarAssinaturaServidor gerenciarAssinaturaServidor : gerenciarAssinatura.getGerenciarAssinaturaServidores()){
				Hibernate.initialize(gerenciarAssinaturaServidor.getServidor());

				Hibernate.initialize(gerenciarAssinaturaServidor.getServidor().getCargo());
				Hibernate.initialize(gerenciarAssinaturaServidor.getServidor().getFuncao());
			}
		}

		return result;
	}

	public List<GerenciarAssinatura> listarTodos(UnidadePrisional unidade) {

		String jpql="select ga from GerenciarAssinatura ga where ga.unidadePrisional.id = :idUnidadePrisional";

		TypedQuery<GerenciarAssinatura> query = em.createQuery(jpql, GerenciarAssinatura.class);

		query.setParameter("idUnidadePrisional", unidade.getId());

		return getGerenciarAssinaturas(query);
	}

	public GerenciarAssinatura buscarGerenciarAssinatura(GerenciarAssinatura gerenciarAssinatura) {

		StringBuilder jpql= new StringBuilder("select ga from GerenciarAssinatura ga " +
				"where ga.unidadePrisional.id = :unidadePrisionalId " +
				"AND ga.status = :status and ga.relatorio = :relatorio "
				);

		if(gerenciarAssinatura.getId() != null){
			jpql.append("AND ga.id != :id ");
		}

		TypedQuery<GerenciarAssinatura> query = em.createQuery(jpql.toString(), GerenciarAssinatura.class);
		query.setParameter("unidadePrisionalId", gerenciarAssinatura.getUnidadePrisional().getId());
		query.setParameter("status", Status.ATIVO);
		query.setParameter("relatorio", gerenciarAssinatura.getRelatorio());

		if(gerenciarAssinatura.getId() != null){
			query.setParameter("id", gerenciarAssinatura.getId());
		}


		try{
			return query.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}

	}

    public GerenciarAssinatura buscar(UnidadePrisional unidade, Relatorio relatorio) {

		StringBuilder jpql= new StringBuilder("select ga from GerenciarAssinatura ga " +
				"where ga.unidadePrisional.id = :unidadePrisionalId " +
				"AND ga.status = :status and ga.relatorio = :relatorio "
		);

		TypedQuery<GerenciarAssinatura> query = em.createQuery(jpql.toString(), GerenciarAssinatura.class);
		query.setParameter("unidadePrisionalId", unidade.getId());
		query.setParameter("status", Status.ATIVO);
		query.setParameter("relatorio", relatorio);


		try{
			GerenciarAssinatura result = query.getSingleResult();
			for(GerenciarAssinaturaServidor gerenciarAssinaturaServidor : result.getGerenciarAssinaturaServidores()){
				Hibernate.initialize(gerenciarAssinaturaServidor.getServidor());

				Hibernate.initialize(gerenciarAssinaturaServidor.getServidor().getCargo());
				Hibernate.initialize(gerenciarAssinaturaServidor.getServidor().getFuncao());
			}

			return result;
		} catch (NoResultException e) {
			log.error("Erro ao buscar gerenciar assinatura {}", e.getMessage());
			return null;
		}
    }
}