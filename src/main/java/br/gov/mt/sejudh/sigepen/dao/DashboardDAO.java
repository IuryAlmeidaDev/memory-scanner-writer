package br.gov.mt.sejudh.sigepen.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.time.DateUtils;

import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.Modalidade;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatusAlternativo;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
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
	
	@SuppressWarnings("unchecked")
	public List<Long> recuperandosTemporariosNaUnidadeDataSolturaAVencer(long unidadeId) {
		String jpql = "select r.id from Recuperando r, Processo p, ProcessoRegimeCondicao prc, "
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
				+ " group by r.id "
				+ " having max(prc.dataSoltura) > trunc(:dataHoje)";
		Query query = em.createQuery(jpql);
		query.setParameter("dataHoje", DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE));
		query.setParameter("unidadeId", unidadeId);
		query.setParameter("modalidade", Modalidade.TEMPORARIA);
		
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> recuperandosTemporariosNaUnidadeDataSolturaVencida(long unidadeId) {
		String jpql = "select r.id from Recuperando r, Processo p, ProcessoRegimeCondicao prc, "
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
				+ " group by r.id "
				+ " having max(prc.dataSoltura) < trunc(:dataHoje)";
		Query query = em.createQuery(jpql);
		query.setParameter("dataHoje", DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE));
		query.setParameter("unidadeId", unidadeId);
		query.setParameter("modalidade", Modalidade.TEMPORARIA);

		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> recuperandosTemporariosNaUnidadeDataSolturaVenceHoje(long unidadeId) {
		String jpql = "select r.id from Recuperando r, Processo p, ProcessoRegimeCondicao prc, "
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
				+ " group by r.id "
				+ "	having max(prc.dataSoltura) = trunc(:dataHoje) ";
		Query query = em.createQuery(jpql);
		query.setParameter("dataHoje", DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE));
		query.setParameter("unidadeId", unidadeId);
		query.setParameter("modalidade", Modalidade.TEMPORARIA);

		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> recuperandosTemporariosNoSistemaDataSolturaAVencer() {
		String jpql = "select r.id from Recuperando r, Processo p, ProcessoRegimeCondicao prc "
				+ " where prc.processo.id = p.id "
				+ " and p.recuperando.id = r.id "
				+ " and r.status = 1 "
				+ " and p.modalidade = :modalidade "
				+ " group by r.id "
				+ " having max(prc.dataSoltura) > trunc(:dataHoje)";
		Query query = em.createQuery(jpql);
		query.setParameter("dataHoje", DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE));
		query.setParameter("modalidade", Modalidade.TEMPORARIA);
		
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> recuperandosTemporariosNoSistemaDataSolturaVencida() {
		String jpql = "select r.id from Recuperando r, Processo p, ProcessoRegimeCondicao prc "
				+ " where prc.processo.id = p.id "
				+ " and p.recuperando.id = r.id "
				+ " and r.status = 1 "
				+ " and p.modalidade = :modalidade "
				+ " and prc.regime.id <> 4 "
				+ " group by r.id "
				+ " having max(prc.dataSoltura) < trunc(:dataHoje)";
		Query query = em.createQuery(jpql);
		query.setParameter("dataHoje", DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE));
		query.setParameter("modalidade", Modalidade.TEMPORARIA);

		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> recuperandosTemporariosNoSistemaDataSolturaVenceHoje() {
		String jpql = "select r.id from Recuperando r, Processo p, ProcessoRegimeCondicao prc "
					+ " where prc.processo.id = p.id "
					+ " and p.recuperando.id = r.id "
					+ " and r.status = 1 "
					+ " and p.modalidade = :modalidade "
					+ " group by r.id "
					+ " having max(prc.dataSoltura) = trunc(:dataHoje)";
		Query query = em.createQuery(jpql);
		query.setParameter("dataHoje", DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE));
		query.setParameter("modalidade", Modalidade.TEMPORARIA);

		return query.getResultList();
	}

	public Long countRecuperandosProvisorios90a180Dias(Long unidadeId) {
	    String jpql =
	        " select count(distinct r.id) " +
	        " from Recuperando r, Processo p, Passagem pa, PassagemLotacao pl, Estrutura e " +
	        " where p.recuperando.id = r.id " +
	        " and pa.recuperando.id = r.id " +
	        " and pa.dataSaida is null " +
	        " and r.status = :status " +
	        " and r.condicaoAtual = :condicao " +
	        " and p.modalidade = :modalidade " +
	        " and pa.id = pl.passagem.id " +
	        " and pl.dataSaida is null " +
	        " and pl.estrutura.id = e.id " +
	        " and e.unidadePrisional.id = :unidadeId " +
	        " group by r.id " +
	        " having (trunc(sysdate) - trunc(max(pa.dataEntrada))) between 90 and 180 ";

	    Query query = em.createQuery(jpql);
	    query.setParameter("status", StatusRecuperando.ATIVO);
	    query.setParameter("condicao", Condicao.PROVISORIO);
	    query.setParameter("modalidade", Modalidade.PREVENTIVA);
	    query.setParameter("unidadeId", unidadeId);

	    try {
	        return Long.valueOf(query.getResultList().size());
	    } catch (Exception e) {
	        return 0L;
	    }
	}

	public Long countRecuperandosProvisoriosMais180Dias(Long unidadeId) {
	    String jpql =
	        " select count(distinct r.id) " +
	        " from Recuperando r, Processo p, Passagem pa, PassagemLotacao pl, Estrutura e " +
	        " where p.recuperando.id = r.id " +
	        " and pa.recuperando.id = r.id " +
	        " and pa.dataSaida is null " +
	        " and r.status = :status " +
	        " and r.condicaoAtual = :condicao " +
	        " and p.modalidade = :modalidade " +
	        " and pa.id = pl.passagem.id " +
	        " and pl.dataSaida is null " +
	        " and pl.estrutura.id = e.id " +
	        " and e.unidadePrisional.id = :unidadeId " +
	        " group by r.id " +
	        " having (trunc(sysdate) - trunc(max(pa.dataEntrada))) > 180 ";

	    Query query = em.createQuery(jpql);
	    query.setParameter("status", StatusRecuperando.ATIVO);
	    query.setParameter("condicao", Condicao.PROVISORIO);
	    query.setParameter("modalidade", Modalidade.PREVENTIVA);
	    query.setParameter("unidadeId", unidadeId);

	    try {
	        return Long.valueOf(query.getResultList().size());
	    } catch (Exception e) {
	        return 0L;
	    }
	}
	
	public Long solicitacaoVisitaPendente(long unidadeId) {
		Date dataLimite = calculaDataLimiteMaiorDeIdade();
		
		String jpql = "SELECT COUNT(DISTINCT sl.id) FROM SolicitacaoVisita sl "
				+ "JOIN sl.pessoaSolicitacao psv "
				+ "WHERE sl.status = :stat AND sl.unidadePrisional.id = :unidadeId "
				+ "AND psv.dataNascimento <= :dataLimite";

		Query query = em.createQuery(jpql);
		query.setParameter("stat", SgpStatusAlternativo.P);
		query.setParameter("unidadeId", unidadeId);
		query.setParameter("dataLimite", dataLimite);

		try {
			return (Long) query.getSingleResult();
		} catch (NoResultException e) {
			return Long.valueOf(0);
		}
	}

	private Date calculaDataLimiteMaiorDeIdade() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -18);
		
		return calendar.getTime();
	}

	private Long totalCredenciamentoPendente(long unidadeId, FaseCredenciamento fase, StatusCredenciamento situacao) {
		String jpql = "select count(distinct v.id) from Visitante v join v.credenciamentos vr"
				+ " where vr.faseCredenciamento = :fase and vr.statusCredenciamento = :situacao and vr.unidadePrisional.id = :unidadeId";
		Query query = em.createQuery(jpql);
		query.setParameter("fase", fase.getId());
		query.setParameter("situacao", situacao.getId());
		query.setParameter("unidadeId", unidadeId);
		try {
			Long x = (Long) query.getSingleResult(); 
			return x;
		} catch (NoResultException e) {
			return Long.valueOf(0);
		}
	}

	public Long totalCredenciamentoRecebimentoPendente(long unidadeId) {
		return this.totalCredenciamentoPendente(unidadeId, FaseCredenciamento.RECEBIMENTO, StatusCredenciamento.PENDENTE);
	}

	public Long totalCredenciamentoChecagemPendente(long unidadeId) {
		return this.totalCredenciamentoPendente(unidadeId, FaseCredenciamento.CHECAGEM, StatusCredenciamento.PENDENTE);
	}

	public Long totalCredenciamentoConferenciaPendente(long unidadeId) {
		return this.totalCredenciamentoPendente(unidadeId, FaseCredenciamento.CONFERENCIA, StatusCredenciamento.PENDENTE);
	}

	public Long totalCredenciamentoAnalisePendente(long unidadeId) {
		return this.totalCredenciamentoPendente(unidadeId, FaseCredenciamento.ANALISE, StatusCredenciamento.PENDENTE);
	}

	public Long totalCredenciamentoAprovacaoPendente(long unidadeId) {
		return this.totalCredenciamentoPendente(unidadeId, FaseCredenciamento.APROVACAO, StatusCredenciamento.PENDENTE);
	}
}
