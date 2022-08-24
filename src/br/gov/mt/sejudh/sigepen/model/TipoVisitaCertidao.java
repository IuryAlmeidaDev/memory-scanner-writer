package br.gov.mt.sejudh.sigepen.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.TipoVisitaObrigatoriedade;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_TIPO_VISITA_CERTIDAO", schema = Sigepen.SEJUDH_SCHEMA)
public class TipoVisitaCertidao extends BaseModel<Long> {

	private static final long serialVersionUID = 4374904532852547760L;

	@Id
    @SequenceGenerator(name = "SIGEPEN_S_TIPO_VISITA_CERT", sequenceName = "SIGEPEN_S_TIPO_VISITA_CERT", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_TIPO_VISITA_CERT")
    @Column(name = "ID_TIPO_VISITA_CERTIDAO")
    private Long id;

    @JoinColumn(name = "ID_TIPO_VISITA")
    @ManyToOne
    private TipoVisita tipoVisita;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_OBRIGATORIEDADE", nullable = false)
    private TipoVisitaObrigatoriedade tipoObrigatoriedade;

    @JoinColumn(name = "ID_CERTIDAO")
    @ManyToOne
    private Certidao certidao;

    @Transient
    private int indexAlteracao;
    
    public TipoVisitaCertidao() {
    	indexAlteracao = Integer.MIN_VALUE;
    }
    
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public TipoVisita getTipoVisita() {
        return tipoVisita;
    }

    public void setTipoVisita(TipoVisita tipoVisita) {
        this.tipoVisita = tipoVisita;
    }

    public Certidao getCertidao() {
        return certidao;
    }

    public void setCertidao(Certidao certidao) {
        this.certidao = certidao;
    }

	public TipoVisitaObrigatoriedade getTipoObrigatoriedade() {
		return tipoObrigatoriedade;
	}

	public void setTipoObrigatoriedade(TipoVisitaObrigatoriedade tipoObrigatoriedade) {
		this.tipoObrigatoriedade = tipoObrigatoriedade;
	}
	
	public int getIndexAlteracao() {
		return indexAlteracao;
	}

	public void setIndexAlteracao(int indexAlt) {
		this.indexAlteracao = indexAlt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((certidao == null) ? 0 : certidao.hashCode());
		result = prime * result + ((tipoObrigatoriedade == null) ? 0 : tipoObrigatoriedade.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TipoVisitaCertidao other = (TipoVisitaCertidao) obj;
		if (certidao == null) {
			if (other.certidao != null)
				return false;
		} else if (!certidao.equals(other.certidao))
			return false;
		if (tipoObrigatoriedade != other.tipoObrigatoriedade)
			return false;
		return true;
	}
}
