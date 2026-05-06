package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.EntradaSaidaDAO;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.EntradaSaida;
import br.gov.mt.sejudh.sigepen.model.EntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EntradaSaidaServiceTest extends BaseUnitTest {

    private static final String MENSAGEM_LIMITE_EXCEDIDO = "Limite de visitas para este PPL ja foi excedido para o dia de hoje.";

    @InjectMocks
    private EntradaSaidaService service;

    @Mock
    private EntradaSaidaDAO dao;

    @Test
    public void iniciarVisita_quandoVisitanteAdultoELimiteAtingido_deveBloquearEntrada() {
        EntradaSaida entrada = entrada(2, nascimentoAdulto(), 10L);
        when(dao.contarVisitantesAdultosPorRecuperandoNoDia(eq(1L), eq(10L), any(Date.class), any(Date.class), any(Date.class), isNull(Long.class))).thenReturn(2L);

        Throwable throwable = catchThrowable(() -> service.iniciarVisita(entrada));

        assertThat(throwable).isInstanceOf(ValidacaoException.class).hasMessage(MENSAGEM_LIMITE_EXCEDIDO);
        verify(dao).bloquearRecuperandoParaControleVisita(10L);
        verify(dao, never()).salvar(any(EntradaSaida.class));
    }

    @Test
    public void iniciarVisita_quandoVisitanteAdultoETotalAbaixoDoLimite_deveSalvarEntrada() throws ValidacaoException {
        EntradaSaida entrada = entrada(2, nascimentoAdulto(), 10L);
        when(dao.contarVisitantesAdultosPorRecuperandoNoDia(eq(1L), eq(10L), any(Date.class), any(Date.class), any(Date.class), isNull(Long.class))).thenReturn(1L);
        when(dao.salvar(entrada)).thenReturn(entrada);

        EntradaSaida retorno = service.iniciarVisita(entrada);

        assertThat(retorno).isSameAs(entrada);
        verify(dao).bloquearRecuperandoParaControleVisita(10L);
        verify(dao).salvar(entrada);
    }

    @Test
    public void iniciarVisita_quandoVisitanteMenorDeIdade_naoDeveAplicarLimite() throws ValidacaoException {
        EntradaSaida entrada = entrada(1, nascimentoMenor(), 10L);
        when(dao.salvar(entrada)).thenReturn(entrada);

        EntradaSaida retorno = service.iniciarVisita(entrada);

        assertThat(retorno).isSameAs(entrada);
        verify(dao, never()).bloquearRecuperandoParaControleVisita(any(Long.class));
        verify(dao, never()).contarVisitantesAdultosPorRecuperandoNoDia(any(Long.class), any(Long.class), any(Date.class), any(Date.class), any(Date.class), any(Long.class));
        verify(dao).salvar(entrada);
    }

    @Test
    public void iniciarVisita_quandoVisitanteCompleta18Anos_deveAplicarLimite() {
        EntradaSaida entrada = entrada(1, nascimentoCom18Anos(), 10L);
        when(dao.contarVisitantesAdultosPorRecuperandoNoDia(eq(1L), eq(10L), any(Date.class), any(Date.class), any(Date.class), isNull(Long.class))).thenReturn(1L);

        Throwable throwable = catchThrowable(() -> service.iniciarVisita(entrada));

        assertThat(throwable).isInstanceOf(ValidacaoException.class).hasMessage(MENSAGEM_LIMITE_EXCEDIDO);
        verify(dao).bloquearRecuperandoParaControleVisita(10L);
        verify(dao).contarVisitantesAdultosPorRecuperandoNoDia(eq(1L), eq(10L), any(Date.class), any(Date.class), any(Date.class), isNull(Long.class));
        verify(dao, never()).salvar(any(EntradaSaida.class));
    }

    @Test
    public void iniciarVisita_quandoVisitanteSemDataNascimento_naoDeveAplicarLimite() throws ValidacaoException {
        EntradaSaida entrada = entrada(1, null, 10L);
        when(dao.salvar(entrada)).thenReturn(entrada);

        EntradaSaida retorno = service.iniciarVisita(entrada);

        assertThat(retorno).isSameAs(entrada);
        verify(dao, never()).bloquearRecuperandoParaControleVisita(any(Long.class));
        verify(dao, never()).contarVisitantesAdultosPorRecuperandoNoDia(any(Long.class), any(Long.class), any(Date.class), any(Date.class), any(Date.class), any(Long.class));
        verify(dao).salvar(entrada);
    }

    @Test
    public void iniciarVisita_quandoLimiteNulo_deveSalvarSemContar() throws ValidacaoException {
        EntradaSaida entrada = entrada(null, nascimentoAdulto(), 10L);
        when(dao.salvar(entrada)).thenReturn(entrada);

        EntradaSaida retorno = service.iniciarVisita(entrada);

        assertThat(retorno).isSameAs(entrada);
        verify(dao, never()).contarVisitantesAdultosPorRecuperandoNoDia(any(Long.class), any(Long.class), any(Date.class), any(Date.class), any(Date.class), any(Long.class));
        verify(dao).salvar(entrada);
    }

    @Test
    public void iniciarVisita_quandoLimiteZero_deveSalvarSemContar() throws ValidacaoException {
        EntradaSaida entrada = entrada(0, nascimentoAdulto(), 10L);
        when(dao.salvar(entrada)).thenReturn(entrada);

        EntradaSaida retorno = service.iniciarVisita(entrada);

        assertThat(retorno).isSameAs(entrada);
        verify(dao, never()).contarVisitantesAdultosPorRecuperandoNoDia(any(Long.class), any(Long.class), any(Date.class), any(Date.class), any(Date.class), any(Long.class));
        verify(dao).salvar(entrada);
    }

    @Test
    public void iniciarVisita_quandoMultiplosRecuperandosEUmAtingeLimite_deveBloquearEntrada() {
        EntradaSaida entrada = entrada(2, nascimentoAdulto(), 10L, 20L);
        when(dao.contarVisitantesAdultosPorRecuperandoNoDia(eq(1L), eq(10L), any(Date.class), any(Date.class), any(Date.class), isNull(Long.class))).thenReturn(1L);
        when(dao.contarVisitantesAdultosPorRecuperandoNoDia(eq(1L), eq(20L), any(Date.class), any(Date.class), any(Date.class), isNull(Long.class))).thenReturn(2L);

        Throwable throwable = catchThrowable(() -> service.iniciarVisita(entrada));

        assertThat(throwable).isInstanceOf(ValidacaoException.class).hasMessage(MENSAGEM_LIMITE_EXCEDIDO);
        verify(dao).bloquearRecuperandoParaControleVisita(10L);
        verify(dao).bloquearRecuperandoParaControleVisita(20L);
        verify(dao, never()).salvar(any(EntradaSaida.class));
    }

    @Test
    public void iniciarVisita_quandoEntradaSemRecuperando_deveSalvarSemAplicarLimite() throws ValidacaoException {
        EntradaSaida entrada = entrada(1, nascimentoAdulto());
        entrada.setRecuperandos(Arrays.asList(new EntradaSaidaRecuperando()));
        when(dao.salvar(entrada)).thenReturn(entrada);

        EntradaSaida retorno = service.iniciarVisita(entrada);

        assertThat(retorno).isSameAs(entrada);
        verify(dao, never()).bloquearRecuperandoParaControleVisita(any(Long.class));
        verify(dao, never()).contarVisitantesAdultosPorRecuperandoNoDia(any(Long.class), any(Long.class), any(Date.class), any(Date.class), any(Date.class), any(Long.class));
        verify(dao).salvar(entrada);
    }

    @Test
    public void iniciarVisita_quandoUnidadeDaSessaoEstaSemLimiteMasBancoPossuiLimite_deveBloquearEntrada() {
        EntradaSaida entrada = entrada(null, nascimentoAdulto(), 10L);
        UnidadePrisional unidadeBanco = new UnidadePrisional(1L);
        unidadeBanco.setQuantidadeVisitaPorRecuperando(2);
        when(dao.buscarUnidadePrisionalParaControleVisita(1L)).thenReturn(unidadeBanco);
        when(dao.contarVisitantesAdultosPorRecuperandoNoDia(eq(1L), eq(10L), any(Date.class), any(Date.class), any(Date.class), isNull(Long.class))).thenReturn(2L);

        Throwable throwable = catchThrowable(() -> service.iniciarVisita(entrada));

        assertThat(throwable).isInstanceOf(ValidacaoException.class).hasMessage(MENSAGEM_LIMITE_EXCEDIDO);
        verify(dao, never()).salvar(any(EntradaSaida.class));
    }

    @Test
    public void alterarVisita_quandoLimiteAtingidoPorOutrosRegistros_deveBloquearAlteracao() {
        EntradaSaida entrada = entrada(2, nascimentoAdulto(), 10L);
        entrada.setId(99L);
        when(dao.contarVisitantesAdultosPorRecuperandoNoDia(eq(1L), eq(10L), any(Date.class), any(Date.class), any(Date.class), eq(99L))).thenReturn(2L);

        Throwable throwable = catchThrowable(() -> service.alterarVisita(entrada));

        assertThat(throwable).isInstanceOf(ValidacaoException.class).hasMessage(MENSAGEM_LIMITE_EXCEDIDO);
        verify(dao).bloquearRecuperandoParaControleVisita(10L);
        verify(dao, never()).salvar(any(EntradaSaida.class));
    }

    @Test
    public void alterarVisita_quandoTotalAbaixoDoLimiteIgnorandoEntradaAtual_deveSalvarAlteracao() throws ValidacaoException {
        EntradaSaida entrada = entrada(2, nascimentoAdulto(), 10L);
        entrada.setId(99L);
        when(dao.contarVisitantesAdultosPorRecuperandoNoDia(eq(1L), eq(10L), any(Date.class), any(Date.class), any(Date.class), eq(99L))).thenReturn(1L);
        when(dao.salvar(entrada)).thenReturn(entrada);

        EntradaSaida retorno = service.alterarVisita(entrada);

        assertThat(retorno).isSameAs(entrada);
        verify(dao).bloquearRecuperandoParaControleVisita(10L);
        verify(dao).salvar(entrada);
    }

    private EntradaSaida entrada(Integer limite, Date dataNascimento, Long... recuperandoIds) {
        UnidadePrisional unidade = new UnidadePrisional(1L);
        unidade.setQuantidadeVisitaPorRecuperando(limite);
        when(dao.buscarUnidadePrisionalParaControleVisita(1L)).thenReturn(unidade);

        Pessoa pessoa = new Pessoa();
        pessoa.setDataNascimento(dataNascimento);

        Visitante visitante = new Visitante();
        visitante.setPessoa(pessoa);

        EntradaSaida entrada = new EntradaSaida();
        entrada.setUnidadePrisional(unidade);
        entrada.setVisitante(visitante);
        entrada.setRecuperandos(new ArrayList<EntradaSaidaRecuperando>());

        for (Long recuperandoId : recuperandoIds) {
            entrada.getRecuperandos().add(entradaRecuperando(recuperandoId));
        }

        return entrada;
    }

    private EntradaSaidaRecuperando entradaRecuperando(Long recuperandoId) {
        Recuperando recuperando = new Recuperando();
        recuperando.setId(recuperandoId);

        Credenciamento credenciamento = new Credenciamento();
        credenciamento.setRecuperando(recuperando);

        EntradaSaidaRecuperando entradaRecuperando = new EntradaSaidaRecuperando();
        entradaRecuperando.setCredenciamento(credenciamento);
        return entradaRecuperando;
    }

    private Date nascimentoAdulto() {
        return anosAtras(19);
    }

    private Date nascimentoMenor() {
        return anosAtras(17);
    }

    private Date nascimentoCom18Anos() {
        return anosAtras(18);
    }

    private Date anosAtras(int anos) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -anos);
        return calendar.getTime();
    }
}
