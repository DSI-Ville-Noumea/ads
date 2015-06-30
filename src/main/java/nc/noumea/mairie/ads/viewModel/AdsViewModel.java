package nc.noumea.mairie.ads.viewModel;

import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.EntiteDto;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class AdsViewModel {

	@WireVariable
	private ITreeConsultationService treeConsultationService;

	@WireVariable
	private ICreateTreeService createTreeService;

	@WireVariable
	private ViewModelHelper viewModelHelper;

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

	/**
	 * This is the second part of the saveCommand. This method is called
	 * by thisIsTheCurrentTree global command once this main ViewModel
	 * has been made aware of what is the current tree to save
	 *
	 * @param currentTree EntiteDto
	 */
	@GlobalCommand
	@NotifyChange({ "editMode" })
	public void thisIsTheCurrentTree(@BindingParam("currentTree") EntiteDto tree) {

		if (!isSaving) {
			return;
		}

		List<ErrorMessageDto> result = createTreeService
				.createTreeFromEntites(tree);
		isSaving = false;

		// If there was no error saving
		if (result.size() == 0) {
			// After saving everything, call the cancel command which triggers
			// the reloading of the revision list
			editMode = false;
			viewModelHelper.postGlobalCommand(null, null, "listChanged", null);

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
	 * "whatIsTheCurrentTree" in order for someone to answer with the
	 * globalcommand "thisIsTheCurrentTree" - From there, it will then
	 * call saveCommand with the returned revisionTree
	 */
	@Command
	public void saveCommand() {

		if (isSaving) {
			return;
		}

		isSaving = true;
		viewModelHelper.postGlobalCommand(null, null, "whatIsTheCurrentTree", null);

	}

	@Command
	@NotifyChange({ "editMode" })
	public void cancelCommand() {
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
	public void openAboutDialogCommand() {
		Executions.createComponents("adsAboutWindow.zul", null, null);
	}
}
