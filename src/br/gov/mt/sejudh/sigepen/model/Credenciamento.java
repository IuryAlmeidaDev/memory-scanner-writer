package br.gov.mt.sejudh.sigepen.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_CREDENCIAMENTO", schema = Sigepen.SEJUDH_SCHEMA)
public class Credenciamento extends BaseModel<Long> {

	private static final long serialVersionUID = -2923263006143325862L;

	@Id
    @SequenceGenerator(name = "SIGEPEN_S_VISITANT_RECUPERANDO", sequenceName = "SIGEPEN_S_VISITANT_RECUPERANDO",
            initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_VISITANT_RECUPERANDO")
    @Column(name = "ID_CREDENCIAMENTO")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_VISITANTE")
    private Visitante visitante;

    @ManyToOne
    @JoinColumn(name = "ID_RECUPERANDO")
    private Recuperando recuperando;

    @ManyToOne
    @JoinColumn(name = "ID_UNIDADE_PRISIONAL")
    private UnidadePrisional unidadePrisional;

    @Column(name = "PROFISSAO")
    private String profissao;

    @ManyToOne
    @JoinColumn(name = "ID_TIPO_VISITA")
    private TipoVisita tipoVisita;

    @ManyToOne
    @JoinColumn(name = "ID_PARENTE")
    private Parente parente;

    @OneToMany(orphanRemoval = true, mappedBy = "credenciamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FaseDoCredenciamento> fasesDoCredenciamento = new ArrayList<>(); //credenciamentos

    @Column(name = "CODG_FASE_CREDENCIAL")
    private Integer faseCredenciamento;

    @Column(name = "CODG_SITUACAO_CREDENCIAL")
    private Integer statusCredenciamento;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_VALIDADE")
    private Date dataValidade;
    
    @Transient
	private Boolean selecionado;
    
    @Transient
    private StatusVisita statusVisita;
    
    @Column(name = "IS_RENOVACAO")
    private Boolean isRenovacao = false;

    public FaseDoCredenciamento getCredencialAprovada() {
        return this.getFasesDoCredenciamento().stream().filter(
                x -> x.getFaseCredenciamento() == FaseCredenciamento.APROVACAO
                        && x.getStatusCredenciamento() == StatusCredenciamento.APROVADO
        ).findFirst().orElse(null);
    }

    public FaseDoCredenciamento getUltimoCredenciamento() {
        return this.getFasesDoCredenciamento().stream().max(
                Comparator.comparing(o -> o.getFaseCredenciamento())
        ).orElse(null);
    }

    public FaseDoCredenciamento getCredencialInativo() {
        return this.getFasesDoCredenciamento().stream().filter(
                x -> x.getFaseCredenciamento() == FaseCredenciamento.APROVACAO
                        && x.getStatusCredenciamento() == StatusCredenciamento.INATIVO
        ).findFirst().orElse(null);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProfissao() {
		return profissao;
	}
    
    public void setProfissao(String profissao) {
    	if(null != profissao)
    		this.profissao = profissao.toUpperCase();
	}

    public Visitante getVisitante() {
        return visitante;
    }

    public void setVisitante(Visitante visitante) {
        this.visitante = visitante;
    }

    public Recuperando getRecuperando() {
        return recuperando;
    }

    public void setRecuperando(Recuperando recuperando) {
        this.recuperando = recuperando;
    }

    public TipoVisita getTipoVisita() {
        return tipoVisita;
    }

    public void setTipoVisita(TipoVisita tipoVisita) {
        this.tipoVisita = tipoVisita;
    }

    public Parente getParente() {
        return parente;
    }

    public void setParente(Parente parente) {
        this.parente = parente;
    }

    public List<FaseDoCredenciamento> getFasesDoCredenciamento() { //getCredenciamentos() {
        if (this.fasesDoCredenciamento == null) {
            this.fasesDoCredenciamento = new ArrayList<>();
        }
        return fasesDoCredenciamento.stream().sorted(Comparator.comparing(FaseDoCredenciamento::getId)).collect(Collectors.toList());
    }

    public void setFasesDoCredenciamento(List<FaseDoCredenciamento> fasesDoCredenciamento) {
        this.fasesDoCredenciamento = fasesDoCredenciamento;
    }

    public UnidadePrisional getUnidadePrisional() {
        return unidadePrisional;
    }

    public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
        this.unidadePrisional = unidadePrisional;
    }

	public Integer getFaseCredenciamento() {
		return faseCredenciamento;
	}

	public void setFaseCredenciamento(Integer faseCredenciamento) {
		this.faseCredenciamento = faseCredenciamento;
	}

	public Integer getStatusCredenciamento() {
		return statusCredenciamento;
	}

	public void setStatusCredenciamento(Integer statusCredenciamento) {
		this.statusCredenciamento = statusCredenciamento;
	}
	
	public Credenciamento() {
		this.faseCredenciamento = FaseCredenciamento.RECEBIMENTO.getId();
		this.statusCredenciamento = StatusCredenciamento.PENDENTE.getId();
	}
	
	public FaseDoCredenciamento getFaseAtualCredenciamento() {
		FaseDoCredenciamento v = this.getFasesDoCredenciamento().stream().max(
                Comparator.comparing(o -> o.getFaseCredenciamento())
        ).orElse(null);

        return v;
	}
	
	@Override
	public String toString() {
		return "Visitante Recuperando [id=" + id +  " , "  +   this.visitante + "] ";
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public StatusVisita getStatusVisita() {
		return statusVisita;
	}

	public void setStatusVisita(StatusVisita statusVisita) {
		this.statusVisita = statusVisita;
	}

	public Date getDataValidade() {
		return dataValidade;
	}

	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}

	public Boolean getIsRenovacao() {
		return isRenovacao;
	}

	public void setIsRenovacao(Boolean isRenovacao) {
		this.isRenovacao = isRenovacao;
	}
}
