package br.gov.mt.sejudh.sigepen.dao.filter;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;

public class FiltroRemanejamento extends FiltroConsulta {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String recuperandoNome;
	
	private String nomeMaeRecuperando;
	
	private UnidadePrisional unidadeOrigem;
	
	private Date dataInicio;
	
	private Date dataFim;
	
	public UnidadePrisional unidadeRemanejada;
	
	private StatusPedido status;
	
	private String observacao;
	
	private Date dataDocumento;
	
	private String numeroDocumento;
	
	private String nomeUnidadeDestino;
	
	private String motivo;
	
	private String resultado;
	
	private Imagem foto;
	
	private String unidadeRecuperando;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRecuperandoNome() {
		return recuperandoNome;
	}

	public void setRecuperandoNome(String recuperandoNome) {
		this.recuperandoNome = recuperandoNome;
	}

	public UnidadePrisional getUnidadeOrigem() {
		return unidadeOrigem;
	}

	public void setUnidadeOrigem(UnidadePrisional unidadeOrigem) {
		this.unidadeOrigem = unidadeOrigem;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}	

	public UnidadePrisional getUnidadeRemanejada() {
		return unidadeRemanejada;
	}

	public void setUnidadeRemanejada(UnidadePrisional unidadeRemanejada) {
		this.unidadeRemanejada = unidadeRemanejada;
	}

	public StatusPedido getStatus() {
		return status;
	}

	public void setStatus(StatusPedido status) {
		this.status = status;
	}
	
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	public String getNomeMaeRecuperando() {
		return nomeMaeRecuperando;
	}

	public void setNomeMaeRecuperando(String nomeMaeRecuperando) {
		this.nomeMaeRecuperando = nomeMaeRecuperando;
	}
	
	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
	
	public Date getDataDocumento() {
		return dataDocumento;
	}

	public void setDataDocumento(Date dataDocumento) {
		this.dataDocumento = dataDocumento;
	}
	
	public String getNomeUnidadeDestino() {
		return nomeUnidadeDestino;
	}

	public void setNomeUnidadeDestino(String nomeUnidadeDestino) {
		this.nomeUnidadeDestino = nomeUnidadeDestino;
	}
	
	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	
	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	
	public Imagem getFoto() {
		return foto;
	}

	public void setFoto(Imagem foto) {
		this.foto = foto;
	}
	
	public String getUnidadeRecuperando() {
		return unidadeRecuperando;
	}

	public void setUnidadeRecuperando(String unidadeRecuperando) {
		this.unidadeRecuperando = unidadeRecuperando;
	}

		
	@Override
	public void configurarFiltros() {
		
		getCriteria()
		.createAlias("pedido", "pedido", JoinType.LEFT_OUTER_JOIN)
		.createAlias("pedido.unidadeOrigem", "unidadeOrigem", JoinType.LEFT_OUTER_JOIN)
		.createAlias("pedido.unidadeDestino", "unidadeDestino", JoinType.LEFT_OUTER_JOIN)
		.createAlias("pedido.recuperando", "recuperando", JoinType.LEFT_OUTER_JOIN)
		.createAlias("foto", "foto", JoinType.LEFT_OUTER_JOIN)
		.createAlias("unidadeRemanejada", "unidadeRecuperando", JoinType.LEFT_OUTER_JOIN)
		.createAlias("recuperando.pessoa", "pessoa", JoinType.LEFT_OUTER_JOIN);
		
		
		Projection proj = Projections.projectionList()
				.add(Projections.property("id").as("id"))
				.add(Projections.property("recuperando.id").as("recId"))
				.add(Projections.property("pedido.id").as("pedidoId"))
				.add(Projections.property("pessoa.nome").as("recuperando"))
				.add(Projections.property("dataInicio").as("dataInicio"))
				.add(Projections.property("dataConclusao").as("dataConclusao"))
				.add(Projections.property("unidadeRecuperando.nome").as("unidadeRecuperando"))
				.add(Projections.property("unidadeOrigem.nome").as("unidadeOrigem"))
	            .add(Projections.property("status").as("status"))
	            .add(Projections.property("pedido.tipoTransito").as("tipoTransito"))
	            .add(Projections.property("observacao").as("observacao"))
	            .add(Projections.property("pessoa.nomeMae").as("nomeMaeRecuperando"))
	            .add(Projections.property("pedido.numeroDocumento").as("numeroDocumento"))
	            .add(Projections.property("pedido.dataDocumento").as("dataDocumento"))
				.add(Projections.property("unidadeDestino.nome").as("nomeUnidadeDestino"))
				.add(Projections.property("pedido.motivo").as("motivo"))
				.add(Projections.property("foto").as("foto"))
				.add(Projections.property("resultado").as("resultado"));
	            
		
		getCriteria().setProjection(proj);	
		
		if (this.getId() != null) {
			getCriteria().add(Restrictions.eq("id", this.getId()));
		}
		
		if (this.getRecuperandoNome() != null && !this.getRecuperandoNome().equalsIgnoreCase("")) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa.nome", this.getRecuperandoNome(), MatchMode.ANYWHERE));
		}
		
