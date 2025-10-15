package br.gov.mt.sejudh.sigepen.relatorio.textos;


import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoAtestado;
import br.gov.mt.sejudh.sigepen.model.Estabelecimento;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
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
	
	public static TextoDoRelatorio getTexto(TipoAtestado tipoAtestado, Recuperando rec, PassagemLotacao lotacao, boolean imprimirComNomeSocial) {
	    return getTexto(tipoAtestado, rec, lotacao, null, imprimirComNomeSocial);
	}
	
	public static TextoDoRelatorio getTexto(TipoAtestado tipoAtestado, Recuperando rec, PassagemLotacao lotacao, String comportamento, boolean imprimirComNomeSocial) {
		switch (tipoAtestado) {
			case Comportamento:
					return getTextoComportamento(tipoAtestado, rec, lotacao, comportamento, imprimirComNomeSocial);
			case Permanencia:
				return getTextoPermanencia(tipoAtestado, rec, lotacao, imprimirComNomeSocial);
			default:
				return new TextoDoRelatorio(null);
		}
	}

	
	private static TextoDoRelatorio getTextoComportamento(TipoAtestado tipoAtestado, Recuperando rec, PassagemLotacao lotacao, String comportamento, boolean imprimirComNomeSocial) {
	    validaComportamento(tipoAtestado, rec, lotacao, comportamento);
	    validaRecuperando(rec);
	    validaDataEntrada(lotacao);

	    boolean temPai = rec.getPessoa().getNomePai() != null;
	    boolean deveImprimirNomeSocial = (rec.getPessoa().getNomeSocial() != null && imprimirComNomeSocial);
	    boolean ativoNaUnidade = isRecuperandoAtivoNaMesmaUnidadeLogada(rec);

	    StringBuilder buildResultado = new StringBuilder();
	    
	    if(deveImprimirNomeSocial) {
	    	buildResultado.append("\tATESTAMOS para os devidos fins, que o(a) recuperando(a) [nome], nome social [nomeSocial], matrícula n° [matricula], ");
	    } else {
	    	buildResultado.append("\tATESTAMOS para os devidos fins, que o(a) recuperando(a) [nome], matrícula n° [matricula], "); 
	    }
	
	    buildResultado.append("filho(a) de ");
	    if (temPai) {
	        buildResultado.append("[nomePai] e ");
	    }
	    buildResultado.append("[nomeMae], data de nascimento [dataNascimento], oriundo de [estabelecimento], ");

	    if (ativoNaUnidade) {
	        buildResultado.append("deu entrada nesta Unidade Prisional no dia [dataEntrada], possui [comportamento] Comportamento Carcerário, uma vez que, [nao] cometeu falta de natureza leve, média ou grave.");
	    } else {
	        buildResultado.append("deu entrada nesta Unidade Prisional em [dataEntrada] e permaneceu até a data [dataSaida], possuindo [comportamento] Comportamento Carcerário, uma vez que, [nao] cometeu falta de natureza leve, média ou grave.");
	    }

	    String resultado = buildResultado.toString();
	    Pessoa pessoa = rec.getPessoa();
	    Estabelecimento procedencia = rec.getUltimaPassagem().getPassagemProcedencia().getProcedencia();

	    resultado = resultado.replace("[nome]", pessoa.getNome().toUpperCase());
	    resultado = resultado.replace("[matricula]", String.valueOf(rec.getId()));
	    resultado = resultado.replace("[nomeMae]", pessoa.getNomeMae().toUpperCase());
	    resultado = resultado.replace("[estabelecimento]", procedencia.getNome());
	    resultado = resultado.replace("[dataNascimento]", MaskUtil.getFormatoData().format(pessoa.getDataNascimento()));
	    resultado = resultado.replace("[comportamento]", comportamento.toUpperCase());

	    resultado = resultado.replace("[nao]", comportamento.equalsIgnoreCase("MAU") ? "" : "não");

	    if (temPai) {
	        resultado = resultado.replace("[nomePai]", pessoa.getNomePai().toUpperCase());
	    }
	    
	    if(deveImprimirNomeSocial) {
	    	resultado = resultado.replace("[nomeSocial]", pessoa.getNomeSocial().toUpperCase());
	    }

	    String dataEntrada = MaskUtil.getFormatoDataHora().format(lotacao.getDataEntrada());
	    resultado = resultado.replace("[dataEntrada]", dataEntrada);

	    if (!ativoNaUnidade && lotacao.getDataSaida() != null) {
	        String dataSaida = MaskUtil.getFormatoDataHora().format(lotacao.getDataSaida());
	        resultado = resultado.replace("[dataSaida]", dataSaida);
	    }

	    return new TextoDoRelatorio(resultado);
	}

	private static TextoDoRelatorio getTextoPermanencia(TipoAtestado tipoAtestado, Recuperando rec, PassagemLotacao lotacao, boolean imprimirComNomeSocial) {
		validaPermanencia(tipoAtestado, rec, lotacao);
	    validaRecuperando(rec);
	    validaDataEntrada(lotacao);
	    
	    boolean deveUsarNomeSocial = (rec.getPessoa().getNomeSocial() != null && imprimirComNomeSocial);
		boolean temPai = (rec.getPessoa().getNomePai() != null);
		boolean ehBrasileiro = (rec.getPessoa().getNacionalidade().getId() == 1L);
		boolean isRecuperandoAtivoNaMesmaUnidadeLogada = isRecuperandoAtivoNaMesmaUnidadeLogada(rec);
		StringBuilder buildResultado = new StringBuilder();
		
		if(deveUsarNomeSocial) {
			buildResultado.append("\tATESTO para os devidos fins, que o(a) recuperando(a) [nome], nome social [nomeSocial], matrícula n° [matricula], "
					+ "filho(a) de ");
		} else {
			buildResultado.append("\tATESTO para os devidos fins, que o(a) recuperando(a) [nome], matrícula n° [matricula], "
					+ "filho(a) de ");
		}
		
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
		resultado = resultado.replace("[matricula]", String.valueOf(rec.getId()));
		resultado = resultado.replace("[nomeMae]", rec.getPessoa().getNomeMae().toUpperCase());
		resultado = resultado.replace("[dataNascimento]", MaskUtil.getFormatoData().format(rec.getPessoa().getDataNascimento()));

		if(deveUsarNomeSocial) 
			resultado = resultado.replace("[nomeSocial]", rec.getPessoa().getNomeSocial().toUpperCase());
		
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
	
	private static void validaRecuperando(Recuperando rec) {
		Pessoa pessoa = rec.getPessoa();
		if(pessoa == null) {
			throw new IllegalArgumentException("Pessoa não pode ser vazio.");
		}
		
		if(pessoa.getNome() == null || pessoa.getNomeMae() == null|| pessoa.getDataNascimento() == null ) {
			throw new IllegalArgumentException("Nome, nome da mae ou data de nascimento não pode ser vazio.");
		}
		
		Passagem ultimaPassagem = rec.getUltimaPassagem();
		if(ultimaPassagem == null || ultimaPassagem.getPassagemProcedencia() == null || ultimaPassagem.getPassagemProcedencia().getProcedencia() == null ||
				ultimaPassagem.getPassagemProcedencia().getProcedencia().getNome() == null) {
			throw new IllegalArgumentException("Informacoes da procedencia estão incompletas.");
		}
	}
	
	private static void validaComportamento(TipoAtestado tipoAtestado, Recuperando rec, PassagemLotacao lotacao, String comportamento) {
		if(tipoAtestado == null || rec == null || lotacao == null || comportamento == null) {
			throw new IllegalArgumentException("Informacoes da procedencia estão incompletas");
		}
	}
	
	private static void validaPermanencia(TipoAtestado tipoAtestado, Recuperando rec, PassagemLotacao lotacao) {
		if(tipoAtestado == null || rec == null || lotacao == null) {
			throw new IllegalArgumentException("Informacoes da procedencia estão incompletas.");
		}
	}
	
	private static void validaDataEntrada(PassagemLotacao lotacao) {
		if(lotacao.getDataEntrada() == null) {
			throw new IllegalArgumentException("Data de entrada da lotacao esta vazia.");
		}
	}

	
}
