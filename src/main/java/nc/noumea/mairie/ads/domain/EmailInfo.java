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
@NamedQueries({ @NamedQuery(name = "getListeDestinataireEmailInfo", query = "select n.mail from EmailInfo n where n.destinataire = true"),
		@NamedQuery(name = "getListeCopieEmailInfo", query = "select n.mail from EmailInfo n where n.copie = true"),
		@NamedQuery(name = "getListeCopieCacheeEmailInfo", query = "select n.mail from EmailInfo n where n.copieCachee = true") })
public class EmailInfo {

	@Id
	@Column(name = "ID_EMAIL_INFO")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	idEmailInfo;

	@Column(name = "MAIL")
	private String	mail;

	@Column(name = "IS_DESTINATAIRE")
	private boolean	destinataire	= false;

	@Column(name = "IS_CC")
	private boolean	copie			= false;

	@Column(name = "IS_CI")
	private boolean	copieCachee		= false;

	public long getIdEmailInfo() {
		return idEmailInfo;
	}

	public void setIdEmailInfo(long idEmailInfo) {
		this.idEmailInfo = idEmailInfo;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public boolean isDestinataire() {
		return destinataire;
	}

	public void setDestinataire(boolean destinataire) {
		this.destinataire = destinataire;
	}

	public boolean isCopie() {
		return copie;
	}

	public void setCopie(boolean copie) {
		this.copie = copie;
	}

	public boolean isCopieCachee() {
		return copieCachee;
	}

	public void setCopieCachee(boolean copieCachee) {
		this.copieCachee = copieCachee;
	}
}
