package br.gov.mt.sejudh.sigepen.bean.outros;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.bean.FileDownloadHandlerBean;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroListaConfere;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoPlantao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.Plantao;
import br.gov.mt.sejudh.sigepen.model.dto.GerarPedidoEstruturaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMapping(id = "GerarListaDeConfere", pattern = "/recuperando/lista-chamada/confere", viewId = "/WEB-INF/pages/_dialogs/ListaDeConfere.xhtml")
public class ListaDeChamadaBean extends MetodosDialogBean {
	String tipoRelatorio;
	UnidadePrisionalDTO unidade;
	Estrutura estrutura;
	GerarPedidoEstruturaDTO gerarPedidoEstruturaDTO;
	FiltroListaConfere filtroConfere;
	int diasIntervalo;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private FileDownloadHandlerBean fileDownload;

	@Inject
	private Logger log;


	@URLAction(onPostback = false)
	public String init() {
		configurarDadosRelatorio();
		filtroConfere = new FiltroListaConfere();

		return null;
	}

	public void imprimirListaDeChamada(Boolean comFoto) {
		try {
			configurarDadosRelatorio();

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("SCHEMA", Sigepen.SEJUDH_SCHEMA);

			switch (tipoRelatorio) {
			case "unidade":
				parameters.put("ID_ESTRUTURA", Integer.parseInt(String.valueOf(unidade.getIdEstrutura())));
				parameters.put("ESTRUTURA_NOME", unidade.getNome());
				break;

			case "estrutura":
				parameters.put("ID_ESTRUTURA", Integer.parseInt(String.valueOf(estrutura.getId())));
				int index = estrutura.getNomeCompleto().indexOf(">");

				if (index > -1) {
					parameters.put("ESTRUTURA_NOME", estrutura.getNomeCompleto().substring(0, index));
				} else {
					parameters.put("ESTRUTURA_NOME", estrutura.getNomeCompleto());
				}
				break;
			}

			if (comFoto) {
				fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(Relatorio.ListaDeChamadaComFoto, parameters));
				LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Chamada com Foto", log);
			} else {
				fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(Relatorio.ListaDeChamada, parameters));
				LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Chamada", log);
			}
			RequestContext.getCurrentInstance().execute("download()");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Chamada", e, log);
		}
	}

	public void imprimirListaSintetica() {
		try {
			configurarDadosRelatorio();

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("SCHEMA", Sigepen.SEJUDH_SCHEMA);

			switch (tipoRelatorio) {
			case "unidade":
				parameters.put("ID_ESTRUTURA", unidade.getIdEstrutura());
				break;

			case "estrutura":
				parameters.put("ID_ESTRUTURA", estrutura.getId());
				break;
			}

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(Relatorio.ListaDeChamadaSintetica, parameters));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Chamada Sintética", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Chamada Sintética", e, log);
		}
	}

	public void imprimirListaConfere() {
		try {
			validarParametrosConfere();

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("SCHEMA", Sigepen.SEJUDH_SCHEMA);
			parameters.put("SUBREPORT_DIR", RelatorioUtil.getSubReportPath(Relatorio.ListaDeConfere));

			switch (tipoRelatorio) {
			case "unidade":
				parameters.put("ID_ESTRUTURA", Integer.parseInt(String.valueOf(unidade.getIdEstrutura())));
				parameters.put("ESTRUTURA_NOME", unidade.getNome());
				break;

			case "estrutura":
				parameters.put("ID_ESTRUTURA", Integer.parseInt(String.valueOf(estrutura.getId())));

				int index = estrutura.getNomeCompleto().indexOf(">");

				if (index > -1) {
					parameters.put("ESTRUTURA_NOME", estrutura.getNomeCompleto().substring(0, index));
				} else {
					parameters.put("ESTRUTURA_NOME", estrutura.getNomeCompleto());
				}
				break;
			}

			parameters.put("PLANTOES", gerarListaPlantoes());

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(Relatorio.ListaDeConfere, parameters));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Chamada Confere", log);
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Chamada Confere", e, log);
		}
	}

	private void configurarDadosRelatorio() {
		unidade = (UnidadePrisionalDTO) facesUtil.getObjetoSessao("unidade");
		estrutura = (Estrutura) facesUtil.getObjetoSessao("estrutura");
		tipoRelatorio = (String) facesUtil.getObjetoSessao("tipo_relatorio");
	}

	public void abrirDialogConfere() {
		facesUtil.exibirPaginaDialog("pretty:GerarListaDeConfere", 201, 431);
	}

	public void fecharDialogConfere(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

	private void validarParametrosConfere() throws ValidacaoException {
		if ((filtroConfere.getDataFinal() == null) || (filtroConfere.getDataInicial() == null)) {
			throw new ValidacaoException("As datas devem ser preenchidas.");
		}

		if (filtroConfere.getDataFinal().before(filtroConfere.getDataInicial())
				|| filtroConfere.getDataInicial().after(filtroConfere.getDataFinal())) {
			throw new ValidacaoException("Data Final não pode ser maior que Data Inicial");
		}

		diasIntervalo = getDataIntervalo();

		if (diasIntervalo > 7 || diasIntervalo <= 0) {
			throw new ValidacaoException("Intervalo das datas deve ser no mínimo 1 e no máximo 7 dias.");
		}
	}

	private int getDataIntervalo() {
		LocalDate dataInicio = filtroConfere.getDataInicial().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate dataFinal = filtroConfere.getDataFinal().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		Period p = Period.between(dataInicio, dataFinal);

		return p.getDays() + 1;
	}

	private List<Plantao> gerarListaPlantoes() {
		List<Plantao> plantoes = new ArrayList<>();
		Plantao primeiroPlantao = new Plantao(filtroConfere.getPlantaoInicial(), filtroConfere.getDataInicial());

		int dias = getDataIntervalo();
		int atual = primeiroPlantao.getPlantao().ordinal();

		for (int i = 0; i < dias; i++) {
			if (atual >= TipoPlantao.values().length) {
				atual = 0;
			}

			Calendar c = Calendar.getInstance();
			c.setTime(filtroConfere.getDataInicial());
			c.add(Calendar.DATE, i);

			Plantao plantao = new Plantao(TipoPlantao.getFromOrdinal(atual), c.getTime());
			plantoes.add(plantao);
			atual += 1;
		}

		return plantoes;
	}

	public FiltroListaConfere getFiltroConfere() {
		return filtroConfere;
	}

	public void setFiltroConfere(FiltroListaConfere filtroConfere) {
		this.filtroConfere = filtroConfere;
	}
}
