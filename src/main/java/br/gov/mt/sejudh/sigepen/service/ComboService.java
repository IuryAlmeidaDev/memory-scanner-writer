package br.gov.mt.sejudh.sigepen.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.ComboDAO;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.CorCabelo;
import br.gov.mt.sejudh.sigepen.enumerator.CorOlho;
import br.gov.mt.sejudh.sigepen.enumerator.Esfera;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.IdadeRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.Lgbt;
import br.gov.mt.sejudh.sigepen.enumerator.Modalidade;
import br.gov.mt.sejudh.sigepen.enumerator.NaturezaFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.enumerator.Periculosidade;
import br.gov.mt.sejudh.sigepen.enumerator.PopulacaoFeminino;
import br.gov.mt.sejudh.sigepen.enumerator.PronomeTratamento;
import br.gov.mt.sejudh.sigepen.enumerator.Sexo;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoVisita;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamentosVisita;
import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TempoCondenacao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoAtestado;
import br.gov.mt.sejudh.sigepen.enumerator.TipoContato;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDeficiencia;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDesligamento;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDocumentoPedidoTransito;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.enumerator.TipoFoto;
import br.gov.mt.sejudh.sigepen.enumerator.TipoInterdicao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoParticularidade;
import br.gov.mt.sejudh.sigepen.enumerator.TipoPlantao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoProcesso;
import br.gov.mt.sejudh.sigepen.enumerator.TipoRelatorioEntradaSaidaRec;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.enumerator.TipoVisitaObrigatoriedade;
import br.gov.mt.sejudh.sigepen.enumerator.UnidadeMedida;
import br.gov.mt.sejudh.sigepen.enumerator.VencimentoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.Certidao;
import br.gov.mt.sejudh.sigepen.model.Cidade;
import br.gov.mt.sejudh.sigepen.model.Escolaridade;
import br.gov.mt.sejudh.sigepen.model.Estado;
import br.gov.mt.sejudh.sigepen.model.EstadoCivil;
import br.gov.mt.sejudh.sigepen.model.Funcionalidade;
import br.gov.mt.sejudh.sigepen.model.IdentidadeDeGenero;
import br.gov.mt.sejudh.sigepen.model.Nacionalidade;
import br.gov.mt.sejudh.sigepen.model.OrientacaoSexual;
import br.gov.mt.sejudh.sigepen.model.Pais;
import br.gov.mt.sejudh.sigepen.model.Parentesco;
import br.gov.mt.sejudh.sigepen.model.Plantao;
import br.gov.mt.sejudh.sigepen.model.Raca;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.RegistroPlantao;
import br.gov.mt.sejudh.sigepen.model.Religiao;
import br.gov.mt.sejudh.sigepen.model.TipoDocumento;
import br.gov.mt.sejudh.sigepen.model.TipoHistorico;
import br.gov.mt.sejudh.sigepen.model.TipoImpedimento;
import br.gov.mt.sejudh.sigepen.model.TipoSanguineo;
import br.gov.mt.sejudh.sigepen.model.TipoServidor;
import br.gov.mt.sejudh.sigepen.model.TipoUnidade;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;


@Stateless
public class ComboService {

	@Inject
	private ComboDAO dao;

	public ComboService() {

	}

	public List<Regime> listarRegime() {
		return dao.listarRegime();
	}

	public List<Pais> listarPais() {
		return dao.listarPais();
	}

	public List<Religiao> listarReligiao() {
		return dao.listarReligiao();
	}

	public List<Raca> listarRaca() {
		return dao.listarRaca();
	}

	public List<EstadoCivil> listarEstadoCivil() {
		return dao.listarEstadoCivil();
	}

	public List<OrientacaoSexual> listarOrientacaoSexual() {
		return dao.listarOrientacaoSexual();
	}

	public List<IdentidadeDeGenero> listarIdentidadeDeGenero() {
		return dao.listarIdentidadeDeGenero();
	}

	public List<TipoSanguineo> listarTipoSanguineo() {
		return dao.listarTipoSanguineo();
	}

