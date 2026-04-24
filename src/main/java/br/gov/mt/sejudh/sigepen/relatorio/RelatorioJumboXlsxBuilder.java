package br.gov.mt.sejudh.sigepen.relatorio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import br.gov.mt.sejudh.sigepen.model.dto.RelatorioJumboDTO;

public final class RelatorioJumboXlsxBuilder {

    static final String NOME_APLICACAO = "Sigepen";
    static final String VERSAO_APLICACAO = "1.0";
    static final String NOME_ABA = "Recuperandos";

    static final String[] CABECALHOS = {
            "", "MATRICULA", "NOME_RECUPERANDO", "NOME_MAE", "UNIDADE_PRISIONAL", "LOCALIZACAO"
    };

    static final double[] LARGURAS_COLUNAS = { 6, 14, 40, 40, 40, 40 };

    private RelatorioJumboXlsxBuilder() {
    }

    public static byte[] gerar(List<RelatorioJumboDTO> lista) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Workbook wb = new Workbook(out, NOME_APLICACAO, VERSAO_APLICACAO);
            Worksheet sheet = wb.newWorksheet(NOME_ABA);

            preencherCabecalho(sheet);
            preencherLinhas(sheet, lista);
            ajustarLayout(sheet);

            wb.finish();
            return out.toByteArray();
        }
    }

    private static void preencherCabecalho(Worksheet sheet) {
        for (int c = 0; c < CABECALHOS.length; c++) {
            sheet.value(0, c, CABECALHOS[c]);
        }
        sheet.range(0, 0, 0, CABECALHOS.length - 1).style().bold().set();
    }

    private static void preencherLinhas(Worksheet sheet, List<RelatorioJumboDTO> lista) {
        if (lista == null) {
            return;
        }
        int r = 1;
        for (RelatorioJumboDTO dto : lista) {
            sheet.value(r, 0, r);
            if (dto.getMatricula() != null) {
                sheet.value(r, 1, dto.getMatricula());
            }
            sheet.value(r, 2, nvl(dto.getNomeRecuperando()));
            sheet.value(r, 3, nvl(dto.getNomeMae()));
            sheet.value(r, 4, nvl(dto.getUnidadePrisional()));
            sheet.value(r, 5, nvl(dto.getLocalizacao()));
            r++;
        }
    }

    private static void ajustarLayout(Worksheet sheet) {
        sheet.freezePane(0, 1);
        for (int c = 0; c < LARGURAS_COLUNAS.length; c++) {
            sheet.width(c, LARGURAS_COLUNAS[c]);
        }
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }
}
