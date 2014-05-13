package nc.noumea.mairie.ads.domain;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ADS_NOEUD")
@PersistenceUnit(unitName = "adsPersistenceUnit")
@NamedQueries({
		@NamedQuery(name = "getNoeudFromIdServiceAndRevision", query = "select n from Noeud n where n.revision.idRevision = :idRevision and n.idService = :idService"),
		@NamedQuery(name = "getNoeudFromCodeServiAndRevision", query = "select n from Noeud n inner join n.siservInfo s where n.revision.idRevision = :idRevision and LOWER(s.codeServi) = LOWER(:codeServi)")
})
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
	private Set<Noeud> noeudsEnfants = new HashSet<>();

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
	private boolean actif = true;

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
