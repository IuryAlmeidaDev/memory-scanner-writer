package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import org.junit.Test;
import org.mockito.Mock;

import javax.ejb.Asynchronous;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class ProcessadorPedidoAlimentacaoAsyncTest extends BaseUnitTest {

    private final ProcessadorPedidoAlimentacaoAsync processador = new ProcessadorPedidoAlimentacaoAsync();

    @Mock
    private PedidoAlimentoService pedidoAlimentoService;

    @Test
    public void processarUnidade_deveDelegarParaService() throws Exception {
        setField("pedidoAlimentoService", pedidoAlimentoService);
        LocalDateTime referencia = LocalDateTime.of(2026, 5, 8, 6, 0);

        processador.processarUnidade(10L, referencia);

        verify(pedidoAlimentoService).gerarPedidosAutomaticosDaUnidade(10L, referencia);
    }

    @Test
    public void processarCatchUpUnidade_deveDelegarParaServiceComDiasRetroativos() throws Exception {
        setField("pedidoAlimentoService", pedidoAlimentoService);
        LocalDateTime referencia = LocalDateTime.of(2026, 5, 8, 6, 0);

        processador.processarCatchUpUnidade(10L, referencia, 2);

        verify(pedidoAlimentoService).gerarPedidosAutomaticosDaUnidadeComCatchUp(10L, referencia, 2);
    }

    @Test
    public void processarUnidade_deveCapturarExcecaoDoService() throws Exception {
        setField("pedidoAlimentoService", pedidoAlimentoService);
        LocalDateTime referencia = LocalDateTime.of(2026, 5, 8, 6, 0);
        doThrow(new RuntimeException("falha")).when(pedidoAlimentoService)
                .gerarPedidosAutomaticosDaUnidade(10L, referencia);

        assertThatCode(() -> processador.processarUnidade(10L, referencia))
                .doesNotThrowAnyException();
    }

    @Test
    public void processarCatchUpUnidade_deveCapturarExcecaoDoService() throws Exception {
        setField("pedidoAlimentoService", pedidoAlimentoService);
        LocalDateTime referencia = LocalDateTime.of(2026, 5, 8, 6, 0);
        doThrow(new RuntimeException("falha")).when(pedidoAlimentoService)
                .gerarPedidosAutomaticosDaUnidadeComCatchUp(10L, referencia, 2);

        assertThatCode(() -> processador.processarCatchUpUnidade(10L, referencia, 2))
                .doesNotThrowAnyException();
    }

    @Test
    public void metodosPublicos_deveExecutarComAsynchronous() throws Exception {
        assertThat(ProcessadorPedidoAlimentacaoAsync.class
                .getMethod("processarUnidade", Long.class, LocalDateTime.class)
                .getAnnotation(Asynchronous.class)).isNotNull();
        assertThat(ProcessadorPedidoAlimentacaoAsync.class
                .getMethod("processarCatchUpUnidade", Long.class, LocalDateTime.class, int.class)
                .getAnnotation(Asynchronous.class)).isNotNull();
    }

    private void setField(String nomeCampo, Object valor) throws Exception {
        Field field = ProcessadorPedidoAlimentacaoAsync.class.getDeclaredField(nomeCampo);
        field.setAccessible(true);
        field.set(processador, valor);
    }
}
