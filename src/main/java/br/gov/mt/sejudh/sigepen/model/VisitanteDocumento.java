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
@Table(name = "SGP_VISITANTE_DOCUMENTO", schema = Sigepen.SEJUDH_SCHEMA)
public class VisitanteDocumento extends BaseModel<Long> {

    @Id
    @SequenceGenerator(name = "SIGEPEN_S_VISITANTE_DOCUMENTO", sequenceName = "SIGEPEN_S_VISITANTE_DOCUMENTO",
            initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_VISITANTE_DOCUMENTO")
    @Column(name = "ID_VISITANTE_DOCUMENTO")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "FLAG_VALIDADO")
    private SimNao validado;

    @ManyToOne
    @JoinColumn(name = "ID_VISITANTE")
    private Visitante visitante;

    @ManyToOne
    @JoinColumn(name = "ID_DOCUMENTO")
    private Documento documento;

    public VisitanteDocumento() {
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

    public void setId(Long id) {
        this.id = id;
    }

    public Visitante getVisitante() {
        return visitante;
    }

    public void setVisitante(Visitante visitante) {
        this.visitante = visitante;
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public SimNao getValidado() {
        return validado;
    }

    public void setValidado(SimNao obrigatorio) {
        this.validado = obrigatorio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VisitanteDocumento that = (VisitanteDocumento) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getVisitante(), that.getVisitante()) &&
                Objects.equals(getDocumento(), that.getDocumento());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getVisitante(), getDocumento());
    }
}
