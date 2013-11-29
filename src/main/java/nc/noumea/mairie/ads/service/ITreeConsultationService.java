package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;

public interface ITreeConsultationService {

	RevisionDto getLatestRevision();
	
	NoeudDto getTreeOfLatestRevisionTree();
	
	byte[] exportTreeOfLatestRevisionToGraphMl();
	
}
