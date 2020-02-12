package br.gov.mt.sejudh.sigepen.dao;

import java.util.Date;

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
	private EntityManager em;
	
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
}
