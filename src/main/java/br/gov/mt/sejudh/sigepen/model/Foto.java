package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.conversores.EnumCustomConverter;
import br.gov.mt.sejudh.sigepen.enumerator.TipoFoto;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name="FOTO", schema = Sigepen.SEJUDH_SCHEMA)
public class Foto implements Comparable<Foto>, Serializable {

	private static final long serialVersionUID = -59636397175074101L;

	@SequenceGenerator(name = "SEQ_FOTO_GENERATOR", sequenceName = "SEQ_FOTO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FOTO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private Long versao;
	
	@ManyToOne(optional = false, fetch=FetchType.LAZY)
	@JoinColumn(name = "RECUPERANDO_ID")
	private Recuperando recuperando;
	
	@Column(name = "TIPO", nullable=false)
	@Convert(converter = EnumCustomConverter.class, disableConversion = true)
	@Type(type = "br.gov.mt.sejudh.sigepen.conversores.EnumCustomType")
	private TipoFoto tipo;
	
	@Column(name = "DATA_INCLUSAO")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInclusao;
	
	@Column(name="IS_ATUAL")
	private Boolean isAtual;

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "IMAGEM_ID")
	private Imagem imagem;
	
	public Foto() {

	}
	
	@Override
	public int compareTo(Foto f) {
		if (this.id > f.getId()) return -1;
		if (this.id < f.getId()) return 1;
		return 0;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
	}

	public TipoFoto getTipo() {
		return tipo;
	}
	
	public void setTipo(TipoFoto tipo) {
		this.tipo = tipo;
	}
	
	public Date getDataInclusao() {
		return dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}
	
	public Boolean getIsAtual() {
		return isAtual;
	}

	public void setIsAtual(Boolean isAtual) {
		this.isAtual = isAtual;
	}

	public Imagem getImagem() {
		return imagem;
	}

	public void setImagem(Imagem imagem) {
		this.imagem = imagem;
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
		if (obj == null)
			return false;
		if (!(obj instanceof Foto))
			return false;
		Foto other = (Foto) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Foto [id=" + id + ", Tipo=" + tipo +  "]";
	}
}
