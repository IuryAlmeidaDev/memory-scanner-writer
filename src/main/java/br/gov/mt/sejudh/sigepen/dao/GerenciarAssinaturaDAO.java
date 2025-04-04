package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroConsulta;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVwVisitanteRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.*;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.*;
import br.gov.mt.sejudh.sigepen.model.dto.AutorizacaoSemBiometriaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.FaseDoCredenciamentoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.VisitanteDTO;
import br.gov.mt.sejudh.sigepen.model.dto.VisitanteListaDTO;
import br.gov.mt.sejudh.sigepen.model.view.AssociarVisitanteResponsavelView;
import br.gov.mt.sejudh.sigepen.model.view.VwVisitante;
import br.gov.mt.sejudh.sigepen.service.VisitanteResponsavelService;
import br.gov.mt.sejudh.sigepen.service.VisitanteService;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.annotations.QueryHints;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RequestScoped
public class GerenciarAssinaturaDAO extends BaseDAO<GerenciarAssinatura, Long> {


	private static final long serialVersionUID = 1635530084073694819L;

	@Override
	public List<GerenciarAssinatura> listarTodos(){


		TypedQuery<GerenciarAssinatura> query = em.createQuery("FROM " + GerenciarAssinatura.class.getName(), GerenciarAssinatura.class);

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

}