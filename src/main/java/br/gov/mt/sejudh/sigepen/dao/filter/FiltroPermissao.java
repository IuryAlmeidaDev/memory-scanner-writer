package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.model.Funcionalidade;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroPermissao extends FiltroConsulta {

	private static final long serialVersionUID = 1L;

	private String nome;
	
	private String descricao;
	
	private Funcionalidade funcionalidade;
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = Sigepen.formatarEspacos(descricao);
	}

	public Funcionalidade getFuncionalidade() {
		return funcionalidade;
	}

	public void setFuncionalidade(Funcionalidade funcionalidade) {
		this.funcionalidade = funcionalidade;
	}

	@Override
	public void configurarFiltros() {
		if (this.getNome() != null && !this.getNome().equalsIgnoreCase("")) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nome", this.getNome(), MatchMode.ANYWHERE));
		}
		
		if (this.getDescricao() != null && !this.getDescricao().equalsIgnoreCase("")) {
			getCriteria().add(Restrictions.ilike("descricao", this.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if (this.getFuncionalidade() != null) {
			getCriteria().add(Restrictions.eq("funcionalidade", this.getFuncionalidade()));
		}
	}
}
