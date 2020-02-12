package br.gov.mt.sejudh.sigepen.auditoria;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import br.gov.mt.sejudh.sigepen.util.Sigepen;


@Entity
@Table(name = "REVISAO", schema = Sigepen.SEJUDH_AUDIT_SCHEMA)
@RevisionEntity(CustomRevListener.class) 
public class CustomRevEntity {
	@SequenceGenerator(name = "SEQ_REV_GENERATOR", sequenceName = "SEQ_REVISAO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_AUDIT_SCHEMA)

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_REV_GENERATOR")
    @RevisionNumber
    private long id;
    
	@Column(name = "DATAHORA")
	@RevisionTimestamp   
    private long revtstmp;
	
	private long usuario_id;	
	
	private String ip;
	
	private String host;
	
	private String url;
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}	

	public long getRevtstmp() {
		return revtstmp;
	}

	public void setRevtstmp(long revtstmp) {
		this.revtstmp = revtstmp;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}	

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public long getUsuarioId() {
		return usuario_id;
	}

	public void setUsuarioId(long usuario_id) {
		this.usuario_id = usuario_id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
