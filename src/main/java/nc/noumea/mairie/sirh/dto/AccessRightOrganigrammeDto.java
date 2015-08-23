package nc.noumea.mairie.sirh.dto;

public class AccessRightOrganigrammeDto {

	private boolean visualisation;
	private boolean edition;
	private boolean administrateur;

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

	public boolean isAdministrateur() {
		return administrateur;
	}

	public void setAdministrateur(boolean administrateur) {
		this.administrateur = administrateur;
	}

}
