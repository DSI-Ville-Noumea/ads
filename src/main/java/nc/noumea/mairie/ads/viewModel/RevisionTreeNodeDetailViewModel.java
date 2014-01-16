package nc.noumea.mairie.ads.viewModel;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;

import nc.noumea.mairie.ads.dto.NoeudDto;

public class RevisionTreeNodeDetailViewModel {

	private NoeudDto selectedNoeud;

	public NoeudDto getSelectedNoeud() {
		return selectedNoeud;
	}

	public void setSelectedNoeud(NoeudDto selectedNoeud) {
		this.selectedNoeud = selectedNoeud;
	}

	@GlobalCommand
	@NotifyChange("selectedNoeud")
	public void revisionTreeNodeSelectedChangeCommand(@BindingParam("treeNode") NoeudDto treeNode) {
		this.setSelectedNoeud(treeNode);
	}

	@GlobalCommand
	@NotifyChange("selectedNoeud")
	public void updateSelectedRevision() {
		// This global command is executed here in order to clear the display of
		// a previously selected node of a different revision
		this.setSelectedNoeud(null);
	}
}
