package nc.noumea.mairie.ads.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.EntiteLight;
import nc.noumea.mairie.domain.Siserv;

@XmlRootElement
public class EntiteDto {

	private Integer idEntite;
	private String sigle;
	private String label;
	private String labelCourt;
	private String labelLong;
	private ReferenceDto typeEntite;
	private String codeServi;
	private List<EntiteDto> enfants;
	private EntiteDto entiteParent;
	private EntiteDto entiteRemplacee;

	private Integer idStatut;
	private Integer idAgentCreation;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateCreation;
	private Integer idAgentModification;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateModification;
	private String refDeliberationActif;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateDeliberationActif;
	private String refDeliberationInactif;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateDeliberationInactif;

	private String commentaire;
	private String nfa;

	private EntiteDto entiteDirection;

	private boolean entiteAs400;

	public EntiteDto() {
		enfants = new ArrayList<>();
	}

	public EntiteDto(Entite entite, boolean withChildren) {
		mapEntite(entite, null);

		if (withChildren) {
			for (Entite n : entite.getEntitesEnfants()) {
				this.enfants.add(new EntiteDto(n, withChildren));
			}
		}
	}

	public EntiteDto(EntiteLight entite, boolean withChildren) {
		mapEntite(entite, null);

		if (withChildren) {
			for (EntiteLight n : entite.getEntitesEnfants()) {
				this.enfants.add(new EntiteDto(n, withChildren));
			}
		}
	}

	public EntiteDto mapEntite(Entite entite, Entite entiteDirection) {
		this.idEntite = entite.getIdEntite();
		this.sigle = entite.getSigle();
		this.label = entite.getLabel();
		this.labelCourt = entite.getLabelCourt();
		this.labelLong = entite.getLabelLong();
		this.typeEntite = entite.getTypeEntite() == null ? null : new ReferenceDto(entite.getTypeEntite());
		this.codeServi = entite.getSiservInfo() == null ? null : entite.getSiservInfo().getCodeServi();
		this.enfants = new ArrayList<>();
		this.entiteParent = null == entite.getEntiteParent() ? null : new EntiteDto(entite.getEntiteParent(), false);
		this.entiteRemplacee = null == entite.getEntiteRemplacee() ? null : new EntiteDto(entite.getEntiteRemplacee(),
				false);
		this.idStatut = null == entite.getStatut() ? null : entite.getStatut().getIdRefStatutEntite();
		this.idAgentCreation = entite.getIdAgentCreation();
		this.dateCreation = entite.getDateCreation();
		this.idAgentModification = entite.getIdAgentModification();
		this.dateModification = entite.getDateModification();
		this.refDeliberationActif = entite.getRefDeliberationActif();
		this.dateDeliberationActif = entite.getDateDeliberationActif();
		this.refDeliberationInactif = entite.getRefDeliberationInactif();
		this.dateDeliberationInactif = entite.getDateDeliberationInactif();
		this.commentaire = entite.getCommentaire();
		this.nfa = entite.getNfa();
		this.entiteAs400 = entite.isEntiteAs400();

		if (null != entiteDirection) {
			this.entiteDirection = new EntiteDto().mapEntite(entiteDirection, null);
		}
		return this;
	}


	public EntiteDto mapEntite(EntiteLight entite, Entite entiteDirection) {
		this.idEntite = entite.getIdEntite();
		this.sigle = entite.getSigle();
		this.label = entite.getLabel();
		this.typeEntite = entite.getTypeEntite() == null ? null : new ReferenceDto(entite.getTypeEntite());
		this.enfants = new ArrayList<>();
		this.entiteParent = null == entite.getEntiteParent() ? null : new EntiteDto(entite.getEntiteParent(), false);
		this.idStatut = null == entite.getStatut() ? null : entite.getStatut().getIdRefStatutEntite();

		if (null != entiteDirection) {
			this.entiteDirection = new EntiteDto().mapEntite(entiteDirection, null);
		}
		return this;
	}

	public EntiteDto mapEntite(EntiteHisto entite, EntiteDto entiteParent, EntiteDto entiteRemplacee) {
		this.idEntite = entite.getIdEntite();
		this.sigle = entite.getSigle();
		this.label = entite.getLabel();
		this.labelCourt = entite.getLabelCourt();
		this.labelLong = entite.getLabelLong();
		this.typeEntite = entite.getTypeEntite() == null ? null : new ReferenceDto(entite.getTypeEntite());
		this.enfants = new ArrayList<>();
		this.entiteParent = null == entite.getIdEntiteParent() ? null : entiteParent;
		this.entiteRemplacee = null == entite.getIdEntiteRemplacee() ? null : entiteRemplacee;
		this.idStatut = null == entite.getStatut() ? null : entite.getStatut().getIdRefStatutEntite();
		this.idAgentCreation = entite.getIdAgentCreation();
		this.dateCreation = entite.getDateCreation();
		this.idAgentModification = entite.getIdAgentModification();
		this.dateModification = entite.getDateModification();
		this.refDeliberationActif = entite.getRefDeliberationActif();
		this.dateDeliberationActif = entite.getDateDeliberationActif();
		this.refDeliberationInactif = entite.getRefDeliberationInactif();
		this.dateDeliberationInactif = entite.getDateDeliberationInactif();
		this.commentaire = entite.getCommentaire();
		this.nfa = entite.getNfa();
		this.entiteAs400 = entite.isEntiteAs400();

		return this;
	}

