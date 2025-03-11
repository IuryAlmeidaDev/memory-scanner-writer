package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroUsuario extends FiltroConsulta {
	
	private static final long serialVersionUID = 1L;
	
	private String nome;
	private String cpf;
	private String login;
	private String email;
	private Boolean resetSenha = false;

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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Boolean getResetSenha() {
		return resetSenha;
	}

	public void setResetSenha(Boolean resetSenha) {
		this.resetSenha = resetSenha;
	}

	@Override
	public void configurarFiltros() {
		getCriteria().createAlias("servidor", "servidor", JoinType.LEFT_OUTER_JOIN);
		
		Projection proj = Projections.projectionList()
				.add(Projections.distinct(Projections.property("id").as("id"))) 
				.add(Projections.property("servidor.nome").as("nome"))
				.add(Projections.property("login").as("login"))
				.add(Projections.property("unidadePerfilSolicitadaResetSenha").as("unidadePerfilSolicitadaResetSenha"))
	            .add(Projections.property("dataSolicitacaoResetSenha").as("dataSolicitacaoResetSenha"))
	            .add(Projections.property("status").as("status"));

		getCriteria().setProjection(proj);
		
		//Não exibe o usuario de Administrador(Não remover)
		getCriteria().add(Restrictions.ge("id", (long)1));
		
		if (this.getNome() != null && !this.getNome().equalsIgnoreCase("")) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("servidor.nome", this.getNome(), MatchMode.ANYWHERE));
		}
		
		if (this.getCpf() != null && !this.getCpf().equalsIgnoreCase("")) {
			getCriteria().add(Restrictions.ilike("servidor.cpf", this.getCpf(), MatchMode.ANYWHERE));
		}
		
		if (this.getLogin() != null && !this.getLogin().equalsIgnoreCase("")) {
			getCriteria().add(Restrictions.ilike("login", this.getLogin(), MatchMode.ANYWHERE));
		}
		
		if (this.getEmail() != null && !this.getEmail().equalsIgnoreCase("")) {
			getCriteria().add(Restrictions.ilike("email", this.getEmail(), MatchMode.ANYWHERE));
		}
		
		if (this.getResetSenha() != null) {
			getCriteria().add(Restrictions.eqOrIsNull("resetSenha", this.getResetSenha()));
		}
	}


}
