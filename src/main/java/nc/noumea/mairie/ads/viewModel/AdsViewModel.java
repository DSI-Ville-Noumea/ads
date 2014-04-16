package nc.noumea.mairie.ads.viewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.service.ITreeConsultationService;
import nc.noumea.mairie.ads.view.tools.ViewModelHelper;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
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

		// Every time a revision is selected, make sure we update the editMode
		// of all views based on whether we can or not modify the revision
		if (viewModelHelper != null) {
			Map<String, Object> args = new HashMap<>();
			args.put("editMode", revision != null && revision.isCanEdit() && editMode);
			viewModelHelper.postGlobalCommand(null, null, "toggleEditModeGlobalCommand", args);
		}
	}

	/**
	 * This is the second part of the saveRevisionCommand. This method is called
	 * by thisIsTheCurrentRevisionTree global command once this main ViewModel
	 * has been made aware of what is the current revisionTree to save
	 *
	 * @param revisionTree
	 */
	@GlobalCommand
	@NotifyChange({ "editMode" })
	public void thisIsTheCurrentRevisionTree(@BindingParam("currentRevisionTree") NoeudDto revisionTree) {

		if (!isSaving)
			return;

		List<ErrorMessageDto> result = createTreeService
				.createTreeFromRevisionAndNoeuds(selectedRevision, revisionTree);
		isSaving = false;

		// If there was no error saving
		if (result.size() == 0) {
			// After saving everything, call the cancel command which triggers
			// the reloading of the revision list
			editMode = false;
			viewModelHelper.postGlobalCommand(null, null, "revisionListChanged", null);

			Map<String, Object> args = new HashMap<>();
			args.put("editMode", false);
			viewModelHelper.postGlobalCommand(null, null, "toggleEditModeGlobalCommand", args);

			viewModelHelper.postGlobalCommand(null, null, "setErrorMessagesGlobalCommand", null);
		} else {
			// if there was at least one error, display them
			Map<String, Object> params = new HashMap<>();
			params.put("messages", result);
			viewModelHelper.postGlobalCommand(null, null, "setErrorMessagesGlobalCommand", params);
		}
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

	}

	@Command
	@NotifyChange({ "editMode" })
	public void cancelRevisionCommand() {
		if (!isSaving && editMode) {
			editMode = false;
			viewModelHelper.postGlobalCommand(null, null, "revisionListChanged", null);

			Map<String, Object> args = new HashMap<>();
			args.put("editMode", false);
			viewModelHelper.postGlobalCommand(null, null, "toggleEditModeGlobalCommand", args);

			viewModelHelper.postGlobalCommand(null, null, "setErrorMessagesGlobalCommand", null);
		}
	}

	@Command
	@NotifyChange({ "editMode" })
	public void newRevisionCommand() {
		editMode = true;
		viewModelHelper.postGlobalCommand(null, null, "newRevisionFromCurrentOne", null);
	}

	@Command
	public void openAboutDialogCommand() {
		Executions.createComponents("adsAboutWindow.zul", null, null);
	}
}
