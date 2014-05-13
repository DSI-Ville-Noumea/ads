package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.dto.NoeudDto;

public interface ITreeConsultationService {

	NoeudDto getTreeOfLatestRevisionTree();
	
	NoeudDto getTreeOfSpecificRevision(long idRevision);

	NoeudDto getNodeByIdService(int idService);

	NoeudDto getNodeByCodeService(String codeServi);
	
}
