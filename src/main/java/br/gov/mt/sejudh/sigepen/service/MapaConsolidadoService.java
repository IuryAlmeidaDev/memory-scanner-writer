package br.gov.mt.sejudh.sigepen.service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.MapaConsolidadoDAO;
import br.gov.mt.sejudh.sigepen.enumerator.Mes;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.MapaConsolidadoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.MapaConsolidadoRecuperandoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioMapaAlimentacaoConsultaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioMapaAlimentacaoRecuperandoDTO;
import br.gov.mt.sejudh.sigepen.util.MonetaryFormatUtil;

@Stateless
public class MapaConsolidadoService {

	@Inject
	private MapaConsolidadoDAO dao;
	@Inject
	private ContratoService contratoService;

	private Long cafeManhaInicio;
	private Long cafeManhaFim;
	private Long almocoInicio;
	private Long almocoFim;
	private Long jantaInicio;
	private Long jantaFim;
	private boolean especial;
	private final Long horaMaxima = 2359L;
	private final Long horaMinima = 1L;
	private LocalDate vigenciaInicio;
	private LocalDate vigenciaFim;

	public MapaConsolidadoService() {
	}

	public List<RelatorioMapaAlimentacaoRecuperandoDTO> obterMapaAlimentacaoRecuperando(Long idEstrutura,
			LocalDate dataInicio, LocalDate dataFim) {
		List<RelatorioMapaAlimentacaoRecuperandoDTO> recuperandos = dao.buscaRelatorioMapaAlimentacaoRecuperando(idEstrutura, dataInicio, dataFim);
		return recuperandos != null ? recuperandos : new ArrayList<RelatorioMapaAlimentacaoRecuperandoDTO>();
	}

	public List<RelatorioMapaAlimentacaoConsultaDTO> obterMapaAlimentacaoConsulta(Long idUnidade, Long idContrato) {
		List<RelatorioMapaAlimentacaoConsultaDTO> consultas = dao.buscarRelatorioMapaAlimentacaoConsulta(idUnidade, idContrato);
		return consultas != null ? consultas : new ArrayList<RelatorioMapaAlimentacaoConsultaDTO>();
	}

	private void buscarHorariosRefeicoes(Long unidade) {
		this.cafeManhaInicio = null;
		this.cafeManhaFim = null;
		this.almocoInicio = null;
		this.almocoFim = null;
		this.jantaInicio = null;
		this.jantaFim = null;
		List<HorarioRefeicao> horariosRefeicoes = this.contratoService.buscarHorarioRefeiracao(unidade);
		this.setHorariosRefeicao(horariosRefeicoes);
	}

	private void setHorariosRefeicao(List<HorarioRefeicao> horariosRefeicoes) {
		if (horariosRefeicoes == null || horariosRefeicoes.isEmpty()) {
			return;
		}

		Long cafeManhaId = 1L;
		Long almocoId = 2L;
		Long jantaId = 3L;
		horariosRefeicoes.forEach(horarios -> {
			if (horarios == null || horarios.getTipoRefeicao() == null || horarios.getTipoRefeicao().getId() == null
					|| horarios.getHorarioInicio() == null || horarios.getHorarioFim() == null) {
				return;
			}

			if (horarios.getTipoRefeicao().getId() == cafeManhaId) {
				this.cafeManhaInicio = Long.parseLong(horarios.getHorarioInicio());
				this.cafeManhaFim = Long.parseLong(horarios.getHorarioFim());
			} else if (horarios.getTipoRefeicao().getId() == almocoId) {
				this.almocoInicio = Long.parseLong(horarios.getHorarioInicio());
				this.almocoFim = Long.parseLong(horarios.getHorarioFim());
			} else if (horarios.getTipoRefeicao().getId() == jantaId) {
				this.jantaInicio = Long.parseLong(horarios.getHorarioInicio());
				this.jantaFim = Long.parseLong(horarios.getHorarioFim());
			}
		});
	}

