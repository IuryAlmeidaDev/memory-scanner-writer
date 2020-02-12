package br.gov.mt.sejudh.sigepen.dao;

import java.util.Calendar;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.time.DateUtils;

import br.gov.mt.sejudh.sigepen.enumerator.Modalidade;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@RequestScoped
public class DashboardDAO {

	@Inject
	private EntityManager em;
	
	public Long totalRecuperandosAtivos() {
		String jpql = "select count(r.id) from Recuperando r where r.status = 1";
		Query query = em.createQuery(jpql);
		
		try {
			return (Long) query.getSingleResult();
		} catch (NoResultException e) {
			return Long.valueOf(0);
		}
	}
	
	public Long totalRecuperandosAtivosNaUnidade(long unidadeId) {
		String jpql = "select r.qtd_recuperandos from " + Sigepen.SEJUDH_SCHEMA + ".QTD_RECUPERANDOS_POR_UNIDADE r where r.unidade_id = :unidadeId";
		Query query = em.createNativeQuery(jpql);
		query.setParameter("unidadeId", unidadeId);
		
		try {
			return Long.valueOf(query.getSingleResult().toString());
		} catch (NoResultException e) {
			return Long.valueOf(0);
		}
	}
	
	public Long totalRecuperandosComCadastroIncompleto(long unidadeId) {
		String jpql = "select r.qtd_recuperando from " + Sigepen.SEJUDH_SCHEMA + ".qtd_recup_cad_incompl_unidade r where r.unidadeprisional_id = :unidadeId";
		Query query = em.createNativeQuery(jpql);
		query.setParameter("unidadeId", unidadeId);
		
		try {
			return Long.valueOf(query.getSingleResult().toString());
		} catch (NoResultException e) {
			return Long.valueOf(0);
		}
	}
	
	public Long recuperandosTemporariosNaUnidadeDataSolturaAVencer(long unidadeId) {
		String jpql = "select count(distinct r.id) from Recuperando r, Processo p, ProcessoRegimeCondicao prc, "
				+ " Passagem pa, PassagemLotacao pl, Estrutura e "
				+ " where prc.processo.id = p.id "
				+ " and p.recuperando.id = pa.recuperando.id "
				+ " and p.recuperando.id = r.id "
				+ " and r.status = 1 "
				+ " and p.modalidade = :modalidade "
				+ " and pa.id = pl.passagem.id "
				+ " and pa.dataSaida is null "
				+ " and pl.dataSaida is null "
				+ " and pl.estrutura.id = e.id "
				+ " and e.unidadePrisional.id = :unidadeId "
				+ " and trunc(prc.dataSoltura) > trunc(:dataHoje)";
		Query query = em.createQuery(jpql);
		query.setParameter("dataHoje", DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE));
		query.setParameter("unidadeId", unidadeId);
		query.setParameter("modalidade", Modalidade.TEMPORARIA);
		
