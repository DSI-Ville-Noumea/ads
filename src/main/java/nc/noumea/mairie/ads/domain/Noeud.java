package nc.noumea.mairie.ads.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(table = "ADS_NOEUD")
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
}
