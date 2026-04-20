package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.dao.CardapioDAO;
import br.gov.mt.sejudh.sigepen.dao.PedidoAlimentoDAO;
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

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        List<Long> idsParaInativar = dao.findPedidosParaInativarAoAssinar(
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
        List<Long> idsEmAberto = dao.findActivePedidoAlimentoIdsByCriteria(
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
            throw new ValidacaoException("O horario de corte para geracao do pedido foi atingido em "
                    + horarioCorte.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ".");
        }
    }

    @Transactional
    public int gerarPedidosAutomaticosPendentes(LocalDateTime referencia) {
        int totalGerados = 0;
        LOGGER.info("Iniciando avaliacao da automacao de pedidos em " + formatarDataHora(referencia));

        for (UnidadePrisional unidadeResumo : unidadePrisionalService.listarUnidadesComContratosAtivo()) {
            try {
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
        return gerarPedidosAutomaticosPendentes(LocalDateTime.now());
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

        if (dao.existePedidoAtivo(dataPedido, idTipoRefeicao, unidade.getId(), contrato.getId())
                || !dao.findActivePedidoAlimentoIdsByCriteria(dataPedido, idTipoRefeicao, unidade.getId(), contrato.getId()).isEmpty()) {
            LOGGER.info("Pedido automatico nao gerado porque ja existe pedido ativo ou em aberto. Unidade "
                    + identificarUnidade(unidade) + ", refeicao " + identificarRefeicao(horarioAlvo)
                    + ", entrega " + dataPedido);
            return Optional.empty();
        }

        if (!cardapioDAO.existeCardapioPara(dataPedido, idTipoRefeicao, contrato.getId())) {
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
        if (!cardapioDAO.existeCardapioPara(dataPedido, tipoRefeicao.getId(), contrato.getId())) {
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
        boolean substituiuPedidoAtivo = false;

        switch (modoGeracao) {
        case MANUAL_PENDENTE:
            salvarPedidoPendente(pedidoAlimento);
            break;
        case MANUAL_ASSINADO:
            boolean aindaTemAtivo = verificarExistenciaPedidoAtivo(
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
            if (!dao.findActivePedidoAlimentoIdsByCriteria(
                    dataPedido,
                    tipoRefeicao.getId(),
                    unidade.getId(),
                    contrato.getId()).isEmpty()) {
                throw new ValidacaoException("Ja existe pedido em aberto para a mesma unidade, refeicao e horario.");
            }

            salvarPrimeiroPedidoAssinado(pedidoAlimento);
            break;
        default:
            throw new ValidacaoException("Modo de geracao de pedido invalido.");
        }

        return new PedidoGerado(pedidoMontado, substituiuPedidoAtivo);
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
            List<HorarioRefeicao> horarios = normalizarHorariosPorTipo(contrato.getHorariosRefeicao());
            if (horarios.isEmpty()) {
                LOGGER.info("Contrato vigente sem horarios de entrega configurados para unidade " + idUnidade + ".");
            }
            return horarios;
        } catch (Exception e) {
            LOGGER.info("Nao foi possivel obter contrato/horarios de entrega para unidade " + idUnidade + ": " + e.getMessage());
            return Arrays.asList();
        }
    }

    private List<HorarioRefeicao> normalizarHorariosPorTipo(List<HorarioRefeicao> horarios) {
        if (horarios == null || horarios.isEmpty()) {
            return Arrays.asList();
        }

        Map<Long, HorarioRefeicao> horariosPorTipo = new LinkedHashMap<>();
        for (HorarioRefeicao horario : horarios) {
            if (horario == null || horario.getTipoRefeicao() == null || horario.getTipoRefeicao().getId() == null) {
                continue;
            }

            HorarioRefeicao atual = horariosPorTipo.get(horario.getTipoRefeicao().getId());
            if (atual == null || isHorarioMaisRecente(horario, atual)) {
                horariosPorTipo.put(horario.getTipoRefeicao().getId(), horario);
            }
        }

        return horariosPorTipo.values().stream()
                .sorted(Comparator.comparing(this::parseHorarioInicio))
                .collect(Collectors.toList());
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
