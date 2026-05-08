package br.gov.mt.sejudh.sigepen.worker;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

@Stateless
public class VisitanteUpdateWorker {

    private static final Logger log = LogManager.getLogger(VisitanteUpdateWorker.class);

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
                log.info("Visitante atingiu maioridade | id={} | nome={} | dataFim={}",
                        idVisitante, nome, v.getDataHorarioFimResponsavel());
            }
        } catch (Exception e) {
            LoggerUtil.logErro(Acao.ALTERAR, idVisitante, e, log);
        }
    }
}