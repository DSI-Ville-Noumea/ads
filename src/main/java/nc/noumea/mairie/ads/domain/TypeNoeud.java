package nc.noumea.mairie.ads.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ADS_TYPE_NOEUD")
public class TypeNoeud {

	@Id
	@Column(name = "ID_TYPE_NOEUD")
	private long idTypeNoeud;

	@Column(name = "LABEL")
	private String label;

	public long getIdTypeNoeud() {
		return idTypeNoeud;
	}

	public void setIdTypeNoeud(long idTypeNoeud) {
		this.idTypeNoeud = idTypeNoeud;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
