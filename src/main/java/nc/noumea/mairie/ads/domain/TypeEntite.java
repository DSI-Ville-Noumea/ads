package nc.noumea.mairie.ads.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;

@Entity
@Table(name = "ADS_TYPE_ENTITE")
@PersistenceUnit(unitName = "adsPersistenceUnit")
public class TypeEntite {

	@Id
	@Column(name = "ID_TYPE_ENTITE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idTypeEntite;

	@Column(name = "LABEL")
	private String label;
	
	public Integer getIdTypeEntite() {
		return idTypeEntite;
	}

	public void setIdTypeEntite(Integer idTypeEntite) {
		this.idTypeEntite = idTypeEntite;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
