package br.gov.mt.sejudh.sigepen.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Table(name = "BIOMETRIA_TEMP", schema = Sigepen.SEJUDH_SCHEMA)
public class BiometriaTemporaria {

	@SequenceGenerator(name = "SEQ_BIO_TEMP_GEN", sequenceName = "SEQ_BIO_TEMP", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BIO_TEMP_GEN")
	@Column(name = "ID")
	private long id;
	
	@Lob
    @Column(name="TEMPLATE")
	private byte[] template;
	
	@Lob
	@Column(name = "JSON")
	private String json;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_COLETA")
	private Date dataColeta;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public byte[] getTemplate() {
		return template;
	}

	public void setTemplate(byte[] template) {
		this.template = template;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public Date getDataColeta() {
		return dataColeta;
	}

	public void setDataColeta(Date dataColeta) {
		this.dataColeta = dataColeta;
	}
}
