package br.gov.mt.sejudh.sigepen.model;

import java.util.Objects;

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

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_VISITANTE_CERTIDAO", schema = Sigepen.SEJUDH_SCHEMA)
public class VisitanteCertidao extends BaseModel<Long> {

    @Id
    @SequenceGenerator(name = "SIGEPEN_S_VISITANTE_CERTIDAO", sequenceName = "SIGEPEN_S_VISITANTE_CERTIDAO",
            initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_VISITANTE_CERTIDAO")
    @Column(name = "ID_VISITANTE_CERTIDAO")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "FLAG_ENTREGUE")
    private SimNao entregue;

    @Enumerated(EnumType.STRING)
    @Column(name = "FLAG_OBRIGATORIO")
    private SimNao obrigatorio;

    @Enumerated(EnumType.STRING)
    @Column(name = "FLAG_VALIDADO")
    private SimNao validado;

    @ManyToOne
    @JoinColumn(name = "ID_VISITANTE")
    private Visitante visitante;

    @ManyToOne
    @JoinColumn(name = "ID_CERTIDAO")
    private Certidao certidao;

    public VisitanteCertidao() {
        this.validado = SimNao.N;
    }

    public Boolean getValueValidado() {
        return this.validado == SimNao.S;
    }

    public void setValueValidado(Boolean valueValidado) {
        if (valueValidado != null && valueValidado) {
            this.validado = SimNao.S;
            return;
        }
        this.validado = SimNao.N;
    }
    @Override
    public Long getId() {
        return id;
    }

    public SimNao getValidado() {
        return validado;
    }

    public void setValidado(SimNao validado) {
        this.validado = validado;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Visitante getVisitante() {
        return visitante;
    }

    public void setVisitante(Visitante visitante) {
        this.visitante = visitante;
    }

    public Certidao getCertidao() {
        return certidao;
    }

    public void setCertidao(Certidao certidao) {
        this.certidao = certidao;
    }

    public SimNao getEntregue() {
        return entregue;
    }

    public void setEntregue(SimNao entregue) {
        this.entregue = entregue;
    }

    public SimNao getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(SimNao obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VisitanteCertidao that = (VisitanteCertidao) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getVisitante(), that.getVisitante()) &&
                Objects.equals(getCertidao(), that.getCertidao());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getVisitante(), getCertidao());
    }
}
