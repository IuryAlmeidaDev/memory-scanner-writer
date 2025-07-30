package br.gov.mt.sejudh.sigepen.model;


import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
//import com.sun.istack.internal.NotNull;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Audited
@Entity
@Table(name = "PLANTAO_VISITA_ESTRUTURA", schema = Sigepen.SEJUDH_SCHEMA)
public class PlantaoVisitaEstrutura extends BaseModel<Long>  {
	
	private static final long serialVersionUID = -2841675579819366317L;
	
	
	@SequenceGenerator(name = "SEQ_PLANTAO_VISITA_ESTRUTURA_GENERATOR", sequenceName = "S_PLANTAO_VISITA_ESTRUTURA", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PLANTAO_VISITA_ESTRUTURA_GENERATOR")
	@Id
	@Column(name = "ID")
	private Long id;

	@Column(name = "ESTRUTURA")
    private String estrutura;

//	@NotNull
	@Column(name = "HOMEM")
	private Long homem;

//	@NotNull
	@Column(name = "MULHER")
	private Long mulher;

//	@NotNull
	@Column(name = "MENOR")
	private Long menor;

	@Column(name = "RECUPERANDO")
	private Long recuperando;

//	@NotNull
	@Column(name = "OCORRENCIA")
	private Long ocorrencia;

	@Column(name = "HORA_INICIO")
	private String horaInicio;

	@Column(name = "HORA_FIM")
	private String horaFim;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "ID_PLANTAO_VISITA")
	private PlantaoVisita plantaoVisita;

	@Transient
	private Long estruturaId;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PlantaoVisitaEstrutura() {
	}

	public PlantaoVisitaEstrutura(Long homem, Long mulher, Long menor, Long recuperando, String estrutura, Long estruturaId) {
		this.homem = homem;
		this.mulher = mulher;
		this.menor = menor;
		this.recuperando = recuperando;
		this.estrutura = estrutura;
		this.estruturaId = estruturaId;
	}

	public void validarCamposObrigatorios() throws ValidacaoException {

		if (this.getEstrutura() == null || this.getEstrutura().trim().isEmpty()) {
			throw new ValidacaoException("Nome da estrutura é obrigatório.");
		}

		if (this.getHomem() == null || this.getHomem() < 0L) {
			throw new ValidacaoException("Nome do homem é obrigatório.");
		}

		if (this.getMulher() == null || this.getMulher() < 0L) {
			throw new ValidacaoException("Nome da mulher é obrigatório.");
		}

		if (this.getMenor() == null || this.getMenor() < 0L ) {
			throw new ValidacaoException("Nome do menor é obrigatório.");
		}

		if (this.getOcorrencia() == null || this.getOcorrencia() < 0L ) {
			throw new ValidacaoException("Nome da ocorrência é obrigatório.");
		}
	}


	/*   */

	public String getEstrutura() {
		return estrutura;
	}

	public void setEstrutura(String estrutura) {
		this.estrutura = estrutura;
	}

	public Long getHomem() {
		return homem;
	}

	public void setHomem(Long homem) {
		this.homem = homem;
	}

	public Long getMulher() {
		return mulher;
	}

	public void setMulher(Long mulher) {
		this.mulher = mulher;
	}

	public Long getMenor() {
		return menor;
	}

	public void setMenor(Long menor) {
		this.menor = menor;
	}

	public Long getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Long recuperando) {
		this.recuperando = recuperando;
	}

	public Long getOcorrencia() {
		return ocorrencia;
	}

	public void setOcorrencia(Long ocorrencia) {
		this.ocorrencia = ocorrencia;
	}

	public String getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}

	public String getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(String horaFim) {
		this.horaFim = horaFim;
	}

	public PlantaoVisita getPlantaoVisita() {
		return plantaoVisita;
	}

	public void setPlantaoVisita(PlantaoVisita plantaoVisita) {
		this.plantaoVisita = plantaoVisita;
	}

	public Long getEstruturaId() {
		return estruturaId;
	}

	public void setEstruturaId(Long estruturaId) {
		this.estruturaId = estruturaId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		PlantaoVisitaEstrutura that = (PlantaoVisitaEstrutura) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + id.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "PlantaoVisitaEstrutura{" +
				"id=" + id +
				", estrutura='" + estrutura + '\'' +
				", homem=" + homem +
				", mulher=" + mulher +
				", menor=" + menor +
				", ocorrencia=" + ocorrencia +
				", horaInicio='" + horaInicio + '\'' +
				", horaFim='" + horaFim + '\'' +
				'}';
	}
}
