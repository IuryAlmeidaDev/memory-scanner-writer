package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.FaseDoCredenciamentoDAO;
import br.gov.mt.sejudh.sigepen.dao.VisitanteDAO;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Usuario;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VisitanteServiceTest extends BaseUnitTest {

    @Spy
    @InjectMocks
    private VisitanteService service;

    @Mock
    private VisitanteDAO visitanteDAO;

    @Mock
    private PessoaService pessoaService;

    @Mock
    private RecuperandoService recuperandoService;

    @Mock
    private FaseDoCredenciamentoDAO faseDoCredenciamentoDAO;

    @Before
    public void prepararService() {
        service.setVisitanteDAO(visitanteDAO);
        doReturn(new UnidadePrisional(1L)).when(service).getUnidadeSelecionadaAtual();

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        doReturn(usuario).when(service).getUsuarioAtual();
    }

    @Test
    public void incluir_quandoCredenciamentoSemTipoVisita_deveLancarValidacao() {
        Visitante visitante = visitante();
        visitante.getCredenciamentos().add(new Credenciamento());

        Throwable throwable = catchThrowable(() -> service.incluir(visitante, null, false, false));

        assertThat(throwable).isInstanceOf(ValidacaoException.class)
                .hasMessage("Informe o tipo de visita do credenciamento.");
        verify(visitanteDAO, never()).salvar(any(Visitante.class));
        verify(pessoaService, never()).salvarPessoaVisitante(any(Pessoa.class));
    }

    @Test
    public void incluir_quandoTipoExigeRecuperandoESemRecuperando_deveLancarValidacaoENaoSalvar() {
        TipoVisita tipoVisita = tipoVisita(SimNao.S, SimNao.N, SimNao.S, TipoEntrada.ATENDIMENTO);
        Visitante visitante = visitante(tipoVisita);
        visitante.getCredenciamentos().add(credenciamento(tipoVisita, null, new UnidadePrisional(10L), null, null));

        Throwable throwable = catchThrowable(() -> service.incluir(visitante, null, false, false));

        assertThat(throwable).isInstanceOf(ValidacaoException.class)
                .hasMessage("Selecione o recuperando para o tipo de visita informado.");
        verify(visitanteDAO, never()).salvar(any(Visitante.class));
        verify(pessoaService, never()).salvarPessoaVisitante(any(Pessoa.class));
    }

    @Test
    public void incluir_quandoTipoExigeParenteESemParente_deveLancarValidacaoENaoSalvar() {
        TipoVisita tipoVisita = tipoVisita(SimNao.S, SimNao.S, SimNao.S, TipoEntrada.FAMILIA);
        Visitante visitante = visitante(tipoVisita);
        visitante.getCredenciamentos().add(credenciamento(tipoVisita, recuperando(20L, 10L), new UnidadePrisional(10L), null, null));

        Throwable throwable = catchThrowable(() -> service.incluir(visitante, null, false, false));

        assertThat(throwable).isInstanceOf(ValidacaoException.class)
                .hasMessage("Selecione o parentesco do visitante com o recuperando.");
        verify(visitanteDAO, never()).salvar(any(Visitante.class));
        verify(pessoaService, never()).salvarPessoaVisitante(any(Pessoa.class));
    }

    @Test
    public void incluir_quandoAtendimentoJuridicoComRecuperandoEUnidadeSemParente_deveSalvar() throws Exception {
        TipoVisita tipoVisita = tipoVisita(SimNao.S, SimNao.N, SimNao.N, TipoEntrada.ATENDIMENTO);
        Visitante visitante = visitante(tipoVisita);
        visitante.getCredenciamentos().add(credenciamento(tipoVisita, recuperando(20L, 10L), new UnidadePrisional(10L), null, null));
        prepararSalvar(visitante);

        Visitante retorno = service.incluir(visitante, null, false, false);

        assertThat(retorno).isSameAs(visitante);
        verify(visitanteDAO).salvar(visitante);
    }

    @Test
    public void incluir_quandoCredenciamentoSemUnidadeERecuperandoTemUnidadeId_devePreencherUnidadeAntesDeSalvar() throws Exception {
        TipoVisita tipoVisita = tipoVisita(SimNao.S, SimNao.N, SimNao.S, TipoEntrada.ATENDIMENTO);
        Visitante visitante = visitante(tipoVisita);
        visitante.getCredenciamentos().add(credenciamento(tipoVisita, recuperando(20L, 99L), null, null, null));
        prepararSalvar(visitante);

        service.incluir(visitante, null, false, false);

        ArgumentCaptor<Visitante> captor = ArgumentCaptor.forClass(Visitante.class);
        verify(visitanteDAO).salvar(captor.capture());
        Credenciamento credenciamento = captor.getValue().getCredenciamentos().get(0);
        assertThat(credenciamento.getUnidadePrisional()).isNotNull();
        assertThat(credenciamento.getUnidadePrisional().getId()).isEqualTo(99L);
    }

    @Test
    public void incluir_quandoCredenciamentoSemUnidadeESemComoInferir_deveLancarValidacaoENaoSalvar() {
        TipoVisita tipoVisita = tipoVisita(SimNao.S, SimNao.N, SimNao.S, TipoEntrada.ATENDIMENTO);
        Visitante visitante = visitante(tipoVisita);
        visitante.getCredenciamentos().add(credenciamento(tipoVisita, recuperando(20L, null), null, null, null));

        Throwable throwable = catchThrowable(() -> service.incluir(visitante, null, false, false));

        assertThat(throwable).isInstanceOf(ValidacaoException.class)
                .hasMessage("Informe a unidade prisional.");
        verify(visitanteDAO, never()).salvar(any(Visitante.class));
        verify(pessoaService, never()).salvarPessoaVisitante(any(Pessoa.class));
    }

    @Test
    public void incluir_quandoAssistenciaReligiosaSemProfissao_deveLancarValidacaoENaoSalvar() {
        TipoVisita tipoVisita = tipoVisita(SimNao.N, SimNao.N, SimNao.S, TipoEntrada.ENTRADA_SAIDA);
        Visitante visitante = visitante(tipoVisita);
        visitante.getCredenciamentos().add(credenciamento(tipoVisita, null, new UnidadePrisional(10L), null, " "));

        Throwable throwable = catchThrowable(() -> service.incluir(visitante, null, false, false));

        assertThat(throwable).isInstanceOf(ValidacaoException.class)
                .hasMessage("Informe a profissão.");
        verify(visitanteDAO, never()).salvar(any(Visitante.class));
        verify(pessoaService, never()).salvarPessoaVisitante(any(Pessoa.class));
    }

    @Test
    public void incluir_quandoAssistenciaReligiosaComProfissaoEUnidade_deveSalvarSemRecuperando() throws Exception {
        TipoVisita tipoVisita = tipoVisita(SimNao.N, SimNao.N, SimNao.S, TipoEntrada.ENTRADA_SAIDA);
        Visitante visitante = visitante(tipoVisita);
        visitante.getCredenciamentos().add(credenciamento(tipoVisita, null, new UnidadePrisional(10L), null, "Pastor"));
        prepararSalvar(visitante);

        Visitante retorno = service.incluir(visitante, null, false, false);

        assertThat(retorno).isSameAs(visitante);
        Credenciamento credenciamento = retorno.getCredenciamentos().get(0);
        assertThat(credenciamento.getRecuperando()).isNull();
        assertThat(credenciamento.getParente()).isNull();
        assertThat(credenciamento.getProfissao()).isEqualTo("PASTOR");
        verify(visitanteDAO).salvar(visitante);
    }

    private void prepararSalvar(Visitante visitante) {
        when(visitanteDAO.salvar(any(Visitante.class))).thenAnswer(invocation -> {
            Visitante visitanteSalvo = invocation.getArgument(0);
            visitanteSalvo.setId(1L);
            for (Credenciamento credenciamento : visitanteSalvo.getCredenciamentos()) {
                if (credenciamento.getId() == null) {
                    credenciamento.setId(10L);
                }
            }
            return visitanteSalvo;
        });
        when(visitanteDAO.buscarPorId(1L)).thenReturn(visitante);
    }

    private Visitante visitante() {
        return visitante(null);
    }

    private Visitante visitante(TipoVisita tipoVisita) {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(1L);

        Visitante visitante = new Visitante();
        visitante.setPessoa(pessoa);
        visitante.setTipoVisita(tipoVisita);
        visitante.setUnidadePrisional(new UnidadePrisional(1L));
        visitante.setUsuarioCadastro(new Usuario());
        return visitante;
    }

    private TipoVisita tipoVisita(SimNao obrigatorioRecuperando, SimNao estaNoRolDeParentes,
                                  SimNao obrigatorioCredenciamento, TipoEntrada tipoEntrada) {
        TipoVisita tipoVisita = new TipoVisita();
        tipoVisita.setId(1L);
        tipoVisita.setObrigatorioRecuperando(obrigatorioRecuperando);
        tipoVisita.setEstaNoRolDeParentes(estaNoRolDeParentes);
        tipoVisita.setObrigatorioCredenciamento(obrigatorioCredenciamento);
        tipoVisita.setTipoEntrada(tipoEntrada);
        return tipoVisita;
    }

    private Recuperando recuperando(Long id, Long unidadeId) {
        Recuperando recuperando = new Recuperando();
        recuperando.setId(id);
        recuperando.setUnidadeId(unidadeId);
        return recuperando;
    }

    private Credenciamento credenciamento(TipoVisita tipoVisita, Recuperando recuperando,
                                          UnidadePrisional unidadePrisional, Parente parente, String profissao) {
        Credenciamento credenciamento = new Credenciamento();
        credenciamento.setTipoVisita(tipoVisita);
        credenciamento.setRecuperando(recuperando);
        credenciamento.setUnidadePrisional(unidadePrisional);
        credenciamento.setParente(parente);
        credenciamento.setProfissao(profissao);
        return credenciamento;
    }
}
