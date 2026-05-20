package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.dao.CardapioDAO;
import br.gov.mt.sejudh.sigepen.dao.PedidoAlimentoDAO;
import br.gov.mt.sejudh.sigepen.enumerator.OrigemPedidoAlimento;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.DietaEspecialPedidoAlimento;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.ItemPedidoAlimento;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.DetalheItemEstruturaCardapioDTO;
import br.gov.mt.sejudh.sigepen.model.dto.DietaEspecialEstruturaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoEstruturaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RefeicaoDetalhadaDTO;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.hibernate.exception.ConstraintViolationException;

@Stateless
public class PedidoAlimentoService {

    public enum ModoGeracaoPedido {
        MANUAL_PENDENTE,
        MANUAL_ASSINADO,
        AUTOMATICO_ATIVO
    }

    public static class PedidoMontado {
        private final PedidoAlimento pedidoAlimento;
        private final List<DetalheItemEstruturaCardapioDTO> itens;
        private final List<DietaEspecialEstruturaDTO> dietasEspeciais;

        public PedidoMontado(PedidoAlimento pedidoAlimento,
                List<DetalheItemEstruturaCardapioDTO> itens,
                List<DietaEspecialEstruturaDTO> dietasEspeciais) {
            this.pedidoAlimento = pedidoAlimento;
            this.itens = itens;
            this.dietasEspeciais = dietasEspeciais;
        }

        public PedidoAlimento getPedidoAlimento() {
            return pedidoAlimento;
        }

        public List<DetalheItemEstruturaCardapioDTO> getItens() {
            return itens;
        }

        public List<DietaEspecialEstruturaDTO> getDietasEspeciais() {
            return dietasEspeciais;
        }
    }

    public static class PedidoGerado {
        private final PedidoMontado pedidoMontado;
        private final boolean substituiuPedidoAtivo;

        public PedidoGerado(PedidoMontado pedidoMontado, boolean substituiuPedidoAtivo) {
            this.pedidoMontado = pedidoMontado;
            this.substituiuPedidoAtivo = substituiuPedidoAtivo;
        }

        public PedidoMontado getPedidoMontado() {
            return pedidoMontado;
        }

        public boolean isSubstituiuPedidoAtivo() {
            return substituiuPedidoAtivo;
        }
    }

    private static class CicloEntregaAtivo {
        private final HorarioRefeicao horarioAtual;
        private final HorarioRefeicao horarioProximo;
        private final LocalDate dataEntregaAtual;

        private CicloEntregaAtivo(HorarioRefeicao horarioAtual, HorarioRefeicao horarioProximo, LocalDate dataEntregaAtual) {
            this.horarioAtual = horarioAtual;
            this.horarioProximo = horarioProximo;
            this.dataEntregaAtual = dataEntregaAtual;
        }
    }

    private static final Logger LOGGER = Logger.getLogger(PedidoAlimentoService.class.getName());
    private static final int HORAS_CORTE_PADRAO = 2;
    private static final Long USUARIO_SISTEMA = 0L;
    private static final DateTimeFormatter DATA_PEDIDO_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter HORARIO_REFEICAO_FORMATTER = DateTimeFormatter.ofPattern("HHmm");
    private static final String INDICE_UNICO_PEDIDO_ATIVO = "UK_SGP_PED_ALIM_ATIVO";

    @Inject
    private PedidoAlimentoDAO dao;

    @Inject
    private ContratoService contratoService;

    @Inject
    private UnidadePrisionalService unidadePrisionalService;

    @Inject
    private EstruturaService estruturaService;

    @Inject
    private CardapioDAO cardapioDAO;

    @Inject
    private TipoRefeicaoService tipoRefeicaoService;

    @Inject
    private AgendaPedidoRefeicaoService agendaPedidoRefeicaoService;

    @Inject
    private RolloutAlimentacaoService rolloutAlimentacaoService;

    @Resource
    private SessionContext sessionContext;

    public boolean verificarExistenciaPedidoAtivo(String dataPedido, Long idTipoRefeicao,
            Long idUnidadePrisional, Long idContrato) {
        return dao.existePedidoAtivo(dataPedido, idTipoRefeicao, idUnidadePrisional, idContrato);
    }

    @Transactional
    public void salvarPedidoPendente(PedidoAlimento novoPedido) {
        novoPedido.setStatus(Status.PENDENTE);
        dao.salvar(novoPedido);
        LOGGER.info("Pedido PENDENTE salvo. ID: " + novoPedido.getId());
    }

    @Transactional
    public void salvarPedidoAutomaticoAtivo(PedidoAlimento novoPedido) {
        novoPedido.setStatus(Status.ATIVO);
        dao.salvar(novoPedido);
        LOGGER.info("Pedido automatico ATIVO salvo. ID: " + novoPedido.getId());
    }

