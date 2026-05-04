package br.gov.mt.sejudh.sigepen.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.EstruturaDAO;
import br.gov.mt.sejudh.sigepen.dao.ServidorDAO;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.SituacaoEstruturaUnidade;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.EstruturaDto;
import br.gov.mt.sejudh.sigepen.model.dto.GerarPedidoEstruturaDTO;

public class EstruturaServiceTest extends BaseUnitTest {

    private static final String DATA_HORA = "01/04/2026 12:00:00";

    @Mock
    private EstruturaDAO dao;

    @Mock
    private ServidorDAO daoServidor;

    @Mock
    private Subject subject;

    @InjectMocks
    private EstruturaService service;

    @After
    public void limparContextoShiro() {
        ThreadContext.unbindSubject();
    }

    @Test
    public void salvar_estruturaNaoCela_zeraCapacidadeAntesDeSalvar() {
        Estrutura estrutura = estrutura(10L, "Ala A", false, 30, SituacaoUnidade.ATIVO);
        when(dao.salvar(estrutura)).thenReturn(estrutura);

        Estrutura resultado = service.salvar(estrutura);

        assertThat(resultado).isSameAs(estrutura);
        assertThat(estrutura.getCapacidade()).isZero();
        verify(dao).salvar(estrutura);
    }

    @Test
    public void salvar_estruturaCela_mantemCapacidadeAntesDeSalvar() {
        Estrutura estrutura = estrutura(11L, "Cela 1", true, 12, SituacaoUnidade.ATIVO);
        when(dao.salvar(estrutura)).thenReturn(estrutura);

        Estrutura resultado = service.salvar(estrutura);

        assertThat(resultado).isSameAs(estrutura);
        assertThat(estrutura.getCapacidade()).isEqualTo(12);
        verify(dao).salvar(estrutura);
    }

    @Test
    public void montarArvoreEstrutura_comFilhos_montaHierarquiaEMarcaNosComFilhos() {
        Estrutura pai = estrutura(1L, "Raiz", false, 0, SituacaoUnidade.ATIVO);
        Estrutura filho = estrutura(2L, "Ala", false, 0, SituacaoUnidade.ATIVO);
        Estrutura neto = estrutura(3L, "Cela", true, 5, SituacaoUnidade.ATIVO);
        DefaultTreeNode root = new DefaultTreeNode(new Estrutura(), null);

        when(dao.buscaEstruturaFilho(pai)).thenReturn(Collections.singletonList(filho));
        when(dao.buscaEstruturaFilho(filho)).thenReturn(Collections.singletonList(neto));
        when(dao.buscaEstruturaFilho(neto)).thenReturn(Collections.<Estrutura>emptyList());

        service.montarArvoreEstrutura(pai, root);

        DefaultTreeNode noPai = (DefaultTreeNode) root.getChildren().get(0);
        DefaultTreeNode noFilho = (DefaultTreeNode) noPai.getChildren().get(0);
        DefaultTreeNode noNeto = (DefaultTreeNode) noFilho.getChildren().get(0);

        assertThat(noPai.getData()).isSameAs(pai);
        assertThat(noFilho.getData()).isSameAs(filho);
        assertThat(noNeto.getData()).isSameAs(neto);
        assertThat(pai.isTemEstruturaFilho()).isTrue();
        assertThat(filho.isTemEstruturaFilho()).isTrue();
        assertThat(neto.isTemEstruturaFilho()).isFalse();
    }

    @Test
    public void montarArvoreEstrutura_semFilhos_criaNoSemMarcarEstruturaFilho() {
        Estrutura estrutura = estrutura(1L, "Raiz", false, 0, SituacaoUnidade.ATIVO);
        DefaultTreeNode root = new DefaultTreeNode(new Estrutura(), null);
        when(dao.buscaEstruturaFilho(estrutura)).thenReturn(Collections.<Estrutura>emptyList());

        service.montarArvoreEstrutura(estrutura, root);

        assertThat(root.getChildren()).hasSize(1);
        assertThat(root.getChildren().get(0).isExpanded()).isTrue();
        assertThat(estrutura.isTemEstruturaFilho()).isFalse();
    }

