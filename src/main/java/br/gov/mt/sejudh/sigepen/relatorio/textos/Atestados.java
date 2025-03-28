package br.gov.mt.sejudh.sigepen.relatorio.textos;

import org.apache.commons.lang3.StringUtils;

import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoAtestado;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.TextoDoRelatorio;
import br.gov.mt.sejudh.sigepen.util.MaskUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

public class Atestados {
	
	public static String getNomeRelatorio(TipoAtestado atestadoSelecionado) {
		switch (atestadoSelecionado) {
			case Comportamento:
				return "ATESTADO COMPROBATÓRIO DE COMPORTAMENTO CARCERÁRIO";
			case Permanencia:
				return "ATESTADO DE PERMANÊNCIA CARCERÁRIA";
			default:
				return "ATESTADO";
		}
	}
	
	public static TextoDoRelatorio getTexto(TipoAtestado tipoAtestado, Recuperando rec, PassagemLotacao lotacao, String comportamento) {
		switch (tipoAtestado) {
			case Comportamento:
				return getTextoComportamento(tipoAtestado, rec, lotacao, comportamento);
			case Permanencia:
				return getTextoPermanencia(tipoAtestado, rec, lotacao);
			default:
				return new TextoDoRelatorio(null);
		}
	}
	
	public static TextoDoRelatorio getTexto(TipoAtestado tipoAtestado, Recuperando rec, PassagemLotacao lotacao) {
		return getTexto(tipoAtestado, rec, lotacao, null);
	}
	
	private static TextoDoRelatorio getTextoComportamento(TipoAtestado tipoAtestado, Recuperando rec, PassagemLotacao lotacao, String comportamento) {
		boolean temPai = (rec.getPessoa().getNomePai() != null);
		boolean isRecuperandoAtivoNaMesmaUnidadeLogada = isRecuperandoAtivoNaMesmaUnidadeLogada(rec);
		StringBuilder buildResultado = new StringBuilder();
		
		buildResultado.append("\tATESTAMOS para os devidos fins, que o(a) recuperando(a) [nome], nome social [nomeSocial], matrícula n° [matricula], "
				+ "filho(a) de ");
		
		if (temPai)
			buildResultado.append("[nomePai] e ");

		buildResultado.append("[nomeMae], data de nascimento [dataNascimento], oriundo de [estabelecimento], ");
		
		
		if (isRecuperandoAtivoNaMesmaUnidadeLogada) {
			buildResultado.append("deu entrada nesta Unidade Prisional no dia [dataEntrada], possui [comportamento] "
					+ "Comportamento Carcerário, uma vez que, [nao] cometeu falta de natureza leve, média ou grave.");
		} else {
			buildResultado.append("deu entrada nesta Unidade Prisional em [dataEntrada] e permaneceu até a data [dataSaida], "
					+ "possuindo [comportamento] Comportamento Carcerário, uma vez que, [nao] cometeu falta de natureza leve, média ou grave.");
		}

		String resultado = buildResultado.toString();
		resultado = resultado.replace("[nome]", rec.getPessoa().getNome().toUpperCase());
		resultado = resultado.replace("[nomeSocial]", rec.getPessoa().getNomeSocial().toUpperCase());
		resultado = resultado.replace("[matricula]", String.valueOf(rec.getId()));
		resultado = resultado.replace("[nomeMae]", rec.getPessoa().getNomeMae().toUpperCase());
		resultado = resultado.replace("[estabelecimento]", rec.getUltimaPassagem().getPassagemProcedencia().getProcedencia().getNome());
		resultado = resultado.replace("[dataNascimento]", MaskUtil.getFormatoData().format(rec.getPessoa().getDataNascimento()));
		resultado = resultado.replace("[comportamento]", comportamento.toUpperCase());
		
		if (comportamento.equals("MAU")) {
			resultado = resultado.replace("[nao] ", StringUtils.EMPTY);
		} else {
			resultado = resultado.replace("[nao]", "não");
		}
		
		
		
		if (temPai)
			resultado = resultado.replace("[nomePai]", rec.getPessoa().getNomePai().toUpperCase());
		
		if (isRecuperandoAtivoNaMesmaUnidadeLogada) {
			resultado = resultado.replace("[dataEntrada]", MaskUtil.getFormatoDataHora().format(lotacao.getDataEntrada()));
		} else {
			resultado = resultado.replace("[dataEntrada]", MaskUtil.getFormatoDataHora().format(lotacao.getDataEntrada()));
			resultado = resultado.replace("[dataSaida]", MaskUtil.getFormatoDataHora().format(lotacao.getDataSaida()));
		}
		
		return new TextoDoRelatorio(resultado);
	}
	
