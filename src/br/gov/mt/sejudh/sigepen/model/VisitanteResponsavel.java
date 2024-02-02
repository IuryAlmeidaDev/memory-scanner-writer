package br.gov.mt.sejudh.sigepen.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_VISITANTE_RESPONSAVEL", schema = Sigepen.SEJUDH_SCHEMA)
public class VisitanteResponsavel extends BaseModel<Long>{

	private static final long serialVersionUID = 5093805604761821221L;

	@Id
    @SequenceGenerator(name = "SIGEPEN_S_VISITANTE_RESPOM", sequenceName = 	"SIGEPEN_S_VISITANTE_RESPOM", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_VISITANTE_RESPOM")
    @Column(name = "ID_VISITANTE_RESPONSAVEL")
    private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VISITANTE")
    private Visitante visitante;
	
	@ManyToOne
    @JoinColumn(name = "VISITANTE_RESPONSAVEL")
	private Visitante visitanteResponsavel;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_CADASTRO")
    private Usuario usuarioCadastro;
	
    @Enumerated(EnumType.STRING)
    @Column(name = "STA_VISITANTE_RESPONSAVEL")
    private StatusVisitante status;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_HORA_CADASTRO")
    private Date dataHoraCadastro;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Visitante getVisitante() {
		return visitante;
	}

	public void setVisitante(Visitante visitante) {
		this.visitante = visitante;
	}


	public Visitante getVisitanteResponsavel() {
		return visitanteResponsavel;
	}

	public void setVisitanteResponsavel(Visitante visitanteResponsavel) {
		this.visitanteResponsavel = visitanteResponsavel;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	public StatusVisitante getStatus() {
		return status;
	}

	public void setStatus(StatusVisitante status) {
		this.status = status;
	}

	public Date getDataHoraCadastro() {
		return dataHoraCadastro;
	}

	public void setDataHoraCadastro(Date dataHoraCadastro) {
		this.dataHoraCadastro = dataHoraCadastro;
	}

}
