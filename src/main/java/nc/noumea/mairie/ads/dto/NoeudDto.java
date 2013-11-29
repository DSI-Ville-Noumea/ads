package nc.noumea.mairie.ads.dto;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.domain.Noeud;

public class NoeudDto {

	private long idNoeud;
	private int idService;
	private long idRevision;
	private String sigle;
	private String label;
	private List<NoeudDto> enfants;

	public NoeudDto() {
		enfants = new ArrayList<NoeudDto>();
	}

	public NoeudDto(Noeud noeud) {
		this.idNoeud = noeud.getIdNoeud();
		this.idService = noeud.getIdService();
		this.idRevision = noeud.getRevision().getIdRevision();
		this.sigle = noeud.getSigle();
		this.label = noeud.getLabel();
		this.enfants = new ArrayList<NoeudDto>();

		for (Noeud n : noeud.getNoeudsEnfants()) {
			this.enfants.add(new NoeudDto(n));
		}
	}

	public long getIdNoeud() {
		return idNoeud;
	}

	public void setIdNoeud(long idNoeud) {
		this.idNoeud = idNoeud;
	}

	public int getIdService() {
		return idService;
	}

	public void setIdService(int idService) {
		this.idService = idService;
	}

	public long getIdRevision() {
		return idRevision;
	}

	public void setIdRevision(long idRevision) {
		this.idRevision = idRevision;
	}

	public String getSigle() {
		return sigle;
	}

	public void setSigle(String sigle) {
		this.sigle = sigle;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<NoeudDto> getEnfants() {
		return enfants;
	}

	public void setEnfants(List<NoeudDto> enfants) {
		this.enfants = enfants;
	}
}
