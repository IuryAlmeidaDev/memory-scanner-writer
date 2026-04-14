package br.gov.mt.sejudh.sigepen.bean.relatorios;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.bean.FileDownloadHandlerBean;
import br.gov.mt.sejudh.sigepen.bean.outros.MetodosDialogBean;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroInfopen;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.IdadeRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.Lgbt;
import br.gov.mt.sejudh.sigepen.enumerator.TempoCondenacao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDeficienciaRelatorio;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.model.Cidade;
import br.gov.mt.sejudh.sigepen.model.Escolaridade;
import br.gov.mt.sejudh.sigepen.model.Estado;
import br.gov.mt.sejudh.sigepen.model.EstadoCivil;
import br.gov.mt.sejudh.sigepen.model.Etnia;
import br.gov.mt.sejudh.sigepen.model.Nacionalidade;
import br.gov.mt.sejudh.sigepen.model.Raca;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.Religiao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.InfopenDTO;
import br.gov.mt.sejudh.sigepen.relatorio.ModeloCabecalho;
import br.gov.mt.sejudh.sigepen.relatorio.ModeloRodape;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.InfopenService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
		@URLMapping(id = "Infopen", pattern = "/relatorio/recuperandos/infopen", viewId = "/WEB-INF/pages/relatorio/Infopen.xhtml"), })
public class InfopenBean extends MetodosDialogBean {

	@Inject
	private InfopenService service;

	@Inject
	private UnidadePrisionalService prisionalService;

	private FiltroInfopen filtro = new FiltroInfopen();
	private Nacionalidade[] nacionalidades = {};
	private Regime[] regimes = {};
	private EstadoCivil[] estadoCivil = {};
	private Religiao[] religiao = {};
	private Escolaridade[] escolaridade = {};
	private Raca[] raca = {};
	private IdadeRecuperando idadeRecuperando;
	private TempoCondenacao tempoCondenacao;
	private Lgbt lbgt;
	private Etnia[] etnia = {};
	private List<Estado> estados = new ArrayList<>();
	private List<Cidade> cidades = new ArrayList<>();

	private List<UnidadePrisional> unidades = new ArrayList<UnidadePrisional>();

	private List<UnidadePrisional> todasUnidades;
	private List<Long> unidadesSelecionadas;

	private boolean filtroUnidade;

	private DualListModel<UnidadePrisional> unidadesPickListModel;

	private String tipoRelatorio;
	private String periodo;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private FileDownloadHandlerBean fileDownload;

	@Inject
	private Logger log;

	@URLAction(onPostback = false)
	public String init() {
		this.filtro.setUnidade(SecurityUtil.getUnidadeSelecionada().getId());
		this.nacionalidades = this.service.buscarNacionalidades(SecurityUtil.getUnidadeSelecionada().getId());
		this.regimes = this.service.buscarRegimes(SecurityUtil.getUnidadeSelecionada().getId());
		this.estadoCivil = this.service.buscarEstadoCivil(SecurityUtil.getUnidadeSelecionada().getId());
		this.religiao = this.service.buscarReligiao(SecurityUtil.getUnidadeSelecionada().getId());
		this.escolaridade = this.service.buscarEscolaridade(SecurityUtil.getUnidadeSelecionada().getId());
		this.raca = this.service.buscarRaca(SecurityUtil.getUnidadeSelecionada().getId());
		this.etnia = this.service.buscarEtnia(SecurityUtil.getUnidadeSelecionada().getId());
		this.estados = this.service.buscarEstados();

		this.filtroUnidade = SecurityUtil.possuiPermissaoNaUnidadeLogada("ListarTodasUnidades");
		if (this.filtroUnidade) {
			this.todasUnidades = prisionalService.listarUnidadesNaoInativas();
			this.unidadesSelecionadas = new ArrayList<>();
			this.unidadesSelecionadas.add(SecurityUtil.getUnidadeSelecionada().getId());

			inicializarPickListUnidades();
		}

		RequestContext.getCurrentInstance().update("meuFormulario");
		return "Pretty:infopen";
	}

