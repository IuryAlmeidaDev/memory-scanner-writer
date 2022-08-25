package br.gov.mt.sejudh.sigepen.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.conversores.EnumCustomConverter;
import br.gov.mt.sejudh.sigepen.enumerator.TipoAutorizacaoValidacaoSemBio;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoVisita;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_ENTRADA_SAIDA", schema = Sigepen.SEJUDH_SCHEMA)
public class EntradaSaida extends BaseModel<Long> {

	private static final long serialVersionUID = 863475178001620366L;

	@Id
	@SequenceGenerator(name = "SEQ_ENTRADA_SAIDA_GENERATOR", sequenceName = "SEQ_ENTRADA_SAIDA", allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ENTRADA_SAIDA_GENERATOR")
	@Column(name = "ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "ID_VISITANTE")
	private Visitante visitante;

	@Column(name = "DATA_ENTRADA")
	private Date dataEntrada;
    
	@Column(name = "DATA_SAIDA")
	private Date dataSaida;
	
	@Column(name = "DATA_CANCELAMENTO")
	private Date dataCancelamento;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "ID_UNIDADE_PRISIONAL")
	private UnidadePrisional unidadePrisional;
    
    @OneToMany(orphanRemoval = true, mappedBy = "entrada", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<EntradaSaidaRecuperando> recuperandos;
    
    @Column(name = "MOTIVO_CANCELAMENTO", length = 250)
    private String motivoCancelamento;
    
    @Column(name = "MOTIVO_FINALIZACAO", length = 250)
    private String motivoFinalizacao;
    
    @Column(name = "MOTIVO_ENTRADA", length = 250)
    private String motivoEntrada;

    @Column(name = "SITUACAO", nullable = false)
    @Convert(converter = EnumCustomConverter.class, disableConversion=true)
    @Type(type = "br.gov.mt.sejudh.sigepen.conversores.EnumCustomType")
    private SituacaoVisita situacao;
    
	@OneToMany(mappedBy = "entradaSaida", cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<AutorizacaoValidacaoSemBiometria> autorizacaoValidacaoSemBiometria;
	
	public EntradaSaida() {
		this.autorizacaoValidacaoSemBiometria = new ArrayList<AutorizacaoValidacaoSemBiometria>();
	}
	
	public AutorizacaoValidacaoSemBiometria getAutorizacao(TipoAutorizacaoValidacaoSemBio acao) {
		Optional<AutorizacaoValidacaoSemBiometria> opt = this.autorizacaoValidacaoSemBiometria.stream().filter(a -> a.getTipoAutorizacaoValidacaoSemBio().equals(acao)).findFirst();
		
		if (opt.isPresent())
			return opt.get();
		else
			return null;
	}

	@Override
	public String toString() {
		return "Entrada/Saida [id=" + id + "]";
	}

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

	public Date getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public Date getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}

	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}

	public List<EntradaSaidaRecuperando> getRecuperandos() {
		return recuperandos;
	}

	public void setRecuperandos(List<EntradaSaidaRecuperando> recuperandos) {
		this.recuperandos = recuperandos;
	}

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = Sigepen.formatarEspacos(motivoCancelamento);
	}

	public void setSituacao(SituacaoVisita situacao) {
		this.situacao = situacao;
	}

	public SituacaoVisita getSituacao() {
		return situacao;
	}

	public String getMotivoFinalizacao() {
		return motivoFinalizacao;
	}

	public void setMotivoFinalizacao(String motivoFinalizacao) {
		this.motivoFinalizacao = motivoFinalizacao;
	}

	public List<AutorizacaoValidacaoSemBiometria> getAutorizacaoValidacaoSemBiometria() {
		return autorizacaoValidacaoSemBiometria;
	}

	public void setAutorizacaoValidacaoSemBiometria(
			List<AutorizacaoValidacaoSemBiometria> autorizacaoValidacaoSemBiometria) {
		this.autorizacaoValidacaoSemBiometria = autorizacaoValidacaoSemBiometria;
	}

	public String getMotivoEntrada() {
		return motivoEntrada;
	}

	public void setMotivoEntrada(String motivoEntrada) {
		this.motivoEntrada = Sigepen.formatarEspacos(motivoEntrada);
	}

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}
}
