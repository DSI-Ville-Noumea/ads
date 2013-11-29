package nc.noumea.mairie.ads.viewModel;

import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.SpringUtil;

import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.ITreeConsultationService;

public class RevisionViewModel {

	@WireVariable
	private ITreeConsultationService treeConsultationService;
	
	private RevisionDto revision;

	public RevisionDto getRevision() {
		return revision;
	}

	public void setRevision(RevisionDto revision) {
		this.revision = revision;
	}

	public RevisionViewModel() {
		treeConsultationService = (ITreeConsultationService) SpringUtil
				.getBean("treeConsultationService");
		
		revision = treeConsultationService.getLatestRevision();
	}
}
