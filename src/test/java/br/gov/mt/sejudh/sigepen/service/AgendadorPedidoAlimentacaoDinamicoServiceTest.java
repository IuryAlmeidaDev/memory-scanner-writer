package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import org.junit.Test;

import javax.ejb.ScheduleExpression;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class AgendadorPedidoAlimentacaoDinamicoServiceTest extends BaseUnitTest {

    private final AgendadorPedidoAlimentacaoDinamicoService service = new AgendadorPedidoAlimentacaoDinamicoService();

    @Test
    public void deveConfigurarTimezoneDeCuiabaNoScheduleExpression() {
        ScheduleExpression expression = service.criarScheduleExpression(LocalDateTime.of(2026, 4, 24, 16, 59, 0));

        assertThat(expression.getTimezone()).isEqualTo("America/Cuiaba");
    }

    @Test
    public void deveConsiderarDisparoNoFuturoApenasQuandoForDepoisDaReferencia() {
        LocalDateTime referencia = LocalDateTime.of(2026, 4, 24, 16, 59, 0);

        assertThat(service.isDisparoFuturo(referencia.plusSeconds(1), referencia)).isTrue();
        assertThat(service.isDisparoFuturo(referencia, referencia)).isFalse();
        assertThat(service.isDisparoFuturo(referencia.minusSeconds(1), referencia)).isFalse();
    }
}