		try {
			return (Long) query.getSingleResult();
		} catch (NoResultException e) {
			return Long.valueOf(0);
		}
	}
	
	public Long recuperandosTemporariosNaUnidadeDataSolturaVencida(long unidadeId) {
		String jpql = "select count(distinct r.id) from Recuperando r, Processo p, ProcessoRegimeCondicao prc, "
				+ " Passagem pa, PassagemLotacao pl, Estrutura e "
				+ " where prc.processo.id = p.id "
				+ " and p.recuperando.id = pa.recuperando.id "
				+ " and p.recuperando.id = r.id "
				+ " and r.status = 1 "
				+ " and p.modalidade = :modalidade "
				+ " and prc.regime.id <> 4 "
				+ " and pa.id = pl.passagem.id "
				+ " and pa.dataSaida is null "
				+ " and pl.dataSaida is null "
				+ " and pl.estrutura.id = e.id "
				+ " and e.unidadePrisional.id = :unidadeId "
				+ " and trunc(prc.dataSoltura) < trunc(:dataHoje)";
		Query query = em.createQuery(jpql);
		query.setParameter("dataHoje", DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE));
		query.setParameter("unidadeId", unidadeId);
		query.setParameter("modalidade", Modalidade.TEMPORARIA);
		
		try {
			return (Long) query.getSingleResult();
		} catch (NoResultException e) {
			return Long.valueOf(0);
		}
	}
	
	public Long recuperandosTemporariosNaUnidadeDataSolturaVenceHoje(long unidadeId) {
		String jpql = "select count(distinct r.id) from Recuperando r, Processo p, ProcessoRegimeCondicao prc, "
				+ " Passagem pa, PassagemLotacao pl, Estrutura e "
				+ " where prc.processo.id = p.id "
				+ " and p.recuperando.id = pa.recuperando.id "
				+ " and p.recuperando.id = r.id "
				+ " and r.status = 1 "
				+ " and p.modalidade = :modalidade "
				+ " and pa.id = pl.passagem.id "
				+ " and pa.dataSaida is null "
				+ " and pl.dataSaida is null "
				+ " and pl.estrutura.id = e.id "
				+ " and e.unidadePrisional.id = :unidadeId "
				+ " and trunc(prc.dataSoltura) = trunc(:dataHoje)";
		Query query = em.createQuery(jpql);
		query.setParameter("dataHoje", DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE));
		query.setParameter("unidadeId", unidadeId);
		query.setParameter("modalidade", Modalidade.TEMPORARIA);
		
		try {
			return (Long) query.getSingleResult();
		} catch (NoResultException e) {
			return Long.valueOf(0);
		}
	}
	
	public Long recuperandosTemporariosNoSistemaDataSolturaAVencer() {
		String jpql = "select count(distinct r.id) from Recuperando r, Processo p, ProcessoRegimeCondicao prc "
				+ " where prc.processo.id = p.id "
				+ " and p.recuperando.id = r.id "
				+ " and r.status = 1 "
				+ " and p.modalidade = :modalidade "
				+ " and trunc(prc.dataSoltura) > trunc(:dataHoje)";
		Query query = em.createQuery(jpql);
		query.setParameter("dataHoje", DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE));
		query.setParameter("modalidade", Modalidade.TEMPORARIA);
		
		try {
			return (Long) query.getSingleResult();
		} catch (NoResultException e) {
			return Long.valueOf(0);
		}
	}
	
	public Long recuperandosTemporariosNoSistemaDataSolturaVencida() {
		String jpql = "select count(distinct r.id) from Recuperando r, Processo p, ProcessoRegimeCondicao prc "
				+ " where prc.processo.id = p.id "
				+ " and p.recuperando.id = r.id "
				+ " and r.status = 1 "
				+ " and p.modalidade = :modalidade "
				+ " and prc.regime.id <> 4 "
				+ " and trunc(prc.dataSoltura) < trunc(:dataHoje)";
		Query query = em.createQuery(jpql);
		query.setParameter("dataHoje", DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE));
		query.setParameter("modalidade", Modalidade.TEMPORARIA);
		
		try {
			return (Long) query.getSingleResult();
		} catch (NoResultException e) {
			return Long.valueOf(0);
		}
	}
	
	public Long recuperandosTemporariosNoSistemaDataSolturaVenceHoje() {
		String jpql = "select count(distinct r.id) from Recuperando r, Processo p, ProcessoRegimeCondicao prc "
					+ " where prc.processo.id = p.id "
					+ " and p.recuperando.id = r.id "
					+ " and r.status = 1 "
					+ " and p.modalidade = :modalidade "
					+ " and trunc(prc.dataSoltura) = trunc(:dataHoje)";
		Query query = em.createQuery(jpql);
		query.setParameter("dataHoje", DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE));
		query.setParameter("modalidade", Modalidade.TEMPORARIA);
		
		try {
			return (Long) query.getSingleResult();
		} catch (NoResultException e) {
			return Long.valueOf(0);
		}
	}
}
