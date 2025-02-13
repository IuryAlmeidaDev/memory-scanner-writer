package br.gov.mt.sejudh.sigepen.dao.filter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.NullPrecedence;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.model.Estabelecimento;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroPedTransito extends FiltroConsulta {
	
	private static final long serialVersionUID = 1L;

	private Long id; 
	
	private Date dataInicio;
	
	private Date dataFim;

	public Long recuperandoId;	
	
	private String nome;
	
	private Estrutura estrutura;
	
	private Estabelecimento estabelecimento;
	
	public UnidadePrisional unidadeOrigem;
	
	public UnidadePrisional unidadeDestino;
	
	public Boolean foraDomicilio;
	
	private TipoTransito tipoTransito;
	
	private List<TipoTransito> listaTipoTransito;
	
	private StatusPedido status;
	
	private Boolean isExtratoRecuperando;
	
	private String nomeSocial;
	
	public FiltroPedTransito() {
		listaTipoTransito = new ArrayList<TipoTransito>();
		this.isExtratoRecuperando = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	

	public Long getRecuperandoId() {
		return recuperandoId;
	}

	public void setRecuperandoId(Long recuperandoId) {
		this.recuperandoId = recuperandoId;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
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

	public Boolean getForaDomicilio() {
		return foraDomicilio;
	}

	public void setForaDomicilio(Boolean foraDomicilio) {
		this.foraDomicilio = foraDomicilio;
	}

	public TipoTransito getTipoTransito() {
		return tipoTransito;
	}

	public void setTipoTransito(TipoTransito tipoTransito) {
		this.tipoTransito = tipoTransito;
	}

	public StatusPedido getStatus() {
		return status;
	}

	public void setStatus(StatusPedido status) {
		this.status = status;
	}

	public Estrutura getEstrutura() {
		return estrutura;
	}

	public void setEstrutura(Estrutura estrutura) {
		this.estrutura = estrutura;
	}

	public Estabelecimento getEstabelecimento() {
		return estabelecimento;
	}

	public void setEstabelecimento(Estabelecimento estabelecimento) {
		this.estabelecimento = estabelecimento;
	}

	public List<TipoTransito> getListaTipoTransito() {
		return listaTipoTransito;
	}

	public void setListaTipoTransito(List<TipoTransito> listaTipoTransito) {
		this.listaTipoTransito = listaTipoTransito;
	}

	public Boolean getIsExtratoRecuperando() {
		return isExtratoRecuperando;
	}

	public void setIsExtratoRecuperando(Boolean isExtratoRecuperando) {
		this.isExtratoRecuperando = isExtratoRecuperando;
	}		

	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

	@Override
	public void configurarFiltros() {
		
		getCriteria()
		.createAlias("recuperando", "recuperando", JoinType.LEFT_OUTER_JOIN)
		.createAlias("recuperando.pessoa", "pessoa", JoinType.LEFT_OUTER_JOIN)
		.createAlias("unidadeOrigem", "unidadeOrigem", JoinType.LEFT_OUTER_JOIN)
		.createAlias("unidadeDestino", "unidadeDestino", JoinType.LEFT_OUTER_JOIN)
		.createAlias("estabelecimento", "estabelecimento", JoinType.LEFT_OUTER_JOIN)
		.createAlias("estruturaDestino", "estrutura", JoinType.LEFT_OUTER_JOIN)
		.createAlias("estruturaOrigem", "estrutura2", JoinType.LEFT_OUTER_JOIN)
        .createAlias("transferencia", "transferencia", JoinType.LEFT_OUTER_JOIN);
		
		getCriteria()
			.createAlias("estrutura2.estruturaComplemento", "estruturacomplOrigem", JoinType.LEFT_OUTER_JOIN)
			.createAlias("estrutura.estruturaComplemento", "estruturacomplDestino", JoinType.LEFT_OUTER_JOIN);
		
		Projection proj = Projections.projectionList()
				.add(Projections.property("id").as("id"))
				.add(Projections.property("recuperando.id").as("idRec"))
				.add(Projections.property("pessoa.nome").as("recuperando"))
				.add(Projections.property("tipoTransito").as("tipoTransito"))
				.add(Projections.property("dataPrevista").as("dataPrevista"))
	            .add(Projections.property("status").as("status"))
	            .add(Projections.property("estabelecimento.nome").as("estabelecimentoNome"))
	            .add(Projections.property("estrutura.nome").as("estruturaNome"))
	            .add(Projections.property("estrutura2.nome").as("estruturaOrigemNome"))
	            .add(Projections.property("unidadeDestino.nome").as("nomeUnidadeDestino"))
	            .add(Projections.property("unidadeOrigem.nome").as("nomeUnidadeOrigem"))
	            .add(Projections.property("transferencia.id").as("idTransferencia"))
	            .add(Projections.property("dataInicio").as("dataInicio"))
	            .add(Projections.property("dataConclusao").as("dataConclusao"))
	            .add(Projections.property("observacao").as("observacao"))
				.add(Projections.property("pessoa.nomeSocial").as("nomeSocial"));
		
		ProjectionList projectionList = (ProjectionList) proj;
		
		projectionList.add(Projections.property("estruturacomplOrigem.nomeCompleto").as("estruturaNomeCompleto"));
		projectionList.add(Projections.property("estruturacomplDestino.nomeCompleto").as("estruturaDestinoNomeCompleto"));
		projectionList.add(Projections.property("estruturacomplDestino.nomeCompletoUnidade").as("estruturaDestinoNomeCompletoComUnidade"));
		
		getCriteria().setProjection(projectionList);
		
		if (this.isExtratoRecuperando) {
			getCriteria()
				.createAlias("movimentacaoInterna", "movInt", JoinType.LEFT_OUTER_JOIN)
				.createAlias("movimentacaoExterna", "movExt", JoinType.LEFT_OUTER_JOIN);
			
			projectionList.add(Projections.property("movInt.dataConclusao").as("dataTerminoMovInt"));
			projectionList.add(Projections.property("movExt.dataInicio").as("dataTerminoMovExt"));
			projectionList.add(Projections.property("transferencia.dataConclusao").as("dataTerminoTransf"));
		}
		
		if(this.getId()!= null){
			getCriteria().add(Restrictions.eq("id", this.getId()));
		}
		
		if(this.getRecuperandoId()!= null){
			getCriteria().add(Restrictions.eq("recuperando.id", this.getRecuperandoId()));
		}
		
		if (this.getNome()!= null && !this.getNome().equalsIgnoreCase("")) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa.nome", this.getNome(), MatchMode.ANYWHERE));
		}
		
		if(this.getEstrutura()!= null){
			getCriteria().add(Restrictions.eq("estruturaDestino", this.getEstrutura()));
		}
		
		if(this.getEstabelecimento()!= null){
			getCriteria().add(Restrictions.eq("estabelecimento", this.getEstabelecimento()));
		}
		
		if(this.getUnidadeOrigem()!= null){
			if(this.getForaDomicilio() == null || this.getForaDomicilio() == false){
				getCriteria().add(Restrictions.eq("unidadeOrigem", this.getUnidadeOrigem()));
			}else{
				getCriteria().add(Restrictions.ne("unidadeOrigem", this.getUnidadeOrigem()));
			}
				
		}
		
		if(this.getUnidadeDestino()!= null){
			getCriteria().add(Restrictions.eq("unidadeDestino", this.getUnidadeDestino()));
		}
		
		if(this.getDataInicio() != null){			
			getCriteria().add(Restrictions.ge("dataPrevista", Sigepen.getDataConsultaMaiorIgual(this.getDataInicio())));
		}
		
		if(this.getDataFim()!= null){
			getCriteria().add(Restrictions.le("dataPrevista", Sigepen.getDataConsultaMenorIgual(this.getDataFim())));
		}
		
		if (this.getListaTipoTransito().size() > 0) {
			getCriteria().add(Restrictions.in("tipoTransito", this.getListaTipoTransito()));
		} else {
			if (this.getTipoTransito() != null) {
				getCriteria().add(Restrictions.eq("tipoTransito", this.getTipoTransito()));
			}
		}
		
		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}
		
		if (this.getNomeSocial()!= null && !this.getNomeSocial().equalsIgnoreCase("")) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa.nomeSocial", this.getNomeSocial(), MatchMode.ANYWHERE));
		}
						
	}
	
	@Override
	public void configurarOrdenacao() {
		super.configurarOrdenacao();
		
		if (this.getOrdenacao() != null) {
			getCriteria().addOrder((this.getOrdenacao().isAscending() ? Order.asc("idTransferencia") : Order.desc("idTransferencia")));
			
			if (this.getPropriedadeOrdenacao().equals("idTransferencia")) {
				this.getOrdenacao().nulls(NullPrecedence.LAST);
			}
		}
	}

	public String getDescricao() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String filtros = "";
		
		if(this.getId()!= null)
			filtros += "Número Pedido: " + this.getId() + ", ";
		
		if(this.getRecuperandoId()!= null)
			filtros += "Matrícula Recuperando: " + this.getRecuperandoId() + ", "; 
		
		if (this.getNome()!= null && !this.getNome().equalsIgnoreCase(""))
			filtros += "Nome Recuperando: " + this.getNome() + ", ";
		
		if(this.getDataInicio() != null && this.getDataFim() != null)
			filtros += "Período: " + sdf.format(this.getDataInicio()) + " a " + sdf.format(this.getDataFim()) + ", ";
		
		if (this.getStatus() != null) 
			filtros += "Status: " + this.getStatus().getDescricao() + ", ";
		
		if(!"".equals(filtros))
			return "Filtro utilizado: " + filtros;
		return null;
	}
		
}



