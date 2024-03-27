package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "JUSTIFICATIVA", schema = Sigepen.SEJUDH_SCHEMA)
public class Justificativa implements Serializable {

	private static final long serialVersionUID = -689264275741833273L;

	@SequenceGenerator(name = "SEQ_JUSTIFICATIVA_GENERATOR", sequenceName = "SEQ_JUSTIFICATIVA", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_JUSTIFICATIVA_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Column(name = "DESCRICAO", nullable = false)
	private String descricao;
	
	@Column(name = "DATA_DESATIVACAO")
	private Date dataDesativacao = new Date();
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "FORNECEDOR_ID")
	private Fornecedor fornecedor;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "CONTRATO_ID")
	private Contrato contrato;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "ARQUIVO_JUSTIFICATIVA_ID")
	private ArquivoJustificativa arquivoJustificativa;
	
	public Justificativa() {
	}

	public Justificativa(long id, String descricao, Date dataDesativacao, Fornecedor fornecedor) {
		this.id = id;
		this.descricao = descricao;
		this.dataDesativacao = dataDesativacao;
		this.fornecedor = fornecedor;
	}

	public Justificativa (Long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getDescricao() {
		return this.descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = Sigepen.formatarEspacos(descricao);
	}
	
	public Date getDataDesativacao() {
		return this.dataDesativacao;
	}
	
	public void setDataDesativacao(Date dataDesativacao) {
		this.dataDesativacao = dataDesativacao;
	}
	
	public Fornecedor getFornecedor() {
		return this.fornecedor;
	}
	
	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
		
	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}
	
	public Contrato getContrato() {
		return this.contrato;
	}
	
	public void setArquivoJustificativa(ArquivoJustificativa arquivoJustificativa) {
		this.arquivoJustificativa = arquivoJustificativa;
	}
	
	public ArquivoJustificativa getArquivoJustificativa() {
		return this.arquivoJustificativa;
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
		if (getClass() != obj.getClass())
			return false;
		Justificativa other = (Justificativa) obj;
		if (id != other.id)
			return false;
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Contrato [id=" + id + ", numContrato = " + this.descricao + "]";
	}

}