    @Test
    public void montarArvoreEstruturaGerarPedido_filhosInativosOuRamosVazios_removeDaArvore() {
        GerarPedidoEstruturaDTO raiz = gerarPedido(1L, "Raiz", false, SituacaoUnidade.ATIVO, 0L, 0L);
        GerarPedidoEstruturaDTO alaSemCela = gerarPedido(2L, "Ala vazia", false, SituacaoUnidade.ATIVO, 0L, 0L);
        GerarPedidoEstruturaDTO celaAtiva = gerarPedido(3L, "Cela ativa", true, SituacaoUnidade.ATIVO, 2L, 1L);
        GerarPedidoEstruturaDTO celaInativa = gerarPedido(4L, "Cela inativa", true, SituacaoUnidade.INATIVO, 1L, 0L);
        DefaultTreeNode root = new DefaultTreeNode("root", null);

        when(dao.buscaEstruturaFilhoGerarPedido(raiz, DATA_HORA, 1L))
                .thenReturn(Arrays.asList(alaSemCela, celaAtiva, celaInativa));
        when(dao.buscaEstruturaFilhoGerarPedido(alaSemCela, DATA_HORA, 1L))
                .thenReturn(Collections.<GerarPedidoEstruturaDTO>emptyList());
        when(dao.buscaEstruturaFilhoGerarPedido(celaAtiva, DATA_HORA, 1L))
                .thenReturn(Collections.<GerarPedidoEstruturaDTO>emptyList());

        TreeNode noRaiz = service.montarArvoreEstruturaGerarPedido(raiz, root, DATA_HORA, 1L);

        assertThat(raiz.isTemEstruturaFilho()).isTrue();
        assertThat(noRaiz.getChildren()).hasSize(1);
        assertThat(noRaiz.getChildren().get(0).getData()).isSameAs(celaAtiva);
        verify(dao, never()).buscaEstruturaFilhoGerarPedido(celaInativa, DATA_HORA, 1L);
    }

    @Test
    public void capacidadeTotalEstrutura_arvoreComFilhos_somaCapacidadesEAtribuiTotais() {
        Estrutura raiz = estrutura(1L, "Raiz", false, 2, SituacaoUnidade.ATIVO);
        Estrutura filho = estrutura(2L, "Ala", false, 3, SituacaoUnidade.ATIVO);
        Estrutura neto = estrutura(3L, "Cela", true, 5, SituacaoUnidade.ATIVO);
        DefaultTreeNode noRaiz = new DefaultTreeNode(raiz, null);
        DefaultTreeNode noFilho = new DefaultTreeNode(filho, noRaiz);
        new DefaultTreeNode(neto, noFilho);

        int resultado = service.capacidadeTotalEstrutura(noRaiz);

        assertThat(resultado).isEqualTo(10);
        assertThat(raiz.getCapacidadeTotal()).isEqualTo(10);
        assertThat(filho.getCapacidadeTotal()).isEqualTo(8);
        assertThat(neto.getCapacidadeTotal()).isEqualTo(5);
    }

    @Test
    public void capacidadeTotalEstruturaGerarPedido_arvoreComFilhos_somaDietaPadraoEAtribuiTotal() {
        GerarPedidoEstruturaDTO raiz = gerarPedido(1L, "Raiz", false, SituacaoUnidade.ATIVO, 2L, 0L);
        GerarPedidoEstruturaDTO filho = gerarPedido(2L, "Cela", true, SituacaoUnidade.ATIVO, 3L, 1L);
        DefaultTreeNode noRaiz = new DefaultTreeNode(raiz, null);
        new DefaultTreeNode(filho, noRaiz);

        int resultado = service.capacidadeTotalEstruturaGerarPedido(noRaiz);

        assertThat(resultado).isEqualTo(5);
        assertThat(raiz.getCapacidadeTotal()).isEqualTo(5);
        assertThat(filho.getCapacidadeTotal()).isEqualTo(3);
    }

    @Test
    public void capacidadeTotalDietaEspecial_arvoreComFilhos_somaDietaEspecialEAtribuiTotal() {
        GerarPedidoEstruturaDTO raiz = gerarPedido(1L, "Raiz", false, SituacaoUnidade.ATIVO, 0L, 1L);
        GerarPedidoEstruturaDTO filho = gerarPedido(2L, "Cela", true, SituacaoUnidade.ATIVO, 0L, 4L);
        DefaultTreeNode noRaiz = new DefaultTreeNode(raiz, null);
        new DefaultTreeNode(filho, noRaiz);

        long resultado = service.capacidadeTotalDietaEspecial(noRaiz);

        assertThat(resultado).isEqualTo(5L);
        assertThat(raiz.getTotalDietaEspecial()).isEqualTo(5);
        assertThat(filho.getTotalDietaEspecial()).isEqualTo(4);
    }

