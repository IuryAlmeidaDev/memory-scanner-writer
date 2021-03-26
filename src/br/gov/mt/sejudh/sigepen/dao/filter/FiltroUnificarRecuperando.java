package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.TipoFoto;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;

public class FiltroUnificarRecuperando extends FiltroConsulta {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String nome;
	private String nomeMae;
    private Date dataNascimento;
    private Boolean isPesquisaFonetica;
	
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
		this.nome = nome;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

    public Boolean getIsPesquisaFonetica() {
		return isPesquisaFonetica;
	}

	public void setIsPesquisaFonetica(Boolean isPesquisaFonetica) {
		this.isPesquisaFonetica = isPesquisaFonetica;
	}

	@Override
	public void configurarFiltros() {
		getCriteria()
		.createAlias("recuperando", "recuperando_", JoinType.LEFT_OUTER_JOIN)
		.createAlias("recuperando_.passagens", "passagem_", JoinType.LEFT_OUTER_JOIN)
		.createAlias("passagem_.lotacao", "lotacao", JoinType.LEFT_OUTER_JOIN)
		.createAlias("lotacao.estrutura", "estrutura", JoinType.LEFT_OUTER_JOIN)
		.createAlias("recuperando_.regimeAtual", "regimeAtual", JoinType.LEFT_OUTER_JOIN)
		.createAlias("estrutura.unidadePrisional", "unidade", JoinType.LEFT_OUTER_JOIN)
		
		//Situação do Cadastro do Recuperando
		.createAlias("recuperando_.situacaoCadastro", "situacaoCadastro", JoinType.LEFT_OUTER_JOIN)
		.createAlias("recuperando_.situacaoCadastroCompleto", "situacaoCadastroCompleto", JoinType.LEFT_OUTER_JOIN);
		//End Situação do cadastro
		
		getCriteria()
		.createAlias("estrutura.estruturaComplemento", "estruturacompl", JoinType.LEFT_OUTER_JOIN);
	
		
		ProjectionList proj = Projections.projectionList();
        proj.add(Projections.distinct(Projections.projectionList()
                .add(Projections.property("id").as("pessoaId"))
                .add(Projections.property("recuperando_.id").as("id"))
                .add(Projections.property("nome").as("nome"))
                .add(Projections.property("nomeSocial").as("nomeSocial"))
                .add(Projections.property("nomeMae").as("nomeMae"))
                .add(Projections.property("dataNascimento").as("dataNascimento"))
                .add(Projections.property("passagem_.dataEntrada").as("dataEntrada"))
                .add(Projections.property("unidade.nome").as("unidade"))
                .add(Projections.property("unidade.sigla").as("siglaUnidade"))
                .add(Projections.property("unidade.id").as("unidadeId"))
                .add(Projections.property("estruturacompl.nomeCompleto").as("lotacaoAtualNomeEstrutura"))
                .add(Projections.property("recuperando_.status").as("status"))
                .add(Projections.property("recuperando_.condicaoAtual").as("condicaoAtual"))
                .add(Projections.property("regimeAtual.descricao").as("regime"))
                .add(Projections.property("recuperando_.periculosidade").as("periculosidade"))
                .add(Projections.property("passagem_.tipoDesligamento").as("taVivo"))

                //Situação do cadastro do Recuperando
                .add(Projections.property("situacaoCadastro.cadastroCompleto").as("situacaoCadastro"))
                .add(Projections.property("situacaoCadastroCompleto.temTatuagem").as("temTatuagem"))
                .add(Projections.property("situacaoCadastroCompleto.temFotoTatuagem").as("temFotoTatuagem"))
                .add(Projections.property("situacaoCadastroCompleto.temFotoFrente").as("temFotoFrente"))
                .add(Projections.property("situacaoCadastroCompleto.temFotoPerfil").as("temFotoPerfil"))
                .add(Projections.property("situacaoCadastroCompleto.temFotoCorpoInteiro").as("temFotoCorpoInteiro"))
                .add(Projections.property("situacaoCadastroCompleto.vidaSocialCompleta").as("vidaSocialCompleta"))
                .add(Projections.property("situacaoCadastroCompleto.temParente").as("temParente"))
        ));

				//End Situação do cadastro
		
		getCriteria().setProjection(proj);

		getCriteria()
			.createAlias("recuperando_.fotos", "foto_", JoinType.LEFT_OUTER_JOIN)
			.createAlias("foto_.imagem", "imagem_", JoinType.LEFT_OUTER_JOIN);
		
		proj.add(Projections.property("foto_.imagem").as("imagemFrente"));
		
		getCriteria().add(Restrictions.and(Restrictions.eq("foto_.isAtual", true)));
		getCriteria().add(Restrictions.and(Restrictions.eq("foto_.tipo", TipoFoto.FRENTE)));

		//Subselect para trazer apenas a ultima passagem e ultima lotação
		DetachedCriteria critPassagem = DetachedCriteria.forClass(Passagem.class, "pass")
				.add(Restrictions.eqProperty("pass.recuperando.id", "recuperando_.id"))
				.setProjection(Projections.projectionList().add(Projections.max("pass.id")));
		
		DetachedCriteria critLotacao = DetachedCriteria.forClass(PassagemLotacao.class, "lot")
				.add(Restrictions.eqProperty("lot.passagem.id", "passagem_.id"))
				.setProjection(Projections.projectionList().add(Projections.max("lot.id")));
		
		getCriteria().add(
			Restrictions.or(
				Restrictions.conjunction()
					.add(Restrictions.isNotNull("recuperando_.id"))
					.add(Restrictions.or(
							Restrictions.isNull("passagem_.id"),
							Restrictions.conjunction()
								.add(Subqueries.propertyEq("passagem_.id", critPassagem))
								.add(Subqueries.propertyEq("lotacao.id", critLotacao)))),
				Restrictions.isNull("recuperando_.id")));

		if (this.getId() != null) {
			getCriteria().add(Restrictions.eq("recuperando_.id", this.getId()));
		}
		
		if (this.getNome()!= null && !this.getNome().equalsIgnoreCase("")) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nome", this.getNome(), MatchMode.ANYWHERE));
		}
		
		if (this.getNomeMae()!= null && !this.getNomeMae().equalsIgnoreCase("")) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nomeMae", this.getNomeMae(), MatchMode.ANYWHERE));
		}
		
        if (this.getDataNascimento() != null) {
        	getCriteria().add(Restrictions.eq("dataNascimento", this.getDataNascimento()));
        }
	}

	@Override
	public void configurarOrdenacao() {
		super.configurarOrdenacao();
		
		if (this.getOrdenacao() != null) {
			getCriteria().addOrder((this.getOrdenacao().isAscending() ? Order.asc("pessoaId") : Order.desc("pessoaId")));
		}
	}
}
