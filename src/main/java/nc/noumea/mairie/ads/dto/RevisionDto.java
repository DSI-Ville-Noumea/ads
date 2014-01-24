package nc.noumea.mairie.ads.dto;

import java.util.Date;

import nc.noumea.mairie.ads.domain.Revision;

public class RevisionDto {

	private long idRevision;
	private Integer idAgent;
	private Date dateModif;
	private Date dateEffet;
	private Date dateDecret;
	private String description;

	// for view
	private boolean editing;
	private String style;

	public RevisionDto() {

	}

	public RevisionDto(Revision revision) {
		this.idRevision = revision.getIdRevision();
		this.idAgent = revision.getIdAgent();
		this.dateModif = revision.getDateModif();
		this.dateEffet = revision.getDateEffet();
		this.dateDecret = revision.getDateDecret();
		this.description = revision.getDescription();
	}

	public long getIdRevision() {
		return idRevision;
	}

	public void setIdRevision(long idRevision) {
		this.idRevision = idRevision;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDateModif() {
		return dateModif;
	}

	public void setDateModif(Date dateModif) {
		this.dateModif = dateModif;
	}

	public Date getDateEffet() {
		return dateEffet;
	}

	public void setDateEffet(Date dateEffet) {
		this.dateEffet = dateEffet;
	}

	public Date getDateDecret() {
		return dateDecret;
	}

	public void setDateDecret(Date dateDecret) {
		this.dateDecret = dateDecret;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEditing() {
		return editing;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

}
