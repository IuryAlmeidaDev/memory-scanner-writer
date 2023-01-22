package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroQuadroVagas extends FiltroConsulta{

	private static final long serialVersionUID = 1L;
	private long id;
	private String nome;
	private String sigla;
	private String cidade;

	
	public long getId() {
		return id;
	}

	public void setId(long unidadeId) {
		this.id = unidadeId;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = Sigepen.formatarEspacos(sigla);
	}
	
	public String getCidade() {
		return cidade;
	}
	
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	
	@Override
	public void configurarFiltros() {
		
		Projection proj = Projections.projectionList()
				.add(Projections.property("unidadeId").as("id")) 
				.add(Projections.property("siglaUnidade").as("sigla"))
				.add(Projections.property("nome").as("nome"));
				

		getCriteria().setProjection(proj);

		if(this.getId() > 0){
			getCriteria().add(Restrictions.eq("unidadeId", this.getId()));
		}
		
		if(this.getNome() !=null && !this.getNome().equalsIgnoreCase("")){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nome", this.getNome(), MatchMode.ANYWHERE));
		}
		
		if(this.getSigla() !=null && !this.getSigla().equalsIgnoreCase("")){
			getCriteria().add(Restrictions.ilike("siglaUnidade", this.getSigla(), MatchMode.ANYWHERE));
		}
	}

}