	private void validarPeriodoVigencia(Contrato contrato, int mes, int ano) {
		this.vigenciaInicio = contrato.getVigenciaContratoInicio().toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate();
		this.vigenciaFim = contrato.getVigenciaContratoFim().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		LocalDate dtInicioMes = LocalDate.parse(obterDataInicio(mes, ano), DateTimeFormatter.ISO_LOCAL_DATE);
		LocalDate dtUltimoMes = LocalDate.parse(obterDataFim(mes, ano), DateTimeFormatter.ISO_LOCAL_DATE);

		if (vigenciaInicio.isBefore(dtInicioMes)) {
			this.vigenciaInicio = dtInicioMes;
		}
		if (!vigenciaFim.isBefore(dtUltimoMes)) {
			this.vigenciaFim = dtUltimoMes;
		}

		this.vigenciaFim = this.vigenciaFim.plusDays(1);
	}

	public MapaConsolidadoDTO obterMapaConsolidado(UnidadePrisional unidade, Mes mes, String ano, Contrato contrato) {
		this.validarPeriodoVigencia(contrato, mes.getId().intValue(), Integer.parseInt(ano));
		this.buscarHorariosRefeicoes(unidade.getId());
		MapaConsolidadoDTO mapaConsolidado = new MapaConsolidadoDTO();
		mapaConsolidado.setNomeUnidade(unidade.getNome());
		List<RelatorioMapaAlimentacaoRecuperandoDTO> mapaAlimentacaoRecuperandos = obterMapaAlimentacaoRecuperando(
				unidade.getEstrutura().getId(), this.vigenciaInicio, this.vigenciaFim);
		List<RelatorioMapaAlimentacaoConsultaDTO> mapaAlimentacaoConsulta = obterMapaAlimentacaoConsulta(
				unidade.getId(), contrato.getId());

		this.montarMapaConsolidado(mapaAlimentacaoConsulta, mapaConsolidado, mes);

		Integer numero = 1;
		for (RelatorioMapaAlimentacaoRecuperandoDTO mapaRecuperando : mapaAlimentacaoRecuperandos) {
			MapaConsolidadoRecuperandoDTO mapaConsolidadoRecuperando = new MapaConsolidadoRecuperandoDTO();

			mapaConsolidadoRecuperando.setNumero(numero);
			numero++;
			mapaConsolidadoRecuperando.setNomeRecuperando(mapaRecuperando.getNomeRecuperando());
			mapaConsolidadoRecuperando.setNomeSocialRecuperando(mapaRecuperando.getNomeSocialRecuperando());
			mapaConsolidadoRecuperando.setSituacaoRecuperando(mapaRecuperando.getSituacaoRecuperando());
			mapaConsolidadoRecuperando.setMatriculaRecuperando(mapaRecuperando.getMatriculaRecuperando());
			mapaConsolidadoRecuperando.setEntradaRecuperando(mapaRecuperando.getEntradaRecuperando());
			mapaConsolidadoRecuperando.setSaidaRecuperando(mapaRecuperando.getSaidaRecuperando());
			mapaConsolidadoRecuperando.setInicioEspecial(mapaRecuperando.getInicioEspecial());
			mapaConsolidadoRecuperando.setFimEspecial(mapaRecuperando.getFimEspecial());

			int qtdDiasEspecial = obterDias(mapaRecuperando.getInicioEspecial(), mapaRecuperando.getFimEspecial());
			this.especial = qtdDiasEspecial == 0 ? false : true;

			mapaConsolidadoRecuperando.setDiasRecuperandoEspecial(qtdDiasEspecial);
			mapaConsolidadoRecuperando.setDiasRecuperandoNormal(
					obterDias(mapaRecuperando.getEntradaRecuperando(), mapaRecuperando.getSaidaRecuperando()));

//			mapaConsolidadoRecuperando.calculoDiasRecuperandoNormal(obterDias(mapaRecuperando.getEntradaRecuperando(),
//					mapaRecuperando.getSaidaRecuperando(), mes.getId().intValue(), Integer.parseInt(ano)));

			mapaConsolidadoRecuperando.setDiasRecuperando(mapaConsolidadoRecuperando.getDiasRecuperandoNormal());

			mapaConsolidadoRecuperando.setHorarioSaida(
					obterHoraio(mapaRecuperando.getSaidaRecuperando(), mes.getId().intValue(), Integer.parseInt(ano)));
			mapaConsolidadoRecuperando.setHorarioEspecialSaida(
					obterHoraio(mapaRecuperando.getFimEspecial(), mes.getId().intValue(), Integer.parseInt(ano)));
			mapaConsolidadoRecuperando.setHorarioEntrada(obterHoraio(mapaRecuperando.getEntradaRecuperando(),
					mes.getId().intValue(), Integer.parseInt(ano)));
			mapaConsolidadoRecuperando.setHorarioEspecialEntrada(
					obterHoraio(mapaRecuperando.getInicioEspecial(), mes.getId().intValue(), Integer.parseInt(ano)));

			mapaAlimentacaoConsulta.forEach(mapaConsulta -> {

				this.montarValorRefeicao(mapaConsulta, mapaConsolidadoRecuperando);
			});
			mapaConsolidadoRecuperando.calculoSoma();

			this.tratarValorRefeicao(mapaConsolidado, mapaAlimentacaoConsulta);
			mapaConsolidado.getConsolidadoRecuperandoDTOs().add(mapaConsolidadoRecuperando);
		}
		this.montarTotais(mapaConsolidado);
		return mapaConsolidado;
	}