		if (this.getUnidadeOrigem()!= null){
			getCriteria().add(Restrictions.eq("pedido.unidadeOrigem", this.getUnidadeOrigem()));
		}
		
		if(this.getUnidadeRemanejada()!= null){
			getCriteria().add(Restrictions.eq("unidadeRemanejada", this.getUnidadeRemanejada()));
		}
		
		if(this.getDataInicio() != null){
			getCriteria().add(Restrictions.ge("dataInicio", this.getDataInicio()));
		}
		
		if(this.getDataFim()!= null){
			getCriteria().add(Restrictions.le("dataFim", this.getDataFim()));
		}
		
		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}
		
		if(this.getObservacao() != null) {
			getCriteria().add(Restrictions.eq("observacao", this.getStatus()));
		}
		
		if(this.getNomeMaeRecuperando() !=null && !this.getNomeMaeRecuperando().equalsIgnoreCase("")) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa.nomeMae", this.getNomeMaeRecuperando(), MatchMode.ANYWHERE));
		}
		
		if(this.getNumeroDocumento() !=null) {
			getCriteria().add(Restrictions.eq("pedido.numeroDocumento", this.getNumeroDocumento()));
		}
		
		if(this.getDataDocumento() != null){
			getCriteria().add(Restrictions.ge("dataDocumento", this.getDataDocumento()));
		}
		

		if (this.getNomeUnidadeDestino()!= null){
			getCriteria().add(Restrictions.eq("pedido.unidadeDestino", this.getNomeUnidadeDestino()));
		}
		
		if(this.getMotivo() !=null) {
			getCriteria().add(Restrictions.eq("pedido.motivo", this.getMotivo()));
		}
		
		if(this.getResultado() !=null) {
			getCriteria().add(Restrictions.eq("resultado", this.getResultado()));
		}
		
		if(this.getFoto() !=null) {
			getCriteria().add(Restrictions.eq("foto", this.getFoto()));
		}
		
		
		if(this.getUnidadeRecuperando() !=null) {
			getCriteria().add(Restrictions.eq("unidadeRecuperando", this.getUnidadeRecuperando()));
		}
				
		
	}

	public String getDescricao() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String filtros = "";
		
		if(this.getId()!= null)
			filtros += "Número Pedido: " + this.getId() + ", ";
		
		if (this.getRecuperandoNome()!= null && !this.getRecuperandoNome().equalsIgnoreCase(""))
			filtros += "Nome Recuperando: " + this.getRecuperandoNome() + ", ";
		
		if (this.getUnidadeOrigem() != null)
			filtros += "Unidade Origem: " + this.getUnidadeOrigem(); 
		
		if(this.getDataInicio() != null && this.getDataFim() != null)
			filtros += "Período: " + sdf.format(this.getDataInicio()) + " a " + sdf.format(this.getDataFim()) + ", ";
		
		if (this.getStatus() != null) 
			filtros += "Status: " + this.getStatus().getDescricao() + ", ";
		
		if(!"".equals(filtros))
			return "Filtro utilizado: " + filtros;
		return null;
	}
	
}
