package br.gov.mt.sejudh.sigepen.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.PassagemDAO;
import br.gov.mt.sejudh.sigepen.dao.PedidoDAO;
import br.gov.mt.sejudh.sigepen.dao.ProcessoDAO;
import br.gov.mt.sejudh.sigepen.dao.RecuperandoDAO;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDesligamento;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.Processo;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Stateless
public class PassagemService {

	@Inject
	private PassagemDAO dao;
	
	@Inject
	private PedidoDAO pedTransitoDao;
	
	@Inject
	private ProcessoDAO processoDao;
	
	@Inject
	private RecuperandoDAO recuperandoDao;
	
	@Inject
	private RecuperandoService recuperandoService;
	
	public Passagem buscaPassagemPorId(Passagem passagem){
		return dao.buscaPassagemPorId(passagem);
	}

	public Recuperando buscarRecuperandoPorIdNovaPassagem(long id) {
		return dao.buscarRecuperandoPorIdNovaPassagem(id);
	}

	public Recuperando buscarRecuperandoPorIdDesligamento(long id) {
		return dao.buscarRecuperandoPorIdDesligamento(id);
	}
	
	public void desligar(Recuperando recuperando, Passagem passagem, PassagemLotacao lotacao) throws Exception {
		if (passagem.getDataSaida().after(Sigepen.getDataAtual())) {
			throw new ValidacaoException("Data de saída não pode ser maior que data atual.");
		}
		
		if (passagem.getDataSaida().before(lotacao.getDataEntrada()) || passagem.getDataSaida().equals(lotacao.getDataEntrada())) {
			throw new ValidacaoException("Data de saída menor ou igual que a data de entrada!");			
		}		
		
		if (pedTransitoDao.recuperandoEmMovimentacao(recuperando)) {
			throw new ValidacaoException("Recuperando em Movimentação, não pode ser Desligado!");			
		}
		
		if (passagem.getTipoDesligamento() == TipoDesligamento.ALVARA_DE_SOLTURA) {
			recuperando = this.buscarProcessosERegimeDoRecuperando(recuperando);
			
			if(!recuperando.getRegimeAtual().getDescricao().equals("Aberto") && !recuperando.getRegimeAtual().getDescricao().equals("Réu Solto")){
				throw new ValidacaoException("Recuperando com regime atual = \""+ recuperando.getRegimeAtual().getDescricao() +"\" não pode ser Desligado!");
			}
		}
		
		if (passagem.getTipoDesligamento() == TipoDesligamento.OUTRO_SISTEMA_PENITENCIARIO) {
			if (passagem.getEstabelecimentoDestino() == null) {
				throw new ValidacaoException("É necessário associar um estabelecimento.");
			}
		}
		
		recuperando.setStatus(StatusRecuperando.INATIVO);
		lotacao.setDataSaida(passagem.getDataSaida());

		recuperando.getPassagens().remove(recuperando.getPassagemAtual());
		passagem.getLotacao().remove(passagem.getLotacaoAtual());
		passagem.getLotacao().add(lotacao);
		recuperando.getPassagens().add(passagem);
		
		recuperandoDao.salvar(recuperando);
	}
	
	private Recuperando buscarProcessosERegimeDoRecuperando(Recuperando recuperando){
		List<Processo> processos = processoDao.processosPorRecuperando(recuperando.getId());
		
		if(!processos.isEmpty()) {
			Regime regime = recuperandoService.regimeAtualRecuperando(processos);
			
			if (regime != null) {
				recuperando.setRegimeAtual(regime);
			}
		}
		
		return recuperando;
	}
	
	public boolean possuiPasagemAberta(Recuperando rec){
		return dao.possuiPassagemAberta(rec);
	}
	
	public boolean validarDataNovaPasagem(Recuperando rec, Date dataEntrada){
		if(this.dao.possuiPassagem(rec)){
			Date ultimaDataSaida = dao.dataUltimaSaida(rec);
			if(dataEntrada.before(ultimaDataSaida)){
				return false;
			}
		}
		
		return true;
	}
}
