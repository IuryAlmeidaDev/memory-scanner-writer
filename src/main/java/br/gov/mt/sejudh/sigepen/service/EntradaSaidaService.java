package br.gov.mt.sejudh.sigepen.service;

import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.EntradaSaidaDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaSaida;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.*;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.*;
import br.gov.mt.sejudh.sigepen.model.dto.EntradaSaidaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RecuperandoVisitanteDTO;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Stateless
public class EntradaSaidaService {

	@Inject
	private EntradaSaidaDAO dao;
	
	@Inject
	private AuditoriaDAO auditDao;

	@Inject
	private TipoVisitaService tipoVisitaService;

	@Inject
	private PedidoService pedidoService;

	@Inject
	private FaltaDisciplinarService faltaDisciplinarService;

	@Inject
	private CredenciamentoService credenciamentoService;

	@Inject
	private EstruturaService estruturaService;

	public void buscaTodasAsCelasDaEstruturaId(Long id) {
		this.dao.buscaTodasAsCelasDaEstruturaId(id);
	}
	
	public int buscarQuantidadeFiltrada(FiltroEntradaSaida filtro) {
		return this.dao.buscarQuantidadeFiltrada(filtro);
	}

	public List<EntradaSaida> buscarEntradasPorFiltro(FiltroEntradaSaida filtro, boolean isRelatorio) {
		return this.dao.buscarEntradasPorFiltro(filtro, isRelatorio);
	}

	public List<Estrutura> buscarEstruturaDisponivel(long idUnidade, Date hora) throws ValidacaoException {
		return this.dao.buscarEstruturaDisponivel(idUnidade, hora);
	}

	public List<ParametroVisita> buscarParametrosVisita(long idUnidade, Date hora) throws ValidacaoException {
		return this.dao.buscarParametrosVisita(idUnidade, hora);
	}

	public Visitante buscarVisitantePorPessoa(FiltroEntradaSaida filtro) {
		return this.dao.buscarVisitantePorPessoa(filtro);
	}
	
	public EntradaSaidaDTO salvar(EntradaSaidaDTO entrada) throws Exception {
		EntradaSaida e = dao.buscarPorId(entrada.getId());
		
		if (!e.getSituacao().equals(SituacaoVisita.EM_ANDAMENTO)) 
			throw new ValidacaoException("Esta visita não está mais em andamento.");
		
		e.setDataSaida(entrada.getDataSaida());
		e.setSituacao(entrada.getSituacao());
		
		salvar(e);
		
		return entrada;
	}

	public EntradaSaida salvar(EntradaSaida entrada) {
		return this.dao.salvar(entrada);
	}

	public Long buscarQuantidadeEmAndamento(FiltroEntradaSaida f) {
		return this.dao.buscarQuantidadeEmAndamento(f);
	}

	public Long buscarQuantidadeFinalizada(FiltroEntradaSaida f) {
		return this.dao.buscarQuantidadeFinalizada(f);
	}
	
	public Long buscarEntradaResponsavel(FiltroEntradaSaida f) {
		return this.dao.buscarEntradaResponsavel(f);
	}
	
	public void excluir(EntradaSaida entrada) {
		this.dao.excluir(entrada);
	}
	
	public EntradaSaida buscarPorId(Long id) {
		return this.dao.buscarPorId(id);
	}

	public Visitante buscarBiometriaVisitante(List<Long> ids) {
		return this.dao.buscarBiometriaVisitante(ids);
	}

	public Visitante buscarPessoaVisitante(List<Long> ids) {
		return this.dao.buscarPessoaVisitante(ids);
	}
	
	public EntradaSaida buscarAuditoria(EntradaSaida entrada) {
		if (entrada != null) {
			entrada.setAutorRegistro(auditDao.autorRegistro(entrada, entrada.getId()));
			entrada.setAutorUltimaAlteracao(auditDao.autorUltimaAlteracao(entrada, entrada.getId()));
		}
		return entrada;
	}
	
