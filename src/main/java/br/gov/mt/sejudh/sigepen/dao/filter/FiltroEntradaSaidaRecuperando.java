package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoVisita;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamentosVisita;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.model.Nacionalidade;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.MaskUtil;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import br.gov.mt.sejudh.sigepen.util.criterion.ExpressaoFonetica.MatchModeFonetica;
import br.gov.mt.sejudh.sigepen.util.criterion.Restricao;

import javax.persistence.Query;

public class FiltroEntradaSaidaRecuperando extends FiltroConsulta {

	private static final long serialVersionUID = 5302934507717899462L;
		
	private String nome;
	private StatusCredenciamentosVisita statusCredenciamento;
	private UnidadePrisional unidade;
	private boolean exibeUnidadeNoFiltro = false;
	private Long responsavel;
	
	private Date dataInicio;
	private Date dataFim;
	private TipoEntrada tipoEntrada;
	private TipoVisita tipoVisita;
	private Boolean possuiDependente;
	private Boolean pesquisaFonetica = false;
	private Date dataNascimento;
	private SimNao emancipado;
	private Nacionalidade nacionalidade;
	private SimNao pne;
	private SimNao permanente;
	private String nomeRecuperando;
	private SituacaoVisita situacao;
	private List<String> filtros;
	private String filtro;
	
