package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.model.Parametro;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@Stateless
public class RolloutAlimentacaoService {

    public static final String PARAM_ROLLOUT_UNIDADES = "ALIM_ROLLOUT_UNIDADES";

    @Inject
    private ParametroService parametroService;

    public boolean unidadeHabilitada(Long unidadeId) {
        String valor = obterValorParametro(PARAM_ROLLOUT_UNIDADES);
        if (valor == null || valor.trim().isEmpty()) {
            return true;
        }

        return Arrays.stream(valor.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .anyMatch(id -> id.equals(String.valueOf(unidadeId)));
    }

    String obterValorParametro(String nomeParametro) {
        List<Parametro> parametros = parametroService.getParametrosSistema();
        return parametros.stream()
                .filter(p -> nomeParametro.equalsIgnoreCase(p.getNome()))
                .map(Parametro::getValor)
                .findFirst()
                .orElse(null);
    }
}
