package nc.noumea.mairie.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SISERVNW")
@PersistenceUnit(unitName = "sirhPersistenceUnit")
@NamedQueries({ @NamedQuery(name = "getSiservNwFromCodeServi", query = "select n from SiservNw n where n.servi = :servi") })
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
	
	@NotNull
	@Column(name = "DATDEB", columnDefinition = "numeric")
	private Integer datDeb = 0;
	
	@NotNull
	@Column(name = "DATFIN", columnDefinition = "numeric")
	private Integer datFin = 0;
	
	@ManyToMany
	@JoinTable(name = "SISERVHIERARCHIE", joinColumns = @JoinColumn(name = "SERVI"), inverseJoinColumns = @JoinColumn(name = "SERVIPERE"))
	private Set<SiservNw> siservNwParent = new HashSet<SiservNw>();
	
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

	public Set<SiservNw> getSiservNwParent() {
		return siservNwParent;
	}

	public void setSiservNwParent(Set<SiservNw> siservNwParent) {
		this.siservNwParent = siservNwParent;
	}

	public Set<SiservNw> getSiservNwEnfant() {
		return siservNwEnfant;
	}

	public void setSiservNwEnfant(Set<SiservNw> siservNwEnfant) {
		this.siservNwEnfant = siservNwEnfant;
	}
	
}
