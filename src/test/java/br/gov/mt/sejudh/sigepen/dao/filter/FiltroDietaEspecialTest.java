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
}