	public List<Escolaridade> listarEscolaridade() {
		return dao.listarEscolaridade();
	}

	public List<Parentesco> listarParentesco() {
		return dao.listarParentesco();
	}

	public List<TipoServidor> listarTipoServidor(){
		return dao.listarTipoServidor();
	}

	public List<TipoHistorico> listarTipoHistoricoPermitidoParaCadastrar() {
		return dao.listarTipoHistoricoPorPermissao("CadastrarHistoricoRecuperando");
	}

	public List<TipoHistorico> listarTipoHistoricoParaVisualizar() {
		return dao.listarTipoHistoricoPorPermissao("VisualizarHistoricoRecuperando");
	}


	public Status[] getStatus() {
		return Status.values();
	}

	public Sexo[] getSexo() {
		return Sexo.values();
	}

	public CorOlho[] getCorOlho() {
		return CorOlho.values();
	}

	public CorCabelo[] getCorCabelo() {
		return CorCabelo.values();
	}

	public Periculosidade[] getPericulosidade() {
		return Periculosidade.values();
	}

	public SituacaoUnidade[] getSituacaoUnidade() {
		SituacaoUnidade[] situacoes = SituacaoUnidade.values();

		//Remove o item "Interditado Parcial" para não aparecer no combo de seleção
		for(int i = 0; i < situacoes.length; i++){
			if (situacoes[i] == SituacaoUnidade.INTERDITADO_PARCIAL){
				SituacaoUnidade[] copy = new SituacaoUnidade[situacoes.length-1];
	            System.arraycopy(situacoes, 0, copy, 0, i);
	            System.arraycopy(situacoes, i+1, copy, i, situacoes.length-i-1);
	            return copy;
	        }
		}
		return situacoes;
	}

	public SituacaoUnidade[] getSituacaoUnidadeTodas() {
		return SituacaoUnidade.values();
	}

	public TipoContato[] getTipoContato() {
		return TipoContato.values();
	}

	public TipoInterdicao[] getTipoInterdicao(){
		return TipoInterdicao.values();
	}

	public TipoDeficiencia[] getTipoDeficiencia(){
		return TipoDeficiencia.values();
	}

	public List<Estado> listarEstado(){
		return dao.listarEstado();
	}

	public List<UnidadePrisional> listarUnidades(){
		return dao.listarUnidadesPrisionais();
	}

	public List<Plantao> listarPlantoesCadastrados(){
		return dao.listarPlantoesCadastrados();
	}

	public List<Cidade> listarCidade(Estado estado){
		return dao.listarCidade(estado);
	}

	public List<TipoUnidade> listarTipoUnidade(){
		return dao.listarTipoUnidade();
	}

	public List<TipoDocumento> listarTipoDocumento(){
		return dao.listarTipoDocumento();
	}

	public List<Nacionalidade> listarNacionalidades(){
		return dao.listarNacionalidades();
	}

    public List<Funcionalidade> listarFuncionalidades() {
        return dao.listarFuncionalidades();
    }

	public StatusRecuperando[] getStatusRecuperando() {
		return StatusRecuperando.values();
	}

	public TipoFoto[] getTipoFoto() {
		return TipoFoto.values();
	}

	public TipoProcesso[] getTipoProcesso() {
		return TipoProcesso.values();
	}

	public List<TipoTransito> getTipoTransito() {
		TipoTransito[] tipos = TipoTransito.values();
		List<TipoTransito> tiposTransito = new ArrayList<>();
		for(int i = 2; i < tipos.length; i++){
			tiposTransito.add(tipos[i]);
		}
		return tiposTransito;
	}

	public Condicao[] getCondicao() {
		return Condicao.values();
	}

	public NaturezaFaltaDisciplinar[] getNaturezaFaltaDisciplinar(){
		return NaturezaFaltaDisciplinar.values();
	}

	public SituacaoFaltaDisciplinar[] getSituacaoFaltaDisciplinar(){
		return SituacaoFaltaDisciplinar.values();
	}

