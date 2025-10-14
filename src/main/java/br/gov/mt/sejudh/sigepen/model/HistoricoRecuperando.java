package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "HISTORICO_RECUPERANDO", schema = Sigepen.SEJUDH_SCHEMA)
public class HistoricoRecuperando implements Serializable {

	private static final long serialVersionUID = 8195563536690216091L;

	@SequenceGenerator(name = "SEQ_HISTORICO_RECUPERANDO_GENERATOR", sequenceName = "SEQ_HISTORICO_RECUPERANDO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_HISTORICO_RECUPERANDO_GENERATOR")
	@Column(name = "ID")
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RECUPERANDO_ID")
	private Recuperando recuperando;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_REGISTRO")
	private Date dataRegistro;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "TIPO_HISTORICO_ID")
	private TipoHistorico tipoHistorico;

	@Column(name = "OBSERVACAO")
	private String observacao;

	@Lob
	@Column(name = "DETALHAMENTO")
	private String detalhamento;

	@ManyToOne
	@JoinColumn(name = "SERVIDOR_ID")
	private Servidor servidor;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
	}

	public Date getDataRegistro() {
		return dataRegistro;
	}

	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	public TipoHistorico getTipoHistorico() {
		return tipoHistorico;
	}

	public void setTipoHistorico(TipoHistorico tipoHistorico) {
		this.tipoHistorico = tipoHistorico;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = Sigepen.formatarEspacos(observacao);
	}

	public String getDetalhamento() {
		return detalhamento;
	}

	public void setDetalhamento(String detalhamento) {
		this.detalhamento = detalhamento;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}


	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("id", id)
				.append("recuperando", recuperando)
				.append("dataRegistro", dataRegistro)
				.append("tipoHistorico", tipoHistorico)
				.append("observacao", observacao)
				.append("detalhamento", detalhamento)
				.append("servidor", servidor)
				.toString();
	}
}
