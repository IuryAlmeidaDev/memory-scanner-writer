package br.gov.mt.sejudh.sigepen.dao;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.envers.AuditReader;

import br.gov.mt.sejudh.sigepen.model.AutoriaRegistro;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@RequestScoped
public class AuditoriaDAO {
	
	@Inject
	private AuditReader auditReader;
	
	@Inject
	private EntityManager entityManager;

	public AuditoriaDAO() {
	}
	
	public AutoriaRegistro autorRegistro(Object objeto, Number id){
		
		if(auditReader.isEntityClassAudited(objeto.getClass())){
			
			AutoriaRegistro autor = new AutoriaRegistro();
		
		try {
		List<Number> revisoes = auditReader.getRevisions(objeto.getClass(), id);
		long revisao = (long) revisoes.get(0);
			String jpql="select case when s.id is null then 'Administrador' else s.nome end, r.data from " + Sigepen.SEJUDH_AUDIT_SCHEMA +  ".revisao r "
					+ "left outer join " + Sigepen.SEJUDH_SCHEMA +  ".usuario u on r.usuario_id = u.id "
					+ "left outer join " + Sigepen.SEJUDH_SCHEMA +  ".servidor s on u.servidor_id = s.id "
					+ "where r.id = :revisao";
			Query query = entityManager.createNativeQuery(jpql);
			query.setParameter("revisao", revisao);			
			Object[] o = (Object[])  query.getSingleResult();	

			if (o != null) {
				autor.setUsuario((String) o[0]);
				autor.setData((Date) o[1]);
			}
		
		return autor;
		} catch (Exception e) {
			return null;
		}	
		
		}
			return null;
	}
	
	public AutoriaRegistro autorUltimaAlteracao(Object objeto, Number id) {
        
        if(auditReader.isEntityClassAudited(objeto.getClass())){
        	
        	AutoriaRegistro autor = new AutoriaRegistro();

		try {
			List<Number> revisoes = auditReader.getRevisions(objeto.getClass(),	id);
			
			long revisao = (long) revisoes.get(revisoes.size()-1);
			String jpql="select case when s.id is null then 'Administrador' else s.nome end, r.data from " + Sigepen.SEJUDH_AUDIT_SCHEMA + ".revisao r "
					+ "left outer join " + Sigepen.SEJUDH_SCHEMA +  ".usuario u on r.usuario_id = u.id "
					+ "left outer join " + Sigepen.SEJUDH_SCHEMA +  ".servidor s on u.servidor_id = s.id "
					+ "where r.id = :revisao";
			Query query = entityManager.createNativeQuery(jpql);
			query.setParameter("revisao", revisao);
			Object[] o = (Object[]) query.getSingleResult();

			if (o != null) {
				autor.setUsuario((String) o[0]);
				autor.setData((Date) o[1]);
			}

			return autor;
		} catch (Exception e) {
			return null;
		}
		
        }
		return null;
	}	
	
}