    @Transactional
    public void salvarPedidoAssinadoSubstituindo(PedidoAlimento novoPedido) {
        List<Long> idsParaInativar = dao.findPedidosEmAbertoPorTipoRefeicaoEUnidade(
                novoPedido.getDataPedido(),
                novoPedido.getIdTipoRefeicao(),
                novoPedido.getIdUnidadePrisional(),
                novoPedido.getIdContrato());

        if (!idsParaInativar.isEmpty()) {
            LOGGER.info("Inativando " + idsParaInativar.size() + " pedido(s) anteriores. IDs: " + idsParaInativar);
            dao.bulkDeactivateItemPedidoAlimentoByPedidoIds(idsParaInativar);
            dao.bulkDeactivateDietaEspecialPedidoAlimentoByPedidoIds(idsParaInativar);
            dao.bulkDeactivatePedidoAlimentoByIds(idsParaInativar);
        }

        novoPedido.setStatus(Status.ATIVO);
        dao.salvar(novoPedido);
        LOGGER.info("Pedido manual ATIVO salvo. ID: " + novoPedido.getId());
    }

    @Transactional
    public void salvarPrimeiroPedidoAssinado(PedidoAlimento novoPedido) {
        List<Long> idsEmAberto = dao.findPedidosEmAbertoPorTipoRefeicaoEUnidade(
                novoPedido.getDataPedido(),
                novoPedido.getIdTipoRefeicao(),
                novoPedido.getIdUnidadePrisional(),
                novoPedido.getIdContrato());

        if (!idsEmAberto.isEmpty()) {
            dao.bulkDeactivateItemPedidoAlimentoByPedidoIds(idsEmAberto);
            dao.bulkDeactivateDietaEspecialPedidoAlimentoByPedidoIds(idsEmAberto);
            dao.bulkDeactivatePedidoAlimentoByIds(idsEmAberto);
        }

        novoPedido.setStatus(Status.ATIVO);
        dao.salvar(novoPedido);
        LOGGER.info("Primeiro pedido ATIVO salvo. ID: " + novoPedido.getId());
    }

    @Transactional
    public void salvarPedidoAlimento(PedidoAlimento novoPedidoAlimento) {
        List<Long> idsPedidosExistentesParaDesativar = dao.findActivePedidoAlimentoIdsByCriteria(
                novoPedidoAlimento.getDataPedido(),
                novoPedidoAlimento.getIdTipoRefeicao(),
                novoPedidoAlimento.getIdUnidadePrisional(),
                novoPedidoAlimento.getIdContrato());

        if (!idsPedidosExistentesParaDesativar.isEmpty()) {
            LOGGER.info("Encontrados " + idsPedidosExistentesParaDesativar.size()
                    + " pedidos ativos para desativar. IDs: " + idsPedidosExistentesParaDesativar);
            dao.bulkDeactivateItemPedidoAlimentoByPedidoIds(idsPedidosExistentesParaDesativar);
            dao.bulkDeactivateDietaEspecialPedidoAlimentoByPedidoIds(idsPedidosExistentesParaDesativar);
            dao.bulkDeactivatePedidoAlimentoByIds(idsPedidosExistentesParaDesativar);
        }

        dao.salvar(novoPedidoAlimento);
        LOGGER.info("Novo PedidoAlimento salvo com sucesso. ID: " + novoPedidoAlimento.getId());
    }

    @Transactional
    public PedidoGerado gerarPedidoManual(UnidadePrisional unidade, Contrato contrato, TipoRefeicao tipoRefeicao,
            String dataPedido, Long idUsuarioResponsavel, boolean assinado) throws ValidacaoException {
        return gerarPedido(unidade, contrato, tipoRefeicao, dataPedido, idUsuarioResponsavel,
                assinado ? ModoGeracaoPedido.MANUAL_ASSINADO : ModoGeracaoPedido.MANUAL_PENDENTE);
    }

    public LocalDateTime calcularHorarioCorte(HorarioRefeicao horarioRefeicao, LocalDate dataPedido, int horasDeAntecedencia) {
        if (horarioRefeicao == null || horarioRefeicao.getHorarioInicio() == null || dataPedido == null) {
            throw new IllegalArgumentException("Horario de refeicao e data do pedido sao obrigatorios.");
        }

        LocalTime horarioEntrega = parseHorarioInicio(horarioRefeicao);
        return LocalDateTime.of(dataPedido, horarioEntrega).minusHours(horasDeAntecedencia);
    }

    public boolean pedidoEstaDentroDaJanelaDeEdicao(PedidoAlimento pedido, HorarioRefeicao horarioRefeicao,
            LocalDate dataPedido, int horasDeAntecedencia, LocalDateTime referencia) {
        LocalDateTime horarioCorte = calcularHorarioCorte(horarioRefeicao, dataPedido, horasDeAntecedencia);
        return pedido != null && pedido.isEditavelAte(horarioCorte, referencia);
    }

    public void validarEdicaoDentroDaJanela(PedidoAlimento pedido, HorarioRefeicao horarioRefeicao,
            LocalDate dataPedido, int horasDeAntecedencia, LocalDateTime referencia) throws ValidacaoException {
        if (!pedidoEstaDentroDaJanelaDeEdicao(pedido, horarioRefeicao, dataPedido, horasDeAntecedencia, referencia)) {
            throw new ValidacaoException("O pedido esta fora da janela de edicao e nao pode mais ser alterado.");
        }
    }

