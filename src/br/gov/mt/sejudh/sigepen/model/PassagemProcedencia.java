package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;

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

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Table(name = "PASSAGEM_PROCEDENCIA", schema=Sigepen.SEJUDH_SCHEMA)
public class PassagemProcedencia implements Serializable{

	private static final long serialVersionUID = 2812545564446839430L;

	@SequenceGenerator(name = "SEQ_PASSAGEM_PROCEDENCIA_GENERATOR", sequenceName = "SEQ_PASSAGEM_PROCEDENCIA", allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PASSAGEM_PROCEDENCIA_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@ManyToOne
	@JoinColumn(name="PROCEDENCIA_ID")
	private Estabelecimento procedencia;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PASSAGEM_ID")
	private Passagem passagem;
	
	@Column(name = "OBSERVACAO", length = 250)
	private String observacao;
	
	public PassagemProcedencia(){
	}
	
	public PassagemProcedencia(PassagemProcedencia passagemProcedencia){
		super();
		this.id 		 = passagemProcedencia.getId();
		this.setObservacao(passagemProcedencia.getObservacao());
		this.passagem 	 = passagemProcedencia.getPassagem();
		this.procedencia = passagemProcedencia.getProcedencia();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Estabelecimento getProcedencia() {
		return procedencia;
	}

	public void setProcedencia(Estabelecimento procedencia) {
		this.procedencia = procedencia;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = Sigepen.formatarEspacos(observacao);
	}

	public Passagem getPassagem() {
		return passagem;
	}

	public void setPassagem(Passagem passagem) {
		this.passagem = passagem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((passagem == null) ? 0 : passagem.hashCode());
		result = prime * result
				+ ((procedencia == null) ? 0 : procedencia.hashCode());
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
		PassagemProcedencia other = (PassagemProcedencia) obj;
		if (id != other.id)
			return false;
		if (passagem == null) {
			if (other.passagem != null)
				return false;
		} else if (!passagem.equals(other.passagem))
			return false;
		if (procedencia == null) {
			if (other.procedencia != null)
				return false;
		} else if (!procedencia.equals(other.procedencia))
			return false;
		return true;
	}

}
