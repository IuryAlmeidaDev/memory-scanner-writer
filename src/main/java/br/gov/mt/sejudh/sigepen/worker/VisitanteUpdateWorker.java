package br.gov.mt.sejudh.sigepen.worker;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import br.gov.mt.sejudh.sigepen.model.Visitante;

@Stateless
public class VisitanteUpdateWorker {

    @PersistenceContext
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void processarIndividual(Long idVisitante) {
        try {
            Visitante v = em.find(Visitante.class, idVisitante);
            
            if (v != null && v.getPessoa() != null) {
                String nome = v.getPessoa().getNome();
                
                v.setResponsavel(null);
                v.setEmancipado(null);
                v.setDataHorarioFimResponsavel(new java.util.Date());
                
                if (v.getVisitantesReponsaveis() != null) {
	                v.getVisitantesReponsaveis().clear();
	            }
                
                em.merge(v);
                em.flush(); 
                System.out.println(">>> [SUCESSO] ID " + idVisitante + " (" + nome + ") agora é MAIOR.");
                System.out.println(">>> [DATA] : " + v.getDataHorarioFimResponsavel());
            }
        } catch (Exception e) {
            System.err.println(">>> [ERRO CRÍTICO] Falha ao processar ID " + idVisitante + ": " + e.getMessage());
        }
    }
}