	private void montarTotais(MapaConsolidadoDTO mapaConsolidado) {
		Integer qtdCafe = 0;
		Integer qtdAlmoco = 0;
		Integer qtdJanta = 0;
		Integer qtdCafeEspecial = 0;
		Integer qtdAlmocoEspecial = 0;
		Integer qtdJantaEspecial = 0;
		Double valorCafe = 0D;
		Double valorAlmoco = 0D;
		Double valorJanta = 0D;
		Double valorCafeEspecial = 0D;
		Double valorAlmocoEspecial = 0D;
		Double valorJantaEspecial = 0D;

		for (MapaConsolidadoRecuperandoDTO recuperando : mapaConsolidado.getConsolidadoRecuperandoDTOs()) {
			qtdCafe = recuperando.getDiasRecuperandoCafeNormal() + qtdCafe;
			qtdAlmoco = recuperando.getDiasRecuperandoAlmocoNormal() + qtdAlmoco;
			qtdJanta = recuperando.getDiasRecuperandoJantaNormal() + qtdJanta;

			qtdCafeEspecial = recuperando.getDiasRecuperandoCafeEspecial() + qtdCafeEspecial;
			qtdAlmocoEspecial = recuperando.getDiasRecuperandoAlmocoEspecial() + qtdAlmocoEspecial;
			qtdJantaEspecial = recuperando.getDiasRecuperandoJantaEspecial() + qtdJantaEspecial;

			valorCafe = recuperando.getCafeManha() + valorCafe;
			valorAlmoco = recuperando.getAlmoco() + valorAlmoco;
			valorJanta = recuperando.getJanta() + valorJanta;

			valorCafeEspecial = recuperando.getCafeManhaEspecial() + valorCafeEspecial;
			valorAlmocoEspecial = recuperando.getAlmocoEspecial() + valorAlmocoEspecial;
			valorJantaEspecial = recuperando.getJantaEspecial() + valorJantaEspecial;
		}

		mapaConsolidado.setTotalCafe(qtdCafe);
		mapaConsolidado.setTotalAlmoco(qtdAlmoco);
		mapaConsolidado.setTotalJanta(qtdJanta);

		mapaConsolidado.setTotalCafeEspecial(qtdCafeEspecial);
		mapaConsolidado.setTotalAlmocoEspecial(qtdAlmocoEspecial);
		mapaConsolidado.setTotalJantaEspecial(qtdJantaEspecial);

		mapaConsolidado.setTotalValorCafe(valorCafe);
		mapaConsolidado.setTotalValorAlmoco(valorAlmoco);
		mapaConsolidado.setTotalValorJanta(valorJanta);

		mapaConsolidado.setTotalValorCafeEspecial(valorCafeEspecial);
		mapaConsolidado.setTotalValorAlmocoEspecial(valorAlmocoEspecial);
		mapaConsolidado.setTotalValorJantaEspecial(valorJantaEspecial);

	}

