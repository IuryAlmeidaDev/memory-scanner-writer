package br.gov.mt.sejudh.sigepen.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.CredenciamentoDAO;
import br.gov.mt.sejudh.sigepen.dao.ParenteDAO;
import br.gov.mt.sejudh.sigepen.dao.PessoaDAO;
import br.gov.mt.sejudh.sigepen.dao.ProcessoDAO;
import br.gov.mt.sejudh.sigepen.dao.RecuperandoDAO;
import br.gov.mt.sejudh.sigepen.dao.VisitanteDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperandoView;
import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusProcesso;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.TipoFoto;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Processo;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

public class RecuperandoServiceTest extends BaseUnitTest {

    @Mock
    private RecuperandoDAO dao;

    @Mock
    private PessoaDAO pessoaDao;

    @Mock
    private ProcessoDAO processoDao;

    @Mock
    private ParenteDAO parenteDAO;

    @Mock
    private CredenciamentoDAO credenciamentoDAO;

    @Mock
    private VisitanteDAO visitanteDAO;

    @Mock
    private VisitanteService visitanteService;

    @Mock
    private PassagemService passagemService;

    @Mock
    private FacesUtil facesUtil;

    @InjectMocks
    private RecuperandoService service;

    @Test
    public void salvar_recuperandoNulo_deveLancarValidacaoException() {
        assertThatThrownBy(() -> service.salvar(null))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("recuperando");

        verify(dao, never()).salvar(any(Recuperando.class));
        verify(dao, never()).recuperandoJaExisteNoSistema(anyString(), anyString(), any(Date.class), any());
    }

    @Test
    public void salvar_semPessoa_deveLancarValidacaoException() {
        Recuperando recuperando = new Recuperando();

        assertThatThrownBy(() -> service.salvar(recuperando))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("pessoa");

        verify(dao, never()).salvar(any(Recuperando.class));
        verify(dao, never()).recuperandoJaExisteNoSistema(anyString(), anyString(), any(Date.class), any());
    }

    @Test
    public void salvar_semNome_deveLancarValidacaoException() {
        Recuperando recuperando = new Recuperando();
        Pessoa pessoa = new Pessoa();
        pessoa.setNomeMae("Ana");
        pessoa.setDataNascimento(dataMaiorDeIdade());
        recuperando.setPessoa(pessoa);

        assertThatThrownBy(() -> service.salvar(recuperando))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Nome");

        verify(dao, never()).salvar(any(Recuperando.class));
        verify(dao, never()).recuperandoJaExisteNoSistema(anyString(), anyString(), any(Date.class), any());
    }

    @Test
    public void salvar_comNomeVazio_deveLancarValidacaoException() {
        Recuperando recuperando = novoRecuperandoComPessoaMinima();
        recuperando.getPessoa().setNome("   ");

        assertThatThrownBy(() -> service.salvar(recuperando))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Nome");

        verify(dao, never()).salvar(any(Recuperando.class));
        verify(dao, never()).recuperandoJaExisteNoSistema(anyString(), anyString(), any(Date.class), any());
    }

    @Test
    public void salvar_semNomeMae_deveLancarValidacaoException() {
        Recuperando recuperando = new Recuperando();
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Maria");
        pessoa.setDataNascimento(dataMaiorDeIdade());
        recuperando.setPessoa(pessoa);

        assertThatThrownBy(() -> service.salvar(recuperando))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Nome da");

        verify(dao, never()).salvar(any(Recuperando.class));
        verify(dao, never()).recuperandoJaExisteNoSistema(anyString(), anyString(), any(Date.class), any());
    }

    @Test
    public void salvar_comNomeMaeVazio_deveLancarValidacaoException() {
        Recuperando recuperando = novoRecuperandoComPessoaMinima();
        recuperando.getPessoa().setNomeMae("   ");

        assertThatThrownBy(() -> service.salvar(recuperando))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Nome da");

        verify(dao, never()).salvar(any(Recuperando.class));
        verify(dao, never()).recuperandoJaExisteNoSistema(anyString(), anyString(), any(Date.class), any());
    }

    @Test
    public void salvar_semDataNascimento_deveLancarValidacaoException() {
        Recuperando recuperando = novoRecuperandoComPessoaMinima();
        recuperando.getPessoa().setDataNascimento(null);

        assertThatThrownBy(() -> service.salvar(recuperando))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Data de nascimento");

        verify(dao, never()).salvar(any(Recuperando.class));
        verify(dao, never()).recuperandoJaExisteNoSistema(anyString(), anyString(), any(Date.class), any());
    }

    @Test
    public void salvar_recuperandoDuplicado_deveLancarValidacaoException() {
        Recuperando recuperando = novoRecuperandoValidoComFotoExistente(10L);
        when(dao.recuperandoJaExisteNoSistema(anyString(), anyString(), any(Date.class), eq(10L))).thenReturn(123L);

        assertThatThrownBy(() -> service.salvar(recuperando))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("existe");

        verify(dao, never()).salvar(any(Recuperando.class));
    }

