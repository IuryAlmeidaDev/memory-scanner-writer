package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.conversores.EnumCustomConverter;
import br.gov.mt.sejudh.sigepen.enumerator.BiometriaTipoDedo;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name="BIOMETRIA_DEDO", schema = Sigepen.SEJUDH_SCHEMA)
public class BiometriaDedo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@SequenceGenerator(name = "SEQ_BIO_DEDO_GENERATOR", sequenceName = "SEQ_BIOMETRIA_DEDO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BIO_DEDO_GENERATOR")
	@Column(name="ID")
	private long id;
	
	@Transient
	private Long versao;

	@Column(name = "DEDO")
	@Convert(converter = EnumCustomConverter.class, disableConversion = true)
	@Type(type = "br.gov.mt.sejudh.sigepen.conversores.EnumCustomType")
	private BiometriaTipoDedo tipoDedo;
	
	@OneToMany(mappedBy="dedo", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)	
	private Set<BiometriaAmostra> amostras;
	
	@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY, optional=false)
	private Biometria biometria;
	
	public BiometriaDedo() {
		this.amostras = new HashSet<BiometriaAmostra>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BiometriaTipoDedo getTipoDedo() {
		return tipoDedo;
	}

	public void setTipoDedo(BiometriaTipoDedo tipoDedo) {
		this.tipoDedo = tipoDedo;
	}

	public Set<BiometriaAmostra> getAmostras() {
		return amostras;
	}

	public void setAmostras(Set<BiometriaAmostra> amostras) {
		this.amostras = amostras;
	}

	public Biometria getBiometria() {
		return biometria;
	}

	public void setBiometria(Biometria biometria) {
		this.biometria = biometria;
	}
}
