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
}
