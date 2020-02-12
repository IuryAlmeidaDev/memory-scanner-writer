package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.Modalidade;
import br.gov.mt.sejudh.sigepen.enumerator.Periculosidade;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoFoto;
import br.gov.mt.sejudh.sigepen.model.Alcunha;
import br.gov.mt.sejudh.sigepen.model.OutroNome;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.ProcessoRegimeCondicao;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroRecuperando extends FiltroConsulta {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String nome;
	private String nomeMae;
	private UnidadePrisional unidade;
	private StatusRecuperando status;
	private Periculosidade periculosidade;
	private Regime regime;
	private Condicao condicaoAtual;
	private Modalidade modalidade;
	private String  alcunha;
	private List<Long> pessoasIds;
	private String outrosNomes;
	private String outraMae;
	private String siglaUnidade;
	private Boolean isCadastroCompleto;
	private Boolean isBuscaFotoParaCadastroRecuperando = false;
	
	private Boolean isTemporarioAVencer;
	private Boolean isTemporarioVenceHoje;
	private Boolean isTemporarioVencido;
	
	public FiltroRecuperando() {
		this.pessoasIds = new ArrayList<Long>();
		this.isTemporarioAVencer = false;
		this.isTemporarioVenceHoje = false;
		this.isTemporarioVencido = false;
	}
	
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
		this.nomeMae = Sigepen.formatarEspacos(nomeMae);
	}

	public UnidadePrisional getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadePrisional unidade) {
		this.unidade = unidade;
	}

	public StatusRecuperando getStatus() {
		return status;
	}

	public void setStatus(StatusRecuperando status) {
		this.status = status;
	}

	public Periculosidade getPericulosidade() {
		return periculosidade;
	}

	public void setPericulosidade(Periculosidade periculosidade) {
		this.periculosidade = periculosidade;
	}

	public Regime getRegime() {
		return regime;
	}

	public void setRegime(Regime regime) {
		this.regime = regime;
	}

	public String getAlcunha() {
		return alcunha;
	}

	public void setAlcunha(String alcunha) {
		this.alcunha = Sigepen.formatarEspacos(alcunha);
	}

	public List<Long> getPessoasIds() {
		return pessoasIds;
	}

	public void setPessoasIds(List<Long> pessoasIds) {
		this.pessoasIds= pessoasIds;
	}

	public Condicao getCondicaoAtual() {
		return condicaoAtual;
	}

	public void setCondicaoAtual(Condicao condicaoAtual) {
		this.condicaoAtual = condicaoAtual;
	}
	
	public Modalidade getModalidade() {
		return modalidade;
	}

	public void setModalidade(Modalidade modalidade) {
		this.modalidade = modalidade;
	}

	public String getOutrosNomes() {
		return outrosNomes;
	}

	public void setOutrosNomes(String outrosNomes) {
		this.outrosNomes = Sigepen.formatarEspacos(outrosNomes);
	}

	public String getOutraMae() {
		return outraMae;
	}

	public void setOutraMae(String outraMae) {
		this.outraMae = outraMae;
	}

	public String getSiglaUnidade() {
		return siglaUnidade;
	}

	public void setSiglaUnidade(String siglaUnidade) {
		this.siglaUnidade = siglaUnidade;
	}

	public Boolean getIsCadastroCompleto() {
		return isCadastroCompleto;
	}

	public void setIsCadastroCompleto(Boolean isCadastroCompleto) {
		this.isCadastroCompleto = isCadastroCompleto;
	}

	public Boolean getIsBuscaFotoParaCadastroRecuperando() {
		return isBuscaFotoParaCadastroRecuperando;
	}

	public void setIsBuscaFotoParaCadastroRecuperando(Boolean isBuscaFotoParaCadastroRecuperando) {
		if (isBuscaFotoParaCadastroRecuperando == null) {
			this.isBuscaFotoParaCadastroRecuperando = false;
		}
		
		this.isBuscaFotoParaCadastroRecuperando = isBuscaFotoParaCadastroRecuperando;
	}

	public Boolean getIsTemporarioAVencer() {
		return isTemporarioAVencer;
	}

	public void setIsTemporarioAVencer(Boolean isTemporarioAVencer) {
		this.isTemporarioAVencer = isTemporarioAVencer;
	}

	public Boolean getIsTemporarioVenceHoje() {
		return isTemporarioVenceHoje;
	}

	public void setIsTemporarioVenceHoje(Boolean isTemporarioVenceHoje) {
		this.isTemporarioVenceHoje = isTemporarioVenceHoje;
	}

	public Boolean getIsTemporarioVencido() {
		return isTemporarioVencido;
	}

	public void setIsTemporarioVencido(Boolean isTemporarioVencido) {
		this.isTemporarioVencido = isTemporarioVencido;
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
	
		
		ProjectionList proj = Projections.projectionList()
				.add(Projections.property("id").as("pessoaId"))
				.add(Projections.property("recuperando_.id").as("id"))
				.add(Projections.property("nome").as("nome"))
				.add(Projections.property("nomeMae").as("nomeMae"))
				.add(Projections.property("dataNascimento").as("dataNascimento"))
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
				.add(Projections.property("situacaoCadastroCompleto.temParente").as("temParente"));
				//End Situação do cadastro
		
		getCriteria().setProjection(proj);

		if (this.isBuscaFotoParaCadastroRecuperando) {
			getCriteria()
				.createAlias("recuperando_.fotos", "foto_", JoinType.LEFT_OUTER_JOIN)
				.createAlias("foto_.imagem", "imagem_", JoinType.LEFT_OUTER_JOIN);
			
			proj.add(Projections.property("foto_.imagem").as("imagemFrente"));
			
			getCriteria().add(Restrictions.and(Restrictions.eq("foto_.isAtual", true)));
			getCriteria().add(Restrictions.and(Restrictions.eq("foto_.tipo", TipoFoto.FRENTE)));
		}

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
		
		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("recuperando_.status", this.getStatus()));
		}
		
		if (this.getCondicaoAtual() != null) {
			getCriteria().add(Restrictions.eq("recuperando_.condicaoAtual", this.getCondicaoAtual()));
		}
		
		if (this.getUnidade() != null) {
			getCriteria().add(Restrictions.eq("unidade.id", this.getUnidade().getId()));				
		}
		
		if(this.getAlcunha() != null && !this.getAlcunha().equalsIgnoreCase("")){
			DetachedCriteria critAlcunha = DetachedCriteria.forClass(Alcunha.class, "a")
					.add(Restrictions.ilike("a.nome", this.getAlcunha(), MatchMode.ANYWHERE))
					.setProjection(Projections.property("a.recuperando.id"));
			
			getCriteria().add(Subqueries.propertyIn("recuperando_.id", critAlcunha));
		}
		
		boolean filtroTemOutroNome = this.getOutrosNomes() != null && !this.getOutrosNomes().equalsIgnoreCase("");
		boolean filtroTemOutraMae = this.getOutraMae() != null && !this.getOutraMae().equalsIgnoreCase("");
		
		if(filtroTemOutroNome || filtroTemOutraMae){
			DetachedCriteria critOutrosNomes = DetachedCriteria.forClass(OutroNome.class, "o");
					
			if (filtroTemOutroNome) {
				critOutrosNomes.add(RestrictionsSigepen.ignoraAcentoLike("o.nome", this.getOutrosNomes(), MatchMode.ANYWHERE));
			}
			
			if (filtroTemOutraMae) {
				critOutrosNomes.add(RestrictionsSigepen.ignoraAcentoLike("o.nomeMae", this.getOutraMae(), MatchMode.ANYWHERE));
			}
					
			critOutrosNomes.setProjection(Projections.property("o.recuperando.id"));
			getCriteria().add(Subqueries.propertyIn("recuperando_.id", critOutrosNomes));
		}
		
		if (this.getPericulosidade() != null) {
			getCriteria().add(Restrictions.eq("recuperando_.periculosidade", this.getPericulosidade()));
		}
		
		if(this.getPessoasIds() != null && this.getPessoasIds().size() > 0) {
			getCriteria().add(Restrictions.in("id", this.getPessoasIds()));
		}
		
		if (this.getRegime() != null) {
			getCriteria().add(Restrictions.eq("recuperando_.regimeAtual", this.getRegime()));
		}
		
		if (this.getIsCadastroCompleto() != null) {
			getCriteria().add(Restrictions.eq("situacaoCadastro.cadastroCompleto", this.getIsCadastroCompleto()));
		}
		
		if (this.getModalidade() != null) {
			DetachedCriteria critModalidade = DetachedCriteria.forClass(ProcessoRegimeCondicao.class, "prc")
				.add(Restrictions.eq("prc.modalidade", this.getModalidade()))
				.createAlias("prc.processo", "proc", JoinType.INNER_JOIN)
				.setProjection(Projections.property("proc.recuperando.id"));
			
			critModalidade.add(Restrictions.eq("proc.modalidade", this.getModalidade()));

			if (this.getModalidade() == Modalidade.TEMPORARIA) {
				critModalidade.add(Restrictions.ne("prc.regime.id", 4L));
				
				if (isTemporarioAVencer) {
					critModalidade.add(Restrictions.sqlRestriction("trunc({alias}.data_soltura) > trunc(sysdate)"));
				} else if (isTemporarioVenceHoje) {
					critModalidade.add(Restrictions.sqlRestriction("trunc({alias}.data_soltura) = trunc(sysdate)"));
				} else if (isTemporarioVencido) {
					critModalidade.add(Restrictions.sqlRestriction("trunc({alias}.data_soltura) < trunc(sysdate)"));
				}
			}
			
			getCriteria().add(Subqueries.propertyIn("recuperando_.id", critModalidade));
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
