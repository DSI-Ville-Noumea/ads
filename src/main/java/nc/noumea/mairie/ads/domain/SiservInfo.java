package nc.noumea.mairie.ads.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(table = "ADS_SISERV_INFO")
public class SiservInfo {

	@Id
	@Column(name = "ID_SISERV_INFO")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idSiservInfo;

	@OneToOne(optional = false)
	@JoinColumn(name = "ID_NOEUD")
	private Noeud noeud;
	
	@Column(name = "CODE_SERVI")
	private String codeServi;
	
}
