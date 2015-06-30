package nc.noumea.mairie.ads.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ads.domain.Entite;

@XmlRootElement
public class EntiteDto {

	private Integer idEntite;
	private String sigle;
	private String label;
	private Integer idTypeEntite;
	private String codeServi;
	private String lib22;
	private List<EntiteDto> enfants;
	
	private String statut;
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

	public EntiteDto(Entite entite) {
		mapEntite(entite);

		for (Entite n : entite.getEntitesEnfants()) {
			this.enfants.add(new EntiteDto(n));
		}
	}

	public EntiteDto mapEntite(Entite entite) {
		this.idEntite = entite.getIdEntite();
		this.sigle = entite.getSigle();
		this.label = entite.getLabel();
		this.idTypeEntite = entite.getTypeEntite() == null ? null : entite.getTypeEntite().getIdTypeEntite();
		this.codeServi = entite.getSiservInfo() == null ? null : entite.getSiservInfo().getCodeServi();
		this.lib22 = entite.getSiservInfo() == null ? null : entite.getSiservInfo().getLib22();
		this.enfants = new ArrayList<>();

		return this;
	}

	public EntiteDto(EntiteDto entite) {
		mapEntite(entite);

		for (EntiteDto n : entite.getEnfants()) {
			this.enfants.add(new EntiteDto(n));
		}
	}

	public EntiteDto mapEntite(EntiteDto entite) {
		this.idEntite = entite.getIdEntite();
		this.sigle = entite.getSigle();
		this.label = entite.getLabel();
		this.idTypeEntite = entite.getIdTypeEntite();
		this.codeServi = entite.getCodeServi();
		this.lib22 = entite.getLib22();
		this.enfants = new ArrayList<>();

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

	public Integer getIdTypeEntite() {
		return idTypeEntite;
	}

	public void setIdTypeEntite(Integer idTypeEntite) {
		this.idTypeEntite = idTypeEntite;
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

	public String getLib22() {
		return lib22;
	}

	public void setLib22(String lib22) {
		this.lib22 = lib22;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
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
}
