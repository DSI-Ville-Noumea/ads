package nc.noumea.mairie.ads.viewModel;

import java.util.List;

import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.IRevisionService;
import nc.noumea.mairie.ads.service.ITreeConsultationService;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
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
	
	@Command
	public void exportSelectedRevision(@BindingParam("revision") RevisionDto revision) {
		Executions.getCurrent().sendRedirect(String.format("/api/arbre?format=graphml&idRevision=%s", revision.getIdRevision()), "_blank");
	}
}