    @Test
    public void lotacaoTotalEstrutura_arvoreComFilhos_somaLotacaoDeCadaNo() {
        Estrutura raiz = estrutura(1L, "Raiz", false, 0, SituacaoUnidade.ATIVO);
        Estrutura filho = estrutura(2L, "Cela", true, 0, SituacaoUnidade.ATIVO);
        DefaultTreeNode noRaiz = new DefaultTreeNode(raiz, null);
        new DefaultTreeNode(filho, noRaiz);

        when(dao.quantidadeRecuperandosLotados(raiz)).thenReturn(2L);
        when(dao.quantidadeRecuperandosLotados(filho)).thenReturn(3L);

        long resultado = service.lotacaoTotalEstrutura(noRaiz);

        assertThat(resultado).isEqualTo(5L);
        verify(dao).quantidadeRecuperandosLotados(raiz);
        verify(dao).quantidadeRecuperandosLotados(filho);
    }

    @Test
    public void buscarNo_estruturaExistenteNaArvore_retornaNoCorrespondente() {
        Estrutura raiz = estrutura(1L, "Raiz", false, 0, SituacaoUnidade.ATIVO);
        Estrutura filho = estrutura(2L, "Ala", false, 0, SituacaoUnidade.ATIVO);
        Estrutura neto = estrutura(3L, "Cela", true, 0, SituacaoUnidade.ATIVO);
        DefaultTreeNode noRaiz = new DefaultTreeNode(raiz, null);
        DefaultTreeNode noFilho = new DefaultTreeNode(filho, noRaiz);
        DefaultTreeNode noNeto = new DefaultTreeNode(neto, noFilho);

        DefaultTreeNode resultado = service.buscarNo(noRaiz, null, neto);

        assertThat(resultado).isSameAs(noNeto);
    }

    @Test
    public void buscarNo_estruturaInexistenteNaArvore_retornaNull() {
        Estrutura raiz = estrutura(1L, "Raiz", false, 0, SituacaoUnidade.ATIVO);
        DefaultTreeNode noRaiz = new DefaultTreeNode(raiz, null);
        Estrutura inexistente = estrutura(99L, "Inexistente", false, 0, SituacaoUnidade.ATIVO);

        DefaultTreeNode resultado = service.buscarNo(noRaiz, null, inexistente);

        assertThat(resultado).isNull();
    }

    @Test
    public void validaFilhoAtivoOuParcial_comFilhoAtivo_retornaTrue() {
        DefaultTreeNode no = new DefaultTreeNode(estrutura(1L, "Raiz", false, 0, SituacaoUnidade.ATIVO), null);
        new DefaultTreeNode(estrutura(2L, "Filho", true, 0, SituacaoUnidade.ATIVO), no);

        boolean resultado = service.validaFilhoAtivoOuParcial(no);

        assertThat(resultado).isTrue();
    }

    @Test
    public void validaFilhoAtivoOuParcial_semFilhosAtivosOuParciais_retornaFalse() {
        DefaultTreeNode no = new DefaultTreeNode(estrutura(1L, "Raiz", false, 0, SituacaoUnidade.ATIVO), null);
        new DefaultTreeNode(estrutura(2L, "Filho", true, 0, SituacaoUnidade.INATIVO), no);

        boolean resultado = service.validaFilhoAtivoOuParcial(no);

        assertThat(resultado).isFalse();
    }

    @Test
    public void validaFilhoAtivoOuInativo_apenasAtivosEInativos_retornaTrue() {
        DefaultTreeNode no = new DefaultTreeNode(estrutura(1L, "Raiz", false, 0, SituacaoUnidade.ATIVO), null);
        new DefaultTreeNode(estrutura(2L, "Ativo", true, 0, SituacaoUnidade.ATIVO), no);
        new DefaultTreeNode(estrutura(3L, "Inativo", true, 0, SituacaoUnidade.INATIVO), no);

        boolean resultado = service.validaFilhoAtivoOuInativo(no);

        assertThat(resultado).isTrue();
    }

    @Test
    public void validaFilhoAtivoOuInativo_comFilhoInterditado_retornaFalse() {
        DefaultTreeNode no = new DefaultTreeNode(estrutura(1L, "Raiz", false, 0, SituacaoUnidade.ATIVO), null);
        new DefaultTreeNode(estrutura(2L, "Interditado", true, 0, SituacaoUnidade.INTERDITADO_TOTAL), no);

        boolean resultado = service.validaFilhoAtivoOuInativo(no);

        assertThat(resultado).isFalse();
    }

