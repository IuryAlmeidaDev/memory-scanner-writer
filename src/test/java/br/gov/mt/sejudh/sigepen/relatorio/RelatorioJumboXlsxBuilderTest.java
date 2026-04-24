package br.gov.mt.sejudh.sigepen.relatorio;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.gov.mt.sejudh.sigepen.model.dto.RelatorioJumboDTO;

public class RelatorioJumboXlsxBuilderTest {

    private static final String SHEET_PATH = "xl/worksheets/sheet1.xml";

    @Test
    public void gerar_deveProduzirZipValidoComAssinaturaPK() throws Exception {
        byte[] bytes = RelatorioJumboXlsxBuilder.gerar(Collections.<RelatorioJumboDTO>emptyList());

        assertThat(bytes).isNotNull();
        assertThat(bytes.length).isGreaterThan(0);
        assertThat(bytes[0]).isEqualTo((byte) 'P');
        assertThat(bytes[1]).isEqualTo((byte) 'K');
    }

    @Test
    public void gerar_deveConterApenasUmaAbaChamadaRecuperandos() throws Exception {
        byte[] bytes = RelatorioJumboXlsxBuilder.gerar(Collections.<RelatorioJumboDTO>emptyList());

        String workbookXml = lerEntrada(bytes, "xl/workbook.xml");

        assertThat(workbookXml).contains("name=\"Recuperandos\"");
        int qtdSheets = contarOcorrencias(workbookXml, "<sheet ");
        assertThat(qtdSheets).isEqualTo(1);
    }

    @Test
    public void gerar_deveEscreverCabecalhoUpperSnakeNaLinha1ComA1Vazia() throws Exception {
        byte[] bytes = RelatorioJumboXlsxBuilder.gerar(Collections.<RelatorioJumboDTO>emptyList());

        List<String> shared = lerSharedStrings(bytes);
        Document sheet = parsearSheet(bytes);
        Element linha1 = acharLinha(sheet, 1);

        assertThat(resolverCelula(linha1, "A1", shared)).isEmpty();
        assertThat(resolverCelula(linha1, "B1", shared)).isEqualTo("MATRICULA");
        assertThat(resolverCelula(linha1, "C1", shared)).isEqualTo("NOME_RECUPERANDO");
        assertThat(resolverCelula(linha1, "D1", shared)).isEqualTo("NOME_MAE");
        assertThat(resolverCelula(linha1, "E1", shared)).isEqualTo("UNIDADE_PRISIONAL");
        assertThat(resolverCelula(linha1, "F1", shared)).isEqualTo("LOCALIZACAO");
    }

    @Test
    public void gerar_deveEscreverMatriculaComoNumeroSemDecimais() throws Exception {
        List<RelatorioJumboDTO> lista = Arrays.asList(
                dto(77404L, "ADRIANO PEREIRA", "MARIA DE LURDES", "CADEIA PUBLICA", "ALA TRABALHADORES"));

        byte[] bytes = RelatorioJumboXlsxBuilder.gerar(lista);
        Document sheet = parsearSheet(bytes);
        Element linha2 = acharLinha(sheet, 2);

        Element celulaB2 = acharCelula(linha2, "B2");
        String tipo = celulaB2.getAttribute("t");
        // tipo "n" (ou ausente) = numeric; NUNCA deve ser "s" (shared) nem "inlineStr"
        assertThat(tipo).isNotEqualTo("s");
        assertThat(tipo).isNotEqualTo("inlineStr");
        assertThat(valorInline(linha2, "B2")).isEqualTo("77404");
    }

