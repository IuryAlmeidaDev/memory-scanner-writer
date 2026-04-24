package br.gov.mt.sejudh.sigepen.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.RelatorioJumboDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRelatorioJumbo;
import br.gov.mt.sejudh.sigepen.enumerator.UnidadeJumbo;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioJumboDTO;

@Stateless
public class RelatorioJumboService {

    @Inject
    private RelatorioJumboDAO dao;

    @Inject
    private UnidadePrisionalService unidadeService;

    public List<RelatorioJumboDTO> buscar(FiltroRelatorioJumbo filtro) {
        validar(filtro);
        return dao.buscar(filtro);
    }

    public List<RelatorioJumboDTO> buscarRaw(FiltroRelatorioJumbo filtro) {
        validar(filtro);
        return dao.buscarRaw(filtro);
    }

    private void validar(FiltroRelatorioJumbo filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("Filtro é obrigatório.");
        }

        List<Long> unidades = filtro.getUnidadesSelecionadas();
        if (unidades == null || unidades.isEmpty()) {
            throw new IllegalArgumentException("Selecione ao menos uma unidade com Jumbo.");
        }

        if (!unidades.stream().allMatch(UnidadeJumbo::contemId)) {
            throw new IllegalArgumentException("Unidade fora da lista de unidades com Jumbo.");
        }

        List<Long> unidadesCadastradas = unidadeService.listarPorIds(UnidadeJumbo.todosIds())
                .stream()
                .map(UnidadePrisional::getId)
                .collect(Collectors.toList());

        if (!unidadesCadastradas.containsAll(unidades)) {
            throw new IllegalArgumentException("Unidade não está cadastrada ou está desativada.");
        }
    }
}
