package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.Sexo;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.model.Visitante;

public class VisitanteDTO {
	private Long id;
	private String nome;
	private String cpf;
	private Date dataNascimento;
	private String nomeMae;
	private String nomeSocial;
	private String nomePai;
	private Imagem imagemAtual;
	private Date dataValidade;
	private String grauParentesco;
	private String nacionalidade;
	private String pais;
	private String estado;
	private String cidade;
	private Sexo sexo;
	private String identidadeDeGenero;
	private SimNao pne;
	private SimNao pnePermanente;
	private SimNao emancipado;
	private VisitanteDTO responsavel;
	private List<VisitanteDTO> visitantesReponsaveis;
	private List<Visitante> visitanteResponsavelRelatorio;
	private StatusVisitante status;
	private FaseCredenciamento faseAtual;
	private StatusCredenciamento situacaoAtual;
	
	private boolean isImpedido = false;
	
	private List<CredenciamentoDTO> credenciamentos;
	
	public VisitanteDTO() {
		super();
		this.setCredenciamentos(new ArrayList<>());
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	

	public List<VisitanteDTO> getVisitantesReponsaveis() {
		return visitantesReponsaveis;
	}

	public void setVisitantesReponsaveis(List<VisitanteDTO> visitantesReponsaveis) {
		this.visitantesReponsaveis = visitantesReponsaveis;
	}
	

	public List<Visitante> getVisitanteResponsavelRelatorio() {
		return visitanteResponsavelRelatorio;
	}

	public void setVisitanteResponsavelRelatorio(List<Visitante> visitanteResponsavelRelatorio) {
		this.visitanteResponsavelRelatorio = visitanteResponsavelRelatorio;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public Sexo getSexo() {
		return sexo;
	}

	public void setSexo(Sexo sexo) {
		this.sexo = sexo;
	}

	public String getIdentidadeDeGenero() {
		return identidadeDeGenero;
	}

	public void setIdentidadeDeGenero(String identidadeDeGenero) {
		this.identidadeDeGenero = identidadeDeGenero;
	}

	public SimNao getPne() {
		return pne;
	}

	public void setPne(SimNao pne) {
		this.pne = pne;
	}

	public SimNao getPneDescricao() {
		return pnePermanente;
	}

	public void setPneDescricao(SimNao pnePermanente) {
		this.pnePermanente = pnePermanente;
	}

	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getNacionalidade() {
		return nacionalidade;
	}

	public void setNacionalidade(String nacionalidade) {
		this.nacionalidade = nacionalidade;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	
	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

	public String getNomePai() {
		return nomePai;
	}

	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	public Imagem getImagemAtual() {
		return imagemAtual;
	}

	public void setImagemAtual(Imagem imagemAtual) {
		this.imagemAtual = imagemAtual;
	}

	public Date getDataValidade() {
		return dataValidade;
	}

	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}

	@Override
	public String toString() {
		return "Visitante [id=" + id + ", nome=" + nome + "]";
	}

	public String getGrauParentesco() {
		return grauParentesco;
	}

	public void setGrauParentesco(String grauParentesco) {
		this.grauParentesco = grauParentesco;
	}

	public SimNao getPnePermanente() {
		return pnePermanente;
	}

	public void setPnePermanente(SimNao pnePermanente) {
		this.pnePermanente = pnePermanente;
	}

	public VisitanteDTO getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(VisitanteDTO responsavel) {
		this.responsavel = responsavel;
	}

	public SimNao getEmancipado() {
		return emancipado;
	}

	public void setEmancipado(SimNao emancipado) {
		this.emancipado = emancipado;
	}

	public List<CredenciamentoDTO> getCredenciamentos() {
		return credenciamentos;
	}

	public void setCredenciamentos(List<CredenciamentoDTO> credenciamentos) {
		this.credenciamentos = credenciamentos;
	}

	public StatusVisitante getStatus() {
		return status;
	}

	public void setStatus(StatusVisitante status) {
		this.status = status;
	}

	public FaseCredenciamento getFaseAtual() {
		return faseAtual;
	}

	public void setFaseAtual(FaseCredenciamento faseAtual) {
		this.faseAtual = faseAtual;
	}

	public StatusCredenciamento getSituacaoAtual() {
		return situacaoAtual;
	}

	public void setSituacaoAtual(StatusCredenciamento situacaoAtual) {
		this.situacaoAtual = situacaoAtual;
	}

	public boolean isImpedido() {
		return isImpedido;
	}

	public void setImpedido(boolean isImpedido) {
		this.isImpedido = isImpedido;
	}
}
