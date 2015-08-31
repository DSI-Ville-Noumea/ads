package nc.noumea.mairie.sirh.dto;

public class AccessRightOrganigrammeDto {

	private boolean visualisation;
	private boolean edition;

	public AccessRightOrganigrammeDto() {
	}

	public boolean isVisualisation() {
		return visualisation;
	}

	public void setVisualisation(boolean visualisation) {
		this.visualisation = visualisation;
	}

	public boolean isEdition() {
		return edition;
	}

	public void setEdition(boolean edition) {
		this.edition = edition;
	}

}