	@Override
	public void configurarFiltros() {
		filtros = new ArrayList<String>();
		filtro = StringUtils.EMPTY;
		
		getCriteria()
		.createAlias("entrada", "entrada", JoinType.INNER_JOIN)
		.createAlias("entrada.visitante", "visitante", JoinType.INNER_JOIN)
		.createAlias("visitante.pessoa", "pessoa_visitante", JoinType.INNER_JOIN)
		.createAlias("credenciamento", "credenciamento", JoinType.INNER_JOIN)
		.createAlias("credenciamento.unidadePrisional", "unidade", JoinType.INNER_JOIN)
		.createAlias("credenciamento.parente", "parente", JoinType.LEFT_OUTER_JOIN)
		.createAlias("parente.parentesco", "parentesco", JoinType.LEFT_OUTER_JOIN)
		.createAlias("credenciamento.recuperando", "recuperando", JoinType.LEFT_OUTER_JOIN)
		.createAlias("recuperando.pessoa", "pessoa_recuperando", JoinType.LEFT_OUTER_JOIN)
		.createAlias("credenciamento.tipoVisita", "tipo_visita", JoinType.INNER_JOIN)
		.createAlias("visitante.responsavel", "responsavel", JoinType.LEFT_OUTER_JOIN)
		.createAlias("responsavel.pessoa", "pessoa_responsavel", JoinType.LEFT_OUTER_JOIN)
;
		
		ProjectionList proj = Projections.projectionList();
		
        proj.add(Projections.projectionList()
                .add(Projections.property("entrada.id").as("id"))
                .add(Projections.property("tipo_visita.descricao").as("tipoVisita"))
                .add(Projections.property("pessoa_visitante.nome").as("nomeVisitante"))
                .add(Projections.property("visitante.id").as("idVisitante"))
                .add(Projections.property("pessoa_responsavel.nome").as("nomeResponsavel"))
                .add(Projections.property("responsavel.id").as("idResponsavel"))
                .add(Projections.property("credenciamento.profissao").as("profissao"))
                .add(Projections.property("parentesco.descricao").as("parentesco"))
                .add(Projections.property("pessoa_recuperando.nome").as("nomeRecuperando"))
                .add(Projections.property("unidade.id").as("idUnidade"))
                .add(Projections.property("unidade.nome").as("nomeUnidade"))
                .add(Projections.property("unidade.sigla").as("siglaUnidade"))
                .add(Projections.property("entrada.dataEntrada").as("dataEntrada"))
                .add(Projections.property("entrada.dataSaida").as("dataSaida"))
                .add(Projections.property("entrada.dataCancelamento").as("dataCancelamento"))
                .add(Projections.property("entrada.situacao").as("situacao"))
                .add(Projections.property("entrada.motivoEntrada").as("motivoEntrada"))
                .add(Projections.property("entrada.motivoCancelamento").as("motivoCancelamento"))
                .add(Projections.property("entrada.motivoFinalizacao").as("motivoFinalizacao"))
        );
		
		getCriteria().setProjection(proj);
		
		if(this.getPossuiDependente() != null && this.getPossuiDependente()) {
			getCriteria().add(Restrictions.isNotNull("visitante.responsavel"));
			if (StringUtils.isNotBlank(this.getNome())) {
				getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa_responsavel.nome", this.getNome(), MatchMode.ANYWHERE));
				this.filtros.add("Responsavel: " + this.getNome());
			}
		}else {
			if (StringUtils.isNotBlank(this.getNome())) {
				this.filtros.add("Nome: " + this.getNome());
				
				if(this.getPesquisaFonetica()) {
					getCriteria().add(Restricao.fonetiza("pessoa_visitante.nomeFonetizado", this.getNome(), MatchModeFonetica.ANYWHERE));
				}else {
					getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa_visitante.nome", this.getNome(), MatchMode.ANYWHERE));
				}
			}
		}
		
		if(this.getUnidade() != null && this.getUnidade().getId() > 0) {
			getCriteria().add(Restrictions.eq("entrada.unidadePrisional.id", this.getUnidade().getId()));
			
			this.filtros.add("Unidade: " + this.getUnidade().getNome());
		}
		
		if (this.getDataInicio() != null) {
        	Date di = Sigepen.getDataConsultaMaiorIgual(this.getDataInicio());
        	Date df = Sigepen.getDataConsultaMenorIgual(this.getDataInicio());
			if(this.getDataFim() != null) {
	        	df = Sigepen.getDataConsultaMenorIgual(this.getDataFim());
			}
			getCriteria().add(Restrictions.between("entrada.dataEntrada", di, df));
			this.filtros.add("Período: " + MaskUtil.getFormatoData().format(di) + " a " + MaskUtil.getFormatoData().format(df));
        }
		
		if (this.getDataInicio() == null && this.getDataFim() != null) {
        	Date di = Sigepen.getDataConsultaMaiorIgual(this.getDataFim());
        	Date df = Sigepen.getDataConsultaMenorIgual(this.getDataFim());
			getCriteria().add(Restrictions.between("entrada.dataSaida", di, df));
			this.filtros.add("Período: " + MaskUtil.getFormatoData().format(di) + " a " + MaskUtil.getFormatoData().format(df));
        }
		
		if (this.getTipoVisita() != null) {
			getCriteria().add(Restrictions.eq("credenciamento.tipoVisita", this.getTipoVisita()));
			this.filtros.add("Tipo Visita: " + this.getTipoVisita().getDescricao());
		}
		
		if (this.getTipoEntrada() != null) {
			getCriteria().add(Restrictions.eq("tipo_visita.tipoEntrada", this.getTipoEntrada()));
			this.filtros.add("Tipo Entrada: " + this.getTipoEntrada().getDescricao());
        }
		
		if (this.getDataNascimento() != null) {
			getCriteria().add(Restrictions.eq("pessoa_visitante.dataNascimento", this.getDataNascimento()));
			this.filtros.add("Data Nascimento: " + MaskUtil.getFormatoData().format(this.getDataNascimento()));
        }
		
		if (this.getEmancipado() != null) {
			getCriteria().add(Restrictions.eq("visitante.emancipado", this.getEmancipado()));
			getCriteria().add(Restrictions.gt("pessoa_visitante.dataNascimento", this.getDataMaioridade()));
			this.filtros.add("Emancipado? " + this.getEmancipado());
        }
		
		if (this.getNacionalidade() != null) {
			getCriteria().add(Restrictions.eq("pessoa_visitante.nacionalidade", this.getNacionalidade()));
			this.filtros.add("Nacionalidade: " +  this.getNacionalidade().getDescricao());
        }
		
		if (this.getPne() != null && SimNao.N.equals(this.getPne())) {
			getCriteria().add(Restrictions.or(Restrictions.isNull("visitante.pne"), Restrictions.eq("visitante.pne", SimNao.N)));
			this.filtros.add("PNE? " +  this.getPne().getDescricao());
        }
		
		if (this.getPne() != null && SimNao.S.equals(this.getPne())) {
			getCriteria().add(Restrictions.eq("visitante.pne", SimNao.S));
			this.filtros.add("PNE? " +  this.getPne().getDescricao());
        }
		
		if (this.getPermanente() != null) {
			getCriteria().add(Restrictions.eq("visitante.pnePermanente", this.getPermanente()));
			this.filtros.add("Permanente? " +  this.getPermanente().getDescricao());
        }

		if (StringUtils.isNotBlank(this.getNomeRecuperando())) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa_recuperando.nome", this.getNomeRecuperando(), MatchMode.ANYWHERE));
			this.filtros.add("Recuperando: " +  this.getNomeRecuperando());
        }
		
