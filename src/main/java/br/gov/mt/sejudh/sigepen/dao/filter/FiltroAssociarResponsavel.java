package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.Date;

import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import org.hibernate.sql.JoinType;

public class FiltroAssociarResponsavel extends FiltroConsulta{
	
	private static final long serialVersionUID = 1L;
	private String nome;
	private String cpf;
	private Date dataNascimento;
	
	private Long matricula;
	
	public Long getMatricula() {
		return matricula;
	}

	public void setMatricula(Long matricula) {
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

		getCriteria()
				.createAlias("credenciamentos", "cre", JoinType.INNER_JOIN)
				.createAlias("cre.parente", "p", JoinType.INNER_JOIN);

		getCriteria()
				.add(Restrictions.eqProperty("id", "cre.visitante.id")) // id_visitante da tabela visitante igual ao idVisitante da tabela credenciamento
				.add(Restrictions.eqProperty("p.recuperando.id", "cre.recuperando.id")) // id_recuperando da tabela parente igual ao idRecuperando da tabela credenciamento
				.add(Restrictions.or(
						Restrictions.isNull("emancipado"),
						Restrictions.eq("emancipado", SimNao.S)
				))
				.add(Restrictions.eq("cre.recuperando.id", matricula));
		
	}
	
	

}
