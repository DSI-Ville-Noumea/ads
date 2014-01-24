package nc.noumea.mairie.ads.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "ADS_NOEUD")
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
	
	@OneToOne(optional = true)
	@JoinColumn(table = "ADS_SISERV_INFO", name = "ID_NOEUD")
	private SiservInfo siservInfo;
	
	public void addParent(Noeud parent) {
		this.noeudParent = parent;
		parent.getNoeudsEnfants().add(this);
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

	public SiservInfo getSiservInfo() {
        return this.siservInfo;
    }

	public void setSiservInfo(SiservInfo siservInfo) {
        this.siservInfo = siservInfo;
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
