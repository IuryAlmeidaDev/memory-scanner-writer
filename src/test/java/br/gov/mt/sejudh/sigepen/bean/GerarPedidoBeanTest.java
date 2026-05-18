package br.gov.mt.sejudh.sigepen.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

public class GerarPedidoBeanTest extends BaseUnitTest {

    @Mock
    private UnidadePrisionalService unidadeService;
    @Mock
    private SecurityUtil security;
    @Mock
    private Subject subject;
    @Mock
    private Session session;

    @InjectMocks
    private GerarPedidoBean bean;

    @After
    public void limparContextoShiro() {
        ThreadContext.unbindSubject();
    }

    @Test
    public void carregarOpcoesFiltroUnidades_semPermissaoFiltraUnidadeAtual() throws Exception {
        configurarUnidadeSelecionada(22L);
        when(security.possuiPermissao("ListarTodasUnidades")).thenReturn(false);
        when(unidadeService.buscarPorFiltroUnidadesComContratosAtivos(any(FiltroUnidadePrisional.class), eq(true)))
                .thenReturn(Arrays.asList(
                        unidadeDto("Unidade B", "UB"),
                        unidadeDto(null, "UA"),
                        unidadeDto("Unidade A", null),
                        unidadeDto("Unidade A", "UB")));

        invocarCarregarOpcoesFiltroUnidades();

        ArgumentCaptor<FiltroUnidadePrisional> filtroCaptor = ArgumentCaptor.forClass(FiltroUnidadePrisional.class);
        verify(unidadeService).buscarPorFiltroUnidadesComContratosAtivos(filtroCaptor.capture(), eq(true));
        assertThat(filtroCaptor.getValue().getId()).isEqualTo(22L);
        assertThat(bean.getNomesUnidadesFiltro()).containsExactly("Unidade A", "Unidade B");
        assertThat(bean.getSiglasUnidadesFiltro()).containsExactly("UA", "UB");
    }

    @Test
    public void carregarOpcoesFiltroUnidades_semPermissaoSemUnidadeNaoConsultaTodas() throws Exception {
        configurarSemUnidadeSelecionada();
        when(security.possuiPermissao("ListarTodasUnidades")).thenReturn(false);

        invocarCarregarOpcoesFiltroUnidades();

        verify(unidadeService, never()).buscarPorFiltroUnidadesComContratosAtivos(any(FiltroUnidadePrisional.class),
                eq(true));
        assertThat(bean.getNomesUnidadesFiltro()).isEmpty();
        assertThat(bean.getSiglasUnidadesFiltro()).isEmpty();
    }

    private void configurarUnidadeSelecionada(Long unidadeId) {
        UnidadePrisional unidade = new UnidadePrisional(unidadeId);
        when(subject.getSession()).thenReturn(session);
        when(session.getAttribute("unidadeSelecionada")).thenReturn(unidade);
        ThreadContext.bind(subject);
    }

    private void configurarSemUnidadeSelecionada() {
        when(subject.getSession()).thenReturn(session);
        when(session.getAttribute("unidadeSelecionada")).thenReturn(null);
        ThreadContext.bind(subject);
    }

    private void invocarCarregarOpcoesFiltroUnidades() throws Exception {
        Method method = GerarPedidoBean.class.getDeclaredMethod("carregarOpcoesFiltroUnidades");
        method.setAccessible(true);
        method.invoke(bean);
    }

    private UnidadePrisionalDTO unidadeDto(String nome, String sigla) {
        UnidadePrisionalDTO dto = new UnidadePrisionalDTO();
        dto.setNome(nome);
        dto.setSigla(sigla);
        return dto;
    }
}
