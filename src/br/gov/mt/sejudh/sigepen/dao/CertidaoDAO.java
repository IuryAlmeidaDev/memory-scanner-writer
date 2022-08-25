package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.model.Certidao;

@RequestScoped
public class CertidaoDAO extends BaseDAO<Certidao, Long> {

	private static final long serialVersionUID = 3499190641290010937L;
	
	public List<Certidao> buscarPorDescricao(String descricao) {
		String jpql = "Select c FROM Certidao c WHERE c.descricao = :descricao";
		TypedQuery<Certidao> query = this.em.createQuery(jpql, Certidao.class);
		query.setParameter("descricao",  descricao.trim());
		
		return query.getResultList();
	}

}
