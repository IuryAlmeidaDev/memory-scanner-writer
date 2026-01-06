package br.gov.mt.sejudh.sigepen.bean;

import br.gov.mt.sejudh.sigepen.enumerator.*;
import br.gov.mt.sejudh.sigepen.model.*;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.service.ComboService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean(eager=true)
@ApplicationScoped
public class ComboHelperBean {

	private Status[] status;

	private SituacaoUnidade[] situacaoUnidade;

	private SituacaoUnidade[] situacaoUnidadeTodas;

	private Sexo[] sexos;

	private CorOlho[] corOlho;

	private CorCabelo[] corCabelo;

	private Periculosidade[] periculosidade;

	private TipoContato[] tipoContato;

	private TipoInterdicao[] tipoInterdicao;

	private TipoDeficiencia[] tipoDeficiencia;

	private List<TipoVisita> tipoVisita;

	private List<Pais> paises;

	private List<TipoSanguineo> tipoSanguineo;

	private List<Regime> regimes;

	private List<Religiao> religioes;

	private List<Raca> racas;

	private List<EstadoCivil> estadoCivil;

	private List<OrientacaoSexual> orientacaoSexual;

	private List<IdentidadeDeGenero> identidadeDeGenero;

	private List<Escolaridade> escolaridades;

	private List<Parentesco> parentescos;

	private List<TipoServidor> tipoServidor;

	private List<Funcionalidade> funcionalidades;

	private List<Estado> estados;

	private List<UnidadePrisional> unidadesPrisionais;

	private List<TipoUnidade> tipoUnidades;

	private List<TipoDocumento> tipoDocumento;

	private List<Nacionalidade> nacionalidades;

	private StatusRecuperando[] statusRecuperando;

	private TipoFoto[] tipoFoto;

	private StatusPedido[] statusPedido;

	private StatusMovimentacao[] statusMovimentacao;

	private TipoProcesso[] tipoProcesso;

	private List<TipoTransito> tipoTransito;

	private Condicao[] condicao;

	private Modalidade[] modalidade;

	private TipoParticularidade[] tipoParticularidade;

	private TipoDesligamento[] tipoDesligamento;

	private Acao[] acao;

	private TipoDocumentoPedidoTransito[] tipoDocumentoPedidoTransito;

	private PronomeTratamento[] pronomeTratamento;

	private Esfera[] esfera;

	private TipoAtestado[] atestados;

	private TipoEntrada[] tipoEntrada;

	private NaturezaFaltaDisciplinar[] naturezaFaltaDisciplinar;

	private SituacaoFaltaDisciplinar[] situacaoFaltaDisciplinars;

	private SimNao[] simNao;

	private FaseCredenciamento[] faseCredenciamento;

	private StatusCredenciamento[] situacaoCredenciamento;

	private StatusCredenciamentosVisita[] situacaoCredenciamentoVisita;

	private StatusCredenciamento[] filtroSituacaoCredenciamento;

	private StatusCredenciamentosVisita[] filtroSituacaoCredenciamentoVisita;

	private SituacaoVisita [] situacaoVisita;

	private IdadeRecuperando[] idadeRecuperando;

	private TempoCondenacao[] tempoCondenacao;

	private Lgbt[] lgbt;

	private TipoRelatorioEntradaSaidaRec[] TipoRelatorioEntradaSaidaRec;

	private PopulacaoFeminino[] populacaoFeminino;

	private TipoVisitaObrigatoriedade[] tipoVisitaObrigatoriedade;

	private UnidadeMedida[] unidadesMedida;

	private VencimentoCredenciamento[] vencimentoCredenciamentos;

	private Relatorio[] relatorio;

    private TipoPlantao[] plantoes;

	@Inject
	private ComboService comboService;

