package nc.noumea.mairie.ads.domain;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_REVISION", referencedColumnName = "ID_REVISION")
	private Revision revision;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_NOEUD_PARENT")
	private Noeud noeudParent;

	@OneToOne(optional = true)
	@JoinColumn(table = "ADS_SISERV_INFO", name = "ID_NOEUD")
	private SiservInfo siservInfo;
}
