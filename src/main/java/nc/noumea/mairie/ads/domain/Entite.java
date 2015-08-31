package nc.noumea.mairie.ads.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "ADS_ENTITE")
@PersistenceUnit(unitName = "adsPersistenceUnit")
@NamedQueries({
		@NamedQuery(name = "getEntiteFromIdEntite", query = "select n from Entite n where n.idEntite = :idEntite"),
		@NamedQuery(name = "getEntiteFromCodeServi", query = "select n from Entite n inner join n.siservInfo s where LOWER(s.codeServi) = LOWER(:codeServi)"),
		@NamedQuery(name = "getEntiteActiveFromSigle", query = "select n from Entite n where n.statut=1 and LOWER(n.sigle) = LOWER(:sigle)"),
		@NamedQuery(name = "getListEntiteByStatut", query = "select n from Entite n where n.statut = :statut") })
public class Entite {

	@Id
	@Column(name = "ID_ENTITE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idEntite;

	@NotNull
	@Column(name = "SIGLE", length = 8)
	private String sigle;

	@NotNull
	@Column(name = "LABEL")
	private String label;

	@Column(name = "LABEL_COURT", length = 60)
	private String labelCourt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_ENTITE_PARENT")
	private Entite entiteParent;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_ENTITE_REMPLACEE")
	private Entite entiteRemplacee;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "entiteParent", cascade = CascadeType.ALL)
	@OrderBy("idEntite asc")
	private Set<Entite> entitesEnfants = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "ID_TYPE_ENTITE", referencedColumnName = "ID_TYPE_ENTITE")
	private TypeEntite typeEntite;

	@OneToOne(mappedBy = "entite", optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	private SiservInfo siservInfo;

	@Column(name = "ID_REF_STATUT_ENTITE")
	@Enumerated(EnumType.ORDINAL)
	private StatutEntiteEnum statut;

	@Column(name = "ID_AGENT_CREATION")
	private Integer idAgentCreation;

	@Column(name = "DATE_CREATION")
	private Date dateCreation;

	@Column(name = "ID_AGENT_MODIF")
	private Integer idAgentModification;

	@Column(name = "DATE_MODIF")
	private Date dateModification;

	@Column(name = "REFERENCE_DELIBERATION_ACTIF")
	private String refDeliberationActif;

	@Column(name = "DATE_DELIBERATION_ACTIF")
	private Date dateDeliberationActif;

	@Column(name = "REFERENCE_DELIBERATION_INACTIF")
	private String refDeliberationInactif;

	@Column(name = "DATE_DELIBERATION_INACTIF")
	private Date dateDeliberationInactif;

	@Column(name = "COMMENTAIRE")
	private String commentaire;

	@Column(name = "NFA")
	private String nfa;

	@Column(name = "IS_ENTITE_AS400")
	private boolean entiteAs400 = false;

	public void addParent(Entite parent) {
		this.entiteParent = parent;
		parent.getEntitesEnfants().add(this);
	}

	@Version
	@Column(name = "version")
	private Integer version;

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getSigle() {
		return this.sigle;
	}

	public void setSigle(String sigle) {
		this.sigle = sigle.toUpperCase();
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public SiservInfo getSiservInfo() {
		return this.siservInfo;
	}

	public void setSiservInfo(SiservInfo siservInfo) {
		this.siservInfo = siservInfo;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public Integer getIdEntite() {
		return idEntite;
	}

	public void setIdEntite(Integer idEntite) {
		this.idEntite = idEntite;
	}

	public Entite getEntiteParent() {
		return entiteParent;
	}

	public void setEntiteParent(Entite entiteParent) {
		this.entiteParent = entiteParent;
	}

	public Set<Entite> getEntitesEnfants() {
		return entitesEnfants;
	}

	public void setEntitesEnfants(Set<Entite> entitesEnfants) {
		this.entitesEnfants = entitesEnfants;
	}

	public TypeEntite getTypeEntite() {
		return typeEntite;
	}

	public void setTypeEntite(TypeEntite typeEntite) {
		this.typeEntite = typeEntite;
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

	public String getLabelCourt() {
		return labelCourt;
	}

	public void setLabelCourt(String labelCourt) {
		this.labelCourt = labelCourt;
	}

	public Entite getEntiteRemplacee() {
		return entiteRemplacee;
	}

	public void setEntiteRemplacee(Entite entiteRemplacee) {
		this.entiteRemplacee = entiteRemplacee;
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
