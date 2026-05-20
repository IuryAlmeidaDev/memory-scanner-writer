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
import br.gov.mt.sejudh.sigepen.dao.HistoricoPedidoAlimentoDAO;
import br.gov.mt.sejudh.sigepen.dao.UnidadePrisionalDAO;
import br.gov.mt.sejudh.sigepen.model.dto.ContratoComboDTO;

public class HistoricoPedidoAlimentoServiceTest extends BaseUnitTest {

    @Mock
    private HistoricoPedidoAlimentoDAO dao;
    @Mock
    private UnidadePrisionalDAO upDao;

    @InjectMocks
    private HistoricoPedidoAlimentoService service;

    @Test
    public void listarContratosParaCombo_deveDelegarPermissaoEUnidadeParaDao() {
        List<ContratoComboDTO> contratos = Collections.singletonList(new ContratoComboDTO(1L, "CT-001"));
        when(dao.listarContratosParaCombo(false, 9L)).thenReturn(contratos);

        List<ContratoComboDTO> resultado = service.listarContratosParaCombo(false, 9L);

        assertThat(resultado).isSameAs(contratos);
        verify(dao).listarContratosParaCombo(false, 9L);
    }

    @Test
    public void buscarMatriculaUsuarioPorId_idValido_deveDelegarParaDao() {
        when(dao.buscarMatriculaUsuarioPorId(10L)).thenReturn(12345L);

        Long resultado = service.buscarMatriculaUsuarioPorId(10L);

        assertThat(resultado).isEqualTo(12345L);
        verify(dao).buscarMatriculaUsuarioPorId(10L);
    }

    @Test
    public void buscarMatriculaUsuarioPorId_idInvalido_naoDeveConsultarDao() {
        Long resultado = service.buscarMatriculaUsuarioPorId(null);

        assertThat(resultado).isNull();
        verify(dao, org.mockito.Mockito.never()).buscarMatriculaUsuarioPorId(org.mockito.Mockito.anyLong());
    }
}
