package br.gov.mt.sejudh.sigepen.dao;

import javax.enterprise.context.RequestScoped;
import javax.persistence.Query;

import org.hibernate.Hibernate;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;

@RequestScoped
public class TipoVisitaDAO extends BaseDAO<TipoVisita, Long> {

	private static final long serialVersionUID = 2028075622138461806L;

	@Override
    public TipoVisita buscarPorId(Long id) {
        return this.buscarPorId(id, false);
    }

    @Override
    public TipoVisita buscarPorId(Long id, Boolean buscarAuditoria) {
        TipoVisita tipoVisita = super.buscarPorId(id, buscarAuditoria);
        if (tipoVisita == null) return null;

        Hibernate.initialize(tipoVisita.getTipoVisitaDocumento());
        Hibernate.initialize(tipoVisita.getTipoVisitaCertidao());
        return tipoVisita;
    }

	public boolean tipoVisitaEstaVinculada(TipoVisita tipoVisita) {
		Query query = em.createQuery("select count(c) from Credenciamento c, TipoVisita t "
				+ "where c.tipoVisita.id = t.id "
				+ "and c.id = :id ");
		query.setParameter("id", tipoVisita.getId());
		long resultado = (Long) query.getSingleResult();
		return (resultado > 0);
	}
}
