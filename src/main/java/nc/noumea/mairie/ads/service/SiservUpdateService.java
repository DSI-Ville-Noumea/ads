package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.repository.ISirhRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.sirh.domain.Siserv;
import nc.noumea.mairie.sirh.domain.SiservAds;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SiservUpdateService implements ISiservUpdateService {

	@Autowired
	private ISirhRepository sirhRepository;

	private ITreeRepository treeRepository;

	@Override
	public void updateSiservWithRevision(Revision revision) {

		List<SiservAds> siservList = sirhRepository.getAllSiservAds();
		List<Noeud> noeuds = treeRepository.getWholeTreeForRevision(revision.getIdRevision());

		Map<Integer, Noeud> noeudsMap = new HashMap<>();
		fillMapWithNoeuds(noeuds.get(0), noeudsMap);

		for (SiservAds siservAds : siservList) {
			Noeud matchingNode = noeudsMap.get(siservAds.getIdService());

			// If this node is no longer present in the revision
			// mark it as inactive for SISERV users
			if (matchingNode == null) {
				siservAds.getSiserv().setCodeActif("I");
			}
			// otherwise, update its content with the latest modifications
			// of the revision
			else {
				siservAds.getSiserv().setLiServ(StringUtils.rightPad(matchingNode.getLabel(), 60));
				siservAds.getSiserv().setSigle(StringUtils.rightPad(matchingNode.getSigle(), 20));
				/*siservAds.setServi(StringUtils.rightPad(
						matchingNode.getSiservInfo() == null ? "" : matchingNode.getSiservInfo().getCodeServi(), 4));*/
				siservAds.getSiserv().setParentSigle(StringUtils.rightPad(matchingNode.getNoeudParent() == null ? "" : matchingNode.getNoeudParent().getSigle(), 20));
			}

			noeudsMap.remove(siservAds.getIdService());
		}

		// For all remaining nodes (that were not here before)
		// Add them to SISERV (create them)
		for (Noeud remainingNode : noeudsMap.values()) {
			Siserv newSiserv = new Siserv();
			newSiserv.setServi(StringUtils.rightPad(
					remainingNode.getSiservInfo() == null ? "" : remainingNode.getSiservInfo().getCodeServi(), 4));
			newSiserv.setSigle(StringUtils.rightPad(remainingNode.getSigle(), 20));
			newSiserv.setLiServ(StringUtils.rightPad(remainingNode.getLabel(), 60));
			newSiserv.setParentSigle(StringUtils.rightPad(remainingNode.getNoeudParent().getSigle(), 20));
			newSiserv.setCodeActif(" ");
			sirhRepository.persist(newSiserv);
		}

	}

	private void fillMapWithNoeuds(Noeud noeud, Map<Integer, Noeud> noeuds) {
		noeuds.put(noeud.getIdService(), noeud);

		for (Noeud e : noeud.getNoeudsEnfants())
			fillMapWithNoeuds(e, noeuds);
	}

}
