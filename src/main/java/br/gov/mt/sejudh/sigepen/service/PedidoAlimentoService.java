package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.dao.PedidoAlimentoDAO;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;
import br.gov.mt.sejudh.sigepen.model.dto.DetalheItemEstruturaCardapioDTO;
import br.gov.mt.sejudh.sigepen.model.dto.DietaEspecialEstruturaDTO;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import com.google.gson.Gson;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidoAlimentoService {

    @Inject
    private PedidoAlimentoDAO dao;

    public void salvarRegistroRelatorio(
            final List<DetalheItemEstruturaCardapioDTO> itensCardapio,
            final List<DietaEspecialEstruturaDTO> refeicoesEspeciais) throws Exception {

        final Map<String, Object> dadosRelatorio = new HashMap<>();
        dadosRelatorio.put("itensCardapio", itensCardapio);
        dadosRelatorio.put("refeicoesEspeciais", refeicoesEspeciais);

        final Gson gson = new Gson();
        final String jsonConteudo = gson.toJson(dadosRelatorio);

        final PedidoAlimento pedidoAlimento = new PedidoAlimento();
        pedidoAlimento.setDataEmissao(new Date());
        pedidoAlimento.setUsuario(SecurityUtil.getMatriculaUsuario());
        pedidoAlimento.setJsonConteudo(jsonConteudo);

        dao.salvarRelatorio(pedidoAlimento);
    }
}