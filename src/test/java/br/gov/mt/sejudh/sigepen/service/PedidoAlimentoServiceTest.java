package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PedidoAlimentoServiceTest extends BaseUnitTest {

    private final PedidoAlimentoService service = new PedidoAlimentoService();

    @Test
    public void devePermitirQuandoReferenciaForNoInicioDaRefeicaoAnterior() throws Exception {
        HorarioRefeicao cafe = horario("0700", "Cafe");
        HorarioRefeicao almoco = horario("1200", "Almoco");

        service.validarGeracaoDentroDaJanelaOperacional(
                cafe,
                LocalDate.of(2026, 4, 24),
                almoco,
                LocalDate.of(2026, 4, 24),
                2,
                LocalDateTime.of(2026, 4, 24, 7, 0));
    }

    @Test
    public void devePermitirQuandoJanelaForCircularNoInicioDaRefeicaoAnterior() throws Exception {
        HorarioRefeicao refeicaoAnterior = horario("1200", "Cafe");
        HorarioRefeicao refeicaoAlvo = horario("1200", "Almoco");

        service.validarGeracaoDentroDaJanelaOperacional(
                refeicaoAnterior,
                LocalDate.of(2026, 4, 24),
                refeicaoAlvo,
                LocalDate.of(2026, 4, 24),
                2,
                LocalDateTime.of(2026, 4, 24, 12, 0));
    }

    @Test
    public void devePermitirQuandoJanelaForCircularUmMinutoAntesDoCorte() throws Exception {
        HorarioRefeicao refeicaoAnterior = horario("1200", "Cafe");
        HorarioRefeicao refeicaoAlvo = horario("1200", "Almoco");

        service.validarGeracaoDentroDaJanelaOperacional(
                refeicaoAnterior,
                LocalDate.of(2026, 4, 24),
                refeicaoAlvo,
                LocalDate.of(2026, 4, 24),
                2,
                LocalDateTime.of(2026, 4, 24, 9, 59));
    }

    @Test(expected = ValidacaoException.class)
    public void deveBloquearQuandoReferenciaForNoHorarioDeCorte() throws Exception {
        HorarioRefeicao cafe = horario("0700", "Cafe");
        HorarioRefeicao almoco = horario("1200", "Almoco");

        service.validarGeracaoDentroDaJanelaOperacional(
                cafe,
                LocalDate.of(2026, 4, 24),
                almoco,
                LocalDate.of(2026, 4, 24),
                2,
                LocalDateTime.of(2026, 4, 24, 10, 0));
    }

    @Test(expected = ValidacaoException.class)
    public void deveBloquearQuandoJanelaForCircularNoHorarioDeCorte() throws Exception {
        HorarioRefeicao refeicaoAnterior = horario("1200", "Cafe");
        HorarioRefeicao refeicaoAlvo = horario("1200", "Almoco");

        service.validarGeracaoDentroDaJanelaOperacional(
                refeicaoAnterior,
                LocalDate.of(2026, 4, 24),
                refeicaoAlvo,
                LocalDate.of(2026, 4, 24),
                2,
                LocalDateTime.of(2026, 4, 24, 10, 0));
    }

    @Test(expected = ValidacaoException.class)
    public void deveBloquearQuandoReferenciaForAntesDaRefeicaoAnterior() throws Exception {
        HorarioRefeicao cafe = horario("0700", "Cafe");
        HorarioRefeicao almoco = horario("1200", "Almoco");

        service.validarGeracaoDentroDaJanelaOperacional(
                cafe,
                LocalDate.of(2026, 4, 24),
                almoco,
                LocalDate.of(2026, 4, 24),
                2,
                LocalDateTime.of(2026, 4, 24, 6, 59));
    }

    private HorarioRefeicao horario(String inicio, String descricao) {
        HorarioRefeicao horario = new HorarioRefeicao();
        horario.setHorarioInicio(inicio);
        TipoRefeicao tipo = new TipoRefeicao();
        tipo.setDescricao(descricao);
        horario.setTipoRefeicao(tipo);
        return horario;
    }
}
