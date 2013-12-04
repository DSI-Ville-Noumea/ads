package nc.noumea.mairie.ads.viewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.IRevisionService;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.DefaultTreeModel;

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
		
		// Sending the event of revision change
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("revision", this.selectedRevision);
		BindUtils.postGlobalCommand(null, null, "updateSelectedRevision", args);
	}

	public RevisionListViewModel() {
		revisionService = (IRevisionService) SpringUtil
				.getBean("revisionService");
		setRevisions(revisionService.getRevisionsByDateEffetDesc());
	}
	
	@GlobalCommand
	@NotifyChange({ "revisions" })
	public void revisionListChanged() {
		setRevisions(revisionService.getRevisionsByDateEffetDesc());
	}
}
