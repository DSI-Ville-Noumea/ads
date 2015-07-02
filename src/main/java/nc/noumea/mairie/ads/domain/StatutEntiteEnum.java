package nc.noumea.mairie.ads.domain;


public enum StatutEntiteEnum {
	
	PREVISION(0), ACTIF(1), TRANSITOIRE(2), INACTIF(3);
	
	private int idRefStatutEntite;

	StatutEntiteEnum(int _value) {
		idRefStatutEntite = _value;
	}

	public int getIdRefStatutEntite() {
		return idRefStatutEntite;
	}

	public static StatutEntiteEnum getStatutEntiteEnum(Integer idRefStatutEntite) {

		if (idRefStatutEntite == null)
			return null;

		switch (idRefStatutEntite) {
			case 1:
				return PREVISION;
			case 2:
				return ACTIF;
			case 3:
				return TRANSITOIRE;
			case 4:
				return INACTIF;
			default:
				return null;
		}
	}
}
