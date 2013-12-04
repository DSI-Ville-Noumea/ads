package nc.noumea.mairie.ads.viewModel;

import nc.noumea.mairie.ads.dto.RevisionDto;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;

public class RevisionViewModel {

	private RevisionDto revision;

	public RevisionDto getRevision() {
		return revision;
	}

	public void setRevision(RevisionDto revision) {
		this.revision = revision;
	}

	public RevisionViewModel() {
		
	}
	
	@GlobalCommand
    @NotifyChange("revision")
    public void updateSelectedRevision(@BindingParam("revision") RevisionDto revision) {
		setRevision(revision);
    }
}
