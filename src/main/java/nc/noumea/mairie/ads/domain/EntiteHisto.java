package nc.noumea.mairie.ads.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ADS_ENTITE_HISTO")
@PersistenceUnit(unitName = "adsPersistenceUnit")
@NamedQueries({ @NamedQuery(name = "getListEntiteHistoByIdEntite", query = "select n from EntiteHisto n where n.idEntite = :idEntite order by n.dateHisto desc ") })
public class EntiteHisto {

	@Id
	@Column(name = "ID_ENTITE_HISTO")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idEntiteHisto;

	@NotNull
	@Column(name = "ID_ENTITE")
	private Integer idEntite;

	@NotNull
	@Column(name = "SIGLE")
	private String sigle;

	@NotNull
	@Column(name = "LABEL")
	private String label;

	@Column(name = "TYPE_ENTITE")
	private String typeEntite;

	@Column(name = "ID_ENTITE_PARENT")
	private Integer idEntiteParent;

	@Column(name = "LABEL_COURT", length = 60)
	private String labelCourt;

	@Column(name = "ID_ENTITE_REMPLACEE")
	private Integer idEntiteRemplacee;

	@NotNull
	@Column(name = "ID_REF_STATUT_ENTITE")
	@Enumerated(EnumType.ORDINAL)
	private StatutEntiteEnum statut;

	@Column(name = "ID_AGENT_CREATION")
	private Integer idAgentCreation;

	@Column(name = "DATE_CREATION")
	private Date dateCreation;

	@Column(name = "ID_AGENT_MODIFICATION")
	private Integer idAgentModification;

	@Column(name = "DATE_MODIFICATION")
	private Date dateModification;

	@Column(name = "REFERENCE_DELIBERATION_ACTIF")
	private String refDeliberationActif;

	@Column(name = "DATE_DELIBERATION_ACTIF")
	private Date dateDeliberationActif;

	@Column(name = "REFERENCE_DELIBERATION_INACTIF")
	private String refDeliberationInactif;

	@Column(name = "DATE_DELIBERATION_INACTIF")
	private Date dateDeliberationInactif;

	@NotNull
	@Column(name = "ID_AGENT_HISTO")
	private Integer idAgentHisto;

	@NotNull
	@Column(name = "DATE_HISTO")
	private Date dateHisto;

	@Column(name = "COMMENTAIRE")
	private String commentaire;

	@Column(name = "NFA")
	private String nfa;

	@Column(name = "IS_ENTITE_AS400")
	private boolean entiteAs400 = false;

	@NotNull
	@Column(name = "TYPE_HISTO")
	@Enumerated(EnumType.ORDINAL)
	private TypeHistoEnum type;

	public EntiteHisto() {
		super();
	}

	public EntiteHisto(Entite entite, Integer idAgentHisto, TypeHistoEnum type) {
		this.idEntite = entite.getIdEntite();
		this.sigle = entite.getSigle();
		this.label = entite.getLabel();
		this.typeEntite = entite.getTypeEntite() != null ? entite.getTypeEntite().getLabel() : null;
		this.idEntiteParent = entite.getEntiteParent() != null ? entite.getEntiteParent().getIdEntite() : null;
		this.labelCourt = entite.getLabelCourt() != null ? entite.getLabelCourt() : null;
		this.idEntiteRemplacee = entite.getEntiteRemplacee() != null ? entite.getEntiteRemplacee().getIdEntite() : null;
		this.statut = entite.getStatut();
		this.idAgentCreation = entite.getIdAgentCreation() != null ? entite.getIdAgentCreation() : null;
		this.dateCreation = entite.getDateCreation() != null ? entite.getDateCreation() : null;
		this.idAgentModification = entite.getIdAgentModification() != null ? entite.getIdAgentModification() : null;
		this.dateModification = entite.getDateModification() != null ? entite.getDateModification() : null;
		this.refDeliberationActif = entite.getRefDeliberationActif() != null ? entite.getRefDeliberationActif() : null;
		this.dateDeliberationActif = entite.getDateDeliberationActif() != null ? entite.getDateDeliberationActif()
				: null;
		this.refDeliberationInactif = entite.getRefDeliberationInactif() != null ? entite.getRefDeliberationInactif()
				: null;
		this.dateDeliberationInactif = entite.getDateDeliberationInactif() != null ? entite
				.getDateDeliberationInactif() : null;
		this.idAgentHisto = idAgentHisto;
		this.dateHisto = new Date();
		this.type = type;
		this.commentaire = entite.getCommentaire();
		this.nfa = entite.getNfa();
	}

	public Integer getIdEntiteHisto() {
		return idEntiteHisto;
	}

	public void setIdEntiteHisto(Integer idEntiteHisto) {
		this.idEntiteHisto = idEntiteHisto;
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

	public String getTypeEntite() {
		return typeEntite;
	}

	public void setTypeEntite(String typeEntite) {
		this.typeEntite = typeEntite;
	}

	public Integer getIdEntiteParent() {
		return idEntiteParent;
	}

	public void setIdEntiteParent(Integer idEntiteParent) {
		this.idEntiteParent = idEntiteParent;
	}

	public String getLabelCourt() {
		return labelCourt;
	}

	public void setLabelCourt(String labelCourt) {
		this.labelCourt = labelCourt;
	}

	public Integer getIdEntiteRemplacee() {
		return idEntiteRemplacee;
	}

	public void setIdEntiteRemplacee(Integer idEntiteRemplacee) {
		this.idEntiteRemplacee = idEntiteRemplacee;
	}

	public StatutEntiteEnum getStatut() {
		return statut;
	}

	public void setStatut(StatutEntiteEnum statut) {
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

	public Integer getIdAgentHisto() {
		return idAgentHisto;
	}

	public void setIdAgentHisto(Integer idAgentHisto) {
		this.idAgentHisto = idAgentHisto;
	}

	public Date getDateHisto() {
		return dateHisto;
	}

	public void setDateHisto(Date dateHisto) {
		this.dateHisto = dateHisto;
	}

	public TypeHistoEnum getType() {
		return type;
	}

	public void setType(TypeHistoEnum type) {
		this.type = type;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
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

}