	private static TextoDoRelatorio getTextoPermanencia(TipoAtestado tipoAtestado, Recuperando rec, PassagemLotacao lotacao) {
		boolean temPai = (rec.getPessoa().getNomePai() != null);
		boolean ehBrasileiro = (rec.getPessoa().getNacionalidade().getId() == 1L);
		boolean isRecuperandoAtivoNaMesmaUnidadeLogada = isRecuperandoAtivoNaMesmaUnidadeLogada(rec);
		StringBuilder buildResultado = new StringBuilder();
		
		buildResultado.append("\tATESTO para os devidos fins, que o(a) recuperando(a) [nome], nome social [nomeSocial], matrícula n° [matricula], "
				+ "filho(a) de ");
		
		if (temPai)
			buildResultado.append("[nomePai] e ");
		
		buildResultado.append("[nomeMae], nascido(a) em [dataNascimento], ");
		
		if (ehBrasileiro) {
			buildResultado.append("natural de [cidade]/[estado], ");
		} else {
			buildResultado.append("natural do país [pais], ");
		}
		
		if (isRecuperandoAtivoNaMesmaUnidadeLogada) {
			buildResultado.append("deu entrada nesta Unidade Prisional em [dataEntrada], onde permanece detido em Regime Fechado "
				+ "à disposição da Justiça.");
		} else {
			buildResultado.append("deu entrada nesta Unidade Prisional em [dataEntrada] e permaneceu até a data [dataSaida], "
					+ "onde permaneceu detido em Regime Fechado à disposição da Justiça.");
		}
		
		String resultado = buildResultado.toString();
		resultado = resultado.replace("[nome]", rec.getPessoa().getNome().toUpperCase());
		resultado = resultado.replace("[nomeSocial]", rec.getPessoa().getNome().toUpperCase());
		resultado = resultado.replace("[matricula]", String.valueOf(rec.getId()));
		resultado = resultado.replace("[nomeMae]", rec.getPessoa().getNomeMae().toUpperCase());
		resultado = resultado.replace("[dataNascimento]", MaskUtil.getFormatoData().format(rec.getPessoa().getDataNascimento()));

		if (temPai)
			resultado = resultado.replace("[nomePai]", rec.getPessoa().getNomePai().toUpperCase());
		
		if (ehBrasileiro) {
			resultado = resultado.replace("[cidade]", rec.getPessoa().getNaturalidade().getNome());
			resultado = resultado.replace("[estado]", rec.getPessoa().getNaturalidade().getEstado().getUf());
		} else {
			resultado = resultado.replace("[pais]", rec.getPessoa().getPais().getNome());
		}
		
		if (isRecuperandoAtivoNaMesmaUnidadeLogada) {
			resultado = resultado.replace("[dataEntrada]", MaskUtil.getFormatoDataHora().format(lotacao.getDataEntrada()));
		} else {
			resultado = resultado.replace("[dataEntrada]", MaskUtil.getFormatoDataHora().format(lotacao.getDataEntrada()));
			resultado = resultado.replace("[dataSaida]", MaskUtil.getFormatoDataHora().format(lotacao.getDataSaida()));
		}
		
		return new TextoDoRelatorio(resultado);
	}
	
	private static boolean isRecuperandoAtivoNaMesmaUnidadeLogada(Recuperando rec) {
		if (rec.getStatus() == StatusRecuperando.ATIVO) {
			return (rec.getUltimaPassagem().getUltimaLotacao().getEstrutura().getUnidadePrisional().getId() == SecurityUtil.getUnidadeSelecionada().getId());
		} else {
			return false;
		}
	}
}