    @Test
    public void salvar_recuperandoValido_deveSalvarERecarregar() throws Exception {
        Recuperando recuperando = novoRecuperandoValidoComFotoExistente(10L);
        prepararFluxoFelizSalvar(recuperando);

        Recuperando resultado = service.salvar(recuperando);

        assertThat(resultado).isSameAs(recuperando);
        verify(dao).salvar(recuperando);
        verify(dao).buscarRecuperandoPorId(10L);
    }

    @Test
    public void salvar_quandoListagemDeParentesRetornaNulo_deveSalvarSemErro() throws Exception {
        Recuperando recuperando = novoRecuperandoValidoComFotoExistente(10L);
        when(dao.recuperandoJaExisteNoSistema(anyString(), anyString(), any(Date.class), eq(10L))).thenReturn(null);
        when(parenteDAO.listarPorRecuperando(recuperando)).thenReturn(null);
        when(facesUtil.getObjetoSessao("NOVA_PASSAGEM")).thenReturn(null);
        when(credenciamentoDAO.listaVisitantes(recuperando)).thenReturn(Optional.empty());
        when(dao.buscarRecuperandoPorId(10L)).thenReturn(recuperando);

        Recuperando resultado = service.salvar(recuperando);

        assertThat(resultado).isSameAs(recuperando);
        verify(dao).salvar(recuperando);
    }

    @Test
    public void salvar_quandoVisitanteNaoEstaInativo_naoDeveSalvarVisitante() throws Exception {
        Recuperando recuperando = novoRecuperandoValidoComFotoExistente(10L);
        Credenciamento credenciamento = new Credenciamento();
        Visitante visitanteCredenciamento = new Visitante();
        visitanteCredenciamento.setId(50L);
        credenciamento.setVisitante(visitanteCredenciamento);
        Visitante visitanteBanco = new Visitante();
        visitanteBanco.setStatus(StatusVisitante.A);
        when(dao.recuperandoJaExisteNoSistema(anyString(), anyString(), any(Date.class), eq(10L))).thenReturn(null);
        when(parenteDAO.listarPorRecuperando(recuperando)).thenReturn(Collections.emptyList());
        when(facesUtil.getObjetoSessao("NOVA_PASSAGEM")).thenReturn(null);
        when(credenciamentoDAO.listaVisitantes(recuperando)).thenReturn(Optional.of(Arrays.asList(credenciamento)));
        when(visitanteDAO.buscarPorId(50L)).thenReturn(visitanteBanco);
        when(dao.buscarRecuperandoPorId(10L)).thenReturn(recuperando);

        service.salvar(recuperando);

        verify(visitanteDAO, never()).salvar(any(Visitante.class));
    }

    @Test
    public void atualizarRegimeCondicaoAtualRecuperando_comProcessos_deveAtualizarDaoEProcessos() {
        Recuperando recuperando = new Recuperando(10L);
        Processo processo = novoProcesso("Fechado", Condicao.CONDENADO, StatusProcesso.EM_EXECUCAO);
        when(processoDao.processosPorRecuperando(10L)).thenReturn(Arrays.asList(processo));

        Recuperando resultado = service.atualizarRegimeCondicaoAtualRecuperando(recuperando);

        assertThat(resultado).isSameAs(recuperando);
        assertThat(recuperando.getRegimeAtual().getDescricao()).isEqualTo("Fechado");
        assertThat(recuperando.getCondicaoAtual()).isEqualTo(Condicao.CONDENADO);
        assertThat(recuperando.getProcessos()).containsExactly(processo);
        verify(dao).atualizarRegimeCondicaoAtualRecuperando(eq(recuperando), eq(processo.getRegimeAtual()),
                eq(Condicao.CONDENADO));
    }

    @Test
    public void atualizarRegimeCondicaoAtualRecuperando_semProcessos_naoDeveAtualizarDao() {
        Recuperando recuperando = new Recuperando(10L);
        when(processoDao.processosPorRecuperando(10L)).thenReturn(Collections.emptyList());

        Recuperando resultado = service.atualizarRegimeCondicaoAtualRecuperando(recuperando);

        assertThat(resultado).isSameAs(recuperando);
        verify(dao, never()).atualizarRegimeCondicaoAtualRecuperando(any(Recuperando.class), any(Regime.class),
                any(Condicao.class));
    }

    @Test
    public void regimeAtualRecuperando_comFechadoAtivo_devePriorizarFechado() {
        Processo aberto = novoProcesso("Aberto", Condicao.PROVISORIO, StatusProcesso.EM_EXECUCAO);
        Processo fechado = novoProcesso("Fechado", Condicao.PROVISORIO, StatusProcesso.EM_EXECUCAO);

        Regime resultado = service.regimeAtualRecuperando(Arrays.asList(aberto, fechado));

        assertThat(resultado.getDescricao()).isEqualTo("Fechado");
    }