	public int buscarQuantidadeFiltrada(FiltroEntradaVisitante filtro) {
		return this.dao.buscarQuantidadeFiltrada(filtro);
	}

	public List<RecuperandoVisitanteDTO> buscarEntradasPorFiltro(FiltroEntradaVisitante filtro, boolean isRelatorio) {
		return agruparPorRecuperando(this.dao.buscarEntradasPorFiltro(filtro, isRelatorio));
	}

	public String validarEntrada(FiltroEntradaSaida f, Long unidade) {
		return this.dao.validarEntrada(f, unidade);
	}

	public Visitante responsavelDeuEntrada(Long id) {
		return dao.responsavelDeuEntrada(id);
	}

	public Set<Long> responsaveisDeuEntrada(Long id) {
		return dao.responsaveisDeuEntrada(id);
	}

	public List<RecuperandoVisitanteDTO> agruparPorRecuperando(List<Visitante> visitantes) {
		Map<Long, RecuperandoVisitanteDTO> mapa = new LinkedHashMap<>();

		for (Visitante visitante : visitantes) {
			// Ignorar visitantes sem credenciamento válido
			if (visitante.getCredenciamentos() == null || visitante.getCredenciamentos().isEmpty() ||
					!visitante.getStatus().equals(StatusVisitante.A)) {
				continue;
			}

			Optional<Credenciamento> credenciamentoOpt = visitante.getCredenciamentos().stream()
					.filter(cred ->
                            {
                                try {
                                    return buscarStatus(cred).getStatus().equals(StatusCredenciamentosVisita.DISPONIVEL);
                                } catch (ValidacaoException e) {
                                    throw new RuntimeException(e);
                                }
                            }
					)
					.findFirst();

			if (!credenciamentoOpt.isPresent()) {
				continue;
			}

			Credenciamento credenciamento = credenciamentoOpt.get();
			Recuperando recuperando = credenciamento.getRecuperando();
			Pessoa pessoaRecuperando = recuperando.getPessoa();
			String nomeEstruturaRecuperando = buscarNomeEstruturaRecuperando(recuperando);

			Long idRecuperando = recuperando.getId();

			// Cria o DTO se ainda não existir
			RecuperandoVisitanteDTO dto = mapa.computeIfAbsent(idRecuperando, id -> {
				RecuperandoVisitanteDTO novoDto = new RecuperandoVisitanteDTO();
				novoDto.setId(id);
				novoDto.setNomeRecuperando(pessoaRecuperando.getNome());
				novoDto.setNomeSocialRecuperando(pessoaRecuperando.getNomeSocial());
				novoDto.setNomeEstrutura(nomeEstruturaRecuperando); // ou lógica para buscar estrutura real
				return novoDto;
			});

			// Adiciona o visitante ao DTO correspondente
			dto.getEntradaSaidas().add(visitante);
		}

		return new ArrayList<>(mapa.values());
	}

	private Long buscarIdEstruturaRecuperando(Recuperando recuperando){
		Long idEstrutura = null;
		for (Passagem p : recuperando.getPassagens()){
			if(p.getDataSaida() == null){
				return p.getUltimaLotacao().getEstrutura().getId();
			}
		}
		return idEstrutura;
	}

	private String buscarNomeEstruturaRecuperando(Recuperando recuperando){
		Long idEstrutura = buscarIdEstruturaRecuperando(recuperando);
		String nomeEstrutura = estruturaService.buscarNomeEstrutura(idEstrutura);
		if(idEstrutura == null || nomeEstrutura == null){
			return "";
		}

		return nomeEstrutura;
	}

	public List<ParametroVisita> getParametros() throws ValidacaoException {
		return buscarParametrosVisita(SecurityUtil.getUnidadeSelecionada().getId(), new Date());
	}

