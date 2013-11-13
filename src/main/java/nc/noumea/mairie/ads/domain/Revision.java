package nc.noumea.mairie.ads.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(table = "ADS_REVISION")
public class Revision {
	
	@Id
	@Column(name = "ID_REVISION")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idRevision;
	
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@Column(name = "DATE_MODIF")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateModif;

	@Column(name = "DATE_EFFET")
	@Temporal(TemporalType.DATE)
	private Date dateEffet;

	@Column(name = "DATE_DECRET")
	@Temporal(TemporalType.DATE)
	private Date dateDecret;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
}