	private void tratarValorRefeicao(MapaConsolidadoDTO mapaConsolidado,
			List<RelatorioMapaAlimentacaoConsultaDTO> mapaAlimentacaoConsulta) {
		MapaConsolidadoRecuperandoDTO mapaConsolidadoRecuperando = new MapaConsolidadoRecuperandoDTO();
		mapaAlimentacaoConsulta.forEach(mapaConsulta -> {
			if (mapaConsulta == null || mapaConsulta.getRefeicao() == null) {
				return;
			}
			if (mapaConsulta.getRefeicao().equals("CAFÉ DA MANHÃ")) {
				mapaConsolidadoRecuperando.setCafeManha(mapaConsulta.getValorNormal());
				mapaConsolidadoRecuperando.setCafeManhaEspecial(mapaConsulta.getValorEspecial());
			}
			if (mapaConsulta.getRefeicao().equals("ALMOÇO")) {
				mapaConsolidadoRecuperando.setAlmoco(mapaConsulta.getValorNormal());
				mapaConsolidadoRecuperando.setAlmocoEspecial(mapaConsulta.getValorEspecial());
			}
			if (mapaConsulta.getRefeicao().equals("JANTA")) {
				mapaConsolidadoRecuperando.setJanta(mapaConsulta.getValorNormal());
				mapaConsolidadoRecuperando.setJantaEspecial(mapaConsulta.getValorEspecial());
			}
		});

		mapaConsolidado.setValorRefeicao(

				"Normal CA " + MonetaryFormatUtil.formatBrl(mapaConsolidadoRecuperando.getCafeManha())
						+ " | AL " + MonetaryFormatUtil.formatBrl(mapaConsolidadoRecuperando.getAlmoco())
						+ " | JA " + MonetaryFormatUtil.formatBrl(mapaConsolidadoRecuperando.getJanta())
						+ " | Especial CA " + MonetaryFormatUtil.formatBrl(mapaConsolidadoRecuperando.getCafeManhaEspecial())
						+ " | AL " + MonetaryFormatUtil.formatBrl(mapaConsolidadoRecuperando.getAlmocoEspecial())
						+ " | JA " + MonetaryFormatUtil.formatBrl(mapaConsolidadoRecuperando.getJantaEspecial()));
	}

	private void montarMapaConsolidado(List<RelatorioMapaAlimentacaoConsultaDTO> mapaAlimentacaoConsulta,
			MapaConsolidadoDTO mapaConsolidado, Mes mes) {
		mapaAlimentacaoConsulta.forEach(mapaConsulta -> {
			if (mapaConsulta == null) {
				return;
			}

			mapaConsolidado.setNomeFornecedor(mapaConsulta.getNomeFornecedor());
			mapaConsolidado.setVirgenciaContartoInicio(mapaConsulta.getVirgenciaContartoInicio());
			mapaConsolidado.setVirgenciaContartoFim(mapaConsulta.getVirgenciaContartoFim());
			mapaConsolidado.setNumeroContrato(mapaConsulta.getNumeroContrato());
			mapaConsolidado.setMesReferencia(mes.getDescricao());

		});
	}

