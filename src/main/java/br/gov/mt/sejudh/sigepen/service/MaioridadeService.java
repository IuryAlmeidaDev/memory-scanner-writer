package br.gov.mt.sejudh.sigepen.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import javax.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.gov.mt.sejudh.sigepen.worker.VisitanteUpdateWorker;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class MaioridadeService {

    private static final Logger log = LogManager.getLogger(MaioridadeService.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    private VisitanteUpdateWorker worker;

    private boolean processando = false;

    @Schedule(hour = "0", minute = "0", second = "0", persistent = false)
    public void atualizarVisitantesMaioridade() {
        if (processando) {
            log.info("Rotina ja em execucao - pulando lote");
            return;
        }

        try {
            processando = true;
            log.info("Iniciando processamento de lote - Maioridade");

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -18);
            Date dataLimite = cal.getTime();

            List<Long> loteIds = em.createQuery(
            	    "SELECT v.id FROM Visitante v JOIN v.pessoa p " +
            	    "WHERE p.dataNascimento <= :dataLimite " +
            	    "AND v.dataHorarioFimResponsavel IS NULL " +
            	    "AND (v.responsavel IS NOT NULL OR v.emancipado IS NOT NULL)", Long.class)
            	    .setParameter("dataLimite", dataLimite)
            	    .setMaxResults(100)
            	    .getResultList();

            if (loteIds.isEmpty()) {
                log.info("Nenhum visitante para processar");
                return;
            }

            for (Long id : loteIds) {
                worker.processarIndividual(id);
            }

            log.info("Lote finalizado | tamanho={}", loteIds.size());

        } catch (Exception e) {
            log.error("Falha na busca do lote de maioridade", e);
        } finally {
            processando = false;
        }
    }
}
