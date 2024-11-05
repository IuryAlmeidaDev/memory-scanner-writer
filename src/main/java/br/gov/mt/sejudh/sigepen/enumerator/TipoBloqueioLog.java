package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoBloqueioLog {
	NUMERO_MAXIMO_TENTATIVAS("NÚMERO MÁXIMO DE TENTATIVAS"), 
	DADOS_INCORRETOS("DADOS INCORRETOS"), 
	NENHUM("NENHUM");

	private String descricao;
	private String longDesc;

	TipoBloqueioLog(String desc) {
		this.descricao = desc;
		this.longDesc = desc;
	}

	TipoBloqueioLog(String desc, String longDesc) {
		this.descricao = desc;
		this.longDesc = longDesc;
	}

	public String getDesc() {
		return this.descricao;
	}

	public String getLongDesc() {
		return this.longDesc;
	}

	public String getName() {
		return this.name();
	}

	public Integer getOrdinal() {
		return this.ordinal();
	}

	public Integer getSize() {
		return values().length;
	}

	public boolean isValido(int cdItemDominio) {
		return (getSize() >= cdItemDominio) ? true : false;
	}

	public TipoBloqueioLog valueOf(int ord) {
		return values()[ord];
	}
}