package br.gov.mt.sejudh.sigepen.dao.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.enumerator.StatusFiltroDietaEspecial;
import br.gov.mt.sejudh.sigepen.model.TipoDieta;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;

public class FiltroDietaEspecialTest extends BaseUnitTest {

    @Test
    public void novoFiltro_statusPadraoAtivo() {
        FiltroDietaEspecial filtro = new FiltroDietaEspecial();

        assertThat(filtro.getStatusDieta()).isEqualTo(StatusFiltroDietaEspecial.ATIVO);
    }

    @Test
    public void configurarFiltros_statusSuspensoFiltraDietasVigentes() {
        Criteria criteria = org.mockito.Mockito.mock(Criteria.class);
        FiltroDietaEspecial filtro = new FiltroDietaEspecial();
        filtro.setStatusDieta(StatusFiltroDietaEspecial.SUSPENSO);
        filtro.setListarTodasUnidades(true);
        filtro.setCriteria(criteria);
        when(criteria.createAlias(anyString(), anyString(), any(org.hibernate.sql.JoinType.class))).thenReturn(criteria);
        when(criteria.setProjection(any(org.hibernate.criterion.Projection.class))).thenReturn(criteria);
        when(criteria.add(any(Criterion.class))).thenReturn(criteria);

        filtro.configurarFiltros();

        ArgumentCaptor<Criterion> captor = ArgumentCaptor.forClass(Criterion.class);
        verify(criteria, atLeastOnce()).add(captor.capture());
        assertThat(captor.getAllValues())
                .anySatisfy(criterio -> assertThat(criterio.toString()).contains("status=Suspenso"))
                .anySatisfy(criterio -> assertThat(criterio.toString()).contains("fim is null").contains("fim>"));
        assertThat(captor.getAllValues())
                .noneSatisfy(criterio -> assertThat(criterio.toString()).contains("fim<="));
    }

    @Test
    public void copiarParaRelatorio_devePreservarFiltrosELimparPaginacao() {
        FiltroDietaEspecial filtro = new FiltroDietaEspecial();
        UnidadePrisional unidade = new UnidadePrisional(10L);
        TipoDieta tipoDieta = new TipoDieta();
        tipoDieta.setId(20L);
        filtro.setUnidadePrisional(unidade);
        filtro.setTipoDieta(tipoDieta);
        filtro.setRecuperando("Maria");
        filtro.setStatusDieta(StatusFiltroDietaEspecial.ENCERRADO);
        filtro.setListarTodasUnidades(false);
        filtro.organizaFiltro(30, 10, "nomeRecuperando", org.primefaces.model.SortOrder.DESCENDING);
        filtro.setPaginacaoIndex(30);

        FiltroDietaEspecial copia = filtro.copiarParaRelatorio(true);

        assertThat(copia).isNotSameAs(filtro);
        assertThat(copia.getUnidadePrisional()).isSameAs(unidade);
        assertThat(copia.getTipoDieta()).isSameAs(tipoDieta);
        assertThat(copia.getRecuperando()).isEqualTo("Maria");
        assertThat(copia.getStatusDieta()).isEqualTo(StatusFiltroDietaEspecial.ENCERRADO);
        assertThat(copia.isListarTodasUnidades()).isTrue();
        assertThat(copia.getPrimeiroRegistro()).isEqualTo(30);
        assertThat(copia.getPaginacaoIndex()).isEqualTo(30);
        assertThat(copia.getQuantidadeRegistros()).isEqualTo(Integer.MAX_VALUE);
        assertThat(copia.getPropriedadeOrdenacao()).isEqualTo("nomeRecuperando");
        assertThat(copia.getSortOrder()).isEqualTo(org.primefaces.model.SortOrder.DESCENDING);
        assertThat(copia.isAscendente()).isFalse();
    }
}
