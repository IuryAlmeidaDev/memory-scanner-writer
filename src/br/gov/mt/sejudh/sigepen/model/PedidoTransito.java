package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.gov.mt.sejudh.sigepen.enumerator.PronomeTratamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDocumentoPedidoTransito;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.util.Sigepen;


@Entity
@Audited
@Table(name = "PEDIDO_TRANSITO", schema = Sigepen.SEJUDH_SCHEMA)
public class PedidoTransito implements Serializable{


	private static final long serialVersionUID = -5836484679427900452L;

	@SequenceGenerator(name = "SEQ_PEDIDO_GENERATOR", sequenceName = "SEQ_PEDIDO_TRANSITO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PEDIDO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Version
	private Long versao;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_TRANSITO")
	private TipoTransito tipoTransito;

	@ManyToOne(optional=false)
	@JoinColumn(name = "RECUPERANDO_ID")
	private Recuperando recuperando;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_PREVISTA")
	private Date dataPrevista;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_AUTORIZACAO")
	private Date dataAutorizacao;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_CANCELAMENTO")
	private Date dataCancelamento;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_INICIO")
	private Date dataInicio;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_CONCLUSAO")
	private Date dataConclusao;

	@Column(name = "MOTIVO", length = 250, nullable = false)
	private String motivo;
	
	@Column(name = "OBSERVACAO", length = 250)
	private String observacao;
	
	@Column(name = "RESULTADO", length = 250)
	private String resultado;
	
	@Column(name = "NUMERO_DOCUMENTO", length = 50)
	private String numeroDocumento;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_DOCUMENTO")
	private Date dataDocumento;
	
	@ManyToOne
	@JoinColumn(name = "FOTO_ID")
	private Foto foto;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "ESTRUTURA_ORIGEM_ID")
	private Estrutura estruturaOrigem;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "ESTRUTURA_DESTINO_ID")
	private Estrutura estruturaDestino;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "UNIDADE_ORIGEM_ID")
	private UnidadePrisional unidadeOrigem;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "UNIDADE_DESTINO_ID")
	private UnidadePrisional unidadeDestino;
	
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "TIPO_MOVIMENTACAO")
	private TipoHistorico tipoMovimentacao;
		
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "ESTABELECIMENTO_ID")
	private Estabelecimento estabelecimento;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "MOV_INTERNA_ID")
	private MovimentacaoInterna movimentacaoInterna;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "TRANSFERENCIA_ID")
	private Transferencia transferencia;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "MOV_EXTERNA_ID")
	private MovimentacaoExterna movimentacaoExterna;
	
	@OneToMany(mappedBy="pedido", fetch = FetchType.LAZY)
	private List<Remanejamento> remanejamentos; 
	
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private StatusPedido status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AUTORIZADOR")
	private Usuario autorizador;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "PRONOME_TRATAMENTO")
	private PronomeTratamento pronomeTratamento;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_DOCUMENTO_PEDIDO_TRANSITO")
	private TipoDocumentoPedidoTransito tipoDocumentoPedidoTransito;
	
	@Column(name = "RESPONSAVEL_DESTINO", length = 80)
	private String responsavelDestino;
	
	@Transient
	private AutoriaRegistro autorRegistro;
	
	@Transient
	private AutoriaRegistro autorUltimaAlteracao;
	
	@Transient
	private String textoOficio;
	
	@Transient
	private String textoAutorizacao;
	
	public PedidoTransito() {		
	}
	
	public PedidoTransito(StatusPedido status, UnidadePrisional unidade) {
		this.unidadeOrigem = unidade;
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public TipoTransito getTipoTransito() {
		return tipoTransito;
	}

	public void setTipoTransito(TipoTransito tipoTransito) {
		this.tipoTransito = tipoTransito;
	}

	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
	}

	public Date getDataPrevista() {
		return dataPrevista;
	}

	public void setDataPrevista(Date dataPrevista) {
		this.dataPrevista = dataPrevista;
	}

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataAutorizacao() {
		return dataAutorizacao;
	}

	public void setDataAutorizacao(Date dataAutorizacao) {
		this.dataAutorizacao = dataAutorizacao;
	}

	public Date getDataConclusao() {
		return dataConclusao;
	}

	public void setDataConclusao(Date dataConclusao) {
		this.dataConclusao = dataConclusao;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = Sigepen.formatarEspacos(motivo);
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = Sigepen.formatarEspacos(resultado);
	}
	
	public void setResultadoParaRelatorio(String resultado) {
		this.resultado = resultado;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = Sigepen.formatarEspacos(observacao);
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = Sigepen.formatarEspacos(numeroDocumento);
	}

	public Date getDataDocumento() {
		return dataDocumento;
	}

	public void setDataDocumento(Date dataDocumento) {
		this.dataDocumento = dataDocumento;
	}	

	public Estrutura getEstruturaOrigem() {
		return estruturaOrigem;
	}

	public void setEstruturaOrigem(Estrutura estruturaOrigem) {
		this.estruturaOrigem = estruturaOrigem;
	}

	public Estrutura getEstruturaDestino() {
		return estruturaDestino;
	}

	public void setEstruturaDestino(Estrutura estruturaDestino) {
		this.estruturaDestino = estruturaDestino;
	}

	public UnidadePrisional getUnidadeOrigem() {
		return unidadeOrigem;
	}

	public void setUnidadeOrigem(UnidadePrisional unidadeOrigem) {
		this.unidadeOrigem = unidadeOrigem;
	}

	public UnidadePrisional getUnidadeDestino() {
		return unidadeDestino;
	}

	public void setUnidadeDestino(UnidadePrisional unidadeDestino) {
		this.unidadeDestino = unidadeDestino;
	}
	
	public TipoHistorico getTipoMovimentacao() {
		return tipoMovimentacao;
	}

	public void setTipoMovimentacao(TipoHistorico tipoMovimentacao) {
		this.tipoMovimentacao = tipoMovimentacao;
	}

	public Estabelecimento getEstabelecimento() {
		return estabelecimento;
	}

	public void setEstabelecimento(Estabelecimento estabelecimento) {
		this.estabelecimento = estabelecimento;
	}	

	public MovimentacaoInterna getMovimentacaoInterna() {
		return movimentacaoInterna;
	}

	public void setMovimentacaoInterna(MovimentacaoInterna movimentacaoInterna) {
		this.movimentacaoInterna = movimentacaoInterna;
	}	

	public MovimentacaoExterna getMovimentacaoExterna() {
		return movimentacaoExterna;
	}

	public void setMovimentacaoExterna(MovimentacaoExterna movimentacaoExterna) {
		this.movimentacaoExterna = movimentacaoExterna;
	}

	public Transferencia getTransferencia() {
		return transferencia;
	}

	public void setTransferencia(Transferencia transferencia) {
		this.transferencia = transferencia;
	}

	public StatusPedido getStatus() {
		return status;
	}

	public void setStatus(StatusPedido status) {
		this.status = status;
	}

	public Foto getFoto() {
		return foto;
	}

	public void setFoto(Foto foto) {
		this.foto = foto;
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

	public Usuario getAutorizador() {
		return autorizador;
	}

	public void setAutorizador(Usuario autorizador) {
		this.autorizador = autorizador;
	}

	public PronomeTratamento getPronomeTratamento() {
		return pronomeTratamento;
	}

	public void setPronomeTratamento(PronomeTratamento pronomeTratamento) {
		this.pronomeTratamento = pronomeTratamento;
	}

	public TipoDocumentoPedidoTransito getTipoDocumentoPedidoTransito() {
		return tipoDocumentoPedidoTransito;
	}

	public void setTipoDocumentoPedidoTransito(TipoDocumentoPedidoTransito tipoDocumentoPedidoTransito) {
		this.tipoDocumentoPedidoTransito = tipoDocumentoPedidoTransito;
	}

	public String getResponsavelDestino() {
		return responsavelDestino;
	}

	public void setResponsavelDestino(String responsavelDestino) {
		this.responsavelDestino = Sigepen.formatarEspacos(responsavelDestino);
	}
	
	public String getTextoOficio() {
		return textoOficio;
	}

	public void setTextoOficio(String textoOficio) {
		this.textoOficio = textoOficio;
	}

	public String getTextoAutorizacao() {
		return textoAutorizacao;
	}

	public void setTextoAutorizacao(String textoAutorizacao) {
		this.textoAutorizacao = textoAutorizacao;
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
		PedidoTransito other = (PedidoTransito) obj;
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
		return "Pedido Trânsito [id=" + id + "]";
	}
}