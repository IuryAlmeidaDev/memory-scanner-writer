package br.gov.mt.sejudh.sigepen.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.TipoFoto;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_VISITANTE", schema = Sigepen.SEJUDH_SCHEMA)
public class Visitante extends BaseModel<Long> {

	private static final long serialVersionUID = 5093805604761821218L;

	@Id
    @SequenceGenerator(name = "SIGEPEN_S_VISITANTE", sequenceName = "SIGEPEN_S_VISITANTE", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_VISITANTE")
    @Column(name = "ID_VISITANTE")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PESSOA_VISITANTE")
    private Pessoa pessoa;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PESSOA_RESPONSAVEL")
    private Visitante responsavel;
   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNIDADE_PRISIONAL")
    private UnidadePrisional unidadePrisional;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_IDENTIDADE_GENERO")
    private IdentidadeDeGenero identidadeDeGenero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_VISITA")
    private TipoVisita tipoVisita;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_HORA_CADASTRO")
    private Date dataHoraCadastro;

    @Enumerated(EnumType.STRING)
    @Column(name = "FLAG_PNE")
    private SimNao pne;

    @Enumerated(EnumType.STRING)
    @Column(name = "FLAG_PNE_PERMANENTE")
    private SimNao pnePermanente;

    @Enumerated(EnumType.STRING)
    @Column(name = "FLAG_EMANCIPADO")
    private SimNao emancipado;

    @Enumerated(EnumType.STRING)
    @Column(name = "STAT_VISITANTE")
    private StatusVisitante status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_CADASTRO")
    private Usuario usuarioCadastro;

    @OneToMany(orphanRemoval = true, mappedBy = "visitante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VisitanteFoto> fotos;

    @OneToMany(orphanRemoval = true, mappedBy = "visitante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VisitanteCertidao> certidoes;

    @OneToMany(orphanRemoval = true, mappedBy = "visitante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VisitanteDocumento> visitanteDocumentos;

    @OneToMany(orphanRemoval = true, mappedBy = "visitante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Credenciamento> credenciamentos;

    @OneToMany(orphanRemoval = true, mappedBy = "visitante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Impedimento> impedimentos = new ArrayList<>();
    
    @OneToMany(orphanRemoval = true, mappedBy = "visitante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EntradaSaida> entradas = new ArrayList<>();

    @Column(name = "CODG_FASE_ATUAL")
    private Integer faseAtual;

    @Column(name = "CODG_SITUACAO_ATUAL")
    private Integer situacaoAtual;
    
	@OneToOne(mappedBy = "visitante", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	private AutorizacaoCadastroSemBiometria autorizacaoCadastroSemBiometria;

	@Transient
    private Date dataAprovacao;

    @Transient
    private FaseCredenciamento faseCredenciamento;

    @Transient
    private Long credenciamentosAtivos;

    @Transient
    private Date numImpedimento;

    @Transient
    private Imagem imagemAtual;
    
    @Transient
    private Credenciamento cadastro;
    
    @Transient
    private List<Visitante> visitantesReponsaveis;
    
    @Transient
 	private Boolean selecionado;
    
    public Visitante() {
		this.faseAtual = FaseCredenciamento.RECEBIMENTO.getId();
		this.situacaoAtual = StatusCredenciamento.PENDENTE.getId();
		this.visitantesReponsaveis = new ArrayList<Visitante>();
    }

    public Visitante(Long id, Pessoa pessoa, TipoVisita tipoVisita, Date dataHoraCadastro, StatusVisitante status,
                     Date dataAprovacao, Integer faseCredenciamento, Long credenciamentosAtivos, Date numImpedimento) {
        this.id = id;
        this.pessoa = pessoa;
        this.tipoVisita = tipoVisita;
        this.dataHoraCadastro = dataHoraCadastro;
        this.status = status;
        this.dataAprovacao = dataAprovacao;
        this.credenciamentosAtivos = credenciamentosAtivos;
        this.numImpedimento = numImpedimento;
        this.faseCredenciamento = FaseCredenciamento.getValue(faseCredenciamento);
    }

    public void adicionarFotoFrente(VisitanteFoto foto) {
        this.fotos.forEach(visitanteFoto -> visitanteFoto.setAtual(false));
        foto.setAtual(true);
        foto.setTipo(TipoFoto.FRENTE);

        this.fotos.add(foto);
    }

    public boolean possuiFotoFrenteAtual() {
        return (getFotoFrenteAtual().getVisitante() != null);
    }

    public VisitanteFoto getFotoFrenteAtual() {
        return this.getFotos().stream().filter(
                visitanteFoto -> visitanteFoto.getTipo() == TipoFoto.FRENTE && visitanteFoto.getFotoAtual() == SimNao.S
        ).findFirst().orElse(new VisitanteFoto());
    }

    public Date getUltimaDataAprovacao() {
        List<Credenciamento> list = this.getCredenciamentos().stream().filter(
                credenciamento -> credenciamento.getCredencialAprovada() != null
        ).collect(Collectors.toList());

        return list.stream().max(Comparator.comparing(o -> o.getCredencialAprovada().getDataAprovacao())).map(
                credenciamento -> credenciamento.getCredencialAprovada().getDataAprovacao()
        ).orElse(null);
    }

    public boolean isImpedido() {
        if (this.numImpedimento == null) {
            return false;
        }
        Date hoje = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
        return this.numImpedimento.after(hoje) || this.numImpedimento.equals(hoje);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdBio() {
        if (this.id == null) return 0L;

        return this.id;
    }
    

    public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public void setIdBio(Long id) {
        this.setId(id);
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Visitante getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Visitante responsavel) {
        this.responsavel = responsavel;
    }

    public UnidadePrisional getUnidadePrisional() {
        return unidadePrisional;
    }

    public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
        this.unidadePrisional = unidadePrisional;
    }

    public Usuario getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(Usuario usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public IdentidadeDeGenero getIdentidadeDeGenero() {
        return identidadeDeGenero;
    }

    public void setIdentidadeDeGenero(IdentidadeDeGenero identidadeDeGenero) {
        this.identidadeDeGenero = identidadeDeGenero;
    }

    public SimNao getPne() {
        return pne;
    }

    public void setPne(SimNao pne) {
        this.pne = pne;
    }

    public SimNao getPnePermanente() {
        return pnePermanente;
    }

    public void setPnePermanente(SimNao pnePermanente) {
        this.pnePermanente = pnePermanente;
    }

    public SimNao getEmancipado() {
        return emancipado;
    }

    public void setEmancipado(SimNao emancipado) {
        this.emancipado = emancipado;
    }

    public Date getDataHoraCadastro() {
        return dataHoraCadastro;
    }

    public void setDataHoraCadastro(Date dataHoraCadastro) {
        this.dataHoraCadastro = dataHoraCadastro;
    }

    public TipoVisita getTipoVisita() {
        return tipoVisita;
    }

    public void setTipoVisita(TipoVisita tipoVisita) {
        this.tipoVisita = tipoVisita;
    }

    public StatusVisitante getStatus() {
        return status;
    }

    public void setStatus(StatusVisitante status) {
        this.status = status;
    }

    public Date getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public Long getCredenciamentosAtivos() {
        return credenciamentosAtivos;
    }

    public void setCredenciamentosAtivos(Long credenciamentosAtivos) {
        this.credenciamentosAtivos = credenciamentosAtivos;
    }

    public FaseCredenciamento getFaseCredenciamento() {
        if (this.faseCredenciamento == null) {
            return FaseCredenciamento.RECEBIMENTO;
        }
        return this.faseCredenciamento;
    }

    public void setFaseCredenciamento(FaseCredenciamento faseCredenciamento) {
        this.faseCredenciamento = faseCredenciamento;
    }

    public List<Visitante> getVisitantesReponsaveis() {
		return visitantesReponsaveis;
	}

	public void setVisitantesReponsaveis(List<Visitante> visitantesReponsaveis) {
		this.visitantesReponsaveis = visitantesReponsaveis;
	}

	public List<VisitanteCertidao> getCertidoes() {
        if (this.certidoes == null) {
            this.certidoes = new ArrayList<>();
        }
        return certidoes;
    }

    public void setCertidoes(List<VisitanteCertidao> certidoes) {
        this.certidoes = certidoes;
    }

    public List<VisitanteDocumento> getVisitanteDocumentos() {
        if (this.visitanteDocumentos == null) {
            this.visitanteDocumentos = new ArrayList<>();
        }
        return visitanteDocumentos;
    }

    public void setVisitanteDocumentos(List<VisitanteDocumento> visitanteDocumentos) {
        this.visitanteDocumentos = visitanteDocumentos;
    }

    public Date getNumImpedimento() {
        return numImpedimento;
    }

    public void setNumImpedimento(Date numImpedimento) {
        this.numImpedimento = numImpedimento;
    }

    public List<VisitanteFoto> getFotos() {
        if (this.fotos == null) {
            this.fotos = new ArrayList<>();
        }
        return fotos;
    }

    public void setFotos(List<VisitanteFoto> fotos) {
        this.fotos = fotos;
    }

    public List<Credenciamento> getCredenciamentos() {
        if (credenciamentos == null) {
        	credenciamentos = new ArrayList<>();
        }
        return credenciamentos;
    }

    public void setCredenciamentos(List<Credenciamento> credenciamentos) {
        this.credenciamentos = credenciamentos;
    }

    public List<Impedimento> getImpedimentos() {
        if (this.impedimentos == null) {
            this.impedimentos = new ArrayList<>();
        }
        return impedimentos;
    }

    public void setImpedimentos(List<Impedimento> impedimentos) {
        this.impedimentos = impedimentos;
    }

    public String getCPFResponsavel() {
        if (isRespPossuiDocumentos(this.responsavel.getPessoa().getDocumentos())) {
            List<Documento> documentos = this.responsavel.getPessoa().getDocumentos();
                Optional<Documento> doc = documentos.stream().filter(dc -> dc.getTipoDocumento().getDescricao().equalsIgnoreCase("CPF")).findFirst();
                if (doc.isPresent()) {
                    String cpf = doc.get().getNumero();
                    return cpf;
                }
        }

        return null;
    }

    private boolean isRespPossuiDocumentos(List<Documento> documentos) {
        try{
            if(documentos != null) {
                if (documentos.size() > 0) {
                    return true;
                }
            }
        }catch (Exception e) {
            return false;
        }

        return false;
    }
    
    public Imagem getImagemAtual() {
        return this.fotos.stream().filter(f -> f.isAtual() && f.getTipo() == TipoFoto.FRENTE).findFirst().get().getImagem();
    }

	public Integer getFaseAtual() {
		return faseAtual;
	}

	public void setFaseAtual(Integer faseAtual) {
		this.faseAtual = faseAtual;
	}

	public Integer getSituacaoAtual() {
		return situacaoAtual;
	}

	public void setSituacaoAtual(Integer situacaoAtual) {
		this.situacaoAtual = situacaoAtual;
	}

    @Override
    public String toString() {
        return "Visitante [id=" + id + ", nome=" + pessoa.getNome() + "]";
    }

	public Credenciamento getCadastro() {
		return cadastro;
	}

	public void setCadastro(Credenciamento cadastro) {
		this.cadastro = cadastro;
	}

	public List<EntradaSaida> getEntradas() {
		return entradas;
	}

	public void setEntradas(List<EntradaSaida> entradas) {
		this.entradas = entradas;
	}
	
    public AutorizacaoCadastroSemBiometria getAutorizacaoCadastroSemBiometria() {
		return autorizacaoCadastroSemBiometria;
	}

	public void setAutorizacaoCadastroSemBiometria(AutorizacaoCadastroSemBiometria autorizacaoCadastroSemBiometria) {
		this.autorizacaoCadastroSemBiometria = autorizacaoCadastroSemBiometria;
	}
}
