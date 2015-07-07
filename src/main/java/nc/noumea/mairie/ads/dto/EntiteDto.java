package nc.noumea.mairie.ads.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.sirh.domain.Siserv;

@XmlRootElement
public class EntiteDto {

	private Integer idEntite;
	private String sigle;
	private String label;
	private String labelCourt;
	private String titreChef;
	private ReferenceDto typeEntite;
	private String codeServi;
	private List<EntiteDto> enfants;
	private EntiteDto entiteParent;
	private EntiteDto entiteRemplacee;

	private Integer idStatut;
	private Integer idAgentCreation;
	private Date dateCreation;
	private Integer idAgentModification;
	private Date dateModification;
	private String refDeliberationActif;
	private Date dateDeliberationActif;
	private String refDeliberationInactif;
	private Date dateDeliberationInactif;

	public EntiteDto() {
		enfants = new ArrayList<>();
	}

	public EntiteDto(Entite entite, boolean withChildren) {
		mapEntite(entite);

		if (withChildren) {
			for (Entite n : entite.getEntitesEnfants()) {
				this.enfants.add(new EntiteDto(n, withChildren));
			}
		}
	}

	public EntiteDto mapEntite(Entite entite) {
		this.idEntite = entite.getIdEntite();
		this.sigle = entite.getSigle();
		this.label = entite.getLabel();
		this.labelCourt = entite.getLabelCourt();
		this.titreChef = entite.getTitreChef();
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
		this.titreChef = entite.getTitreChef();
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

	public String getTitreChef() {
		return titreChef;
	}

	public void setTitreChef(String titreChef) {
		this.titreChef = titreChef;
	}

	public Integer getIdStatut() {
		return idStatut;
	}

	public void setIdStatut(Integer idStatut) {
		this.idStatut = idStatut;
	}
}
