package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.repository.ITreeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TreeConsultationService implements ITreeConsultationService {

	@Autowired
	private ITreeRepository treeRepository;
	
	@Override
	public NoeudDto getTreeOfLatestRevisionTree() {

		return new NoeudDto(treeRepository.getWholeTreeForRevision(1).get(0));
	}

}
