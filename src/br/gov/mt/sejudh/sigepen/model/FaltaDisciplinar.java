package br.gov.mt.sejudh.sigepen.model;

import br.gov.mt.sejudh.sigepen.conversores.EnumCustomConverter;
import br.gov.mt.sejudh.sigepen.enumerator.*;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Audited
@Table(name = "SGP_FALTA_DISCIPLINAR", schema = Sigepen.SEJUDH_SCHEMA)
public class FaltaDisciplinar implements Serializable{


	private static final long serialVersionUID = -5836484679427900453L;

	@SequenceGenerator(name = "SIGEPEN_S_FALTA_DISCIPINA", sequenceName = "SIGEPEN_S_FALTA_DISCIPINA", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_FALTA_DISCIPINA")
	@Column(name = "ID_FALTA_DISCIPLINAR")
	private long id;

	@ManyToOne(optional=false)
	@JoinColumn(name = "ID_RECUPERANDO")
	private Recuperando recuperando;

	@ManyToOne(optional=false)
	@JoinColumn(name = "ID_TIPO_FALTA_DISCIPLINAR")
	private TipoFaltaDisciplinar tipo;

	@ManyToOne(optional=false)
	@JoinColumn(name = "ID_ESTRUTURA")
	private Estrutura estruturaOcorrencia;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_FALTA_DISCIPLINAR", nullable = false)
	private Date data;

	// INI - Dados do Fato Gerador da Falta Disciplinar
	@Column(name = "NOME_TESTEMUNHA", length = 250)
	private String nomeTestemunha;

	@Column(name = "NUMR_DOC_FATO_FALTA_DISCIPLINA", length = 80, nullable = false)
	private String fatoNumeroDocumento;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_FATO_FALTA_DISCIPLINAR", nullable = false)
	private Date fatoData;

	@Column(name = "DESC_FATO_FALTA_DISCIPLINAR", length = 4000, nullable = false)
	private String fatoDescricao;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_SITUACAO_PENDENTE_FALTA", nullable = false)
	private Date dataSituacaoPendente;

	// FIM - Dados do Fato Gerador da Falta Disciplinar

	// INI - Dados do PAD da Falta Disciplinar
	@Column(name = "NUMR_DOC_PAD_FALTA_DISCIPLINAR", length = 80)
	private String padNumeroDocumento;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_PAD_FALTA_DISCIPLINAR")
	private Date padDataDocumento;

	@Column(name = "DESC_PAD_FALTA_DISCIPLINAR", length = 4000)
	private String padDescricao;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_SITUACAO_EM_ANDAMENTO_FALTA")
	private Date dataSituacaoEmAndamento;
	// FIM - DAdos do PAD da Falta Disciplinar

	// INI - Dados do Parecer da Falta Disciplinar
	@Column(name = "NUMR_PARECER_FALTA_DISCIPLINAR", length = 80)
	private String parecerNumero;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_PARECER_FALTA_DISCIPLINAR")
	private Date parecerData;

	@Column(name = "DESC_PARECER_FALTA_DISCIPLINAR", length = 4000)
	private String parecerDescricao;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_SITUACAO_CONCLUIDA_FALTA")
	private Date dataSituacaoConcluida;

	@Column(name="FLAG_RECONHECIDA_FALTA_DISCIPL")
	private SimNao reconhecida;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_INICIO_FALTA_DISCIPLINAR")
	private Date dataInicio;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_FIM_FALTA_DISCIPLINAR")
	private Date dataFim;
	// FIM - Dados do Parecer da Falta Disciplinar

	@Column(name="CODG_SITUACAO_FALTA_DISCIPLINA", nullable = false)
	@Convert(converter = EnumCustomConverter.class, disableConversion=true)
	@Type(type = "br.gov.mt.sejudh.sigepen.conversores.EnumCustomType")
	private SituacaoFaltaDisciplinar situacao;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_SITUACAO_CANCELAMENTO")
	private Date dataSituacaoCancelada;

	@Column(name = "DESC_MOTIVO_CANCELAMENTO_FALTA", length = 250)
	private String motivoCancelamento;

	@Enumerated
	@Column(name = "STAT_FALTA_DISCIPLINAR", nullable = false)
	private Status status;

	@Transient
	private SituacaoFaltaDisciplinar novaSituacao;

	@Transient
	private AutoriaRegistro autorRegistro;

	@Transient
	private AutoriaRegistro autorUltimaAlteracao;

	@Column(name = "FATO_SUSPENDE_VISITA")
	private boolean fatoSuspendeVisita;

	@Column(name = "PAD_SUSPENDE_VISITA")
	private boolean padSuspendeVisita;

	@Column(name = "PARECER_SUSPENDE_VISITA")
	private boolean parecerSuspendeVisita;

	public FaltaDisciplinar() {
	}

	public FaltaDisciplinar(Status status) {
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public TipoFaltaDisciplinar getTipo() {
		return tipo;
	}

	public void setTipo(TipoFaltaDisciplinar tipo) {
		this.tipo = tipo;
	}

	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date dataPrevista) {
		this.data = dataPrevista;
	}

	public Date getDataSituacaoEmAndamento() {
		return dataSituacaoEmAndamento;
	}

	public void setDataSituacaoEmAndamento(Date dataCancelamento) {
		this.dataSituacaoEmAndamento = dataCancelamento;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getPadDataDocumento() {
		return padDataDocumento;
	}

	public void setPadDataDocumento(Date dataAutorizacao) {
		this.padDataDocumento = dataAutorizacao;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataConclusao) {
		this.dataFim = dataConclusao;
	}

	public Date getDataSituacaoPendente() {
		return dataSituacaoPendente;
	}

	public void setDataSituacaoPendente(Date dataSituacaoPendente) {
		this.dataSituacaoPendente = dataSituacaoPendente;
	}

	public String getNomeTestemunha() {
		return nomeTestemunha;
	}

	public void setNomeTestemunha(String motivo) {
		this.nomeTestemunha = Sigepen.formatarEspacos(motivo);
	}

	public String getFatoDescricao() {
		return fatoDescricao;
	}

	public void setFatoDescricao(String fatoDescricao) {
		this.fatoDescricao = fatoDescricao.toUpperCase();
	}

	public String getPadNumeroDocumento() {
		return padNumeroDocumento;
	}

	public void setPadNumeroDocumento(String observacao) {
		this.padNumeroDocumento = Sigepen.formatarEspacos(observacao);
	}

	public String getFatoNumeroDocumento() {
		return fatoNumeroDocumento;
	}

	public void setFatoNumeroDocumento(String numeroDocumento) {
		this.fatoNumeroDocumento = Sigepen.formatarEspacos(numeroDocumento);
	}

	public Date getFatoData() {
		return fatoData;
	}

	public void setFatoData(Date dataDocumento) {
		this.fatoData = dataDocumento;
	}	

	public Date getDataSituacaoConcluida() {
		return dataSituacaoConcluida;
	}

	public void setDataSituacaoConcluida(Date data) {
		this.dataSituacaoConcluida = data;
	}

	public SimNao getReconhecida() {
		return reconhecida;
	}

	public void setReconhecida(SimNao reconhecida) {
		this.reconhecida = reconhecida;
	}

	public SituacaoFaltaDisciplinar getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoFaltaDisciplinar situacao) {
		this.situacao = situacao;
	}
	
	public Date getDataSituacaoCancelada() {
		return dataSituacaoCancelada;
	}

	public void setDataSituacaoCancelada(Date data) {
		this.dataSituacaoCancelada = data;
	}	

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(String motivo) {
		this.motivoCancelamento = motivo;
	}

	public Date getParecerData() {
		return parecerData;
	}

	public void setParecerData(Date data) {
		this.parecerData = data;
	}

	public String getParecerDescricao() {
		return parecerDescricao;
	}

	public void setParecerDescricao(String parecerDescricao) {
		this.parecerDescricao = parecerDescricao.toUpperCase();
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

	public String getPadDescricao() {
		return padDescricao;
	}

	public void setPadDescricao(String responsavelDestino) {
		this.padDescricao = responsavelDestino.toUpperCase();
	}

	public Estrutura getEstruturaOcorrencia() {
		return estruturaOcorrencia;
	}

	public void setEstruturaOcorrencia(Estrutura lotacaoAtual) {
		this.estruturaOcorrencia = lotacaoAtual;
	}

	public String getParecerNumero() {
		return parecerNumero;
	}

	public void setParecerNumero(String textoOficio) {
		this.parecerNumero = textoOficio;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public SituacaoFaltaDisciplinar getNovaSituacao() {
		return novaSituacao;
	}

	public void setNovaSituacao(SituacaoFaltaDisciplinar novaSituacao) {
		this.novaSituacao = novaSituacao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((recuperando == null) ? 0 : recuperando.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FaltaDisciplinar other = (FaltaDisciplinar) obj;
		if (id != other.id)
			return false;
		if (recuperando == null) {
			if (other.recuperando != null)
				return false;
		} else if (!recuperando.equals(other.recuperando))
			return false;
		return true;
	}	

	@Override
	public String toString() {
		return "Falta Disciplinar [id=" + id + "]";
	}

	public String getSituacaoTransicao(){
		String situacaoTransicao = "";
		if(this.getSituacao() == null){
			situacaoTransicao =  " > "+this.getNovaSituacao().getDescricao();
		}else if(this.getNovaSituacao() == null){
			situacaoTransicao = this.getSituacao().getDescricao();
		}else if(!this.getSituacao().equals(this.getNovaSituacao())){
			situacaoTransicao = this.getSituacao().getDescricao()+" > "+this.getNovaSituacao().getDescricao();
		}else{
			situacaoTransicao = this.getSituacao().getDescricao();
		}
		return situacaoTransicao;
	}

	public boolean isFatoSuspendeVisita() {
		return fatoSuspendeVisita;
	}

	public void setFatoSuspendeVisita(boolean fatoSuspendeVisita) {
		this.fatoSuspendeVisita = fatoSuspendeVisita;
	}

	public boolean isPadSuspendeVisita() {
		return padSuspendeVisita;
	}

	public void setPadSuspendeVisita(boolean padSuspendeVisita) {
		this.padSuspendeVisita = padSuspendeVisita;
	}

	public boolean isParecerSuspendeVisita() {
		return parecerSuspendeVisita;
	}

	public void setParecerSuspendeVisita(boolean parecerSuspendeVisita) {
		this.parecerSuspendeVisita = parecerSuspendeVisita;
	}
}