    public void validarGeracaoAntesDoCorte(HorarioRefeicao horarioRefeicao, LocalDate dataPedido,
            int horasDeAntecedencia, LocalDateTime referencia) throws ValidacaoException {
        LocalDateTime horarioCorte = calcularHorarioCorte(horarioRefeicao, dataPedido, horasDeAntecedencia);
        if (!referencia.isBefore(horarioCorte)) {
            throw new ValidacaoException("A geracao desta refeicao so e permitida ate 2 horas antes do inicio do horario de entrega. Horario de corte: "
                    + horarioCorte.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ".");
        }
    }

    public void validarGeracaoDentroDaJanelaOperacional(HorarioRefeicao horarioRefeicaoAnterior,
            LocalDate dataRefeicaoAnterior,
            HorarioRefeicao horarioRefeicaoAlvo,
            LocalDate dataRefeicaoAlvo,
            int horasDeAntecedencia,
            LocalDateTime referencia) throws ValidacaoException {
        if (horarioRefeicaoAnterior == null || horarioRefeicaoAlvo == null
                || dataRefeicaoAnterior == null || dataRefeicaoAlvo == null || referencia == null) {
            throw new IllegalArgumentException("Dados obrigatorios para validar a janela operacional nao informados.");
        }

        LocalTime horarioInicioAnterior = parseHorarioInicio(horarioRefeicaoAnterior);
        LocalTime horarioCorte = calcularHorarioCorte(horarioRefeicaoAlvo, dataRefeicaoAlvo, horasDeAntecedencia).toLocalTime();
        LocalTime horarioReferencia = referencia.toLocalTime();

        boolean janelaSimples = horarioInicioAnterior.isBefore(horarioCorte);
        boolean permitido;

        if (janelaSimples) {
            permitido = !horarioReferencia.isBefore(horarioInicioAnterior) && horarioReferencia.isBefore(horarioCorte);
        } else {
            permitido = !horarioReferencia.isBefore(horarioInicioAnterior) || horarioReferencia.isBefore(horarioCorte);
        }

        if (!permitido) {
            throw new ValidacaoException("A geracao desta refeicao e permitida a partir de "
                    + horarioInicioAnterior.format(DateTimeFormatter.ofPattern("HH:mm"))
                    + " e antes de "
                    + horarioCorte.format(DateTimeFormatter.ofPattern("HH:mm"))
                    + ". Horario atual considerado: "
                    + horarioReferencia.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ".");
        }
    }

    @Transactional
    public int gerarPedidosAutomaticosPendentes(LocalDateTime referencia) {
        int totalGerados = 0;
        LOGGER.info("Iniciando avaliacao da automacao de pedidos em " + formatarDataHora(referencia));

        for (UnidadePrisional unidadeResumo : unidadePrisionalService.listarUnidadesComContratosAtivo()) {
            try {
                if (!rolloutAlimentacaoService.unidadeHabilitada(unidadeResumo.getId())) {
                    continue;
                }
                if (!agendaPedidoRefeicaoService.unidadeAptaParaAutomacao(unidadeResumo.getId(), referencia)) {
                    LOGGER.info("NAO_GERADO_BLOQUEIO unidade=" + unidadeResumo.getId()
                            + " motivo=Sem horario vigente para todas as refeicoes");
                    continue;
                }
                UnidadePrisional unidade = unidadePrisionalService.buscaPorId(unidadeResumo.getId());
                totalGerados += gerarPedidosAutomaticosDaUnidade(unidade, referencia);
            } catch (Exception e) {
                LOGGER.warning("Falha ao processar automacao da unidade " + unidadeResumo.getId() + ": " + e.getMessage());
            }
        }

        return totalGerados;
    }