	public EntiteDto(EntiteDto entite) {
		mapEntite(entite);

		for (EntiteDto n : entite.getEnfants()) {
			this.enfants.add(new EntiteDto(n));
		}
	}

	public EntiteDto(Siserv service) {
		this.label = service.getLiServ();
		this.codeServi = service.getServi();
		this.sigle = service.getSigle();
	}

	public EntiteDto mapEntite(EntiteDto entite) {
		this.idEntite = entite.getIdEntite();
		this.sigle = entite.getSigle();
		this.label = entite.getLabel();
		this.labelCourt = entite.getLabelCourt();
		this.labelLong = entite.getLabelLong();
		this.typeEntite = entite.getTypeEntite();
		this.codeServi = entite.getCodeServi();
		this.enfants = new ArrayList<>();
		this.entiteParent = null == entite.getEntiteParent() ? null : new EntiteDto(entite.getEntiteParent());
		this.entiteRemplacee = null == entite.getEntiteRemplacee() ? null : new EntiteDto(entite.getEntiteRemplacee());
		this.idStatut = entite.getIdStatut();
		this.idAgentCreation = entite.getIdAgentCreation();
		this.dateCreation = entite.getDateCreation();
		this.idAgentModification = entite.getIdAgentModification();
		this.dateModification = entite.getDateModification();
		this.refDeliberationActif = entite.getRefDeliberationActif();
		this.dateDeliberationActif = entite.getDateDeliberationActif();
		this.refDeliberationInactif = entite.getRefDeliberationInactif();
		this.dateDeliberationInactif = entite.getDateDeliberationInactif();
		this.commentaire = entite.getCommentaire();
		this.nfa = entite.getNfa();
		this.entiteAs400 = entite.isEntiteAs400();

		return this;
	}

	public Integer getIdEntite() {
		return idEntite;
	}

	public void setIdEntite(Integer idEntite) {
		this.idEntite = idEntite;
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

	public ReferenceDto getTypeEntite() {
		return typeEntite;
	}

	public void setTypeEntite(ReferenceDto typeEntite) {
		this.typeEntite = typeEntite;
	}

	public String getCodeServi() {
		return codeServi;
	}

	public void setCodeServi(String codeServi) {
		this.codeServi = codeServi;
	}

	public List<EntiteDto> getEnfants() {
		return enfants;
	}

	public void setEnfants(List<EntiteDto> enfants) {
		this.enfants = enfants;
	}

	public Integer getIdAgentCreation() {
		return idAgentCreation;
	}

	public void setIdAgentCreation(Integer idAgentCreation) {
		this.idAgentCreation = idAgentCreation;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Integer getIdAgentModification() {
		return idAgentModification;
	}

	public void setIdAgentModification(Integer idAgentModification) {
		this.idAgentModification = idAgentModification;
	}

	public Date getDateModification() {
		return dateModification;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

	public String getRefDeliberationActif() {
		return refDeliberationActif;
	}

	public void setRefDeliberationActif(String refDeliberationActif) {
		this.refDeliberationActif = refDeliberationActif;
	}

	public Date getDateDeliberationActif() {
		return dateDeliberationActif;
	}

	public void setDateDeliberationActif(Date dateDeliberationActif) {
		this.dateDeliberationActif = dateDeliberationActif;
	}

	public String getRefDeliberationInactif() {
		return refDeliberationInactif;
	}

	public void setRefDeliberationInactif(String refDeliberationInactif) {
		this.refDeliberationInactif = refDeliberationInactif;
	}

	public Date getDateDeliberationInactif() {
		return dateDeliberationInactif;
	}

	public void setDateDeliberationInactif(Date dateDeliberationInactif) {
		this.dateDeliberationInactif = dateDeliberationInactif;
	}

	public EntiteDto getEntiteParent() {
		return entiteParent;
	}

	public void setEntiteParent(EntiteDto entiteParent) {
		this.entiteParent = entiteParent;
	}

	public EntiteDto getEntiteRemplacee() {
		return entiteRemplacee;
	}

	public void setEntiteRemplacee(EntiteDto entiteRemplacee) {
		this.entiteRemplacee = entiteRemplacee;
	}

	public String getLabelCourt() {
		return labelCourt;
	}

	public void setLabelCourt(String labelCourt) {
		this.labelCourt = labelCourt;
	}

	public Integer getIdStatut() {
		return idStatut;
	}

	public void setIdStatut(Integer idStatut) {
		this.idStatut = idStatut;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public EntiteDto getEntiteDirection() {
		return entiteDirection;
	}

	public void setEntiteDirection(EntiteDto entiteDirection) {
		this.entiteDirection = entiteDirection;
	}

	public String getNfa() {
		return nfa;
	}

	public void setNfa(String nfa) {
		this.nfa = nfa;
	}

	public boolean isEntiteAs400() {
		return entiteAs400;
	}

	public void setEntiteAs400(boolean entiteAs400) {
		this.entiteAs400 = entiteAs400;
	}

	public String getLabelLong() {
		return labelLong;
	}

	public void setLabelLong(String labelLong) {
		this.labelLong = labelLong;
	}

}
