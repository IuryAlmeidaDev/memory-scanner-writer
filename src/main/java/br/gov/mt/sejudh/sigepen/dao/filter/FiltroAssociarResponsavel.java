package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;

public class FiltroAssociarResponsavel extends FiltroConsulta{
	
	private static final long serialVersionUID = 1L;
	private String nome;
	private String cpf;
	private Date dataNascimento;
	
	private String matricula;
	
	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	@Override
	public void configurarFiltros() {
		
		if (StringUtils.isNotBlank(this.getNome())) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nome", this.getNome(), MatchMode.ANYWHERE));
		}
		if (this.getDataNascimento() != null) {
			getCriteria().add(Restrictions.eq("dataNascimento", this.getDataNascimento()));
		}
		if (StringUtils.isNotBlank(this.getCpf())) {

			getCriteria().add(Restrictions.like("cpf", this.getCpf(), MatchMode.ANYWHERE));
		}
		
	}
	
	

}
