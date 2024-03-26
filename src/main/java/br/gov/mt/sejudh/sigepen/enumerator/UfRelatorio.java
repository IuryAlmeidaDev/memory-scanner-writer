package br.gov.mt.sejudh.sigepen.enumerator;

public enum UfRelatorio {
	TT("Todos"),
	AC("ACRE"),
	AL("ALAGOAS"),
	AM("AMAZONAS"),
	AP("AMAPÁ"),
	BA("BAHIA"),
	CE("CEARÁ"),
	DF("DISTRITO FEDERAL"),
	ES("ESPÍRITO SANTO"),
	GO("GOIÁS"),
	MA("MARANHÃO"),
	MG("MINAS GERAIS"),
	MS("MATO GROSSO DO SUL"),
	MT("MATO GROSSO"),
	PA("PARÁ"),
	PB("PARAÍBA"),
	PE("PERNAMBUCO"),
	PI("PIAUÍ"),
	PR("PARANA"),
	RJ("RIO DE JANEIRO"),
	RN("RIO GRANDE DO NORTE"),
	RO("RONDÔNIA"),
	RR("RORAIMA"),
	RS("RIO GRANDE DO SUL"),
	SC("SANTA CATARINA"),
	SE("SERGIPE"),
	SP("SÃO PAULO"),
	TO("TOCANTINS");

	private String descricao;
	
	private UfRelatorio(String descricao){
		this.descricao = descricao;
	}

	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}



