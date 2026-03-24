package br.gov.mt.sejudh.sigepen.dao.filter;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import org.hibernate.criterion.Restrictions;
import java.util.Date;
import java.text.SimpleDateFormat;

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

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if (dataPedidoInicial != null) {
			getCriteria().add(Restrictions.ge("dataPedido", sdf.format(dataPedidoInicial) + " 00:00"));
		}
		if (dataPedidoFinal != null) {
			getCriteria().add(Restrictions.le("dataPedido", sdf.format(dataPedidoFinal) + " 23:59"));
		}

		if (status != null) {
			getCriteria().add(Restrictions.eq("status", status));
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