package nc.noumea.mairie.sirh.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SISERV")
@PersistenceUnit(unitName = "sirhPersistenceUnit")
public class Siserv {

	@Id
	@Column(name = "SERVI", columnDefinition = "char")
	private String servi;

	@NotNull
	@Column(name = "LISERV", columnDefinition = "char")
	private String liServ;

	@NotNull
	@Column(name = "CODACT", columnDefinition = "char")
	private String codeActif;

	@NotNull
	@Column(name = "SIGLE", columnDefinition = "char")
	private String sigle;

	@NotNull
	@Column(name = "DEPEND", columnDefinition = "char")
	private String parentSigle;

	//@OneToOne(mappedBy = "siserv", optional = true)
	private transient SiservAds siservAds;

	public String getServi() {
		return servi;
	}

	public void setServi(String servi) {
		this.servi = servi;
	}

	public String getLiServ() {
		return liServ;
	}

	public void setLiServ(String liServ) {
		this.liServ = liServ;
	}

	public String getCodeActif() {
		return codeActif;
	}

	public void setCodeActif(String codeActif) {
		this.codeActif = codeActif;
	}

	public String getSigle() {
		return sigle;
	}

	public void setSigle(String sigle) {
		this.sigle = sigle;
	}

	public String getParentSigle() {
		return parentSigle;
	}

	public void setParentSigle(String parentSigle) {
		this.parentSigle = parentSigle;
	}

	public SiservAds getSiservAds() {
		return siservAds;
	}

	public void setSiservAds(SiservAds siservAds) {
		this.siservAds = siservAds;
	}
}
