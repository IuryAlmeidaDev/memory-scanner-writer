package br.gov.mt.sejudh.sigepen.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;

public class RecuperandoTest extends BaseUnitTest {

    @Test
    public void construtorPadrao_deveInicializarHistoricos() {
        Recuperando recuperando = new Recuperando();

        assertThat(recuperando.getHistoricos()).isNotNull();
        assertThat(recuperando.getHistoricos()).isEmpty();
    }

    @Test
    public void construtorComStatus_deveInicializarHistoricos() {
        Recuperando recuperando = new Recuperando(StatusRecuperando.INATIVO_PARCIAL);

        assertThat(recuperando.getHistoricos()).isNotNull();
        assertThat(recuperando.getHistoricos()).isEmpty();
    }

    @Test
    public void setHistoricos_devePreservarReferenciaAtualDaLista() {
        Recuperando recuperando = new Recuperando();
        List<HistoricoRecuperando> referenciaAtual = recuperando.getHistoricos();
        List<HistoricoRecuperando> historicos = Arrays.asList(new HistoricoRecuperando());

        recuperando.setHistoricos(historicos);

        assertThat(recuperando.getHistoricos()).isSameAs(referenciaAtual);
    }

    @Test
    public void setHistoricos_deveAdicionarItensRecebidosEAjustarRecuperandoNoHistorico() {
        Recuperando recuperando = new Recuperando();
        HistoricoRecuperando primeiroHistorico = new HistoricoRecuperando();
        HistoricoRecuperando segundoHistorico = new HistoricoRecuperando();

        recuperando.setHistoricos(Arrays.asList(primeiroHistorico, null, segundoHistorico));

        assertThat(recuperando.getHistoricos())
                .containsExactly(primeiroHistorico, segundoHistorico);
        assertThat(primeiroHistorico.getRecuperando()).isSameAs(recuperando);
        assertThat(segundoHistorico.getRecuperando()).isSameAs(recuperando);
    }

    @Test
    public void setHistoricos_comNullNaoDeveLimparListaExistente() {
        Recuperando recuperando = new Recuperando();
        HistoricoRecuperando historicoExistente = new HistoricoRecuperando();
        recuperando.getHistoricos().add(historicoExistente);

        recuperando.setHistoricos(null);

        assertThat(recuperando.getHistoricos()).containsExactly(historicoExistente);
    }

    @Test
    public void setHistoricos_comNullEListaAtualNullDeveInicializarListaVazia() {
        Recuperando recuperando = new Recuperando(new Pessoa());

        recuperando.setHistoricos(null);

        assertThat(recuperando.getHistoricos()).isNotNull();
        assertThat(recuperando.getHistoricos()).isEmpty();
    }

    @Test
    public void setHistoricos_comMesmaInstanciaNaoDeveDuplicarNemLimparItens() {
        Recuperando recuperando = new Recuperando();
        List<HistoricoRecuperando> historicos = recuperando.getHistoricos();
        HistoricoRecuperando historicoExistente = new HistoricoRecuperando();
        historicos.add(historicoExistente);

        recuperando.setHistoricos(historicos);

        assertThat(recuperando.getHistoricos()).isSameAs(historicos);
        assertThat(recuperando.getHistoricos()).containsExactly(historicoExistente);
    }

    @Test
    public void setHistoricos_comListaValidaEListaAtualNullDeveInicializarECopiarItens() {
        Recuperando recuperando = new Recuperando(new Pessoa());
        HistoricoRecuperando historico = new HistoricoRecuperando();
        List<HistoricoRecuperando> historicos = new ArrayList<HistoricoRecuperando>();
        historicos.add(historico);

        recuperando.setHistoricos(historicos);

        assertThat(recuperando.getHistoricos()).isNotNull();
        assertThat(recuperando.getHistoricos()).isNotSameAs(historicos);
        assertThat(recuperando.getHistoricos()).containsExactly(historico);
        assertThat(historico.getRecuperando()).isSameAs(recuperando);
    }
}
