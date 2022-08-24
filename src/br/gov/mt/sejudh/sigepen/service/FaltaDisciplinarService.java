package br.gov.mt.sejudh.sigepen.service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.FaltaDisciplinarDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.FaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.dto.FaltaDisciplinarDTO;

@Stateless
public class FaltaDisciplinarService {
	
	@Inject
	private FaltaDisciplinarDAO dao;
	
	@Inject
	private AuditoriaDAO auditDAO;

	@Inject RecuperandoService recuperandoService;

	public void salvar(FaltaDisciplinar faltaDisciplinar, SituacaoFaltaDisciplinar novaSituacao) throws ValidacaoException {

		if(faltaDisciplinar.getId() == 0){
			prepararSituacaoPendente(faltaDisciplinar);
		}

		validarMudancaSituacao(faltaDisciplinar,novaSituacao);

		validarPorSituacao(faltaDisciplinar, novaSituacao);

		prepararPorSituacao(faltaDisciplinar, novaSituacao);

		dao.salvar(faltaDisciplinar);
	}

	private void validarPorSituacao(FaltaDisciplinar faltaDisciplinar, SituacaoFaltaDisciplinar novaSituacao) throws ValidacaoException{
		switch (novaSituacao){
			case FINALIZADA:
			case CONCLUIDA:
				validarSituacaoConcluida(faltaDisciplinar);
				break;
			case EM_ANDAMENTO:
				validarSituacaoEmAndamento(faltaDisciplinar);
				break;
			case PENDENTE:
				validarSituacaoPendente(faltaDisciplinar);
				break;
		}
	}

	private void prepararPorSituacao(FaltaDisciplinar faltaDisciplinar, SituacaoFaltaDisciplinar novaSituacao) {
		switch (novaSituacao){
			case PENDENTE:
				prepararSituacaoPendente(faltaDisciplinar);
				break;
			case EM_ANDAMENTO:
				prepararSituacaoEmAndamento(faltaDisciplinar);
				break;
			case CONCLUIDA:
				prepararSituacaoConcluida(faltaDisciplinar);
				break;
			case CANCELADA:
				prepararSituacaoCancelada(faltaDisciplinar);
				break;
		}
	}
	public void cancelar(FaltaDisciplinar faltaDisciplinar) throws ValidacaoException{
		validarCancelamento(faltaDisciplinar);
		faltaDisciplinar.setSituacao(SituacaoFaltaDisciplinar.CANCELADA);
		faltaDisciplinar.setDataSituacaoCancelada(new Date());
		dao.salvar(faltaDisciplinar);
	}

	private void validarCancelamento(FaltaDisciplinar faltaDisciplinar) throws ValidacaoException {
		if(faltaDisciplinar.getMotivoCancelamento() == null || faltaDisciplinar.getMotivoCancelamento().equals("")){
			throw new ValidacaoException("Necessário informar o Motivo do Cancelamento!");
		}
	}

	private void validarSituacaoPendente(FaltaDisciplinar faltaDisciplinar) throws ValidacaoException{
		
		Date data = new Date();
		
		if(faltaDisciplinar.getRecuperando() == null){
			throw new ValidacaoException("Necessário informar o recuperando!");
		}
		if(faltaDisciplinar.getTipo() == null){
			throw new ValidacaoException("Tipo Falta Disciplinar é obrigatória!");
		}
		if(faltaDisciplinar.getData() == null){
			throw new ValidacaoException("Data do ocorrido é obrigatória!");
		}
		
		if(faltaDisciplinar.getData().after(data)){
			throw new ValidacaoException("Data do ocorrido não poder ser superior a data atual!");
		}
		
		if(faltaDisciplinar.getFatoDescricao() == null || faltaDisciplinar.getFatoDescricao().trim().equals("")){
			throw new ValidacaoException("Descrição do fato é obrigatório!");
		}
		if(faltaDisciplinar.getFatoData()==null){
			throw new ValidacaoException("Data do documento é obrigatória!");
		}
		
		if(faltaDisciplinar.getFatoData().after(data)){
			throw new ValidacaoException("Data do documento não poder ser superior a data atual!");
		}
		
		if(faltaDisciplinar.getFatoNumeroDocumento()==null || faltaDisciplinar.getFatoNumeroDocumento().trim().equals("")) {
			throw new ValidacaoException("Número do documento é obrigatório!");
		}

		if(faltaDisciplinar.getEstruturaOcorrencia() == null){
			throw new ValidacaoException("É necessário adicionar uma estrutura.");
		}
		try {
			validarDataFaltaAnteriorEntrada(faltaDisciplinar);
		} catch (Exception e) {
			throw new ValidacaoException("Recuperando não estava ativo na unidade na data da ocorrência.");
		}
		
		
		
	}

