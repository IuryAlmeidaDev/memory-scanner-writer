package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoPlantao {
	A,
	B,
	C,
	D;

	public String getName() {
		return name();
	}
	
	public static TipoPlantao getFromOrdinal(int ordinal) {
		for(TipoPlantao p : values()) {
			if (p.ordinal() == ordinal) {
				return p;
			}
		}
		
		return null;
	}
}
