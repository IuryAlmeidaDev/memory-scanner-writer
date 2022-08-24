package br.gov.mt.sejudh.sigepen.base.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Transient;

import br.gov.mt.sejudh.sigepen.model.AutoriaRegistro;

public abstract class BaseModel<I extends Number> implements Serializable {

	private static final long serialVersionUID = 6903375347901033706L;

	public abstract I getId();

    @Transient
    private AutoriaRegistro autorRegistro;

    @Transient
    private AutoriaRegistro autorUltimaAlteracao;

    public AutoriaRegistro getAutorRegistro() {
        return autorRegistro;
    }

    public void setAutorRegistro(AutoriaRegistro autorRegistro) {
        this.autorRegistro = autorRegistro;
    }

    public AutoriaRegistro getAutorUltimaAlteracao() {
        return autorUltimaAlteracao;
    }

    public void setAutorUltimaAlteracao(AutoriaRegistro autorUltimaAlteracao) {
        this.autorUltimaAlteracao = autorUltimaAlteracao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseModel<?> that = (BaseModel<?>) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
