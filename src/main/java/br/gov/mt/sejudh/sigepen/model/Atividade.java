package br.gov.mt.sejudh.sigepen.model;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoPlantao;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "ATIVIDADE", schema = Sigepen.SEJUDH_SCHEMA)
public class Atividade extends BaseModel<Long> {
	
	private static final long serialVersionUID = -689264275741833273L;
	

	@SequenceGenerator(name = "SEQ_ATIVIDADE_GENERATOR", sequenceName = "SEQ_ATIVIDADE", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ATIVIDADE_GENERATOR")
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "DESCRICAO")
    private String descricao;
	
	@Enumerated
	@Column(name = "SITUACAO_PLANTAO")
	private SituacaoPlantao situacaoPlantao;
	
	@Column(name = "TIPO")
	private Long tipo;
	
	
	
	/* --------------------------GET & SETTS -------------------------- */
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public SituacaoPlantao getSituacaoPlantao() {
		return situacaoPlantao;
	}

	public void setSituacaoPlantao(SituacaoPlantao situacaoPlantao) {
		this.situacaoPlantao = situacaoPlantao;
	}
	
	public Long getTipo() {
		return tipo;
	}

	public void setTipo(Long tipo) {
		this.tipo = tipo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(id);
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
		Atividade other = (Atividade) obj;
		return Objects.equals(id, other.id);
	}
	
}
