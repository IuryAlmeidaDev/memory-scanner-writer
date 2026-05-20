package br.gov.mt.sejudh.sigepen.bean;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.FaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.service.TipoVisitaService;
import br.gov.mt.sejudh.sigepen.service.VisitanteService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import javax.faces.application.FacesMessage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VisitanteBeanTest extends BaseUnitTest {

    @Mock
    private TipoVisitaService tipoVisitaService;

    @Mock
    private FacesUtil facesUtil;

    @Mock
    private VisitanteService service;

    @Spy
    @InjectMocks
    private VisitanteBean bean;

    private Visitante visitante;

    @Before
    public void prepararBean() throws Exception {
        visitante = new Visitante();
        visitante.setPessoa(new Pessoa());
        visitante.setCredenciamentos(new ArrayList<Credenciamento>());

        bean.setVisitante(visitante);
        setCampo("credenciamentos", new ArrayList<Credenciamento>());
        setCampo("cadastroTipoVisita", true);
        setCampo("cadastroDadosVisitante", false);
    }

    @Test
    public void proximoCadastroTipoVisita_quandoTipoExigeRecuperandoESemCredenciamento_deveBloquearAvanco() throws Exception {
        TipoVisita tipoVisita = tipoVisita(1L, SimNao.S, SimNao.S, TipoEntrada.FAMILIA);
        bean.setTipoVisita(tipoVisita);
        when(tipoVisitaService.buscarPorId(tipoVisita.getId())).thenReturn(tipoVisita);

        bean.proximoCadastroTipoVisita();

        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
                "Selecione o recuperando para o tipo de visita informado.");
        assertThat(visitante.getCredenciamentos()).isEmpty();
        assertThat(bean.getCadastroTipoVisita()).isTrue();
        assertThat(bean.getCadastroDadosVisitante()).isFalse();
    }

    @Test
    public void proximoCadastroTipoVisita_quandoCredenciamentoSemRecuperando_deveBloquearAvanco() throws Exception {
        TipoVisita tipoVisita = tipoVisita(1L, SimNao.S, SimNao.S, TipoEntrada.FAMILIA);
        Credenciamento credenciamento = credenciamento(tipoVisita, null, new UnidadePrisional(10L), new Parente());
        setCampo("credenciamentos", Collections.singletonList(credenciamento));
        bean.setTipoVisita(tipoVisita);
        when(tipoVisitaService.buscarPorId(tipoVisita.getId())).thenReturn(tipoVisita);

        bean.proximoCadastroTipoVisita();

        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
                "Selecione o recuperando para o tipo de visita informado.");
        assertThat(visitante.getCredenciamentos()).isEmpty();
        assertThat(bean.getCadastroTipoVisita()).isTrue();
        assertThat(bean.getCadastroDadosVisitante()).isFalse();
    }

    @Test
    public void proximoCadastroTipoVisita_quandoRecuperandoTemUnidadeId_devePreencherUnidade() throws Exception {
        TipoVisita tipoVisita = tipoVisita(2L, SimNao.S, SimNao.N, TipoEntrada.ATENDIMENTO);
        Recuperando recuperando = recuperando(20L, 99L);
        Credenciamento credenciamento = credenciamento(tipoVisita, recuperando, null, null);
        setCampo("credenciamentos", Collections.singletonList(credenciamento));
        bean.setTipoVisita(tipoVisita);
        when(tipoVisitaService.buscarPorId(tipoVisita.getId())).thenReturn(tipoVisita);

        bean.proximoCadastroTipoVisita();

        assertThat(credenciamento.getUnidadePrisional()).isNotNull();
        assertThat(credenciamento.getUnidadePrisional().getId()).isEqualTo(99L);
        assertThat(visitante.getCredenciamentos()).containsExactly(credenciamento);
    }

    @Test
    public void proximoCadastroTipoVisita_quandoUnidadeNaoPodeSerInferida_deveBloquearAvanco() throws Exception {
        TipoVisita tipoVisita = tipoVisita(2L, SimNao.S, SimNao.N, TipoEntrada.ATENDIMENTO);
        Credenciamento credenciamento = credenciamento(tipoVisita, recuperando(20L, null), null, null);
        setCampo("credenciamentos", Collections.singletonList(credenciamento));
        bean.setTipoVisita(tipoVisita);
        when(tipoVisitaService.buscarPorId(tipoVisita.getId())).thenReturn(tipoVisita);

        bean.proximoCadastroTipoVisita();

        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Informe a unidade prisional.");
        assertThat(visitante.getCredenciamentos()).isEmpty();
        assertThat(bean.getCadastroTipoVisita()).isTrue();
        assertThat(bean.getCadastroDadosVisitante()).isFalse();
    }

    @Test
    public void proximoCadastroTipoVisita_quandoAssistenciaReligiosaSemUnidade_deveBloquearAvanco() throws Exception {
        TipoVisita tipoVisita = tipoVisita(4L, SimNao.N, SimNao.N, TipoEntrada.ENTRADA_SAIDA);
        bean.setTipoVisita(tipoVisita);
        bean.setProfissao("Pastor");
        when(tipoVisitaService.buscarPorId(tipoVisita.getId())).thenReturn(tipoVisita);

        bean.proximoCadastroTipoVisita();

        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Informe a unidade prisional.");
        assertThat(visitante.getCredenciamentos()).isEmpty();
        assertThat(bean.getCadastroTipoVisita()).isTrue();
        assertThat(bean.getCadastroDadosVisitante()).isFalse();
    }

    @Test
    public void proximoCadastroTipoVisita_quandoAssistenciaReligiosaSemProfissao_deveBloquearAvanco() throws Exception {
        TipoVisita tipoVisita = tipoVisita(4L, SimNao.N, SimNao.N, TipoEntrada.ENTRADA_SAIDA);
        bean.setTipoVisita(tipoVisita);
        bean.setUnidadePrisional(new UnidadePrisional(10L));
        bean.setProfissao(" ");
        when(tipoVisitaService.buscarPorId(tipoVisita.getId())).thenReturn(tipoVisita);

        bean.proximoCadastroTipoVisita();

        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Informe a profissão.");
        assertThat(visitante.getCredenciamentos()).isEmpty();
        assertThat(bean.getCadastroTipoVisita()).isTrue();
        assertThat(bean.getCadastroDadosVisitante()).isFalse();
    }

    @Test
    public void proximoCadastroTipoVisita_quandoAssistenciaReligiosaComCamposObrigatorios_deveCriarCredenciamentoSemRecuperando() throws Exception {
        TipoVisita tipoVisita = tipoVisita(4L, SimNao.N, SimNao.N, TipoEntrada.ENTRADA_SAIDA);
        bean.setTipoVisita(tipoVisita);
        bean.setUnidadePrisional(new UnidadePrisional(10L));
        bean.setProfissao("Pastor");
        when(tipoVisitaService.buscarPorId(tipoVisita.getId())).thenReturn(tipoVisita);

        bean.proximoCadastroTipoVisita();

        assertThat(visitante.getCredenciamentos()).hasSize(1);
        Credenciamento credenciamento = visitante.getCredenciamentos().get(0);
        assertThat(credenciamento.getRecuperando()).isNull();
        assertThat(credenciamento.getParente()).isNull();
        assertThat(credenciamento.getTipoVisita()).isSameAs(tipoVisita);
        assertThat(credenciamento.getVisitante()).isSameAs(visitante);
        assertThat(credenciamento.getUnidadePrisional().getId()).isEqualTo(10L);
        assertThat(credenciamento.getProfissao()).isEqualTo("PASTOR");
    }

    @Test
    public void habilitarCredenciar_quandoCredenciamentoSemUnidade_devePermitirCliqueParaValidar() {
        doReturn(new UnidadePrisional(1L)).when(bean).getUnidadeSelecionadaAtual();

        boolean retorno = bean.habilitarCredenciar(new Credenciamento());

        assertThat(retorno).isFalse();
    }

    @Test
    public void habilitarExcluirCredenciamento_quandoUnidadeNula_devePermitirExclusao() {
        doReturn(new UnidadePrisional(1L)).when(bean).getUnidadeSelecionadaAtual();

        boolean retorno = bean.habilitarExcluirCredenciamento(new Credenciamento());

        assertThat(retorno).isFalse();
    }

    @Test
    public void habilitarExcluirCredenciamento_quandoUnidadeDiferente_deveBloquearExclusao() {
        doReturn(new UnidadePrisional(1L)).when(bean).getUnidadeSelecionadaAtual();
        Credenciamento credenciamento = new Credenciamento();
        credenciamento.setUnidadePrisional(new UnidadePrisional(2L));

        boolean retorno = bean.habilitarExcluirCredenciamento(credenciamento);

        assertThat(retorno).isTrue();
    }

    @Test
    public void credenciarOuInformarErro_quandoCredenciamentoValido_deveRedirecionarParaCredenciamento() throws Exception {
        TipoVisita tipoVisita = tipoVisita(1L, SimNao.S, SimNao.N, TipoEntrada.ATENDIMENTO);
        Credenciamento credenciamento = credenciamento(tipoVisita, recuperando(1L, 10L),
                new UnidadePrisional(10L), null);
        credenciamento.setId(1L);
        doNothing().when(bean).redirecionarParaCredenciamento(credenciamento);

        String retorno = bean.credenciarOuInformarErro(credenciamento);

        assertThat(retorno).isEmpty();
        verify(bean).redirecionarParaCredenciamento(credenciamento);
    }

    @Test
    public void credenciarOuInformarErro_quandoSemUnidade_deveExibirMensagemENaoRedirecionar() {
        TipoVisita tipoVisita = tipoVisita(1L, SimNao.S, SimNao.N, TipoEntrada.ATENDIMENTO);
        Credenciamento credenciamento = credenciamento(tipoVisita, recuperando(1L, 10L), null, null);
        credenciamento.setId(1L);

        String retorno = bean.credenciarOuInformarErro(credenciamento);

        assertThat(retorno).isEmpty();
        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
                "Não foi possível continuar este credenciamento porque ele está incompleto. Campos pendentes: unidade prisional.");
    }

    @Test
    public void credenciarOuInformarErro_quandoTipoExigeRecuperandoESemRecuperando_deveExibirMensagemENaoRedirecionar() {
        TipoVisita tipoVisita = tipoVisita(1L, SimNao.S, SimNao.N, TipoEntrada.ATENDIMENTO);
        Credenciamento credenciamento = credenciamento(tipoVisita, null, new UnidadePrisional(10L), null);
        credenciamento.setId(1L);

        String retorno = bean.credenciarOuInformarErro(credenciamento);

        assertThat(retorno).isEmpty();
        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
                "Não foi possível continuar este credenciamento porque ele está incompleto. Campos pendentes: recuperando.");
    }

    @Test
    public void credenciarOuInformarErro_quandoTipoExigeParenteESemParente_deveExibirMensagemENaoRedirecionar() {
        TipoVisita tipoVisita = tipoVisita(1L, SimNao.S, SimNao.S, TipoEntrada.FAMILIA);
        Credenciamento credenciamento = credenciamento(tipoVisita, recuperando(1L, 10L),
                new UnidadePrisional(10L), null);
        credenciamento.setId(1L);

        String retorno = bean.credenciarOuInformarErro(credenciamento);

        assertThat(retorno).isEmpty();
        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
                "Não foi possível continuar este credenciamento porque ele está incompleto. Campos pendentes: parentesco.");
    }

    @Test
    public void credenciarOuInformarErro_quandoAssistenciaReligiosaSemRecuperando_devePermitirSeUnidadeEProfissaoExistirem() throws Exception {
        TipoVisita tipoVisita = tipoVisita(1L, SimNao.N, SimNao.N, TipoEntrada.ENTRADA_SAIDA);
        Credenciamento credenciamento = credenciamento(tipoVisita, null, new UnidadePrisional(10L), null);
        credenciamento.setId(1L);
        credenciamento.setProfissao("Pastor");
        doNothing().when(bean).redirecionarParaCredenciamento(credenciamento);

        String retorno = bean.credenciarOuInformarErro(credenciamento);

        assertThat(retorno).isEmpty();
        verify(bean).redirecionarParaCredenciamento(credenciamento);
    }

    @Test
    public void credenciarOuInformarErro_quandoUnidadeRecuperandoEParenteNulos_deveExibirTodasAsPendenciasENaoRedirecionar() {
        TipoVisita tipoVisita = tipoVisita(1L, SimNao.S, SimNao.S, TipoEntrada.FAMILIA);
        Credenciamento credenciamento = credenciamento(tipoVisita, null, null, null);
        credenciamento.setId(1L);

        String retorno = bean.credenciarOuInformarErro(credenciamento);

        assertThat(retorno).isEmpty();
        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
                "Não foi possível continuar este credenciamento porque ele está incompleto. Campos pendentes: unidade prisional, recuperando e parentesco.");
    }

    @Test
    public void credenciarOuInformarErro_quandoAssistenciaReligiosaSemUnidadeEProfissao_deveExibirTodasAsPendenciasENaoRedirecionar() {
        TipoVisita tipoVisita = tipoVisita(1L, SimNao.N, SimNao.N, TipoEntrada.ENTRADA_SAIDA);
        Credenciamento credenciamento = credenciamento(tipoVisita, null, null, null);
        credenciamento.setId(1L);

        String retorno = bean.credenciarOuInformarErro(credenciamento);

        assertThat(retorno).isEmpty();
        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
                "Não foi possível continuar este credenciamento porque ele está incompleto. Campos pendentes: unidade prisional e profissão.");
    }

    @Test
    public void excluirCredenciamento_quandoFaseJaIniciada_deveBloquearComMensagem() {
        TipoVisita tipoVisita = tipoVisita(1L, SimNao.S, SimNao.N, TipoEntrada.ATENDIMENTO);
        Credenciamento credenciamento = credenciamento(tipoVisita, recuperando(1L, 10L),
                new UnidadePrisional(10L), null);
        credenciamento.setId(1L);
        FaseDoCredenciamento fase = new FaseDoCredenciamento();
        fase.setId(1L);
        fase.setStatusCredenciamento(StatusCredenciamento.REGULAR);
        credenciamento.setFasesDoCredenciamento(Collections.singletonList(fase));

        bean.excluirCredenciamento(credenciamento);

        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
                "Não é permitido excluir credenciamentos já iniciados.");
        verify(service, never()).excluirCredenciamentoDoVisitante(credenciamento);
    }

    @Test
    public void excluirCredenciamento_quandoCredenciamentoIncompletoSemFaseIniciada_deveExcluir() {
        Credenciamento credenciamento = new Credenciamento();
        credenciamento.setId(1L);
        credenciamento.setVisitante(visitante);
        visitante.getCredenciamentos().add(credenciamento);

        bean.excluirCredenciamento(credenciamento);

        verify(service).excluirCredenciamentoDoVisitante(credenciamento);
        verify(service).atribuirStatusVisitantePosCredenciamento(visitante);
        verify(facesUtil).exibirMensagemInfo("Credenciamento excluído com sucesso!", false);
        assertThat(visitante.getCredenciamentos()).doesNotContain(credenciamento);
    }

    @Test
    public void excluirCredenciamento_quandoFaseIniciadaECredenciamentoInvalido_deveExcluir() {
        TipoVisita tipoVisita = tipoVisita(1L, SimNao.S, SimNao.S, TipoEntrada.FAMILIA);
        Credenciamento credenciamento = credenciamento(tipoVisita, null, null, null);
        credenciamento.setId(1L);
        FaseDoCredenciamento fase = new FaseDoCredenciamento();
        fase.setId(1L);
        fase.setFaseCredenciamento(FaseCredenciamento.RECEBIMENTO);
        fase.setStatusCredenciamento(StatusCredenciamento.REGULAR);
        credenciamento.setFasesDoCredenciamento(Collections.singletonList(fase));
        visitante.getCredenciamentos().add(credenciamento);

        bean.excluirCredenciamento(credenciamento);

        verify(service).excluirCredenciamentoDoVisitante(credenciamento);
        verify(service).atribuirStatusVisitantePosCredenciamento(visitante);
        assertThat(visitante.getCredenciamentos()).doesNotContain(credenciamento);
    }

    @Test
    public void excluirCredenciamento_quandoCredenciamentoAprovadoMesmoComPendencia_deveBloquear() {
        TipoVisita tipoVisita = tipoVisita(1L, SimNao.S, SimNao.S, TipoEntrada.FAMILIA);
        Credenciamento credenciamento = credenciamento(tipoVisita, null, null, null);
        credenciamento.setId(1L);
        credenciamento.setStatusCredenciamento(StatusCredenciamento.APROVADO.getId());

        bean.excluirCredenciamento(credenciamento);

        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
                "Não foi permitido excluir credenciamentos já concluídos.");
        verify(service, never()).excluirCredenciamentoDoVisitante(credenciamento);
    }

    @Test
    public void excluirCredenciamento_quandoCredenciamentoNulo_deveBloquearENaoChamarService() {
        bean.excluirCredenciamento((Credenciamento) null);

        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
                "Não foi possí­vel localizar o credenciamento selecionado.");
        verify(service, never()).excluirCredenciamentoDoVisitante(any(Credenciamento.class));
    }

    private TipoVisita tipoVisita(Long id, SimNao obrigatorioRecuperando, SimNao estaNoRolDeParentes,
                                  TipoEntrada tipoEntrada) {
        TipoVisita tipoVisita = new TipoVisita();
        tipoVisita.setId(id);
        tipoVisita.setObrigatorioRecuperando(obrigatorioRecuperando);
        tipoVisita.setEstaNoRolDeParentes(estaNoRolDeParentes);
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
                                          UnidadePrisional unidadePrisional, Parente parente) {
        Credenciamento credenciamento = new Credenciamento();
        credenciamento.setTipoVisita(tipoVisita);
        credenciamento.setVisitante(visitante);
        credenciamento.setRecuperando(recuperando);
        credenciamento.setUnidadePrisional(unidadePrisional);
        credenciamento.setParente(parente);
        return credenciamento;
    }

    private void setCampo(String nome, Object valor) throws Exception {
        Field field = VisitanteBean.class.getDeclaredField(nome);
        field.setAccessible(true);
        field.set(bean, valor);
    }
}
