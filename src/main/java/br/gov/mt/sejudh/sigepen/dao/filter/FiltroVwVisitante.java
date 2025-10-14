package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mt.sejudh.sigepen.enumerator.VencimentoCredenciamento;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import br.gov.mt.sejudh.sigepen.util.criterion.ExpressaoFonetica.MatchModeFonetica;
import br.gov.mt.sejudh.sigepen.util.criterion.Restricao;

public class FiltroVwVisitante extends FiltroConsulta {

    private static final long serialVersionUID = 1L;
    private String nome;
    private String nomeMae;
    private String cpf;
    private String recuperando;
    private Date dataNascimento;
    private StatusVisitante status;
    private UnidadePrisional unidadePrisional;
    private FaseCredenciamento faseCredenciamento;
    private StatusCredenciamento situacaoCredenciamento;
    private TipoVisita tipoVisita;
    private Visitante responsavel;
    private List<Long> pessoasIds;
	private boolean ehRelatorio;
	private Boolean isPesquisaFonetica = false;
    private VencimentoCredenciamento vencimentoCredenciamento;

    public FiltroVwVisitante() {
        this.pessoasIds = new ArrayList<>();
    }

    @Override
    public void configurarFiltros() {
    	if (StringUtils.isNotBlank(this.getNome()) || StringUtils.isNotBlank(this.getCpf())
                || CollectionUtils.isNotEmpty(this.getPessoasIds()) || this.getDataNascimento() != null
                || StringUtils.isNotBlank(this.getNomeMae())) {
    		getCriteria()
    		.createAlias("recuperando", "r", JoinType.LEFT_OUTER_JOIN)
    		.createAlias("vwVisitante", "visitante", JoinType.LEFT_OUTER_JOIN);
    	} else {
    		getCriteria()
    		.createAlias("recuperando", "r", JoinType.LEFT_OUTER_JOIN)
    		.createAlias("vwVisitante", "visitante", JoinType.INNER_JOIN);
    	}

    	
		Criteria criteriaCredenciamentos;
        if (StringUtils.isNotBlank(this.getRecuperando()) || this.getUnidadePrisional() != null || this.getTipoVisita() != null) {
        	criteriaCredenciamentos = getCriteria().createAlias("visitante.credenciamentos", "vr", JoinType.LEFT_OUTER_JOIN);
        } else {
        	criteriaCredenciamentos = getCriteria();
        }
        Criteria criteriaRecuperando = null;

		ProjectionList proj = Projections.projectionList();
        proj.add(Projections.distinct(Projections.projectionList()
                .add(Projections.property("id").as("idPessoa"))
                .add(Projections.property("visitante.id").as("id"))
                .add(Projections.property("nome").as("nome"))
                .add(Projections.property("visitante.dataHoraCadastro").as("dataHoraCadastro"))
                .add(Projections.property("visitante.status").as("status"))
                .add(Projections.property("visitante.ultimaAprovacao").as("ultimaAprovacao"))
                .add(Projections.property("visitante.ultimaFaseCredenciamento").as("ultimaFaseCredenciamento"))
                .add(Projections.property("visitante.ultimoStatusCredenciamento").as("ultimoStatusCredenciamento"))
                .add(Projections.property("visitante.credenciamentosAtivos").as("credenciamentosAtivos"))
                .add(Projections.property("visitante.pessoa").as("pessoa"))
                .add(Projections.property("visitante.faseAtual").as("faseAtual"))
                .add(Projections.property("visitante.situacaoAtual").as("situacaoAtual"))
                .add(Projections.property("visitante.tipoDesligamento").as("tipoDesligamento"))
                .add(Projections.property("visitante.dataSaida").as("dataSaida"))
                .add(Projections.property("visitante.fimImpedimento").as("fimImpedimento"))
                .add(Projections.property("visitante.idRecuperando").as("idRecuperando"))));
        getCriteria().setProjection(proj);

        if (StringUtils.isNotBlank(this.getNome()) || StringUtils.isNotBlank(this.getCpf())
                || CollectionUtils.isNotEmpty(this.getPessoasIds()) || this.getDataNascimento() != null
                || StringUtils.isNotBlank(this.getNomeMae())) {
            this.configurarFiltrosPessoa();
        }

         if (this.getStatus() != null) {
            getCriteria().add(Restrictions.eq("visitante.status", this.getStatus()));
        }

        if (this.getUnidadePrisional() != null) {
            getCriteria().add(Restrictions.eq("vr.unidadePrisional", this.getUnidadePrisional()));
        }

        if (this.getFaseCredenciamento() != null) {
        	if (StringUtils.isNotBlank(this.getRecuperando()) || this.getUnidadePrisional() != null || this.getTipoVisita() != null) { 
        		getCriteria().add(Restrictions.eq("vr.faseCredenciamento", this.getFaseCredenciamento().getId()));
                if (this.getSituacaoCredenciamento() != null) {
                	getCriteria().add(Restrictions.eq("vr.statusCredenciamento", this.getSituacaoCredenciamento().getId()));
                }
        	} else {
        		Criteria criteriaCredenciamentos2 = getCriteria().createAlias("visitante.credenciamentos", "vr", JoinType.LEFT_OUTER_JOIN);
        		getCriteria().add(Restrictions.eq("vr.faseCredenciamento", this.getFaseCredenciamento().getId()));
                if (this.getSituacaoCredenciamento() != null) {
                	getCriteria().add(Restrictions.eq("vr.statusCredenciamento", this.getSituacaoCredenciamento().getId()));
                }
        	}
        } else {
        	if (StringUtils.isNotBlank(this.getRecuperando()) || this.getUnidadePrisional() != null || this.getTipoVisita() != null) { 
                if (this.getSituacaoCredenciamento() != null) {
                	getCriteria().add(Restrictions.eq("vr.statusCredenciamento", this.getSituacaoCredenciamento().getId()));
                }
        	} else {
        		Criteria criteriaCredenciamentos2 = getCriteria().createAlias("visitante.credenciamentos", "vr", JoinType.LEFT_OUTER_JOIN);
                if (this.getSituacaoCredenciamento() != null) {
                	getCriteria().add(Restrictions.eq("vr.statusCredenciamento", this.getSituacaoCredenciamento().getId()));
                }
        		
        	}
        }

        if (this.getTipoVisita() != null) {
            getCriteria().add(Restrictions.eq("visitante.credenciamentos.recuperando.tipoVisita", this.getTipoVisita()));
        }

        if (StringUtils.isNotBlank(this.getRecuperando())) {
        	if (criteriaRecuperando == null) {
            	criteriaRecuperando = criteriaCredenciamentos.createAlias("vr.recuperando", "r3", JoinType.INNER_JOIN);
            	Criteria criteriaPessoa = criteriaRecuperando.createAlias("r3.pessoa", "pes", JoinType.INNER_JOIN);
        	}else {
            	Criteria criteriaPessoa = criteriaRecuperando.createAlias("r3.pessoa", "pes", JoinType.INNER_JOIN);
        		
        	}
        	getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pes.nome", this.getRecuperando(), MatchMode.ANYWHERE));
        }

        if (this.getResponsavel() != null) {
            getCriteria().add(Restrictions.eq("visitante.responsavel", this.getResponsavel()));
        }
        
        if (CollectionUtils.isNotEmpty(this.getPessoasIds())) {
            getCriteria().add(Restrictions.in("visitante.idPessoa", this.getPessoasIds()));
        }

    	if (!(StringUtils.isNotBlank(this.getNome()) || StringUtils.isNotBlank(this.getCpf())
                || CollectionUtils.isNotEmpty(this.getPessoasIds()) || this.getDataNascimento() != null
                || StringUtils.isNotBlank(this.getNomeMae()))) {
    		getCriteria().add(Restrictions.gt("visitante.id", 0L));
    	}

    }

