package br.gov.mt.sejudh.sigepen.dao.filter;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroImpedimento extends FiltroConsulta {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String cpf;
    private SgpStatus status;
    private Visitante visitante;

    @Override
    public void configurarFiltros() {
        if (StringUtils.isNotBlank(this.getNome()) || StringUtils.isNotBlank(this.getCpf())) {
            this.configurarFiltrosPessoa();
        }

        if (getVisitante() != null) {
            getCriteria().add(Restrictions.eq("visitante", this.getVisitante()));
        }

        if (this.getStatus() != null) {
            getCriteria().add(Restrictions.eq("status", this.getStatus()));
        }
    }

    private void configurarFiltrosPessoa() {
        Criteria criteriaVisitante = getCriteria().createAlias("visitante", "v", JoinType.INNER_JOIN);
        Criteria criteriaPessoa = criteriaVisitante.createAlias("v.pessoa", "p", JoinType.INNER_JOIN);

        if (StringUtils.isNotBlank(this.getNome())) {
            criteriaPessoa.add(RestrictionsSigepen.ignoraAcentoLike("p.nome", this.getNome(), MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(this.getCpf())) {
            Criteria criteriaDocumento = criteriaPessoa.createAlias("p.documentos", "doc", JoinType.INNER_JOIN);

            criteriaDocumento.add(Restrictions.like("doc.numero", this.getCpf(), MatchMode.ANYWHERE));
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Sigepen.formatarEspacos(nome);
    }

    public SgpStatus getStatus() {
        return status;
    }

    public void setStatus(SgpStatus status) {
        this.status = status;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Visitante getVisitante() {
        return visitante;
    }

    public void setVisitante(Visitante visitante) {
        this.visitante = visitante;
    }
}
