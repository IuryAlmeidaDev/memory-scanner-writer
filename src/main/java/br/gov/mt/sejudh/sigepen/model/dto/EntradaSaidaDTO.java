package br.gov.mt.sejudh.sigepen.model.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import br.gov.mt.sejudh.sigepen.conversores.EnumCustomConverter;
import br.gov.mt.sejudh.sigepen.enumerator.Sexo;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoVisita;
import br.gov.mt.sejudh.sigepen.enumerator.TipoAutorizacaoValidacaoSemBio;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Immutable
@Entity
@IdClass(EntradaSaidaDTOId.class)
public class EntradaSaidaDTO {

	@Id
	private Long id;
	@Id
	private Long idCredenciamento;

	private String tipoVisita;

	private String parentesco;
	private String profissao;
	private String nomeRecuperando;
	private Date dataEntrada;
	private Date dataSaida;
	private Date dataCancelamento;

	private String nomeVisitante;
	private Long visitanteId;
	@Enumerated(EnumType.STRING)
	private SimNao pneVisitante;
	@Enumerated(EnumType.STRING)
	private SimNao pnePermanenteVisitante;
	@Enumerated(EnumType.STRING)
	private SimNao emancipadoVisitante;
	private String nomeMaeVisitante;
	private String nomePaiVisitante;
	@Enumerated(EnumType.STRING)
	private Sexo sexoVisitante;
	private String nomeSocialVisitante;
	private Date dataNascimentoVisitante;
	private String nacionalidadeVisitante;
	private String estadoVisitante;
	private String cidadeVisitante;
	private String identidadeGeneroVisitante;

	private String nomeResponsavel;
	private Long idResponsavel;
	@Enumerated(EnumType.STRING)
	private SimNao pneResponsavel;
	@Enumerated(EnumType.STRING)
	private SimNao pnePermanenteResponsavel;
	@Enumerated(EnumType.STRING)
	private SimNao emancipadoResponsavel;
	private String nomeMaeResponsavel;
	private String nomePaiResponsavel;

	private String sexoResponsavel;
	private String nomeSocialResponsavel;
	private Date dataNascimentoResponsavel;
	private String nacionalidadeResponsavel;
	private String estadoResponsavel;
	private String cidadeResponsavel;
	private String identidadeGeneroResponsavel;

	@Convert(converter = EnumCustomConverter.class, disableConversion = true)
	@Type(type = "br.gov.mt.sejudh.sigepen.conversores.EnumCustomType")
	private SituacaoVisita situacao;
	private String motivoFinalizacao;
	private String motivoEntrada;
	private String motivoCancelamento;
	private Long idUnidade;
	private String nomeUnidade;
	private String siglaUnidade;

	@Transient
	private VisitanteDTO visitante;

	@Transient
	private VisitanteDTO responsavel;

	@Transient
	private List<AutorizacaoSemBiometriaDTO> autorizacoes;

	public AutorizacaoSemBiometriaDTO getAutorizacao(TipoAutorizacaoValidacaoSemBio acao) {
		Optional<AutorizacaoSemBiometriaDTO> opt = this.autorizacoes.stream()
				.filter(a -> a.getTipoAutorizacao().equals(acao)).findFirst();

		if (opt.isPresent())
			return opt.get();
		else
			return null;
	}

	public List<AutorizacaoSemBiometriaDTO> getAutorizacoes() {
		return autorizacoes;
	}

	public void setAutorizacoes(List<AutorizacaoSemBiometriaDTO> autorizacoes) {
		this.autorizacoes = autorizacoes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTipoVisita() {
		return tipoVisita;
	}

	public void setTipoVisita(String tipoVisita) {
		this.tipoVisita = tipoVisita;
	}

	public String getParentesco() {
		return parentesco;
	}

	public void setParentesco(String parentesco) {
		this.parentesco = parentesco;
	}

	public String getNomeRecuperando() {
		return nomeRecuperando;
	}

	public void setNomeRecuperando(String nomeRecuperando) {
		this.nomeRecuperando = nomeRecuperando;
	}

	public Date getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public Date getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}

