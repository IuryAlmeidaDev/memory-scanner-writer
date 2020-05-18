package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.Periculosidade;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDesligamento;
import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.model.OutroNome;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

public class RecuperandoDTO {

	private Long id;
	private Long pessoaId;
	private String nome;
	private String nomeMae;
	private String nomeSocial;
	private Date dataNascimento;
	private StatusRecuperando status;
	private Periculosidade periculosidade;
	private String regime;
	private Condicao condicaoAtual;
	private String unidade;
	private String siglaUnidade;
	private String lotacaoAtualNomeEstrutura;
	private Long unidadeId;
	private Boolean situacaoCadastro;
	
	private Boolean temTatuagem;
	private Boolean temFotoTatuagem;
	private Boolean temFotoFrente;
	private Boolean temFotoPerfil;
	private Boolean temFotoCorpoInteiro;
	private Boolean vidaSocialCompleta;
	private Boolean temParente;
	
	private Boolean taVivo;
	
	private List<String> alcunhas;
	private List<OutroNome> outrosNomes;
	
	private Imagem imagemFrente;
	
	public RecuperandoDTO() {
		this.alcunhas = new ArrayList<String>();
		this.outrosNomes = new ArrayList<OutroNome>();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPessoaId() {
		return pessoaId;
	}

	public void setPessoaId(Long pessoaId) {
		this.pessoaId = pessoaId;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	
	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getStatus() {
		return (status != null) ? status.getDescricao() : null;
	}

	public void setStatus(StatusRecuperando status) {
		this.status = (status == null) ? StatusRecuperando.INATIVO : status;
	}

	public String getRegime() {
		return regime;
	}

	public void setRegime(String regime) {
		this.regime = regime;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	
	public Long getUnidadeId() {
		return unidadeId;
	}

	public void setUnidadeId(Long unidadeId) {
		this.unidadeId = unidadeId;
	}

	public Periculosidade getPericulosidade() {
		return periculosidade;
	}

	public void setPericulosidade(Periculosidade periculosidade) {
		this.periculosidade = periculosidade;
	}

	public Boolean getIsRecuperando() {
		return (getId() != null);
	}

	public Boolean getIsRecuperandoInativo() {
		return (!getIsRecuperando() || this.status == StatusRecuperando.INATIVO);
	}
	
	public Boolean getIsRecuperandoAtivo() {
		return (this.status == StatusRecuperando.ATIVO);
	}

	public Boolean getIsRecuperandoParcial() {
		return (this.status == StatusRecuperando.INATIVO_PARCIAL);
	}
	
	public Boolean getIsMesmaUnidadeQueServidorLogado() {
		if (unidadeId == null) return false;
		return (SecurityUtil.getUnidadeSelecionada().getId() == unidadeId);
	}

	public Condicao getCondicaoAtual() {
		return condicaoAtual;
	}

	public void setCondicaoAtual(Condicao condicaoAtual) {
		this.condicaoAtual = condicaoAtual;
	}

	public Boolean getSituacaoCadastro() {
		return situacaoCadastro;
	}

	public void setSituacaoCadastro(Boolean situacaoCadastro) {
		this.situacaoCadastro = situacaoCadastro;
	}

	public Boolean getTemTatuagem() {
		return temTatuagem;
	}

	public void setTemTatuagem(Boolean temTatuagem) {
		this.temTatuagem = temTatuagem;
	}

	public Boolean getTemFotoTatuagem() {
		return temFotoTatuagem;
	}

	public void setTemFotoTatuagem(Boolean temFotoTatuagem) {
		this.temFotoTatuagem = temFotoTatuagem;
	}

	public Boolean getTemFotoFrente() {
		return temFotoFrente;
	}

	public void setTemFotoFrente(Boolean temFotoFrente) {
		this.temFotoFrente = temFotoFrente;
	}

	public Boolean getTemFotoPerfil() {
		return temFotoPerfil;
	}

	public void setTemFotoPerfil(Boolean temFotoPerfil) {
		this.temFotoPerfil = temFotoPerfil;
	}

	public Boolean getTemFotoCorpoInteiro() {
		return temFotoCorpoInteiro;
	}

	public void setTemFotoCorpoInteiro(Boolean temFotoCorpoInteiro) {
		this.temFotoCorpoInteiro = temFotoCorpoInteiro;
	}

	public Boolean getVidaSocialCompleta() {
		return vidaSocialCompleta;
	}

	public void setVidaSocialCompleta(Boolean vidaSocialCompleta) {
		this.vidaSocialCompleta = vidaSocialCompleta;
	}

	public Boolean getTemParente() {
		return temParente;
	}

	public void setTemParente(Boolean temParente) {
		this.temParente = temParente;
	}

	public Boolean getTaVivo() {
		return taVivo;
	}

	public void setTaVivo(TipoDesligamento tipoDesligamento) {
		this.taVivo = (tipoDesligamento != TipoDesligamento.OBITO);
	}

	public List<String> getAlcunhas() {
		return alcunhas;
	}

	public void setAlcunhas(List<String> alcunhas) {
		this.alcunhas = alcunhas;
	}

	public List<OutroNome> getOutrosNomes() {
		return outrosNomes;
	}

	public void setOutrosNomes(List<OutroNome> outrosNomes) {
		this.outrosNomes = outrosNomes;
	}

	public String getSiglaUnidade() {
		return siglaUnidade;
	}

	public void setSiglaUnidade(String siglaUnidade) {
		this.siglaUnidade = siglaUnidade;
	}

	public String getLotacaoAtualNomeEstrutura() {
		return lotacaoAtualNomeEstrutura;
	}

	public void setLotacaoAtualNomeEstrutura(String lotacaoAtualNomeEstrutura) {
		this.lotacaoAtualNomeEstrutura = lotacaoAtualNomeEstrutura;
	}

	public Imagem getImagemFrente() {
		return imagemFrente;
	}

	public void setImagemFrente(Imagem imagemFrente) {
		this.imagemFrente = imagemFrente;
	}

	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

	@Override
	public String toString() {
		return "Recuperando [id=" + id + ", nome=" + nome + "]";
	}
}
