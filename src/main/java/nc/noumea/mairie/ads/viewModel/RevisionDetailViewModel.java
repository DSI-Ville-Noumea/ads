package nc.noumea.mairie.ads.viewModel;

import nc.noumea.mairie.ads.dto.RevisionDto;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;

public class RevisionDetailViewModel {

	private RevisionDto revision;

	public RevisionDto getRevision() {
		return revision;
	}

	public void setRevision(RevisionDto revision) {
		this.revision = revision;
	}

	private boolean editMode;
	
	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public RevisionDetailViewModel() {
		
	}
	
	@GlobalCommand
    @NotifyChange("revision")
    public void updateSelectedRevision(@BindingParam("revision") RevisionDto revision) {
		setRevision(revision);
    }
	
	@GlobalCommand
    @NotifyChange("revision")
	public void revisionListChanged() {
		setRevision(null);
	}
	
	@GlobalCommand
	@NotifyChange({ "editMode" })
	public void toggleEditModeGlobalCommand(@BindingParam("editMode") boolean editMode) {
		this.editMode = editMode;
	}
}