	public SituacaoVisita getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoVisita situacao) {
		this.situacao = situacao;
	}

	public String getProfissao() {
		return profissao;
	}

	public void setProfissao(String profissao) {
		this.profissao = profissao;
	}

	public String getMotivoFinalizacao() {
		return motivoFinalizacao;
	}

	public void setMotivoFinalizacao(String motivoFinalizacao) {
		this.motivoFinalizacao = motivoFinalizacao;
	}

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	public String getMotivoEntrada() {
		return motivoEntrada;
	}

	public void setMotivoEntrada(String motivoEntrada) {
		this.motivoEntrada = motivoEntrada;
	}

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public String getNomeUnidade() {
		return nomeUnidade;
	}

	public void setNomeUnidade(String nomeUnidade) {
		this.nomeUnidade = nomeUnidade;
	}

	public String getSiglaUnidade() {
		return siglaUnidade;
	}

	public void setSiglaUnidade(String siglaUnidade) {
		this.siglaUnidade = siglaUnidade;
	}

	public Long getIdUnidade() {
		return idUnidade;
	}

	public void setIdUnidade(Long idUnidade) {
		this.idUnidade = idUnidade;
	}

	public String getNomeVisitante() {
		return nomeVisitante;
	}

	public void setNomeVisitante(String nomeVisitante) {
		this.nomeVisitante = nomeVisitante;
	}

	public Long getIdVisitante() {
		return visitanteId;
	}

	public void setIdVisitante(Long visitanteId) {
		this.visitanteId = visitanteId;
	}

	public SimNao getPneVisitante() {
		return pneVisitante;
	}

	public void setPneVisitante(SimNao pneVisitante) {
		this.pneVisitante = pneVisitante;
	}

	public SimNao getPnePermanenteVisitante() {
		return pnePermanenteVisitante;
	}

	public void setPnePermanenteVisitante(SimNao pnePermanenteVisitante) {
		this.pnePermanenteVisitante = pnePermanenteVisitante;
	}

	public SimNao getEmancipadoVisitante() {
		return emancipadoVisitante;
	}

	public void setEmancipadoVisitante(SimNao emancipadoVisitante) {
		this.emancipadoVisitante = emancipadoVisitante;
	}

	public String getNomeMaeVisitante() {
		return nomeMaeVisitante;
	}

	public void setNomeMaeVisitante(String nomeMaeVisitante) {
		this.nomeMaeVisitante = nomeMaeVisitante;
	}

	public String getNomePaiVisitante() {
		return nomePaiVisitante;
	}

	public void setNomePaiVisitante(String nomePaiVisitante) {
		this.nomePaiVisitante = nomePaiVisitante;
	}

	public Sexo getSexoVisitante() {
		return sexoVisitante;
	}

	public void setSexoVisitante(Sexo sexoVisitante) {
		this.sexoVisitante = sexoVisitante;
	}

	public String getNomeSocialVisitante() {
		return nomeSocialVisitante;
	}

	public void setNomeSocialVisitante(String nomeSocialVisitante) {
		this.nomeSocialVisitante = nomeSocialVisitante;
	}

	public Date getDataNascimentoVisitante() {
		return dataNascimentoVisitante;
	}

	public void setDataNascimentoVisitante(Date dataNascimentoVisitante) {
		this.dataNascimentoVisitante = dataNascimentoVisitante;
	}

	public String getNacionalidadeVisitante() {
		return nacionalidadeVisitante;
	}

	public void setNacionalidadeVisitante(String nacionalidadeVisitante) {
		this.nacionalidadeVisitante = nacionalidadeVisitante;
	}

	public String getEstadoVisitante() {
		return estadoVisitante;
	}

	public void setEstadoVisitante(String estadoVisitante) {
		this.estadoVisitante = estadoVisitante;
	}

	public String getCidadeVisitante() {
		return cidadeVisitante;
	}

	public void setCidadeVisitante(String cidadeVisitante) {
		this.cidadeVisitante = cidadeVisitante;
	}

	public String getIdentidadeGeneroVisitante() {
		return identidadeGeneroVisitante;
	}

	public void setIdentidadeGeneroVisitante(String identidadeGeneroVisitante) {
		this.identidadeGeneroVisitante = identidadeGeneroVisitante;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public Long getIdResponsavel() {
		return idResponsavel;
	}

	public void setIdResponsavel(Long idResponsavel) {
		this.idResponsavel = idResponsavel;
	}

	public SimNao getPneResponsavel() {
		return pneResponsavel;
	}

	public void setPneResponsavel(SimNao pneResponsavel) {
		this.pneResponsavel = pneResponsavel;
	}

	public SimNao getPnePermanenteResponsavel() {
		return pnePermanenteResponsavel;
	}

	public void setPnePermanenteResponsavel(SimNao pnePermanenteResponsavel) {
		this.pnePermanenteResponsavel = pnePermanenteResponsavel;
	}

	public SimNao getEmancipadoResponsavel() {
		return emancipadoResponsavel;
	}

	public void setEmancipadoResponsavel(SimNao emancipadoResponsavel) {
		this.emancipadoResponsavel = emancipadoResponsavel;
	}

	public String getNomeMaeResponsavel() {
		return nomeMaeResponsavel;
	}

	public void setNomeMaeResponsavel(String nomeMaeResponsavel) {
		this.nomeMaeResponsavel = nomeMaeResponsavel;
	}

	public String getNomePaiResponsavel() {
		return nomePaiResponsavel;
	}

	public void setNomePaiResponsavel(String nomePaiResponsavel) {
		this.nomePaiResponsavel = nomePaiResponsavel;
	}

	public String getSexoResponsavel() {
		return sexoResponsavel;
	}

	public void setSexoResponsavel(String sexoResponsavel) {
		this.sexoResponsavel = sexoResponsavel;
	}

	public String getNomeSocialResponsavel() {
		return nomeSocialResponsavel;
	}

	public void setNomeSocialResponsavel(String nomeSocialResponsavel) {
		this.nomeSocialResponsavel = nomeSocialResponsavel;
	}

	public Date getDataNascimentoResponsavel() {
		return dataNascimentoResponsavel;
	}

	public void setDataNascimentoResponsavel(Date dataNascimentoResponsavel) {
		this.dataNascimentoResponsavel = dataNascimentoResponsavel;
	}

	public String getNacionalidadeResponsavel() {
		return nacionalidadeResponsavel;
	}

	public void setNacionalidadeResponsavel(String nacionalidadeResponsavel) {
		this.nacionalidadeResponsavel = nacionalidadeResponsavel;
	}

	public String getEstadoResponsavel() {
		return estadoResponsavel;
	}

	public void setEstadoResponsavel(String estadoResponsavel) {
		this.estadoResponsavel = estadoResponsavel;
	}

	public String getCidadeResponsavel() {
		return cidadeResponsavel;
	}

	public void setCidadeResponsavel(String cidadeResponsavel) {
		this.cidadeResponsavel = cidadeResponsavel;
	}

	public String getIdentidadeGeneroResponsavel() {
		return identidadeGeneroResponsavel;
	}

	public void setIdentidadeGeneroResponsavel(String identidadeGeneroResponsavel) {
		this.identidadeGeneroResponsavel = identidadeGeneroResponsavel;
	}

	public void criarVisitante() {
		VisitanteDTO visitante = new VisitanteDTO();
		visitante.setId(this.visitanteId);
		visitante.setNome(this.nomeVisitante);
		visitante.setPne(this.pneVisitante);
		visitante.setPnePermanente(this.pnePermanenteVisitante);
		visitante.setEmancipado(this.emancipadoVisitante);
		visitante.setNomeMae(this.nomeMaeVisitante);
		visitante.setNomePai(this.nomePaiVisitante);
		visitante.setSexo(this.sexoVisitante);
		visitante.setNomeSocial(this.nomeSocialVisitante);
		visitante.setDataNascimento(this.dataNascimentoVisitante);
		visitante.setNacionalidade(this.nacionalidadeVisitante);
		visitante.setEstado(this.estadoVisitante);
		visitante.setCidade(this.cidadeVisitante);
		visitante.setIdentidadeDeGenero(this.identidadeGeneroVisitante);
		this.visitante = visitante;
	}

	public void criarResponsavel() {
		if (this.idResponsavel == null) {
			return;
		}
		VisitanteDTO responsavel = new VisitanteDTO();
		responsavel.setId(this.idResponsavel);
		responsavel.setNome(this.nomeResponsavel);
		responsavel.setPne(this.pneResponsavel);
		responsavel.setPnePermanente(this.pnePermanenteResponsavel);
		responsavel.setEmancipado(this.emancipadoResponsavel);
		responsavel.setNomeMae(this.nomeMaeResponsavel);
		responsavel.setNomePai(this.nomePaiResponsavel);
		responsavel.setSexo(Sexo.valueOf(this.sexoResponsavel));
		responsavel.setNomeSocial(this.nomeSocialResponsavel);
		responsavel.setDataNascimento(this.dataNascimentoResponsavel);
		responsavel.setNacionalidade(this.nacionalidadeResponsavel);
		responsavel.setEstado(this.estadoResponsavel);
		responsavel.setCidade(this.cidadeResponsavel);
		responsavel.setIdentidadeDeGenero(this.identidadeGeneroResponsavel);
		this.responsavel = responsavel;

	}

	public VisitanteDTO getVisitante() {
		return visitante;
	}

	public void setVisitante(VisitanteDTO visitante) {
		this.visitante = visitante;
	}

	public VisitanteDTO getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(VisitanteDTO responsavel) {
		this.responsavel = responsavel;
	}

	public Long getIdCredenciamento() {
		return idCredenciamento;
	}

	public void setIdCredenciamento(Long idCredenciamento) {
		this.idCredenciamento = idCredenciamento;
	}
}
