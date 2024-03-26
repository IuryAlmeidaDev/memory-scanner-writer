package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroPessoa extends FiltroConsulta {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String nome;
	private String nomeMae;
	private String nomePai;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}
	public String getNomeMae() {
		return nomeMae;
	}
	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	
	public String getNomePai() {
		return nomePai;
	}
	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}
	
	@Override
	public void configurarFiltros() {

		Projection proj = Projections.projectionList()
				.add(Projections.distinct(Projections.property("id").as("id"))) 
				.add(Projections.property("nome").as("nome"))
				.add(Projections.property("dataNascimento").as("dataNascimento"))
	            .add(Projections.property("nomeMae").as("nomeMae"));

		getCriteria().setProjection(proj);
		
		if (this.getId() != null) {
			getCriteria().add(Restrictions.eq("id", this.getId()));
		}
		
		if (this.getNome() != null && !this.getNome().equalsIgnoreCase("")) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nome", this.getNome(), MatchMode.ANYWHERE));
		}
		
		if (this.getNomeMae() != null && !this.getNomeMae().equalsIgnoreCase("")) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nomeMae", this.getNomeMae(), MatchMode.ANYWHERE));
		}
		
		if (this.getNomePai() != null && !this.getNomePai().equalsIgnoreCase("")) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nomePai", this.getNomePai(), MatchMode.ANYWHERE));
		}
	}
}
