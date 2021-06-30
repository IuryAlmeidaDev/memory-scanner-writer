package br.gov.mt.sejudh.sigepen.biometrics;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.mt.sejudh.sigepen.model.BiometriaTemporaria;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@RequestScoped
public class BiometriaDAO {

	@Inject
	private EntityManager em;

	public BiometriaTemporaria buscarBiometriaTemporaria(long id) {
		BiometriaTemporaria bioTemp = em.find(BiometriaTemporaria.class, id);

		return bioTemp;
	}
	
	public boolean removerBiometriaTemporaria(long id) {
		BiometriaTemporaria bioTemp = em.find(BiometriaTemporaria.class, id);
		boolean foiRemovido = false;
		
		if (bioTemp != null) {
			em.remove(em.merge(bioTemp));
			foiRemovido = true;
		}
		
		//bioTemp = em.find(BiometriaTemporaria.class, id);			
		
		return foiRemovido;
	}
	
	public BiometriaTemporaria salvarBiometriaTemporaria(BiometriaTemporaria bioTemp) {
		if (bioTemp.getId() > 0) {
			bioTemp = em.merge(bioTemp);
		} else {
			em.persist(bioTemp);
		}

		return bioTemp;
	}

	public String buscarVersaoAtualAplicativo() {
		String jpql = "select p.valor from " + Sigepen.SEJUDH_SCHEMA + ".parametro p where p.nome = :param";
		Query query = em.createNativeQuery(jpql);
		query.setParameter("param", "VERSAO_DESKTOP");

		return query.getSingleResult().toString();
	}
}
