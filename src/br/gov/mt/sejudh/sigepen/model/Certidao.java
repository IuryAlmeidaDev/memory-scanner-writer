package br.gov.mt.sejudh.sigepen.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_CERTIDAO", schema = Sigepen.SEJUDH_SCHEMA)
public class Certidao extends BaseModel<Long> {

	private static final long serialVersionUID = 380083765463658864L;

	@Id
    @SequenceGenerator(name = "SIGEPEN_S_CERTIDAO", sequenceName = "SIGEPEN_S_CERTIDAO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_CERTIDAO")
    @Column(name = "ID_CERTIDAO")
    private Long id;

    @Column(name = "DESC_CERTIDAO")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "STAT_CERTIDAO")
    private SgpStatus status;

    public Certidao() {
        this.status = SgpStatus.A;
    }

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

    public SgpStatus getStatus() {
        return status;
    }

    public void setStatus(SgpStatus status) {
        this.status = status;
    }

	@Override
	public String toString() {
		return "Certidao [id=" + id + ", descricao=" + descricao + ", status=" + status + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Certidao other = (Certidao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
