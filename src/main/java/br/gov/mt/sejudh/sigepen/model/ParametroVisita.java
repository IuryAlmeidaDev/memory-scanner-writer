package br.gov.mt.sejudh.sigepen.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.conversores.EnumCustomConverter;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_PARAMETRO_VISITA", schema = Sigepen.SEJUDH_SCHEMA)
public class ParametroVisita extends BaseModel<Long> {

	private static final long serialVersionUID = 2337285461770033179L;

	@Id
	@SequenceGenerator(name = "SEQ_PARAMETRO_VISITA_GENERATOR", sequenceName = "SEQ_PARAMETRO_VISITA", allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PARAMETRO_VISITA_GENERATOR")
	@Column(name = "ID")
	private Long id;

	@Convert(converter = EnumCustomConverter.class, disableConversion = true)
	@Type(type = "br.gov.mt.sejudh.sigepen.conversores.EnumCustomType")
	@Column(name = "ID_TIPO_ENTRADA")
	private TipoEntrada tipoEntrada;

	@ManyToOne
	@JoinColumn(name = "ID_UNIDADE_PRISIONAL")
	private UnidadePrisional unidade;

	@Column(name = "HORA_ENTRADA")
	private String horaEntrada;

	@Column(name = "HORA_SAIDA")
	private String horaSaida;

	@Column(name = "INTERVALO")
	private Integer intervalo;

	@Column(name = "QTD_SALA")
	private Integer quantidadeSala;

	@Column(name = "DOMINGO")
	private boolean domingo;

	@Column(name = "SEGUNDA")
	private boolean segunda;

	@Column(name = "TERCA")
	private boolean terca;

	@Column(name = "QUARTA")
	private boolean quarta;

	@Column(name = "QUINTA")
	private boolean quinta;

	@Column(name = "SEXTA")
	private boolean sexta;

	@Column(name = "SABADO")
	private boolean sabado;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private SgpStatus status;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="parametroVisita", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<ParametroVisitaEstrutura> estruturas;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="parametroVisita", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<ParametroVisitaBloqueio> bloqueios;

    @Transient
    private List<Estrutura> celas;
    
    public ParametroVisita() {
    	this.status = SgpStatus.A;
    }
    
	@Override
	public String toString() {
		return "Parametro Visita [id=" + id + "]";
	}

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

	public UnidadePrisional getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadePrisional unidade) {
		this.unidade = unidade;
	}

	public Integer getIntervalo() {
		return intervalo;
	}

	public void setIntervalo(Integer intervalo) {
		this.intervalo = intervalo;
	}

	public Integer getQuantidadeSala() {
		return quantidadeSala;
	}

	public void setQuantidadeSala(Integer quantidadeSala) {
		this.quantidadeSala = quantidadeSala;
	}

	public boolean isDomingo() {
		return domingo;
	}

	public void setDomingo(boolean domingo) {
		this.domingo = domingo;
	}

	public boolean isSegunda() {
		return segunda;
	}

	public void setSegunda(boolean segunda) {
		this.segunda = segunda;
	}

	public boolean isTerca() {
		return terca;
	}

	public void setTerca(boolean terca) {
		this.terca = terca;
	}

	public boolean isQuarta() {
		return quarta;
	}

	public void setQuarta(boolean quarta) {
		this.quarta = quarta;
	}

	public boolean isQuinta() {
		return quinta;
	}

	public void setQuinta(boolean quinta) {
		this.quinta = quinta;
	}

	public boolean isSexta() {
		return sexta;
	}

	public void setSexta(boolean sexta) {
		this.sexta = sexta;
	}

	public boolean isSabado() {
		return sabado;
	}

	public void setSabado(boolean sabado) {
		this.sabado = sabado;
	}

	public SgpStatus getStatus() {
		return status;
	}

	public void setStatus(SgpStatus status) {
		this.status = status;
	}

	public String getHoraEntrada() {
		return horaEntrada;
	}

	public void setHoraEntrada(String horaEntrada) {
		this.horaEntrada = horaEntrada;
	}

	public String getHoraSaida() {
		return horaSaida;
	}

	public void setHoraSaida(String horaSaida) {
		this.horaSaida = horaSaida;
	}

	public List<ParametroVisitaEstrutura> getEstruturas() {
		return estruturas;
	}

	public void setEstruturas(List<ParametroVisitaEstrutura> estruturas) {
		this.estruturas = estruturas;
	}

	public List<ParametroVisitaBloqueio> getBloqueios() {
		return bloqueios;
	}

	public void setBloqueios(List<ParametroVisitaBloqueio> bloqueios) {
		this.bloqueios = bloqueios;
	}

	public String dias() {
		String r = "";
		if(this.domingo)
			r += "Dom,";
		if(this.segunda)
			r += "Seg,";
		if(this.terca)
			r += "Ter,";
		if(this.quarta)
			r += "Qua,";
		if(this.quinta)
			r += "Qui,";
		if(this.sexta)
			r += "Sex,";
		if(this.sabado)
			r += "Sab,";
		return r;
	}
	
	private String formatarHora(String hora) {
		if(hora.length() != 4)
			return hora;
		return hora.substring(0,2) + ":" + hora.substring(2,4);
	}
	
	public String horaEntrada() {
		if(this.horaEntrada == null)
			this.horaEntrada = "";
		return formatarHora(this.horaEntrada);
	}
	
	public String horaSaida() {
		if(this.horaSaida == null)
			this.horaSaida = "";
		return formatarHora(this.horaSaida);
	}
	
	public int entrada() {
		try {
			return Integer.parseInt(this.horaEntrada);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public int saida() {
		try {
			return Integer.parseInt(this.horaSaida);
		} catch (Exception e) {
			return 0;
		}
	}

	public List<Estrutura> getCelas() {
		return celas;
	}

	public void setCelas(List<Estrutura> celas) {
		this.celas = celas;
	}
	
}
