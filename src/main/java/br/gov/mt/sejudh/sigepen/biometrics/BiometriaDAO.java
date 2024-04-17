package br.gov.mt.sejudh.sigepen.biometrics;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.gov.mt.sejudh.sigepen.model.Biometria;
import br.gov.mt.sejudh.sigepen.model.BiometriaTemporaria;
import br.gov.mt.sejudh.sigepen.model.dto.PessoaBiometriaWSDTO;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@RequestScoped
public class BiometriaDAO {

	@Inject
	private EntityManager em;

	public BiometriaTemporaria buscarBiometriaTemporaria(long id) {
		BiometriaTemporaria bioTemp = em.find(BiometriaTemporaria.class, id);

		return bioTemp;
	}
	
	public Biometria buscarBiometriaPorId(long id) {
		Biometria bio = em.find(Biometria.class, id);

		return bio;
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
	
	public PessoaBiometriaWSDTO buscarIdsVinculadosAPessoa(Long idPessoa) {
		String jpql = "select new br.gov.mt.sejudh.sigepen.model.dto.PessoaBiometriaWSDTO(p.nome, p.id, r.id, v.id) from "
				+ "Pessoa p left join p.recuperando r left join p.visitante v "
				+ "where p.id = :id ";
		
		TypedQuery<PessoaBiometriaWSDTO> query = em.createQuery(jpql, PessoaBiometriaWSDTO.class);
		query.setParameter("id", idPessoa);
		
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}	
}
