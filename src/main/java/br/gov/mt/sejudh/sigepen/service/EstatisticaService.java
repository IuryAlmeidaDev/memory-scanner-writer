package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.dao.MovimentacaoExternaDAO;
import br.gov.mt.sejudh.sigepen.dao.MovimentacaoInternaDAO;
import br.gov.mt.sejudh.sigepen.dao.PassagemLotacaoDAO;
import br.gov.mt.sejudh.sigepen.dao.RecuperandoDAO;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;

@Stateless
public class EstatisticaService {

    @Inject
    private RecuperandoDAO recuperandoDAO;

    @Inject
    private MovimentacaoInternaDAO movimentacaoInternaDAO;

    @Inject
    private MovimentacaoExternaDAO movimentacaoExternaDAO;

    @Inject
    private PassagemLotacaoDAO passagemLotacaoDAO;

    public Long getTotalRecuperandosNaUnidade(UnidadePrisional unidade, Date dataPlantao) {
        return recuperandoDAO.countAtivosPorUnidadeNaData(unidade.getId(), dataPlantao);
    }

    public Long getTotalMovimentacoesInternas(UnidadePrisional unidade, Date inicio, Date fim) {
        return movimentacaoInternaDAO.countPorUnidadeNaData(unidade.getId(), inicio, fim);
    }

    public Long getTotalMovimentacoesExternas(UnidadePrisional unidade, Date inicio, Date fim) {
        return movimentacaoExternaDAO.countPorUnidadeNaData(unidade.getId(), inicio, fim);
    }

    public Long getTotalEntradasNaUnidade(UnidadePrisional unidade, Date inicio, Date fim) {
        return passagemLotacaoDAO.countEntradasPorUnidadeNaData(unidade.getId(), inicio, fim);
    }

    public Long getTotalSaidasNaUnidade(UnidadePrisional unidade, Date inicio, Date fim) {
        return passagemLotacaoDAO.countSaidasPorUnidadeNaData(unidade.getId(), inicio, fim);
    }

}