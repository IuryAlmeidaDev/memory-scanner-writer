package br.gov.mt.sejudh.sigepen.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.DietaEspecialDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroDietaEspecial;
import br.gov.mt.sejudh.sigepen.model.dto.DietaEspecialDTO;

public class DietaEspecialServiceTest extends BaseUnitTest {

    @Mock
    private DietaEspecialDAO dao;
    @Mock
    private AuditoriaDAO auditDao;

    @InjectMocks
    private DietaEspecialService service;

    @Test
    public void encerrarDietasAtivasDoRecuperando_deveInativarAtivasOuSuspensas() {
        Date dataFim = new Date();

        service.encerrarDietasAtivasDoRecuperando(99L, dataFim);

        verify(dao).inativarDietasAtivasOuSuspensasDoRecuperando(99L, dataFim);
    }

    @Test
    public void suspenderDietasAtivasDoRecuperando_deveDelegarParaDao() {
        service.suspenderDietasAtivasDoRecuperando(99L);

        verify(dao).suspenderDietasAtivasDoRecuperando(99L);
    }

    @Test
    public void reativarDietasSuspensasDoRecuperando_deveDelegarParaDao() {
        service.reativarDietasSuspensasDoRecuperando(99L);

        verify(dao).reativarDietasSuspensasDoRecuperando(99L);
    }

    @Test
    public void inativarDietasAtivasOuSuspensasDoRecuperando_deveDelegarParaDao() {
        Date dataFim = new Date();

        service.inativarDietasAtivasOuSuspensasDoRecuperando(99L, dataFim);

        verify(dao).inativarDietasAtivasOuSuspensasDoRecuperando(99L, dataFim);
    }

    @Test
    public void listarNomesRecuperandosParaFiltro_deveDelegarPermissaoEUnidadeParaDao() {
        List<String> nomes = Arrays.asList("Ana", "Joao");
        when(dao.listarNomesRecuperandosParaFiltro(false, 7L)).thenReturn(nomes);

        List<String> resultado = service.listarNomesRecuperandosParaFiltro(false, 7L);

        assertThat(resultado).isSameAs(nomes);
        verify(dao).listarNomesRecuperandosParaFiltro(false, 7L);
    }

    @Test
    public void buscarPorFiltroRelatorio_deveDelegarParaDao() {
        FiltroDietaEspecial filtro = new FiltroDietaEspecial();
        List<DietaEspecialDTO> dietas = Arrays.asList(new DietaEspecialDTO());
        when(dao.buscaPorFiltroRelatorio(filtro)).thenReturn(dietas);

        List<DietaEspecialDTO> resultado = service.buscarPorFiltroRelatorio(filtro);

        assertThat(resultado).isSameAs(dietas);
        verify(dao).buscaPorFiltroRelatorio(filtro);
    }
}
