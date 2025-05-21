package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.model.Ocorrencia;
import br.gov.mt.sejudh.sigepen.model.TipoOcorrencia;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class OcorrenciaDAO extends BaseDAO<Ocorrencia, Long> {


	private static final long serialVersionUID = 1635530084073694819L;


}