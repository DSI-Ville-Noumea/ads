package nc.noumea.mairie.ads.viewModel;

import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.IRevisionService;
import nc.noumea.mairie.ads.view.tools.ViewModelHelper;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private boolean editMode;

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public RevisionListViewModel() {
		revisions = new ArrayList<>();
	}

	@GlobalCommand
	@NotifyChange({ "revisions", "selectedRevision" })
	public void revisionListChanged() {

		selectedRevision = null;
		revisions.clear();

		for (RevisionDto rDto : revisionService.getRevisionsByDateEffetDesc()) {
			revisions.add(new RevisionListItemViewModel(rDto));
		}
	}

	@Command
	public void exportSelectedRevision(@BindingParam("revision") RevisionDto revision) {
		Executions.getCurrent().sendRedirect(
				String.format("/api/arbre/%s/graphml", revision.getIdRevision()), "_blank");
	}

	@Command
	public void rollBackToSelectedRevision(@BindingParam("revision") final RevisionDto revision) {
		Messagebox.show("Vous êtes sur le point de réappliquer l'arbre de cette révision. La table SISERV sera mise à jour par le job automatique. Êtes-vous sûr de vouloir continuer ?",
				"Confirmation", Messagebox.OK | Messagebox.CANCEL,
				Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener(){
					public void onEvent(Event e){
						if(Messagebox.ON_OK.equals(e.getName())){
							revisionService.rollbackToPreviousRevision(revision, revision.getIdRevision());
						}else if(Messagebox.ON_CANCEL.equals(e.getName())){
							return;
						}
					}
				}
		);

	}

	@GlobalCommand
	@NotifyChange({ "selectedRevision", "editModeStyle" })
	public void newRevisionFromCurrentOne() {

		RevisionListItemViewModel vM = revisions.get(0);
		this.setSelectedRevision(vM);
		vM.setEditModeStyle(true);
		viewModelHelper.postNotifyChange(null, null, this.selectedRevision, "editModeStyle");

		// Send global command
		Map<String, Object> args = new HashMap<>();
		args.put("revision", vM.getRevision());
		viewModelHelper.postGlobalCommand(null, null, "updateSelectedRevision", args);
	}

}
