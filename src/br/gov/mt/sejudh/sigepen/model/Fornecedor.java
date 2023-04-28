package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "FORNECEDOR", schema = Sigepen.SEJUDH_SCHEMA)
public class Fornecedor implements Serializable {

	private static final long serialVersionUID = -689264275741833273L;

	@SequenceGenerator(name = "SEQ_FORNECEDOR_GENERATOR", sequenceName = "SEQ_FORNECEDOR", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FORNECEDOR_GENERATOR")
	@Column(name = "ID")
	private long id;

	@Column(name = "RAZAO_SOCIAL", length = 150, nullable = false)
	private String razaoSocial;

	@Column(name = "NOME_FANTAZIA", length = 150)
	private String nomeFantasia;

	@Column(name = "CNPJ", length = 14)
	private String cnpj;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "fornecedor", fetch = FetchType.LAZY)
	private Endereco endereco;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "fornecedor", fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	private List<Contato> contatos;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONTRATO_ID")
	private Contrato contrato;

	public Fornecedor() {
		this.contatos = new ArrayList<Contato>();
	}

	public Fornecedor(long id, String razaoSocial, String nomeFantasia, String cnpj) {
		this.contatos = new ArrayList<Contato>();
		this.id = id;
		this.razaoSocial = razaoSocial;
		this.nomeFantasia = nomeFantasia;
		this.cnpj = cnpj;
	}

	public Fornecedor(Long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getrazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public Endereco getEndereco() {
		return this.endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public List<Contato> getContatos() {
		return this.contatos;
	}

	public void setContatos(List<Contato> contatos) {
		this.contatos = contatos;
	}

	@Override
	public String toString() {
		return "Fornecedor [id=" + id + ", nome=" + nomeFantasia + "]";
	}
}
