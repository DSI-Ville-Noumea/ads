package nc.noumea.mairie.ads.viewModel;

import nc.noumea.mairie.ads.dto.RevisionDto;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;

public class RevisionListItemViewModel {

	private RevisionDto revision;

	public RevisionDto getRevision() {
		return revision;
	}

	public void setRevision(RevisionDto revision) {
		this.revision = revision;
	}

	private boolean editModeStyle;

	public boolean isEditModeStyle() {
		return editModeStyle;
	}

	@NotifyChange("editModeStyle")
	public void setEditModeStyle(boolean editModeStyle) {
		this.editModeStyle = editModeStyle;
	}

	public RevisionListItemViewModel() {

	}

	public RevisionListItemViewModel(RevisionDto revision) {
		this.setRevision(revision);
	}

}
