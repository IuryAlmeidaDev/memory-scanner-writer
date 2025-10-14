package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.model.AutoriaRegistro;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.hibernate.envers.AuditReader;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestScoped
public class AuditoriaDAO {
	
	@Inject
	private AuditReader auditReader;
	
	@Inject
	private EntityManager entityManager;

	
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

    /**
     * Batch: first revision author for Ocorrencia ids.
     */
    public Map<Long, AutoriaRegistro> autoresRegistroOcorrenciaPrimeiraRevisao(List<Long> ids) {
        Map<Long, AutoriaRegistro> map = new HashMap<>();
        if (ids == null || ids.isEmpty()) return map;
        StringBuilder inClause = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) inClause.append(",");
            inClause.append("?").append(i + 1);
        }
        String inList = inClause.toString();
        String sql =
                "select m.id_ocorrencia, case when s.id is null then 'Administrador' else s.nome end as usuario, r.data " +
                        "from " + Sigepen.SEJUDH_AUDIT_SCHEMA + ".aud_sigepen_ocorrencia a " +
                        "join (select id_ocorrencia, min(rev) as rev from " + Sigepen.SEJUDH_AUDIT_SCHEMA + ".aud_sigepen_ocorrencia where id_ocorrencia in (" + inList + ") group by id_ocorrencia) m " +
                        "on a.id_ocorrencia = m.id_ocorrencia and a.rev = m.rev " +
                        "left join " + Sigepen.SEJUDH_AUDIT_SCHEMA + ".revisao r on r.id = a.rev " +
                        "left join " + Sigepen.SEJUDH_SCHEMA + ".usuario u on r.usuario_id = u.id " +
                        "left join " + Sigepen.SEJUDH_SCHEMA + ".servidor s on u.servidor_id = s.id";
        Query q = entityManager.createNativeQuery(sql);
        for (int i = 0; i < ids.size(); i++) {
            q.setParameter(i + 1, ids.get(i));
        }
        List<Object[]> rows = q.getResultList();
        for (Object[] row : rows) {
            Long id = ((Number) row[0]).longValue();
            AutoriaRegistro ar = new AutoriaRegistro();
            ar.setUsuario((String) row[1]);
            ar.setData((Date) row[2]);
            map.put(id, ar);
        }
        return map;
    }

    /**
     * Batch: last revision author for Ocorrencia ids.
     */
    public Map<Long, AutoriaRegistro> autoresRegistroOcorrenciaUltimaRevisao(List<Long> ids) {
        Map<Long, AutoriaRegistro> map = new HashMap<>();
        if (ids == null || ids.isEmpty()) return map;
        StringBuilder inClause = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) inClause.append(",");
            inClause.append("?").append(i + 1);
        }
        String inList = inClause.toString();
        String sql =
                "select m.id_ocorrencia, case when s.id is null then 'Administrador' else s.nome end as usuario, r.data " +
                        "from " + Sigepen.SEJUDH_AUDIT_SCHEMA + ".aud_sigepen_ocorrencia a " +
                        "join (select id_ocorrencia, max(rev) as rev from " + Sigepen.SEJUDH_AUDIT_SCHEMA + ".aud_sigepen_ocorrencia where id_ocorrencia in (" + inList + ") group by id_ocorrencia) m " +
                        "on a.id_ocorrencia = m.id_ocorrencia and a.rev = m.rev " +
                        "left join " + Sigepen.SEJUDH_AUDIT_SCHEMA + ".revisao r on r.id = a.rev " +
                        "left join " + Sigepen.SEJUDH_SCHEMA + ".usuario u on r.usuario_id = u.id " +
                        "left join " + Sigepen.SEJUDH_SCHEMA + ".servidor s on u.servidor_id = s.id";
        Query q = entityManager.createNativeQuery(sql);
        for (int i = 0; i < ids.size(); i++) {
            q.setParameter(i + 1, ids.get(i));
        }
        List<Object[]> rows = q.getResultList();
        for (Object[] row : rows) {
            Long id = ((Number) row[0]).longValue();
            AutoriaRegistro ar = new AutoriaRegistro();
            ar.setUsuario((String) row[1]);
            ar.setData((Date) row[2]);
            map.put(id, ar);
        }
        return map;
    }
	
}
