package nc.noumea.mairie.ads.viewModel;

import java.util.HashMap;
import java.util.Map;

import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.service.ITreeConsultationService;
import nc.noumea.mairie.ads.view.tools.ViewModelHelper;

import org.zkoss.bind.annotation.Command;
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
	 * This method will perform differently whether we're currently saving or
	 * trying to - If not currently saving, call global command
	 * "whatIsTheCurrentTree" in order for someone to answer with the
	 * globalcommand "thisIsTheCurrentTree" - From there, it will then call
	 * saveCommand with the returned revisionTree
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
