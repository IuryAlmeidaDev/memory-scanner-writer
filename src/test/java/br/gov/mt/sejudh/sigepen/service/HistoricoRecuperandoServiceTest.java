package br.gov.mt.sejudh.sigepen.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.HistoricoRecuperandoDAO;
import br.gov.mt.sejudh.sigepen.dao.PassagemDAO;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.HistoricoRecuperando;
import br.gov.mt.sejudh.sigepen.model.Recuperando;

public class HistoricoRecuperandoServiceTest extends BaseUnitTest {

    @Mock
    private HistoricoRecuperandoDAO dao;

    @Mock
    private PassagemDAO passagemDAO;

    @Spy
    @InjectMocks
    private HistoricoRecuperandoService service;

    @Test
    public void salvar_quandoDataRegistroAnteriorAEntradaDaPassagemAtual_deveLancarValidacaoException()
            throws Exception {
        Date dataEntrada = data(2026, Calendar.MAY, 7, 10, 0);
        HistoricoRecuperando historico = novoHistorico(123L, data(2026, Calendar.MAY, 7, 9, 59));
        when(passagemDAO.buscarDataEntradaPassagemAtual(123L)).thenReturn(Optional.of(dataEntrada));

        assertThatThrownBy(() -> service.salvar(historico))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("não pode ser anterior");

        verify(dao, never()).salvar(any(HistoricoRecuperando.class));
    }

    @Test
    public void salvar_quandoDataRegistroIgualAEntradaDaPassagemAtual_deveSalvar() throws Exception {
        Date dataEntrada = data(2026, Calendar.MAY, 7, 10, 0);
        HistoricoRecuperando historico = novoHistorico(123L, dataEntrada);
        when(passagemDAO.buscarDataEntradaPassagemAtual(123L)).thenReturn(Optional.of(dataEntrada));

        service.salvar(historico);

        verify(dao).salvar(historico);
    }

    @Test
    public void salvar_quandoDataRegistroPosteriorAEntradaDaPassagemAtual_deveSalvar() throws Exception {
        Date dataEntrada = data(2026, Calendar.MAY, 7, 10, 0);
        HistoricoRecuperando historico = novoHistorico(123L, data(2026, Calendar.MAY, 7, 10, 1));
        when(passagemDAO.buscarDataEntradaPassagemAtual(123L)).thenReturn(Optional.of(dataEntrada));

        service.salvar(historico);

        verify(dao).salvar(historico);
    }

    @Test
    public void salvar_quandoHistoricoNaoTemRecuperando_deveLancarValidacaoException() {
        HistoricoRecuperando historico = new HistoricoRecuperando();
        historico.setDataRegistro(data(2026, Calendar.MAY, 7, 10, 0));

        assertThatThrownBy(() -> service.salvar(historico))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Recuperando");

        verify(passagemDAO, never()).buscarDataEntradaPassagemAtual(any(Long.class));
        verify(dao, never()).salvar(any(HistoricoRecuperando.class));
    }

    @Test
    public void salvar_quandoHistoricoNaoTemDataRegistro_deveLancarValidacaoException() {
        HistoricoRecuperando historico = novoHistorico(123L, null);

        assertThatThrownBy(() -> service.salvar(historico))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Data");

        verify(passagemDAO, never()).buscarDataEntradaPassagemAtual(any(Long.class));
        verify(dao, never()).salvar(any(HistoricoRecuperando.class));
    }

    @Test
    public void salvar_quandoRecuperandoNaoTemPassagemAtual_deveLancarValidacaoException() {
        HistoricoRecuperando historico = novoHistorico(123L, data(2026, Calendar.MAY, 7, 10, 0));
        when(passagemDAO.buscarDataEntradaPassagemAtual(123L)).thenReturn(Optional.<Date>empty());

        assertThatThrownBy(() -> service.salvar(historico))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("passagem atual");

        verify(dao, never()).salvar(any(HistoricoRecuperando.class));
    }

    @Test
    public void removerHistoricoRecuperando_quandoUsuarioNaoTemPermissao_deveLancarValidacaoExceptionENaoRemover() {
        doReturn(false).when(service).possuiPermissaoExcluirHistorico();

        assertThatThrownBy(() -> service.removerHistoricoRecuperando(123L, 456L))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("sem permiss");

        verify(dao, never()).removerHistoricoRecuperando(any(Long.class), any(Long.class));
    }

    @Test
    public void removerHistoricoRecuperando_quandoUsuarioTemPermissao_deveRemover() throws Exception {
        doReturn(true).when(service).possuiPermissaoExcluirHistorico();

        service.removerHistoricoRecuperando(123L, 456L);

        verify(dao).removerHistoricoRecuperando(123L, 456L);
    }

    private HistoricoRecuperando novoHistorico(long recuperandoId, Date dataRegistro) {
        HistoricoRecuperando historico = new HistoricoRecuperando();
        historico.setRecuperando(new Recuperando(recuperandoId));
        historico.setDataRegistro(dataRegistro);
        return historico;
    }

    private Date data(int ano, int mes, int dia, int hora, int minuto) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, ano);
        calendar.set(Calendar.MONTH, mes);
        calendar.set(Calendar.DAY_OF_MONTH, dia);
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
