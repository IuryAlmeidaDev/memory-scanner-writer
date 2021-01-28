package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name="BIOMETRIA_AMOSTRA", schema = Sigepen.SEJUDH_SCHEMA)
public class BiometriaAmostra implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@SequenceGenerator(name = "SEQ_BIO_AMOS_GENERATOR", sequenceName = "SEQ_BIOMETRIA_AMOSTRA", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BIO_AMOS_GENERATOR")
	@Column(name="ID")
	private long id;
	
	@Column(name="AMOSTRA_NUMERO")
	private Integer amostraNumero;
	
	@Lob
	@Column(name="DATA")
	private byte[] data;

    @Lob
    @Column(name="IMAGEM")
    private byte[] imagem;
    
    @Column(name="QUALIDADE")
    private Integer qualidade;

	@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="BIOMETRIA_DEDO_ID")
	private BiometriaDedo dedo;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Integer getAmostraNumero() {
		return amostraNumero;
	}

	public void setAmostraNumero(Integer amostraNumero) {
		this.amostraNumero = amostraNumero;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public BiometriaDedo getDedo() {
		return dedo;
	}

	public void setDedo(BiometriaDedo dedo) {
		this.dedo = dedo;
	}

	public byte[] getImagem() {
		return imagem;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}

	public Integer getQualidade() {
		return qualidade;
	}

	public void setQualidade(Integer qualidade) {
		this.qualidade = qualidade;
	}
}
