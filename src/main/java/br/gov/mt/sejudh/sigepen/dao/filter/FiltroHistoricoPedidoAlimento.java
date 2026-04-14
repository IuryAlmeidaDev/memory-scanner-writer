package br.gov.mt.sejudh.sigepen.dao.filter;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import org.hibernate.criterion.Restrictions;

import java.util.Calendar;
import java.util.Date;

public class FiltroHistoricoPedidoAlimento extends FiltroConsulta {

	private static final long serialVersionUID = 1L;

	private Long idUnidadePrisional;
	private Long idContrato;
	private TipoRefeicao tipoRefeicao;
	private Date dataPedidoInicial;
	private Date dataPedidoFinal;
	private Status status;

	@Override
	public void configurarFiltros() {
		if (idUnidadePrisional != null) {
			getCriteria().add(Restrictions.eq("idUnidadePrisional", idUnidadePrisional));
		}
		if (idContrato != null) {
			getCriteria().add(Restrictions.eq("idContrato", idContrato));
		}
		if (tipoRefeicao != null && tipoRefeicao.getId() != null) {
			getCriteria().add(Restrictions.eq("idTipoRefeicao", tipoRefeicao.getId()));
		}

		if (dataPedidoInicial != null) {
	        getCriteria().add(Restrictions.ge("dataEmissao", dataPedidoInicial));
	    }
	    
	    if (dataPedidoFinal != null) {
	        Calendar c = Calendar.getInstance();
	        c.setTime(dataPedidoFinal);
	        c.set(Calendar.HOUR_OF_DAY, 23);
	        c.set(Calendar.MINUTE, 59);
	        c.set(Calendar.SECOND, 59);
	        
	        getCriteria().add(Restrictions.le("dataEmissao", c.getTime()));
	    }
	}

	public Long getIdUnidadePrisional() {
		return idUnidadePrisional;
	}

	public void setIdUnidadePrisional(Long idUnidadePrisional) {
		this.idUnidadePrisional = idUnidadePrisional;
	}

	public Long getIdContrato() {
		return idContrato;
	}

	public void setIdContrato(Long idContrato) {
		this.idContrato = idContrato;
	}

	public TipoRefeicao getTipoRefeicao() {
		return tipoRefeicao;
	}

	public void setTipoRefeicao(TipoRefeicao tipoRefeicao) {
		this.tipoRefeicao = tipoRefeicao;
	}

	public Date getDataPedidoInicial() {
		return dataPedidoInicial;
	}

	public void setDataPedidoInicial(Date dataPedidoInicial) {
		this.dataPedidoInicial = dataPedidoInicial;
	}

	public Date getDataPedidoFinal() {
		return dataPedidoFinal;
	}

	public void setDataPedidoFinal(Date dataPedidoFinal) {
		this.dataPedidoFinal = dataPedidoFinal;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}