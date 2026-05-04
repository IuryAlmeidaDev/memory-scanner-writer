package br.gov.mt.sejudh.sigepen.model.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.Test;

import br.gov.mt.sejudh.sigepen.enumerator.Status;

public class DietaEspecialDTOTest {

    @Test
    public void getStatusAtual_statusSuspensoSemFim_retornaSuspensa() {
        DietaEspecialDTO dto = new DietaEspecialDTO();
        dto.setStatus(Status.SUSPENSO);

        assertThat(dto.getStatusAtual()).isEqualTo("Suspensa");
    }

    @Test
    public void getStatusAtual_statusInativoSemFim_retornaEncerrada() {
        DietaEspecialDTO dto = new DietaEspecialDTO();
        dto.setStatus(Status.INATIVO);

        assertThat(dto.getStatusAtual()).isEqualTo("Encerrada");
    }

    @Test
    public void getStatusAtual_fimNoPassado_retornaEncerrada() {
        DietaEspecialDTO dto = new DietaEspecialDTO();
        dto.setStatus(Status.ATIVO);
        dto.setFim(new Date(System.currentTimeMillis() - 60_000));

        assertThat(dto.getStatusAtual()).isEqualTo("Encerrada");
    }

    @Test
    public void getStatusAtual_statusAtivoComFimFuturo_retornaAtiva() {
        DietaEspecialDTO dto = new DietaEspecialDTO();
        dto.setStatus(Status.ATIVO);
        dto.setFim(new Date(System.currentTimeMillis() + 60_000));

        assertThat(dto.getStatusAtual()).isEqualTo("Ativa");
    }
}
