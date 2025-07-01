package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.GerenciarAssinatura;
import br.gov.mt.sejudh.sigepen.model.GerenciarAssinaturaServidor;
import br.gov.mt.sejudh.sigepen.model.TipoOcorrencia;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import org.hibernate.Hibernate;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

@RequestScoped
public class TipoOcorrenciaDAO extends BaseDAO<TipoOcorrencia, Long> {


	private static final long serialVersionUID = 1635530084073694819L;

//	@Override
//	public List<TipoOcorrencia> listarTodos(){
//
//
//		TypedQuery<TipoOcorrencia> query = em.createQuery("FROM " + TipoOcorrencia.class.getName(), TipoOcorrencia.class);
//		List<TipoOcorrencia> tipoOcorrencias = query.getResultList();
//
//		for (TipoOcorrencia tipoOcorrencia : tipoOcorrencias) {
//			Hibernate.initialize(tipoOcorrencia.getOcorrencias());
//		}
//
//		return tipoOcorrencias;
//	}



}