	private void montarValorRefeicao(RelatorioMapaAlimentacaoConsultaDTO mapaConsulta,
			MapaConsolidadoRecuperandoDTO mapaConsolidadoRecuperando) {
		if (mapaConsulta == null || mapaConsulta.getRefeicao() == null) {
			return;
		}

		int qtdDiasNormal = 0;
		int qtdDiasEspecial = 0;

		if (mapaConsulta.getRefeicao().equals("CAFÉ DA MANHÃ")) {

			qtdDiasNormal = contarRefeicoesNoPeriodo(mapaConsolidadoRecuperando.getDataEntradaRecuperando(),
					mapaConsolidadoRecuperando.getDataSaidaRecuperando(), this.cafeManhaInicio, this.cafeManhaFim);
			qtdDiasEspecial = contarRefeicoesEspeciais(mapaConsolidadoRecuperando, this.cafeManhaInicio, this.cafeManhaFim);

			mapaConsolidadoRecuperando.setDiasRecuperandoCafeNormal(qtdDiasNormal - qtdDiasEspecial);
			mapaConsolidadoRecuperando.setCafeManha(mapaConsulta.getValorNormal() * (qtdDiasNormal - qtdDiasEspecial));
			mapaConsolidadoRecuperando.setDiasRecuperandoCafeEspecial(qtdDiasEspecial);
			mapaConsolidadoRecuperando.setCafeManhaEspecial(mapaConsulta.getValorEspecial() * qtdDiasEspecial);
		}
		if (mapaConsulta.getRefeicao().equals("ALMOÇO")) {

			qtdDiasNormal = contarRefeicoesNoPeriodo(mapaConsolidadoRecuperando.getDataEntradaRecuperando(),
					mapaConsolidadoRecuperando.getDataSaidaRecuperando(), this.almocoInicio, this.almocoFim);
			qtdDiasEspecial = contarRefeicoesEspeciais(mapaConsolidadoRecuperando, this.almocoInicio, this.almocoFim);

			mapaConsolidadoRecuperando.setDiasRecuperandoAlmocoNormal(qtdDiasNormal - qtdDiasEspecial);
			mapaConsolidadoRecuperando.setAlmoco(mapaConsulta.getValorNormal() * (qtdDiasNormal - qtdDiasEspecial));
			mapaConsolidadoRecuperando.setDiasRecuperandoAlmocoEspecial(qtdDiasEspecial);
			mapaConsolidadoRecuperando.setAlmocoEspecial(mapaConsulta.getValorEspecial() * qtdDiasEspecial);
		}
		if (mapaConsulta.getRefeicao().equals("JANTA")) {

			qtdDiasNormal = contarRefeicoesNoPeriodo(mapaConsolidadoRecuperando.getDataEntradaRecuperando(),
					mapaConsolidadoRecuperando.getDataSaidaRecuperando(), this.jantaInicio, this.jantaFim);
			qtdDiasEspecial = contarRefeicoesEspeciais(mapaConsolidadoRecuperando, this.jantaInicio, this.jantaFim);

			mapaConsolidadoRecuperando.setDiasRecuperandoJantaNormal(qtdDiasNormal - qtdDiasEspecial);
			mapaConsolidadoRecuperando.setJanta(mapaConsulta.getValorNormal() * (qtdDiasNormal - qtdDiasEspecial));
			mapaConsolidadoRecuperando.setDiasRecuperandoJantaEspecial(qtdDiasEspecial);
			mapaConsolidadoRecuperando.setJantaEspecial(mapaConsulta.getValorEspecial() * qtdDiasEspecial);
		}
	}

	private int contarRefeicoesEspeciais(MapaConsolidadoRecuperandoDTO recuperando, Long horarioRefeicaoInicio,
			Long horarioRefeicaoFim) {
		if (recuperando.getInicioEspecial() == null) {
			return 0;
		}

		Date inicio = maiorData(recuperando.getDataEntradaRecuperando(), recuperando.getInicioEspecial());
		Date fim = menorData(recuperando.getDataSaidaRecuperando(), recuperando.getFimEspecial());

		if (fim != null && inicio != null && fim.before(inicio)) {
			return 0;
		}

		return contarRefeicoesNoPeriodo(inicio, fim, horarioRefeicaoInicio, horarioRefeicaoFim);
	}

