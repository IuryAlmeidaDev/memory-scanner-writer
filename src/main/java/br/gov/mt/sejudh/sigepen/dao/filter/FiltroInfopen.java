package br.gov.mt.sejudh.sigepen.dao.filter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.gov.mt.sejudh.sigepen.enumerator.IdadeRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.Lgbt;
import br.gov.mt.sejudh.sigepen.enumerator.PopulacaoFeminino;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.TempoCondenacao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDeficienciaRelatorio;
import br.gov.mt.sejudh.sigepen.model.Cidade;
import br.gov.mt.sejudh.sigepen.model.Escolaridade;
import br.gov.mt.sejudh.sigepen.model.Estado;
import br.gov.mt.sejudh.sigepen.model.EstadoCivil;
import br.gov.mt.sejudh.sigepen.model.Etnia;
import br.gov.mt.sejudh.sigepen.model.Nacionalidade;
import br.gov.mt.sejudh.sigepen.model.Raca;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.Religiao;

public class FiltroInfopen extends FiltroConsulta {

	private static final long serialVersionUID = -5645657516059523491L;

	private int tipoRelatorio;
	
	private Nacionalidade nacionalidade;
	
	private Regime regime;
	
	private EstadoCivil estadoCivil;
	
	private Religiao religiao;
	
	private Escolaridade escolaridade;
	
	private Raca raca;
	
	private IdadeRecuperando idadeRecuperando;
	
	private TempoCondenacao tempoCondenacao;
	
	private long unidade;
	
	private Lgbt lgbt;
	
	private Etnia etnia;
	
	private TipoDeficienciaRelatorio tipoDeficiencia;
	
	private SimNao recebeAuxilio;
	
	private PopulacaoFeminino populacaoFeminino;
	
	private SimNao aposentado;
		
	private Date dataInicial;	
	
	private Date dataFinal;	
	
	private List<Long> unidadesIds;
	
	private Estado estado;
	
	private Cidade cidade;

	public int getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(int tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public Nacionalidade getNacionalidade() {
		return nacionalidade;
	}

	public void setNacionalidade(Nacionalidade nacionalidade) {
		this.nacionalidade = nacionalidade;
	}

	@Override
	public void configurarFiltros() {
		
	}

	public Regime getRegime() {
		return regime;
	}

	public void setRegime(Regime regime) {
		this.regime = regime;
	}

	public EstadoCivil getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(EstadoCivil estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public Religiao getReligiao() {
		return religiao;
	}

	public void setReligiao(Religiao religiao) {
		this.religiao = religiao;
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

	public IdadeRecuperando getIdadeRecuperando() {
		return idadeRecuperando;
	}

	public void setIdadeRecuperando(IdadeRecuperando idadeRecuperando) {
		this.idadeRecuperando = idadeRecuperando;
	}

	public TempoCondenacao getTempoCondenacao() {
		return tempoCondenacao;
	}

	public void setTempoCondenacao(TempoCondenacao tempoCondenacao) {
		this.tempoCondenacao = tempoCondenacao;
	}
	
	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public String getDescricao() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String filtros = "";
		if(this.getNacionalidade() != null)
			filtros += "Nacionalidade: " + this.getNacionalidade().getDescricao() + ", ";
		if(this.getRegime() != null)
			filtros += "Regime: " + this.getRegime().getDescricao() + ", ";
		if(this.getEstadoCivil() != null)
			filtros += "Estado Civil: " + this.getEstadoCivil().getDescricao() + ", ";
		if(this.getReligiao() != null)
			filtros += "Religião: " + this.getReligiao().getDescricao() + ", ";
		if(this.getEscolaridade() != null)
			filtros += "Escolaridade: " + this.getEscolaridade().getDescricao() + ", ";
		if(this.getRaca() != null)
			filtros += "Raça: " + this.getRaca().getDescricao() + ", ";
		if(this.getIdadeRecuperando() != null && !IdadeRecuperando.T.equals(this.getIdadeRecuperando()))
			filtros += "Idade: " + this.getIdadeRecuperando().getDescricao() + ", ";
		if(this.getTempoCondenacao() != null && !TempoCondenacao.T.equals(this.getTempoCondenacao()))
			filtros += "Tempo condenação: " +this.getTempoCondenacao().getDescricao() + ", ";
		if(this.getLgbt() != null && !Lgbt.T.equals(this.getLgbt()))
			filtros += "Identidade de Gênero: " + this.getLgbt().getDescricao() + ", ";
		if(this.getEtnia() != null) 
			filtros += "Etnia: " + this.getEtnia().getDescricao() + ", ";
		if(this.getTipoDeficiencia() != null && !TipoDeficienciaRelatorio.TODOS.equals(this.getTipoDeficiencia()))
			filtros += "Deficiência: " + this.getTipoDeficiencia().getDescricao() + ", ";
		if(this.getRecebeAuxilio() != null) {
			String dscr = SimNao.N.equals(this.getRecebeAuxilio()) ? "Não, " : "Sim, ";
			filtros += "Recebe Auxílio: " + dscr;
		}
		if(this.getPopulacaoFeminino() != null && !PopulacaoFeminino.T.equals(this.getPopulacaoFeminino()))
			filtros += "Mulheres: " + this.getPopulacaoFeminino().getDescricao() + ", ";
		if(this.getAposentado() != null) {
			String dscr = SimNao.N.equals(this.getAposentado()) ? "Não, " : "Sim, ";
			filtros += "Aposentado: " + dscr;
		}
		
		if(this.getDataInicial() != null) {
			filtros += "Período: " + sdf.format(this.getDataInicial()) +  ", ";			
		}
		
		if(this.getDataFinal() != null) {
			filtros += "Período: " + sdf.format(this.getDataFinal()) +  ", ";			
		}
		if(this.getEstado() != null) {
		    filtros += "Estado: " + this.getEstado().getNome() + ", ";
		}
		if(this.getCidade() != null) {
		    filtros += "Município: " + this.getCidade().getNome() + ", ";
		}
	
		if(!"".equals(filtros))
			return "Filtro utilizado: " + filtros;
		return null;
	}


	public long getUnidade() {
		return unidade;
	}

	public void setUnidade(long unidade) {
		this.unidade = unidade;
	}
	
	public Lgbt getLgbt() {
		return lgbt;
	}

	public void setLgbt(Lgbt lgbt) {
		this.lgbt = lgbt;
	}

	public Etnia getEtnia() {
		return etnia;
	}

	public void setEtnia(Etnia etnia) {
		this.etnia = etnia;
	}

	public TipoDeficienciaRelatorio getTipoDeficiencia() {
		return tipoDeficiencia;
	}

	public void setTipoDeficiencia(TipoDeficienciaRelatorio tipoDeficiencia) {
		this.tipoDeficiencia = tipoDeficiencia;
	}

	public SimNao getRecebeAuxilio() {
		return recebeAuxilio;
	}

	public void setRecebeAuxilio(SimNao recebeAuxilio) {
		this.recebeAuxilio = recebeAuxilio;
	}

	public PopulacaoFeminino getPopulacaoFeminino() {
		return populacaoFeminino;
	}

	public void setPopulacaoFeminino(PopulacaoFeminino populacaoFeminino) {
		this.populacaoFeminino = populacaoFeminino;
	}

	public SimNao getAposentado() {
		return aposentado;
	}

	public void setAposentado(SimNao aposentado) {
		this.aposentado = aposentado;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
		
	public List<Long> getUnidadesIds(){
		return unidadesIds;
	}
	
	public void setUnidadesIds(List<Long> unidadesIds) {
		this.unidadesIds = unidadesIds;
	}

}
