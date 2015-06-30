package nc.noumea.mairie.sirh.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SISERVNW")
@PersistenceUnit(unitName = "sirhPersistenceUnit")
public class SiservNw {
	
	@Id
	@Column(name = "SERVI", columnDefinition = "char")
	private String servi;
	
	@ManyToOne
	@JoinColumn(name = "SERVIOLD", referencedColumnName = "SERVI")
	private Siserv siServ;
	
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

	@NotNull
	@Column(name = "LI22", columnDefinition = "char")
	private String li22;

	@OneToMany(mappedBy = "siservNw", cascade = CascadeType.ALL)
	private Set<SiservAds> siservAds = new HashSet<>();
	
	@ManyToMany
	@JoinTable(name = "SISERVHIERARCHIE", joinColumns = @JoinColumn(name = "SERVIPERE"), inverseJoinColumns = @JoinColumn(name = "SERVI"))
	private Set<SiservNw> siservNwEnfant = new HashSet<SiservNw>();

	public String getServi() {
		return servi;
	}

	public void setServi(String servi) {
		this.servi = servi;
	}

	public Siserv getSiServ() {
		return siServ;
	}

	public void setSiServ(Siserv siServ) {
		this.siServ = siServ;
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

	public String getLi22() {
		return li22;
	}

	public void setLi22(String li22) {
		this.li22 = li22;
	}

	public Set<SiservAds> getSiservAds() {
		return siservAds;
	}

	public void setSiservAds(Set<SiservAds> siservAds) {
		this.siservAds = siservAds;
	}
}