    @Test
    public void gerar_devePreservarStringsCrasSemNormalizar() throws Exception {
        List<RelatorioJumboDTO> lista = Arrays.asList(
                dto(77404L, "ADRIANO PEREIRA", "MARIA DE LURDES", "CADEIA PUBLICA", "ALA TRABALHADORES"));

        byte[] bytes = RelatorioJumboXlsxBuilder.gerar(lista);
        List<String> shared = lerSharedStrings(bytes);
        Document sheet = parsearSheet(bytes);
        Element linha2 = acharLinha(sheet, 2);

        assertThat(resolverCelula(linha2, "A2", shared)).isEqualTo("1");
        assertThat(resolverCelula(linha2, "C2", shared)).isEqualTo("ADRIANO PEREIRA");
        assertThat(resolverCelula(linha2, "D2", shared)).isEqualTo("MARIA DE LURDES");
        assertThat(resolverCelula(linha2, "E2", shared)).isEqualTo("CADEIA PUBLICA");
        assertThat(resolverCelula(linha2, "F2", shared)).isEqualTo("ALA TRABALHADORES");
    }

    @Test
    public void gerar_deveNumerarIndiceSequencialApartirDeUm() throws Exception {
        List<RelatorioJumboDTO> lista = Arrays.asList(
                dto(1L, "A", "AM", "U", "L"),
                dto(2L, "B", "BM", "U", "L"),
                dto(3L, "C", "CM", "U", "L"));

        byte[] bytes = RelatorioJumboXlsxBuilder.gerar(lista);
        List<String> shared = lerSharedStrings(bytes);
        Document sheet = parsearSheet(bytes);

        assertThat(resolverCelula(acharLinha(sheet, 2), "A2", shared)).isEqualTo("1");
        assertThat(resolverCelula(acharLinha(sheet, 3), "A3", shared)).isEqualTo("2");
        assertThat(resolverCelula(acharLinha(sheet, 4), "A4", shared)).isEqualTo("3");
    }

    @Test
    public void gerar_deveTratarCamposNullDoDtoComoStringVazia() throws Exception {
        List<RelatorioJumboDTO> lista = Arrays.asList(dto(null, null, null, null, null));

        byte[] bytes = RelatorioJumboXlsxBuilder.gerar(lista);
        List<String> shared = lerSharedStrings(bytes);
        Document sheet = parsearSheet(bytes);
        Element linha2 = acharLinha(sheet, 2);

        assertThat(resolverCelula(linha2, "A2", shared)).isEqualTo("1");
        assertThat(resolverCelula(linha2, "C2", shared)).isEmpty();
        assertThat(resolverCelula(linha2, "D2", shared)).isEmpty();
        assertThat(resolverCelula(linha2, "E2", shared)).isEmpty();
        assertThat(resolverCelula(linha2, "F2", shared)).isEmpty();
    }

    @Test
    public void gerar_comListaNullDeveProduzirApenasCabecalho() throws Exception {
        byte[] bytes = RelatorioJumboXlsxBuilder.gerar(null);
        List<String> shared = lerSharedStrings(bytes);
        Document sheet = parsearSheet(bytes);

        NodeList rows = sheet.getElementsByTagName("row");
        assertThat(rows.getLength()).isEqualTo(1);
        assertThat(resolverCelula((Element) rows.item(0), "B1", shared)).isEqualTo("MATRICULA");
    }

    @Test
    public void gerar_deveCongelarPrimeiraLinha() throws Exception {
        byte[] bytes = RelatorioJumboXlsxBuilder.gerar(Collections.<RelatorioJumboDTO>emptyList());
        String sheetXml = lerEntrada(bytes, SHEET_PATH);

        assertThat(sheetXml).contains("<pane");
        assertThat(sheetXml).contains("state=\"frozen\"");
    }

    private static RelatorioJumboDTO dto(Long matricula, String nome, String mae, String unidade, String local) {
        RelatorioJumboDTO d = new RelatorioJumboDTO();
        d.setMatricula(matricula);
        d.setNomeRecuperando(nome);
        d.setNomeMae(mae);
        d.setUnidadePrisional(unidade);
        d.setLocalizacao(local);
        return d;
    }

    private static Document parsearSheet(byte[] bytes) throws Exception {
        String xml = lerEntrada(bytes, SHEET_PATH);
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return db.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
    }

