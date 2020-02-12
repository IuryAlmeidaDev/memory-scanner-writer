package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroPerfil extends FiltroConsulta {

	private static final long serialVersionUID = 1L;
	
	private String nome;
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}

	@Override
	public void configurarFiltros() {
		//Não exibe o perfil de Administrador(Não remover)
		getCriteria().add(Restrictions.ge("id", (long)1));
		
		if (this.getNome() != null && !this.getNome().equalsIgnoreCase("")) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nome", this.getNome(), MatchMode.ANYWHERE));
		}
	}
}
