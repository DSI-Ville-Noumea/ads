package nc.noumea.mairie.ads.viewModel;

import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.service.ITreeConsultationService;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class AdsViewModel {

	@WireVariable
	private ITreeConsultationService treeConsultationService;

	@WireVariable
	private ICreateTreeService createTreeService;

	private RevisionDto selectedRevision;

	public RevisionDto getSelectedRevision() {
		return selectedRevision;
	}

	public void setSelectedRevision(RevisionDto selectedRevision) {
		this.selectedRevision = selectedRevision;
	}

	private boolean editMode;

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	private boolean viewMode = true;

	public boolean isViewMode() {
		return viewMode;
	}

	public void setViewMode(boolean viewMode) {
		this.viewMode = viewMode;
	}

	public AdsViewModel() {
		updateSelectedRevision(null);
	}

	@GlobalCommand
	public void updateSelectedRevision(@BindingParam("revision") RevisionDto revision) {
		setSelectedRevision(revision);
	}

	@Command
	public void saveRevisionCommand() {
//		createTreeService.createTreeFromRevisionAndNoeuds(selectedRevision, buildTreeNodes(noeudTree.getRoot()));
	}

	@Command
	@NotifyChange({ "editMode", "viewMode" })
	public void newRevisionCommand() {
		editMode = true;
		viewMode = false;
	}
}
