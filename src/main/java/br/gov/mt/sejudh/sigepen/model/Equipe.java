package br.gov.mt.sejudh.sigepen.model;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEquipe;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Audited
@Entity
@Table(name = "SIGEPEN_EQUIPE", schema = Sigepen.SEJUDH_SCHEMA)
public class Equipe extends BaseModel<Long> {

	private static final long serialVersionUID = -2841675579819366317L;

	@SequenceGenerator(name = "SEQ_EQUIPE_GENERATOR", sequenceName = "SIGEPEN_S_SIGEPEN_EQUIPE", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EQUIPE_GENERATOR")
	@Id
	@Column(name = "ID")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_EQUIPE")
	private TipoEquipe tipoEquipe;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_PLANTAO")
	private Plantao plantao;

	@OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PlantaoServidor> plantaoServidor = new ArrayList<>();

	@PrePersist
	public void prePersist(){
		this.plantaoServidor.forEach(p -> p.setEquipe(this));
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TipoEquipe getTipoEquipe() {
		return tipoEquipe;
	}

	public void setTipoEquipe(TipoEquipe tipoEquipe) {
		this.tipoEquipe = tipoEquipe;
	}

	public List<PlantaoServidor> getPlantaoServidor() {
		return plantaoServidor;
	}

	public void setPlantaoServidor(List<PlantaoServidor> plantaoServidor) {
		this.plantaoServidor = plantaoServidor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		Equipe equipe = (Equipe) o;
		return id.equals(equipe.id);
	}

	public Plantao getPlantao() {
		return plantao;
	}

	public void setPlantao(Plantao plantao) {
		this.plantao = plantao;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + id.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Equipe{" +
				"id=" + id +
				", tipoEquipe=" + tipoEquipe +
				", plantaoServidor=" + plantaoServidor +
				'}';
	}
}
