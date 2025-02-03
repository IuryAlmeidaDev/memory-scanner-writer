package br.gov.mt.sejudh.sigepen.bean.relatorios;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
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
import br.gov.mt.sejudh.sigepen.model.Escolaridade;
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
        @URLMapping(id = "Infopen", pattern = "/relatorio/recuperandos/infopen", viewId = "/WEB-INF/pages/relatorio/Infopen.xhtml"),
})
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
	
	private List<UnidadePrisional> unidades = new ArrayList<UnidadePrisional>();
	
	
	private String tipoRelatorio;
    private String periodo;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private FileDownloadHandlerBean fileDownload;

	@Inject
	private Logger log;
	
	@URLAction(onPostback=false)
	public String init() {		
		this.filtro.setUnidade(SecurityUtil.getUnidadeSelecionada().getId());
		this.nacionalidades = this.service.buscarNacionalidades(SecurityUtil.getUnidadeSelecionada().getId());
		this.regimes = this.service.buscarRegimes(SecurityUtil.getUnidadeSelecionada().getId());
		this.estadoCivil = this.service.buscarEstadoCivil(SecurityUtil.getUnidadeSelecionada().getId());
		this.religiao = this.service.buscarReligiao(SecurityUtil.getUnidadeSelecionada().getId());
		this.escolaridade = this.service.buscarEscolaridade(SecurityUtil.getUnidadeSelecionada().getId());
		this.raca = this.service.buscarRaca(SecurityUtil.getUnidadeSelecionada().getId());
		this.etnia = this.service.buscarEtnia(SecurityUtil.getUnidadeSelecionada().getId());
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
		
	
	public void imprimir() {
	    try {
	    		        
	    	long impFeminino = service.buscarFeminino(SecurityUtil.getUnidadeSelecionada().getId());
            Map<String, Object> parameters = new HashMap<>();
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
	        
	        
	        
	        /* -------------------------------------- Estatisco ---------------------------------------------------- */
	        
	        
    	    // Infopen EstatiscoPorData ou EstatiscoUnificadoPorData
	        if(this.filtro.getTipoRelatorio() == 0 || this.filtro.getTipoRelatorio() == 2) {
	        	
	        	parameters.put("UNIDADE_ID", SecurityUtil.getUnidadeSelecionada().getId());
	        	
	        	if(this.filtro.getDataInicial() != null && relatorioPorData != null) {	        			        	    
	        		 parameters.put("DATA_SAIDA", formatarData(this.filtro.getDataInicial()));		        							
	        		
	        		fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(relatorioPorData, parameters));
	        		LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório Estatístico Infopen Por Data", log);
	        		RequestContext.getCurrentInstance().execute("download()");
	        		return;	
	        	}
	        	
	        	// Infopen Estatisco
	        	fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(relatorio, parameters));
	        	LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório Estatístico Infopen", log);
	        	RequestContext.getCurrentInstance().execute("download()");
	        	return;	
	        }
	        
	        
	        
	        /* Estatistico Sequenciado e SequenciadoPorData */
	        
	        //novo para testar
	        if (this.filtro.getTipoRelatorio() == 3) {
                List<UnidadePrisional> unidades = prisionalService.listarUnidadesNaoInativas();

                if (unidades == null || unidades.isEmpty()) {
                    return;
                }

                List<Arquivo> arquivosGerados = new ArrayList<>();
                criarDiretorio();

                for (UnidadePrisional unidade : unidades) {
                    Arquivo arquivo = null;

                    if (this.filtro.getDataInicial() != null && relatorioPorData != null) {
                        parameters.put("UNIDADE_ID", unidade.getId());
                        parameters.put("DATA_SAIDA", formatarData(this.filtro.getDataInicial()));

                        arquivo = RelatorioUtil.gerarRelatorio(relatorioPorData, parameters);
                    } else {
                        parameters.put("UNIDADE_ID", unidade.getId());
                        arquivo = RelatorioUtil.gerarRelatorio(relatorio, parameters);
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
                            LoggerUtil.logErro(Acao.IMPRIMIR, "Erro ao salvar conteúdo no arquivo: " + tempFile, e, log);
                            throw new RuntimeException("Erro ao salvar conteúdo no arquivo: " + tempFile);
                        }

                        arquivosGerados.add(arquivo);
                    }
                }

                File zipFile = zipArquivos(arquivosGerados);
                fileDownload.prepararArquivo22(zipFile);
                RequestContext.getCurrentInstance().execute("download()");
            }
	        
	        
	        
	        //antigo
	        if (this.filtro.getTipoRelatorio() == 3) {	            
	            List<UnidadePrisional> unidades = prisionalService.listarUnidadesNaoInativas();
	            
	            if (unidades == null || unidades.isEmpty()) { 
	                return; 
	            }
	            
	            List<Arquivo> arquivosGerados = new ArrayList<>();	            
	            criarDiretorio();
	            
	            for (UnidadePrisional unidade : unidades) {
	            	
	                Arquivo arquivo = null;	                
	                if (this.filtro.getDataInicial() != null && relatorioPorData != null) {
	                    parameters.put("UNIDADE_ID", unidade.getId());		        	    
	                    parameters.put("DATA_SAIDA", formatarData(this.filtro.getDataInicial()));	
	                    
	                    arquivo = RelatorioUtil.gerarRelatorio(relatorioPorData, parameters);
	                    
	                } else {
	                    parameters.put("UNIDADE_ID", unidade.getId());
	                    arquivo = RelatorioUtil.gerarRelatorio(relatorio, parameters);
	                }
	                
	                if (arquivo != null) {
	                    String nomeArquivo = "relatorio_" + unidade.getId() + ".pdf";
	                    String caminhoArquivo = "C:\\infopen\\" + nomeArquivo;
	                    arquivo.setCaminho(caminhoArquivo);
	                    arquivo.setNome(nomeArquivo);
	                    
	                    try (FileOutputStream fos = new FileOutputStream(new File(caminhoArquivo))) {
	                        fos.write(arquivo.getBytes()); // Usando setBytes para obter o conteúdo
	                        LoggerUtil.logInfo(Acao.IMPRIMIR, "Arquivo criado e conteúdo salvo: " + caminhoArquivo, log);
	                        
	                    } catch (IOException e) {
	                        LoggerUtil.logErro(Acao.IMPRIMIR, "Erro ao salvar conteúdo no arquivo: " + caminhoArquivo, e, log);
	                        throw new RuntimeException("Erro ao salvar conteúdo no arquivo: " + caminhoArquivo);
	                    }
	                    
	                    arquivosGerados.add(arquivo);
	                }
	            }
	            
	            File zipFile = zipArquivos(arquivosGerados);
	            fileDownload.prepararArquivo2(zipFile);
	            RequestContext.getCurrentInstance().execute("download()");
	        }


	        
	        
	        /* -------------------------------------- Detalhado ---------------------------------------------------- */
	        
	        
     	     // Infopen DetalhadoPorData ou DetalhadoUnificadoPorData
	        if (this.filtro.getTipoRelatorio() == 1 || this.filtro.getTipoRelatorio() == 5) {
	        	
	        	parameters.put("UNIDADE_ID", SecurityUtil.getUnidadeSelecionada().getId());
	        	
	        	
	        	if (this.filtro.getDataInicial() != null && relatorioPorData != null) {	        		
	        		
	        	    parameters.put("DATA_PESQUISA", formatarData(this.filtro.getDataInicial()));
	        	    
	        	    if (this.filtro.getDescricao() != null && !this.filtro.getDescricao().isEmpty()) {
	                    parameters.put("FILTRO_DETALHADO", this.filtro.getDescricao());
	                }
	        							
		        	List<InfopenDTO> lista = this.service.buscarInfopenDetalhadoPorFiltro(this.filtro);
		        	
						if (lista.isEmpty() || lista.get(0).getRecuperandos().isEmpty()) {
							facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
							return;
						}
		        		        		        	             		        	
					fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, relatorioPorData, parameters));					
		        	LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório de Recuperandos Infopen Detalhado Por Data", log);
		        	RequestContext.getCurrentInstance().execute("download()");
		        	return;		        		
	        	}
				
				//Infopen Detalhado ou DetalhadoUnificado	        	
	        	if (this.filtro.getDescricao() != null && !this.filtro.getDescricao().isEmpty()) {
                    parameters.put("FILTRO_DETALHADO", this.filtro.getDescricao());
                }
						        				
				List<InfopenDTO> lista = this.service.buscarInfopenDetalhadoPorFiltro(this.filtro);
		        	
		        	if (lista.isEmpty() || lista.get(0).getRecuperandos().isEmpty()) {
		        		facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
		        	    return;
		        	}

	        	fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, relatorio, parameters)); 
	        	LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório de Recuperandos Infopen Detalhado " + relatorio, log); 
	        	RequestContext.getCurrentInstance().execute("download()");    				        		        	
				return;		        		
	        }
	        
	        
	        /* DetalhadoSequenciado e DetalhadoSequenciadoPorData */	        
	        if(this.filtro.getTipoRelatorio() == 4) {	        	
	        	List<UnidadePrisional> unidades = prisionalService.listarUnidadesNaoInativas();
	        	
	        	if(unidades == null || unidades.isEmpty()) {
	        		facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Nenhuma unidade encontrada para geração de relatórios!");
	        		return;
	        	}
	        	
	        	List<Arquivo> arquivosGerados = new ArrayList < > ();	        	
	        	criarDiretorio();
	        	
	        	for(UnidadePrisional unidade: unidades) {	        		
	        		List <InfopenDTO> lista = this.service.buscarInfopenDetalhadoPorFiltro(this.filtro);
	        		
	        		if(lista.isEmpty() || lista.get(0).getRecuperandos().isEmpty()) {
	        			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado para a unidade " + unidade.getNome() + "!");
	        			return;
	        		}
	        		
	        		Arquivo arquivo = null;
	        		if(this.filtro.getDataInicial() != null && this.filtro.getDataFinal() != null) {	        			
		        	    parameters.put("DATA_PESQUISA", formatarData(this.filtro.getDataInicial()));
	        			parameters.put("UNIDADE_ID", unidade.getId());
	        			
	        			if (this.filtro.getDescricao() != null && !this.filtro.getDescricao().isEmpty()) {
	                        parameters.put("FILTRO_DETALHADO", this.filtro.getDescricao());
	                    }
	        			arquivo = RelatorioUtil.gerarRelatorio(lista, relatorioPorData, parameters);
	        			
	        		} else {
	        			parameters.put("UNIDADE_ID", unidade.getId());
	        			
	        			if (this.filtro.getDescricao() != null && !this.filtro.getDescricao().isEmpty()) {
	                        parameters.put("FILTRO_DETALHADO", this.filtro.getDescricao());
	                    }
	        			
	        			arquivo = RelatorioUtil.gerarRelatorio(lista, relatorio, parameters);
	        		}
	        		
	        		if(arquivo != null) {	        			
	        			String nomeArquivo = "relatorio_" + unidade.getId() + ".pdf";
	        			String caminhoArquivo = "C:\\infopen\\" + nomeArquivo;
	        			
	        			arquivo.setCaminho(caminhoArquivo);
	        			arquivo.setNome(nomeArquivo);
	        			
	        			try (FileOutputStream fos = new FileOutputStream(new File(caminhoArquivo))) {
	        				fos.write(arquivo.getBytes());
	        				LoggerUtil.logInfo(Acao.IMPRIMIR, "Arquivo criado e conteúdo salvo: " + caminhoArquivo, log);
	        				
	        			} catch (IOException e) {
	        				LoggerUtil.logErro(Acao.IMPRIMIR, "Erro ao salvar conteúdo no arquivo: " + caminhoArquivo, e, log);
	        				throw new RuntimeException("Erro ao salvar conteúdo no arquivo: " + caminhoArquivo);
	        			}
	        			arquivosGerados.add(arquivo);
	        		}
	        	}
	        	
	        	File zipFile = zipArquivos(arquivosGerados);
	        	fileDownload.prepararArquivo2(zipFile);
	        	RequestContext.getCurrentInstance().execute("download()");
	        }

	  	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao imprimir Relatório: " + e.getMessage());
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

	
	// Método zipArquivos atualizado para evitar duplicidade
	private File zipArquivos(List<Arquivo> arquivos) throws IOException {
	    File zipFile = new File("relatorios.zip");
	    try (FileOutputStream fos = new FileOutputStream(zipFile);
	         ZipOutputStream zos = new ZipOutputStream(fos)) {
	        
	        for (Arquivo arquivo : arquivos) {
	            // Garante nome único ao adicionar no ZIP
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
	
	
	
	private String formatarData(Date data) {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    return sdf.format(data);
	}
	
	
	
	//CRIAR METODO PRIVADO PARA PASSAR O FILTRO
	
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
    
}
