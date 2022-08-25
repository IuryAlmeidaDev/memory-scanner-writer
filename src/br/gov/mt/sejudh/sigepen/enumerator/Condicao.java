package br.gov.mt.sejudh.sigepen.enumerator;

public enum Condicao {
	
	PROVISORIO("Provisório"),
	CONDENADO("Condenado"),
	MEDIDA_SEGURANCA("Medida de Segurança");

	private String descricao;
	
	private Condicao(String descricao){
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public static Condicao getValueByString(String c) {
		if (c == null) return null;
		
		for (Condicao ca : Condicao.values()) {
			if (ca.toString().equals(c)) {
				return ca;
			}
		}
		
		throw new IllegalArgumentException("Não existe condição: " + c);
	}
}
