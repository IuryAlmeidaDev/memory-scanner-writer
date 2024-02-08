package br.gov.mt.sejudh.sigepen.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "FORNECEDOR", schema = Sigepen.SEJUDH_SCHEMA)
public class Fornecedor extends BaseModel<Long> {

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

	@Enumerated
	@Column(name = "STATUS")
	private Status status;
	
	@Column(name = "CREADOR")
	private String creador;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "fornecedor", fetch = FetchType.LAZY)
	private Endereco endereco;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "fornecedor", fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	private List<Contato> contatos;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "fornecedor")
	private List<Contrato> contratos;
	
	@NotAudited
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "fornecedor", orphanRemoval = true, fetch = FetchType.LAZY)
	private Justificativa justificativa;

	public Fornecedor() {
		this.justificativa = new Justificativa();
		this.status = Status.ATIVO;
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

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getrazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = Sigepen.formatarEspacos(razaoSocial);
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = Sigepen.formatarEspacos(nomeFantasia);
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

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getCreador() {
		return this.creador;
	}
	
	public void setCreador(String creador) {
		this.creador = Sigepen.formatarEspacos(creador);
	}
	
	public List<Contato> getContratos() {
		return this.contatos;
	}

	public void setContratos(List<Contrato> contratos) {
		this.contratos = contratos;
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
		Fornecedor other = (Fornecedor) obj;
		if (id != other.id)
			return false;
		if (cnpj == null) {
			if (other.cnpj != null)
				return false;
		} else if (!cnpj.equals(other.cnpj))
			return false;
		if (razaoSocial == null) {
			if (other.razaoSocial != null)
				return false;
		} else if (!razaoSocial.equals(other.razaoSocial))
			return false;
		if (nomeFantasia == null) {
			if (other.nomeFantasia != null)
				return false;
		} else if (!nomeFantasia.equals(other.nomeFantasia))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Fornecedor [id=" + id + ", nome=" + nomeFantasia + ", cnpj " + cnpj + " ]";
	}

}