	private Date maiorData(Date primeira, Date segunda) {
		if (primeira == null) {
			return segunda;
		}
		if (segunda == null) {
			return primeira;
		}
		return primeira.after(segunda) ? primeira : segunda;
	}

	private Date menorData(Date primeira, Date segunda) {
		if (primeira == null) {
			return segunda;
		}
		if (segunda == null) {
			return primeira;
		}
		return primeira.before(segunda) ? primeira : segunda;
	}

	private int contarRefeicoesNoPeriodo(Date inicio, Date fim, Long horarioRefeicaoInicio, Long horarioRefeicaoFim) {
		if (inicio == null || horarioRefeicaoInicio == null || horarioRefeicaoFim == null) {
			return 0;
		}

		LocalDateTime inicioPeriodo = inicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime fimPeriodo = fim != null
				? fim.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
				: this.vigenciaFim.atStartOfDay();
		LocalDateTime inicioVigencia = this.vigenciaInicio.atStartOfDay();
		LocalDateTime fimVigencia = this.vigenciaFim.atStartOfDay();

		if (inicioPeriodo.isBefore(inicioVigencia)) {
			inicioPeriodo = inicioVigencia;
		}
		if (fimPeriodo.isAfter(fimVigencia)) {
			fimPeriodo = fimVigencia;
		}
		if (fimPeriodo.isBefore(inicioPeriodo)) {
			return 0;
		}

		LocalTime horaInicioRefeicao = toLocalTime(horarioRefeicaoInicio);
		LocalTime horaFimRefeicao = toLocalTime(horarioRefeicaoFim);
		int total = 0;

		for (LocalDate dia = inicioPeriodo.toLocalDate(); dia.isBefore(this.vigenciaFim); dia = dia.plusDays(1)) {
			LocalDateTime inicioRefeicao = dia.atTime(horaInicioRefeicao);
			LocalDateTime fimRefeicao = dia.atTime(horaFimRefeicao);

			if (!fimPeriodo.isBefore(inicioRefeicao) && !inicioPeriodo.isAfter(fimRefeicao)) {
				total++;
			}
		}

		return total;
	}

	private LocalTime toLocalTime(Long horario) {
		String valor = String.format("%04d", horario);
		return LocalTime.of(Integer.parseInt(valor.substring(0, 2)), Integer.parseInt(valor.substring(2, 4)));
	}

	private boolean validaHorarioPedidoPorRefeicao(Long horarioEntrada, Long horarioSaida, Long horarioRefeicaoInicio, Long horarioRefeicaoFim) {
		if (horarioEntrada == null || horarioSaida == null || horarioRefeicaoInicio == null || horarioRefeicaoFim == null) {
			return false;
		}

		return horarioSaida >= horarioRefeicaoInicio && horarioEntrada <= horarioRefeicaoFim;
	}

	private int validarQtdDiasPeloHorario(int qtdDias, boolean horarioPedido) {
		if (qtdDias == 0) {
			return qtdDias;
		}
		if (horarioPedido) {
			return qtdDias;
		}
		return qtdDias - 1;
	}

	private int validarQtdDiasPeloHorario(int qtdDias, boolean horarioPedido, boolean especial) {
		if (!especial) {
			return 0;
		}
		if (qtdDias == 0) {
			return 1;
		}
		if (horarioPedido) {
			return qtdDias;
		}
		return qtdDias - 1;
	}

	private String obterDataFim(int mes, int ano) {
		LocalDateTime dataHora = LocalDateTime.of(ano, Month.of(mes), Month.of(mes).maxLength(), 0, 0, 0);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return dataHora.format(formatter);

	}

