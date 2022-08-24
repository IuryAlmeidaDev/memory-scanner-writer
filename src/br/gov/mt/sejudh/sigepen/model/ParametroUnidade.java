package br.gov.mt.sejudh.sigepen.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.ParametroVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_PARAMETRO_UNIDADE", schema = Sigepen.SEJUDH_SCHEMA)
public class ParametroUnidade extends BaseModel<Long> {

    @Id
    @SequenceGenerator(name = "SIGEPEN_S_PARAMETRO_UNIDADE", sequenceName = "SIGEPEN_S_PARAMETRO_UNIDADE",
            allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_PARAMETRO_UNIDADE")
    @Column(name = "ID_PARAMETRO_UNIDADE")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "NOME_PARAMETRO")
    private ParametroVisitante nome;

    @Column(name = "DESC_PARAMETRO")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "FLAG_VALIDAR_PARAMETRO")
    private SimNao validar;

    @ManyToOne
    @JoinColumn(name = "ID_UNIDADE_PRISIONAL")
    private UnidadePrisional unidadePrisional;

    @Transient
    private Boolean valueValidar;

    public ParametroUnidade() {
        this.valueValidar  = false;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParametroVisitante getNome() {
        return nome;
    }

    public void setNome(ParametroVisitante nome) {
        this.nome = nome;
    }

    public SimNao getValidar() {
        return validar;
    }

    public void setValidar(SimNao validar) {
        this.validar = validar;
    }

    public UnidadePrisional getUnidadePrisional() {
        return unidadePrisional;
    }

    public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
        this.unidadePrisional = unidadePrisional;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao =  Sigepen.formatarEspacos(descricao);
    }

    public Boolean getValueValidar() {
        return valueValidar;
    }

    public void setValueValidar(Boolean valueValidar) {
        this.valueValidar = valueValidar;
    }
}
