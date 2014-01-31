package nc.noumea.mairie.ads.viewModel;

import nc.noumea.mairie.ads.service.IReferenceDataService;
import nc.noumea.mairie.ads.view.tools.ViewModelHelper;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Window;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class NewReferenceDataViewModel {

	@WireVariable
	private IReferenceDataService referenceDataService;
	
	@WireVariable
	private ViewModelHelper viewModelHelper;
	
	private String newLabel;

	public String getNewLabel() {
		return newLabel;
	}

	public void setNewLabel(String newLabel) {
		this.newLabel = newLabel;
	}

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private boolean cantSave = true;

	public boolean isCantSave() {
		return cantSave;
	}

	public void setCantSave(boolean cantSave) {
		this.cantSave = cantSave;
	}

	public NewReferenceDataViewModel() {

	}

	@Command
	public void closeWindowCommand(@BindingParam("win") Window x) {
		x.detach();
	}

	@Command
	@NotifyChange({ "cantSave", "message" })
	public void checkValueCommand(@BindingParam("changingLabel") String changingLabel) {
		
		if (changingLabel == null || changingLabel.equals("")) {
			cantSave = true;
			message = "Le label ne peut être vide !";
			return;
		}
		
		if (referenceDataService.doesTypeNoeudValueAlreadyExists(changingLabel)) {
			cantSave = true;
			message = "Ce type existe déjà !";
			return;
		}
		
		cantSave = false;
		message = null;
	}
	
	@Command
	public void saveNewValueCommand(@BindingParam("win") Window x) {
		referenceDataService.saveNewTypeNoeud(newLabel);
		closeWindowCommand(x);
		viewModelHelper.postGlobalCommand(null, null, "typeNoeudListChangedGlobalCommand", null);
	}
}
