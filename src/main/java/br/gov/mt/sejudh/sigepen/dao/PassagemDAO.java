package br.gov.mt.sejudh.sigepen.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Hibernate;

import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.Processo;
import br.gov.mt.sejudh.sigepen.model.Recuperando;

@RequestScoped
public class PassagemDAO {

	@Inject
	private EstruturaDAO estruturaDAO;
	
	@Inject
	private EntityManager em;
	
	public EntityManager getEntityManager() {
	    return em;
	}
	
	public Passagem buscaPassagemPorId(Passagem passagem) {
		try{
			String jpql="select p from Passagem p where p.id = :id";
			TypedQuery<Passagem> query = em.createQuery(jpql, Passagem.class);
			query.setParameter("id", passagem.getId());			
			Passagem p = query.getSingleResult();
			Hibernate.initialize(p.getLotacao());
			Hibernate.initialize(p.getFoto());
			Hibernate.initialize(p.getRecuperando());
			Hibernate.initialize(p.getRecuperando().getPessoa());
			Hibernate.initialize(p.getEstabelecimentoDestino());
			
			p.getLotacao().forEach(lotacao -> estruturaDAO.buscarEstruturasCompleta(lotacao.getEstrutura()));
			
			return p;
		} catch(NoResultException e){
			return null;
		}
	}
	
	public Recuperando buscarRecuperandoPorIdNovaPassagem(long idRecuperando) {
		try {
			String jpql="select r from Recuperando r where r.id = :id";
			TypedQuery<Recuperando> query = em.createQuery(jpql, Recuperando.class);
			query.setParameter("id", idRecuperando);			
			Recuperando r = query.getSingleResult();
			Hibernate.initialize(r.getFotos());
			Hibernate.initialize(r.getProcessos());
			Hibernate.initialize(r.getPassagens());
			Hibernate.initialize(r.getPessoa());
			Hibernate.initialize(r.getPessoa().getBiometria());
			
			for(Processo p : r.getProcessos()){
				Hibernate.initialize(p.getCrimes());
			}
			return r;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public Recuperando buscarRecuperandoPorIdDesligamento(long idRecuperando) {
		try {
			String jpql="select r from Recuperando r where r.id = :id";
			TypedQuery<Recuperando> query = em.createQuery(jpql, Recuperando.class);
			query.setParameter("id", idRecuperando);			
			Recuperando r = query.getSingleResult();		
			Hibernate.initialize(r.getPessoa());
			Hibernate.initialize(r.getPessoa().getBiometria());
			Hibernate.initialize(r.getPassagens());
			Hibernate.initialize(r.getFotoFrenteAtual());
			
			for (Passagem p : r.getPassagens()) {
				Hibernate.initialize(p.getLotacao());
				Hibernate.initialize(p.getLotacao());
				
				for (PassagemLotacao l : p.getLotacao()) {
					Hibernate.initialize(l.getEstrutura());
					Hibernate.initialize(l.getEstrutura().getUnidadePrisional());
				}
			}
			
			return r;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public boolean possuiPassagem(Recuperando rec){
		Query query = em.createQuery("select count(p) from Passagem p where p.recuperando.id = :idRec");
		query.setParameter("idRec", rec.getId());
		long count = (Long)query.getSingleResult();
		return count>0;		
	}
	
	public boolean possuiPassagemAberta(Recuperando rec){
		Query query = em.createQuery("select count(p) from Passagem p where p.recuperando.id = :idRec and p.dataSaida is null");
		query.setParameter("idRec", rec.getId());
		long count = (Long)query.getSingleResult();
		return count>0;		
	}
	
	public Date dataUltimaSaida(Recuperando rec){
		Query query = em.createQuery("select max(p.dataSaida) from Passagem p where p.recuperando.id = :idRec");
		query.setParameter("idRec", rec.getId());
		Date dataSaida = (Date)query.getSingleResult();
		return dataSaida;		
	}

	public List<Passagem> buscarPassagensDoRecuperando(Long id) {
		try{
			String jpql="select p from Passagem p, PassagemLotacao l where p.recuperando.id = :id"
					+ " and l.passagem.id = p.id";
			TypedQuery<Passagem> query = em.createQuery(jpql, Passagem.class);
			query.setParameter("id", id);
			List<Passagem> p = query.getResultList();

			return p;
		} catch(NoResultException e){
			return null;
		}
	}

	public Optional<Passagem> buscaPassagemPorIdPessoa(Long id) {
		try{
			String jpql = "select p from Passagem p WHERE p.recuperando.pessoa.id = :id Order By p desc";
			TypedQuery<Passagem> query = em.createQuery(jpql, Passagem.class).setParameter("id", id).setMaxResults(1);

			return Optional.of(query.getSingleResult());
		}catch(NoResultException ex) {
			return Optional.empty();
		}
	}

	public boolean buscarPrimeiraPassagem(long idRecuperando) {
		try{
			String jpql="select p from Passagem p where p.recuperando.id = :id";
			TypedQuery<Passagem> query = em.createQuery(jpql, Passagem.class);
			query.setParameter("id", idRecuperando);
			List<Passagem> passagens = query.getResultList();
			for(Passagem p : passagens) {
				return false;
			}
			return true;
		} catch(NoResultException e){
			// intentional: getResultList nao dispara NoResultException — catch defensivo
			return false;
		}
	}

	public Passagem buscaPassagemPorId(long idPassagem) {
		try{
			String jpql = "SELECT p FROM Passagem p " +
					"LEFT JOIN FETCH p.lotacao " +
					"LEFT JOIN FETCH p.foto " +
					"LEFT JOIN FETCH p.recuperando rec " +
					"LEFT JOIN FETCH rec.pessoa " +
					"LEFT JOIN FETCH p.estabelecimentoDestino " +
					"WHERE p.id = :id";

			TypedQuery<Passagem> query = em.createQuery(jpql, Passagem.class);
			query.setParameter("id", idPassagem);

			Passagem p = query.getSingleResult();
			estruturaDAO.buscarEstruturasCompleta(p.getLotacaoAtual() != null ? p.getLotacaoAtual().getEstrutura() : p.getUltimaLotacao().getEstrutura());

			return p;
		} catch(NoResultException e){
			return null;
		}
	}

	public Passagem salvar(Passagem passagem) {
		if(passagem.getId() > 0)
			return this.em.merge(passagem);
		else
			this.em.persist(passagem);
		return passagem;
	}
	
	public Optional<Date> buscarDataEntradaPassagemAtivaPorPessoa(Long idPessoa) {
	    try {
	        String jpql =
	            "select p.dataEntrada " +
	            "from Passagem p " +
	            "where p.recuperando.pessoa.id = :idPessoa " +
	            "and p.dataSaida is null " +
	            "order by p.id desc";

	        TypedQuery<Date> query = em.createQuery(jpql, Date.class);
	        query.setParameter("idPessoa", idPessoa);
	        query.setMaxResults(1);

	        return Optional.of(query.getSingleResult());
	    } catch (NoResultException e) {
	        return Optional.empty();
	    }
	}

}
