package br.gov.mt.sejudh.sigepen.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.primefaces.model.DefaultTreeNode;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.SituacaoEstruturaUnidade;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;
import br.gov.mt.sejudh.sigepen.service.EstruturaService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

public class EstruturaBeanTest extends BaseUnitTest {

    @Mock
    private EstruturaService estruturaService;

    @Mock
    private FacesUtil facesUtil;

    @Mock
    private Logger log;

    @InjectMocks
    private EstruturaBean bean;

    @Test
    public void init_unidadeNula_inicializaEstruturaESituacaoSemMontarArvore() {
        bean.setUnidade(null);

        bean.init();

        assertThat(bean.getEstrutura()).isNotNull();
        assertThat(bean.getSituacaoEstruturaUnidade()).isNotNull();
        assertThat(bean.getRoot()).isNull();
        verify(estruturaService, never()).montarArvoreEstrutura(any(Estrutura.class), any(DefaultTreeNode.class));
        verify(estruturaService, never()).capacidadeTotalEstrutura(any(DefaultTreeNode.class));
    }

    @Test
    public void init_unidadeSemId_inicializaEstruturaESituacaoSemMontarArvore() {
        bean.setUnidade(new UnidadePrisional());

        bean.init();

        assertThat(bean.getEstrutura()).isNotNull();
        assertThat(bean.getSituacaoEstruturaUnidade()).isNotNull();
        assertThat(bean.getRoot()).isNull();
        verify(estruturaService, never()).montarArvoreEstrutura(any(Estrutura.class), any(DefaultTreeNode.class));
    }

    @Test
    public void init_unidadeComEstrutura_montaArvoreEAtualizaCapacidadeTotal() {
        Estrutura estruturaUnidade = estrutura(1L, "Raiz", false, 0, SituacaoUnidade.ATIVO);
        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade A");
        unidade.setEstrutura(estruturaUnidade);
        bean.setUnidade(unidade);

        doAnswer(invocation -> {
            DefaultTreeNode root = invocation.getArgument(0);
            ((Estrutura) root.getData()).setCapacidadeTotal(42);
            return 42;
        }).when(estruturaService).capacidadeTotalEstrutura(any(DefaultTreeNode.class));

        bean.init();

        assertThat(bean.getRoot()).isNotNull();
        assertThat(bean.getRoot().isExpanded()).isTrue();
        assertThat(bean.getUnidade().getCapacidadeTotal()).isEqualTo(42);
        assertThat(bean.getEstrutura()).isNotNull();
        assertThat(bean.getSituacaoEstruturaUnidade()).isNotNull();
        verify(estruturaService).montarArvoreEstrutura(eq(estruturaUnidade), eq(bean.getRoot()));
        verify(estruturaService).capacidadeTotalEstrutura(bean.getRoot());
    }

    @Test
    public void novo_quandoChamado_reinicializaBeanERetornaCadastroUnidade() {
        bean.setUnidade(null);

        String resultado = bean.novo();

        assertThat(resultado).isEqualTo("pretty:CadastrarUnidade");
        assertThat(bean.getEstrutura()).isNotNull();
        assertThat(bean.getSituacaoEstruturaUnidade()).isNotNull();
    }

    @Test
    public void incluirEstruturaPai_estruturaPaiSelecionada_preparaNovaEstruturaAtiva() throws Exception {
        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade A");
        Estrutura pai = estrutura(1L, "Raiz", false, 0, SituacaoUnidade.ATIVO);
        pai.setUnidadePrisional(unidade);
        DefaultTreeNode root = new DefaultTreeNode(pai, null);
        bean.setRoot(root);
        when(estruturaService.buscarNo(root, null, pai)).thenReturn(root);

        bean.incluirEstruturaPai(pai);

        assertThat(bean.getEstrutura()).isNotSameAs(pai);
        assertThat(bean.getEstrutura().getEstruturaPai()).isSameAs(pai);
        assertThat(bean.getEstrutura().getSituacaoUnidade()).isEqualTo(SituacaoUnidade.ATIVO);
        assertThat(bean.getEstrutura().getUnidadePrisional()).isSameAs(unidade);
        assertThat(getNoSelecionado()).isSameAs(root);
    }

    @Test
    public void estruturaRelacionada_estruturaSelecionada_preparaSituacaoRelacionada() {
        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade A");
        Estrutura estrutura = estrutura(2L, "Cela", true, 8, SituacaoUnidade.ATIVO);
        estrutura.setUnidadePrisional(unidade);

        bean.estruturaRelacionada(estrutura);

        assertThat(bean.getEstrutura()).isSameAs(estrutura);
        assertThat(bean.getSituacaoEstruturaUnidade().getEstrutura()).isSameAs(estrutura);
        assertThat(bean.getSituacaoEstruturaUnidade().getUnidadePrisional()).isSameAs(unidade);
    }

