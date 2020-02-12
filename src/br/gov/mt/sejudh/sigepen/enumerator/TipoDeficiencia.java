package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoDeficiencia {
	NAO_POSSUI("Não Possui"),
	VISUAL("Visual"),
	AUDITIVA("Auditiva"),
	MOTORA("Motora"),
	MENTAL("Mental");
	
	private String descricao;
	
	private TipoDeficiencia(String descricao){
		this.descricao = descricao;
	}
	
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao(){
		return this.descricao;
	}

}
