package nc.noumea.mairie.ads.dto;

import nc.noumea.mairie.ads.domain.Noeud;

public class DiffNoeudDto extends NoeudDto {

	private NoeudDto parent;

	public DiffNoeudDto(Noeud noeud) {
		mapNoeud(noeud);

		if (noeud.getNoeudParent() != null)
			parent = new NoeudDto().mapNoeud(noeud.getNoeudParent());
	}

	public NoeudDto getParent() {
		return parent;
	}

	public void setParent(NoeudDto parent) {
		this.parent = parent;
	}
}