    @Test
    public void listarPorUnidade_serviceRetornaHistorico_defineSituacoes() {
        UnidadePrisionalDTO unidadePrisional = unidadeDto(10L);
        List<SituacaoEstruturaUnidade> historico = Arrays.asList(new SituacaoEstruturaUnidade(),
                new SituacaoEstruturaUnidade());
        when(estruturaService.listarPorUnidade(10L)).thenReturn(historico);

        bean.listarPorUnidade(unidadePrisional);

        assertThat(bean.getSituacoes()).isSameAs(historico);
        verify(estruturaService).listarPorUnidade(10L);
        verify(facesUtil, never()).exibirMensagem(any(FacesMessage.Severity.class), any(String.class), any(String.class));
    }

    @Test
    public void listarPorUnidade_serviceLancaExcecao_exibeMensagemFatal() {
        UnidadePrisionalDTO unidadePrisional = unidadeDto(10L);
        when(estruturaService.listarPorUnidade(10L)).thenThrow(new RuntimeException("falha consulta"));

        bean.listarPorUnidade(unidadePrisional);

        assertThat(bean.getSituacoes()).isNull();
        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "falha consulta");
    }

    @Test
    public void alterar_estruturaSelecionada_defineEstruturaENoSelecionado() throws Exception {
        Estrutura estrutura = estrutura(2L, "Cela", true, 8, SituacaoUnidade.ATIVO);
        DefaultTreeNode root = new DefaultTreeNode(estrutura, null);
        bean.setRoot(root);
        when(estruturaService.buscarNo(root, null, estrutura)).thenReturn(root);

        bean.alterar(estrutura);

        assertThat(bean.getEstrutura()).isSameAs(estrutura);
        assertThat(getNoSelecionado()).isSameAs(root);
        verify(estruturaService).buscarNo(root, null, estrutura);
    }

    @Test
    public void excluir_sucesso_excluiAtualizaFlashEReinicializaBean() {
        UnidadePrisional unidade = new UnidadePrisional(0L, "Unidade A");
        Estrutura estrutura = estrutura(2L, "Cela", true, 8, SituacaoUnidade.ATIVO);
        bean.setUnidade(unidade);

        bean.excluir(estrutura);

        verify(estruturaService).excluir(estrutura);
        verify(facesUtil).setFlashParameter("unidade", unidade);
        verify(facesUtil).exibirMensagemInfo(contains("Estrutura"), eq(false));
        assertThat(bean.getEstrutura()).isNotNull();
        assertThat(bean.getSituacaoEstruturaUnidade()).isNotNull();
    }

    @Test
    public void excluir_serviceLancaExcecao_exibeMensagemFatal() {
        Estrutura estrutura = estrutura(2L, "Cela", true, 8, SituacaoUnidade.ATIVO);
        doThrow(new RuntimeException("falha exclusao")).when(estruturaService).excluir(estrutura);

        bean.excluir(estrutura);

        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_FATAL), eq("ERRO"), contains("falha exclusao"));
    }

    @Test
    public void salvar_serviceLancaExcecao_exibeMensagemFatalSemAtualizarArvore() {
        Estrutura estrutura = estrutura(2L, "Cela", true, 8, SituacaoUnidade.ATIVO);
        bean.setEstrutura(estrutura);
        when(estruturaService.salvar(estrutura)).thenThrow(new RuntimeException("falha salvar"));

        bean.salvar();

        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_FATAL), eq("ERRO"), contains("falha salvar"));
        verify(estruturaService, never()).capacidadeTotalEstrutura(any(DefaultTreeNode.class));
    }

    @Test
    public void salvarSituacaoEstrutura_serviceLancaExcecao_exibeMensagemFatal() throws Exception {
        DefaultTreeNode root = new DefaultTreeNode(new Estrutura(), null);
        SituacaoEstruturaUnidade situacao = new SituacaoEstruturaUnidade();
        bean.setRoot(root);
        bean.setSituacaoEstruturaUnidade(situacao);
        doThrow(new RuntimeException("falha situacao"))
                .when(estruturaService).salvarSituacaoEstrutura(situacao, root);

        bean.salvarSituacaoEstrutura();

        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "falha situacao");
    }

    @Test
    public void listarPorUnidade_serviceRetornaListaVazia_defineListaVazia() {
        UnidadePrisionalDTO unidadePrisional = unidadeDto(10L);
        when(estruturaService.listarPorUnidade(10L)).thenReturn(Collections.<SituacaoEstruturaUnidade>emptyList());

        bean.listarPorUnidade(unidadePrisional);

        assertThat(bean.getSituacoes()).isEmpty();
        verify(estruturaService).listarPorUnidade(10L);
    }

    private DefaultTreeNode getNoSelecionado() throws Exception {
        Field field = EstruturaBean.class.getDeclaredField("noSelecionado");
        field.setAccessible(true);
        return (DefaultTreeNode) field.get(bean);
    }

    private UnidadePrisionalDTO unidadeDto(Long id) {
        UnidadePrisionalDTO dto = new UnidadePrisionalDTO();
        dto.setId(id);
        return dto;
    }

    private Estrutura estrutura(Long id, String nome, boolean cela, int capacidade, SituacaoUnidade situacao) {
        Estrutura estrutura = new Estrutura();
        estrutura.setId(id);
        estrutura.setNome(nome);
        estrutura.setCela(cela);
        estrutura.setCapacidade(capacidade);
        estrutura.setSituacaoUnidade(situacao);
        return estrutura;
    }
}
