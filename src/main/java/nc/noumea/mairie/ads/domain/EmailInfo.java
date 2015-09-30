package nc.noumea.mairie.ads.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;

@Entity
@Table(name = "ADS_EMAIL_INFO")
@PersistenceUnit(unitName = "adsPersistenceUnit")
@NamedQueries({ @NamedQuery(name = "getListeIdAgentEmailInfo", query = "select n.idAgent from EmailInfo n where n.actif = true") })
public class EmailInfo {

	@Id
	@Column(name = "ID_EMAIL_INFO")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	idEmailInfo;

	@Column(name = "ID_AGENT")
	private Integer	idAgent;

	@Column(name = "IS_ACTIF")
	private boolean	actif	= true;

	public long getIdEmailInfo() {
		return idEmailInfo;
	}

	public void setIdEmailInfo(long idEmailInfo) {
		this.idEmailInfo = idEmailInfo;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

}
