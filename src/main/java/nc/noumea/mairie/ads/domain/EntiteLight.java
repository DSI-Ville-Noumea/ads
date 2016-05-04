package nc.noumea.mairie.ads.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "ADS_ENTITE")
@PersistenceUnit(unitName = "adsPersistenceUnit")
public class EntiteLight {

	@Id
	@Column(name = "ID_ENTITE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idEntite;

	@NotNull
	@Column(name = "SIGLE", length = 8)
	private String sigle;

	@NotNull
	@Column(name = "LABEL")
	private String label;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_ENTITE_PARENT")
	private EntiteLight entiteParent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "entiteParent", cascade = CascadeType.ALL)
	@OrderBy("idEntite asc")
	private Set<EntiteLight> entitesEnfants = new HashSet<>();

	@Column(name = "ID_REF_STATUT_ENTITE")
	@Enumerated(EnumType.ORDINAL)
	private StatutEntiteEnum statut;

	@ManyToOne
	@JoinColumn(name = "ID_TYPE_ENTITE", referencedColumnName = "ID_TYPE_ENTITE")
	private TypeEntite typeEntite;


	public String getSigle() {
		return this.sigle;
	}

	public void setSigle(String sigle) {
		this.sigle = sigle.toUpperCase();
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public Integer getIdEntite() {
		return idEntite;
	}

	public void setIdEntite(Integer idEntite) {
		this.idEntite = idEntite;
	}

	public EntiteLight getEntiteParent() {
		return entiteParent;
	}

	public void setEntiteParent(EntiteLight entiteParent) {
		this.entiteParent = entiteParent;
	}

	public Set<EntiteLight> getEntitesEnfants() {
		return entitesEnfants;
	}

	public void setEntitesEnfants(Set<EntiteLight> entitesEnfants) {
		this.entitesEnfants = entitesEnfants;
	}

	public TypeEntite getTypeEntite() {
		return typeEntite;
	}

	public void setTypeEntite(TypeEntite typeEntite) {
		this.typeEntite = typeEntite;
	}

	public StatutEntiteEnum getStatut() {
		return statut;
	}

	public void setStatut(StatutEntiteEnum statut) {
		this.statut = statut;
	}
}
