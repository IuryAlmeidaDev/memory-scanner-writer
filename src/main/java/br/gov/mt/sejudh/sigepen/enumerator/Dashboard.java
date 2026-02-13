package br.gov.mt.sejudh.sigepen.enumerator;

// Descrição do enum é o nome da permissão para visualizar o dashboard.
public enum Dashboard {
	RECUPERANDOS_ATIVOS_SISTEMA("TotalRecuperandosAtivos"),
	RECUPERANDOS_ATIVOS_UNIDADE("TotalRecuperandosAtivosNaUnidade"),
	RECUPERANDOS_ATIVOS_CADASTRO_INCOMPLETO_UNIDADE("TotalRecuperandosComCadastroIncompleto"),
	RECUPERANDOS_TEMPORARIOS_NA_UNIDADE_SOLTURA_A_VENCER("TemporariosNaUnidadeAVencer"),
	RECUPERANDOS_TEMPORARIOS_NA_UNIDADE_SOLTURA_VENCIDA("TemporariosNaUnidadeVencido"),
	RECUPERANDOS_TEMPORARIOS_NA_UNIDADE_SOLTURA_VENCEHOJE("TemporariosNaUnidadeVenceHoje"),
	RECUPERANDOS_TEMPORARIOS_NO_SISTEMA_SOLTURA_A_VENCER("TemporariosNoSistemaAVencer"),
	RECUPERANDOS_TEMPORARIOS_NO_SISTEMA_SOLTURA_VENCIDA("TemporariosNoSistemaVencido"),
	RECUPERANDOS_TEMPORARIOS_NO_SISTEMA_SOLTURA_VENCEHOJE("TemporariosNoSistemaVenceHoje"),
	RECUPERANDOS_PROVISORIOS_90_180_DIAS("RecuperandosProvisorios90a180Dias"),
	RECUPERANDOS_PROVISORIOS_MAIS_180_DIAS("RecuperandosProvisoriosMais180Dias"),
	SOLICITACAO_VISITA_PENDENTE("TotalSolicitacaoVisitasPendentesPorUnidade"),
	TOTAL_CREDENCIAMENTO_RECEBIMENTO_PENDENTE("TotalCredenciamentoRecebimentoPorUnidade"),
	TOTAL_CREDENCIAMENTO_CHECAGEM_PENDENTE("TotalCredenciamentoChecagemPorUnidade"),
	TOTAL_CREDENCIAMENTO_CONFERENCIA_PENDENTE("TotalCredenciamentoConferenciaPorUnidade"),
	TOTAL_CREDENCIAMENTO_ANALISE_PENDENTE("TotalCredenciamentoAnalisePorUnidade"),
	TOTAL_CREDENCIAMENTO_APROVACAO_PENDENTE("TotalCredenciamentoAprovacaoPorUnidade"),
	TOTAL_MOVIMENTACAO_INTERNA_PENDENTE("TotalMovimentacaoInternaPendente"),
	TOTAL_MOVIMENTACAO_INTERNA_EM_ANDAMENTO("TotalMovimentacaoInternaEmAndamento"),
	TOTAL_MOVIMENTACAO_INTERNA_AGUARDANDO_FINALIZAR("TotalMovimentacaoInternaAguardandoFinalizar"),
	TOTAL_MOVIMENTACAO_EXTERNA_PENDENTE("TotalMovimentacaoExternaPendente"),
	TOTAL_MOVIMENTACAO_EXTERNA_EM_ANDAMENTO("TotalMovimentacaoExternaEmAndamento"),
	TOTAL_MOVIMENTACAO_EXTERNA_AGUARDANDO_FINALIZAR("TotalMovimentacaoExternaAguardandoFinalizar"),
	TOTAL_MOVIMENTACAO_INTERNA_FINALIZADO_PARCIAL("TotalMovimentacaoInternaFinalizadoParcial"),
	TOTAL_MOVIMENTACAO_EXTERNA_FINALIZADO_PARCIAL("TotalMovimentacaoExternaFinalizadoParcial"),
	TOTAL_TRANSFERENCIA_PENDENTE("TotalTransferenciaPendente"),
	TOTAL_TRANSFERENCIA_EM_ANDAMENTO("TotalTransferenciaEmAndamento"),
	TOTAL_TRANSFERENCIA_FINALIZADO_PARCIAL("TotalTransferenciaFinalizadoParcial"),
	TOTAL_RECEBER_TRANSFERENCIA_VINCULADO("totalReceberTransferenciaViculada"),
	TOTAL_RECEBER_TRANSFERENCIA_EM_ANDAMENTO("TotalReceberTransferenciaEmAndamento"),
	TOTAL_REMANEJAMENTO_EM_ANDAMENTO("TotalRemanejamentoEmAndamento");

	private String descricao;
	
	Dashboard(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public static Dashboard getValue(String descricao) {
		if (descricao == null || descricao == "") return null;
		
		for (Dashboard cat : Dashboard.values()) {
			if (cat.getDescricao().equals(descricao)) {
				return cat;
			}
		}
		
		throw new IllegalArgumentException("Não existe dashboard com a descricao: " + descricao);
	}
}
