package nc.noumea.mairie.ads.viewModel;

import java.util.List;

import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.IRevisionService;

import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RevisionListViewModel {

	@WireVariable
	private IRevisionService revisionService;

	private List<RevisionDto> revisions;

	public List<RevisionDto> getRevisions() {
		return revisions;
	}

	public void setRevisions(List<RevisionDto> revisions) {
		this.revisions = revisions;
	}

	private RevisionDto selectedRevision;

	public RevisionDto getSelectedRevision() {
		return selectedRevision;
	}

	public void setSelectedRevision(RevisionDto selectedRevision) {
		this.selectedRevision = selectedRevision;
	}

	public RevisionListViewModel() {

	}
	
	@GlobalCommand
	@NotifyChange({ "revisions" })
	public void revisionListChanged() {
		setRevisions(revisionService.getRevisionsByDateEffetDesc());
		selectedRevision = null;
	}
}
