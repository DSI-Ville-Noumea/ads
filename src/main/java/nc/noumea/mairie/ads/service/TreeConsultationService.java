package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.repository.IRevisionRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TreeConsultationService implements ITreeConsultationService {

	@Autowired
	private ITreeRepository treeRepository;

	@Autowired
	private IRevisionRepository revisionRepository;

	@Override
	public NoeudDto getTreeOfLatestRevisionTree() {

		return new NoeudDto(getLatestRevisionRootNode());
	}

	/**
	 * Responsible for retrieving the latest revision of the tree and its root
	 * node only
	 *
	 * @return
	 */
	protected Noeud getLatestRevisionRootNode() {

		// Get latest revision
		Revision rev = revisionRepository.getLatestRevision();

		// Return root node associated with this revision
		return treeRepository.getWholeTreeForRevision(rev.getIdRevision()).get(0);
	}

	@Override
	public NoeudDto getTreeOfSpecificRevision(long idRevision) {

		List<Noeud> noeuds = treeRepository.getWholeTreeForRevision(idRevision);

		if (noeuds.isEmpty())
			return null;

		return new NoeudDto(noeuds.get(0));
	}
}
