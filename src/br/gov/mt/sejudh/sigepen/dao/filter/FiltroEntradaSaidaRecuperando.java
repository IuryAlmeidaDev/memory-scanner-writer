package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
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
	private String filtro;
	
	@Override
	public void configurarFiltros() {
		filtro = "";
		getCriteria()
		.createAlias("entrada", "entrada", JoinType.INNER_JOIN)
		.createAlias("credenciamento", "credenciamento", JoinType.INNER_JOIN)
		.createAlias("entrada.visitante", "visitante", JoinType.INNER_JOIN)
		.createAlias("credenciamento.recuperando", "recuperando", JoinType.LEFT_OUTER_JOIN)
		.createAlias("recuperando.pessoa", "pessoa_recuperando", JoinType.LEFT_OUTER_JOIN)
		.createAlias("visitante.pessoa", "pessoa_visitante", JoinType.INNER_JOIN)
		.createAlias("credenciamento.tipoVisita", "tipo_visita", JoinType.INNER_JOIN)
		.createAlias("visitante.responsavel", "responsavel", JoinType.LEFT_OUTER_JOIN)
		.createAlias("responsavel.pessoa", "pessoa_responsavel", JoinType.LEFT_OUTER_JOIN);

		if(this.getPossuiDependente() != null && this.getPossuiDependente()) {
			getCriteria().add(Restrictions.isNotNull("visitante.responsavel"));
			if (StringUtils.isNotBlank(this.getNome())) {
				getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa_responsavel.nome", this.getNome(), MatchMode.ANYWHERE));
				this.filtro += " Responsavel: " + this.getNome() + ", ";
			}
		}else {
			if (StringUtils.isNotBlank(this.getNome())) {
				this.filtro += " Nome: " + this.getNome() + ", ";
				if(this.getPesquisaFonetica()) {
					getCriteria().add(Restricao.fonetiza("pessoa_visitante.nomeFonetizado", this.getNome(), MatchModeFonetica.ANYWHERE));
				}else {
					getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa_visitante.nome", this.getNome(), MatchMode.ANYWHERE));
				}
			}
		}
		
		if(this.getUnidade() != null && this.getUnidade().getId() > 0) {
			getCriteria().add(Restrictions.eq("entrada.unidadePrisional.id", this.getUnidade().getId()));
			
			if (exibeUnidadeNoFiltro) {
				this.filtro += " Unidade: " + this.getUnidade().getNome() + ", ";
			}
		}
		
		if (this.getDataInicio() != null) {
        	Date di = Sigepen.getDataConsultaMaiorIgual(this.getDataInicio());
        	Date df = Sigepen.getDataConsultaMenorIgual(this.getDataInicio());
			if(this.getDataFim() != null) {
	        	df = Sigepen.getDataConsultaMenorIgual(this.getDataFim());
			}
			getCriteria().add(Restrictions.between("entrada.dataEntrada", di, df));
			this.filtro += " Período: " + MaskUtil.getFormatoData().format(di) + " a " + MaskUtil.getFormatoData().format(df) + ", ";
        }
		
		if (this.getDataInicio() == null && this.getDataFim() != null) {
        	Date di = Sigepen.getDataConsultaMaiorIgual(this.getDataFim());
        	Date df = Sigepen.getDataConsultaMenorIgual(this.getDataFim());
			getCriteria().add(Restrictions.between("entrada.dataSaida", di, df));
			this.filtro += " Período: " + MaskUtil.getFormatoData().format(di) + " a " + MaskUtil.getFormatoData().format(df) + ", ";
        }
		
		if (this.getTipoVisita() != null) {
			getCriteria().add(Restrictions.eq("credenciamento.tipoVisita", this.getTipoVisita()));
			this.filtro += " Tipo Visita: " + this.getTipoVisita().getDescricao() + ", ";
		}
		
		if (this.getTipoEntrada() != null) {
			getCriteria().add(Restrictions.eq("tipo_visita.tipoEntrada", this.getTipoEntrada()));
			this.filtro += " Tipo Entrada: " + this.getTipoEntrada().getDescricao() + ", ";
        }
		
		if (this.getDataNascimento() != null) {
			getCriteria().add(Restrictions.eq("pessoa_visitante.dataNascimento", this.getDataNascimento()));
			this.filtro += " Data Nascimento: " + MaskUtil.getFormatoData().format(this.getDataNascimento()) + ", ";
        }
		
		if (this.getEmancipado() != null) {
			getCriteria().add(Restrictions.eq("visitante.emancipado", this.getEmancipado()));
			getCriteria().add(Restrictions.gt("pessoa_visitante.dataNascimento", this.getDataMaioridade()));
			this.filtro += " Emancipado? " + this.getEmancipado() + ", ";
        }
		
		if (this.getNacionalidade() != null) {
			getCriteria().add(Restrictions.eq("pessoa_visitante.nacionalidade", this.getNacionalidade()));
			this.filtro += " Nacionalidade: " +  this.getNacionalidade().getDescricao() + ", ";
        }
		
		if (this.getPne() != null && SimNao.N.equals(this.getPne())) {
			getCriteria().add(Restrictions.or(Restrictions.isNull("visitante.pne"), Restrictions.eq("visitante.pne", SimNao.N)));
			this.filtro += " PNE? " +  this.getPne().getDescricao() + ", ";
        }
		
		if (this.getPne() != null && SimNao.S.equals(this.getPne())) {
			getCriteria().add(Restrictions.eq("visitante.pne", SimNao.S));
			this.filtro += " PNE? " +  this.getPne().getDescricao() + ", ";
        }
		
		if (this.getPermanente() != null) {
			getCriteria().add(Restrictions.eq("visitante.pnePermanente", this.getPermanente()));
			this.filtro += " Permanente? " +  this.getPermanente().getDescricao() + ", ";
        }

		if (StringUtils.isNotBlank(this.getNomeRecuperando())) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa_recuperando.nome", this.getNomeRecuperando(), MatchMode.ANYWHERE));
			this.filtro += " Recuperando: " +  this.getNomeRecuperando() + ", ";
        }
		
		if(this.getSituacao() != null) {
			getCriteria().add(Restrictions.eq("entrada.situacao", this.getSituacao()));
			this.filtro += " Situação: " +  this.getSituacao() + ", ";
		}
		if(!"".equals(this.filtro)) {
			this.filtro = this.filtro.substring(0, this.filtro.length() - 2);
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
}