    private static String lerEntrada(byte[] zipBytes, String caminho) throws IOException {
        // fastexcel/opczip gera data descriptors sem tamanho no local header,
        // o que faz o ZipInputStream quebrar ao navegar entre entries.
        // ZipFile usa o central directory e funciona.
        Path tmp = Files.createTempFile("xlsx-test-", ".xlsx");
        try {
            Files.write(tmp, zipBytes);
            try (ZipFile zf = new ZipFile(tmp.toFile())) {
                ZipEntry entry = zf.getEntry(caminho);
                if (entry == null) {
                    throw new IOException("Entrada " + caminho + " nao encontrada no zip.");
                }
                try (InputStream in = zf.getInputStream(entry)) {
                    java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                    byte[] buf = new byte[4096];
                    int n;
                    while ((n = in.read(buf)) > 0) {
                        out.write(buf, 0, n);
                    }
                    return new String(out.toByteArray(), "UTF-8");
                }
            }
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    private static Element acharLinha(Document sheet, int numero) {
        NodeList rows = sheet.getElementsByTagName("row");
        for (int i = 0; i < rows.getLength(); i++) {
            Element row = (Element) rows.item(i);
            if (String.valueOf(numero).equals(row.getAttribute("r"))) {
                return row;
            }
        }
        throw new AssertionError("Linha " + numero + " nao encontrada no sheet.");
    }

    private static Element acharCelula(Element row, String ref) {
        NodeList cells = row.getElementsByTagName("c");
        for (int i = 0; i < cells.getLength(); i++) {
            Element cell = (Element) cells.item(i);
            if (ref.equals(cell.getAttribute("r"))) {
                return cell;
            }
        }
        throw new AssertionError("Celula " + ref + " nao encontrada na linha.");
    }

    private static String valorInline(Element row, String ref) {
        NodeList cells = row.getElementsByTagName("c");
        for (int i = 0; i < cells.getLength(); i++) {
            Element cell = (Element) cells.item(i);
            if (!ref.equals(cell.getAttribute("r"))) {
                continue;
            }
            NodeList v = cell.getElementsByTagName("v");
            if (v.getLength() > 0) {
                return v.item(0).getTextContent();
            }
            return "";
        }
        return "";
    }

    private static String resolverCelula(Element row, String ref, List<String> sharedStrings) {
        NodeList cells = row.getElementsByTagName("c");
        for (int i = 0; i < cells.getLength(); i++) {
            Element cell = (Element) cells.item(i);
            if (!ref.equals(cell.getAttribute("r"))) {
                continue;
            }
            String tipo = cell.getAttribute("t");
            NodeList v = cell.getElementsByTagName("v");
            String raw = v.getLength() > 0 ? v.item(0).getTextContent() : "";
            if ("s".equals(tipo)) {
                int idx = Integer.parseInt(raw);
                return idx < sharedStrings.size() ? sharedStrings.get(idx) : "";
            }
            if ("inlineStr".equals(tipo)) {
                NodeList t = cell.getElementsByTagName("t");
                return t.getLength() > 0 ? t.item(0).getTextContent() : "";
            }
            return raw;
        }
        return "";
    }

    private static List<String> lerSharedStrings(byte[] bytes) throws Exception {
        String xml;
        try {
            xml = lerEntrada(bytes, "xl/sharedStrings.xml");
        } catch (IOException e) {
            return Collections.emptyList();
        }
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        NodeList siItems = doc.getElementsByTagName("si");
        List<String> out = new ArrayList<>(siItems.getLength());
        for (int i = 0; i < siItems.getLength(); i++) {
            Element si = (Element) siItems.item(i);
            NodeList tNodes = si.getElementsByTagName("t");
            out.add(tNodes.getLength() > 0 ? tNodes.item(0).getTextContent() : "");
        }
        return out;
    }

    private static int contarOcorrencias(String texto, String trecho) {
        int n = 0;
        int idx = 0;
        while ((idx = texto.indexOf(trecho, idx)) != -1) {
            n++;
            idx += trecho.length();
        }
        return n;
    }
}
