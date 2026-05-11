package br.gov.mt.sejudh.sigepen.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.ApostilamentoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroApostilamento;
import br.gov.mt.sejudh.sigepen.model.dto.ApostilamentoDTO;

public class ApostilamentoServiceTest extends BaseUnitTest {

    @Mock
    private ApostilamentoDAO apostilamentoDAO;

    @InjectMocks
    private ApostilamentoService service;

    @Test
    public void deveMarcarStatusVigenteQuandoDataFimForHojeOuFutura() {
        ApostilamentoDTO hoje = dto(comData(0));
        ApostilamentoDTO futuro = dto(comData(2));

        when(apostilamentoDAO.buscaPorFiltro(any(FiltroApostilamento.class))).thenReturn(Arrays.asList(hoje, futuro));

        List<ApostilamentoDTO> resultado = service.buscaPorFiltro(new FiltroApostilamento());

        assertThat(resultado.get(0).getStatusVigencia()).isEqualTo("Vigente");
        assertThat(resultado.get(1).getStatusVigencia()).isEqualTo("Vigente");
    }

    @Test
    public void deveMarcarStatusEncerradoQuandoDataFimForAnteriorAoDiaAtual() {
        ApostilamentoDTO passado = dto(comData(-1));

        when(apostilamentoDAO.buscaPorFiltro(any(FiltroApostilamento.class))).thenReturn(Arrays.asList(passado));

        List<ApostilamentoDTO> resultado = service.buscaPorFiltro(new FiltroApostilamento());

        assertThat(resultado.get(0).getStatusVigencia()).isEqualTo("Encerrado");
    }

    private ApostilamentoDTO dto(Date dataFim) {
        ApostilamentoDTO dto = new ApostilamentoDTO();
        dto.setDataFim(dataFim);
        return dto;
    }

    private Date comData(int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, dias);
        return calendar.getTime();
    }
}