	@PostConstruct
	public void init() {
		status 			 	 		= comboService.getStatus();
		sexos	         	 		= comboService.getSexo();
		corOlho              		= comboService.getCorOlho();
		corCabelo            		= comboService.getCorCabelo();
		periculosidade		 		= comboService.getPericulosidade();
		tipoContato	     	 		= comboService.getTipoContato();
		tipoInterdicao	 	 		= comboService.getTipoInterdicao();
		tipoDeficiencia	 	 		= comboService.getTipoDeficiencia();
		paises 			 	 		= comboService.listarPais();
		situacaoUnidade  	 		= comboService.getSituacaoUnidade();
		regimes 		 	 		= comboService.listarRegime();
		religioes 		 	 		= comboService.listarReligiao();
		racas 		 	 	 		= comboService.listarRaca();
		estadoCivil 	 	 		= comboService.listarEstadoCivil();
		tipoSanguineo 	 	 		= comboService.listarTipoSanguineo();
		orientacaoSexual 	 		= comboService.listarOrientacaoSexual();
		identidadeDeGenero			= comboService.listarIdentidadeDeGenero();
		escolaridades 	 	 		= comboService.listarEscolaridade();
		parentescos 	 	 		= comboService.listarParentesco();
		tipoServidor 	 	 		= comboService.listarTipoServidor();
		estados			 	 		= comboService.listarEstado();
		unidadesPrisionais 	 		= comboService.listarUnidades();
		funcionalidades  	 		= comboService.listarFuncionalidades();
		tipoUnidades	 	 		= comboService.listarTipoUnidade();
		tipoDocumento	 	 		= comboService.listarTipoDocumento();
		nacionalidades		 		= comboService.listarNacionalidades();
		situacaoUnidadeTodas 		= comboService.getSituacaoUnidadeTodas();
		statusRecuperando    		= comboService.getStatusRecuperando();
		tipoFoto             		= comboService.getTipoFoto();
		tipoProcesso		 		= comboService.getTipoProcesso();
		tipoTransito     	 		= comboService.getTipoTransito();
		condicao			 		= comboService.getCondicao();
		modalidade					= comboService.getModalidade();
		tipoParticularidade  		= comboService.getTipoParticularidade();
		statusPedido		 		= comboService.getStatusPedido();
		statusMovimentacao	 		= comboService.getStatusMovimentacao();
		tipoDesligamento	 		= comboService.getTipoDesligamento();
		acao				 		= comboService.getAcao();
		pronomeTratamento	 		= comboService.getPronomeTratamento();
		tipoDocumentoPedidoTransito = comboService.getTipoDocumentoPedidoTransito();
		esfera						= comboService.getEsfera();
		atestados					= comboService.getAtestados();
		tipoEntrada					= comboService.getTipoEntrada();
		simNao						= comboService.getSimNao();
		faseCredenciamento			= comboService.getFaseCredenciamento();
		situacaoCredenciamentoVisita = comboService.getSituacaoCredenciamentoVisita();
		situacaoCredenciamento		= comboService.getSituacaoCredenciamento();
		naturezaFaltaDisciplinar    = comboService.getNaturezaFaltaDisciplinar();
		situacaoFaltaDisciplinars   = comboService.getSituacaoFaltaDisciplinar();
		filtroSituacaoCredenciamento= comboService.getFiltroSituacaoCredenciamento();
		filtroSituacaoCredenciamentoVisita = comboService.getFiltroSituacaoCredenciamentoVisita();
		tipoVisita = comboService.getTipoVisitas();
		situacaoVisita = comboService.getSituacaoVisita();
		idadeRecuperando = comboService.getIdadeRecuperando();
		tempoCondenacao = comboService.getTempoCondenacao();
		lgbt = comboService.getLgbt();
		TipoRelatorioEntradaSaidaRec = comboService.getTipoRelatorioEntradaSaidaRec();
		populacaoFeminino = comboService.getPopulacaoFeminino();
		tipoVisitaObrigatoriedade = comboService.getTipoVisitaObrigatoriedade();
		unidadesMedida = comboService.getUnidadesMedida();
		vencimentoCredenciamentos = comboService.getVencimentoCredenciamento();
		relatorio = comboService.getRelatorio();
        plantoes = comboService.getTipoPlantao();
	}

	public NaturezaFaltaDisciplinar[] getNaturezaFaltaDisciplinar(){
		return this.naturezaFaltaDisciplinar;
	}

	public SituacaoFaltaDisciplinar[] getSituacaoFaltaDisciplinar() { return this.situacaoFaltaDisciplinars; }

	public Date getMaxDate(){
		Calendar now = Calendar.getInstance();
        now.add(Calendar.YEAR, -18);
        return now.getTime();
	}

	public Date getDateMinima(){
		Calendar now = Calendar.getInstance();
        return now.getTime();
	}

	public Status[] getStatus() {
		return status;
	}

	public SgpStatusAlternativo[] getSgpStatusAlternativo() {
		return SgpStatusAlternativo.values();
	}

    public SgpStatus[] getStatusSgp() {
        return SgpStatus.values();
    }

    public StatusVisitante[] getStatusVisitantes() {
        return StatusVisitante.values();
    }

	public List<Pais> getPaises() {
		return paises;
	}

	public void setPaises(List<Pais> paises) {
		this.paises = paises;
	}

	public void setStatus(Status[] status) {
		this.status = status;
	}

	public SituacaoUnidade[] getSituacaoUnidade() {
		return situacaoUnidade;
	}