    @Transactional
    public int gerarPedidosAutomaticosPendentes() {
        return gerarPedidosAutomaticosPendentes(LocalDateTime.now(ZoneId.of("America/Cuiaba")));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int gerarPedidosAutomaticosDaUnidade(Long unidadeId, LocalDateTime referencia) {
        if (unidadeId == null) {
            return 0;
        }

        UnidadePrisional unidadeResumo;
        try {
            unidadeResumo = unidadePrisionalService.buscaPorId(unidadeId);
        } catch (Exception e) {
            LOGGER.warning("Falha ao buscar unidade para automacao. unidade=" + unidadeId + ", erro=" + e.getMessage());
            return 0;
        }

        if (unidadeResumo == null) {
            return 0;
        }

        if (!rolloutAlimentacaoService.unidadeHabilitada(unidadeResumo.getId())) {
            return 0;
        }

        if (!agendaPedidoRefeicaoService.unidadeAptaParaAutomacao(unidadeResumo.getId(), referencia)) {
            LOGGER.info("NAO_GERADO_BLOQUEIO unidade=" + unidadeResumo.getId()
                    + " motivo=Sem horario vigente para todas as refeicoes");
            return 0;
        }

        return gerarPedidosAutomaticosDaUnidade(unidadeResumo, referencia);
    }

    @Transactional
    public int gerarPedidosAutomaticosDaUnidadeComCatchUp(Long unidadeId, LocalDateTime referencia, int diasRetroativos) {
        if (unidadeId == null || referencia == null) {
            return 0;
        }

        UnidadePrisional unidade = unidadePrisionalService.buscaPorId(unidadeId);
        if (unidade == null || !rolloutAlimentacaoService.unidadeHabilitada(unidade.getId())) {
            return 0;
        }

        if (!agendaPedidoRefeicaoService.unidadeAptaParaAutomacao(unidade.getId(), referencia)) {
            LOGGER.info("NAO_GERADO_BLOQUEIO unidade=" + unidade.getId()
                    + " motivo=Sem horario vigente para todas as refeicoes");
            return 0;
        }

        int dias = Math.max(0, diasRetroativos);
        LocalDateTime inicio = referencia.minusDays(dias);
        List<LocalDateTime> referenciasCatchUp = montarReferenciasCatchUp(unidade.getId(), inicio, referencia);
        int totalGerados = 0;
        PedidoAlimentoService executorTransacional = obterExecutorTransacional();

        for (LocalDateTime referenciaCatchUp : referenciasCatchUp) {
            totalGerados += executorTransacional.gerarPedidosAutomaticosDaUnidade(unidade.getId(), referenciaCatchUp);
        }

        return totalGerados;
    }

    PedidoAlimentoService obterExecutorTransacional() {
        if (sessionContext == null) {
            return this;
        }

        try {
            return sessionContext.getBusinessObject(PedidoAlimentoService.class);
        } catch (Exception e) {
            LOGGER.warning("Nao foi possivel obter proxy transacional do PedidoAlimentoService: " + e.getMessage());
            return this;
        }
    }

    private int gerarPedidosAutomaticosDaUnidade(UnidadePrisional unidade, LocalDateTime referencia) {
        if (unidade == null || unidade.getEstrutura() == null) {
            LOGGER.info("Ignorando unidade sem estrutura configurada para automacao.");
            return 0;
        }

        List<HorarioRefeicao> horariosEntrega = buscarHorariosEntregaVigentes(unidade.getId(), referencia);
        if (horariosEntrega.isEmpty()) {
            LOGGER.info("Unidade " + identificarUnidade(unidade) + " sem horarios de entrega configurados no contrato vigente.");
            return 0;
        }

        Optional<CicloEntregaAtivo> cicloAtivo = localizarCicloEntregaAtivo(horariosEntrega, referencia);
        if (!cicloAtivo.isPresent()) {
            LOGGER.info("Nenhum ciclo de entrega ativo encontrado para automacao. Unidade "
                    + identificarUnidade(unidade) + ", referencia " + formatarDataHora(referencia));
            return 0;
        }

        CicloEntregaAtivo ciclo = cicloAtivo.get();
        return gerarPedidoAutomaticoSeGatilhoAtingido(
                unidade,
                ciclo.horarioAtual,
                ciclo.horarioProximo,
                ciclo.dataEntregaAtual,
                referencia).isPresent() ? 1 : 0;
    }

    private List<LocalDate> datasCandidatas(LocalDate referencia) {
        return Arrays.asList(referencia.minusDays(1), referencia);
    }

    private Optional<CicloEntregaAtivo> localizarCicloEntregaAtivo(List<HorarioRefeicao> horariosEntrega,
            LocalDateTime referencia) {
        CicloEntregaAtivo cicloSelecionado = null;
        LocalDateTime entregaSelecionada = null;

        for (int indiceAtual = 0; indiceAtual < horariosEntrega.size(); indiceAtual++) {
            HorarioRefeicao horarioAtual = horariosEntrega.get(indiceAtual);
            HorarioRefeicao horarioProximo = horariosEntrega.get((indiceAtual + 1) % horariosEntrega.size());

            for (LocalDate dataEntregaAtual : datasCandidatas(referencia.toLocalDate())) {
                LocalDateTime dataHoraEntregaAtual = LocalDateTime.of(dataEntregaAtual, parseHorarioInicio(horarioAtual));
                if (dataHoraEntregaAtual.isAfter(referencia)) {
                    continue;
                }

                LocalDate dataEntregaProxima = determinarDataEntregaProxima(horarioAtual, horarioProximo, dataEntregaAtual);
                LocalDateTime horarioCorte = calcularHorarioCorte(horarioProximo, dataEntregaProxima, HORAS_CORTE_PADRAO);
                if (!referencia.isBefore(horarioCorte)) {
                    continue;
                }

                if (entregaSelecionada == null || dataHoraEntregaAtual.isAfter(entregaSelecionada)) {
                    entregaSelecionada = dataHoraEntregaAtual;
                    cicloSelecionado = new CicloEntregaAtivo(horarioAtual, horarioProximo, dataEntregaAtual);
                }
            }
        }

        return Optional.ofNullable(cicloSelecionado);
    }

    private Optional<PedidoAlimento> gerarPedidoAutomaticoSeGatilhoAtingido(UnidadePrisional unidade,
            HorarioRefeicao horarioEntregaAtual, HorarioRefeicao horarioEntregaProximo,
            LocalDate dataEntregaAtual, LocalDateTime referencia) {
        LocalDateTime dataHoraEntregaAtual = LocalDateTime.of(dataEntregaAtual, parseHorarioInicio(horarioEntregaAtual));
        LocalDate dataEntregaProxima = determinarDataEntregaProxima(horarioEntregaAtual, horarioEntregaProximo, dataEntregaAtual);
        LocalDateTime dataHoraEntregaProxima = LocalDateTime.of(dataEntregaProxima, parseHorarioInicio(horarioEntregaProximo));
        LocalDateTime horarioCorte = calcularHorarioCorte(horarioEntregaProximo, dataEntregaProxima, HORAS_CORTE_PADRAO);
        String contextoAtual = "unidade " + identificarUnidade(unidade)
                + ", refeicao atual " + identificarRefeicao(horarioEntregaAtual);

        if (referencia.isBefore(dataHoraEntregaAtual)) {
            LOGGER.fine("Gatilho ainda nao atingido. " + contextoAtual
                    + ", entrega atual " + formatarDataHora(dataHoraEntregaAtual)
                    + ", referencia " + formatarDataHora(referencia));
            return Optional.empty();
        }

        LOGGER.info("Gatilho de entrega atingido. " + contextoAtual
                + ", entrega atual " + formatarDataHora(dataHoraEntregaAtual)
                + ", referencia " + formatarDataHora(referencia));

        LOGGER.info("Candidato a geracao identificado. " + contextoAtual
                + ", proxima refeicao " + identificarRefeicao(horarioEntregaProximo)
                + ", entrega " + formatarDataHora(dataHoraEntregaProxima)
                + ", corte " + formatarDataHora(horarioCorte));

        if (!referencia.isBefore(horarioCorte)) {
            LOGGER.info("Pedido nao gerado porque o horario de corte ja foi atingido. " + contextoAtual
                    + ", proxima refeicao " + identificarRefeicao(horarioEntregaProximo)
                    + ", corte " + formatarDataHora(horarioCorte)
                    + ", referencia " + formatarDataHora(referencia));
            return Optional.empty();
        }

        return gerarPedidoAutomatico(unidade, horarioEntregaProximo, dataHoraEntregaProxima);
    }

    private LocalDate determinarDataEntregaProxima(HorarioRefeicao horarioEntregaAtual,
            HorarioRefeicao horarioEntregaProximo, LocalDate dataEntregaAtual) {
        LocalTime horarioAtual = parseHorarioInicio(horarioEntregaAtual);
        LocalTime horarioProximo = parseHorarioInicio(horarioEntregaProximo);
        return horarioProximo.isAfter(horarioAtual) ? dataEntregaAtual : dataEntregaAtual.plusDays(1);
    }

    private Optional<PedidoAlimento> gerarPedidoAutomatico(UnidadePrisional unidade, HorarioRefeicao horarioAlvo,
            LocalDateTime dataHoraEntrega) {
        Timestamp dataReferenciaContrato = Timestamp.valueOf(dataHoraEntrega);
        Contrato contrato = contratoService.buscarContratoVigenteEAtivoPorUnidade(unidade.getId(), dataReferenciaContrato);
        String dataPedido = DATA_PEDIDO_FORMATTER.format(dataHoraEntrega);
        Long idTipoRefeicao = horarioAlvo.getTipoRefeicao().getId();

        if (dao.existePedidoAtivoPorTipoRefeicaoEUnidade(dataPedido, idTipoRefeicao, unidade.getId(), contrato.getId())) {
            LOGGER.info("Pedido automatico nao gerado porque ja existe pedido ATIVO para a refeicao na unidade. Unidade "
                    + identificarUnidade(unidade) + ", refeicao " + identificarRefeicao(horarioAlvo)
                    + ", entrega " + dataPedido);
            return Optional.empty();
        }

        if (!cardapioDAO.existeCardapioPara(dataPedido, idTipoRefeicao, contrato.getId(), unidade.getId())) {
            LOGGER.info("Pedido automatico nao gerado por ausencia de cardapio. Unidade "
                    + identificarUnidade(unidade) + ", refeicao " + identificarRefeicao(horarioAlvo)
                    + ", entrega " + dataPedido);
            return Optional.empty();
        }

        try {
            PedidoGerado pedidoGerado = gerarPedido(
                    unidade,
                    contrato,
                    horarioAlvo.getTipoRefeicao(),
                    dataPedido,
                    obterIdUsuarioResponsavel(),
                    ModoGeracaoPedido.AUTOMATICO_ATIVO);
            LOGGER.info("Pedido automatico gerado com sucesso. Unidade " + identificarUnidade(unidade)
                    + ", refeicao " + identificarRefeicao(horarioAlvo)
                    + ", entrega " + dataPedido
                    + ", contrato " + contrato.getNumContrato());
            return Optional.of(pedidoGerado.getPedidoMontado().getPedidoAlimento());
        } catch (ValidacaoException e) {
            LOGGER.info("Pedido automatico nao gerado. Unidade "
                    + identificarUnidade(unidade) + ", refeicao " + identificarRefeicao(horarioAlvo)
                    + ", entrega " + dataPedido + ", motivo: " + e.getMessage());
            return Optional.empty();
        } catch (RuntimeException e) {
            if (isViolacaoUnicidade(e)) {
                LOGGER.info("Pedido automatico nao gerado porque outro processo criou o pedido primeiro. Unidade "
                        + identificarUnidade(unidade) + ", refeicao " + identificarRefeicao(horarioAlvo)
                        + ", entrega " + dataPedido);
                return Optional.empty();
            }
            throw e;
        }
    }

    public PedidoMontado montarPedido(UnidadePrisional unidade, Contrato contrato, TipoRefeicao tipoRefeicao,
            String dataPedido, Long idUsuarioResponsavel) {
        List<PedidoEstruturaDTO> estruturaPedido = estruturaService
                .montarEstruturaPedido(dataPedido, unidade.getEstrutura().getId());
        List<PedidoEstruturaDTO> estruturaNivel2 = estruturaPedido.stream()
                .filter(estrutura -> estrutura.getNivel() == 2)
                .collect(Collectors.toList());
        List<RefeicaoDetalhadaDTO> refeicoesDetalhadas = estruturaService
                .buscaPedidoConsulta(dataPedido, unidade.getEstrutura().getId(), tipoRefeicao);
        List<DetalheItemEstruturaCardapioDTO> itens = DetalheItemEstruturaCardapioDTO
                .combinarPedidos(estruturaNivel2, refeicoesDetalhadas);
        List<DietaEspecialEstruturaDTO> dietas = estruturaService
                .buscarDietasEspeciais(unidade.getEstrutura().getId(), dataPedido);

        LOGGER.info("Consolidacao do pedido. Unidade " + identificarUnidade(unidade)
                + ", refeicao " + identificarRefeicao(tipoRefeicao)
                + ", dataPedido " + dataPedido
                + ", contrato " + (contrato != null ? contrato.getId() : null)
                + ", estruturaRaiz " + (unidade != null && unidade.getEstrutura() != null ? unidade.getEstrutura().getId() : null)
                + ", estruturaPedido " + estruturaPedido.size()
                + ", estruturaNivel2 " + estruturaNivel2.size()
                + ", refeicoesDetalhadas " + refeicoesDetalhadas.size()
                + ", itensConsolidados " + itens.size()
                + ", dietasEspeciais " + dietas.size());

        PedidoAlimento pedidoAlimento = PedidoAlimento.create(
                dataPedido,
                idUsuarioResponsavel,
                unidade.getId(),
                contrato.getId(),
                tipoRefeicao.getId());

        for (DetalheItemEstruturaCardapioDTO itemDTO : itens) {
            pedidoAlimento.getItensPedido().add(ItemPedidoAlimento.newInstance(pedidoAlimento, itemDTO));
        }

        for (DietaEspecialEstruturaDTO dietaDTO : dietas) {
            pedidoAlimento.getDietasEspeciais().add(DietaEspecialPedidoAlimento.newInstance(pedidoAlimento, dietaDTO));
        }

        return new PedidoMontado(pedidoAlimento, itens, dietas);
    }

    public PedidoMontado montarPedidoValidando(UnidadePrisional unidade, Contrato contrato, TipoRefeicao tipoRefeicao,
            String dataPedido, Long idUsuarioResponsavel) throws ValidacaoException {
        if (!cardapioDAO.existeCardapioPara(dataPedido, tipoRefeicao.getId(), contrato.getId(), unidade.getId())) {
            throw new ValidacaoException("Nao existe um cardapio para esse tipo de refeicao.");
        }

        PedidoMontado pedidoMontado = montarPedido(unidade, contrato, tipoRefeicao, dataPedido, idUsuarioResponsavel);
        if (pedidoMontado.getItens().isEmpty()) {
            throw new ValidacaoException("Nao ha itens consolidados para gerar o pedido.");
        }

        return pedidoMontado;
    }

    @Transactional
    public PedidoGerado gerarPedido(UnidadePrisional unidade, Contrato contrato, TipoRefeicao tipoRefeicao,
            String dataPedido, Long idUsuarioResponsavel, ModoGeracaoPedido modoGeracao) throws ValidacaoException {
        LOGGER.info("Gerando pedido pela service. modo " + modoGeracao
                + ", unidade " + identificarUnidade(unidade)
                + ", refeicao " + identificarRefeicao(tipoRefeicao)
                + ", dataPedido " + dataPedido
                + ", contrato " + (contrato != null ? contrato.getId() : null)
                + ", usuario " + idUsuarioResponsavel);

        PedidoMontado pedidoMontado = montarPedidoValidando(unidade, contrato, tipoRefeicao, dataPedido, idUsuarioResponsavel);
        PedidoAlimento pedidoAlimento = pedidoMontado.getPedidoAlimento();
        pedidoAlimento.setOrigem(origemDoModo(modoGeracao));
        boolean substituiuPedidoAtivo = false;

        switch (modoGeracao) {
        case MANUAL_PENDENTE:
            salvarPedidoPendente(pedidoAlimento);
            break;
        case MANUAL_ASSINADO:
            boolean aindaTemAtivo = dao.existePedidoAtivoPorTipoRefeicaoEUnidade(
                    dataPedido,
                    tipoRefeicao.getId(),
                    unidade.getId(),
                    contrato.getId());

            if (aindaTemAtivo) {
                salvarPedidoAssinadoSubstituindo(pedidoAlimento);
                substituiuPedidoAtivo = true;
            } else {
                salvarPrimeiroPedidoAssinado(pedidoAlimento);
            }
            break;
        case AUTOMATICO_ATIVO:
            if (dao.existePedidoAtivoPorTipoRefeicaoEUnidade(
                    dataPedido,
                    tipoRefeicao.getId(),
                    unidade.getId(),
                    contrato.getId())) {
                throw new ValidacaoException("Ja existe pedido ATIVO para a mesma unidade e refeicao.");
            }

            salvarPedidoAutomaticoAtivoSemSubstituirAtivo(pedidoAlimento);
            break;
        default:
            throw new ValidacaoException("Modo de geracao de pedido invalido.");
        }

        return new PedidoGerado(pedidoMontado, substituiuPedidoAtivo);
    }

    private OrigemPedidoAlimento origemDoModo(ModoGeracaoPedido modoGeracao) throws ValidacaoException {
        if (modoGeracao == null) {
            throw new ValidacaoException("Modo de geracao de pedido invalido.");
        }

        switch (modoGeracao) {
        case MANUAL_PENDENTE:
        case MANUAL_ASSINADO:
            return OrigemPedidoAlimento.MANUAL;
        case AUTOMATICO_ATIVO:
            return OrigemPedidoAlimento.AUTOMATICO;
        default:
            throw new ValidacaoException("Modo de geracao de pedido invalido.");
        }
    }

    private void salvarPedidoAutomaticoAtivoSemSubstituirAtivo(PedidoAlimento pedidoAlimento) {
        List<Long> idsPendentes = dao.findPedidosPendentesPorTipoRefeicaoEUnidade(
                pedidoAlimento.getDataPedido(),
                pedidoAlimento.getIdTipoRefeicao(),
                pedidoAlimento.getIdUnidadePrisional(),
                pedidoAlimento.getIdContrato());

        if (!idsPendentes.isEmpty()) {
            LOGGER.info("Inativando " + idsPendentes.size()
                    + " pedido(s) PENDENTE(s) antes da geracao automatica. IDs: " + idsPendentes);
            dao.bulkDeactivateItemPedidoAlimentoByPedidoIds(idsPendentes);
            dao.bulkDeactivateDietaEspecialPedidoAlimentoByPedidoIds(idsPendentes);
            dao.bulkDeactivatePedidoAlimentoByIds(idsPendentes);
        }

        pedidoAlimento.setStatus(Status.ATIVO);
        dao.salvarComFlush(pedidoAlimento);
        LOGGER.info("Pedido automatico ATIVO salvo. ID: " + pedidoAlimento.getId());
    }

    private List<LocalDateTime> montarReferenciasCatchUp(Long unidadeId, LocalDateTime inicio, LocalDateTime fim) {
        List<LocalDateTime> referencias = new ArrayList<>();
        LocalDate data = inicio.toLocalDate();

        while (!data.isAfter(fim.toLocalDate())) {
            List<HorarioRefeicao> horarios = buscarHorariosEntregaVigentes(unidadeId, data.atStartOfDay());
            for (HorarioRefeicao horario : horarios) {
                LocalDateTime entrega = LocalDateTime.of(data, parseHorarioInicio(horario));
                LocalDateTime referenciaGatilho = entrega.plusSeconds(1);
                if (!referenciaGatilho.isBefore(inicio) && !referenciaGatilho.isAfter(fim)) {
                    referencias.add(referenciaGatilho);
                }
            }
            data = data.plusDays(1);
        }

        referencias.sort(Comparator.naturalOrder());
        if (referencias.isEmpty() || !referencias.get(referencias.size() - 1).equals(fim)) {
            referencias.add(fim);
        }
        return referencias;
    }

    boolean isViolacaoUnicidade(Throwable e) {
        Throwable atual = e;
        while (atual != null) {
            if (atual instanceof ConstraintViolationException) {
                ConstraintViolationException constraint = (ConstraintViolationException) atual;
                if (isConstraintPedidoAtivo(constraint.getConstraintName())) {
                    return true;
                }
            }
            String mensagem = atual.getMessage();
            if (isMensagemConstraintPedidoAtivo(mensagem)) {
                return true;
            }
            atual = atual.getCause();
        }
        return e instanceof PersistenceException && isMensagemConstraintPedidoAtivo(e.getMessage());
    }

    private boolean isMensagemConstraintPedidoAtivo(String mensagem) {
        if (mensagem == null) {
            return false;
        }
        String mensagemNormalizada = mensagem.toUpperCase();
        return mensagemNormalizada.contains("ORA-00001")
                && mensagemNormalizada.contains(INDICE_UNICO_PEDIDO_ATIVO);
    }

    private boolean isConstraintPedidoAtivo(String constraintName) {
        return constraintName != null
                && constraintName.toUpperCase().contains(INDICE_UNICO_PEDIDO_ATIVO);
    }

    private Long obterIdUsuarioResponsavel() {
        try {
            if (SecurityUtil.getUsuario() != null && SecurityUtil.getUsuario().getId() > 0) {
                return SecurityUtil.getUsuario().getId();
            }
        } catch (Exception e) {
            LOGGER.info("Usuario logado nao disponivel para automacao. Sera utilizado o usuario do sistema.");
        }

        return USUARIO_SISTEMA;
    }

    private List<HorarioRefeicao> buscarHorariosEntregaVigentes(Long idUnidade, LocalDateTime referencia) {
        try {
            Contrato contrato = contratoService.buscarContratoVigenteEAtivoPorUnidade(idUnidade, Timestamp.valueOf(referencia));
            List<HorarioRefeicao> horarios = new ArrayList<>();
            for (TipoRefeicao tipoRefeicao : tipoRefeicaoService.listarTodos()) {
                HorarioRefeicao horario = contratoService.buscarHorarioEntregaVigentePorUnidade(
                        idUnidade,
                        tipoRefeicao.getId(),
                        Timestamp.valueOf(referencia));
                if (horario == null || horario.getHorarioInicio() == null) {
                    LOGGER.info("Unidade " + idUnidade
                            + " sem horario vigente para a refeicao " + identificarRefeicao(tipoRefeicao)
                            + ". Contrato " + contrato.getId() + ".");
                    return Arrays.asList();
                }
                horarios.add(horario);
            }

            horarios.sort(Comparator.comparing(this::parseHorarioInicio)
                    .thenComparing(h -> h.getTipoRefeicao().getId()));

            if (horarios.isEmpty()) {
                LOGGER.info("Contrato vigente sem horarios de entrega configurados para unidade " + idUnidade + ".");
            }
            return horarios;
        } catch (Exception e) {
            LOGGER.info("Nao foi possivel obter contrato/horarios de entrega para unidade " + idUnidade + ": " + e.getMessage());
            return Arrays.asList();
        }
    }

    private List<HorarioRefeicao> normalizarHorariosPorTipo(List<HorarioRefeicao> horarios, LocalDateTime referencia) {
        if (horarios == null || horarios.isEmpty()) {
            return Arrays.asList();
        }

        Map<Long, HorarioRefeicao> horariosPorTipo = new LinkedHashMap<>();
        for (HorarioRefeicao horario : horarios) {
            if (horario == null || horario.getTipoRefeicao() == null || horario.getTipoRefeicao().getId() == null) {
                continue;
            }
            if (!isHorarioVigenteNaReferencia(horario, referencia)) {
                continue;
            }

            HorarioRefeicao atual = horariosPorTipo.get(horario.getTipoRefeicao().getId());
            if (atual == null || isHorarioMaisRecente(horario, atual)) {
                horariosPorTipo.put(horario.getTipoRefeicao().getId(), horario);
            }
        }

        return horariosPorTipo.values().stream()
                .sorted(Comparator.comparing(this::parseHorarioInicio)
                        .thenComparing(horario -> horario.getTipoRefeicao().getId()))
                .collect(Collectors.toList());
    }

    private boolean isHorarioVigenteNaReferencia(HorarioRefeicao horario, LocalDateTime referencia) {
        if (horario == null || referencia == null) {
            return false;
        }

        LocalDate dataReferencia = referencia.toLocalDate();
        LocalDate dataInicio = toLocalDate(horario.getDataInicio());
        LocalDate dataFim = toLocalDate(horario.getDataFim());

        boolean inicioValido = dataInicio == null || !dataReferencia.isBefore(dataInicio);
        boolean fimValido = dataFim == null || !dataReferencia.isAfter(dataFim);
        return inicioValido && fimValido;
    }

    private LocalDate toLocalDate(java.util.Date data) {
        if (data == null) {
            return null;
        }
        return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private boolean isHorarioMaisRecente(HorarioRefeicao candidato, HorarioRefeicao atual) {
        if (candidato.getDataInicio() != null && atual.getDataInicio() != null) {
            int comparacao = candidato.getDataInicio().compareTo(atual.getDataInicio());
            if (comparacao != 0) {
                return comparacao > 0;
            }
        } else if (candidato.getDataInicio() != null) {
            return true;
        } else if (atual.getDataInicio() != null) {
            return false;
        }

        return candidato.getId() > atual.getId();
    }

    private String identificarUnidade(UnidadePrisional unidade) {
        if (unidade == null) {
            return "desconhecida";
        }

        return unidade.getId() + (unidade.getNome() != null ? " - " + unidade.getNome() : "");
    }

    private String identificarRefeicao(HorarioRefeicao horario) {
        if (horario == null || horario.getTipoRefeicao() == null) {
            return "desconhecida";
        }

        TipoRefeicao tipoRefeicao = horario.getTipoRefeicao();
        return tipoRefeicao.getId() + (tipoRefeicao.getDescricao() != null ? " - " + tipoRefeicao.getDescricao() : "");
    }

    private String identificarRefeicao(TipoRefeicao tipoRefeicao) {
        if (tipoRefeicao == null) {
            return "desconhecida";
        }

        return tipoRefeicao.getId() + (tipoRefeicao.getDescricao() != null ? " - " + tipoRefeicao.getDescricao() : "");
    }

    private String formatarDataHora(LocalDateTime dataHora) {
        return dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private LocalTime parseHorarioInicio(HorarioRefeicao horarioRefeicao) {
        return LocalTime.parse(horarioRefeicao.getHorarioInicio(), HORARIO_REFEICAO_FORMATTER);
    }

}
