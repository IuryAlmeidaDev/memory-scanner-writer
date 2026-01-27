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

import br.gov.mt.sejudh.sigepen.worker.VisitanteUpdateWorker;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class MaioridadeService {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private VisitanteUpdateWorker worker;

    private boolean processando = false;

    @Schedule(hour = "0", minute = "0", second = "0", persistent = false)
    public void atualizarVisitantesMaioridade() {
        if (processando) {
            System.out.println(">>> [LOG SIGEPEN] Rotina já em execução. Pulando...");
            return;
        }

        try {
            processando = true;
            System.out.println(">>> [LOG SIGEPEN] Iniciando processamento por lotes (Maioridade)...");
            
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -18);
            Date dataLimite = cal.getTime();

            List<Long> loteIds = em.createQuery(
                "SELECT v.id FROM Visitante v JOIN v.pessoa p " +
                "WHERE p.dataNascimento <= :dataLimite " +
                "AND (v.responsavel IS NOT NULL OR v.emancipado IS NOT NULL)", Long.class)
                .setParameter("dataLimite", dataLimite)
                .setMaxResults(100) 
                .getResultList();

            if (loteIds.isEmpty()) {
                System.out.println(">>> [LOG SIGEPEN] Nada para processar.");
                return;
            }

            for (Long id : loteIds) {
                worker.processarIndividual(id);
            }
            
            System.out.println(">>> [LOG SIGEPEN] Lote de " + loteIds.size() + " finalizado.");

        } catch (Exception e) {
            System.err.println(">>> [ERRO] Falha na busca do lote: " + e.getMessage());
        } finally {
            processando = false;
        }
    }
}