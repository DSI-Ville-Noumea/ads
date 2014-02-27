package nc.noumea.mairie.ads.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "ADS_NOEUD")
@PersistenceUnit(unitName = "adsPersistenceUnit")
public class Noeud {

	@Id
	@Column(name = "ID_NOEUD")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idNoeud;

	@Column(name = "ID_SERVICE")
	private Integer idService;

	@Column(name = "SIGLE")
	private String sigle;

	@Column(name = "LABEL")
	private String label;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ID_REVISION", referencedColumnName = "ID_REVISION")
	private Revision revision;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ID_NOEUD_PARENT")
	private Noeud noeudParent;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "noeudParent", cascade = CascadeType.ALL)
	@OrderBy("idService asc")
	private Set<Noeud> noeudsEnfants = new HashSet<Noeud>();

	@ManyToOne
	@JoinColumn(name = "ID_TYPE_NOEUD", referencedColumnName = "ID_TYPE_NOEUD")
	private TypeNoeud typeNoeud;

	@OneToOne(mappedBy = "noeud", optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	private SiservInfo siservInfo;

	public void addParent(Noeud parent) {
		this.noeudParent = parent;
		parent.getNoeudsEnfants().add(this);
	}

	@Column(name = "IS_ACTIF")
	private boolean actif;

	@Version
	@Column(name = "version")
	private Integer version;

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public long getIdNoeud() {
		return this.idNoeud;
	}

	public void setIdNoeud(long idNoeud) {
		this.idNoeud = idNoeud;
	}

	public Integer getIdService() {
		return this.idService;
	}

	public void setIdService(Integer idService) {
		this.idService = idService;
	}

	public String getSigle() {
		return this.sigle;
	}

	public void setSigle(String sigle) {
		this.sigle = sigle;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Revision getRevision() {
		return this.revision;
	}

	public void setRevision(Revision revision) {
		this.revision = revision;
	}

	public Noeud getNoeudParent() {
		return this.noeudParent;
	}

	public void setNoeudParent(Noeud noeudParent) {
		this.noeudParent = noeudParent;
	}

	public Set<Noeud> getNoeudsEnfants() {
		return this.noeudsEnfants;
	}

	public void setNoeudsEnfants(Set<Noeud> noeudsEnfants) {
		this.noeudsEnfants = noeudsEnfants;
	}

	public TypeNoeud getTypeNoeud() {
		return typeNoeud;
	}

	public void setTypeNoeud(TypeNoeud typeNoeud) {
		this.typeNoeud = typeNoeud;
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

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}
}
