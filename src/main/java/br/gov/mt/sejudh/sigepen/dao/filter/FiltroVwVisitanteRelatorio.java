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

public class FiltroVwVisitanteRelatorio extends FiltroConsulta {

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
	private Boolean isPesquisaFonetica;
    private VencimentoCredenciamento vencimentoCredenciamento;

    public FiltroVwVisitanteRelatorio() {
        this.pessoasIds = new ArrayList<>();
    }

    @Override
    public void configurarFiltros() {
        Criteria criteriaCredenciamentos = getCriteria().createAlias("credenciamentos", "vr", JoinType.LEFT_OUTER_JOIN);

        if (StringUtils.isNotBlank(this.getNome()) || StringUtils.isNotBlank(this.getCpf())
                || CollectionUtils.isNotEmpty(this.getPessoasIds()) || this.getDataNascimento() != null
                || StringUtils.isNotBlank(this.getNomeMae())) {
            this.configurarFiltrosPessoa();
        }

        if (this.getStatus() != null) {
            getCriteria().add(Restrictions.eq("status", this.getStatus()));
        }

        if (this.getUnidadePrisional() != null) {
        	getCriteria().add(Restrictions.eq("vr.unidadePrisional", this.getUnidadePrisional()));
        }

        if (this.getSituacaoCredenciamento() != null) {
        	getCriteria().add(Restrictions.eq("vr.statusCredenciamento", this.getSituacaoCredenciamento().getId()));
        }

        if (this.getFaseCredenciamento() != null) {
        	getCriteria().add(Restrictions.eq("vr.faseCredenciamento", this.getFaseCredenciamento().getId()));
        }

        if (this.getTipoVisita() != null) {
            criteriaCredenciamentos.add(Restrictions.disjunction(
                    Restrictions.eq("vr.tipoVisita", this.getTipoVisita()), Restrictions.eq("tipoVisita", this.getTipoVisita())
            ));
        }

        if (StringUtils.isNotBlank(this.getRecuperando())) {
            Criteria criteriaRecuperando = criteriaCredenciamentos.createAlias("vr.recuperando", "r", JoinType.INNER_JOIN);
            Criteria criteriaPessoa = criteriaRecuperando.createAlias("r.pessoa", "p", JoinType.INNER_JOIN);

            criteriaPessoa.add(RestrictionsSigepen.ignoraAcentoLike("p.nome", this.getRecuperando(), MatchMode.ANYWHERE));
        }

        if (this.getResponsavel() != null) {
            getCriteria().add(Restrictions.eq("responsavel", this.getResponsavel()));
        }
    }
    

    private void configurarFiltrosPessoa() {
        Criteria criteriaPessoa = getCriteria().createAlias("pessoa", "p", JoinType.INNER_JOIN);

        if (StringUtils.isNotBlank(this.getNome())) {
            criteriaPessoa.add(RestrictionsSigepen.ignoraAcentoLike("p.nome", this.getNome(), MatchMode.ANYWHERE));
        }
        if (this.getDataNascimento() != null) {
            criteriaPessoa.add(Restrictions.eq("p.dataNascimento", this.getDataNascimento()));
        }
        if (StringUtils.isNotBlank(this.getCpf())) {
            Criteria criteriaDocumento = criteriaPessoa.createAlias("p.documentos", "doc", JoinType.INNER_JOIN);

            criteriaDocumento.add(Restrictions.like("doc.numero", this.getCpf(), MatchMode.ANYWHERE));
        }
        if (CollectionUtils.isNotEmpty(this.getPessoasIds())) {
            getCriteria().add(Restrictions.in("p.id", this.getPessoasIds()));
        }
        if (StringUtils.isNotBlank(this.getNomeMae())) {
			if (this.getIsPesquisaFonetica()) {
				getCriteria().add(Restricao.fonetiza("p.nomeFonetizadoMae", this.getNomeMae(), MatchModeFonetica.ANYWHERE));
			} else {
				getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("p.nomeMae", this.getNomeMae(), MatchMode.ANYWHERE));
			}
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Sigepen.formatarEspacos(nome);
    }

    public StatusVisitante getStatus() {
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