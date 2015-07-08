package nc.noumea.mairie.ads.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.dto.ChangeStatutDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.repository.ISirhRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.ads.service.ISiservUpdateService;
import nc.noumea.mairie.sirh.domain.SiservAds;
import nc.noumea.mairie.sirh.domain.SiservNw;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SiservUpdateService implements ISiservUpdateService {

	// Level max d'export dans SISERV (démarre à 0 pour la racine)
	private final Integer SISERV_MAX_DEPTH_LEVEL = 17;

	private Logger logger = LoggerFactory.getLogger(SiservUpdateService.class);

	@Autowired
	private ISirhRepository sirhRepository;

	@Autowired
	private ITreeRepository treeRepository;

	@Override
	@Transactional(value = "chainedTransactionManager", propagation = Propagation.REQUIRED)
	public void updateSiserv() {

		logger.info("Entered Update SISERVNW.");

		// Start by deleting all Siserv ADS entries in SIRH
		logger.info("Deleting all SISERV_ADS records from SIRH...");
		sirhRepository.deleteAllSiservAds();
		// Flush the modification to ensure no transaction issue
		sirhRepository.flush();

		logger.info("Retrieving nodes to export to SISERVNW and SISERV_ADS...");
		List<SiservNw> existingSiservNws = sirhRepository.getAllSiservNw();
		List<Entite> entites = treeRepository.getWholeTree();

		Map<String, SiservNw> siservNwByServi = new HashMap<>();

		for (SiservNw s : existingSiservNws)
			siservNwByServi.put(s.getServi(), s);

		Map<Integer, Integer> levelsByIdService = new HashMap<>();
		fillLevelsByIdServiceRecursive(entites.get(0), levelsByIdService, 0);

		for (Entite n : entites) {

			logger.debug("Exporting node id [{}] sigle [{}] ...", n.getIdEntite(),  n.getSigle());

			SiservAds siservAds = new SiservAds();
//			siservAds.setIdServiceParent(n.getEntiteParent() == null ? 0 : n.getEntiteParent().getIdEntite());

			SiservNw matchingSiservNw = siservNwByServi.get(n.getSiservInfo().getCodeServi());

			boolean updateSiserv = true;

			// If this level is too deep for SISERV
			if (levelsByIdService.get(n.getIdEntite()) > SISERV_MAX_DEPTH_LEVEL) {

				logger.debug("This node is too deep for SISERV (level > {}) ...", SISERV_MAX_DEPTH_LEVEL);

				Entite parentNodeInSiserv = n.getEntiteParent();
				// Search through parent nodes until a node of the max siserv level is found
				// this will be the siserv parent of the node
				while (levelsByIdService.get(parentNodeInSiserv.getIdEntite()) > SISERV_MAX_DEPTH_LEVEL)
					parentNodeInSiserv = parentNodeInSiserv.getEntiteParent();

				matchingSiservNw = siservNwByServi.get(parentNodeInSiserv.getSiservInfo().getCodeServi());
				// We will not update the Siserv for this node because it is a re attachement and not
				// the actual node linked to siserv
				updateSiserv = false;

				logger.debug("This node will be re attached to parent SISERVNW servi [{}] sigle [{}].", SISERV_MAX_DEPTH_LEVEL);
			}

			if (matchingSiservNw == null) {
				logger.debug("No SISERVNW already existing, creating new one ...");
				matchingSiservNw = new SiservNw();
				matchingSiservNw.setServi(n.getSiservInfo().getCodeServi());
				//TODO setServiOld()
				
				siservNwByServi.put(matchingSiservNw.getServi(), matchingSiservNw);
			}

			logger.debug("Linking node to SISERVNW servi [{}] sigle [{}].", matchingSiservNw.getServi(), matchingSiservNw.getServi());

			if (updateSiserv) {
				matchingSiservNw.setSigle(StringUtils.rightPad(n.getSigle(), 20));
				matchingSiservNw.setLiServ(StringUtils.rightPad(n.getLabel(), 60));
				String parentSigle = n.getEntiteParent() == null ? "" : n.getEntiteParent().getSigle();
				matchingSiservNw.setParentSigle(StringUtils.rightPad(parentSigle, 20));
//				matchingSiservNw.setCodeActif(n.isActif() ? " " : "I");

				logger.debug("After modification SISERVNW servi [{}] is : sigle [{}] label [{}] parentSigle [{}] actif [{}].",
						matchingSiservNw.getServi(), matchingSiservNw.getSigle(), matchingSiservNw.getLiServ(),
						matchingSiservNw.getParentSigle(), matchingSiservNw.getCodeActif());
			}

			matchingSiservNw.getSiservAds().add(siservAds);
			siservAds.setSiservNw(matchingSiservNw);

			// Removing the Siserv element from the initial list
			if (existingSiservNws.contains(matchingSiservNw))
				existingSiservNws.remove(matchingSiservNw);

			logger.debug("Saving SISERVNW and SISERV_ADS...");
			sirhRepository.persist(matchingSiservNw);
		}

		// For all the remaining siserv elements
		// that were removed from the revision
		// they need to be put as inactive in SISERV
		logger.info("Setting missing services of Revision as inactives in SISERVNW...");
		for (SiservNw siservNw : existingSiservNws) {
			logger.debug("Setting servi [{}] sigle [{}] as inactive.", siservNw.getServi(), siservNw.getSigle());
			if (!siservNw.getCodeActif().equals("I"))
				siservNw.setCodeActif("I");
		}

		logger.info("Update SISERVNW done.");
	}

	protected void fillLevelsByIdServiceRecursive(Entite entite, Map<Integer, Integer> levelsByIdService, int level) {

		levelsByIdService.put(entite.getIdEntite(), level);

		for (Entite ne : entite.getEntitesEnfants())
			fillLevelsByIdServiceRecursive(ne, levelsByIdService, level + 1);

	}
	
	@Override
	@Transactional(value = "chainedTransactionManager", propagation = Propagation.REQUIRED)
	public ReturnMessageDto updateSiservByOneEntityOnly(Entite entite, ChangeStatutDto changeStatutDto) {
		
		ReturnMessageDto result = new ReturnMessageDto();
		
		//TODO
		
		return result;
	}

}