	public Modalidade[] getModalidade() {
		return Modalidade.values();
	}

	public TipoParticularidade[] getTipoParticularidade() {
		return TipoParticularidade.values();
	}

	public StatusPedido[] getStatusPedido() {
		return StatusPedido.values();
	}

	public StatusMovimentacao[] getStatusMovimentacao() {
		return StatusMovimentacao.values();
	}

	public TipoDesligamento[] getTipoDesligamento() {
		return TipoDesligamento.values();
	}

	public Acao[] getAcao() {
		return Acao.values();
	}

	public PronomeTratamento[] getPronomeTratamento() {
		return PronomeTratamento.values();
	}

	public TipoDocumentoPedidoTransito[] getTipoDocumentoPedidoTransito() {
		return TipoDocumentoPedidoTransito.values();
	}

	public List<UnidadePrisional> getUnidadesPrisionais() {
		return dao.listarUnidadesPrisionais();
	}

    public List<TipoVisita> getTipoVisitas() {
        return dao.listarTipoVisitas();
    }

    public List<Certidao> getCertidoes() {
        return dao.listarCertidoes();
    }

    public List<TipoImpedimento> getTipoImpedimentos() {
        return dao.listarTipoImpedimentos();
    }

	public Esfera[] getEsfera() {
		return Esfera.values();
	}

	public TipoAtestado[] getAtestados() {
		return TipoAtestado.values();
	}

	public TipoEntrada[] getTipoEntrada(){
		return TipoEntrada.values();
	}

	public SimNao[] getSimNao(){
		return SimNao.values();
	}

	public FaseCredenciamento[] getFaseCredenciamento(){
		return FaseCredenciamento.values();
	}

	public StatusCredenciamento[] getSituacaoCredenciamento(){
		return StatusCredenciamento.values();
	}

	public StatusCredenciamentosVisita[] getSituacaoCredenciamentoVisita(){
		return StatusCredenciamentosVisita.values();
	}

	public StatusCredenciamentosVisita[] getFiltroSituacaoCredenciamentoVisita(){
        return new StatusCredenciamentosVisita[]{
                StatusCredenciamentosVisita.DISPONIVEL,
                StatusCredenciamentosVisita.INDISPONIVEL,
                StatusCredenciamentosVisita.FALTA_DISCIPLINAR
                };
	}

	public StatusCredenciamento[] getFiltroSituacaoCredenciamento(){
        return new StatusCredenciamento[]{
                StatusCredenciamento.PENDENTE,
                StatusCredenciamento.APROVADO,
                StatusCredenciamento.NAO_APROVADO,
                StatusCredenciamento.INATIVO
                };
	}

	public SituacaoVisita[] getSituacaoVisita() {
		return SituacaoVisita.values();

	}

	public IdadeRecuperando[] getIdadeRecuperando() {
		return IdadeRecuperando.values();
	}

	public TempoCondenacao[] getTempoCondenacao() {
		return TempoCondenacao.values();
	}

	public Lgbt[] getLgbt() {
		return Lgbt.values();
	}

	public TipoRelatorioEntradaSaidaRec[] getTipoRelatorioEntradaSaidaRec(){
		return TipoRelatorioEntradaSaidaRec.values();
	}

	public PopulacaoFeminino[] getPopulacaoFeminino() {
		return PopulacaoFeminino.values();
	}

	public TipoVisitaObrigatoriedade[] getTipoVisitaObrigatoriedade() {
		return TipoVisitaObrigatoriedade.values();
	}

	public UnidadeMedida[] getUnidadesMedida() {
		return UnidadeMedida.values();
	}

	public VencimentoCredenciamento[] getVencimentoCredenciamento() {
		return VencimentoCredenciamento.values();
	}


	public TipoPlantao[] getTipoPlantao() {
		return TipoPlantao.values();
	}

	public Relatorio[] getRelatorio() {
	    return new Relatorio[] { Relatorio.Atestado };
	}

}