	private void validarDataFaltaAnteriorEntrada(FaltaDisciplinar faltaDisciplinar) throws ValidacaoException {
		Recuperando recuperandoComPassagens = recuperandoService.buscarRecuperandoComPassagens(faltaDisciplinar.getRecuperando().getId());

		Optional<Passagem> optPassagem = recuperandoComPassagens.getPassagens().stream()
				.min(Comparator.comparing(Passagem::getId));

		if (optPassagem.isPresent()) {
			Passagem primeiraPassagem = optPassagem.get();
			if(faltaDisciplinar.getData().before(primeiraPassagem.getDataEntrada())){
				throw new ValidacaoException("Data de Ocorrência não pode ser menor que a data da primeira passagem do recuperando.");
			}
		}else{
			throw new ValidacaoException("Recuperando não estava ativo na unidade na data da ocorrência.");
		}
	}


	public void excluir(FaltaDisciplinarDTO pedido) {
		FaltaDisciplinar faltaDisciplinar = buscaFaltaDisciplinarPorId(pedido.getId());
		dao.excluir(faltaDisciplinar);
	}		

	public List<FaltaDisciplinarDTO> buscaPorFiltro(FiltroFaltaDisciplinar filtro, boolean isBuscaRelatorio) {
		return dao.buscaPorFiltro(filtro, isBuscaRelatorio);
    }
	
	public FaltaDisciplinar buscaFaltaDisciplinarPorId(long id) {
		FaltaDisciplinar faltaDisciplinar = dao.buscarFaltaDisciplinarCompletaPorId(id);
		
		if (faltaDisciplinar != null) {
			faltaDisciplinar.setAutorRegistro(auditDAO.autorRegistro(faltaDisciplinar, faltaDisciplinar.getId()));
			faltaDisciplinar.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(faltaDisciplinar, faltaDisciplinar.getId()));
		}
		
