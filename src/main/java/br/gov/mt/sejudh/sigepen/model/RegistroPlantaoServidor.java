package br.gov.mt.sejudh.sigepen.model;


import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.util.Sigepen;


@Entity
@Table(name = "REGISTRO_PLANTAO_SERVIDOR", schema = Sigepen.SEJUDH_SCHEMA)
public class RegistroPlantaoServidor extends BaseModel<Long>  {
	
	private static final long serialVersionUID = -2841675579819366317L;
	
	
	@SequenceGenerator(name = "SEQ_REGISTRO_PLANTAO_SERVIDOR_GENERATOR", sequenceName = "SEQ_REGISTRO_PLANTAO_SERVIDOR", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_REGISTRO_PLANTAO_SERVIDOR_GENERATOR")
	@Id
	@Column(name = "ID")
	private Long id;	
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_REGISTRO_PLANTAO_SERVIDOR", nullable = false)
	private Date dataRegistroPlantaoServidor;
		
	@Column(name = "NOME_SERVIDOR")
    private String nomeServidor;
	
	@Column(name = "ATIVIDADE_SELECIONADA")
    private String atividadeSelecionada;
	
	@Column(name = "SITUACAO_SELECIONADA")
	private String situacaoSelecionada;	    
	
	@Column(name = "REGISTRO_PLANTAO_ID")
    private Long registroPlantaoId;
	
	@Column(name = "OCORRENCIA_SELECIONADA", nullable = true)
	private String ocorrenciaSelecionada;

	@Column(name = "JUSTIFICATIVA", nullable = true)
	private String justificativa;

	
	
	/* ------------------------  Getts e Setts  ------------------------ */
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}	

	public Date getDataRegistroPlantaoServidor() {
		return dataRegistroPlantaoServidor;
	}

	public void setDataRegistroPlantaoServidor(Date dataRegistroPlantaoServidor) {
		this.dataRegistroPlantaoServidor = dataRegistroPlantaoServidor;
	}

	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}


	public String getAtividadeSelecionada() {
		return atividadeSelecionada;
	}

	public void setAtividadeSelecionada(String atividadeSelecionada) {
		this.atividadeSelecionada = atividadeSelecionada;
	}

	public String getSituacaoSelecionada() {
		return situacaoSelecionada;
	}

	public void setSituacaoSelecionada(String situacaoSelecionada) {
		this.situacaoSelecionada = situacaoSelecionada;
	}

	public Long getRegistroPlantaoId() {
		return registroPlantaoId;
	}

	public void setRegistroPlantaoId(Long registroPlantaoId) {
		this.registroPlantaoId = registroPlantaoId;
	}
	
	
	
	
	public String getOcorrenciaSelecionada() {
		return ocorrenciaSelecionada;
	}

	public void setOcorrenciaSelecionada(String ocorrenciaSelecionada) {
		this.ocorrenciaSelecionada = ocorrenciaSelecionada;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegistroPlantaoServidor other = (RegistroPlantaoServidor) obj;
		return id == other.id;
	}

}
