package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.enumerator.BiometriaTipoDedo;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "BIOMETRIA", schema = Sigepen.SEJUDH_SCHEMA)
public class Biometria implements Serializable {

	private static final long serialVersionUID = 1L;

	@SequenceGenerator(name = "SEQ_BIOMETRIA_GENERATOR", sequenceName = "SEQ_BIOMETRIA", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BIOMETRIA_GENERATOR")
	@Column(name="ID")
	private long id;
	
	@Version
	private long versao;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_CAPTURA", nullable = false)
	private Date dataCaptura;
	
	@OneToMany(mappedBy="biometria", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private Set<BiometriaDedo> dedos;
	
	@Lob
	@Column(name = "HASH")
	@Deprecated
	private String hash;
	
	@Lob
    @Column(name="TEMPLATE")
	private byte[] template;
	
	@Transient
	private boolean biometriaFoiAlterada = false;
	
	@Transient
	private long idPessoa;
	
	public Biometria() {
		this.dedos = new HashSet<BiometriaDedo>();
	}
	
	public boolean isDedoCapturado(BiometriaTipoDedo tipoDedo) {
		for (BiometriaDedo b : dedos) {
			if (this.template != null && b.getTipoDedo().equals(tipoDedo)) {
				return true;
			}
		}
		
		return false;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public Date getDataCaptura() {
		return dataCaptura;
	}

	public void setDataCaptura(Date dataCaptura) {
		this.dataCaptura = dataCaptura;
	}

	public Set<BiometriaDedo> getDedos() {
		return dedos;
	}

	public void setDedos(Set<BiometriaDedo> dedos) {
		this.dedos = dedos;
	}

	public boolean isBiometriaFoiAlterada() {
		return biometriaFoiAlterada;
	}

	public void setBiometriaFoiAlterada(boolean biometriaFoiAlterada) {
		this.biometriaFoiAlterada = biometriaFoiAlterada;
	}

	@Deprecated
	public String getHash() {
		return hash;
	}

	@Deprecated
	public void setHash(String hash) {
		this.hash = hash;
	}

	public byte[] getTemplate() {
		return template;
	}

	public void setTemplate(byte[] template) {
		this.template = template;
	}
	
	public long getIdPessoa() {
		return idPessoa;
	}

	public void setIdPessoa(long idPessoa) {
		this.idPessoa = idPessoa;
	}

	public Imagem getImagemBiometriaPorPosicao(BiometriaTipoDedo tipoDedo) {
		BiometriaAmostra amostra;
		Optional<BiometriaDedo> dedo = dedos.stream().filter(x -> x.getTipoDedo().equals(tipoDedo)).findFirst();
		
		if (this.template == null)
			return null;
		
		if (!dedo.isPresent())
			return null;

		try {
			amostra = dedo.get().getAmostras().stream().max(Comparator.comparing(BiometriaAmostra::getQualidade)).get();
		} catch (NoSuchElementException e) {
			return null;
		}

		Imagem i = new Imagem();
		
		i.setArquivo(amostra.getImagem());
		i.setContentType("");
		i.setId(amostra.getId());
		 
		return i;
	}
}
