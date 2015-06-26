package nc.noumea.mairie.ads.domain;

import javax.persistence.*;

@Entity
@Table(name = "ADS_TYPE_NOEUD")
@PersistenceUnit(unitName = "adsPersistenceUnit")
public class TypeNoeud {

	@Id
	@Column(name = "ID_TYPE_NOEUD")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idTypeNoeud;

	@Column(name = "LABEL")
	private String label;

	@Column(name = "IS_ACTIF")
	private boolean actif = true;

	public int getIdTypeNoeud() {
		return idTypeNoeud;
	}

	public void setIdTypeNoeud(int idTypeNoeud) {
		this.idTypeNoeud = idTypeNoeud;
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
