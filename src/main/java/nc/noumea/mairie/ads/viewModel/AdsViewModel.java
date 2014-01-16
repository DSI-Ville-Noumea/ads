package nc.noumea.mairie.ads.viewModel;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.service.ITreeConsultationService;

import org.zkoss.bind.BindUtils;
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

	private NoeudDto revisionTree;
	
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
	
	private boolean isSaving;

	public AdsViewModel() {
		updateSelectedRevision(null);
	}

	@GlobalCommand
	public void updateSelectedRevision(@BindingParam("revision") RevisionDto revision) {
		setSelectedRevision(revision);
	}
	
	@GlobalCommand
	public void thisIsTheCurrentRevisionTree(@BindingParam("currentRevisionTree") NoeudDto revisionTree) {
		this.revisionTree = revisionTree;
		if (isSaving)
			saveRevisionCommand();
	}

	/**
	 * This method will perform differently whether we're currently saving or trying to
	 * - If not currently saving, call global command "whatIsTheCurrentRevisionTree" in order
	 * for someone to answer with the globalcommand "thisIsTheCurrentRevisionTree"
	 * - If already saving, call the createTreeService in order to save the tree and its 
	 * revision info
	 */
	@Command
	public void saveRevisionCommand() {
		
		if (!isSaving) {
			isSaving = true;
			BindUtils.postGlobalCommand(null, null, "whatIsTheCurrentRevisionTree", null);
			return;
		}
		
		createTreeService.createTreeFromRevisionAndNoeuds(selectedRevision, revisionTree);
		isSaving = false;
	}

	@Command
	public void cancelRevisionCommand() {
		if (!isSaving && editMode) {
			editMode = false;
		}
	}
	
	@Command
	@NotifyChange({ "editMode", "viewMode" })
	public void newRevisionCommand() {
		editMode = true;
		viewMode = false;
	}
}
