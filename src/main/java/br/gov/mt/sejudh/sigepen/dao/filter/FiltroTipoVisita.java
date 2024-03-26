package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;

public class FiltroTipoVisita extends FiltroConsulta {

	private static final long serialVersionUID = -9179646283599339470L;

	private String descricao;

    private TipoEntrada tipoEntrada;

    private SimNao obrigatorioCredenciamento;

    private SimNao estaNoRolDeParentes;

    private SimNao obrigatorioRecuperando;

    private SgpStatus status;

    @Override
    public void configurarFiltros() {
        if(this.getDescricao()!= null && !this.getDescricao().equalsIgnoreCase("") ){
            getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("descricao", this.getDescricao(), MatchMode.ANYWHERE));
        }

        if (this.getTipoEntrada() != null) {
            getCriteria().add(Restrictions.eq("tipoEntrada", this.getTipoEntrada()));
        }

        if (this.getObrigatorioCredenciamento() != null) {
            getCriteria().add(Restrictions.eq("obrigatorioCredenciamento", this.getObrigatorioCredenciamento()));
        }

        if (this.getEstaNoRolDeParentes() != null) {
            getCriteria().add(Restrictions.eq("estaNoRolDeParentes", this.getEstaNoRolDeParentes()));
        }

        if (this.getObrigatorioRecuperando() != null) {
            getCriteria().add(Restrictions.eq("obrigatorioRecuperando", this.getObrigatorioRecuperando()));
        }

        if (this.getStatus() != null) {
            getCriteria().add(Restrictions.eq("status", this.getStatus()));
        }
    }

    public String getDescricao(){
        return this.descricao;
    }

    public TipoEntrada getTipoEntrada(){
        return this.tipoEntrada;
    }

    public SimNao getObrigatorioCredenciamento(){
        return this.obrigatorioCredenciamento;
    }

    public SimNao getEstaNoRolDeParentes(){
        return this.estaNoRolDeParentes;
    }

    public SgpStatus getStatus(){
        return this.status;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setTipoEntrada(TipoEntrada tipoEntrada) {
        this.tipoEntrada = tipoEntrada;
    }

    public void setObrigatorioCredenciamento(SimNao obrigatorioCredenciamento) {
        this.obrigatorioCredenciamento = obrigatorioCredenciamento;
    }

    public void setEstaNoRolDeParentes(SimNao estaNoRolDeParentes) {
        this.estaNoRolDeParentes = estaNoRolDeParentes;
    }

    public SimNao getObrigatorioRecuperando() {
        return obrigatorioRecuperando;
    }

    public void setObrigatorioRecuperando(SimNao obrigatorioRecuperando) {
        this.obrigatorioRecuperando = obrigatorioRecuperando;
    }

    public void setStatus(SgpStatus status) {
        this.status = status;
    }
}
