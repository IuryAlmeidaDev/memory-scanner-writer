package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroVisitanteImpedimento extends FiltroConsulta {

	private static final long serialVersionUID = 1L;
	private String nome;
	private String cpf;
	private Date dataNascimento;
	private Long matricula;

	@Override
	public void configurarFiltros() {
		getCriteria()
		.createAlias("pessoa", "pessoa", JoinType.INNER_JOIN)
		.createAlias("pessoa.documentos", "doc", JoinType.LEFT_OUTER_JOIN);
		
		getCriteria().add(Restrictions.ne("status", StatusVisitante.I));
		
		if (StringUtils.isNotBlank(this.getNome())) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa.nome", this.getNome(), MatchMode.ANYWHERE));
		}
		if (this.getDataNascimento() != null) {
			getCriteria().add(Restrictions.eq("pessoa.dataNascimento", this.getDataNascimento()));
		}
		if (StringUtils.isNotBlank(this.getCpf())) {

			getCriteria().add(Restrictions.like("doc.numero", this.getCpf(), MatchMode.ANYWHERE));
		}
		if(this.getMatricula() != null && this.getMatricula() > 0) {
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
		
		ProjectionList proj = Projections.projectionList();
        proj.add(Projections.distinct(Projections.projectionList()
                .add(Projections.property("pessoa").as("pessoa"))
        		.add(Projections.property("id").as("id"))
        		));
        
        getCriteria().setProjection(proj);
        

	}

	public FiltroVisitanteImpedimento() {

	}

	public FiltroVisitanteImpedimento(FiltroVisitanteImpedimento filtroVisitanteImpedimento) {
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
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

	public Long getMatricula() {
		return matricula;
	}

	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}


	
	

}
