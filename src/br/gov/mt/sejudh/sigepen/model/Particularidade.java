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
import br.gov.mt.sejudh.sigepen.enumerator.OrientacaoParticularidade;
import br.gov.mt.sejudh.sigepen.enumerator.RegiaoParticularidade;
import br.gov.mt.sejudh.sigepen.enumerator.TipoParticularidade;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name="PARTICULARIDADE", schema = Sigepen.SEJUDH_SCHEMA)
public class Particularidade implements Serializable, Comparable<Particularidade> {
	
	private static final long serialVersionUID = 1010523348798586426L;

	@SequenceGenerator(name = "SEQ_PARTICULARIDADE_GENERATOR", sequenceName = "SEQ_PARTICULARIDADE", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PARTICULARIDADE_GENERATOR")
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
	private TipoParticularidade tipo;
	
	@Column(name = "ORIENTACAO", nullable=false)
	@Convert(converter = EnumCustomConverter.class, disableConversion = true)
	@Type(type = "br.gov.mt.sejudh.sigepen.conversores.EnumCustomType")
	private OrientacaoParticularidade orientacao;
	
	@Column(name = "REGIAO", nullable=false)
	@Convert(converter = EnumCustomConverter.class, disableConversion = true)
	@Type(type = "br.gov.mt.sejudh.sigepen.conversores.EnumCustomType")
	private RegiaoParticularidade regiao;
	
	@Column(name = "DATA_INCLUSAO")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInclusao;

    @NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "IMAGEM_ID")
	private Imagem imagem;
	
	public Particularidade() {

	}
	
	@Override
	public int compareTo(Particularidade p) {
		if (this.id > p.getId()) return -1;
		if (this.id < p.getId()) return 1;
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

	public TipoParticularidade getTipo() {
		return tipo;
	}
	
	public void setTipo(TipoParticularidade tipo) {
		this.tipo = tipo;
	}
	
	public OrientacaoParticularidade getOrientacao() {
		return orientacao;
	}

	public void setOrientacao(OrientacaoParticularidade orientacao) {
		this.orientacao = orientacao;
	}

	public RegiaoParticularidade getRegiao() {
		return regiao;
	}

	public void setRegiao(RegiaoParticularidade regiao) {
		this.regiao = regiao;
	}
	
	public Date getDataInclusao() {
		return dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
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
		result = prime * result
				+ ((dataInclusao == null) ? 0 : dataInclusao.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((imagem == null) ? 0 : imagem.hashCode());
		result = prime * result
				+ ((orientacao == null) ? 0 : orientacao.hashCode());
		result = prime * result
				+ ((recuperando == null) ? 0 : recuperando.hashCode());
		result = prime * result + ((regiao == null) ? 0 : regiao.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Particularidade))
			return false;
		Particularidade other = (Particularidade) obj;
		if (dataInclusao == null) {
			if (other.dataInclusao != null)
				return false;
		} else if (!dataInclusao.equals(other.dataInclusao))
			return false;
		if (id != other.id)
			return false;
		if (imagem == null) {
			if (other.imagem != null)
				return false;
		} else if (!imagem.equals(other.imagem))
			return false;
		if (orientacao != other.orientacao)
			return false;
		if (recuperando == null) {
			if (other.recuperando != null)
				return false;
		} else if (!recuperando.equals(other.recuperando))
			return false;
		if (regiao != other.regiao)
			return false;
		if (tipo != other.tipo)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Particularidade [id=" + id + "]";
	}
}
