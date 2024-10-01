package br.gov.mt.sejudh.sigepen.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "ITEM_CARDAPIO", schema = Sigepen.SEJUDH_SCHEMA)
public class ItemCardapio extends BaseModel<Long>{

	private static final long serialVersionUID = 1972235683092346075L;
	
	@SequenceGenerator(name = "SEQ_ITEM_CARDAPIO_GENERATOR", sequenceName = "SEQ_ITEM_CARDAPIO", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ITEM_CARDAPIO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@OneToOne(optional = false)
	@JoinColumn(name = "ALIMENTO_ID")
	private Alimento alimento;

	@Column(name = "STATUS", nullable = false)
	private Status status;
	
	@Column(name = "MASC_MG", nullable = false)
	private Double masc;
	
	@Column(name = "FEM_MG", nullable = false)
	private Double fem;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CARDAPIO_ID")
	private Cardapio cardapio;

	public ItemCardapio() {
		this.status = Status.ATIVO;
	}
	
	@Override
	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Alimento getAlimento() {
		return alimento;
	}

	public void setAlimento(Alimento alimento) {
		this.alimento = alimento;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Double getMasc() {
		return masc;
	}

	public void setMasc(Double masc) {
		this.masc = masc;
	}

	public Double getFem() {
		return fem;
	}

	public void setFem(Double fem) {
		this.fem = fem;
	}

	public Cardapio getCardapio() {
		return cardapio;
	}

	public void setCardapio(Cardapio cardapio) {
		this.cardapio = cardapio;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		ItemCardapio other = (ItemCardapio) obj;
		if (id != other.id)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TipoDieta [id=" + id + ", status=" + status + "]";
	}

}