	public void setSituacaoUnidade(SituacaoUnidade[] situacaoUnidade) {
		this.situacaoUnidade = situacaoUnidade;
	}

	public TipoInterdicao[] getTipoInterdicao() {
		return tipoInterdicao;
	}

	public void setTipoInterdicao(TipoInterdicao[] tipoInterdicao) {
		this.tipoInterdicao = tipoInterdicao;
	}

	public List<Regime> getRegimes() {
		return regimes;
	}

	public List<Raca> getRacas() {
		return racas;
	}

	public void setRacas(List<Raca> racas) {
		this.racas = racas;
	}

	public void setRegimes(List<Regime> regimes) {
		this.regimes = regimes;
	}

	public List<Religiao> getReligioes() {
		return religioes;
	}

	public void setReligioes(List<Religiao> religioes) {
		this.religioes = religioes;
	}

	public List<EstadoCivil> getEstadoCivil() {
		return estadoCivil;
	}

	public List<TipoSanguineo> getTipoSanguineo() {
		return tipoSanguineo;
	}

	public void setTipoSanguineo(List<TipoSanguineo> tipoSanguineo) {
		this.tipoSanguineo = tipoSanguineo;
	}

	public void setEstadoCivil(List<EstadoCivil> estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public List<OrientacaoSexual> getOrientacaoSexual() {
		return orientacaoSexual;
	}

	public void setOrientacaoSexual(List<OrientacaoSexual> orientacaoSexual) {
		this.orientacaoSexual = orientacaoSexual;
	}

	public List<IdentidadeDeGenero> getIdentidadeDeGenero() {
		return identidadeDeGenero;
	}

	public void setIdentidadeDeGenero(List<IdentidadeDeGenero> identidadeDeGenero) {
		this.identidadeDeGenero = identidadeDeGenero;
	}

	public List<Escolaridade> getEscolaridades() {
		return escolaridades;
	}

	public void setEscolaridades(List<Escolaridade> escolaridades) {
		this.escolaridades = escolaridades;
	}

	public List<Parentesco> getParentescos() {
		return parentescos;
	}

	public void setParentescos(List<Parentesco> parentescos) {
		this.parentescos = parentescos;
	}

	public List<TipoServidor> getTipoServidor() {
		return tipoServidor;
	}

	public void setTipoServidor(List<TipoServidor> tipoServidor) {
		this.tipoServidor = tipoServidor;
	}

	public List<TipoHistorico> getTipoHistoricoAdd() {
		return comboService.listarTipoHistoricoPermitidoParaCadastrar();
	}

	public List<TipoHistorico> getTipoHistoricoView() {
		return comboService.listarTipoHistoricoParaVisualizar();
	}
	
	public List<TipoHistorico> getTipoHistoricoSemPermissao() {
		return comboService.listarTipoHistoricoSemPermissao();
	}

	public List<Estado> getEstados() {
		return estados;
	}

	public void setEstados(List<Estado> estados) {
		this.estados = estados;
	}

	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}

	public void setUnidadesPrisionais(List<UnidadePrisional> unidadesPrisionais) {
		this.unidadesPrisionais = unidadesPrisionais;
	}

	public List<TipoUnidade> getTipoUnidades() {
		return tipoUnidades;
	}

	public void setTipoUnidades(List<TipoUnidade> tipoUnidades) {
		this.tipoUnidades = tipoUnidades;
	}

	public List<Funcionalidade> getFuncionalidades() {
		for (Funcionalidade funcionalidade : funcionalidades) {
			System.out.println("Funcionalidade id " + funcionalidade.getId() + ": " + funcionalidade.getNome());
		}
		return funcionalidades;
	}

	public void setFuncionalidades(List<Funcionalidade> funcionalidades) {
		this.funcionalidades = funcionalidades;
	}

	public Sexo[] getSexos() {
		return sexos;
	}

	public void setSexos(Sexo[] sexos) {
		this.sexos = sexos;
	}

	public TipoContato[] getTipoContato() {
		return tipoContato;
	}

	public void setTipoContato(TipoContato[] tipoContato) {
		this.tipoContato = tipoContato;
	}

	public SituacaoUnidade[] getSituacaoUnidadeTodas() {
		return situacaoUnidadeTodas;
	}

	public void setSituacaoUnidadeTodas(SituacaoUnidade[] situacaoUnidadeTodas) {
		this.situacaoUnidadeTodas = situacaoUnidadeTodas;
	}

