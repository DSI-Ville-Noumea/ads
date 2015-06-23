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

	@Override
	public NoeudDto getNodeByIdService(int idService) {

		// Get latest revision
		Revision rev = revisionRepository.getLatestRevision();

		Noeud result = treeRepository.getNoeudFromIdService(idService, rev.getIdRevision());

		if (result == null)
			return null;

		return new NoeudDto().mapNoeud(result);
	}

	@Override
	public NoeudDto getNodeByCodeService(String codeServi) {

		// Get latest revision
		Revision rev = revisionRepository.getLatestRevision();

		Noeud result = treeRepository.getNoeudFromCodeServi(codeServi, rev.getIdRevision());

		if (result == null)
			return null;

		return new NoeudDto().mapNoeud(result);
	}

	@Override
	public NoeudDto getNodeByIdServiceWithChildren(int idService) {

		// Get latest revision
		Revision rev = revisionRepository.getLatestRevision();

		Noeud result = treeRepository.getNoeudFromIdService(idService, rev.getIdRevision());

		if (result == null)
			return null;

		return new NoeudDto(result);
	}

	@Override
	public NoeudDto getNodeByCodeServiceWithChildren(String codeServi) {

		// Get latest revision
		Revision rev = revisionRepository.getLatestRevision();

		Noeud result = treeRepository.getNoeudFromCodeServi(codeServi, rev.getIdRevision());

		if (result == null)
			return null;

		return new NoeudDto(result);
	}

	@Override
	public NoeudDto getNodeBySigle(String sigle) {

		// Get latest revision
		Revision rev = revisionRepository.getLatestRevision();

		Noeud result = treeRepository.getNoeudFromSigle(sigle, rev.getIdRevision());

		if (result == null)
			return null;

		return new NoeudDto().mapNoeud(result);
	}
}
