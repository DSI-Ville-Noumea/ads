package nc.noumea.mairie.ads.dto;

import nc.noumea.mairie.ads.domain.Entite;

public class DiffEntiteDto extends EntiteDto {

	private EntiteDto parent;

	public DiffEntiteDto(Entite entite) {
		mapEntite(entite);

		if (entite.getEntiteParent() != null)
			parent = new EntiteDto().mapEntite(entite.getEntiteParent());
	}

	public EntiteDto getParent() {
		return parent;
	}

	public void setParent(EntiteDto parent) {
		this.parent = parent;
	}
}