    private void configurarFiltrosPessoa() {
        if (StringUtils.isNotBlank(this.getNome())) {
            
			if (this.getIsPesquisaFonetica()) {
				getCriteria().add(Restricao.fonetiza("nomeFonetizado", this.getNome(), MatchModeFonetica.ANYWHERE));
			} else {
				getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nome", this.getNome(), MatchMode.ANYWHERE));
			}
        }
        if (StringUtils.isNotBlank(this.getNomeMae())) {
			if (this.getIsPesquisaFonetica()) {
				getCriteria().add(Restricao.fonetiza("nomeFonetizadoMae", this.getNomeMae(), MatchModeFonetica.ANYWHERE));
			} else {
				getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nomeMae", this.getNomeMae(), MatchMode.ANYWHERE));
			}
        }
        if (this.getDataNascimento() != null) {
        	getCriteria().add(Restrictions.eq("dataNascimento", this.getDataNascimento()));
        }
        if (StringUtils.isNotBlank(this.getCpf())) {
            Criteria criteriaDocumento = getCriteria().createAlias("documentos", "doc", JoinType.INNER_JOIN);

            criteriaDocumento.add(Restrictions.like("doc.numero", this.getCpf(), MatchMode.ANYWHERE));
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Sigepen.formatarEspacos(nome);
    }

    public StatusVisitante getStatus() {
        if (vencimentoCredenciamento != null && !vencimentoCredenciamento.equals(VencimentoCredenciamento.VENCIDOS)){
            status = StatusVisitante.A;
        }
        return status;
    }

    public void setStatus(StatusVisitante status) {
        this.status = status;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getRecuperando() {
        return recuperando;
    }

    public void setRecuperando(String recuperando) {
        this.recuperando = recuperando;
    }

    public List<Long> getPessoasIds() {
        return pessoasIds;
    }

    public void setPessoasIds(List<Long> pessoasIds) {
        this.pessoasIds = pessoasIds;
    }

    public TipoVisita getTipoVisita() {
        return tipoVisita;
    }

    public void setTipoVisita(TipoVisita tipoVisita) {
        this.tipoVisita = tipoVisita;
    }

    public UnidadePrisional getUnidadePrisional() {
        return unidadePrisional;
    }

    public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
        this.unidadePrisional = unidadePrisional;
    }

    public StatusCredenciamento getSituacaoCredenciamento() {
        return situacaoCredenciamento;
    }

    public void setSituacaoCredenciamento(StatusCredenciamento situacaoCredenciamento) {
        this.situacaoCredenciamento = situacaoCredenciamento;
    }

    public Visitante getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Visitante responsavel) {
        this.responsavel = responsavel;
    }
    
    public void setEhRelatorio(boolean ehRelatorio) {
		this.ehRelatorio = ehRelatorio;
	}

	public Boolean getIsPesquisaFonetica() {
		return isPesquisaFonetica;
	}

	public void setIsPesquisaFonetica(Boolean isPesquisaFonetica) {
		this.isPesquisaFonetica = isPesquisaFonetica;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = Sigepen.formatarEspacos(nomeMae);
	}

	public FaseCredenciamento getFaseCredenciamento() {
		return faseCredenciamento;
	}

	public void setFaseCredenciamento(FaseCredenciamento faseCredenciamento) {
		this.faseCredenciamento = faseCredenciamento;
	}

    public VencimentoCredenciamento getVencimentoCredenciamento() {
        return vencimentoCredenciamento;
    }

    public void setVencimentoCredenciamento(VencimentoCredenciamento vencimentoCredenciamento) {
        this.vencimentoCredenciamento = vencimentoCredenciamento;
    }
}