		if(this.getSituacao() != null) {
			getCriteria().add(Restrictions.eq("entrada.situacao", this.getSituacao()));
			this.filtros.add("Situação: " +  this.getSituacao());
		}
		
		if (this.filtros.size() > 0) {
			this.filtro = String.join(", ", filtros);
		}
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}

	public StatusCredenciamentosVisita getStatusCredenciamento() {
		return statusCredenciamento;
	}

	public void setStatusCredenciamento(StatusCredenciamentosVisita statusCredenciamento) {
		this.statusCredenciamento = statusCredenciamento;
	}

	public UnidadePrisional getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadePrisional unidade) {
		this.unidade = unidade;
	}

	public Long getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Long responsavel) {
		this.responsavel = responsavel;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public TipoEntrada getTipoEntrada() {
		return tipoEntrada;
	}

	public void setTipoEntrada(TipoEntrada tipoEntrada) {
		this.tipoEntrada = tipoEntrada;
	}

	public TipoVisita getTipoVisita() {
		return tipoVisita;
	}

	public void setTipoVisita(TipoVisita tipoVisita) {
		this.tipoVisita = tipoVisita;
	}

	public Boolean getPossuiDependente() {
		return possuiDependente;
	}

	public void setPossuiDependente(Boolean possuiDependente) {
		this.possuiDependente = possuiDependente;
	}

	public Boolean getPesquisaFonetica() {
		return pesquisaFonetica;
	}

	public void setPesquisaFonetica(Boolean pesquisaFonetica) {
		this.pesquisaFonetica = pesquisaFonetica;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public SimNao getEmancipado() {
		return emancipado;
	}

	public void setEmancipado(SimNao emancipado) {
		this.emancipado = emancipado;
	}

	public Nacionalidade getNacionalidade() {
		return nacionalidade;
	}

	public void setNacionalidade(Nacionalidade nacionalidade) {
		this.nacionalidade = nacionalidade;
	}

	public SimNao getPne() {
		return pne;
	}

	public void setPne(SimNao pne) {
		this.pne = pne;
	}

	public SimNao getPermanente() {
		return permanente;
	}

	public void setPermanente(SimNao permanente) {
		this.permanente = permanente;
	}

	public String getNomeRecuperando() {
		return nomeRecuperando;
	}

	public void setNomeRecuperando(String nomeRecuperando) {
		this.nomeRecuperando = nomeRecuperando;
	}
	
	public SituacaoVisita getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoVisita situacao) {
		this.situacao = situacao;
	}

	public Date getDataMaioridade(){
		Calendar now = Calendar.getInstance();
        now.add(Calendar.YEAR, -18);
        return now.getTime();
	}

	public String getFiltro() {
		return filtro;
	}

	public void setFiltro(String filtro) {
		this.filtro = filtro;
	}

	public boolean isExibeUnidadeNoFiltro() {
		return exibeUnidadeNoFiltro;
	}

	public void setExibeUnidadeNoFiltro(boolean exibeUnidadeNoFiltro) {
		this.exibeUnidadeNoFiltro = exibeUnidadeNoFiltro;
	}

	public String configureFiltros() {
		StringBuilder builder = new StringBuilder("WHERE (1=1)  ");

		if(this.getPossuiDependente() != null && this.getPossuiDependente()) {
			builder.append("and resp.id is not null  ");
		}

		if(this.getUnidade() != null && this.getUnidade().getId() > 0) {
			builder.append("and up.id = :unidade  ");
		}


		if (this.getDataInicio() != null) {
			builder.append("and esv.data_entrada between :di and :df  ");
		}

		if (this.getDataInicio() == null && this.getDataFim() != null) {
			builder.append("and esv.data_entrada between :di and :df  ");
		}

		if (this.getTipoVisita() != null) {
			builder.append("and tipo_visita.ID_TIPO_VISITA = :tipoVisita  ");
		}

		if (this.getTipoEntrada() != null) {
			builder.append("and tipo_visita.CODG_TIPO_ENTRADA = :tipoEntrada  ");
		}

		if (this.getDataNascimento() != null) {
			builder.append("and pessoa_v.DATA_NASCIMENTO > :dataNascimento  ");
		}

		if (this.getEmancipado() != null) {
			builder.append("and vis.FLAG_EMANCIPADO = :emancipado  ");
		}

		if (this.getNacionalidade() != null) {
			builder.append("and pessoa_v.nacionalidade_id = :nascionalidade  ");
		}

		if (this.getPne() != null && SimNao.N.equals(this.getPne())) {
			builder.append("and(vis.FLAG_PNE is null or vis.FLAG_PNE = :pne )");
		}

		if (this.getPermanente() != null) {
			builder.append("and vis.FLAG_PNE_PERMANENTE = :pnePermanente ");
		}

		if (StringUtils.isNotBlank(this.getNomeRecuperando())) {
			builder.append("and translate(lower(pessoa_rec.nome),'àáâãäèéêëìíîïòóôõöùúûüç', 'aaaaaeeeeiiiiooooouuuuc') like translate(lower(:recuperando),'àáâãäèéêëìíîïòóôõöùúûüç', 'aaaaaeeeeiiiiooooouuuuc') ");
		}

		if (StringUtils.isNotBlank(this.getNome())) {
			builder.append("and translate(lower(pessoa_vis.nome),'àáâãäèéêëìíîïòóôõöùúûüç', 'aaaaaeeeeiiiiooooouuuuc') like translate(lower(:visitante),'àáâãäèéêëìíîïòóôõöùúûüç', 'aaaaaeeeeiiiiooooouuuuc') ");
		}

		if (this.getSituacao() != null) {
		    int situacaoCode;
		    switch (this.getSituacao()) {
		        case FINALIZADA:
		            situacaoCode = 2;
		            break;
		        case EM_ANDAMENTO:
		            situacaoCode = 1;
		            break;
		        case CANCELADA:
		            situacaoCode = 3;
		            break;
		        default:
		            situacaoCode = -1; // Valor padrão para situações não tratadas, se necessário.
		    }
		    if (situacaoCode != -1) {
		        builder.append("and esv.situacao = ").append(situacaoCode).append(" ");
		    }
		}

		return builder.toString();
	}

	public void configureParametos(Query query, int first) {

		if(this.getUnidade() != null && this.getUnidade().getId() > 0) {
			query.setParameter("unidade", getUnidade().getId());

		}


		if (this.getDataInicio() != null) {
			Date di = Sigepen.getDataConsultaMaiorIgual(this.getDataInicio());
			Date df = Sigepen.getDataConsultaMenorIgual(this.getDataInicio());
			if(this.getDataFim() != null) {
				df = Sigepen.getDataConsultaMenorIgual(this.getDataFim());
			}
			query.setParameter("di", di);
			query.setParameter("df", df);
		}

		if (this.getDataInicio() == null && this.getDataFim() != null) {
			Date di = Sigepen.getDataConsultaMaiorIgual(this.getDataFim());
			Date df = Sigepen.getDataConsultaMenorIgual(this.getDataFim());
			query.setParameter("di", di);
			query.setParameter("df", df);
		}

		if (this.getTipoVisita() != null) {
			query.setParameter("tipoVisita", getTipoVisita().getId());
		}

		if (this.getTipoEntrada() != null) {
			query.setParameter("tipoEntrada", getTipoEntrada().getId());
		}

		if (this.getDataNascimento() != null) {
			query.setParameter("dataNascimento", getDataNascimento());
		}

		if (this.getEmancipado() != null) {
			query.setParameter("emancipado", getEmancipado().getDescricao().equals("Sim") ? "S" : "N");
		}

		if (this.getNacionalidade() != null) {
			query.setParameter("nascionalidade", getNacionalidade());
		}

		if (this.getPne() != null && SimNao.N.equals(this.getPne())) {
			query.setParameter("pne", getPne().getDescricao().equals("Sim") ? "S" : "N");
		}

		if (this.getPermanente() != null) {
			query.setParameter("pnePermanente", getPermanente().getDescricao().equals("Sim") ? "S" : "N");
		}

		if (StringUtils.isNotBlank(this.getNomeRecuperando())) {
			query.setParameter("recuperando", getNomeRecuperando());
		}

		if (StringUtils.isNotBlank(this.getNome())) {
			query.setParameter("visitante", getNome());
		}

//		if(this.getSituacao() != null) {
//			query.setParameter("situacao", getSituacao());
//		}

		if (first>=0){
			query.setParameter("rowInicial", first);
			query.setParameter("rowFinal", (first+10));
		}

	}
}
