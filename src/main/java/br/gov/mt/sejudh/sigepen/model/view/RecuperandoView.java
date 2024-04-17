package br.gov.mt.sejudh.sigepen.model.view;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Immutable;

import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.model.EstruturaComplemento;
import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.model.OutroNome;
import br.gov.mt.sejudh.sigepen.model.SituacaoCadastroRecuperando;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Immutable
@Table(name = "LISTAGEM_RECUPERANDOS", schema = Sigepen.SEJUDH_SCHEMA)
public class RecuperandoView {

	@Id
	@Column(name = "ID_PESSOA", insertable = false, updatable = false, nullable = false)
	Long pessoaId;
    
    @Column(name = "MATRICULA", insertable = false, updatable = false, nullable = false)
	Long id;
	
	@Column(name = "ID_VISITANTE")
	Long visitanteId;
	
	@Column(name = "ID_UNIDADE")
	Long unidadeId;
	
	@Column(name = "ID_ESTRUTURA", insertable = false, updatable = false, nullable = false)
	Long estruturaId;

	@Column(name = "NOME")
	String nome;
	
	@Column(name = "NOME_SOCIAL")
	String nomeSocial;
	
	@Column(name = "DATA_NASCIMENTO")
	Date dataNascimento;

	@Column(name = "NOME_MAE")
	String nomeMae;

	@Column(name = "REGIME_ATUAL")
	String regime;
	
	@Column(name = "REGIME_ATUAL_ID")
	Long regimeId;

    @Column(name = "CONDICAO_ATUAL")
	String condicaoAtual;

	@Column(name = "STATUS", columnDefinition = "number")
	String status;

    @Column(name = "PERICULOSIDADE")
    String periculosidade;

	@Column(name = "SIGLA_UNIDADE")
	String siglaUnidade;
	
