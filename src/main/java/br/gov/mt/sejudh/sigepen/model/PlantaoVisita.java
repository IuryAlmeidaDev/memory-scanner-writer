package br.gov.mt.sejudh.sigepen.model;


import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Audited
@Entity
@Table(name = "SIGEPEN_PLANTAO_VISITA", schema = Sigepen.SEJUDH_SCHEMA)
public class PlantaoVisita extends BaseModel<Long>  {

	private static final long serialVersionUID = -2841675579819366317L;


	@SequenceGenerator(name = "SEQ_PLANTAO_VISITA_GENERATOR", sequenceName = "SIGEPEN_S_PLANTAO_VISITA", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PLANTAO_VISITA_GENERATOR")
	@Id
	@Column(name = "ID")
	private Long id;

	@Enumerated
	@Column(name = "TIPO_ENTRADA")
	private TipoEntrada tipoEntrada;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_PLANTAO")
	private Plantao plantao;

	@OneToMany(mappedBy = "plantaoVisita", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PlantaoVisitaEstrutura> plantaoVisitaEstruturas = new ArrayList<PlantaoVisitaEstrutura>();

	public PlantaoVisita(){}

	public PlantaoVisita(TipoEntrada tipoEntrada,Plantao plantao,List<PlantaoVisitaEstrutura> plantaoVisitaEstruturas){
		this.tipoEntrada =	tipoEntrada;
		this.plantao = plantao;
		this.plantaoVisitaEstruturas = plantaoVisitaEstruturas;
	}

	@PreUpdate
	@PrePersist
	public void prePersist(){
		this.plantaoVisitaEstruturas.forEach(plantaoVisitaEstrutura -> {plantaoVisitaEstrutura.setPlantaoVisita(this);});
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TipoEntrada getTipoEntrada() {
		return tipoEntrada;
	}

	public void setTipoEntrada(TipoEntrada tipoEntrada) {
		this.tipoEntrada = tipoEntrada;
	}

	public Plantao getPlantao() {
		return plantao;
	}

	public void setPlantao(Plantao plantao) {
		this.plantao = plantao;
	}

	public List<PlantaoVisitaEstrutura> getPlantaoVisitaEstruturas() {
		return plantaoVisitaEstruturas;
	}

	public void setPlantaoVisitaEstruturas(List<PlantaoVisitaEstrutura> plantaoVisitaEstruturas) {
		this.plantaoVisitaEstruturas = plantaoVisitaEstruturas;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		PlantaoVisita that = (PlantaoVisita) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + id.hashCode();
		return result;
	}
}
