package br.gov.mt.sejudh.sigepen.base.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import br.gov.mt.sejudh.sigepen.model.AutoriaRegistro;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroConsulta;

public class BaseDAO<T extends BaseModel<I>, I extends Number> implements Serializable {


	private static final long serialVersionUID = -5231983672787758860L;

	private final Class<T> tClass;

    @Inject
    protected EntityManager em;

    @Inject
    private AuditoriaDAO auditoriaDAO;

    @SuppressWarnings("all")
    public BaseDAO() {
        Class obtainedClass = getClass();
        Type genericSuperclass = null;
        while (true) {
            genericSuperclass = obtainedClass.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                break;
            }
            obtainedClass = obtainedClass.getSuperclass();
        }
        ParameterizedType genericSuperclass_ = (ParameterizedType) genericSuperclass;
        this.tClass = (Class<T>) genericSuperclass_.getActualTypeArguments()[0];
    }


    public T salvar(T t) {
        if (t.getId() == null) {
            em.persist(t);
            return t;
        }

        return em.merge(t);
    }

    public void initialize(Object proxy) {
        Hibernate.initialize(proxy);
    }

    public void excluir(T t) {
        em.remove(em.merge(t));
    }

    public T buscarPorId(I id) {
        return em.find(this.tClass, id);
    }

    public T buscarPorId(I id, Boolean buscarAuditoria) {
        T t = em.find(this.tClass, id);
        if (buscarAuditoria) {
            t.setAutorRegistro(this.auditoriaDAO.autorRegistro(t, t.getId()));
            t.setAutorUltimaAlteracao(this.auditoriaDAO.autorUltimaAlteracao(t, t.getId()));
        }

        return t;
    }

    @SuppressWarnings("unchecked")
    public List<T> buscaPorFiltro(FiltroConsulta filtro, boolean isRelatorio) {
        Criteria criteria = filtro.criarCriteriaParaBusca(this.tClass, em, isRelatorio);
        if (filtro.isProjetarDados()) {
            criteria.setResultTransformer(new AliasToBeanResultTransformer(this.tClass));
        }

        return criteria.list();
    }

    public List<T> buscaPorCampo(String campo, Object valor) {
        TypedQuery<T> query = em.createQuery(
                "select f from " + this.tClass.getSimpleName() + " f where f." + campo + " = :valor ",
                this.tClass
        );
        query.setParameter("valor", valor);

        return query.getResultList();
    }

    public int quantidadeFiltrados(FiltroConsulta filtro) {
        Criteria criteria = filtro.criarCriteriaParaQuantidade(this.tClass, em);
        criteria.setProjection(Projections.countDistinct("id"));
        return ((Number) criteria.uniqueResult()).intValue();
    }

    public Long totalRegistros() {
        return em.createQuery("SELECT COUNT(t) FROM " + tClass.getName() + " t ", Long.class).getSingleResult();
    }

    public List<T> listarTodos() {
        return em.createQuery("FROM " + this.tClass.getName(), this.tClass).getResultList();
    }

    public T buscarAutorRegistro(T t){
        t.setAutorRegistro(this.auditoriaDAO.autorRegistro(t, t.getId()));
        t.setAutorUltimaAlteracao(this.auditoriaDAO.autorUltimaAlteracao(t, t.getId()));
        return t;
    }
}
