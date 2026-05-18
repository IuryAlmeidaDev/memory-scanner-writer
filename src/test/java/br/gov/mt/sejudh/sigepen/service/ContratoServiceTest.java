package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.ContratoDAO;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadeContratoConflitoDTO;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContratoServiceTest extends BaseUnitTest {

    @Mock
    private ContratoDAO dao;

    @InjectMocks
    private ContratoService service;

    @Test
    public void deveRetornarDetalhesDasUnidadesConflitantesComNumeroContrato() {
        Contrato contratoAtual = contrato(99L, "CT-ATUAL", unidade(1L, "Penitenciaria A"), unidade(2L, "Cadeia B"));
        List<UnidadeContratoConflitoDTO> conflitosDao = Collections.singletonList(
                new UnidadeContratoConflitoDTO(1L, "PENITENCIARIA A", "CT-0001")
        );

        when(dao.listarConflitosUnidadeContratoAtivo(contratoAtual)).thenReturn(conflitosDao);

        List<UnidadeContratoConflitoDTO> conflitos = service.listarConflitosUnidadeEmContratosAtivos(contratoAtual);

        assertThat(conflitos).hasSize(1);
        assertThat(conflitos.get(0).getNomeUnidade()).isEqualTo("PENITENCIARIA A");
        assertThat(conflitos.get(0).getNumeroContrato()).isEqualTo("CT-0001");
    }

    @Test
    public void deveMontarMensagemComQuebraDeLinhaParaMultiplosConflitos() {
        List<UnidadeContratoConflitoDTO> conflitos = Arrays.asList(
                new UnidadeContratoConflitoDTO("PENITENCIARIA A", "CT-0001"),
                new UnidadeContratoConflitoDTO("CADEIA B", "CT-0002")
        );

        String mensagem = service.montarMensagemConflitosUnidadeContratoAtivo(conflitos);

        assertThat(mensagem).contains("unidades abaixo ja estao vinculadas a contrato(s) ativo(s):");
        assertThat(mensagem).contains(" - PENITENCIARIA A (Contrato: CT-0001);");
        assertThat(mensagem).contains(" - CADEIA B (Contrato: CT-0002)");
    }

    @Test
    public void deveMontarMensagemSingularQuandoHouverApenasUmaUnidadeConflitante() {
        List<UnidadeContratoConflitoDTO> conflitos = Collections.singletonList(
                new UnidadeContratoConflitoDTO("PENITENCIARIA A", "CT-0001")
        );

        String mensagem = service.montarMensagemConflitosUnidadeContratoAtivo(conflitos);

        assertThat(mensagem)
                .isEqualTo("Nao foi possivel adicionar a unidade PENITENCIARIA A, pois ela ja esta vinculada ao contrato ativo CT-0001.");
    }

    @Test
    public void listarParaFiltro_deveDelegarPermissaoEUnidadeParaDao() {
        List<Contrato> contratos = Collections.singletonList(new Contrato(1L));
        when(dao.listarParaFiltro(false, 22L)).thenReturn(contratos);

        List<Contrato> resultado = service.listarParaFiltro(false, 22L);

        assertThat(resultado).isSameAs(contratos);
        verify(dao).listarParaFiltro(false, 22L);
    }

    @Test
    public void listarAtivosParaFiltro_deveDelegarPermissaoEUnidadeParaDao() {
        List<Contrato> contratos = Collections.singletonList(new Contrato(1L));
        when(dao.listarAtivosParaFiltro(true, null)).thenReturn(contratos);

        List<Contrato> resultado = service.listarAtivosParaFiltro(true, null);

        assertThat(resultado).isSameAs(contratos);
        verify(dao).listarAtivosParaFiltro(true, null);
    }

    @Test
    public void listarFornecedoresParaFiltro_deveDelegarParametrosParaDao() {
        List<String> fornecedores = Collections.singletonList("Fornecedor A");
        when(dao.listarFornecedoresParaFiltro(false, 22L, "For", 20)).thenReturn(fornecedores);

        List<String> resultado = service.listarFornecedoresParaFiltro(false, 22L, "For", 20);

        assertThat(resultado).isSameAs(fornecedores);
        verify(dao).listarFornecedoresParaFiltro(false, 22L, "For", 20);
    }

    @Test
    public void listarNumerosParaFiltro_deveDelegarParametrosParaDao() {
        List<String> numeros = Collections.singletonList("CT-001");
        when(dao.listarNumerosParaFiltro(false, 22L, "CT", 20)).thenReturn(numeros);

        List<String> resultado = service.listarNumerosParaFiltro(false, 22L, "CT", 20);

        assertThat(resultado).isSameAs(numeros);
        verify(dao).listarNumerosParaFiltro(false, 22L, "CT", 20);
    }

    @Test
    public void listarUnidadesPorNumeroContrato_deveDelegarParaDao() {
        List<UnidadePrisional> unidades = Collections.singletonList(unidade(7L, "Unidade A"));
        when(dao.listarUnidadesPorNumeroContrato("CT-001")).thenReturn(unidades);

        List<UnidadePrisional> resultado = service.listarUnidadesPorNumeroContrato("CT-001");

        assertThat(resultado).isSameAs(unidades);
        verify(dao).listarUnidadesPorNumeroContrato("CT-001");
    }

    private Contrato contrato(Long id, String numero, UnidadePrisional... unidades) {
        Contrato contrato = new Contrato(id);
        contrato.setNumContrato(numero);
        contrato.setUnidades(Arrays.asList(unidades));
        return contrato;
    }

    private UnidadePrisional unidade(Long id, String nome) {
        UnidadePrisional unidade = new UnidadePrisional(id);
        unidade.setNome(nome);
        return unidade;
    }
}
