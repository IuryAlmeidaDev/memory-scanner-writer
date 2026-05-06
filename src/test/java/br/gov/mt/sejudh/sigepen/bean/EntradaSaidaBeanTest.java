package br.gov.mt.sejudh.sigepen.bean;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaSaida;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoVisita;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.EntradaSaida;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.service.EntradaSaidaService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import javax.faces.application.FacesMessage;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EntradaSaidaBeanTest extends BaseUnitTest {

    private static final String MENSAGEM_LIMITE_EXCEDIDO = "Limite de visitas para este PPL ja foi excedido para o dia de hoje.";

    @Mock
    private EntradaSaidaService service;

    @Mock
    private FacesUtil facesUtil;

    @Spy
    @InjectMocks
    private EntradaSaidaBean bean;

    private UnidadePrisional unidadeSelecionada;

    @Before
    public void isolarDependenciasEstaticas() {
        unidadeSelecionada = new UnidadePrisional(1L);
        doReturn(unidadeSelecionada).when(bean).getUnidadeSelecionadaAtual();
    }

    @Test
    public void iniciarVisita_quandoServiceBloquearLimite_deveExibirMensagemENaoAtualizarEntrada() throws Exception {
        EntradaSaida entradaOriginal = entradaComVisitante();
        Credenciamento credenciamento = credenciamentoSelecionado(entradaOriginal.getVisitante());
        bean.setEntrada(entradaOriginal);
        bean.setCredenciamentos(Collections.singletonList(credenciamento));

        when(service.buscarEntradasPorFiltro(any(FiltroEntradaSaida.class), eq(true))).thenReturn(Collections.<EntradaSaida>emptyList());
        when(service.iniciarVisita(any(EntradaSaida.class))).thenThrow(new ValidacaoException(MENSAGEM_LIMITE_EXCEDIDO));

        bean.iniciarVisita();

        verify(service).iniciarVisita(any(EntradaSaida.class));
        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro", MENSAGEM_LIMITE_EXCEDIDO);
        assertThat(bean.getEntrada()).isSameAs(entradaOriginal);
    }

    @Test
    public void iniciarVisita_quandoServiceForChamado_deveMontarEntradaComUnidadeSituacaoERecuperandosSelecionados() throws Exception {
        EntradaSaida entradaOriginal = entradaComVisitante();
        Credenciamento credenciamento = credenciamentoSelecionado(entradaOriginal.getVisitante());
        bean.setEntrada(entradaOriginal);
        bean.setCredenciamentos(Collections.singletonList(credenciamento));

        when(service.buscarEntradasPorFiltro(any(FiltroEntradaSaida.class), eq(true))).thenReturn(Collections.<EntradaSaida>emptyList());
        when(service.iniciarVisita(any(EntradaSaida.class))).thenThrow(new ValidacaoException(MENSAGEM_LIMITE_EXCEDIDO));

        bean.iniciarVisita();

        ArgumentCaptor<EntradaSaida> captor = ArgumentCaptor.forClass(EntradaSaida.class);
        verify(service).iniciarVisita(captor.capture());

        EntradaSaida entradaEnviada = captor.getValue();
        assertThat(entradaEnviada.getUnidadePrisional()).isSameAs(unidadeSelecionada);
        assertThat(entradaEnviada.getVisitante()).isSameAs(entradaOriginal.getVisitante());
        assertThat(entradaEnviada.getSituacao()).isEqualTo(SituacaoVisita.EM_ANDAMENTO);
        assertThat(entradaEnviada.getRecuperandos()).hasSize(1);
        assertThat(entradaEnviada.getRecuperandos().get(0).getCredenciamento()).isSameAs(credenciamento);
    }

    @Test
    public void salvar_quandoServiceBloquearLimite_deveExibirMensagemELiberarNovaTentativa() throws Exception {
        EntradaSaida entradaOriginal = entradaComVisitante();
        entradaOriginal.setRecuperandos(new ArrayList<>());
        Credenciamento credenciamento = credenciamentoSelecionado(entradaOriginal.getVisitante());
        bean.setEntrada(entradaOriginal);
        bean.setCredenciamentos(Collections.singletonList(credenciamento));

        when(service.alterarVisita(any(EntradaSaida.class))).thenThrow(new ValidacaoException(MENSAGEM_LIMITE_EXCEDIDO));

        bean.salvar(entradaOriginal);
        bean.salvar(entradaOriginal);

        verify(service, times(2)).alterarVisita(entradaOriginal);
        verify(facesUtil, times(2)).exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro", MENSAGEM_LIMITE_EXCEDIDO);
        assertThat(entradaOriginal.getRecuperandos()).hasSize(1);
        assertThat(entradaOriginal.getRecuperandos().get(0).getCredenciamento()).isSameAs(credenciamento);
    }

    private EntradaSaida entradaComVisitante() {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(100L);
        pessoa.setNome("Visitante");

        Visitante visitante = new Visitante();
        visitante.setId(200L);
        visitante.setPessoa(pessoa);

        EntradaSaida entrada = new EntradaSaida();
        entrada.setVisitante(visitante);
        return entrada;
    }

    private Credenciamento credenciamentoSelecionado(Visitante visitante) {
        Credenciamento credenciamento = new Credenciamento();
        credenciamento.setSelecionado(true);
        credenciamento.setVisitante(visitante);
        return credenciamento;
    }
}
