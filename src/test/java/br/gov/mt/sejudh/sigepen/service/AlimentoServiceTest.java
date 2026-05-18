package br.gov.mt.sejudh.sigepen.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.AlimentoDAO;
import br.gov.mt.sejudh.sigepen.model.Alimento;

public class AlimentoServiceTest extends BaseUnitTest {

    @Mock
    private AlimentoDAO alimentoDAO;

    @InjectMocks
    private AlimentoService service;

    @Test
    public void listarTodos_deveDelegarParaDao() {
        List<Alimento> alimentos = Collections.singletonList(new Alimento());
        when(alimentoDAO.listarTodos()).thenReturn(alimentos);

        List<Alimento> resultado = service.listarTodos();

        assertThat(resultado).isSameAs(alimentos);
        verify(alimentoDAO).listarTodos();
    }

    @Test
    public void listarDescricoesParaFiltro_deveDelegarConsultaELimiteParaDao() {
        List<String> descricoes = Collections.singletonList("Arroz");
        when(alimentoDAO.listarDescricoesParaFiltro("Ar", 20)).thenReturn(descricoes);

        List<String> resultado = service.listarDescricoesParaFiltro("Ar", 20);

        assertThat(resultado).isSameAs(descricoes);
        verify(alimentoDAO).listarDescricoesParaFiltro("Ar", 20);
    }
}