    @Test
    public void buscarEstruturasFilhos_filhosCelaENaoCela_adicionaTodosERecursaApenasNaoCela() {
        Estrutura pai = estrutura(1L, "Raiz", false, 0, SituacaoUnidade.ATIVO);
        Estrutura cela = estrutura(2L, "Cela", true, 0, SituacaoUnidade.ATIVO);
        Estrutura ala = estrutura(3L, "Ala", false, 0, SituacaoUnidade.ATIVO);
        Estrutura subCela = estrutura(4L, "Sub cela", true, 0, SituacaoUnidade.ATIVO);
        List<Estrutura> retorno = new ArrayList<Estrutura>();

        when(dao.buscaEstruturaFilho(pai)).thenReturn(Arrays.asList(cela, ala));
        when(dao.buscaEstruturaFilho(ala)).thenReturn(Collections.singletonList(subCela));

        service.buscarEstruturasFilhos(pai, retorno);

        assertThat(retorno).containsExactly(cela, ala, subCela);
        verify(dao, never()).buscaEstruturaFilho(cela);
        verify(dao, never()).buscaEstruturaFilho(subCela);
    }

    @Test
    public void montarArvoreDaUnidade_listaComPaiEFilhos_montaHierarquiaEMarcaPais() {
        EstruturaDto pai = estruturaDto(1L, null, "Raiz");
        EstruturaDto filho = estruturaDto(2L, 1L, "Ala");
        EstruturaDto neto = estruturaDto(3L, 2L, "Cela");
        DefaultTreeNode root = new DefaultTreeNode("root", null);

        service.montarArvoreDaUnidade(Arrays.asList(pai, filho, neto), root);

        TreeNode noPai = root.getChildren().get(0);
        TreeNode noFilho = noPai.getChildren().get(0);

        assertThat(noPai.getData()).isSameAs(pai);
        assertThat(noFilho.getData()).isSameAs(filho);
        assertThat(noFilho.getChildren().get(0).getData()).isSameAs(neto);
        assertThat(pai.getTemEstruturaFilho()).isTrue();
        assertThat(filho.getTemEstruturaFilho()).isTrue();
        assertThat(neto.getTemEstruturaFilho()).isNull();
    }

    @Test
    public void montarArvoreDaUnidade_listaVazia_naoAdicionaNos() {
        DefaultTreeNode root = new DefaultTreeNode("root", null);

        service.montarArvoreDaUnidade(Collections.<EstruturaDto>emptyList(), root);

        assertThat(root.getChildren()).isEmpty();
    }

