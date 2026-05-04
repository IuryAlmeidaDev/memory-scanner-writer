package br.gov.mt.sejudh.sigepen.service;

import static org.mockito.Mockito.verify;

import java.util.Date;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.DietaEspecialDAO;

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
}