	public FiltroInfopen getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroInfopen filtro) {
		this.filtro = filtro;
	}

	public Nacionalidade[] getNacionalidades() {
		return nacionalidades;
	}

	public void setNacionalidades(Nacionalidade[] nacionalidades) {
		this.nacionalidades = nacionalidades;
	}

	public Regime[] getRegimes() {
		return regimes;
	}

	public void setRegimes(Regime[] regimes) {
		this.regimes = regimes;
	}

	public EstadoCivil[] getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(EstadoCivil[] estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public Religiao[] getReligiao() {
		return religiao;
	}

	public void setReligiao(Religiao[] religiao) {
		this.religiao = religiao;
	}

	public Escolaridade[] getEscolaridade() {
		return escolaridade;
	}

	public void setEscolaridade(Escolaridade[] escolaridade) {
		this.escolaridade = escolaridade;
	}

	public Raca[] getRaca() {
		return raca;
	}

	public void setRaca(Raca[] raca) {
		this.raca = raca;
	}

	public IdadeRecuperando getIdadeRecuperando() {
		return idadeRecuperando;
	}

	public void setIdadeRecuperando(IdadeRecuperando idadeRecuperando) {
		this.idadeRecuperando = idadeRecuperando;
	}

	public TempoCondenacao getTempoCondenacao() {
		return tempoCondenacao;
	}

	public void setTempoCondenacao(TempoCondenacao tempoCondenacao) {
		this.tempoCondenacao = tempoCondenacao;
	}

	public boolean isFiltroUnidadesDetalhado() {
		return this.filtroUnidade && (this.filtro.getTipoRelatorio() == 1 || this.filtro.getTipoRelatorio() == 4
				|| this.filtro.getTipoRelatorio() == 5);
	}

	public List<UnidadePrisional> getTodasUnidades() {
		return todasUnidades;
	}

	public List<Long> getUnidadesSelecionadas() {
		return unidadesSelecionadas;
	}

	public void setUnidadesSelecionadas(List<Long> unidadesSelecionadas) {
		this.unidadesSelecionadas = unidadesSelecionadas;
	}
	public List<Estado> getEstados() {
	    return estados;
	}

	public void setEstados(List<Estado> estados) {
	    this.estados = estados;
	}

	public List<Cidade> getCidades() {
	    return cidades;
	}

	public void setCidades(List<Cidade> cidades) {
	    this.cidades = cidades;
	}
	
	public void onMudarEstado() {
	    this.filtro.setCidade(null);
	    this.cidades = new ArrayList<>();

	    if (this.filtro.getEstado() != null 
	            && this.filtro.getEstado().getUf() != null 
	            && !this.filtro.getEstado().getUf().isEmpty()) {
	        this.cidades = this.service.buscarMunicipiosPorEstado(
	                this.filtro.getEstado().getUf());
	    }
	    RequestContext.getCurrentInstance().update("meuFormulario:panelMunicipio");
	}

	public void inicializarPickListUnidades() {
		List<UnidadePrisional> unidadesNaoSelecionada = new ArrayList<>();
		List<UnidadePrisional> unidadesEscolhida = new ArrayList<>();

		if (this.todasUnidades != null) {
			unidadesNaoSelecionada.addAll(this.todasUnidades);
		}

		if (this.unidadesSelecionadas != null && !this.unidadesSelecionadas.isEmpty()) {
			for (Long unidadeId : this.unidadesSelecionadas) {
				UnidadePrisional unidade = this.todasUnidades.stream()
				        .filter(u -> Objects.equals(u.getId(), unidadeId))
				        .findFirst()
				        .orElse(null);

				if (unidade != null) {
					unidadesEscolhida.add(unidade);
					unidadesNaoSelecionada.remove(unidade);
				}
			}
		}

		this.unidadesPickListModel = new DualListModel<>(unidadesNaoSelecionada, unidadesEscolhida);
	}

	public void associarUnidades() {
		if (this.unidadesPickListModel != null) {
			this.unidadesSelecionadas = this.unidadesPickListModel.getTarget().stream().map(UnidadePrisional::getId)
					.collect(Collectors.toList());

			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "Sucesso",
					"Unidades selecionadas: " + this.unidadesSelecionadas.size());
		}
	}

	public DualListModel<UnidadePrisional> getUnidadesPickListModel() {
		if(!filtroUnidade) {
			return new DualListModel<>(new ArrayList<>(), new ArrayList<>());
		}
		if (this.unidadesPickListModel == null && this.todasUnidades != null) {
			inicializarPickListUnidades();
		}
		return unidadesPickListModel;
	}

	public void setUnidadesPickListModel(DualListModel<UnidadePrisional> unidadePickListModel) {
		this.unidadesPickListModel = unidadePickListModel;
	}

	public boolean isFiltroUnidade() {
		return filtroUnidade;
	}

	public void imprimir() {
		try {
			
			Date hoje = new Date();
	        
	        if (this.filtro.getDataInicial() != null && this.filtro.getDataInicial().after(hoje)) {
	            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Data Inválida",
	                    "A Data Inicial não pode ser uma data futura.");
	            return;
	        }
	        
	        if (this.filtro.getDataFinal() != null && this.filtro.getDataFinal().after(hoje)) {
	            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Data Inválida",
	                    "A Data Final não pode ser uma data futura.");
	            return;
	        }
	        
	       
	        if (this.filtro.getDataInicial() != null && this.filtro.getDataFinal() != null) {
	            if (this.filtro.getDataInicial().after(this.filtro.getDataFinal())) {
	                facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Período Inválido",
	                        "A Data Inicial não pode ser maior que a Data Final.");
	                return;
	            }
	        }
		
			List<String> tiposQueExigemDuasDatas = Arrays.asList("detalhado", "unificado", "sequenciado", 
			                                                       "sequenciadoDetalhado", "unificadoDetalhado");

			boolean exigeDuasDatas = this.tipoRelatorio != null && tiposQueExigemDuasDatas.contains(this.tipoRelatorio);

			if (exigeDuasDatas) {
			    if (this.filtro.getDataInicial() == null || this.filtro.getDataFinal() == null) {
			        facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Período Incompleto",
			                "Para filtrar por data, é necessário preencher tanto a Data Inicial quanto a Data Final.");
			        return;
			    }
			}

			
			boolean multiplaUnidades = this.filtroUnidade && this.unidadesSelecionadas != null && !this.unidadesSelecionadas.isEmpty();
			
			if (this.filtroUnidade && this.unidadesSelecionadas != null && !this.unidadesSelecionadas.isEmpty()) {
			    this.filtro.setUnidadesIds(new ArrayList<>(this.unidadesSelecionadas));
			} else {
				this.filtro.setUnidadesIds(null);


			}
			
			 Map<String, Object> parameters = new HashMap<>();
			    parameters.put("SCHEMA", Sigepen.SEJUDH_SCHEMA);
			    parameters.put("MULTIPLAS_UNIDADES", multiplaUnidades);
			    String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/relatorios/") + File.separator;
			    parameters.put("SUBREPORT_DIR", reportPath);
			
			if (multiplaUnidades && (this.filtro.getTipoRelatorio() == 1 || this.filtro.getTipoRelatorio() == 4
			        || this.filtro.getTipoRelatorio() == 5)) {

			    List<UnidadePrisional> unidades = this.todasUnidades.stream()
			            .filter(u -> this.unidadesSelecionadas.contains(u.getId()))
			            .collect(Collectors.toList());

			    if (unidades == null || unidades.isEmpty()) {
			        facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Nenhuma unidade encontrada.");
			        return;
			    }

			    
			    this.filtro.setUnidadesIds(new ArrayList<>(this.unidadesSelecionadas));
			    
			    List<String> nomesUnidades = unidades.stream().map(UnidadePrisional::getNome).collect(Collectors.toList());
			    parameters.put("UNIDADE_SELECIONADAS_NOMES", nomesUnidades);

			    if(this.filtro.getDescricao() != null && !this.filtro.getDescricao().isEmpty()) {
			    	parameters.put("FILTRO_DETALHADO", this.filtro.getDescricao());
			    }
			    
			    List<InfopenDTO> lista = this.service.buscarInfopenDetalhadoPorFiltro(this.filtro);

			    if (lista == null || lista.isEmpty()) {
			        facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO",
			                "Relatório não impresso, nenhum registro encontrado!");
			        return;
			    }

			    for (InfopenDTO dto : lista) {
			        if (dto.getRecuperandos() == null) {
			            dto.setRecuperandos(new ArrayList<>());
			        }
			    }
			    
			    
			    fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.InfopenDetalhadoMestre, parameters));
			    LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório Consolidado de Unidades Gerado", log);
			    RequestContext.getCurrentInstance().execute("download()");
			    return;
			}else {
				 this.filtro.setUnidadesIds(null);

			}

			
			

			long impFeminino = service.buscarFeminino(SecurityUtil.getUnidadeSelecionada().getId());
		
			parameters.put("SCHEMA", Sigepen.SEJUDH_SCHEMA);
			parameters.put("IMP_FEMININO", impFeminino);

			
			
			Relatorio relatorio = null;
			Relatorio relatorioPorData = null;
			
			if (this.filtro.getTipoRelatorio() == 0) { // Sintético
				relatorio = Relatorio.EstatisticoInfopen;
				relatorioPorData = Relatorio.EstatisticoInfopenPorData;
			}

			else if (this.filtro.getTipoRelatorio() == 1) { // Detalhado
				relatorio = Relatorio.InfopenDetalhado;
				relatorioPorData = Relatorio.InfopenDetalhadoPorData;
			}


			else if (this.filtro.getTipoRelatorio() == 2) { // EstatisticoUnificado;
				relatorio = Relatorio.EstatisticoInfopenUnificado;
				relatorioPorData = Relatorio.EstatisticoInfopenUnificadoPorData;
			}

			else if (this.filtro.getTipoRelatorio() == 3) { // EstatisticoSequenciado;
				relatorio = Relatorio.EstatisticoInfopenSequenciado;
				relatorioPorData = Relatorio.EstatisticoInfopenSequenciadoPorData;
			}

			else if (this.filtro.getTipoRelatorio() == 4) { // DetalhadoSequenciado
				relatorio = Relatorio.InfopenDetalhadoSequenciado;
				relatorioPorData = Relatorio.InfopenDetalhadoSequenciadoPorData;
			}

			else if (this.filtro.getTipoRelatorio() == 5) { // DetalhadoUnificado
				relatorio = Relatorio.InfopenDetalhadoUnificado;
				relatorioPorData = Relatorio.InfopenDetalhadoUnificadoPorData;
			}
			

			if (this.filtro.getUnidadesIds() != null && !this.filtro.getUnidadesIds().isEmpty()) {
	            parameters.put("UNIDADES_IDS", this.filtro.getUnidadesIds());
	            parameters.put("UNIDADE_ID", this.filtro.getUnidadesIds().get(0));
	        } else {
	        	Long unidadeId = this.filtro.getUnidade();
	            parameters.put("UNIDADE_ID", unidadeId);
	            parameters.put("UNIDADES_IDS", null);
	        }

		
			if (this.filtro.getTipoRelatorio() == 0 || this.filtro.getTipoRelatorio() == 2) {


				if (this.filtro.getDataInicial() != null && relatorioPorData != null) {
					parameters.put("DATA_SAIDA", formatarData(this.filtro.getDataInicial()));

					fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(relatorioPorData, parameters));
					LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório Estatístico Infopen Por Data", log);
					RequestContext.getCurrentInstance().execute("download()");
					return;
				}

			
				fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(relatorio, parameters));
				LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório Estatístico Infopen", log);
				RequestContext.getCurrentInstance().execute("download()");
				return;
			}

			

			if (this.filtro.getTipoRelatorio() == 3) {
				List<UnidadePrisional> unidades = prisionalService.listarUnidadesNaoInativas();

				if (unidades == null || unidades.isEmpty()) {
					return;
				}

				List<Arquivo> arquivosGerados = new ArrayList<>();

				for (UnidadePrisional unidade : unidades) {

					Map<String, Object> parametrosUnidade = new HashMap<>(parameters);

					Arquivo arquivo = null;

					if (this.filtro.getDataInicial() != null && relatorioPorData != null) {
						parametrosUnidade.put("UNIDADE_ID", unidade.getId());
						parametrosUnidade.put("UNIDADE_NOME", unidade.getNome());
						parametrosUnidade.put("DATA_SAIDA", formatarData(this.filtro.getDataInicial()));
						arquivo = RelatorioUtil.gerarRelatorio(relatorioPorData, parametrosUnidade);
					} else {
						parametrosUnidade.put("UNIDADE_ID", unidade.getId());
						parametrosUnidade.put("UNIDADE_NOME", unidade.getNome());
						arquivo = RelatorioUtil.gerarRelatorio(relatorio, parametrosUnidade);
					}

					if (arquivo != null) {
						String nomeArquivo = "relatorio_" + unidade.getId() + ".pdf";
						Path tempFile = Files.createTempFile("infopen_", ".pdf");
						arquivo.setCaminho(tempFile.toString());
						arquivo.setNome(nomeArquivo);

						try (FileOutputStream fos = new FileOutputStream(tempFile.toFile())) {
							fos.write(arquivo.getBytes());
							LoggerUtil.logInfo(Acao.IMPRIMIR, "Arquivo criado e conteúdo salvo: " + tempFile, log);

						} catch (IOException e) {
							LoggerUtil.logErro(Acao.IMPRIMIR, "Erro ao salvar conteúdo no arquivo: " + tempFile, e,
									log);
							throw new RuntimeException("Erro ao salvar conteúdo no arquivo: " + tempFile);
						}

						arquivosGerados.add(arquivo);
					}
				}

				File zipFile = zipArquivos(arquivosGerados);
				fileDownload.prepararArquivo22(zipFile);
				RequestContext.getCurrentInstance().execute("download()");
			}

			
			if (this.filtro.getTipoRelatorio() == 1 || this.filtro.getTipoRelatorio() == 5) {

				if (this.filtro.getDataInicial() != null && relatorioPorData != null) {
					 parameters.put("DATA_PESQUISA",
					            formatarData(filtro.getDataInicial()) + " a " + formatarData(filtro.getDataFinal()));

					if (this.filtro.getDescricao() != null && !this.filtro.getDescricao().isEmpty()) {
						parameters.put("FILTRO_DETALHADO", this.filtro.getDescricao());
					}
						
					List<InfopenDTO> lista = this.service.buscarInfopenDetalhadoPorFiltro(this.filtro);
					

					parameters.put("DADOS_AGRUPADOS_POR_UNIDADE", lista);

					if (lista == null || lista.isEmpty()) {
						facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO",
								"Relatório não impresso, nenhum registro encontrado!");
						return;
					}

					fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, relatorioPorData, parameters));
					LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório de Recuperandos Infopen Detalhado Por Data", log);
					RequestContext.getCurrentInstance().execute("download()");
					return;
				}else {
					parameters.put("DATA_PESQUISA", "");	      
				}

				
				if (this.filtro.getDescricao() != null && !this.filtro.getDescricao().isEmpty()) {
					parameters.put("FILTRO_DETALHADO", this.filtro.getDescricao());
				}

				List<InfopenDTO> lista = this.service.buscarInfopenDetalhadoPorFiltro(this.filtro);

				if (lista == null || lista.isEmpty()) {
					facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO",
							"Relatório não impresso, nenhum registro encontrado!");
					return;
				}

				fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, relatorio, parameters));
				LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório de Recuperandos Infopen Detalhado " + relatorio, log);
				RequestContext.getCurrentInstance().execute("download()");
				return;
			}


			if (this.filtro.getTipoRelatorio() == 4) {
				List<UnidadePrisional> unidades = prisionalService.listarUnidadesNaoInativas();

				if (this.filtroUnidade && this.unidadesSelecionadas != null && !this.unidadesSelecionadas.isEmpty()) {
					unidades = this.todasUnidades.stream().filter(u -> this.unidadesSelecionadas.contains(u.getId()))
							.collect(Collectors.toList());
				}

				if (unidades == null || unidades.isEmpty()) {
					facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO",
							"Nenhuma unidade encontrada para geração de relatórios!");
					return;
				}

				List<Arquivo> arquivosGerados = new ArrayList<>();

				for (UnidadePrisional unidade : unidades) {
					filtro.setUnidade(unidade.getId());
					List<InfopenDTO> lista = this.service.buscarInfopenDetalhadoPorFiltro(this.filtro);

					if (lista.isEmpty() || lista.get(0).getRecuperandos().isEmpty()) {
						facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO",
								"Relatório não impresso, nenhum registro encontrado para a unidade " + unidade.getNome());
						return;
					}

					Map<String, Object> parametrosUnidade = new HashMap<>(parameters);

					Arquivo arquivo = null;

					if (this.filtro.getDataInicial() != null && this.filtro.getDataFinal() != null) {
						parametrosUnidade.put("DATA_PESQUISA", formatarData(this.filtro.getDataInicial()));
						parametrosUnidade.put("UNIDADE_ID", unidade.getId());
						parametrosUnidade.put("UNIDADE_NOME", unidade.getNome());

						if (this.filtro.getDescricao() != null && !this.filtro.getDescricao().isEmpty()) {
							parameters.put("FILTRO_DETALHADO", this.filtro.getDescricao());
						}
						arquivo = RelatorioUtil.gerarRelatorio(lista, relatorioPorData, parametrosUnidade);

					} else {
						parametrosUnidade.put("UNIDADE_ID", unidade.getId());
						parametrosUnidade.put("UNIDADE_NOME", unidade.getNome());

						if (this.filtro.getDescricao() != null && !this.filtro.getDescricao().isEmpty()) {
							parameters.put("FILTRO_DETALHADO", this.filtro.getDescricao());
						}

						arquivo = RelatorioUtil.gerarRelatorio(lista, relatorio, parametrosUnidade);
					}

					if (arquivo != null) {
						String nomeArquivo = "relatorio_" + unidade.getId() + ".pdf";
						Path tempFile = Files.createTempFile("infopen_", ".pdf");
						arquivo.setCaminho(tempFile.toString());
						arquivo.setNome(nomeArquivo);

						try (FileOutputStream fos = new FileOutputStream(tempFile.toFile())) {
							fos.write(arquivo.getBytes());
							LoggerUtil.logInfo(Acao.IMPRIMIR, "Arquivo criado e conteúdo salvo: " + tempFile, log);

						} catch (IOException e) {
							LoggerUtil.logErro(Acao.IMPRIMIR, "Erro ao salvar conteúdo no arquivo: " + tempFile, e,
									log);
							throw new RuntimeException("Erro ao salvar conteúdo no arquivo: " + tempFile);
						}

						arquivosGerados.add(arquivo);
					}
				}

				File zipFile = zipArquivos(arquivosGerados);
				fileDownload.prepararArquivo22(zipFile);
				RequestContext.getCurrentInstance().execute("download()");
			}

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao imprimir Relatório: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Erro ao gerar relatório", e, log);
		}
	}

	private void criarDiretorio() {
		File diretorio = new File("C:\\infopen");
		if (!diretorio.exists()) {
			diretorio.mkdirs();
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Diretório criado: " + diretorio.getAbsolutePath(), log);
		}
	}

	
	private File zipArquivos(List<Arquivo> arquivos) throws IOException {
		File zipFile = new File("relatorios.zip");
		try (FileOutputStream fos = new FileOutputStream(zipFile); ZipOutputStream zos = new ZipOutputStream(fos)) {

			for (Arquivo arquivo : arquivos) {
				
				String nomeArquivoZip = arquivo.getNome();
				String nomeUnico = nomeArquivoZip.replace(".pdf", "_" + System.currentTimeMillis() + ".pdf");

				try (FileInputStream fis = new FileInputStream(new File(arquivo.getCaminho()))) {
					ZipEntry zipEntry = new ZipEntry(nomeUnico);
					zos.putNextEntry(zipEntry);

					byte[] bytes = new byte[1024];
					int length;
					while ((length = fis.read(bytes)) >= 0) {
						zos.write(bytes, 0, length);
					}
					zos.closeEntry();
				}
			}
		}
		return zipFile;
	}
	
	public void onMudarTipoRelatorio() {
	    this.tipoRelatorio = null;
	    this.filtro.setDataInicial(null);
	    this.filtro.setDataFinal(null);

	    RequestContext.getCurrentInstance().update("meuFormulario:panel_filtros");
	    log.info("Tipo de relatório alterado - filtros resetados");
	}

	
	public void onMudarFiltroData() {
	    if (this.tipoRelatorio == null || this.tipoRelatorio.isEmpty()) {
	        this.filtro.setDataInicial(null);
	        this.filtro.setDataFinal(null);
	        log.info("Filtro por data desativado - datas limpas");
	    }
	    
	    RequestContext.getCurrentInstance().update("meuFormulario:panel_filtros");
	}


	private String formatarData(Date data) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(data);
	}



	public String getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(String tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public Etnia[] getEtnia() {
		return etnia;
	}

	public void setEtnia(Etnia[] etnia) {
		this.etnia = etnia;
	}

	public TipoDeficienciaRelatorio[] getTiposDeficiencia() {
		return TipoDeficienciaRelatorio.values();
	}
	
	public Date getDataMaxima() {
	    return new Date();
	}

}
