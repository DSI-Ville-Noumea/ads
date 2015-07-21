package nc.noumea.mairie.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SISERV")
@PersistenceUnit(unitName = "sirhPersistenceUnit")
@NamedQueries({ @NamedQuery(name = "getSiservFromCodeServi", query = "select n from Siserv n where n.servi = :servi"),
	@NamedQuery(name = "getSiservFromParentSigle", query = "select n from Siserv n where n.parentSigle = :parentSigle")})
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

	@NotNull
	@Column(name = "LI22", columnDefinition = "char")
	private String li22;

	@OneToMany(mappedBy = "siServ", cascade = CascadeType.ALL)
	private Set<SiservNw> siservNw = new HashSet<SiservNw>();

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

	public Set<SiservNw> getSiservNw() {
		return siservNw;
	}

	public void setSiservNw(Set<SiservNw> siservNw) {
		this.siservNw = siservNw;
	}

	public String getLi22() {
		return li22;
	}

	public void setLi22(String li22) {
		this.li22 = li22;
	}
}
