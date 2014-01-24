package nc.noumea.mairie.ads.viewModel;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.service.ITreeConsultationService;
import nc.noumea.mairie.ads.view.tools.ViewModelHelper;

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

	@WireVariable
	private ViewModelHelper viewModelHelper;

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

	private boolean isSaving;

	public boolean isSaving() {
		return isSaving;
	}

	public void setSaving(boolean isSaving) {
		this.isSaving = isSaving;
	}

	public AdsViewModel() {
		updateSelectedRevision(null);
	}

	@GlobalCommand
	public void updateSelectedRevision(@BindingParam("revision") RevisionDto revision) {
		setSelectedRevision(revision);
	}

	/**
	 * This is the second part of the saveRevisionCommand. This method is called by thisIsTheCurrentRevisionTree global command
	 * once this main ViewModel has been made aware of what is the current revisionTree to save
	 * @param revisionTree
	 */
	@GlobalCommand
	public void thisIsTheCurrentRevisionTree(@BindingParam("currentRevisionTree") NoeudDto revisionTree) {
		
		if (!isSaving)
			return;

		createTreeService.createTreeFromRevisionAndNoeuds(selectedRevision, revisionTree);
		isSaving = false;
		
		// After saving everything, call the cancel command which triggers the reloading of the revision list
		cancelRevisionCommand();
	}

	/**
	 * This method will perform differently whether we're currently saving or
	 * trying to - If not currently saving, call global command
	 * "whatIsTheCurrentRevisionTree" in order for someone to answer with the
	 * globalcommand "thisIsTheCurrentRevisionTree" - From there, it will then
	 * call saveRevisionCommand with the returned revisionTree
	 */
	@Command
	public void saveRevisionCommand() {

		if (isSaving)
			return;

		isSaving = true;
		viewModelHelper.postGlobalCommand(null, null, "whatIsTheCurrentRevisionTree", null);
		return;

	}

	@Command
	@NotifyChange({ "editMode", "viewMode" })
	public void cancelRevisionCommand() {
		if (!isSaving && editMode) {
			editMode = false;
			viewMode = true;
			viewModelHelper.postGlobalCommand(null, null, "revisionListChanged", null);
		}
	}

	@Command
	@NotifyChange({ "editMode", "viewMode" })
	public void newRevisionCommand() {
		editMode = true;
		viewMode = false;
		viewModelHelper.postGlobalCommand(null, null, "newRevisionFromCurrentOne", null);
	}
}
