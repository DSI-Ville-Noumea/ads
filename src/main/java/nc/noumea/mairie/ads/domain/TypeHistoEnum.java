package nc.noumea.mairie.ads.domain;

public enum TypeHistoEnum {

	CREATION(0), MODIFICATION(1), SUPPRESSION(2), CHANGEMENT_STATUT(3);

	private int idRefTypeHisto;

	TypeHistoEnum(int _value) {
		idRefTypeHisto = _value;
	}

	public static TypeHistoEnum getTypeHistoEnum(Integer idRefTypeHisto) {

		switch (idRefTypeHisto) {
			case 0:
				return CREATION;
			case 1:
				return MODIFICATION;
			case 2:
				return SUPPRESSION;
			case 3:
				return CHANGEMENT_STATUT;
			default:
				return null;
		}
	}

	public int getIdRefTypeHisto() {
		return idRefTypeHisto;
	}
}