    @Test
    public void condicaoAtualRecuperando_comCondenadoAtivo_devePriorizarCondenado() {
        Processo provisorio = novoProcesso("Aberto", Condicao.PROVISORIO, StatusProcesso.EM_EXECUCAO);
        Processo condenado = novoProcesso("Aberto", Condicao.CONDENADO, StatusProcesso.EM_EXECUCAO);

        Condicao resultado = service.condicaoAtualRecuperando(Arrays.asList(provisorio, condenado));

        assertThat(resultado).isEqualTo(Condicao.CONDENADO);
    }

    @Test
    public void condicaoAtualRecuperando_comProvisorioEMedidaSeguranca_deveRetornarMedidaSeguranca() {
        Processo provisorio = novoProcesso("Aberto", Condicao.PROVISORIO, StatusProcesso.EM_EXECUCAO);
        Processo medida = novoProcesso("Aberto", Condicao.MEDIDA_SEGURANCA, StatusProcesso.EM_EXECUCAO);

        Condicao resultado = service.condicaoAtualRecuperando(Arrays.asList(provisorio, medida));

        assertThat(resultado).isEqualTo(Condicao.MEDIDA_SEGURANCA);
    }

    @Test
    public void buscarRecuperandosPorFiltro_quandoBuscarFotoFrenteEIdNulo_naoDeveConsultarImagemDoRecuperando() {
        FiltroRecuperandoView filtro = new FiltroRecuperandoView();
        filtro.setIsBuscarFotoFrente(true);
        RecuperandoView view = new RecuperandoView();
        view.setId(null);
        view.setVisitanteId(null);
        when(dao.buscarRecuperandosPorFiltro(filtro, false)).thenReturn(Collections.singletonList(view));

        java.util.List<RecuperandoView> resultado = service.buscarRecuperandosPorFiltro(filtro, false);

        assertThat(resultado).containsExactly(view);
        assertThat(resultado.get(0).getImagemFrente()).isNull();
        verify(dao, never()).buscarImagemFrente(any());
        verify(dao, never()).buscarLotacaoAtual(anyLong(), anyBoolean());
    }

    @Test
    public void buscarRecuperandosPorFiltro_quandoTemRecuperandoEVisitante_devePriorizarImagemDoVisitante() {
        FiltroRecuperandoView filtro = new FiltroRecuperandoView();
        filtro.setIsBuscarFotoFrente(true);
        RecuperandoView view = new RecuperandoView();
        view.setId(10L);
        view.setVisitanteId(20L);
        Imagem imagemRecuperando = new Imagem();
        Imagem imagemVisitante = new Imagem();
        when(dao.buscarRecuperandosPorFiltro(filtro, false)).thenReturn(Collections.singletonList(view));
        when(dao.buscarImagemFrente(10L)).thenReturn(imagemRecuperando);
        when(visitanteService.buscarImagemFrente(20L)).thenReturn(imagemVisitante);

        java.util.List<RecuperandoView> resultado = service.buscarRecuperandosPorFiltro(filtro, false);

        assertThat(resultado.get(0).getImagemFrente()).isSameAs(imagemVisitante);
        verify(dao).buscarImagemFrente(10L);
        verify(visitanteService).buscarImagemFrente(20L);
    }

    private void prepararFluxoFelizSalvar(Recuperando recuperando) {
        when(dao.recuperandoJaExisteNoSistema(anyString(), anyString(), any(Date.class), eq(recuperando.getId())))
                .thenReturn(null);
        when(parenteDAO.listarPorRecuperando(recuperando)).thenReturn(Collections.emptyList());
        when(facesUtil.getObjetoSessao("NOVA_PASSAGEM")).thenReturn(null);
        when(credenciamentoDAO.listaVisitantes(recuperando)).thenReturn(Optional.empty());
        when(dao.buscarRecuperandoPorId(recuperando.getId())).thenReturn(recuperando);
    }

    private Recuperando novoRecuperandoComPessoaMinima() {
        Recuperando recuperando = new Recuperando();
        recuperando.setPessoa(novaPessoaMaiorDeIdade());
        return recuperando;
    }

    private Recuperando novoRecuperandoValidoComFotoExistente(long id) {
        Recuperando recuperando = novoRecuperandoComPessoaMinima();
        recuperando.setId(id);
        recuperando.adicionarFotoFrente(novaFotoFrenteAtual(5L, recuperando));
        return recuperando;
    }

    private Pessoa novaPessoaMaiorDeIdade() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Maria");
        pessoa.setNomeMae("Ana");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -20);
        pessoa.setDataNascimento(calendar.getTime());
        return pessoa;
    }

    private Date dataMaiorDeIdade() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -20);
        return calendar.getTime();
    }

    private Foto novaFotoFrenteAtual(long id, Recuperando recuperando) {
        Foto foto = new Foto();
        foto.setId(id);
        foto.setTipo(TipoFoto.FRENTE);
        foto.setIsAtual(true);
        foto.setRecuperando(recuperando);
        return foto;
    }

    private Processo novoProcesso(String regime, Condicao condicao, StatusProcesso status) {
        Processo processo = new Processo();
        processo.setStatusProcesso(status);
        processo.setRegimeAtual(new Regime(1L, regime));
        processo.setCondicaoAtual(condicao);
        return processo;
    }
}
