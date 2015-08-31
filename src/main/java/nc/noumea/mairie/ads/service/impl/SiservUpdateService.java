package nc.noumea.mairie.ads.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.dto.ChangeStatutDto;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.repository.IMairieRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.ads.service.ISiservUpdateService;
import nc.noumea.mairie.domain.Siserv;
import nc.noumea.mairie.domain.SiservNw;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SiservUpdateService implements ISiservUpdateService {

	private static String LIST_STATIC_CHARS = "BCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static Integer LONGUEUR_CODE_SERVI = 16;

	private Logger logger = LoggerFactory.getLogger(SiservUpdateService.class);

	@Autowired
	private IMairieRepository sirhRepository;

	@Autowired
	private ITreeRepository treeRepository;

	/**
	 * Cree un service actif ou desactive le service dans l AS400
	 */
	@Override
	@Transactional(value = "chainedTransactionManager", propagation = Propagation.REQUIRED)
	public ReturnMessageDto createOrDisableSiservByOneEntityOnly(Entite entite, ChangeStatutDto changeStatutDto,
			ReturnMessageDto result) {
		if (result == null)
			result = new ReturnMessageDto();

		// activation/ creation
		if (changeStatutDto.getIdStatut().equals(StatutEntiteEnum.ACTIF.getIdRefStatutEntite())) {
			try {
				// on cree le CODE_SERVI
				List<String> existingServiCodes = sirhRepository.getAllServiCodes();
				createCodeServiIfEmpty(entite, existingServiCodes);

				// on cree/modifie SISERVNW,
				// et on gere SISERV et SISERVHIERARCHIE si besoin
				createOrUpdateSiservNwForOneEntity(entite);
			} catch (ReturnMessageDtoException e) {
				throw e;
			} catch (Exception e) {
				logger.debug("Une erreur s'est produite lors de la création du service dans l'AS400.");
				logger.debug(e.getMessage());
				result.getErrors().add("Une erreur s'est produite lors de la création du service dans l'AS400.");
			}
		}

		// desactivation
		if (changeStatutDto.getIdStatut().equals(StatutEntiteEnum.INACTIF.getIdRefStatutEntite())) {
			try {
				// desactive SISERNW si le niveau de l entite est inferieur a 17
				// et desactive SISERV egalement si tous les enfant inactifs
				disableSiServNw(entite);
			} catch (Exception e) {
				logger.debug("Une erreur s'est produite lors de la création du service dans l'AS400.");
				logger.debug(e.getMessage());
				result.getErrors().add("Une erreur s'est produite lors de la création du service dans l'AS400.");
			}
		}

		return result;
	}

	/**
	 * Ce service cree ou met a jour le service dans SISERVNW correspondant a l
	 * entite : <br />
	 * - cree ou met a jour SISERVNW (avec SISERVNW.SERVI =
	 * ADS_SISERV_INFO.CODE_SERVI) <br />
	 * - cree/modifie une ligne dans SISERV si inexistant <br />
	 * - cree une ligne dans SISERVHIERARCHIE si inexistant <br />
	 * 
	 * @param entite
	 *            Entite
	 */
	protected void createOrUpdateSiservNwForOneEntity(Entite entite) {

		logger.info("Retrieving nodes to export to SISERVNW and SISERV_ADS...");

		// ////////////////////////////////////////////////////////////////////////////////////
		// ////////////////////// 1er ETAPE : on recupere tous les CODE_SERVI de
		// SISERVNW ////
		// ////////////////////////////////////////////////////////////////////////////////////

		// on recupere tous les CODE_SERVI de SISERVNW ///////
		List<SiservNw> existingSiservNws = sirhRepository.getAllSiservNw();

		Map<String, SiservNw> siservNwByServi = new HashMap<>();

		for (SiservNw s : existingSiservNws)
			siservNwByServi.put(s.getServi(), s);

		logger.debug("Exporting Entity id [{}] sigle [{}] ...", entite.getIdEntite(), entite.getSigle());

		// ////////////////////////////////////////////////////////////////////////////////
		// ////////////////////// 2e ETAPE : on cree/modifie SISERVNW ou on fait
		// rien /////
		// ////////////////////////////////////////////////////////////////////////////////

		// si l entite est ne possede pas de CODE_SERVI
		// c est que l on a depasse les 16 niveaux de la table SISERVNW
		// dans ce cas, on recherche l entite parent (ou grand(grand)parent)
		// ayant un code SISERVNW
		// on renseignera le CODE_SERVI de l entite (grand)parent dans la table
		// SISERV_INFO pour les correspondances
		SiservNw matchingSiservNw = null;

		if (null != entite.getSiservInfo().getCodeServi() && !"".equals(entite.getSiservInfo().getCodeServi().trim())) {
			matchingSiservNw = siservNwByServi.get(entite.getSiservInfo().getCodeServi());
		} else {
			logger.debug("This entity has no CODE_SERVI for SISERVNW so level > 16...");
			// dans le cas ou il n y a pas de code service alors on ne cree rien
			// dans l AS400
			// c est que le niveau de l entite est superieur a 16
			logger.debug("Update SISERVNW done.");
			ReturnMessageDto dto = new ReturnMessageDto();
			dto.getErrors()
					.add("L'entité n'a pas de code_servi AS400, car nous avons atteint la limite de 26 lettres.");
			throw new ReturnMessageDtoException(dto);
		}

		// matchingSiservNw == NULL, alors l entite n existe pas dans SISERVNW
		// et on la cree
		if (matchingSiservNw == null) {
			logger.debug("No SISERVNW already existing, creating new one ...");
			matchingSiservNw = new SiservNw();
			matchingSiservNw.setServi(entite.getSiservInfo().getCodeServi());

			siservNwByServi.put(matchingSiservNw.getServi(), matchingSiservNw);
		}

		logger.debug("Linking node to SISERVNW servi [{}] sigle [{}].", matchingSiservNw.getServi(),
				matchingSiservNw.getServi());

		matchingSiservNw.setSigle(StringUtils.rightPad(entite.getSigle(), 20));
		matchingSiservNw.setLiServ(StringUtils.rightPad(entite.getLabelCourt(), 60));
		String parentSigle = entite.getEntiteParent() == null ? "" : entite.getEntiteParent().getSigle();
		matchingSiservNw.setParentSigle(StringUtils.rightPad(parentSigle, 20));
		matchingSiservNw.setCodeActif(" ");
		// ne peut pas etre NULL
		matchingSiservNw.setLi22(StringUtils.rightPad("", 22));

		logger.debug("After modification SISERVNW servi [{}] is : sigle [{}] label [{}] parentSigle [{}] actif [{}].",
				matchingSiservNw.getServi(), matchingSiservNw.getSigle(), matchingSiservNw.getLiServ(),
				matchingSiservNw.getParentSigle(), matchingSiservNw.getCodeActif());

		// /////////////////////////////////////////////////////////////////////
		// ////////////////////// 3e ETAPE : SISERV ////////////////////////////
		// /////////////////////////////////////////////////////////////////////
		// DAAA = 1st level, DBAA = 2nd level, DBBA = 3rd level
		String code_servi = entite.getSiservInfo().getCodeServi();
		int level = getLevelofCodeServi(code_servi);

		if (level != -1 && level < 5) {
			Siserv siServ = matchingSiservNw.getSiServ();

			if (null == siServ) {
				siServ = new Siserv();
				siServ.setServi(code_servi.substring(0, 4));
			}

			siServ.setSigle(StringUtils.rightPad(entite.getSigle(), 20));
			siServ.setLiServ(StringUtils.rightPad(entite.getLabelCourt(), 60));
			siServ.setParentSigle(StringUtils.rightPad(parentSigle, 20));
			siServ.setCodeActif(" ");
			// ne peut pas etre NULL
			siServ.setLi22(StringUtils.rightPad("", 22));

			matchingSiservNw.setSiServ(siServ);

			sirhRepository.persist(siServ);

			logger.debug("Create or update SISERV");
		} else {
			Entite entiteParent = entite.getEntiteParent();
			if (null != entiteParent) {
				SiservNw siservNwParent = siservNwByServi.get(entiteParent.getSiservInfo().getCodeServi());
				matchingSiservNw.setSiServ(siservNwParent.getSiServ());
			}
		}

		// /////////////////////////////////////////////////////////////////////
		// ////////////////////// 3e ETAPE : SISERVHIERARCHIE //////////////////
		// /////////////////////////////////////////////////////////////////////
		if (null != matchingSiservNw.getSiservNwParent() && matchingSiservNw.getSiservNwParent().isEmpty()) {
			// on recupere SISERVNW parent
			Entite entiteParent = entite.getEntiteParent();
			if (null != entiteParent) {
				SiservNw siservNwParent = siservNwByServi.get(entiteParent.getSiservInfo().getCodeServi());
				// matchingSiservNw.getSiservNwParent().add(siservNwParent);
				siservNwParent.getSiservNwEnfant().add(matchingSiservNw);

				logger.debug("Create SISERVHIERACHIE");
			}
		}

		// /////////////////////////////////////////////////////////////////////
		// ////////////////////// SAUVEGARDE ///////////////////////////////////
		// /////////////////////////////////////////////////////////////////////

		logger.debug("Saving SISERVNW");

		sirhRepository.persist(matchingSiservNw);

		logger.info("Update SISERVNW done.");
	}

	/**
	 * Ce service desactive le service correspondant a l entite dans SISERVNW ET
	 * desactive egalement dans SISERV si et seulement si tous les services dans
	 * SISERVNW rattaches au service de SISERV sont inactifs
	 * 
	 * @param entite
	 *            Entite
	 */
	protected void disableSiServNw(Entite entite) {

		// si CODE_SERVI de ADS_SISERV_INFO est NULL, c'est que
		// sur plus de 16 niveaux (limite AS400) et donc absent de SISERVNW
		// => on ne fait rien
		if (null != entite.getSiservInfo().getCodeServi() && !"".equals(entite.getSiservInfo().getCodeServi().trim())) {

			// on recherche SISERVNW correspondant
			SiservNw siServNw = sirhRepository.getSiservNwByCode(entite.getSiservInfo().getCodeServi());
			// et on le desactive
			disableSiservNw(siServNw);

			// si tous les services enfant de SISERV sont desactives
			// alors on desactive egalement le service dans SISERV
			Siserv siServ = siServNw.getSiServ();
			if (null == siServ.getCodeActif() || !"I".equals(siServ.getCodeActif().trim())) {
				boolean isAllSiServNwDisable = true;
				for (SiservNw siservNwEnfant : siServ.getSiservNw()) {
					if (null == siservNwEnfant.getCodeActif() || !"I".equals(siservNwEnfant.getCodeActif().trim())) {
						isAllSiServNwDisable = false;
						break;
					}
				}
				if (isAllSiServNwDisable) {
					siServ.setCodeActif("I");
					sirhRepository.persist(siServ);
				}
			}
			sirhRepository.persist(siServNw);
		}
	}

	/**
	 * On desactive SiservNw passe en parametre (SISERVNW.CODACT = 'I')
	 * 
	 * @param siservNw
	 *            SiservNw
	 */
	private void disableSiservNw(SiservNw siservNw) {
		logger.debug("Setting servi [{}] sigle [{}] as inactive.", siservNw.getServi(), siservNw.getSigle());
		if (!siservNw.getCodeActif().equals("I"))
			siservNw.setCodeActif("I");
	}

	/**
	 * Genere un CODE_SERVI (AS400) si celui-ci est vide
	 * 
	 * @param entite
	 *            Entite L entite ayant besoin d un CODE_SERVI
	 * @param existingServiCodes
	 *            List<String> la liste des CODE_SERVI existants dans SISERVNW
	 *            (AS400)
	 */
	protected void createCodeServiIfEmpty(Entite entite, List<String> existingServiCodes) {

		// If no siserv info or if code servi is not empty, leave it as is
		if (entite.getSiservInfo() == null || !StringUtils.isBlank(entite.getSiservInfo().getCodeServi())) {
			return;
		}

		// if no parent entity, leave it (we can't guess root code servi)
		if (entite.getEntiteParent() == null)
			return;

		String codeParent = entite.getEntiteParent().getSiservInfo().getCodeServi();

		// If the parent entity doesnt have a codeServi, we cant do anything
		if (StringUtils.isBlank(codeParent))
			return;

		// DAAA = 1st level, DBAA = 2nd level, DBBA = 3rd level
		// /!\ attention a par exemple : DAAB ou DAGA
		int level = 0;
		if (!entite.isEntiteAs400()) {
			level = getLevelofCodeServi(codeParent);

			if (level == -1)
				return;

			if (level >= LONGUEUR_CODE_SERVI) {
				return;
			}
		}

		String newCode = codeParent.substring(0, level);
		String code = "";
		for (int i = 0; i < LIST_STATIC_CHARS.length(); i++) {
			code = newCode.concat(String.valueOf(LIST_STATIC_CHARS.charAt(i)));
			code = StringUtils.rightPad(code, LONGUEUR_CODE_SERVI, 'A');
			if (!existingServiCodes.contains(code))
				break;
			else
				code = "";
		}

		// We've found the code !!
		if (!StringUtils.isBlank(code)) {
			entite.getSiservInfo().setCodeServi(code);
			existingServiCodes.add(code);
		}
	}

	protected int getLevelofCodeServi(String codeParent) {
		// DAAA = 1st level, DBAA = 2nd level, DBBA = 3rd level
		// /!\ attention a par exemple : DAAB ou DAGA
		int firstA = codeParent.indexOf('A');

		if (firstA == -1)
			return -1;

		int level = firstA;

		for (int i = firstA; i < codeParent.length(); i++) {
			String charactere = codeParent.substring(i, i + 1);

			if (!charactere.equals("A")) {
				level = i + 1;
			}
		}

		return level;
	}

	/**
	 * Modifie un service actif uniquement dans l AS400
	 */
	@Override
	@Transactional(value = "chainedTransactionManager", propagation = Propagation.REQUIRED)
	public ReturnMessageDto updateSiservNwAndSiServ(Entite entite, EntiteDto entiteDto, ReturnMessageDto result) {
		if (result == null)
			result = new ReturnMessageDto();

		// on recupere SISERVNW
		SiservNw siServNw = sirhRepository.getSiservNwByCode(entite.getSiservInfo().getCodeServi());

		if (null == siServNw) {
			logger.debug("L'entité n'existe pas dans l'AS400.");
			result.getErrors().add("L'entité n'existe pas dans l'AS400.");
			return result;
		}

		if ("I".equals(siServNw.getCodeActif())) {
			logger.debug("Un service inactif ne peut pas être modifié dans l'AS400.");
			result.getErrors().add("Un service inactif ne peut pas être modifié dans l'AS400.");
			return result;
		}

		// on mappe
		// le code SISERV ne peut pas etre modifie
		if (null != entiteDto.getLabelCourt())
			siServNw.setLiServ(StringUtils.rightPad(entiteDto.getLabelCourt(), 60));

		// gestion du sigle pour modifier la colonne DEPEND des services enfant
		if (!siServNw.getSigle().equals(entiteDto.getSigle())) {
			siServNw.setSigle(StringUtils.rightPad(entiteDto.getSigle(), 20));

			if (null != siServNw.getSiservNwEnfant()) {
				for (SiservNw enfant : siServNw.getSiservNwEnfant()) {
					if (!enfant.getCodeActif().equals("I")) {
						enfant.setParentSigle(StringUtils.rightPad(entiteDto.getSigle(), 20));
					}
				}
			}
		}

		// on gere SISERV
		updateSiserv(siServNw);

		sirhRepository.persist(siServNw);

		return result;
	}

	/**
	 * Modifie SiServ si SiServNw correspond a un niveau inferieur ou egale a 4
	 * 
	 * @param siServNw
	 *            SiservNw
	 */
	protected void updateSiserv(SiservNw siServNw) {

		// si niveau < 4 (commence à 0)
		if (getLevelofCodeServi(siServNw.getServi()) < 5) {

			Siserv siserv = siServNw.getSiServ();

			if (null != siServNw.getLiServ())
				siserv.setLiServ(StringUtils.rightPad(siServNw.getLiServ(), 60));

			if (!siserv.getSigle().equals(siServNw.getSigle())) {

				List<Siserv> listSiServEnfants = sirhRepository.getSiservFromParentSigle(siserv.getSigle());

				siserv.setSigle(StringUtils.rightPad(siServNw.getSigle(), 20));

				if (null != listSiServEnfants) {
					for (Siserv enfant : listSiServEnfants) {
						if (!enfant.getCodeActif().equals("I")) {
							enfant.setParentSigle(StringUtils.rightPad(siServNw.getSigle(), 20));
						}
					}
				}
			}
		}
	}

}
