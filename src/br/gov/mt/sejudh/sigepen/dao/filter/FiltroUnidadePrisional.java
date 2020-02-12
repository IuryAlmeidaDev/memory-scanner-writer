package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.model.TipoUnidade;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroUnidadePrisional extends FiltroConsulta{

	private static final long serialVersionUID = 1L;
	private long id;
	private String nome;
	private String sigla;
	private TipoUnidade tipoUnidade;
	private SituacaoUnidade situacaoUnidade;
    List<SituacaoUnidade> listaSituacao = new ArrayList<SituacaoUnidade>();

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public TipoUnidade getTipoUnidade() {
		return tipoUnidade;
	}

	public void setTipoUnidade(TipoUnidade tipoUnidade) {
		this.tipoUnidade = tipoUnidade;
	}	

	public SituacaoUnidade getSituacaoUnidade() {
		return situacaoUnidade;
	}

	public void setSituacaoUnidade(SituacaoUnidade situacaoUnidade) {
		this.situacaoUnidade = situacaoUnidade;
	}
	
    public List<SituacaoUnidade> getListaSituacao() {
        return listaSituacao;
    }

    public void setListaSituacao(List<SituacaoUnidade> listaSituacao) {
        this.listaSituacao = listaSituacao;
    }

	
	@Override
	public void configurarFiltros() {
		getCriteria()
		.createAlias("tipoUnidade", "tipoUnidade", JoinType.LEFT_OUTER_JOIN)
		.createAlias("responsavel", "responsavel", JoinType.LEFT_OUTER_JOIN)
		.createAlias("endereco", "endereco", JoinType.LEFT_OUTER_JOIN)
		.createAlias("endereco.cidade", "cidade", JoinType.LEFT_OUTER_JOIN)
		.createAlias("estrutura", "estrutura", JoinType.LEFT_OUTER_JOIN);
		
		Projection proj = Projections.projectionList()
				.add(Projections.property("id").as("id")) 
				.add(Projections.property("sigla").as("sigla"))
				.add(Projections.property("nome").as("nome"))
				.add(Projections.property("tipoUnidade.descricao").as("tipo"))
				.add(Projections.property("responsavel.nome").as("responsavel"))
	            .add(Projections.property("cidade.nome").as("municipio"))
	            .add(Projections.property("estrutura.situacaoUnidade").as("situacaoUnidade"))
	            .add(Projections.property("estrutura.id").as("idEstrutura"))
	           	.add(Projections.alias(Projections.sqlProjection(" (SELECT count(pl.ID) FROM " + Sigepen.SEJUDH_SCHEMA +  ".PASSAGEM_LOTACAO pl INNER JOIN " + Sigepen.SEJUDH_SCHEMA +  ".ESTRUTURA est_ ON pl.ESTRUTURA_ID = est_.ID WHERE est_.UNIDADEPRISIONAL_ID = this_.ID AND pl.DATA_SAIDA IS NULL) as quantidadeAtual ", new String[]{"quantidadeAtual"}, new Type[]{new IntegerType()}),"quantidadeAtual"));
				

		getCriteria().setProjection(proj);

		if(this.getId() > 0){
			getCriteria().add(Restrictions.eq("id", this.getId()));
		}
		
		if(this.getNome() !=null && !this.getNome().equalsIgnoreCase("")){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nome", this.getNome(), MatchMode.ANYWHERE));
		}
		
		if(this.getSigla() !=null && !this.getSigla().equalsIgnoreCase("")){
			getCriteria().add(Restrictions.ilike("sigla", this.getSigla(), MatchMode.ANYWHERE));
		}
		
		if(this.getTipoUnidade() != null){
			getCriteria().add(Restrictions.eq("tipoUnidade", this.getTipoUnidade()));
		}

		if (this.getSituacaoUnidade() != null) {
			getCriteria().add(Restrictions.eq("estrutura.situacaoUnidade", this.getSituacaoUnidade()));
		}
	}

}
