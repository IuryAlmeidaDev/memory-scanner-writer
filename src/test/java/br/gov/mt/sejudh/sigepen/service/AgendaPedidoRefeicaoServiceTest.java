package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class AgendaPedidoRefeicaoServiceTest extends BaseUnitTest {

    private final AgendaPedidoRefeicaoService service = new AgendaPedidoRefeicaoService();

    @Test
    public void deveUsarHorarioDaUnidadeQuandoExistirOverride() {
        HorarioRefeicao unidade = horario("0700");
        HorarioRefeicao contrato = horario("0630");

        HorarioRefeicao vigente = service.resolverHorarioVigente(unidade, contrato);

        assertThat(vigente.getHorarioInicio()).isEqualTo("0700");
    }

    @Test
    public void deveUsarHorarioDoContratoQuandoUnidadeNaoTiverVigente() {
        HorarioRefeicao contrato = horario("0630");

        HorarioRefeicao vigente = service.resolverHorarioVigente(null, contrato);

        assertThat(vigente.getHorarioInicio()).isEqualTo("0630");
    }

    @Test
    public void deveCalcularCorteComDuasHorasDeAntecedencia() {
        HorarioRefeicao proxima = horario("1200");

        LocalDateTime corte = service.calcularCorte(LocalDate.of(2026, 4, 23), proxima);

        assertThat(corte).isEqualTo(LocalDateTime.of(2026, 4, 23, 10, 0));
    }

    @Test
    public void deveCalcularTempoRestanteAteCorte() {
        String tempo = service.calcularTempoRestanteAteCorte(
                LocalDateTime.of(2026, 4, 23, 10, 0),
                LocalDateTime.of(2026, 4, 23, 8, 35));

        assertThat(tempo).isEqualTo("1h 25m");
    }

    @Test
    public void deveRetornarEncerradoQuandoPassarDoCorte() {
        String tempo = service.calcularTempoRestanteAteCorte(
                LocalDateTime.of(2026, 4, 23, 10, 0),
                LocalDateTime.of(2026, 4, 23, 10, 0));

        assertThat(tempo).isEqualTo("Encerrado");
    }

    @Test
    public void deveRetornarTracoQuandoNaoHouverCorte() {
        String tempo = service.calcularTempoRestanteAteCorte(null, LocalDateTime.now());

        assertThat(tempo).isEqualTo("--");
    }

    private HorarioRefeicao horario(String inicio) {
        HorarioRefeicao horario = new HorarioRefeicao();
        horario.setHorarioInicio(inicio);
        TipoRefeicao tipo = new TipoRefeicao();
        tipo.setDescricao("REF");
        horario.setTipoRefeicao(tipo);
        return horario;
    }
}
