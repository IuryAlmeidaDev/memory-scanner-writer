package br.gov.mt.sejudh.sigepen.model;


import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;


@Entity
@Table(name = "SIGEPEN_PLANTAO_SERVIDOR", schema = Sigepen.SEJUDH_SCHEMA)
public class PlantaoServidor extends BaseModel<Long>  {
	
	private static final long serialVersionUID = -2841675579819366317L;
	
	
	@SequenceGenerator(name = "SEQ_PLANTAO_SERVIDOR_GENERATOR", sequenceName = "SIGEPEN_S_PLANTAO_SERVIDOR", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PLANTAO_SERVIDOR_GENERATOR")
	@Id
	@Column(name = "ID")
	private Long id;

    @ManyToOne
	@JoinColumn(name = "ID_SERVIDOR")
	private Servidor servidor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_EQUIPE")
	private Equipe equipe;
	
	@Column(name = "ATIVIDADE")
    private String atividae;
	
	@Column(name = "SITUACAO")
	private String situacao;

	@Column(name = "OCORRENCIA", nullable = true)
	private String ocorrencia;

	@Column(name = "JUSTIFICATIVA", nullable = true)
	private String justificativa;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public String getAtividae() {
		return atividae;
	}

	public void setAtividae(String atividae) {
		this.atividae = atividae;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public Equipe getEquipe() {
		return equipe;
	}

	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
	}

	public String getOcorrencia() {
		return ocorrencia;
	}

	public void setOcorrencia(String ocorrencia) {
		this.ocorrencia = ocorrencia;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		PlantaoServidor that = (PlantaoServidor) o;
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
		return "PlantaoServidor{" +
				"id=" + id +
				", servidor=" + servidor +
				", atividae='" + atividae + '\'' +
				", situacao='" + situacao + '\'' +
				'}';
	}
}