	public StatusVisita buscarStatus(Credenciamento vr) throws ValidacaoException {
		StatusVisita result = new StatusVisita();
		result.setObservacao("");
		result.setStatus(StatusCredenciamentosVisita.DISPONIVEL);
		if (StatusVisitante.M.equals(vr.getVisitante().getStatus())) {
			result.setObservacao("Visitante impedido");
			result.setStatus(StatusCredenciamentosVisita.IMPEDIMENTO);
		} else {
			if (vr.getFaseCredenciamento().equals(FaseCredenciamento.APROVACAO.getId())
					&& vr.getStatusCredenciamento().equals(StatusCredenciamento.APROVADO.getId())) {
				if (vr.getRecuperando() != null) {

					boolean emTransferencia = pedidoService.recuperandoEmTransferencia(vr.getRecuperando());
					boolean emMovimentacao = pedidoService.recuperandoEmMovimentacaoExterna(vr.getRecuperando());

					if (emTransferencia || emMovimentacao) {
						result.setObservacao("Recuperando em movimentação");
						result.setStatus(StatusCredenciamentosVisita.INDISPONIVEL);
					}

					for (FaltaDisciplinar f : this.faltaDisciplinarService.listarFaltaDisciplinarPorRecuperandoId(vr.getRecuperando().getId())) {
						Date di = new Date(0);
						Date df = new Date(0);
						if (f.getDataInicio() != null && f.getDataFim() != null) {
							di = Sigepen.getDataConsultaMaiorIgual(f.getDataInicio());
							df = Sigepen.getDataConsultaMenorIgual(f.getDataFim());
						}
						Date dh = new Date();
						if (vr.getTipoVisita().getTipoEntrada().equals(TipoEntrada.FAMILIA)) {
							if (f.getSituacao().equals(SituacaoFaltaDisciplinar.CONCLUIDA)
									&& f.isParecerSuspendeVisita() && di.before(dh) && df.after(dh)) {
								result.setObservacao("Falta disciplinar em andamento");
								result.setStatus(StatusCredenciamentosVisita.FALTA_DISCIPLINAR);
							} else if (f.getSituacao().equals(SituacaoFaltaDisciplinar.EM_ANDAMENTO)
									&& f.isPadSuspendeVisita()) {
								result.setObservacao("Falta disciplinar em andamento");
								result.setStatus(StatusCredenciamentosVisita.FALTA_DISCIPLINAR);
							} else if (f.getSituacao().equals(SituacaoFaltaDisciplinar.PENDENTE)
									&& f.isFatoSuspendeVisita()) {
								result.setObservacao("Falta disciplinar em andamento");
								result.setStatus(StatusCredenciamentosVisita.FALTA_DISCIPLINAR);
							}
						}
					}
					boolean lotacaoDisponivel = false;
					PassagemLotacao x = this.faltaDisciplinarService.buscaPassagemLotacaoPorRecuperandoId(vr.getRecuperando().getId());

//					TipoVisita tipoVisita =  tipoVisitaService.buscaPorIdCredenciamento(vr.getId());
					TipoVisita tipoVisita = vr.getTipoVisita();

					for(Estrutura e : this.getEstruturaDisponivel(tipoVisita.getTipoEntrada())) {
						if(e.getId() == x.getEstrutura().getId()) {

							lotacaoDisponivel = true;
						}
					}

					if (!lotacaoDisponivel) {
						result.setObservacao("Lotação do Recuperando não agendada para o dia de visita");
						result.setStatus(StatusCredenciamentosVisita.INDISPONIVEL);
					}
				} else {
					if (vr.getUnidadePrisional() == null) {
						if (vr.getVisitante().getUnidadePrisional().getId() != SecurityUtil.getUnidadeSelecionada()
								.getId()) {
							result.setObservacao("Lotação do Recuperando não agendada para o dia de visita");
							result.setStatus(StatusCredenciamentosVisita.INDISPONIVEL);
						}
					} else {
						if (vr.getUnidadePrisional().getId() != SecurityUtil.getUnidadeSelecionada().getId()) {
							result.setObservacao("Lotação do Recuperando não agendada para o dia de visita");
							result.setStatus(StatusCredenciamentosVisita.INDISPONIVEL);
						}
					}

					boolean lotacaoDisponivel = false;

					TipoVisita tipoVisita = tipoVisitaService.buscaPorIdCredenciamento(vr.getId());
					List<Estrutura> listEstruturasDisponiveis = this.getEstruturaDisponivelEntradaSaida(tipoVisita.getTipoEntrada());


					if (listEstruturasDisponiveis.size() > 0) {
						for (Estrutura e : listEstruturasDisponiveis) {
							if (e != null) {
								lotacaoDisponivel = true;
							}
						}

						if (!lotacaoDisponivel) {
							result.setObservacao("Lotação do Recuperando não agendada para o dia de visita");
							result.setStatus(StatusCredenciamentosVisita.INDISPONIVEL);
						}
					} else {
						result.setObservacao("Parâmetro da unidade não cadastrado");
						result.setStatus(StatusCredenciamentosVisita.INDISPONIVEL);
					}
				}

				for (FaseDoCredenciamento vc : this.credenciamentoService.listaFasesDoCredenciamento(vr)) {
					if (FaseCredenciamento.APROVACAO.equals(vc.getFaseCredenciamento())) {
						if (vr.getDataValidade() == null) {
							result.setObservacao("Credenciamento em andamento");
							result.setStatus(StatusCredenciamentosVisita.INDISPONIVEL);
						} else {
							if (vr.getDataValidade().before(Sigepen.getDataConsultaMaiorIgual(new Date()))) {
								result.setObservacao("Credenciamento vencido");
								result.setStatus(StatusCredenciamentosVisita.IMPEDIMENTO);
							}
						}
					}
				}
			} else if (vr.getStatusCredenciamento().equals(StatusCredenciamento.INATIVO.getId())) {
				result.setObservacao("Credenciamento inativo");
				result.setStatus(StatusCredenciamentosVisita.INDISPONIVEL);
			} else if (vr.getStatusCredenciamento().equals(StatusCredenciamento.NAO_APROVADO.getId())) {
				result.setObservacao("Credenciamento não aprovado");
				result.setStatus(StatusCredenciamentosVisita.INDISPONIVEL);
			} else if (vr.getStatusCredenciamento().equals(StatusCredenciamento.PENDENTE.getId())) {
				result.setObservacao("Credenciamento em andamento");
				result.setStatus(StatusCredenciamentosVisita.INDISPONIVEL);
			} else {
				result.setObservacao("Credenciamento não disponível");
				result.setStatus(StatusCredenciamentosVisita.INDISPONIVEL);
			}
		}
		return result;
	}

	public List<Estrutura> getEstruturaDisponivel(TipoEntrada te) throws ValidacaoException {
		List<Estrutura> r = new ArrayList<Estrutura>();
		for (ParametroVisita p : this.getParametros()) {
			if (p.getTipoEntrada() == te) {
				for (Estrutura e : p.getCelas()) {
					boolean cad = true;
					for (Estrutura x : r) {
						if (x.getId() == e.getId()) {
							cad = false;
						}
					}
					if (cad) {
						r.add(e);
					}
				}
			}
		}
		return r;
	}

	public List<Estrutura> getEstruturaDisponivelEntradaSaida(TipoEntrada te) throws ValidacaoException {
		List<Estrutura> r = new ArrayList<Estrutura>();
		for (ParametroVisita p : this.getParametros()) {
			if (p.getTipoEntrada() == te) {
				for (Estrutura e : p.getCelas()) {
					boolean cad = true;
					for (Estrutura x : r) {
						if (x.getId() == e.getId()) {
							cad = false;
						}
					}
					if (cad) {
						r.add(e);
					}
				}
			}
		}
		return r;
	}

}