	public List<TipoDocumento> getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(List<TipoDocumento> tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public List<Nacionalidade> getNacionalidades() {
		return nacionalidades;
	}

	public void setNacionalidades(List<Nacionalidade> nacionalidades) {
		this.nacionalidades = nacionalidades;
	}

	public StatusRecuperando[] getStatusRecuperando() {
		return statusRecuperando;
	}

	public void setStatusRecuperando(StatusRecuperando[] statusRecuperando) {
		this.statusRecuperando = statusRecuperando;
	}		

	public TipoDeficiencia[] getTipoDeficiencia() {
		return tipoDeficiencia;
	}

	public void setTipoDeficiencia(TipoDeficiencia[] tipoDeficiencia) {
		this.tipoDeficiencia = tipoDeficiencia;
	}

	public CorOlho[] getCorOlho() {
		return corOlho;
	}

	public void setCorOlho(CorOlho[] corOlho) {
		this.corOlho = corOlho;
	}

	public CorCabelo[] getCorCabelo() {
		return corCabelo;
	}

	public void setCorCabelo(CorCabelo[] corCabelo) {
		this.corCabelo = corCabelo;
	}

	public Periculosidade[] getPericulosidade() {
		return periculosidade;
	}

	public void setPericulosidade(Periculosidade[] periculosidade) {
		this.periculosidade = periculosidade;
	}

	public TipoFoto[] getTipoFoto() {
		return tipoFoto;
	}

	public void setTipoFoto(TipoFoto[] tipoFoto) {
		this.tipoFoto = tipoFoto;
	}

	public TipoProcesso[] getTipoProcesso() {
		return tipoProcesso;
	}

	public void setTipoProcesso(TipoProcesso[] tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
	}		

	public List<TipoTransito> getTipoTransito() {
		return tipoTransito;
	}

	public void setTipoTransito(List<TipoTransito> tipoTransito) {
		this.tipoTransito = tipoTransito;
	}

	public Condicao[] getCondicao() {
		return condicao;
	}

	public void setCondicao(Condicao[] condicao) {
		this.condicao = condicao;
	}

	public Modalidade[] getModalidade() {
		return modalidade;
	}

	public void setModalidade(Modalidade[] modalidade) {
		this.modalidade = modalidade;
	}

	public TipoParticularidade[] getTipoParticularidade() {
		return tipoParticularidade;
	}

	public void setTipoParticularidade(TipoParticularidade[] tipoParticularidade) {
		this.tipoParticularidade = tipoParticularidade;
	}

	public StatusPedido[] getStatusPedido() {
		return statusPedido;
	}

	public TipoDesligamento[] getTipoDesligamento() {
		return tipoDesligamento;
	}

	public void setTipoDesligamento(TipoDesligamento[] tipoDesligamento) {
		this.tipoDesligamento = tipoDesligamento;
	}

	public Acao[] getAcao() {
		return acao;
	}

	public void setAcao(Acao[] acao) {
		this.acao = acao;
	}

	public StatusMovimentacao[] getStatusMovimentacao() {
		return statusMovimentacao;
	}

	public void setStatusMovimentacao(StatusMovimentacao[] statusMovimentacao) {
		this.statusMovimentacao = statusMovimentacao;
	}

	public void setStatusPedido(StatusPedido[] statusPedido) {
		this.statusPedido = statusPedido;
	}

	public TipoDocumentoPedidoTransito[] getTipoDocumentoPedidoTransito() {
		return tipoDocumentoPedidoTransito;
	}

	public void setTipoDocumentoPedidoTransito(TipoDocumentoPedidoTransito[] tipoDocumentoPedidoTransito) {
		this.tipoDocumentoPedidoTransito = tipoDocumentoPedidoTransito;
	}

	public PronomeTratamento[] getPronomeTratamento() {
		return pronomeTratamento;
	}

	public void setPronomeTratamento(PronomeTratamento[] pronomeTratamento) {
		this.pronomeTratamento = pronomeTratamento;
	}

	public Esfera[] getEsfera() {
		return esfera;
	}

	public void setEsfera(Esfera[] esfera) {
		this.esfera = esfera;
	}

	public TipoAtestado[] getAtestados() {
		return atestados;
	}

	public void setAtestados(TipoAtestado[] atestados) {
		this.atestados = atestados;
	}

	public TipoEntrada[] getTipoEntrada() {
		return tipoEntrada;
	}

	public void setTipoEntrada(TipoEntrada[] tipoEntrada) {
		this.tipoEntrada = tipoEntrada;
	}

	public SimNao[] getSimNao() {
		return simNao;
	}

	public void setSimNao(SimNao[] simNao) {
		this.simNao = simNao;
	}

	public FaseCredenciamento[] getFaseCredenciamento() {
		return faseCredenciamento;
	}

	public void setFaseCredenciamento(FaseCredenciamento[] faseCredenciamentos) {
		this.faseCredenciamento = faseCredenciamentos;
	}

	public StatusCredenciamento[] getSituacaoCredenciamento() {
		return situacaoCredenciamento;
	}

	public void setSituacaoCredenciamento(StatusCredenciamento[] situacaoCredenciamento) {
		this.situacaoCredenciamento = situacaoCredenciamento;
	}

	public StatusCredenciamento[] getFiltroSituacaoCredenciamento() {
		return filtroSituacaoCredenciamento;
	}

	public void setFiltroSituacaoCredenciamento(StatusCredenciamento[] filtroSituacaoCredenciamento) {
		this.filtroSituacaoCredenciamento = filtroSituacaoCredenciamento;
	}

	public StatusCredenciamentosVisita[] getSituacaoCredenciamentoVisita() {
		return situacaoCredenciamentoVisita;
	}

	public void setSituacaoCredenciamento(StatusCredenciamentosVisita[] situacaoCredenciamentoVisita) {
		this.situacaoCredenciamentoVisita = situacaoCredenciamentoVisita;
	}

	public StatusCredenciamentosVisita[] getFiltroSituacaoCredenciamentoVisita() {
		return filtroSituacaoCredenciamentoVisita;
	}

	public void setFiltroSituacaoCredenciamento(StatusCredenciamentosVisita[] filtroSituacaoCredenciamentoVisita) {
		this.filtroSituacaoCredenciamentoVisita = filtroSituacaoCredenciamentoVisita;
	}

	public List<TipoVisita> getTipoVisita() {
		return tipoVisita;
	}

	public void setTipoVisita(List<TipoVisita> tipoVisita) {
		this.tipoVisita = tipoVisita;
	}

	public SituacaoVisita[] getSituacaoVisita() {
		return situacaoVisita;
	}

	public void setSituacaoVisita(SituacaoVisita[] situacaoVisita) {
		this.situacaoVisita = situacaoVisita;
	}

	public IdadeRecuperando[] getIdadeRecuperando() {
		return idadeRecuperando;
	}

	public void setIdadeRecuperando(IdadeRecuperando[] idadeRecuperando) {
		this.idadeRecuperando = idadeRecuperando;
	}

	public TempoCondenacao[] getTempoCondenacao() {
		return tempoCondenacao;
	}

	public void setTempoCondenacao(TempoCondenacao[] tempoCondenacao) {
		this.tempoCondenacao = tempoCondenacao;
	}

	public Lgbt[] getLgbt() {
		return lgbt;
	}

	public void setLgbt(Lgbt[] lgbt) {
		this.lgbt = lgbt;
	}

	public TipoRelatorioEntradaSaidaRec[] getTipoRelatorioEntradaSaidaRec() {
		return TipoRelatorioEntradaSaidaRec;
	}

	public void setTipoRelatorioEntradaSaidaRec(TipoRelatorioEntradaSaidaRec[] tipoRelatorioEntradaSaidaRec) {
		TipoRelatorioEntradaSaidaRec = tipoRelatorioEntradaSaidaRec;
	}

	public PopulacaoFeminino[] getPopulacaoFeminino() {
		return populacaoFeminino;
	}

	public void setPopulacaoFeminino(PopulacaoFeminino[] populacaoFeminino) {
		this.populacaoFeminino = populacaoFeminino;
	}

	public TipoVisitaObrigatoriedade[] getTipoVisitaObrigatoriedade() {
		return tipoVisitaObrigatoriedade;
	}

	public void setTipoVisitaObrigatoriedade(TipoVisitaObrigatoriedade[] tipoVisitaObrigatoriedade) {
		this.tipoVisitaObrigatoriedade = tipoVisitaObrigatoriedade;
	}

	public UnidadeMedida[] getUnidadesMedida() {
		return unidadesMedida;
	}

	public void setUnidadesMedida(UnidadeMedida[] unidadesMedida) {
		this.unidadesMedida = unidadesMedida;
	}

	public VencimentoCredenciamento[] getVencimentoCredenciamentos() {
		return vencimentoCredenciamentos;
	}

	public void setVencimentoCredenciamentos(VencimentoCredenciamento[] vencimentoCredenciamentos) {
		this.vencimentoCredenciamentos = vencimentoCredenciamentos;
	}

    public Relatorio[] getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(Relatorio[] relatorio) {
        this.relatorio = relatorio;
    }

    public TipoPlantao[] getPlantoes() {
        return plantoes;
    }

    public void setPlantoes(TipoPlantao[] plantoes) {
        this.plantoes = plantoes;
    }
}
