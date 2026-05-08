package br.gov.mt.sejudh.sigepen.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.HistoricoRecuperando;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Servidor;
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;
import br.gov.mt.sejudh.sigepen.service.HistoricoRecuperandoService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

public class HistoricoRecuperandoBeanTest extends BaseUnitTest {

    @Mock
    private FacesUtil facesUtil;

    @Mock
    private Logger log;

    @Mock
    private RecuperandoService recuperandoService;

    @Mock
    private HistoricoRecuperandoService historicoRecuperandoService;

    @Mock
    private FileDownloadHandlerBean fileDownload;

    @Spy
    @InjectMocks
    private HistoricoRecuperandoBean bean;

    @Test
    public void listarHistoricoRecuperando_deveBuscarHistoricosPermitidosPeloIdDoRecuperando() {
        Recuperando recuperando = new Recuperando(123L);
        List<HistoricoRecuperando> historicos = Arrays.asList(
                new HistoricoRecuperando(),
                new HistoricoRecuperando());
        bean.setRecuperando(recuperando);
        when(historicoRecuperandoService.listarHistoricoRecuperandoPermitidos(123L))
                .thenReturn(historicos);

        bean.listarHistoricoRecuperando();

        assertThat(bean.getHistoricos()).isSameAs(historicos);
        verify(historicoRecuperandoService).listarHistoricoRecuperandoPermitidos(123L);
    }

    @Test
    public void listarHistoricoRecuperando_deveUsarListaVaziaQuandoRecuperandoForNull() {
        bean.setRecuperando(null);

        bean.listarHistoricoRecuperando();

        assertThat(bean.getHistoricos()).isNotNull();
        assertThat(bean.getHistoricos()).isEmpty();
        verify(historicoRecuperandoService, never()).listarHistoricoRecuperandoPermitidos(anyLong());
    }

    @Test
    public void cadastrarHistoricoRecuperando_deveCarregarRecuperandoListarHistoricosEAssociarHistorico() {
        RecuperandoView recuperandoView = new RecuperandoView();
        recuperandoView.setId(321L);
        Recuperando recuperando = new Recuperando(321L);
        List<HistoricoRecuperando> historicos = Arrays.asList(new HistoricoRecuperando());
        when(recuperandoService.buscarRecuperandoPorId(321L, false)).thenReturn(recuperando);
        when(historicoRecuperandoService.listarHistoricoRecuperandoPermitidos(321L))
                .thenReturn(historicos);
        doNothing().when(bean).registrarVisualizacaoHistorico();

        bean.cadastrarHistoricoRecuperando(recuperandoView);

        assertThat(bean.getRecuperando()).isSameAs(recuperando);
        assertThat(bean.getRecuperandoView()).isSameAs(recuperandoView);
        assertThat(bean.getHistoricos()).isSameAs(historicos);
        assertThat(bean.getHistoricoRecuperando().getRecuperando()).isSameAs(recuperando);
        verify(recuperandoService).buscarRecuperandoPorId(321L, false);
        verify(historicoRecuperandoService).listarHistoricoRecuperandoPermitidos(321L);
        verify(bean).registrarVisualizacaoHistorico();
    }

    @Test
    public void salvar_quandoServiceLancaValidacaoException_deveExibirAvisoEManterFormulario() throws Exception {
        Recuperando recuperando = new Recuperando(123L);
        HistoricoRecuperando historico = new HistoricoRecuperando();
        historico.setDataRegistro(new Date());
        String mensagem = "A data do histórico não pode ser anterior à data de entrada da passagem atual do recuperando.";
        bean.setRecuperando(recuperando);
        bean.setHistoricoRecuperando(historico);
        doReturn(new Servidor()).when(bean).getServidorLogado();
        doThrow(new ValidacaoException(mensagem)).when(historicoRecuperandoService).salvar(historico);

        bean.salvar();

        assertThat(bean.getHistoricoRecuperando()).isSameAs(historico);
        assertThat(historico.getRecuperando()).isSameAs(recuperando);
        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_WARN), eq("ATENÇÃO"), eq(mensagem));
        verify(bean, never()).listarHistoricoRecuperando();
    }

    @Test
    public void removerHistoricoSelecionado_quandoServiceLancaValidacaoException_deveExibirAvisoEManterLista()
            throws Exception {
        HistoricoRecuperando historico = novoHistorico(123L, 456L);
        List<HistoricoRecuperando> historicos = new ArrayList<>(Arrays.asList(historico));
        String mensagem = "Usuário sem permissão para excluir histórico do recuperando.";
        bean.setHistoricoRecuperando(historico);
        bean.setHistoricos(historicos);
        doThrow(new ValidacaoException(mensagem))
                .when(historicoRecuperandoService).removerHistoricoRecuperando(123L, 456L);

        bean.removerHistoricoSelecionado();

        assertThat(bean.getHistoricos()).containsExactly(historico);
        assertThat(bean.getHistoricoRecuperando()).isSameAs(historico);
        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_WARN), eq("ATENÇÃO"), eq(mensagem));
    }

    @Test
    public void removerHistoricoSelecionado_quandoRemoveComSucesso_deveChamarServiceRemoverDaListaEExibirMensagem()
            throws Exception {
        HistoricoRecuperando historico = novoHistorico(123L, 456L);
        List<HistoricoRecuperando> historicos = new ArrayList<>(Arrays.asList(historico));
        bean.setHistoricoRecuperando(historico);
        bean.setHistoricos(historicos);

        bean.removerHistoricoSelecionado();

        assertThat(bean.getHistoricos()).isEmpty();
        assertThat(bean.getHistoricoRecuperando()).isNotSameAs(historico);
        verify(historicoRecuperandoService).removerHistoricoRecuperando(123L, 456L);
        verify(facesUtil).exibirMensagemInfo("Histórico removido com sucesso!", false);
    }

    private HistoricoRecuperando novoHistorico(long recuperandoId, long historicoId) {
        HistoricoRecuperando historico = new HistoricoRecuperando();
        historico.setId(historicoId);
        historico.setRecuperando(new Recuperando(recuperandoId));
        return historico;
    }
}