		return faltaDisciplinar;
    }

	public List<FaltaDisciplinar> listarFaltaDisciplinarAtivo() {
		return dao.listarFaltaDisciplinarAtivo();
	}
		
	public int quantidadeFiltrados(FiltroFaltaDisciplinar filtro){
		return dao.quantidadeFiltrados(filtro);
	}

	private void prepararSituacaoPendente(FaltaDisciplinar faltaDisciplinar){
		faltaDisciplinar.setSituacao(SituacaoFaltaDisciplinar.PENDENTE);
		if(faltaDisciplinar.getDataSituacaoEmAndamento() == null ) {
			faltaDisciplinar.setDataSituacaoPendente(new Date());
		}
	}

	private void prepararSituacaoEmAndamento(FaltaDisciplinar faltaDisciplinar){

		faltaDisciplinar.setSituacao(SituacaoFaltaDisciplinar.EM_ANDAMENTO);
		if(faltaDisciplinar.getDataSituacaoEmAndamento() == null) {
			faltaDisciplinar.setDataSituacaoEmAndamento(new Date());
		}
	}

	private void prepararSituacaoConcluida(FaltaDisciplinar faltaDisciplinar){
		faltaDisciplinar.setSituacao(SituacaoFaltaDisciplinar.CONCLUIDA);
		if(faltaDisciplinar.getDataSituacaoConcluida() == null) {
			faltaDisciplinar.setDataSituacaoConcluida(new Date());
		}
	}

	private void prepararSituacaoCancelada(FaltaDisciplinar faltaDisciplinar){

		faltaDisciplinar.setSituacao(SituacaoFaltaDisciplinar.CANCELADA);
		if(faltaDisciplinar.getDataSituacaoCancelada() == null) {
			faltaDisciplinar.setDataSituacaoCancelada(new Date());
		}
	}

	/**
	 * Validar se a mudança de situação é possível ser realizada para novaSituacao informada.
	 * @param faltaDisciplinar - falta disciplinar atual
	 * @param novaSituacao - Nova situação a ser atribuida a falta disciplinar
	 */
	private void validarMudancaSituacao(FaltaDisciplinar faltaDisciplinar, SituacaoFaltaDisciplinar novaSituacao) throws  ValidacaoException{
		if(faltaDisciplinar == null || faltaDisciplinar.getSituacao() == null){
			throw  new ValidacaoException("Informe uma falta disciplinar com situação definida!");
		}
		if(novaSituacao == null){
			throw new ValidacaoException("Informe a nova situação desejada!");
		}
		//retornar caso não tenha alteração de situação.
		if(faltaDisciplinar.getSituacao().equals(novaSituacao)){
			return;
		}
		if(faltaDisciplinar.getSituacao().isSituacaoPendente() &&
				(!novaSituacao.equals(SituacaoFaltaDisciplinar.EM_ANDAMENTO) && !novaSituacao.equals(SituacaoFaltaDisciplinar.CANCELADA) )
		){
			throw new ValidacaoException("Falta disciplina na Situação Pendente, só pode ser modificada para situaçãoes: Em Andamento ou Cancelada");
		}
		if(faltaDisciplinar.getSituacao().isSituacaoEmAndamento() &&
				(!novaSituacao.equals(SituacaoFaltaDisciplinar.CONCLUIDA) && !novaSituacao.equals(SituacaoFaltaDisciplinar.CANCELADA))
		){
			throw new ValidacaoException("Falta disciplina na Situação Em Andamento, só pode ser modificada para situaçãoes: Concluída ou Cancelada");
		}
		if(faltaDisciplinar.getSituacao().isSituacaoConcluida() &&
				(!novaSituacao.equals(SituacaoFaltaDisciplinar.CANCELADA) && !novaSituacao.equals(SituacaoFaltaDisciplinar.FINALIZADA))){
			throw new ValidacaoException("Falta disciplina na Situação Concluída, só pode ser modificada para situaçãoes: Cancelada ou Finalizada");
		}
		if(faltaDisciplinar.getSituacao().isSituacaoCancelada()){
			throw new ValidacaoException("Falta disciplinar na Situação Cancelada não pode ser ter sua situação modificada.");
		}
	}

	private void validarSituacaoEmAndamento(FaltaDisciplinar faltaDisciplinar) throws ValidacaoException {
		validarSituacaoPendente(faltaDisciplinar);
		if (faltaDisciplinar.getPadNumeroDocumento()==null) {
			throw new ValidacaoException("Número do documento PAD obrigatório!");
		}
		if(faltaDisciplinar.getPadDataDocumento() == null){
			throw new ValidacaoException("Data do documento PAD obrigatória!");
		}
		if(faltaDisciplinar.getPadDescricao()==null || faltaDisciplinar.getFatoDescricao().trim().equals("")){
			throw new ValidacaoException("Descrição do PAD obrigatória!");
		}
	}

	private void validarSituacaoConcluida(FaltaDisciplinar faltaDisciplinar) throws ValidacaoException{
		validarSituacaoPendente(faltaDisciplinar);
		validarSituacaoEmAndamento(faltaDisciplinar);

		if(faltaDisciplinar.getParecerNumero() == null || faltaDisciplinar.getParecerNumero().trim().equals("")){
			throw new ValidacaoException("Número do parecer obrigatório!");
		}
		if(faltaDisciplinar.getParecerData() == null) {
			throw new ValidacaoException("Data do parecer obrigatória!");
		}
		if(faltaDisciplinar.getParecerDescricao() == null || faltaDisciplinar.getParecerDescricao().trim().equals("")){
			throw new ValidacaoException("Descrição do parecer obrigatório!");
		}
		if(faltaDisciplinar.getReconhecida() == null ){
			throw new ValidacaoException("Campo Falta Reconhecida é obrigatório!");
		}
		if(faltaDisciplinar.getReconhecida().equals(SimNao.S)){
			if(faltaDisciplinar.getDataInicio() == null ){
				throw new ValidacaoException("Data início da falta disciplinar é obrigatório!");
			}
			if(faltaDisciplinar.getDataFim() == null){
				throw new ValidacaoException("Data fim da falta disciplinar é obrigatório!");
			}
			if(faltaDisciplinar.getDataFim().before(faltaDisciplinar.getDataInicio())){
				throw new ValidacaoException("Data fim da falta disciplinar menor que a data de início!");
			}
			if(faltaDisciplinar.getDataInicio().after(faltaDisciplinar.getDataFim())){
				throw new ValidacaoException("Data início da falta disciplinar maior que data fim!");
			}
		}
	}
	
}
