package nc.noumea.mairie.ads.viewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.IRevisionService;
import nc.noumea.mairie.ads.view.tools.ViewModelHelper;

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
	
	@WireVariable
	private ViewModelHelper viewModelHelper;

	private List<RevisionListItemViewModel> revisions;
	
	public List<RevisionListItemViewModel> getRevisions() {
		return revisions;
	}

	public void setRevisions(List<RevisionListItemViewModel> revisions) {
		this.revisions = revisions;
	}
	
	private RevisionListItemViewModel selectedRevision;

	public RevisionListItemViewModel getSelectedRevision() {
		return selectedRevision;
	}

	public void setSelectedRevision(RevisionListItemViewModel selectedRevision) {
		this.selectedRevision = selectedRevision;
	}
	
	private RevisionListItemViewModel revisionListItem;
	
	public RevisionListItemViewModel getRevisionListItem() {
		return revisionListItem;
	}

	public void setRevisionListItem(RevisionListItemViewModel revisionListItem) {
		this.revisionListItem = revisionListItem;
	}

	public RevisionListViewModel() {
		revisions = new ArrayList<RevisionListItemViewModel>();
	}
	
	@GlobalCommand
	@NotifyChange({ "revisions" })
	public void revisionListChanged() {
		
		revisions.clear();
		
		for (RevisionDto rDto : revisionService.getRevisionsByDateEffetDesc()) {
			revisions.add(new RevisionListItemViewModel(rDto));
		}
		
		selectedRevision = null;
	}
	
	@Command
	public void exportSelectedRevision(@BindingParam("revision") RevisionDto revision) {
		Executions.getCurrent().sendRedirect(String.format("/api/arbre?format=graphml&idRevision=%s", revision.getIdRevision()), "_blank");
	}
	
	@GlobalCommand
	@NotifyChange({ "selectedRevision" })
	public void newRevisionFromCurrentOne() {
		
		RevisionListItemViewModel vM = revisions.get(0);
		this.setSelectedRevision(vM);
		vM.setEditModeStyle(true);
		
		// Send global command
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("revision", vM.getRevision());
		viewModelHelper.postGlobalCommand(null, null, "updateSelectedRevision", args);
	}
}
