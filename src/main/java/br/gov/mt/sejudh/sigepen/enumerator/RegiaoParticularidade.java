package br.gov.mt.sejudh.sigepen.enumerator;

public enum RegiaoParticularidade {
	
	FACE(1, "Face"),
	CABECA_POSTERIOR(2, "Cabeça Posterior"),
	PESCOCO_FRENTE(3, "Pescoço Frente"),
	PESCOCO_TRAS(4, "Pescoço Trás"),
	OMBRO_ESQUERDO_FRENTE(5, "Ombro Esquerdo"),
	PEITORAL_ESQUERDO(6, "Peitoral Esquerdo"),
	PEITORAL_DIREITO(7, "Peitoral Direito"),
	OMBRO_DIREITO_FRENTE(8, "Ombro Direito"),
	OMBRO_ESQUERDO_TRAS(9, "Ombro Esquerdo"),
	DORSO_ESQUERDO(10, "Dorso Esquerdo"),
	DORSO_DIREITO(11, "Dorso Direito"),
	OMBRO_DIREITO_TRAS(12, "Ombro Direito"),
	BICEPS_ESQUERDO(13, "Bíceps Esquerdo"),
	BICEPS_DIREITO(14, "Bíceps Direito"),
	TRICEPS_ESQUERDO(15, "Tríceps Esquerdo"),
	LOMBAR_ESQUERDO(16, "Lombar Esquerdo"),
	LOMBAR_DIREITO(17, "Lombar Direito"),
	TRICEPS_DIREITO(18, "Tríceps Direito"),
	ANTEBRACO_ESQUERDO_FRENTE(19, "Antebraço Esquerdo"),
	ABDOMEM_ESQUERDO(20, "Abdómem Esquerdo"),
	ABDOMEM_DIREITO(21, "Abdómem Direito"),
	ANTEBRACO_DIREITO_FRENTE(22, "Antebraço Direito"),
	ANTEBRACO_ESQUERDO_TRAS(23, "Antebraço Esquerdo"),
	GLUTEO_ESQUERDO(24, "Glúteo Esquerdo"),
	GLUTEO_DIREITO(25, "Glúteo Direito"),
	ANTEBRACO_DIREITO_TRAS(26, "Antebraço Direito"),
	PALMA_ESQUERDA(27, "Palma Esquerda"),
	PELVE_ESQUERDA(28, "Pelve Esquerda"),
	PELVE_DIREITA(29, "Pelve Direita"),
	PALMA_DIREITA(30, "Palma Direita"),
	DORSO_MAO_ESQUERDA(31, "Dorso da Mão Esquerda"),
	DORSO_MAO_DIREITA(32, "Dorso da Mão Direita"),
	COXA_ESQUERDA(33, "Coxa Esquerda"),
	COXA_DIREITA(34, "Coxa Direita"),
	POSTERIOR_ESQUERDA(35, "Posterior Esquerda"),
	POSTERIOR_DIREITA(36, "Posterior Direita"),
	CANELA_ESQUERDA(37, "Canela Esquerda"),
	CANELA_DIREITA(38, "Canela Direita"),
	PANTURRILHA_ESQUERDA(39, "Panturrilha Esquerda"),
	PANTURRILHA_DIREITA(40, "Panturrilha Direita"),
	PE_ESQUERDO(41, "Pé Esquerdo"),
	PE_DIREITO(42, "Pé Direito"),
	CALCANHAR_ESQUERDO(43, "Calcanhar Esquerdo"),
	CALCANHAR_DIREITO(44, "Calcanhar Direito");
	
	
	private int id;
	private String descricao;
	
	private RegiaoParticularidade(int id, String descricao) {
		this.setDescricao(descricao);
		this.setId(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public static RegiaoParticularidade getValue(Integer id) {
		if (id == null || id == 0) return null;
		
		for (RegiaoParticularidade regiao : RegiaoParticularidade.values()) {
			if (regiao.getId() == id) {
				return regiao;
			}
		}
		
		throw new IllegalArgumentException("Não existe região de particularidade com id: " + id);
	}
}
