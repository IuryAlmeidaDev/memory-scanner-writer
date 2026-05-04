package br.gov.mt.sejudh.sigepen.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.Date;

import javax.enterprise.event.Event;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.CredenciamentoDAO;
import br.gov.mt.sejudh.sigepen.dao.ParenteDAO;
import br.gov.mt.sejudh.sigepen.dao.PassagemDAO;
import br.gov.mt.sejudh.sigepen.dao.PedidoDAO;
import br.gov.mt.sejudh.sigepen.dao.ProcessoDAO;
import br.gov.mt.sejudh.sigepen.dao.RecuperandoDAO;
import br.gov.mt.sejudh.sigepen.dao.SolicitacaoVisitaDAO;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDesligamento;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Estabelecimento;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Regime;

public class PassagemServiceTest extends BaseUnitTest {

    @Mock
    private PassagemDAO dao;
    @Mock
    private PedidoDAO pedTransitoDao;
    @Mock
    private ProcessoDAO processoDao;
    @Mock
    private RecuperandoDAO recuperandoDao;
    @Mock
    private RecuperandoService recuperandoService;
    @Mock
    private CredenciamentoDAO credenciamentoDAO;
    @Mock
    private ParenteDAO parenteDAO;
    @Mock
    private Event<Recuperando> eventoRecuperando;
    @Mock
    private SolicitacaoVisitaDAO solicitacaoVisitaDAO;
    @Mock
    private DietaEspecialService dietaEspecialService;

    @InjectMocks
    private PassagemService service;

    @Test
    public void desligar_porObitoInativaDietasComDataSaida() throws Exception {
        assertDesligamentoInativaDieta(TipoDesligamento.OBITO);
    }

    @Test
    public void desligar_porFugaInativaDietasComDataSaida() throws Exception {
        assertDesligamentoInativaDieta(TipoDesligamento.FUGA);
    }

    @Test
    public void desligar_porAlvaraInativaDietasComDataSaida() throws Exception {
        assertDesligamentoInativaDieta(TipoDesligamento.ALVARA_DE_SOLTURA);
    }

    @Test
    public void desligar_porOutroSistemaInativaDietasComDataSaida() throws Exception {
        assertDesligamentoInativaDieta(TipoDesligamento.OUTRO_SISTEMA_PENITENCIARIO);
    }

    @Test
    public void desligar_recuperandoEmMovimentacao_lancaValidacaoENaoInativaDietas() {
        Recuperando recuperando = new Recuperando();
        recuperando.setId(99L);
        Passagem passagem = new Passagem();
        passagem.setDataSaida(new Date(System.currentTimeMillis() - 60_000));
        PassagemLotacao lotacao = new PassagemLotacao();
        lotacao.setDataEntrada(new Date(System.currentTimeMillis() - 120_000));
        when(pedTransitoDao.recuperandoEmMovimentacao(recuperando)).thenReturn(true);

        assertThatThrownBy(() -> service.desligar(recuperando, passagem, lotacao))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Desligado");

        verify(dietaEspecialService, never()).inativarDietasAtivasOuSuspensasDoRecuperando(any(Long.class), any(Date.class));
        verify(recuperandoDao, never()).salvar(any(Recuperando.class));
    }

    private void assertDesligamentoInativaDieta(TipoDesligamento tipoDesligamento) throws Exception {
        Date dataEntrada = new Date(System.currentTimeMillis() - 120_000);
        Date dataSaida = new Date(System.currentTimeMillis() - 60_000);
        Recuperando recuperando = new Recuperando();
        recuperando.setId(99L);
        recuperando.setStatus(StatusRecuperando.ATIVO);
        recuperando.setRegimeAtual(new Regime(1L, "Aberto"));

        Passagem passagem = new Passagem();
        passagem.setTipoDesligamento(tipoDesligamento);
        passagem.setDataEntrada(dataEntrada);
        passagem.setDataSaida(dataSaida);
        passagem.setRecuperando(recuperando);
        if (TipoDesligamento.OUTRO_SISTEMA_PENITENCIARIO.equals(tipoDesligamento)) {
            passagem.setEstabelecimentoDestino(new Estabelecimento());
        }

        PassagemLotacao lotacao = new PassagemLotacao();
        lotacao.setDataEntrada(dataEntrada);
        passagem.getLotacao().add(lotacao);
        recuperando.getPassagens().add(passagem);

        when(pedTransitoDao.recuperandoEmMovimentacao(recuperando)).thenReturn(false);
        when(parenteDAO.listarPorRecuperando(recuperando)).thenReturn(Collections.emptyList());
        when(processoDao.processosPorRecuperando(99L)).thenReturn(Collections.emptyList());

        service.desligar(recuperando, passagem, lotacao);

        verify(recuperandoDao).salvar(recuperando);
        verify(dietaEspecialService).inativarDietasAtivasOuSuspensasDoRecuperando(eq(99L), eq(dataSaida));
        verify(eventoRecuperando).fire(recuperando);
    }
}
