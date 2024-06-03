package br.gov.mt.sejudh.sigepen.dao;

import java.math.BigDecimal;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.ParametroVisita;
import br.gov.mt.sejudh.sigepen.model.ParametroVisitaEstrutura;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@RequestScoped
public class ParametroVisitaDAO {
	
	@Inject
	private EntityManager em;

	public List<ParametroVisita> buscarParametrosVisita(long id) {
		UnidadePrisional up = em.find(UnidadePrisional.class, id);
		Hibernate.initialize(up.getParametrosVisita());
		if(up.getParametrosVisita() != null) {
			for(ParametroVisita p : up.getParametrosVisita()) {
				Hibernate.initialize(p.getEstruturas());
				Hibernate.initialize(p.getBloqueios());
			}
		}
		return up.getParametrosVisita();
	}

	public ParametroVisita salvar(ParametroVisita parametro) {
		if(parametro.getId() == null) {
			this.em.persist(parametro);
			return parametro;
		}else
			return this.em.merge(parametro);
	}

	public void excluirParametroVisita(ParametroVisita p) {
		this.em.remove(this.em.merge(p));
	}
	
	public boolean parametroExistente(ParametroVisita parametro) {
		String ids = "";
		if(parametro.getEstruturas() != null) {
			for(ParametroVisitaEstrutura e : parametro.getEstruturas()) {
				ids += e.getEstrutura().getId() + ",";
			}
		}
		String whereIds = "";
		if(!"".equals(ids)) {
			ids += " -1";
			whereIds = " AND PE.ID_ESTRUTURA IN (" + ids + ")";
		}
			
		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append("SELECT TO_NUMBER(COUNT(P.ID)) FROM " + Sigepen.SEJUDH_SCHEMA + ".SGP_PARAMETRO_VISITA P ");
			jpql.append(" INNER JOIN " + Sigepen.SEJUDH_SCHEMA + ".SGP_PARAM_VISITA_ESTRUTURA PE  ");
			jpql.append(" ON P.ID = PE.ID_PARAMETRO_VISITA ");
			jpql.append(" WHERE P.ID_UNIDADE_PRISIONAL = :unidade ");
			if(parametro.getHoraEntrada() != null && parametro.getHoraEntrada().length() == 4)
				jpql.append(" AND ((:d1 >= TO_NUMBER(P.HORA_ENTRADA) and :d2 <= TO_NUMBER(P.HORA_SAIDA)) OR (:d1 > TO_NUMBER(P.HORA_ENTRADA) AND :d1 < TO_NUMBER(P.HORA_SAIDA))  or (:d2 > TO_NUMBER(P.HORA_ENTRADA) AND :d2 < TO_NUMBER(P.HORA_SAIDA)) or (:d1 < TO_NUMBER(P.HORA_ENTRADA) AND :d2 > TO_NUMBER(P.HORA_SAIDA))) ");
			jpql.append(" AND P.STATUS = '" + SgpStatus.A + "'");
			if(parametro.getId() != null)
				jpql.append(" AND P.ID <> " + parametro.getId());
			jpql.append(" AND P.ID_TIPO_ENTRADA = " + parametro.getTipoEntrada().getId());
			jpql.append(" AND ( 1 = 0");
			if(parametro.isDomingo())
				jpql.append(" OR P.DOMINGO = 1");
			if(parametro.isSegunda())
				jpql.append(" OR P.SEGUNDA = 1 ");
			if(parametro.isTerca())
				jpql.append(" OR P.TERCA = 1 ");
			if(parametro.isQuarta())
				jpql.append(" OR P.QUARTA = 1 ");
			if(parametro.isQuinta())
				jpql.append(" OR P.QUINTA = 1 ");
			if(parametro.isSexta())
				jpql.append(" OR P.SEXTA = 1 ");
			if(parametro.isSabado())
				jpql.append(" OR P.SABADO = 1 ");
			jpql.append(" )");
			jpql.append(whereIds);
			
			Query query = this.em.createNativeQuery(jpql.toString());		
			query.setParameter("unidade", parametro.getUnidade().getId());
			if(parametro.getHoraEntrada() != null && parametro.getHoraEntrada().length() == 4) {
				query.setParameter("d1", parametro.getHoraEntrada());
				query.setParameter("d2", parametro.getHoraSaida());
			}
			BigDecimal quantidade = (BigDecimal)query.getSingleResult();
			return quantidade.toBigInteger().longValue() > 0;
		} catch (NoResultException e) {
			return true;
		}		
	}
	
	public boolean estruturaExistente(ParametroVisita parametro, Estrutura estrutura) {
		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append("SELECT TO_NUMBER(COUNT(P.ID)) FROM " + Sigepen.SEJUDH_SCHEMA + ".SGP_PARAMETRO_VISITA P ");
			jpql.append(" INNER JOIN " + Sigepen.SEJUDH_SCHEMA + ".SGP_PARAM_VISITA_ESTRUTURA PE  ");
			jpql.append(" ON P.ID = PE.ID_PARAMETRO_VISITA ");
			jpql.append(" WHERE P.ID_UNIDADE_PRISIONAL = :unidade");
			if(parametro.getHoraEntrada() != null && parametro.getHoraEntrada().length() == 4)
				jpql.append(" AND ((:d1 >= TO_NUMBER(P.HORA_ENTRADA) and :d2 <= TO_NUMBER(P.HORA_SAIDA)) OR (:d1 > TO_NUMBER(P.HORA_ENTRADA) AND :d1 < TO_NUMBER(P.HORA_SAIDA))  or (:d2 > TO_NUMBER(P.HORA_ENTRADA) AND :d2 < TO_NUMBER(P.HORA_SAIDA)) or (:d1 < TO_NUMBER(P.HORA_ENTRADA) AND :d2 > TO_NUMBER(P.HORA_SAIDA))) ");
			jpql.append(" AND P.STATUS = '" + SgpStatus.A + "'");
			if(parametro.getId() != null)
				jpql.append(" AND P.ID <> " + parametro.getId());
			jpql.append(" AND P.ID_TIPO_ENTRADA = " + parametro.getTipoEntrada().getId());
			jpql.append(" AND ( 1 = 0");
			if(parametro.isDomingo())
				jpql.append(" OR P.DOMINGO = 1");
			if(parametro.isSegunda())
				jpql.append(" OR P.SEGUNDA = 1 ");
			if(parametro.isTerca())
				jpql.append(" OR P.TERCA = 1 ");
			if(parametro.isQuarta())
				jpql.append(" OR P.QUARTA = 1 ");
			if(parametro.isQuinta())
				jpql.append(" OR P.QUINTA = 1 ");
			if(parametro.isSexta())
				jpql.append(" OR P.SEXTA = 1 ");
			if(parametro.isSabado())
				jpql.append(" OR P.SABADO = 1 ");
			jpql.append(" )");
			jpql.append(" AND PE.ID_ESTRUTURA = " + Long.toString(estrutura.getId()));
			
			Query query = this.em.createNativeQuery(jpql.toString());
			query.setParameter("unidade", parametro.getUnidade().getId());
			if(parametro.getHoraEntrada() != null && parametro.getHoraEntrada().length() == 4) {
				query.setParameter("d1", parametro.getHoraEntrada());
				query.setParameter("d2", parametro.getHoraSaida());
			}
			BigDecimal quantidade = (BigDecimal)query.getSingleResult();
			return quantidade.toBigInteger().longValue() > 0;
		} catch (NoResultException e) {
			return true;
		}		
	}

	@SuppressWarnings("unchecked")
	public List<ParametroVisitaEstrutura> listarEstruturas(ParametroVisita p) {
		Session session = this.em.unwrap(Session.class);
		List<ParametroVisitaEstrutura> pve = session.createSQLQuery("SELECT * FROM sigepen_owner.SGP_PARAM_VISITA_ESTRUTURA pve WHERE pve.id_parametro_visita = :id")
			    .addEntity(ParametroVisitaEstrutura.class)
			    .setParameter("id", p.getId())
			    .list();
		 
		 for(ParametroVisitaEstrutura p1 : pve) {
			 Hibernate.initialize(p1.getEstrutura());
		 }
		 
		 return pve;
	}
}
