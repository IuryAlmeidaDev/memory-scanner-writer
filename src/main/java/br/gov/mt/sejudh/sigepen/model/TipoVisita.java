package br.gov.mt.sejudh.sigepen.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.conversores.EnumCustomConverter;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_TIPO_VISITA", schema = Sigepen.SEJUDH_SCHEMA)
public class TipoVisita extends BaseModel<Long> {

	private static final long serialVersionUID = 6531889864468818993L;

	@Id
    @SequenceGenerator(name = "SIGEPEN_S_TIPO_VISITA", sequenceName = "SIGEPEN_S_TIPO_VISITA", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_TIPO_VISITA")
    @Column(name = "ID_TIPO_VISITA")
    private Long id;

    @Column(name = "DESC_TIPO_VISITA", length = 55, nullable = false)
    private String descricao;

    @Convert(converter = EnumCustomConverter.class, disableConversion = true)
    @Type(type = "br.gov.mt.sejudh.sigepen.conversores.EnumCustomType")
    @Column(name = "CODG_TIPO_ENTRADA")
    private TipoEntrada tipoEntrada;

    @Enumerated(EnumType.STRING)
    @Column(name = "FLAG_OBRIGATORIO_CREDENCIAL", nullable = false)
    private SimNao obrigatorioCredenciamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "STAT_TIPO_VISITA")
    private SgpStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "FLAG_ESTA_NO_ROL_DE_PARENTES", nullable = false)
    private SimNao estaNoRolDeParentes;

    @Enumerated(EnumType.STRING)
    @Column(name = "FLAG_OBRIGATORIO_RECUPERANDO", nullable = false)
    private SimNao obrigatorioRecuperando;

    @OneToMany(mappedBy = "tipoVisita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoVisitaDocumento> tipoVisitaDocumento;

    @OneToMany(mappedBy = "tipoVisita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoVisitaCertidao> tipoVisitaCertidao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao =  Sigepen.formatarEspacos(descricao);
    }

    public TipoEntrada getTipoEntrada() {
        return tipoEntrada;
    }

    public void setTipoEntrada(TipoEntrada codgTipoEntrada) {
        this.tipoEntrada = codgTipoEntrada;
    }

    public SimNao getObrigatorioCredenciamento() {
        return obrigatorioCredenciamento;
    }

    public void setObrigatorioCredenciamento(SimNao obrigatorioCredenciamento) {
        this.obrigatorioCredenciamento = obrigatorioCredenciamento;
    }

    public SgpStatus getStatus() {
        return status;
    }

    public void setStatus(SgpStatus status) {
        this.status = status;
    }

    public SimNao getEstaNoRolDeParentes() {
        return estaNoRolDeParentes;
    }

    public void setEstaNoRolDeParentes(SimNao estaNoRolDeParentes) {
        this.estaNoRolDeParentes = estaNoRolDeParentes;
    }

    public List<TipoVisitaDocumento> getTipoVisitaDocumento() {
        return tipoVisitaDocumento;
    }

    public SimNao getObrigatorioRecuperando() {
        return obrigatorioRecuperando;
    }

    public void setObrigatorioRecuperando(SimNao obrigatorioRecuperando) {
        this.obrigatorioRecuperando = obrigatorioRecuperando;
    }

    public void setTipoVisitaDocumento(List<TipoVisitaDocumento> tipoVisitaDocumento) {
        this.tipoVisitaDocumento = tipoVisitaDocumento;
    }

    public List<TipoVisitaCertidao> getTipoVisitaCertidao() {
        return tipoVisitaCertidao;
    }

    public void setTipoVisitaCertidao(List<TipoVisitaCertidao> tipoVisitaCertidao) {
        this.tipoVisitaCertidao = tipoVisitaCertidao;
    }

	@Override
	public String toString() {
		return "TipoVisita [id=" + id + ", descricao=" + descricao + "]";
	}
    
    
}
