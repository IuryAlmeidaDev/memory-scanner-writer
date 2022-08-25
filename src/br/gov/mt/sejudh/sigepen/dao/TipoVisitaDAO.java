package br.gov.mt.sejudh.sigepen.dao;

import javax.enterprise.context.RequestScoped;

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
}
