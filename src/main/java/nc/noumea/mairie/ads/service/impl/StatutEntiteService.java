package nc.noumea.mairie.ads.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeHistoEnum;
import nc.noumea.mairie.ads.dto.ChangeStatutDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.ads.service.IAccessRightsService;
import nc.noumea.mairie.ads.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ads.service.ISiservUpdateService;
import nc.noumea.mairie.ads.service.IStatutEntiteService;
import nc.noumea.mairie.ads.service.ITreeDataConsistencyService;
import nc.noumea.mairie.sirh.dto.EnumStatutFichePoste;
import nc.noumea.mairie.sirh.dto.FichePosteDto;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatutEntiteService implements IStatutEntiteService {

	private final Logger					logger												= LoggerFactory.getLogger(StatutEntiteService.class);

	@Autowired
	private IAdsRepository					adsRepository;

	@Autowired
	private ISirhWSConsumer					sirhWsConsumer;

	@Autowired
	private ISiservUpdateService			siservUpdateService;

	@Autowired
	private ITreeDataConsistencyService		dataConsistencyService;

	@Autowired
	private ITreeRepository					treeRepository;

	@Autowired
	private IAgentMatriculeConverterService	converterService;

	@Autowired
	private IAccessRightsService			accessRightsService;

	private final String					CHAMPS_NON_RENSEIGNES								= "Les champs ne sont pas correctement renseignés.";
	private final String					CHAMPS_OBLIGATOIRES									= "Les champs NFA, référence de délibération et date de délibération sont obligatoires.";
	private final String					ENTITE_MODIFIEE										= "L'entité est bien modifiée en statut ";
	private final String					DATE_INACTIF_POSTERIEURE_DATE_ACTIF_OBLIGATOIRES	= "La date de délibération pour la désactivation doit être postérieure à la date de délibération d'activation.";

	/**
	 * Service qui change le statut d une entite (et de ses entites filles optionnellement)
	 */
	@Override
	@Transactional(value = "chainedTransactionManager")
	public ReturnMessageDto changeStatutEntite(Integer idAgent, ChangeStatutDto dto, ReturnMessageDto result) {
		if (result == null)
			result = new ReturnMessageDto();

		// 17765
		// on verifie les droits de la personne
		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		result = accessRightsService.verifAccessRightEcriture(convertedIdAgent, result);
		if (!result.getErrors().isEmpty())
			return result;

		logger.debug("Debut changement de statut de l'entite : " + dto.getIdEntite());

		if (null == dto || null == dto.getIdEntite() || null == dto.getIdStatut()) {
			logger.debug(CHAMPS_NON_RENSEIGNES);
			result.getErrors().add(CHAMPS_NON_RENSEIGNES);
			return result;
		}

		Entite entite = adsRepository.get(Entite.class, dto.getIdEntite());

		// on check les donnees et toutes les RG
		result = checkDatasChangeStatutDto(result, dto, entite);

		if (!result.getErrors().isEmpty()) {
			loggeReturnMessageDto(result, dto.getIdEntite());
			return result;
		}

		// on modifie le statut de l entite et les donnees de deliberation
		mappingEntite(entite, dto);

		// on modifie dans SISERV si besoin
		result = createOrUpdateSiServ(result, dto, entite);

		if (!result.getErrors().isEmpty()) {
			loggeReturnMessageDto(result, dto.getIdEntite());
			return result;
		}

		// #16901
		// verifier l unicite des sigles
		// on recupere l arbre en entier
		Entite root = treeRepository.getWholeTree().get(0);
		result = dataConsistencyService.checkDataConsistencyForModifiedEntity(root, entite, result);
		if (!result.getErrors().isEmpty()) {
			adsRepository.clear();
			throw new ReturnMessageDtoException(result);
		}

		if (dto.getIdStatut().toString().equals(String.valueOf(StatutEntiteEnum.ACTIF.getIdRefStatutEntite()))) {
			// #16243 : les fiches de poste en statut "en création" qui sont
			// associées
			// à l'entité sont à passer en statut "validée"
			// si SIRH retourne une erreur (Fiche de Poste ne respectant pas
			// toutes les RG de validation d'une FDP)
			// on ne change pas le statut
			ReturnMessageDto resultSIRHWS = sirhWsConsumer.activeFichesPosteByIdEntite(entite.getIdEntite(), dto.getIdAgent());
			for (String err : resultSIRHWS.getErrors()) {
				result.getErrors().add(err);
			}
			for (String inf : resultSIRHWS.getInfos()) {
				result.getInfos().add(inf);
			}
			if (!result.getErrors().isEmpty()) {
				adsRepository.clear();
				throw new ReturnMessageDtoException(result);
			}
		}

		adsRepository.persistEntity(entite, new EntiteHisto(entite, dto.getIdAgent(), TypeHistoEnum.CHANGEMENT_STATUT));

		logger.debug(ENTITE_MODIFIEE + StatutEntiteEnum.getStatutEntiteEnum(dto.getIdStatut()).toString());
		result.getInfos().add(ENTITE_MODIFIEE + StatutEntiteEnum.getStatutEntiteEnum(dto.getIdStatut()).toString());

		return result;
	}

	protected void mappingEntite(Entite entite, ChangeStatutDto dto) {

		if (dto.getIdStatut().equals(StatutEntiteEnum.ACTIF.getIdRefStatutEntite())) {
			entite.setDateDeliberationActif(dto.getDateDeliberation());
			entite.setRefDeliberationActif(dto.getRefDeliberation());
			entite.setNfa(dto.getNfa());
			// #18543
			entite.setEntiteAs400(dto.isEntiteAS400());
		}
		if (dto.getIdStatut().equals(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite())) {
			if (null != dto.getDateDeliberation())
				entite.setDateDeliberationInactif(dto.getDateDeliberation());

			if (null != dto.getRefDeliberation() && !"".equals(dto.getRefDeliberation().trim()))
				entite.setRefDeliberationInactif(dto.getRefDeliberation());

			if (null != dto.getNfa() && !"".equals(dto.getNfa().trim()) && entite.getNfa() == null)
				entite.setNfa(dto.getNfa());
		}
		if (dto.getIdStatut().equals(StatutEntiteEnum.INACTIF.getIdRefStatutEntite())) {
			if (null != dto.getDateDeliberation())
				entite.setDateDeliberationInactif(dto.getDateDeliberation());

			if (null != dto.getRefDeliberation() && !"".equals(dto.getRefDeliberation().trim()))
				entite.setRefDeliberationInactif(dto.getRefDeliberation());
		}
		entite.setIdAgentModification(dto.getIdAgent());
		entite.setDateModification(new Date());

		entite.setStatut(StatutEntiteEnum.getStatutEntiteEnum(dto.getIdStatut()));

	}

	/**
	 * Cette methode verifie TOUTES les regles de gestion avant la modification du statut de l entite
	 * 
	 * @param result ReturnMessageDto
	 * @param dto ChangeStatutDto
	 * @param entite Entite
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto checkDatasChangeStatutDto(ReturnMessageDto result, ChangeStatutDto dto, Entite entite) {

		// l entite existe
		if (null == entite) {
			result.getErrors().add("L'entité n'existe pas.");
			return result;
		}

		// on check si le workflow des statuts est respecte
		if (StatutEntiteEnum.INACTIF.equals(entite.getStatut())) {
			result.getErrors().add("L'entité est inactive. Elle ne peut plus être modifiée.");
			return result;
		}

		if (dto.getIdStatut().equals(StatutEntiteEnum.ACTIF.getIdRefStatutEntite())) {
			checkDatasForNewStatutActif(result, dto, entite);
		}

		if (dto.getIdStatut().equals(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite())) {
			checkDatasForNewStatutTransitoire(result, dto, entite);
		}

		if (dto.getIdStatut().equals(StatutEntiteEnum.INACTIF.getIdRefStatutEntite())) {
			checkDatasForNewStatutInactif(result, dto, entite);
		}

		// #16888
		if (null != dto.getDateDeliberation()
				&& !dto.getDateDeliberation().before(new DateTime().plusDays(1).withHourOfDay(0).withMinuteOfHour(0).withMillisOfDay(0).toDate())) {
			result.getErrors().add("La date de délibération ne peut pas être postérieure à la date du jour.");
			return result;
		}

		return result;
	}

	/**
	 * "prévision" => "actif" RG #16541 : champs obligatoires : date de délibération et référence de délibération
	 * 
	 * @param result ReturnMessageDto
	 * @param dto ChangeStatutDto
	 * @param entite Entite
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto checkDatasForNewStatutActif(ReturnMessageDto result, ChangeStatutDto dto, Entite entite) {

		// le statut en cours est obligatoirement ACTIF
		if (!StatutEntiteEnum.PREVISION.equals(entite.getStatut()) && dto.getIdStatut().equals(StatutEntiteEnum.ACTIF.getIdRefStatutEntite())) {
			result.getErrors().add("Vous ne pouvez pas modifier l'entité en statut ACTIF, car elle n'est pas en statut PREVISION.");
			return result;
		}
		// #16876
		// le statut du parent est obligatoirement ACTIF
		if (!StatutEntiteEnum.ACTIF.equals(entite.getEntiteParent().getStatut()) && dto.getIdStatut().equals(StatutEntiteEnum.ACTIF.getIdRefStatutEntite())) {
			result.getErrors().add("Vous ne pouvez pas modifier l'entité en statut ACTIF, car elle son parent n'est pas en statut ACTIF.");
			return result;
		}

		// #17620 : NFA obligatoire
		if (null == dto.getRefDeliberation() || "".equals(dto.getRefDeliberation().trim()) || null == dto.getDateDeliberation() || null == dto.getNfa()
				|| "".equals(dto.getNfa().trim())) {
			result.getErrors().add(CHAMPS_OBLIGATOIRES);
		}

		return result;
	}

	/**
	 * "ACTIF" => "TRANSITOIRE" RG #16244 : Autorisé SI : - le noeud est feuille ou si tous les noeuds enfants sont déjà en statut "transitoire" ou "inactif"
	 * 
	 * - aucun impact sur les fiches de poste
	 * 
	 * @param result ReturnMessageDto
	 * @param dto ChangeStatutDto
	 * @param entite Entite
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto checkDatasForNewStatutTransitoire(ReturnMessageDto result, ChangeStatutDto dto, Entite entite) {

		// le statut en cours est obligatoirement ACTIF
		if (!StatutEntiteEnum.ACTIF.equals(entite.getStatut()) && dto.getIdStatut().equals(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite())) {
			result.getErrors().add("Vous ne pouvez pas modifier l'entité en statut TRANSITOIRE, car elle n'est pas en statut ACTIF.");
			return result;
		}

		// #17620 : NFA obligatoire
		if (entite.getNfa() == null && (null == dto.getNfa() || "".equals(dto.getNfa().trim()))) {
			result.getErrors().add(CHAMPS_OBLIGATOIRES);
			return result;
		}

		// - la transition n'est autorisée que si le noeud est feuille ou si
		// tous les noeuds enfants sont déjà en statut "transitoire" ou
		// "inactif"
		if (null != entite.getEntitesEnfants()) {
			for (Entite enfant : entite.getEntitesEnfants()) {
				result = recursiveCheckStatutEntitesEnfants(result, dto, enfant, Arrays.asList(StatutEntiteEnum.TRANSITOIRE, StatutEntiteEnum.INACTIF));

				if (!result.getErrors().isEmpty())
					return result;
			}
		}

		return result;
	}

	//
	/**
	 * "actif" => "inactif" RG #16315 : Autorisé SI : - La date de délibération et la date d'application du passage en inactif sont obligatoires. - s'il
	 * n'existe pas de FDP en statut "valide" ou "gelé" associée à l'entité ou l'une de ses sous-entités - à condition que tous les noeuds descendants soient
	 * inactifs
	 * 
	 * @param result ReturnMessageDto
	 * @param dto ChangeStatutDto
	 * @param entite Entite
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto checkDatasForNewStatutInactif(ReturnMessageDto result, ChangeStatutDto dto, Entite entite) {

		// le statut en cours est obligatoirement ACTIF
		if (StatutEntiteEnum.PREVISION.equals(entite.getStatut()) && dto.getIdStatut().equals(StatutEntiteEnum.INACTIF.getIdRefStatutEntite())) {
			result.getErrors().add("Vous ne pouvez pas modifier l'entité en statut INACTIF, car elle est en statut PREVISION.");
			return result;
		}

		// la date de délibération et la date d'application du passage en
		// inactif sont obligatoires
		if (((null == entite.getRefDeliberationInactif() || "".equals(entite.getRefDeliberationInactif().trim())) && (null == dto.getRefDeliberation() || ""
				.equals(dto.getRefDeliberation().trim()))) || (null == entite.getDateDeliberationInactif() && null == dto.getDateDeliberation())) {
			result.getErrors().add(CHAMPS_OBLIGATOIRES);
			return result;
		}

		// #17620 : NFA obligatoire
		if (entite.getNfa() == null && (null == dto.getNfa() || "".equals(dto.getNfa().trim()))) {
			result.getErrors().add(CHAMPS_OBLIGATOIRES);
			return result;
		}

		// #17397 date de désactivation doit être >= date d'activation
		if (null != dto.getDateDeliberation() && null != entite.getDateDeliberationActif()
				&& !dto.getDateDeliberation().after(entite.getDateDeliberationActif())) {
			result.getErrors().add(DATE_INACTIF_POSTERIEURE_DATE_ACTIF_OBLIGATOIRES);
			return result;
		}

		// on verifie qu'il n'existe pas de FDP en statut "valide" ou "gelé"
		// associée à l'entité ou l'une de ses sous-entités
		result = checkFichesPosteValideOuGeleeOuEnCreationAssocies(result, entite);

		// et que tous les enfants soient inactifs
		if (null != entite.getEntitesEnfants()) {
			for (Entite enfant : entite.getEntitesEnfants()) {
				result = recursiveCheckStatutEntitesEnfants(result, dto, enfant, Arrays.asList(StatutEntiteEnum.INACTIF));

				if (!result.getErrors().isEmpty())
					return result;
			}
		}
		return result;
	}

	/**
	 * Ce service verifie qu'il n'existe pas de Fiches de Poste en statut "valide" ou "gelé" associée à l'entité ou l'une de ses sous-entités
	 * 
	 * @param result ReturnMessageDto
	 * @param entite Entite
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto checkFichesPosteValideOuGeleeOuEnCreationAssocies(ReturnMessageDto result, Entite entite) {
		// s'il n'existe pas de FDP en statut "valide" ou "gelé" associée à
		// l'entité ou l'une de ses sous-entités
		List<FichePosteDto> listFichesPoste = sirhWsConsumer.getListFichesPosteByIdEntite(entite.getIdEntite(),
				Arrays.asList(EnumStatutFichePoste.VALIDEE.getId(), EnumStatutFichePoste.GELEE.getId(), EnumStatutFichePoste.EN_CREATION.getId()));

		if (null != listFichesPoste && !listFichesPoste.isEmpty()) {
			result.getErrors()
					.add("Vous ne pouvez pas désactiver l'entité, des fiches de postes en statut Valide ou Gelé sont associées à l'entité ou l'une de ses sous-entités.");
			return result;
		}

		for (Entite enfant : entite.getEntitesEnfants()) {
			result = checkFichesPosteValideOuGeleeOuEnCreationAssocies(result, enfant);

			if (!result.getErrors().isEmpty())
				return result;
		}

		return result;
	}

	/**
	 * Cette methode recursive verifie les statuts aurtorises pour les entites enfant
	 * 
	 * @param result ReturnMessageDto
	 * @param dto ChangeStatutDto
	 * @param entiteEnfant Entite
	 * @param listStatutsEntiteAutorises List<StatutEntiteEnum>
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto recursiveCheckStatutEntitesEnfants(ReturnMessageDto result, ChangeStatutDto dto, Entite entiteEnfant,
			List<StatutEntiteEnum> listStatutsEntiteAutorises) {

		if (!listStatutsEntiteAutorises.contains(entiteEnfant.getStatut())) {
			String error = "Vous ne pouvez pas modifier l'entité en statut " + StatutEntiteEnum.getStatutEntiteEnum(dto.getIdStatut()).toString()
					+ ", car une de ses entités fille n'est pas en statut ";
			for (StatutEntiteEnum statut : listStatutsEntiteAutorises) {
				error += statut.toString() + " ou ";
			}
			error = error.substring(0, error.length() - 4);

			result.getErrors().add(error);
			return result;
		}

		if (null != entiteEnfant.getEntitesEnfants()) {
			for (Entite enfant : entiteEnfant.getEntitesEnfants()) {
				result = recursiveCheckStatutEntitesEnfants(result, dto, enfant, listStatutsEntiteAutorises);

				if (!result.getErrors().isEmpty())
					return result;
			}
		}

		return result;
	}

	/**
	 * Ce service met a jour SISERV lorsque l entite passe de : - "transitoire" => "inactif" - "actif" => "inactif" - "prévision" => "actif"
	 * 
	 * @param result ReturnMessageDto
	 * @param dto ChangeStatutDto
	 * @param entite Entite
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto createOrUpdateSiServ(ReturnMessageDto result, ChangeStatutDto dto, Entite entite) {

		if (dto.getIdStatut().equals(StatutEntiteEnum.ACTIF.getIdRefStatutEntite())
				|| dto.getIdStatut().equals(StatutEntiteEnum.INACTIF.getIdRefStatutEntite())) {
			result = siservUpdateService.createOrDisableSiservByOneEntityOnly(entite, dto, result);
		}

		return result;
	}

	/**
	 * Logge les erreurs de ReturnMessageDto
	 * 
	 * @param result ReturnMessageDto
	 * @param idEntite Integer
	 */
	private void loggeReturnMessageDto(ReturnMessageDto result, Integer idEntite) {
		if (null != result && !result.getErrors().isEmpty()) {
			for (String error : result.getErrors()) {
				logger.debug("Erreur maj Statut Entite " + idEntite + " : " + error);
			}
		}
	}
}
