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

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_CREDENCIAL_DOCUMENTO", schema = Sigepen.SEJUDH_SCHEMA)
public class CredencialDocumento extends BaseModel<Long> {

	private static final long serialVersionUID = 6840178915888578077L;

	@Id
    @SequenceGenerator(name = "SGP_S_CREDENCIAL_DOCUMENTO", sequenceName = "SGP_S_CREDENCIAL_DOCUMENTO",
            allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SGP_S_CREDENCIAL_DOCUMENTO")
    @Column(name = "ID_CREDENCIAL_DOCUMENTO")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "FLAG_VALIDADO")
    private SimNao validado;

    @ManyToOne
    @JoinColumn(name = "ID_FASE_CREDENCIAMENTO")
    private FaseDoCredenciamento faseDoCredenciamento;

    @ManyToOne
    @JoinColumn(name = "ID_VISITANTE_DOCUMENTO")
    private VisitanteDocumento visitanteDocumento;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SimNao getValidado() {
        return validado;
    }

    public void setValidado(SimNao validado) {
        this.validado = validado;
    }

    public FaseDoCredenciamento getFaseDoCredenciamento() {
        return faseDoCredenciamento;
    }

    public void setFaseDoCredenciamento(FaseDoCredenciamento faseCredenciamento) {
        this.faseDoCredenciamento = faseCredenciamento;
    }

    public VisitanteDocumento getVisitanteDocumento() {
        return visitanteDocumento;
    }

    public void setVisitanteDocumento(VisitanteDocumento visitanteDocumento) {
        this.visitanteDocumento = visitanteDocumento;
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
}
