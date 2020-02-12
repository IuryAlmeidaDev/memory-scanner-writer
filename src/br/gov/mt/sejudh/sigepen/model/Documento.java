package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "DOCUMENTO", schema = Sigepen.SEJUDH_SCHEMA)
public class Documento implements Serializable {

	private static final long serialVersionUID = 4460285607063881059L;
	
	@SequenceGenerator(name = "SEQ_DOCUMENTO_GENERATOR", sequenceName = "SEQ_DOCUMENTO", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOCUMENTO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "PESSOA_ID")
	private Pessoa pessoa;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "SERVIDOR_ID")
	private Servidor servidor;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "RECUPERANDO_ID")
	private Recuperando recuperando;
	
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "TIPO_DOCUMENTO_ID", nullable = false)
	private TipoDocumento tipoDocumento;
	
	@Column(name="NUMERO", length = 20)
	private String numero;
	
	@Column(name = "DATA_EMISSAO")
	@Temporal(TemporalType.DATE)
	private Date dataEmissao;
	
	@Column(name = "DATA_VALIDADE")
	@Temporal(TemporalType.DATE)
	private Date dataValidade;
	
	@Column(name="DESCRICAO", length = 250)
	private String descricao;	
	
	public Documento() {
	}
	
	public Documento(Documento doc) {
		this.id = doc.getId();
		this.servidor = doc.getServidor();
		this.pessoa = doc.getPessoa();
		this.recuperando = doc.getRecuperando();
		this.tipoDocumento = doc.getTipoDocumento();
		this.numero = doc.getNumero();
		this.dataEmissao = doc.getDataEmissao();
		this.dataValidade = doc.getDataValidade();
		this.setDescricao(doc.getDescricao());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TipoDocumento getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = Sigepen.formatarEspacos(numero);
	}

	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public Date getDataValidade() {
		return dataValidade;
	}

	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = Sigepen.formatarEspacos(descricao);
	}	

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		result = prime * result
				+ ((tipoDocumento == null) ? 0 : tipoDocumento.hashCode());
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
		Documento other = (Documento) obj;
		if (id != other.id)
			return false;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		if (tipoDocumento == null) {
			if (other.tipoDocumento != null)
				return false;
		} else if (!tipoDocumento.equals(other.tipoDocumento))
			return false;
		return true;
	}	

	@Override
	public String toString() {
		
		String titular = "";
		
		if(pessoa != null){
			if(pessoa.getId()>0){
				titular = ", Titular=" + pessoa.toString();				
			} else{
				titular = ", Titular=novo Titular";
			}
		}else if(recuperando != null){
			if(recuperando.getId()>0){
				titular = ", Titular=" + recuperando.toString();				
			} else{
				titular = ", Titular=novo Titular";
			}			
		}else if(servidor != null ){
			if(servidor.getId()>0){
				titular = ", Titular=" + servidor.toString();
			}else{
				titular = ", Titular=novo Titular";				
			}
		}
		
		return "Documento [id=" + id +", Tipo=" + tipoDocumento.getDescricao() + ", numero=" + this.getNumero() + titular + "]";
	}
}
