package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "ARQUIVO_JUSTIFICATIVA", schema = Sigepen.SEJUDH_SCHEMA)
public class ArquivoJustificativa implements Serializable {

	private static final long serialVersionUID = -6444576956364777332L;

	@SequenceGenerator(name = "SEQ_ARQUIVO_JUSTIFICATIVA_GENERATOR", sequenceName = "SEQ_ARQUIVO_JUSTIFICATIVA", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ARQUIVO_JUSTIFICATIVA_GENERATOR")
	@Column(name = "ID")
	private long id;

	@Column(name = "NOME", length = 30)
	private String nome;
	
	@Column(name = "CONTENT_TYPE", length = 30)
	private String contentType;

	@Lob
	@Column(name = "ARQUIVO", nullable = false)
	private byte[] arquivo;

	@Column(name = "DATA_INCLUSAO")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInclusao;
	
	@NotAudited
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "arquivoJustificativa", orphanRemoval = true, fetch = FetchType.LAZY)
	private Justificativa justificativa;

	public ArquivoJustificativa() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public byte[] getArquivo() {
		return arquivo;
	}

	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Date getDataInclusao() {
		return dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}
	
	public Justificativa getJustificativa() {
		return this.justificativa;
	}
	
	public void setJustificativa(Justificativa justificativa) {
		this.justificativa = justificativa;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(arquivo);
		result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result + ((dataInclusao == null) ? 0 : dataInclusao.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ArquivoJustificativa))
			return false;
		ArquivoJustificativa other = (ArquivoJustificativa) obj;
		if (!Arrays.equals(arquivo, other.arquivo))
			return false;
		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;
		if (dataInclusao == null) {
			if (other.dataInclusao != null)
				return false;
		} else if (!dataInclusao.equals(other.dataInclusao))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ArquivoJustificativa [id=" + id + "]";
	}
}
