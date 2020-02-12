package br.gov.mt.sejudh.sigepen.util;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

import br.gov.mt.sejudh.sigepen.enumerator.TipoContato;
import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.TextoDoRelatorio;

public class TextoRelatorioPadrao {

	public static TextoDoRelatorio getTextoTermoDeComparecimentoMensal(Recuperando rec) {
		StringBuilder buildResultado = new StringBuilder();
		buildResultado.append("ATESTO, para os devidos fins, que nesta data o recuperando: [nome], Processo: ____________________, "
				+ "filho(a) de [nomeMae], registrado no Sistema de Gestão Penitenciária SIGEPEN sob a matrícula [matricula] compareceu "
				+ "a unidade da Secretaria Adjunta de Administração Penitenciaria, para dar cumprimento das medidas a ela imposta, informando "
				+ "residir à rua [logradouro], bairro [bairro], cidade de [cidadeuf], telefone [telefone].");
		
		String resultado = buildResultado.toString();
		resultado = resultado.replace("[nome]", rec.getPessoa().getNome().toUpperCase());
		resultado = resultado.replace("[matricula]", String.valueOf(rec.getId()));
		resultado = resultado.replace("[nomeMae]", rec.getPessoa().getNomeMae().toUpperCase());
		
		Comparator<Endereco> comparadorEndereco = Comparator.comparing(Endereco::getId);
		Comparator<Contato> comparadorContato = Comparator.comparing(Contato::getId);
		
		Optional<Endereco> endereco = rec.getPessoa().getEnderecos().stream().max(comparadorEndereco);
		
		Predicate<Contato> condicaoCelular = c -> c.getTipoContato().equals(TipoContato.CELULAR);
		Predicate<Contato> condicaoFixo = c -> c.getTipoContato().equals(TipoContato.FIXO);
		
		Optional<Contato> contato = rec.getPessoa().getContatos().stream().filter(condicaoCelular.or(condicaoFixo)).max(comparadorContato);
		
		if (endereco.isPresent()) {
			Endereco e = endereco.get();
			resultado = resultado.replace("[logradouro]", e.getLogradouro());
			resultado = resultado.replace("[bairro]", e.getBairro());
			resultado = resultado.replace("[cidadeuf]", e.getCidade().getNome() + "-" + e.getCidade().getEstado().getUf());
		} else {
			resultado = resultado.replace("[logradouro]", "______________________________");
			resultado = resultado.replace("[bairro]", "______________________________");
			resultado = resultado.replace("[cidadeuf]", "______________________________");
		}
		
		if (contato.isPresent()) {
			Contato c = contato.get();
			resultado = resultado.replace("[telefone]", c.getValor());
		} else {
			resultado = resultado.replace("[telefone]", "____________________");
		}

		return new TextoDoRelatorio(resultado);
	}
}
