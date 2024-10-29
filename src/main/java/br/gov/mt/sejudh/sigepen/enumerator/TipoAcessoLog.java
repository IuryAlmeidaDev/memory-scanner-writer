package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoAcessoLog {
	TENTATIVA_LOGIN("TENTATIVA"), 
	SUCESSO_LOGIN("SUCESSO"), 
	FALHA_LOGIN("FALHA"),
	LOGOUT("LOGOUT");

	private String descricao;
	private String longDesc;

	TipoAcessoLog(String desc) {
		this.descricao = desc;
		this.longDesc = desc;
	}

	TipoAcessoLog(String desc, String longDesc) {
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

	
	public TipoAcessoLog valueOf(int ord) {
		return values()[ord];
	}
}