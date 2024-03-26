package br.gov.mt.sejudh.sigepen.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoFoto;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_VISITANTE_FOTO", schema = Sigepen.SEJUDH_SCHEMA)
public class VisitanteFoto extends BaseModel<Long> {

    @Id
    @SequenceGenerator(name = "SIGEPEN_S_VISITANTE_FOTO", sequenceName = "SIGEPEN_S_VISITANTE_FOTO",
            initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_VISITANTE_FOTO")
    @Column(name = "ID_VISITANTE_FOTO")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_VISITANTE")
    private Visitante visitante;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_FOTO", nullable=false)
    private TipoFoto tipo;

    @Enumerated(EnumType.STRING)
    @Column(name="FOTO_ATUAL")
    private SimNao fotoAtual;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name = "ID_IMAGEM")
    private Imagem imagem;

    public boolean isAtual() {
        return this.fotoAtual == SimNao.S;
    }

    public void setAtual(boolean atual) {
        if (atual) {
            this.fotoAtual = SimNao.S;
            return;
        }
        this.fotoAtual = SimNao.N;
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

    public TipoFoto getTipo() {
        return tipo;
    }

    public void setTipo(TipoFoto tipo) {
        this.tipo = tipo;
    }

    public SimNao getFotoAtual() {
        return fotoAtual;
    }

    public void setFotoAtual(SimNao fotoAtua) {
        this.fotoAtual = fotoAtua;
    }

    public Imagem getImagem() {
        return imagem;
    }

    public void setImagem(Imagem imagem) {
        this.imagem = imagem;
    }
}