	@Column(name = "NOME_UNIDADE")
	String nomeUnidade;
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "recuperando")
	SituacaoCadastroRecuperando situacaoCadastro;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "ID_ESTRUTURA")
	EstruturaComplemento estrutura;

	@Column(name = "TA_VIVO")
	Boolean taVivo;
	
	@Transient
	byte[] fotoFrente;
	
	@Transient
	String lotacaoAtual;
	
	@Transient
	private	Imagem imagemFrente;
	
	@Transient
	private Imagem visitanteImagemFrente;
	
	@Transient
	private Boolean possuiAtestadoUnidade;
	
	@Transient
	private List<String> alcunhas;
	
	@Transient
	private List<OutroNome> outrosNomes;

	public RecuperandoView() {

	}
	
	

	public SituacaoCadastroRecuperando getSituacaoCadastro() {
		return situacaoCadastro;
	}



	public void setSituacaoCadastro(SituacaoCadastroRecuperando situacaoCadastro) {
		this.situacaoCadastro = situacaoCadastro;
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

	public Long getVisitanteId() {
		return visitanteId;
	}

	public void setVisitanteId(Long visitanteId) {
		this.visitanteId = visitanteId;
	}
	
	public Long getUnidadeId() {
		return unidadeId;
	}

	public void setUnidadeId(Long unidadeId) {
		this.unidadeId = unidadeId;
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

	public String getRegime() {
		if (StringUtils.isEmpty(this.regime))
			return StringUtils.EMPTY;
		
		return regime;
	}

	public void setRegime(String regime) {
		this.regime = regime;
	}

	public String getCondicaoAtual() {
		if (StringUtils.isEmpty(this.condicaoAtual))
			return StringUtils.EMPTY;
		
		return Condicao.getValueByString(this.condicaoAtual).getDescricao();
	}

	public void setCondicaoAtual(String condicao) {
		this.condicaoAtual = condicao;
	}

	public String getStatus() {
		if (status == null) 
			return StringUtils.EMPTY;
		
		return StatusRecuperando.getValue(Integer.valueOf(status)).getDescricao();
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPericulosidade() {
		return periculosidade;
	}

	public void setPericulosidade(String periculosidade) {
		this.periculosidade = periculosidade;
	}

//	public SituacaoCadastroRecuperando getSituacaoCadastro() {
//		return situacaoCadastro;
//	}
//
//	public void setSituacaoCadastro(SituacaoCadastroRecuperando situacaoCadastro) {
//		this.situacaoCadastro = situacaoCadastro;
//	}

	public Boolean getTaVivo() {
		return taVivo;
	}

	public void setTaVivo(Boolean taVivo) {
		this.taVivo = taVivo;
	}

	public String getSiglaUnidade() {
		if (StringUtils.isEmpty(this.siglaUnidade))
			return StringUtils.EMPTY;
		
		return siglaUnidade;
	}

	public void setSiglaUnidade(String siglaUnidade) {
		this.siglaUnidade = siglaUnidade;
	}

	public Long getRegimeId() {
		return regimeId;
	}

	public void setRegimeId(Long regimeId) {
		this.regimeId = regimeId;
	}

	public Boolean getIsRecuperando() {
		return (getId() != null);
	}
	
	public Boolean getIsVisitante() {
		return (getVisitanteId() != null);
	}
	
	public Boolean getIsRecuperandoEVisitante() {
		return getIsRecuperando() && getIsVisitante();
	}

	public Boolean getIsRecuperandoInativo() {
		if (status == null) return true;
		
		return (!getIsRecuperando() || StatusRecuperando.getValue(Integer.parseInt(this.status)) == StatusRecuperando.INATIVO);
	}
	
	public Boolean getIsRecuperandoAtivo() {
		if (status == null) return false;
		
		return (StatusRecuperando.getValue(Integer.parseInt(this.status)) == StatusRecuperando.ATIVO);
	}

	public Boolean getIsRecuperandoParcial() {
		if (status == null) return false;
		
		return (StatusRecuperando.getValue(Integer.parseInt(this.status)) == StatusRecuperando.INATIVO_PARCIAL);
	}
	
	public Boolean getIsMesmaUnidadeQueServidorLogado() {
		if (unidadeId == null) return false;
		return (SecurityUtil.getUnidadeSelecionada().getId() == unidadeId);
	}

	public String getNomeUnidade() {
		return nomeUnidade;
	}

	public void setNomeUnidade(String nomeUnidade) {
		this.nomeUnidade = nomeUnidade;
	}

	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public byte[] getFotoFrente() {
		return fotoFrente;
	}

	public void setFotoFrente(byte[] fotoFrente) {
		this.fotoFrente = fotoFrente;
	}

	public Imagem getImagemFrente() {
		return imagemFrente;
	}

	public void setImagemFrente(Imagem imagemFrente) {
		this.imagemFrente = imagemFrente;
	}

	public Imagem getVisitanteImagemFrente() {
		return visitanteImagemFrente;
	}

	public void setVisitanteImagemFrente(Imagem visitanteImagemFrente) {
		this.visitanteImagemFrente = visitanteImagemFrente;
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

	public EstruturaComplemento getEstrutura() {
		return estrutura;
	}

	public void setEstrutura(EstruturaComplemento estrutura) {
		this.estrutura = estrutura;
	}

	public Long getEstruturaId() {
		return estruturaId;
	}

	public void setEstruturaId(Long estruturaId) {
		this.estruturaId = estruturaId;
	}

	public String getLotacaoAtual() {
		return lotacaoAtual;
	}

	public void setLotacaoAtual(String lotacaoAtual) {
		this.lotacaoAtual = lotacaoAtual;
	}

	public Boolean getPossuiAtestadoUnidade() {
		return possuiAtestadoUnidade;
	}

	public void setPossuiAtestadoUnidade(Boolean possuiAtestadoUnidade) {
		this.possuiAtestadoUnidade = possuiAtestadoUnidade;
	}
	
    @Override
    public String toString() {
        return "Recuperando [id=" + id + ", nome=" + getNome() + "]";
    }
}
