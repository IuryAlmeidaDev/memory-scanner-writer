package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.PlantaoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRelatorioEntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEquipe;
import br.gov.mt.sejudh.sigepen.enumerator.TipoRelatorioEntradaSaidaRec;
import br.gov.mt.sejudh.sigepen.model.*;
import br.gov.mt.sejudh.sigepen.model.dto.*;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.TextUtil;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class PlantaoService extends BaseService<Plantao, Long> {
	private static final long serialVersionUID = 1L;
    private static final String EMPTY_PLACEHOLDER = "---";

	private PlantaoDAO dao;

    @Inject
    public void setPlantaoDAO(PlantaoDAO dao) {
        this.dao = dao;
        super.setDao(dao);
    }

	@Inject
    private OcorrenciaService ocorrenciaService;

    @Inject
    private EstatisticaService estatisticaService;

    @Inject
    private RelatorioEntradaSaidaRecuperandoService relatorioEntradaSaidaRecuperandoService;

    @Inject
    private PlantaoVisitaEstruturaService plantaoVisitaEstruturaService;

    public boolean isPlantaoDaUnidadeLogada(Plantao plantao) {
        UnidadePrisional unidadeLogada = SecurityUtil.getUnidadeSelecionada();
        return plantao.getUnidadePrisional().equals(unidadeLogada);
    }

    public boolean isDataEncerramentoIgualDataAbertura(Plantao plantao) {
        Calendar dataPlantao = Calendar.getInstance();
        dataPlantao.setTime(plantao.getDataPlantao());

        Calendar hoje = Calendar.getInstance();

        return dataPlantao.get(Calendar.YEAR) == hoje.get(Calendar.YEAR)
                && dataPlantao.get(Calendar.DAY_OF_YEAR) == hoje.get(Calendar.DAY_OF_YEAR);
    }

    public boolean isExigeJustificativaParaEncerramento(Plantao plantao) {
        Calendar limiteJustificativa = Calendar.getInstance();
        limiteJustificativa.setTime(plantao.getDataPlantao());
        limiteJustificativa.add(Calendar.DAY_OF_MONTH, 1);
        limiteJustificativa.set(Calendar.HOUR_OF_DAY, 8);
        limiteJustificativa.set(Calendar.MINUTE, 0);
        limiteJustificativa.set(Calendar.SECOND, 0);
        limiteJustificativa.set(Calendar.MILLISECOND, 0);

        Date agora = new Date();
        return agora.after(limiteJustificativa.getTime());
    }

	public boolean existePlantaoNaDataPorUnidade(UnidadePrisional unidade) {
		return dao.existePlantaoNaDataPorUnidade(unidade);
	}

	public Plantao obterPlantaoEmAndamento(UnidadePrisional unidade) {
		return dao.obterPlantaoEmAndamento(unidade);
	}

	public Plantao obterPlantaoAnterior(Plantao plantaoAtual, UnidadePrisional unidade) {
		return dao.obterPlantaoAnterior(plantaoAtual, unidade);
	}

	public Plantao buscarPlantaoMaisAntigoEmAbertoPorUnidade(UnidadePrisional unidade) {
		return dao.buscarPlantaoMaisAntigoEmAbertoPorUnidade(unidade);
	}

	public Plantao buscarUltimoPlantaoEmAberto() {
		return dao.buscarUltimoPlantaoEmAberto();
	}

	public Plantao buscarUltimoPlantao() {
		return dao.buscarUltimoPlantao();
	}

	@Override
	public Plantao buscarPorId(Long id) {
		Plantao plantao = dao.buscarPorId(id);
        if (plantao == null) {
            return null;
        }
		plantao = dao.buscarAutorRegistro(plantao);

        try {
            // Equipes e seus servidores
            dao.initialize(plantao.getEquipes());
            if (plantao.getEquipes() != null) {
                for (Equipe e : plantao.getEquipes()) {
                    if (e != null) {
                        dao.initialize(e.getPlantaoServidor());
                        // Garantir que funcao e cargo do Servidor estejam inicializados para evitar LazyInitializationException
                        for (PlantaoServidor ps : e.getPlantaoServidor()) {
                            if (ps != null && ps.getServidor() != null) {
                                try {
                                    dao.initialize(ps.getServidor().getFuncao());
                                } catch (Exception ignore) {
                                }
                                try {
                                    dao.initialize(ps.getServidor().getCargo());
                                } catch (Exception ignore) {
                                }
                            }
                        }
                    }
                }
            }

            // Ocorrências do plantão
            dao.initialize(plantao.getOcorrencias());

            // Visitas do plantão e suas estruturas
            dao.initialize(plantao.getPlantaoVisitas());
            if (plantao.getPlantaoVisitas() != null) {
                for (PlantaoVisita pv : plantao.getPlantaoVisitas()) {
                    if (pv != null) {
                        dao.initialize(pv.getPlantaoVisitaEstruturas());
                    }
                }
            }
        } catch (Exception ignore) {
        }

		return plantao;
	}

    public Arquivo gerarRelatorioRegistroPlantao(Long plantaoId) throws Exception {
        Plantao plantao = validarEObterPlantao(plantaoId);

        Map<String, Object> parametros = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");

        preencherParametrosBasicos(plantao, parametros, sdf);
        preencherDadosAbertura(plantao, parametros);
        preencherEstatisticas(plantao, parametros);
        preencherDadosEncerramento(plantao, parametros, sdf);

        List<PlantaoServidorDTO> dataSourcePrincipal = montarDataSourcePrincipal(plantao);
        parametros.put("VISITAS", montarVisitasDataSource(plantao));
        parametros.put("OCORRENCIAS", montarOcorrenciasDataSource(plantao));

        List<RelatorioEntradaSaidaRecuperandoDTO> entradaSaida = montarEntradaSaidaPlantao(plantao);
        parametros.put("ENTRADA/SAIDA", new JRBeanCollectionDataSource(!entradaSaida.isEmpty() ? entradaSaida : Collections.singletonList(new RelatorioEntradaSaidaRecuperandoDTO())));

        return RelatorioUtil.gerarRelatorio(dataSourcePrincipal, Relatorio.RegistroPlantao, parametros);
    }

    private List<RelatorioEntradaSaidaRecuperandoDTO> montarEntradaSaidaPlantao(Plantao plantao) {
        FiltroRelatorioEntradaSaidaRecuperando filtro = new FiltroRelatorioEntradaSaidaRecuperando();
        filtro.setUnidade(plantao.getUnidadePrisional().getId());
        Date inicio = plantao.getDataPlantao();
        Date fim = plantao.getDataPlantaoEncerramento() != null
                ? plantao.getDataPlantaoEncerramento()
                : new Date(inicio.getTime() + 24L * 60L * 60L * 1000L);
        filtro.setDataInicial(inicio);
        filtro.setDataFinal(fim);
        filtro.setTipoRelatorioEntradaSaidaRec(TipoRelatorioEntradaSaidaRec.ENTRADA_SAIDA);

        return relatorioEntradaSaidaRecuperandoService.buscarRelatorioEntradaSaidaPorFiltro(filtro);
    }

    private Plantao validarEObterPlantao(Long plantaoId) {
        if (plantaoId == null) {
            throw new IllegalArgumentException("ID do plantão é obrigatório");
        }
        Plantao plantao = buscarPorId(plantaoId);
        if (plantao == null) {
            throw new IllegalStateException("Plantão não encontrado");
        }
        return plantao;
    }

    private void preencherParametrosBasicos(Plantao plantao, Map<String, Object> parametros, SimpleDateFormat sdf) {
        String nomeRelatorio = "Livro de Plantão - "
                + new SimpleDateFormat("dd 'de' MMMM yyyy", new java.util.Locale("pt", "BR"))
                .format(plantao.getDataPlantao())
                + " Plantão " + plantao.getPlantao().getDescricao();

        parametros.put("NOME_DO_RELATORIO", nomeRelatorio);
        parametros.put("DATA_HORA_INICIAL", sdf.format(plantao.getDataPlantao()));
    }

    private void preencherDadosAbertura(Plantao plantao, Map<String, Object> parametros) {
        Usuario usuarioInicio = plantao.getUsuarioInicioPlantao();
        Servidor servidorInicio = usuarioInicio != null ? usuarioInicio.getServidor() : null;

        if (servidorInicio != null) {
            parametros.put("NOME_SERVIDOR_ABERTURA", TextUtil.toTitleCase(servidorInicio.getNome()));
            parametros.put("MATRICULA_SERVIDOR_ABERTURA", servidorInicio.getMatricula());
        } else {
            parametros.put("NOME_SERVIDOR_ABERTURA", "Não informado");
            parametros.put("MATRICULA_SERVIDOR_ABERTURA", null);
        }
    }

    private void preencherEstatisticas(Plantao plantao, Map<String, Object> parametros) {
        Date inicio = plantao.getDataPlantao();
        Date fim = plantao.getDataPlantaoEncerramento() != null ? plantao.getDataPlantaoEncerramento() : new Date();
        UnidadePrisional unidade = plantao.getUnidadePrisional();

        parametros.put("TOTAL_RECUPERANDOS", estatisticaService.getTotalRecuperandosNaUnidade(unidade, inicio));
        parametros.put("TOTAL_ENTRADAS", estatisticaService.getTotalEntradasNaUnidade(unidade, inicio, fim));
        parametros.put("TOTAL_SAIDAS", estatisticaService.getTotalSaidasNaUnidade(unidade, inicio, fim));
        parametros.put("TOTAL_MOV_INTERNA", estatisticaService.getTotalMovimentacoesInternas(unidade, inicio, fim));
        parametros.put("TOTAL_MOV_EXTERNA", estatisticaService.getTotalMovimentacoesExternas(unidade, inicio, fim));
    }

    private void preencherDadosEncerramento(Plantao plantao, Map<String, Object> parametros, SimpleDateFormat sdf) {
        Date fim = plantao.getDataPlantaoEncerramento();
        if (fim != null) {
            parametros.put("DATA_HORA_ENCERRAMENTO", sdf.format(fim));

            Usuario usuarioFim = plantao.getUsuarioFimPlantao();
            Servidor servidorFim = usuarioFim != null ? usuarioFim.getServidor() : null;

            if (servidorFim != null) {
                parametros.put("NOME_SERVIDOR_ENCERRAMENTO", TextUtil.toTitleCase(servidorFim.getNome()));
                parametros.put("MATRICULA_SERVIDOR_ENCERRAMENTO", servidorFim.getMatricula());
            }
        } else {
            parametros.put("DATA_HORA_ENCERRAMENTO", "Plantão em aberto");
        }
    }

    private List<PlantaoServidorDTO> montarDataSourcePrincipal(Plantao plantao) {
        List<PlantaoServidorDTO> saida = new ArrayList<>();

        for (TipoEquipe tipo : TipoEquipe.values()) {
            String nomeEquipe = tipo.getDescricao();

            // Encontrar equipe do tipo no plantão
            List<PlantaoServidor> servidores = getPlantaoServidoresForTipo(plantao, tipo);

            // Comunicados: ocorrência presente e situação != "Presente"
            List<ComunicadoDTO> comunicados;
            if (servidores.isEmpty()) {
                comunicados = Collections.singletonList(ComunicadoDTO.criar(EMPTY_PLACEHOLDER, EMPTY_PLACEHOLDER, EMPTY_PLACEHOLDER, EMPTY_PLACEHOLDER));
            } else {
                comunicados = servidores.stream()
                        .filter(ps -> ps.getOcorrencia() != null && !ps.getOcorrencia().isEmpty() && ps.getSituacao() != null && !"Presente".equalsIgnoreCase(ps.getSituacao()))
                        .map(ComunicadoDTO::criarComunicado)
                        .collect(Collectors.toList());
                if (comunicados.isEmpty()) {
                    comunicados = Collections.singletonList(ComunicadoDTO.criar(EMPTY_PLACEHOLDER, EMPTY_PLACEHOLDER, EMPTY_PLACEHOLDER, EMPTY_PLACEHOLDER));
                }
            }

            if (servidores.isEmpty()) {
                // Placeholder de linha quando não há servidores neste tipo
                saida.add(PlantaoServidorDTO.criar(
                        nomeEquipe,
                        comunicados,
                        EMPTY_PLACEHOLDER,
                        EMPTY_PLACEHOLDER,
                        EMPTY_PLACEHOLDER,
                        EMPTY_PLACEHOLDER
                ));
            } else {
                for (PlantaoServidor ps : servidores) {
                    String nome = ps.getServidor() != null ? ps.getServidor().getNome() : EMPTY_PLACEHOLDER;
                    saida.add(PlantaoServidorDTO.criar(
                            nomeEquipe,
                            comunicados,
                            nome,
                            ps.getAtividade(),
                            ps.getSituacao(),
                            ps.getOcorrencia()
                    ));
                }
            }
        }

        return saida;
    }

    private static List<PlantaoServidor> getPlantaoServidoresForTipo(Plantao plantao, TipoEquipe tipo) {
        Equipe equipeDoTipo = null;
        if (plantao.getEquipes() != null) {
            for (Equipe e : plantao.getEquipes()) {
                if (e != null && tipo.equals(e.getTipoEquipe())) {
                    equipeDoTipo = e;
                    break;
                }
            }
        }

        List<PlantaoServidor> servidores = (equipeDoTipo != null && equipeDoTipo.getPlantaoServidor() != null)
                ? equipeDoTipo.getPlantaoServidor()
                : Collections.emptyList();
        return servidores;
    }

    private JRBeanCollectionDataSource montarVisitasDataSource(Plantao plantao) {
        List<VisitaDetalheDTO> visitasDataSource = new ArrayList<>();

        for (TipoEntrada te : TipoEntrada.values()) {
            List<PlantaoVisitaEstrutura> estruturas = plantaoVisitaEstruturaService
                    .buscarPlantaoVisitaEstruturaPorUnidadeAndtipoEntrada(te.getId(), plantao);
            if (estruturas == null || estruturas.isEmpty()) {
                visitasDataSource.add(VisitaDetalheDTO.criarPlaceholder(te.getDescricao(), EMPTY_PLACEHOLDER));
            } else {
                for (PlantaoVisitaEstrutura pve : estruturas) {
                    visitasDataSource.add(VisitaDetalheDTO.criarVisitaDetalheDTO(te.toString(), pve));
                }
            }
        }

        return new JRBeanCollectionDataSource(visitasDataSource);
    }

    private JRBeanCollectionDataSource montarOcorrenciasDataSource(Plantao plantao) {
        return new JRBeanCollectionDataSource(
                ocorrenciaService.buscarOcorrenciasPorPlantao(plantao.getId())
                        .stream()
                        .map(OcorrenciaRelatorioRegistroPlantaoDTO::from)
                        .collect(Collectors.collectingAndThen(
                                Collectors.toList(),
                                ocorrencias -> ocorrencias.isEmpty()
                                        ? Collections.singletonList(OcorrenciaRelatorioRegistroPlantaoDTO.criar(
                                        EMPTY_PLACEHOLDER, EMPTY_PLACEHOLDER, EMPTY_PLACEHOLDER, null,
                                        EMPTY_PLACEHOLDER, EMPTY_PLACEHOLDER))
                                        : ocorrencias
                        ))
        );
    }
}
