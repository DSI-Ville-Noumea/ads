package nc.noumea.mairie.ads.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import nc.noumea.mairie.ads.domain.Revision;

import org.codehaus.jackson.annotate.JsonIgnore;

@XmlRootElement
public class RevisionDto {

	private long idRevision;
	private Integer idAgent;
	private Date dateModif;
	private Date dateEffet;
	private Date dateDecret;
	private String description;

	// for view
	@JsonIgnore
	private boolean appliedToSiserv;
	@JsonIgnore
	private boolean editing;
	@JsonIgnore
	private String style;
	@JsonIgnore
	private boolean canEdit;

	public RevisionDto() {

	}

	public RevisionDto(Revision revision) {
		this.idRevision = revision.getIdRevision();
		this.idAgent = revision.getIdAgent();
		this.dateModif = revision.getDateModif();
		this.dateEffet = revision.getDateEffet();
		this.dateDecret = revision.getDateDecret();
		this.description = revision.getDescription();
		this.appliedToSiserv = revision.isExportedSiserv();
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

	@XmlTransient
	public boolean isAppliedToSiserv() {
		return appliedToSiserv;
	}

	public void setAppliedToSiserv(boolean appliedToSiserv) {
		this.appliedToSiserv = appliedToSiserv;
	}

	@XmlTransient
	public boolean isEditing() {
		return editing;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	@XmlTransient
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@XmlTransient
	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

}