    @Test
    public void alterarSituacaoEstruturasFilhos_interditadoTotal_cascataSituacaoEPersisteHistoricos() {
        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade A");
        Estrutura pai = estrutura(1L, "Raiz", false, 0, SituacaoUnidade.ATIVO);
        Estrutura filho = estrutura(2L, "Cela", true, 0, SituacaoUnidade.ATIVO);
        DefaultTreeNode noPai = new DefaultTreeNode(pai, null);
        new DefaultTreeNode(filho, noPai);
        SituacaoEstruturaUnidade situacao = situacao(pai, unidade, SituacaoUnidade.INTERDITADO_TOTAL);

        service.alterarSituacaoEstruturasFilhos(situacao, noPai, pai);

        ArgumentCaptor<SituacaoEstruturaUnidade> captor = ArgumentCaptor.forClass(SituacaoEstruturaUnidade.class);
        assertThat(pai.getSituacaoUnidade()).isEqualTo(SituacaoUnidade.INTERDITADO_TOTAL);
        assertThat(filho.getSituacaoUnidade()).isEqualTo(SituacaoUnidade.INTERDITADO_TOTAL);
        verify(dao).salvar(pai);
        verify(dao).salvar(filho);
        verify(dao).salvarSituacaoEstrutura(situacao);
        verify(dao, org.mockito.Mockito.atLeastOnce()).salvarSituacaoEstrutura(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(SituacaoEstruturaUnidade::getEstrutura)
                .contains(pai, filho);
        assertThat(captor.getAllValues())
                .filteredOn(s -> s.getEstrutura().equals(filho))
                .allMatch(s -> !s.isOrigemAlteracao());
    }

    @Test
    public void alterarSituacaoEstruturaPai_interditadoTotalComFilhoAtivo_marcaPaiComoInterditadoParcial() {
        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade A");
        Estrutura pai = estrutura(1L, "Raiz", false, 0, SituacaoUnidade.ATIVO);
        Estrutura filhoInterditado = estrutura(2L, "Cela 1", true, 0, SituacaoUnidade.INTERDITADO_TOTAL);
        Estrutura filhoAtivo = estrutura(3L, "Cela 2", true, 0, SituacaoUnidade.ATIVO);
        filhoInterditado.setEstruturaPai(pai);
        filhoAtivo.setEstruturaPai(pai);
        DefaultTreeNode noPai = new DefaultTreeNode(pai, null);
        DefaultTreeNode noFilhoInterditado = new DefaultTreeNode(filhoInterditado, noPai);
        new DefaultTreeNode(filhoAtivo, noPai);
        SituacaoEstruturaUnidade situacao = situacao(filhoInterditado, unidade, SituacaoUnidade.INTERDITADO_TOTAL);

        service.alterarSituacaoEstruturaPai(situacao, noFilhoInterditado);

        ArgumentCaptor<SituacaoEstruturaUnidade> captor = ArgumentCaptor.forClass(SituacaoEstruturaUnidade.class);
        assertThat(pai.getSituacaoUnidade()).isEqualTo(SituacaoUnidade.INTERDITADO_PARCIAL);
        verify(dao).salvar(pai);
        verify(dao).salvarSituacaoEstrutura(captor.capture());
        assertThat(captor.getValue().getEstrutura()).isSameAs(pai);
        assertThat(captor.getValue().getSituacao()).isEqualTo(SituacaoUnidade.INTERDITADO_PARCIAL);
        assertThat(captor.getValue().isOrigemAlteracao()).isFalse();
    }

    @Test
    public void salvarSituacaoEstrutura_inativoComServidoresLotados_lancaValidacaoException() {
        configurarUsuarioLogado("usuario.teste");
        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade A");
        Estrutura raiz = estrutura(1L, "Raiz", false, 0, SituacaoUnidade.ATIVO);
        raiz.setUnidadePrisional(unidade);
        DefaultTreeNode root = new DefaultTreeNode(new Estrutura(), null);
        new DefaultTreeNode(raiz, root);
        SituacaoEstruturaUnidade situacao = situacao(raiz, unidade, SituacaoUnidade.INATIVO);

        when(daoServidor.unidadePossuiServidoresLotados(unidade)).thenReturn(true);

        assertThatThrownBy(() -> service.salvarSituacaoEstrutura(situacao, root))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("servidores lotados");
        assertThat(situacao.getLoginUsuario()).isEqualTo("USUARIO.TESTE");
        assertThat(situacao.isOrigemAlteracao()).isTrue();
        verify(dao, never()).salvar(any(Estrutura.class));
        verify(dao, never()).salvarSituacaoEstrutura(any(SituacaoEstruturaUnidade.class));
    }

    private void configurarUsuarioLogado(String login) {
        when(subject.getPrincipal()).thenReturn(login);
        ThreadContext.bind(subject);
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

    private GerarPedidoEstruturaDTO gerarPedido(Long id, String nome, boolean cela, SituacaoUnidade situacao,
            Long dietaPadrao, Long dietaEspecial) {
        GerarPedidoEstruturaDTO dto = new GerarPedidoEstruturaDTO(id, 0, cela, nome, dietaPadrao, dietaEspecial);
        dto.setSituacaoUnidade(situacao);
        return dto;
    }

    private EstruturaDto estruturaDto(Long id, Long idPai, String nome) {
        EstruturaDto dto = new EstruturaDto();
        dto.setId(id);
        dto.setIdEstruturaPai(idPai);
        dto.setEstruturaNome(nome);
        return dto;
    }

    private SituacaoEstruturaUnidade situacao(Estrutura estrutura, UnidadePrisional unidade, SituacaoUnidade situacao) {
        SituacaoEstruturaUnidade situacaoEstrutura = new SituacaoEstruturaUnidade();
        situacaoEstrutura.setEstrutura(estrutura);
        situacaoEstrutura.setUnidadePrisional(unidade);
        situacaoEstrutura.setSituacao(situacao);
        situacaoEstrutura.setMotivo("Motivo");
        situacaoEstrutura.setLoginUsuario("usuario.teste");
        return situacaoEstrutura;
    }
}
