package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroVisitante extends FiltroConsulta {

    private static final long serialVersionUID = 1L;
    private String nome;
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

    @Override
    public void configurarFiltros() {
        Criteria criteriaCredenciamentos = getCriteria().createAlias("credenciamentos", "vr", JoinType.LEFT_OUTER_JOIN);
        Criteria criteriaCredenciamento = criteriaCredenciamentos.createAlias(
                "vr.credenciamentos", "credenciamento", JoinType.LEFT_OUTER_JOIN,
                Restrictions.and(Restrictions.ne("credenciamento.statusCredenciamento", StatusCredenciamento.INATIVO))
        );

        if (super.isProjetarDados()) {
            this.configurarProjections();
        }

        if (StringUtils.isNotBlank(this.getNome()) || StringUtils.isNotBlank(this.getCpf())
                || CollectionUtils.isNotEmpty(this.getPessoasIds()) || this.getDataNascimento() != null) {
            this.configurarFiltrosPessoa();
        }

        if (this.getStatus() != null) {
            getCriteria().add(Restrictions.eq("status", this.getStatus()));
        }

        if (this.getUnidadePrisional() != null) {
            criteriaCredenciamentos.add(Restrictions.eq("vr.unidadePrisional", this.getUnidadePrisional()));
        }

        if (this.getSituacaoCredenciamento() != null) {
            criteriaCredenciamento.add(Restrictions.eq("credenciamento.statusCredenciamento", this.getSituacaoCredenciamento()));
        }

        if (this.getFaseCredenciamento() != null) {
            criteriaCredenciamento.add(Restrictions.eq("credenciamento.faseCredenciamento", this.getFaseCredenciamento()));
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
    }

    private void configurarProjections() {
        getCriteria().createAlias(
                "impedimentos", "impedimento", JoinType.LEFT_OUTER_JOIN
        );

        ProjectionList proj = Projections.projectionList()
                .add(Projections.groupProperty("id").as("id"))
                .add(Projections.groupProperty("pessoa").as("pessoa"))
                .add(Projections.groupProperty("tipoVisita").as("tipoVisita"))
                .add(Projections.groupProperty("dataHoraCadastro").as("dataHoraCadastro"))
                .add(Projections.groupProperty("status").as("status"))
                .add(Projections.max("credenciamento.dataAprovacao").as("dataAprovacao"))
                .add(Projections.max("credenciamento.faseCredenciamento").as("faseCredenciamento"))
                .add(Projections.count("credenciamento.id").as("credenciamentosAtivos"))
                .add(Projections.max("impedimento.fimImpedimento").as("numImpedimento"))
                ;

        getCriteria().setProjection(proj);
    }

    public FiltroVisitante() {
        this.pessoasIds = new ArrayList<>();
    }

    public FiltroVisitante(FiltroVwVisitante filtroVwVisitante) {
        this.nome = filtroVwVisitante.getNome();
        this.cpf = filtroVwVisitante.getCpf();
        this.recuperando = filtroVwVisitante.getRecuperando();
        this.dataNascimento = filtroVwVisitante.getDataNascimento();
        this.status = filtroVwVisitante.getStatus();
        this.unidadePrisional = filtroVwVisitante.getUnidadePrisional();
        this.faseCredenciamento = filtroVwVisitante.getFaseCredenciamento();
        this.situacaoCredenciamento = filtroVwVisitante.getSituacaoCredenciamento();
        this.tipoVisita = filtroVwVisitante.getTipoVisita();
        this.responsavel = filtroVwVisitante.getResponsavel();
        this.pessoasIds = filtroVwVisitante.getPessoasIds();
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

    public FaseCredenciamento getFaseCredenciamento() {
        return faseCredenciamento;
    }

    public void setFaseCredenciamento(FaseCredenciamento faseCredenciamento) {
        this.faseCredenciamento = faseCredenciamento;
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
}
