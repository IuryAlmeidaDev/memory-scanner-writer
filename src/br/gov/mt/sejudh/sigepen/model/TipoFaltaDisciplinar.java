package br.gov.mt.sejudh.sigepen.model;

import br.gov.mt.sejudh.sigepen.enumerator.NaturezaFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Audited
@Entity
@Table(name = "SGP_TIPO_FALTA_DISCIPLINAR", schema = Sigepen.SEJUDH_SCHEMA)
public class TipoFaltaDisciplinar implements Serializable {

	@SequenceGenerator(name = "SIGEPEN_S_TIPO_FALTA_DISCIPINA_GENERATOR", sequenceName = "SIGEPEN_S_TIPO_FALTA_DISCIPINA", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_TIPO_FALTA_DISCIPINA_GENERATOR")
	@Column(name = "ID_TIPO_FALTA_DISCIPLINAR")
	private long id;

	@Column(name = "DESC_TIPO_FALTA_DISCIPLINAR", length = 250, nullable = false)
	private String descricao;

	@Enumerated(EnumType.STRING)
	@Column(name = "CODG_NAT_TPO_FALTA_DISCIPLINAR", length = 10, nullable = false)
	private NaturezaFaltaDisciplinar natureza;

	@Enumerated
	@Column(name = "STAT_TIPO_FALTA_DISCIPLINAR")
	private Status status;

	@Transient
	private AutoriaRegistro autorRegistro;

	@Transient
	private AutoriaRegistro autorUltimaAlteracao;

	public TipoFaltaDisciplinar(){
	}

	public TipoFaltaDisciplinar(Status status){
		this.setStatus(status);	
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

	public void setDescricao(String nome) {
		this.descricao = Sigepen.formatarEspacos(nome);
	}

	public NaturezaFaltaDisciplinar getNatureza() {
		return natureza;
	}

	public void setNatureza(NaturezaFaltaDisciplinar natureza) {
		this.natureza = natureza;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

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
		TipoFaltaDisciplinar other = (TipoFaltaDisciplinar) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TipoFaltaDisciplinar{" +
				"id=" + id +
				", descricao='" + descricao + '\'' +
				", natureza=" + natureza +
				", status=" + status +
				'}';
	}
}
