package nc.noumea.mairie.ads.domain;

public enum TypeEntiteEnum {
	
	DIRECTION(27), SERVICE(28), SECTION(29), NON_AFFICHE(30);
	
	private final int id;
	
	TypeEntiteEnum ( Integer id ) {
		this.id = id;
	}
	
	public Integer getId( ) {
		return this.id;
	}
	
}
