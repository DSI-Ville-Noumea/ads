package nc.noumea.mairie.ads.domain;

import javax.persistence.*;

@Entity
@Table(name = "ADS_TYPE_ENTITE")
@PersistenceUnit(unitName = "adsPersistenceUnit")
public class TypeEntite {

	@Id
	@Column(name = "ID_TYPE_ENTITE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idTypeEntite;

	@Column(name = "LABEL")
	private String label;

	@Column(name = "IS_ACTIF")
	private boolean actif = true;


	public int getIdTypeEntite() {
		return idTypeEntite;
	}

	public void setIdTypeEntite(int idTypeEntite) {
		this.idTypeEntite = idTypeEntite;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}
}
