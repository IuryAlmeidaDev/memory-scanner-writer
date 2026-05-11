package br.gov.mt.sejudh.sigepen.model;

import br.gov.mt.sejudh.sigepen.conversores.EnumCustomConverter;
import br.gov.mt.sejudh.sigepen.enumerator.*;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Audited
@Entity
@Table(name="RECUPERANDO", schema = Sigepen.SEJUDH_SCHEMA)
public class Recuperando implements Serializable, PersistentAttributeInterceptable {

    private static final long serialVersionUID = 1019401164144456898L;

    @SequenceGenerator(name = "SEQ_RECUPERANDO_GENERATOR", sequenceName = "SEQ_RECUPERANDO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RECUPERANDO_GENERATOR")
    @Column(name = "ID")
    private long id;

    @Transient
	private long versao;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "PESSOA_ID", nullable = false)
    private Pessoa pessoa;

    @Column(name = "STATUS", nullable = false)
    @Convert(converter = EnumCustomConverter.class, disableConversion=true)
    @Type(type = "br.gov.mt.sejudh.sigepen.conversores.EnumCustomType")
    private StatusRecuperando status;

    @Enumerated(EnumType.STRING)
    @Column(name = "PERICULOSIDADE")
    private Periculosidade periculosidade;

    @Column(name = "OBSERVACAO", length = 250)
    private String observacao;

    @Column(name = "ALTURA", nullable = false, precision = 2)
    private Float altura;

    @Column(name = "PESO", precision = 3)
    private Float peso;

    @Enumerated
    @Column(name = "COR_OLHO")
    private CorOlho corOlho;

    @Enumerated
    @Column(name = "COR_CABELO")
    private CorCabelo corCabelo;

    @Column(name = "TEM_SINAL_TATUAGEM", nullable = false)
    private Boolean temSinalOuTatuagem;

    @Column(name = "MONITORAMENTO_ELETRONICO")
    private Boolean monitoramentoEletronico;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="recuperando", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<Alcunha> alcunhas;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="recuperando", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<OutroNome> outrosNomes;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="recuperando", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<Documento> documentos;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="recuperando", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<Parente> parentes;

    @Column(name = "ALTERACAO_PARENTES")
    @Temporal(TemporalType.TIMESTAMP)
    private Date alteracaoParentes;

    @Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORIENTACAO_SEXUAL_ID")
    private OrientacaoSexual orientacaoSexual;

    @Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDENTIDADE_GENERO_ID")
    private IdentidadeDeGenero identidadeDeGenero;

    @Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ESCOLARIDADE_ID")
    private Escolaridade escolaridade;

    @Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="RACA_ID")
    private Raca raca;

    @Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ESTADO_CIVIL_ID")
    private EstadoCivil estadoCivil;

    @Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="TIPO_SANGUINEO_ID")
    private TipoSanguineo tipoSanguineo;

    @Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="RELIGIAO_ID")
    private Religiao religiao;

    @Column(name = "PROFISSAO", length = 80)
    private String profissao;

    @Enumerated
    @Column(name = "PNE")
    private TipoDeficiencia pne;

    @Column(name = "GESTANTE")
    private SimNao gestante;

    @OneToMany(orphanRemoval=true, mappedBy="recuperando", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy(clause = "dataEntrada DESC")
    private Set<Passagem> passagens;

    @OneToMany(orphanRemoval=true, mappedBy="recuperando", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Foto> fotos;

    @OneToMany(orphanRemoval=true, mappedBy="recuperando", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Particularidade> particularidades;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="recuperando", fetch=FetchType.LAZY)
    private List<Processo> processos;
    
    @OneToMany(orphanRemoval=true, mappedBy="recuperando", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private List<HistoricoRecuperando> historicos;

	@Transient
    private String naturalidade;
    
    @Transient
    private Boolean tornarRecuperando;

    @Transient
    private Long tornarRecuperandoIdVisitante;
    
    @Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGIME_ATUAL_ID")
    private Regime regimeAtual;

    @Enumerated(EnumType.STRING)
    @Column(name = "CONDICAO_ATUAL")
    private Condicao condicaoAtual;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="recuperando", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<FaltaDisciplinar> faltas;

    @Transient
    private AutoriaRegistro autorRegistro;

    @Transient
    private AutoriaRegistro autorUltimaAlteracao;

    @Transient
    private int quantFilhos;

    @Transient
    private String conjuge;

    @Transient
    private String alcunhaPrincipal;

    @Transient
    private int idade;

    @Transient
    private String lotacaoAtual;

    @Transient
    private String unidadeAtual;

    @Transient
    private Long unidadeId;

    @Transient
    private PersistentAttributeInterceptor persistentAttributeInterceptor;

    @Transient
    private Imagem imagemAtual;

    @Column(name = "DATA_CADASTRO_PARENTE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastroParente;
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="recuperando", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<SolicitacaoVisita> solicitacoes;
    
    @Column(name = "INICIO_GESTACAO")
    private Date inicioGestacao;

    @Column(name = "INICIO_LACTANTE")
    private Date inicioLactante;

    @Column(name = "LACTANTE")
    private SimNao lactante;
    
    @Column(name = "POSSUI_FILHO")
    private SimNao possuiFilho;
    
    @Column(name = "FILHO_PCD")
    private SimNao filhoPcd;
    
    @Column(name = "APOSENTADO")
    private SimNao aposentado;
    
    @Column(name = "RECEBE_BENEFICIO")
    private SimNao recebeBeneficio;
    
    @Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ID_ETNIA")
    private Etnia etnia;

	@OneToOne(mappedBy = "recuperando", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	private AutorizacaoCadastroSemBiometria autorizacaoCadastroSemBiometria;	
	
	@Column(name = "PERICULOSIDADE_ADAPTATIVA")
    private Integer periculosidadeAdaptativa;
	
	@PrePersist
    @PreUpdate
    private void updatePericulosidadeAdaptativa() {
        if (periculosidade != null) {
            this.periculosidadeAdaptativa = periculosidade.ordinal();
        }
    }

	public Recuperando() {
        this.passagens = new HashSet<Passagem>();
        this.fotos = new ArrayList<Foto>();
        this.particularidades = new ArrayList<Particularidade>();
        this.documentos = new ArrayList<Documento>();
        this.processos = new ArrayList<Processo>();
        this.historicos = new ArrayList<HistoricoRecuperando>();
        this.setTornarRecuperando(false);
        this.setTornarRecuperandoIdVisitante(null);
    }

    public Recuperando(long id) {
        this.id = id;
        this.setTornarRecuperando(false);
        this.setTornarRecuperandoIdVisitante(null);
    }

    public Recuperando(long id, String nome) {
        this.id = id;
        this.pessoa = new Pessoa();
        this.pessoa.setNome(nome);
        this.setTornarRecuperando(false);
        this.setTornarRecuperandoIdVisitante(null);
    }

    public Recuperando(StatusRecuperando status) {
        this.passagens = new HashSet<Passagem>();
        this.fotos = new ArrayList<Foto>();
        this.particularidades = new ArrayList<Particularidade>();
        this.documentos = new ArrayList<Documento>();
        this.setStatus(status);
        this.processos = new ArrayList<Processo>();
        this.historicos = new ArrayList<HistoricoRecuperando>();
        this.setTornarRecuperando(false);
        this.setTornarRecuperandoIdVisitante(null);
    }

    public Recuperando(Pessoa pessoa) {
        this.pessoa = pessoa;
        this.setTornarRecuperando(false);
        this.setTornarRecuperandoIdVisitante(null);
    }

    public Passagem getPassagemAtual() {
        for (Passagem passagem : passagens) {
            if (passagem.getDataSaida() == null) {
                return passagem;
            }
        }

        return new Passagem();
    }

    public PassagemLotacao getLotacao() {
        for (Passagem passagem : passagens) {
        	for(PassagemLotacao p : passagem.getLotacao()) {
            	if(p.getDataSaida() == null)
            		return p;
        		
        	}
        }

        return new PassagemLotacao();
    }
    
    public Passagem getUltimaPassagem() {
        return passagens.stream().max(Comparator.comparing(Passagem::getId)).get();
    }

    public void adicionarFotoFrente(Foto foto) {
        for (Iterator<Foto> i = this.fotos.iterator(); i.hasNext();) {
            Foto f = i.next();

            if (f.getTipo().equals(foto.getTipo()) && f.getId() == foto.getId()) {
                i.remove();
            }
        }

        this.fotos.add(foto);
    }

    public boolean possuiFotoFrenteAtual() {
        return (getFotoFrenteAtual().getId() != -1);
    }

    public Foto getFotoFrenteAtual() {
        Foto fotoAtual = new Foto();
        fotoAtual.setId(-1);
        Foto fotoNaoSalva = null;

        for (Foto foto : this.fotos) {
            if (foto.getTipo() == TipoFoto.FRENTE && foto.getIsAtual()) {
                if (foto.getId() > 0) {
                    fotoAtual = foto;
                } else {
                    fotoNaoSalva = foto;
                }
            }
        }

        return (fotoNaoSalva != null) ? fotoNaoSalva : fotoAtual;
    }

    public Imagem getImagemAtual() {
        return this.fotos.stream().filter(f -> f.getIsAtual() && f.getTipo() == TipoFoto.FRENTE).findFirst().get().getImagem();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public StatusRecuperando getStatus() {
        return status;
    }

    public void setStatus(StatusRecuperando status) {
        this.status = status;
    }

    public Float getAltura() {
        return altura;
    }

    public void setAltura(Float altura) {
        this.altura = altura;
    }

    public CorOlho getCorOlho() {
        return corOlho;
    }

    public void setCorOlho(CorOlho corOlho) {
        this.corOlho = corOlho;
    }

    public CorCabelo getCorCabelo() {
        return corCabelo;
    }

    public void setCorCabelo(CorCabelo corCabelo) {
        this.corCabelo = corCabelo;
    }

    public OrientacaoSexual getOrientacaoSexual() {
        return orientacaoSexual;
    }

    public void setOrientacaoSexual(OrientacaoSexual orientacaoSexual) {
        this.orientacaoSexual = orientacaoSexual;
    }

    public IdentidadeDeGenero getIdentidadeDeGenero() {
        return identidadeDeGenero;
    }

    public void setIdentidadeDeGenero(IdentidadeDeGenero identidadeDeGenero) {
        this.identidadeDeGenero = identidadeDeGenero;
    }

    public Escolaridade getEscolaridade() {
        return escolaridade;
    }

    public void setEscolaridade(Escolaridade escolaridade) {
        this.escolaridade = escolaridade;
    }

    public Raca getRaca() {
        return raca;
    }

    public void setRaca(Raca raca) {
        this.raca = raca;
    }

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public TipoSanguineo getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(TipoSanguineo tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public Religiao getReligiao() {
        return religiao;
    }

    public void setReligiao(Religiao religiao) {
        this.religiao = religiao;
    }

    public void setNaturalidade(String naturalidade) {
        this.naturalidade = Sigepen.formatarEspacos(naturalidade);
    }

    public TipoDeficiencia getPne() {
        return pne;
    }

    public void setPne(TipoDeficiencia pne) {
        this.pne = pne;
    }

    public SimNao isGestante() {
        return gestante;
    }

    public void setGestante(SimNao gestante) {
        this.gestante = gestante;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = Sigepen.formatarEspacos(profissao);
    }

    public Float getPeso() {
        return peso;
    }

    public void setPeso(Float peso) {
        this.peso = peso;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        if (this.persistentAttributeInterceptor != null) {
            this.pessoa = (Pessoa) this.persistentAttributeInterceptor.writeObject(this, "pessoa", this.pessoa, pessoa);
        } else {
            this.pessoa = pessoa;
        }
    }

    public List<Documento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }

    public Set<Passagem> getPassagens() {
        return passagens;
    }

    public void setPassagens(Set<Passagem> passagens) {
        this.passagens = passagens;
    }

    public List<Foto> getFotos() {
        Collections.sort(fotos);
        return fotos;
    }

    public void setFotos(List<Foto> fotos) {
        this.fotos = fotos;
    }

    public List<Particularidade> getParticularidades() {
        Collections.sort(particularidades);
        return particularidades;
    }

    public void setParticularidades(List<Particularidade> particularidades) {
        this.particularidades = particularidades;
    }

    public Date getAlteracaoParentes() {
        return alteracaoParentes;
    }

    public void setAlteracaoParentes(Date alteracaoParentes) {
        this.alteracaoParentes = alteracaoParentes;
    }

    public String getNaturalidade() {

        if (this.getPessoa() == null){
            return null;
        }

        if (this.getPessoa().getNaturalidade() != null) {
            return this.getPessoa().getNaturalidade().getNome() + ", " + this.getPessoa().getNaturalidade().getEstado().getUf() + ", BRASIL";
        } else {
        	if(this.getPessoa().getPais() != null)
        		return this.getPessoa().getPais().getNome();
        	return null;
        }
    }

    public Boolean getTemSinalOuTatuagem() {
        return temSinalOuTatuagem;
    }

    public void setTemSinalOuTatuagem(Boolean temSinalOuTatuagem) {
        this.temSinalOuTatuagem = temSinalOuTatuagem;
    }

    public List<Alcunha> getAlcunhas() {
        return alcunhas;
    }

    public void setAlcunhas(List<Alcunha> alcunhas) {
        this.alcunhas = alcunhas;
    }

    public List<OutroNome> getOutrosNomes() {
        return outrosNomes;
    }

    public void setOutrosNomes(List<OutroNome> outrosNomes) {
        this.outrosNomes = outrosNomes;
    }

    public Periculosidade getPericulosidade() {
        return periculosidade;
    }

    public void setPericulosidade(Periculosidade periculosidade) {
        this.periculosidade = periculosidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = Sigepen.formatarEspacos(observacao);
    }

    public List<Processo> getProcessos() {
        return processos;
    }

    public void setProcessos(List<Processo> processos) {
        this.processos = processos;
    }

    public List<Parente> getParentes() {
        return parentes;
    }

    public void setParentes(List<Parente> parentes) {
        this.parentes = parentes;
    }

    public Boolean getMonitoramentoEletronico() {
        return monitoramentoEletronico;
    }

    public void setMonitoramentoEletronico(Boolean monitoramentoEletronico) {
        this.monitoramentoEletronico = monitoramentoEletronico;
    }

    public Regime getRegimeAtual() {
        return regimeAtual;
    }

    public void setRegimeAtual(Regime regimeAtual) {
        this.regimeAtual = regimeAtual;
    }

    public Condicao getCondicaoAtual() {
        return condicaoAtual;
    }

    public void setCondicaoAtual(Condicao condicaoAtual) {
        this.condicaoAtual = condicaoAtual;
    }


    public AutoriaRegistro getAutorRegistro() {
        return autorRegistro;
    }

    public void setAutorRegistro(AutoriaRegistro autorRegistro) {
        this.autorRegistro = autorRegistro;
    }

    public AutoriaRegistro getAutorUltimaAlteracao() {
        return autorUltimaAlteracao;
    }

    public void setAutorUltimaAlteracao(AutoriaRegistro autorUltimaAlteracao) {
        this.autorUltimaAlteracao = autorUltimaAlteracao;
    }

    public int getQuantFilhos() {
        return quantFilhos;
    }

    public void setQuantFilhos(int quantFilhos) {
        this.quantFilhos = quantFilhos;
    }

    public String getConjuge() {
        return conjuge;
    }

    public void setConjuge(String conjuge) {
        this.conjuge = conjuge;
    }

    public String getAlcunhaPrincipal() {
        return alcunhaPrincipal;
    }

    public void setAlcunhaPrincipal(String alcunhaPrincipal) {
        this.alcunhaPrincipal = alcunhaPrincipal;
    }

    public int getIdade() {
        Calendar nascimento = new GregorianCalendar();
        nascimento.setTime(this.getPessoa().getDataNascimento());
        Calendar dataAtual = Calendar.getInstance();
        this.setIdade(dataAtual.get(Calendar.YEAR) - nascimento.get(Calendar.YEAR));
        nascimento.add(Calendar.YEAR, idade);

        if (dataAtual.before(nascimento)) {
            idade--;
        }
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getLotacaoAtual() {
        return lotacaoAtual;
    }

    public void setLotacaoAtual(String lotacaoAtual) {
        this.lotacaoAtual = lotacaoAtual;
    }

    public String getUnidadeAtual() {
        return unidadeAtual;
    }

    public void setUnidadeAtual(String unidadeAtual) {
        this.unidadeAtual = unidadeAtual;
    }

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }
               

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((altura == null) ? 0 : altura.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((pessoa == null) ? 0 : pessoa.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Recuperando))
            return false;
        Recuperando other = (Recuperando) obj;
        if (altura == null) {
            if (other.altura != null)
                return false;
        } else if (!altura.equals(other.altura))
            return false;
        if (id != other.id)
            return false;
        if (pessoa == null) {
            if (other.pessoa != null)
                return false;
        } else if (!pessoa.equals(other.pessoa))
            return false;
        if (status != other.status)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Recuperando [id=" + id + " ] ";
    }

    @Override
    public PersistentAttributeInterceptor $$_hibernate_getInterceptor() {
        return this.persistentAttributeInterceptor;
    }

    @Override
    public void $$_hibernate_setInterceptor(PersistentAttributeInterceptor persistentAttributeInterceptor) {
        this.persistentAttributeInterceptor = persistentAttributeInterceptor;
    }

	public Long getTornarRecuperandoIdVisitante() {
		return tornarRecuperandoIdVisitante;
	}

	public void setTornarRecuperandoIdVisitante(Long tornarRecuperandoIdVisitante) {
		this.tornarRecuperandoIdVisitante = tornarRecuperandoIdVisitante;
	}

	public Boolean isTornarRecuperando() {
		return tornarRecuperando;
	}

	public void setTornarRecuperando(Boolean tornarRecuperando) {
		this.tornarRecuperando = tornarRecuperando;
	}

	public List<FaltaDisciplinar> getFaltas() {
		return faltas;
	}

	public void setFaltas(List<FaltaDisciplinar> faltas) {
		this.faltas = faltas;
	}

	public Date getDataCadastroParente() {
		return dataCadastroParente;
	}

	public void setDataCadastroParente(Date dataCadastroParente) {
		this.dataCadastroParente = dataCadastroParente;
	}

	public List<SolicitacaoVisita> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(List<SolicitacaoVisita> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public Date getInicioGestacao() {
		return inicioGestacao;
	}

	public void setInicioGestacao(Date inicioGestacao) {
		this.inicioGestacao = inicioGestacao;
	}

	public boolean ehLactante() {
		if(this != null && this.lactante != null)
			return SimNao.S.equals(this.lactante);
		return false;
	}

	public void setLactante(SimNao lactante) {
		this.lactante = lactante;
	}

	public boolean ehPossuiFilho() {
		if(this != null && this.possuiFilho != null)
			return SimNao.S.equals(this.possuiFilho);
		return false;
	}

	public void setPossuiFilho(SimNao possuiFilho) {
		this.possuiFilho = possuiFilho;
	}

	public boolean ehFilhoPcd() {
		if(this != null && this.filhoPcd != null)
			return SimNao.S.equals(this.filhoPcd);
		return false;
	}

	public void setFilhoPcd(SimNao filhoPcd) {
		this.filhoPcd = filhoPcd;
	}

	public boolean ehAposentado() {
		if(this != null && this.aposentado != null)
			return SimNao.S.equals(this.aposentado);
		return false;
	}

	public void setAposentado(SimNao aposentado) {
		this.aposentado = aposentado;
	}

	public boolean ehRecebeBeneficio() {
		if(this != null && this.recebeBeneficio != null)
			return SimNao.S.equals(this.recebeBeneficio);
		return false;
	}

	public void setRecebeBeneficio(SimNao recebeBeneficio) {
		this.recebeBeneficio = recebeBeneficio;
	}

	public Etnia getEtnia() {
		return etnia;
	}

	public void setEtnia(Etnia etnia) {
		this.etnia = etnia;
	}

	public Date getInicioLactante() {
		return inicioLactante;
	}

	public void setInicioLactante(Date inicioLactante) {
		this.inicioLactante = inicioLactante;
	}

	public SimNao getLactante() {
		return lactante;
	}

	public SimNao getPossuiFilho() {
		return possuiFilho;
	}

	public SimNao getFilhoPcd() {
		return filhoPcd;
	}

	public SimNao getAposentado() {
		return aposentado;
	}

	public SimNao getRecebeBeneficio() {
		return recebeBeneficio;
	}

	public boolean ehGestante() {
		if(this != null && this.gestante != null)
			return SimNao.S.equals(this.gestante);
		return false;
    }

    public SimNao getGestante() {
		return gestante;
	}

	public List<HistoricoRecuperando> getHistoricos() {
		return historicos;
	}

	public void setHistoricos(List<HistoricoRecuperando> historicos) {
		if (this.historicos == null) {
			this.historicos = new ArrayList<HistoricoRecuperando>();
		}

		if (historicos == null || this.historicos == historicos) {
			return;
		}

		this.historicos.clear();
		for (HistoricoRecuperando historico : historicos) {
			if (historico != null) {
				historico.setRecuperando(this);
				this.historicos.add(historico);
			}
		}
	}

	public AutorizacaoCadastroSemBiometria getAutorizacaoCadastroSemBiometria() {
		return autorizacaoCadastroSemBiometria;
	}

	public void setAutorizacaoCadastroSemBiometria(AutorizacaoCadastroSemBiometria autorizacaoCadastroSemBiometria) {
		this.autorizacaoCadastroSemBiometria = autorizacaoCadastroSemBiometria;
	}

	public Integer getPericulosidadeAdaptativa() {
		if (periculosidadeAdaptativa == null && periculosidade != null) {
			return periculosidade.ordinal();
		}
		return periculosidadeAdaptativa;
	}

	public void setPericulosidadeAdaptativa(Integer periculosidadeAdaptativa) {
		this.periculosidadeAdaptativa = periculosidadeAdaptativa;
	}
	
	


	 
}
