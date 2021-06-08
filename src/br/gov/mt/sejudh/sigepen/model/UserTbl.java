package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

//Registros biométricos que foram carregados na memória (NServer)

@Entity
@Table(name = "USERTBL", schema = Sigepen.SEJUDH_SCHEMA)
public class UserTbl implements Serializable {

	private static final long serialVersionUID = -135709151558657113L;

	@Id
	@Column(name = "ID")
	private long id;
	
	@Column(name = "DBID")
	private long pessoa_id;
	
	@Lob
	private byte[] template;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPessoa_id() {
		return pessoa_id;
	}

	public void setPessoa_id(long pessoa_id) {
		this.pessoa_id = pessoa_id;
	}

	public byte[] getTemplate() {
		return template;
	}

	public void setTemplate(byte[] template) {
		this.template = template;
	}
}
