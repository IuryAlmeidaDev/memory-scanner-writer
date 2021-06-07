package br.gov.mt.sejudh.sigepen.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "HISTORICO_RECUPERANDO", schema = Sigepen.SEJUDH_SCHEMA)
public class HistoricoRecuperando {

	@SequenceGenerator(name = "SEQ_HISTORICO_RECUPERANDO_GENERATOR", sequenceName = "SEQ_HISTORICO_RECUPERANDO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_HISTORICO_RECUPERANDO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Version
	private Long versao;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RECUPERANDO_ID")
	private Recuperando recuperando;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_REGISTRO")
	private Date dataRegistro;
	
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "TIPO_HISTORICO_ID")
	private TipoHistorico tipoHistorico;
	
	@Column(name = "OBSERVACAO")
	private String observacao;

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

	@Override
	public String toString() {
		return "Historico Recuperando [id=" + id + ", recuperando="+ recuperando +"]";
	}
	
	
}
