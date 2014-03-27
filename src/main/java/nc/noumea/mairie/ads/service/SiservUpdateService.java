package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.repository.ISirhRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.sirh.domain.Siserv;
import nc.noumea.mairie.sirh.domain.SiservAds;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SiservUpdateService implements ISiservUpdateService {

	// Level max d'export dans SISERV (démarre à 0 pour la racine)
	private static Integer SISERV_MAX_DEPTH_LEVEL = 5;

	@Autowired
	private ISirhRepository sirhRepository;

	private ITreeRepository treeRepository;

	@Override
	public void updateSiserv(Revision revision) {
		//sirhRepository.deleteSiservAds();
		//sirhRepository.flush();

		List<Siserv> existingSiservs = sirhRepository.getAllSiserv();
		List<Noeud> noeuds = treeRepository.getWholeTreeForRevision(revision.getIdRevision());

		Map<String, Siserv> siservByServi = new HashMap<>();

		for (Siserv s : existingSiservs)
			siservByServi.put(s.getServi(), s);

		Map<Integer, Integer> levelsByIdService = new HashMap<>();
		fillLevelsByIdServiceRecursive(noeuds.get(0), levelsByIdService, 0);

		for (Noeud n : noeuds) {

			SiservAds siservAds = new SiservAds();
			siservAds.setIdService(n.getIdService());
			siservAds.setIdServiceParent(n.getNoeudParent() == null ? null : n.getNoeudParent().getIdService());

			Siserv matchingSiserv = siservByServi.get(n.getSiservInfo().getCodeServi());

			boolean updateSiserv = true;

			// If this level is too deep for SISERV
			if (levelsByIdService.get(n.getIdService()) > SISERV_MAX_DEPTH_LEVEL) {
				Noeud parentNodeInSiserv = n.getNoeudParent();
				// Search through parent nodes until a node of the max siserv level is found
				// this will be the siserv parent of the node
				while (levelsByIdService.get(parentNodeInSiserv.getIdService()) > SISERV_MAX_DEPTH_LEVEL)
					parentNodeInSiserv = parentNodeInSiserv.getNoeudParent();

				matchingSiserv = siservByServi.get(parentNodeInSiserv.getSiservInfo().getCodeServi());
				// We will not update the Siserv for this node because it is a re attachement and not
				// the actual node linked to siserv
				updateSiserv = false;
			}

			if (matchingSiserv == null) {
				matchingSiserv = new Siserv();
				matchingSiserv.setServi(n.getSiservInfo().getCodeServi());
				siservByServi.put(matchingSiserv.getServi(), matchingSiserv);
			}

			if (updateSiserv) {
				matchingSiserv.setSigle(StringUtils.rightPad(n.getSigle(), 20));
				matchingSiserv.setLiServ(StringUtils.rightPad(n.getLabel(), 60));
				String parentSigle = n.getNoeudParent() == null ? "" : n.getNoeudParent().getSigle();
				matchingSiserv.setParentSigle(StringUtils.rightPad(parentSigle, 20));
				matchingSiserv.setCodeActif(n.isActif() ? " " : "I");
			}

			matchingSiserv.getSiservAds().add(siservAds);
			siservAds.setSiserv(matchingSiserv);

			// Removing the Siserv element from the initial list
			if (existingSiservs.contains(matchingSiserv))
				existingSiservs.remove(matchingSiserv);

			sirhRepository.persist(matchingSiserv);
		}

		// For all the remaining siserv elements
		// that were removed from the revision
		// they need to be put as inactive in SISERV
		for (Siserv siserv : existingSiservs) {
			siserv.setCodeActif("I");
		}
	}

	protected void fillLevelsByIdServiceRecursive(Noeud noeud, Map<Integer,Integer> levelsByIdService, int level) {

		levelsByIdService.put(noeud.getIdService(), level);

		for (Noeud ne : noeud.getNoeudsEnfants())
			fillLevelsByIdServiceRecursive(ne, levelsByIdService, level + 1);

	}

}