	private String obterDataInicio(int mes, int ano) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return YearMonth.of(ano, mes).atDay(1).format(formatter);

	}

	private Long obterHoraio(Date data, int mes, int ano) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");
		LocalDate dtInicioMes = this.vigenciaInicio;
		LocalDate dtUltimoMes = this.vigenciaFim;

		if (data == null) {
			return this.horaMaxima;
		}
		LocalDate d1 = LocalDate.parse(sdf.format(data), DateTimeFormatter.ISO_LOCAL_DATE);
		if (d1.isBefore(dtInicioMes)) {
			return this.horaMinima;
		}
		if (!d1.isBefore(dtUltimoMes)) {
			return this.horaMaxima;
		}

		return this.convetHoraLong(formatoHora.format(data));
	}

	private Long convetHoraLong(String hora) {
		return Long.parseLong(hora.replace(":", ""));
	}

	private int obterDias(Date dataInicio, Date dataFinal) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Duration diff = null;

		if (dataInicio != null && dataFinal == null) {
			LocalDate d1 = LocalDate.parse(sdf.format(dataInicio), DateTimeFormatter.ISO_LOCAL_DATE);
			LocalDate d2 = this.vigenciaFim;

			if (d1.isBefore(this.vigenciaInicio)) {
				diff = Duration.between(this.vigenciaInicio.atStartOfDay(), d2.atStartOfDay());
			} else {
				diff = Duration.between(d1.atStartOfDay(), d2.atStartOfDay());
			}

			return (int) diff.toDays();
		}

		if (dataFinal == null && dataInicio == null) {
			return 0;
		} else {
			LocalDate d1 = LocalDate.parse(sdf.format(dataInicio), DateTimeFormatter.ISO_LOCAL_DATE);
			LocalDate d2 = LocalDate.parse(sdf.format(dataFinal), DateTimeFormatter.ISO_LOCAL_DATE).plusDays(1);

			if (d1.isBefore(this.vigenciaInicio) && d2.isBefore(this.vigenciaFim)) {
				diff = Duration.between(this.vigenciaInicio.atStartOfDay(), d2.atStartOfDay());
				return (int) diff.toDays();
			} else if (d1.isBefore(this.vigenciaInicio) && !d2.isBefore(this.vigenciaFim)) {
				diff = Duration.between(this.vigenciaInicio.atStartOfDay(), this.vigenciaFim.atStartOfDay());
				return (int) diff.toDays();
			} else if (!d1.isBefore(this.vigenciaInicio) && d2.isBefore(this.vigenciaFim)) {
				diff = Duration.between(d1.atStartOfDay(), d2.atStartOfDay());
				return (int) diff.toDays();
			} else {
				diff = Duration.between(d1.atStartOfDay(), this.vigenciaFim.atStartOfDay());
				return obterQtdDias(diff);
			}

		}
	}

	private int obterQtdDias(Duration diff) {
		return (int) diff.toDays();
	}
	
	public Map<String, Integer> obterDadosAuditoria(Long idUnidade, String dataInicio, String dataFim) {
	    Map<String, Object> dadosBrutos = dao.buscarTotaisPedidosAuditados(idUnidade, dataInicio, dataFim);
	    if (dadosBrutos == null) {
	    	dadosBrutos = new HashMap<String, Object>();
	    }
	    Map<String, Integer> params = new HashMap<>();

	    params.put("AUDIT_CAFE", (Integer) dadosBrutos.getOrDefault("AUDIT_CAFE", 0));
	    params.put("AUDIT_ALMOCO", (Integer) dadosBrutos.getOrDefault("AUDIT_ALMOCO", 0));
	    params.put("AUDIT_JANTA", (Integer) dadosBrutos.getOrDefault("AUDIT_JANTA", 0));
	    
	    params.put("AUDIT_CAFE_ESP", (Integer) dadosBrutos.getOrDefault("AUDIT_CAFE_ESPECIAL", 0));
	    params.put("AUDIT_ALMOCO_ESP", (Integer) dadosBrutos.getOrDefault("AUDIT_ALMOCO_ESPECIAL", 0));
	    params.put("AUDIT_JANTA_ESP", (Integer) dadosBrutos.getOrDefault("AUDIT_JANTA_ESPECIAL", 0));
	    
	    return params;
	}
}
