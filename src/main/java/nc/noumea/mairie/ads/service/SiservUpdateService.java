package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.repository.ISirhRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.sirh.domain.Siserv;
import nc.noumea.mairie.sirh.domain.SiservAds;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SiservUpdateService implements ISiservUpdateService {

	// Level max d'export dans SISERV (démarre à 0 pour la racine)
	private final Integer SISERV_MAX_DEPTH_LEVEL = 5;

	private Logger logger = LoggerFactory.getLogger(SiservUpdateService.class);

	@Autowired
	private ISirhRepository sirhRepository;

	@Autowired
	private ITreeRepository treeRepository;

	@Autowired
	private IRevisionService revisionService;

	@Override
	@Transactional(value = "chainedTransactionManager", propagation = Propagation.REQUIRED)
	public void updateSiserv(Revision revision) {

		logger.info("Entered Update SISERV.");

		// Start by deleting all Siserv ADS entries in SIRH
		logger.info("Deleting all SISERV_ADS records from SIRH...");
		sirhRepository.deleteAllSiservAds();
		// Flush the modification to ensure no transaction issue
		sirhRepository.flush();

		logger.info("Retrieving nodes to export to SISERV and SISERV_ADS...");
		List<Siserv> existingSiservs = sirhRepository.getAllSiserv();
		List<Noeud> noeuds = treeRepository.getWholeTreeForRevision(revision.getIdRevision());

		Map<String, Siserv> siservByServi = new HashMap<>();

		for (Siserv s : existingSiservs)
			siservByServi.put(s.getServi(), s);

		Map<Integer, Integer> levelsByIdService = new HashMap<>();
		fillLevelsByIdServiceRecursive(noeuds.get(0), levelsByIdService, 0);

		for (Noeud n : noeuds) {

			logger.debug("Exporting node id [{}] sigle [{}] ...", n.getIdService(),  n.getSigle());

			SiservAds siservAds = new SiservAds();
			siservAds.setIdService(n.getIdService());
			siservAds.setIdServiceParent(n.getNoeudParent() == null ? 0 : n.getNoeudParent().getIdService());

			Siserv matchingSiserv = siservByServi.get(n.getSiservInfo().getCodeServi());

			boolean updateSiserv = true;

			// If this level is too deep for SISERV
			if (levelsByIdService.get(n.getIdService()) > SISERV_MAX_DEPTH_LEVEL) {

				logger.debug("This node is too deep for SISERV (level > {}) ...", SISERV_MAX_DEPTH_LEVEL);

				Noeud parentNodeInSiserv = n.getNoeudParent();
				// Search through parent nodes until a node of the max siserv level is found
				// this will be the siserv parent of the node
				while (levelsByIdService.get(parentNodeInSiserv.getIdService()) > SISERV_MAX_DEPTH_LEVEL)
					parentNodeInSiserv = parentNodeInSiserv.getNoeudParent();

				matchingSiserv = siservByServi.get(parentNodeInSiserv.getSiservInfo().getCodeServi());
				// We will not update the Siserv for this node because it is a re attachement and not
				// the actual node linked to siserv
				updateSiserv = false;

				logger.debug("This node will be re attached to parent SISERV servi [{}] sigle [{}].", SISERV_MAX_DEPTH_LEVEL);
			}

			if (matchingSiserv == null) {
				logger.debug("No SISERV already existing, creating new one ...");
				matchingSiserv = new Siserv();
				matchingSiserv.setServi(n.getSiservInfo().getCodeServi());
				matchingSiserv.setLi22(StringUtils.rightPad(n.getSiservInfo().getLib22(), 22));
				siservByServi.put(matchingSiserv.getServi(), matchingSiserv);
			}

			logger.debug("Linking node to SISERV servi [{}] sigle [{}].", matchingSiserv.getServi(), matchingSiserv.getServi());

			if (updateSiserv) {
				matchingSiserv.setSigle(StringUtils.rightPad(n.getSigle(), 20));
				matchingSiserv.setLiServ(StringUtils.rightPad(n.getLabel(), 60));
				String parentSigle = n.getNoeudParent() == null ? "" : n.getNoeudParent().getSigle();
				matchingSiserv.setParentSigle(StringUtils.rightPad(parentSigle, 20));
				matchingSiserv.setLi22(StringUtils.rightPad(n.getSiservInfo().getLib22(), 22));
				matchingSiserv.setCodeActif(n.isActif() ? " " : "I");

				logger.debug("After modification SISERV servi [{}] is : sigle [{}] label [{}] parentSigle [{}] actif [{}].",
						matchingSiserv.getServi(), matchingSiserv.getSigle(), matchingSiserv.getLiServ(),
						matchingSiserv.getParentSigle(), matchingSiserv.getCodeActif());
			}

			matchingSiserv.getSiservAds().add(siservAds);
			siservAds.setSiserv(matchingSiserv);

			// Removing the Siserv element from the initial list
			if (existingSiservs.contains(matchingSiserv))
				existingSiservs.remove(matchingSiserv);

			logger.debug("Saving SISERV and SISERV_ADS...");
			sirhRepository.persist(matchingSiserv);
		}

		// For all the remaining siserv elements
		// that were removed from the revision
		// they need to be put as inactive in SISERV
		logger.info("Setting missing services of Revision as inactives in SISERV...");
		for (Siserv siserv : existingSiservs) {
			logger.debug("Setting servi [{}] sigle [{}] as inactive.", siserv.getServi(), siserv.getSigle());
			if (!siserv.getCodeActif().equals("I"))
				siserv.setCodeActif("I");
		}

		// Set the revision as exported
		revisionService.updateRevisionToExported(revision);

		logger.info("Update SISERV done.");
	}

	@PersistenceContext(unitName = "adsPersistenceUnit")
	private EntityManager adsEntityManager;

	protected void fillLevelsByIdServiceRecursive(Noeud noeud, Map<Integer, Integer> levelsByIdService, int level) {

		levelsByIdService.put(noeud.getIdService(), level);

		for (Noeud ne : noeud.getNoeudsEnfants())
			fillLevelsByIdServiceRecursive(ne, levelsByIdService, level + 1);

	}

}
