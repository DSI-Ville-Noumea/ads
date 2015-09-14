package nc.noumea.mairie.ads.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeEntite;
import nc.noumea.mairie.ads.domain.TypeHistoEnum;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.repository.IMairieRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.ads.service.IAccessRightsService;
import nc.noumea.mairie.ads.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.service.IHelperService;
import nc.noumea.mairie.ads.service.ISiservUpdateService;
import nc.noumea.mairie.ads.service.ITreeConsultationService;
import nc.noumea.mairie.ads.service.ITreeDataConsistencyService;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTreeService implements ICreateTreeService {

	@Autowired
	private ITreeRepository treeRepository;

	@Autowired
	private IAdsRepository adsRepository;

	@Autowired
	private IHelperService helperService;

	@Autowired
	private IMairieRepository sirhRepository;

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Autowired
	private ITreeDataConsistencyService dataConsistencyService;

	@Autowired
	private ISiservUpdateService siservUpdateService;

	@Autowired
	private ITreeConsultationService consultationService;

	@Autowired
	private IAgentMatriculeConverterService converterService;

	@Autowired
	private IAccessRightsService accessRightsService;

	protected Entite buildCoreEntites(EntiteDto entiteDto, Entite parent, List<String> existingServiCodes) {

		Entite newEntity = new Entite();
		newEntity.setLabel(entiteDto.getLabel());
		newEntity.setSigle(entiteDto.getSigle());
		newEntity.setTypeEntite(adsRepository.get(TypeEntite.class, entiteDto.getTypeEntite().getId()));

		if (parent != null)
			newEntity.addParent(parent);

		SiservInfo sisInfo = new SiservInfo();
		sisInfo.setCodeServi(entiteDto.getCodeServi() == null || entiteDto.getCodeServi().equals("") ? null : entiteDto.getCodeServi());
		sisInfo.addToEntite(newEntity);

		for (EntiteDto enfantDto : entiteDto.getEnfants()) {
			buildCoreEntites(enfantDto, newEntity, existingServiCodes);
		}

		return newEntity;
	}

	protected Entite buildCoreEntites(Entite entite) {

		Entite newEntity = new Entite();

		newEntity.setLabel(entite.getLabel());
		newEntity.setSigle(entite.getSigle());
		newEntity.setTypeEntite(entite.getTypeEntite());

		SiservInfo sisInfo = new SiservInfo();
		sisInfo.setCodeServi(entite.getSiservInfo().getCodeServi());
		sisInfo.addToEntite(newEntity);

		for (Entite e : entite.getEntitesEnfants()) {
			Entite enfant = buildCoreEntites(e);
			enfant.addParent(newEntity);
		}

		return newEntity;
	}

	/**
	 * Creation d une entite
	 * 
	 * #16255 : il est convenu pour le moment de creer entite par entite et non
	 * en cascade avec les entites enfant
	 * 
	 * @param entiteDto
	 *            EntiteDto
	 * @return ReturnMessageDto
	 */
	@Override
	@Transactional(value = "adsTransactionManager")
	public ReturnMessageDto createEntity(Integer idAgent, EntiteDto entiteDto, TypeHistoEnum typeHisto, ReturnMessageDto result, boolean isDuplication, boolean withDelibActif) {
		if (result == null)
			result = new ReturnMessageDto();

		// 17765
		// on verifie les droits de la personne
		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		result = accessRightsService.verifAccessRightEcriture(convertedIdAgent, result);
		if (!result.getErrors().isEmpty())
			return result;

		// statut PREVISION OBLIGATOIRE
		entiteDto.setIdStatut(StatutEntiteEnum.PREVISION.getIdRefStatutEntite());

		result = checkDataToCreateEntity(entiteDto, result);

		if (!result.getErrors().isEmpty())
			return result;

		List<String> existingServiCodes = sirhRepository.getAllServiCodes();
		Entite entiteParent = adsRepository.get(Entite.class, entiteDto.getEntiteParent().getIdEntite());

		result = checkEntiteParentWithCodeAS400Alphanumerique(entiteParent, result);

		if (!result.getErrors().isEmpty())
			return result;

		Entite entite = buildCoreEntites(entiteDto, entiteParent, existingServiCodes, false, withDelibActif);

		return saveNewEntityAndReturnMessages(entite, entiteDto.getIdAgentCreation(), typeHisto, result, isDuplication);
	}

	/**
	 * Modification d une entite
	 * 
	 * @param entiteDto
	 *            EntiteDto
	 * @return ReturnMessageDto
	 */
	@Override
	@Transactional(value = "chainedTransactionManager")
	public ReturnMessageDto modifyEntity(Integer idAgent, EntiteDto entiteDto, ReturnMessageDto result) {

		if (result == null)
			result = new ReturnMessageDto();

		// 17765
		// on verifie les droits de la personne
		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		result = accessRightsService.verifAccessRightEcriture(convertedIdAgent, result);
		if (!result.getErrors().isEmpty())
			return result;

		Entite entite = adsRepository.get(Entite.class, entiteDto.getIdEntite());

		if (null == entite) {
			result.getErrors().add("L'entité n'existe pas.");
			return result;
		}

		// on modifie dans SISERV si besoin
		result = createOrUpdateSiServ(result, entiteDto, entite);

		if (!result.getErrors().isEmpty()) {
			adsRepository.clear();
			throw new ReturnMessageDtoException(result);
		}

		List<String> existingServiCodes = sirhRepository.getAllServiCodes();
		entite = modifyCoreEntites(entiteDto, entite, existingServiCodes);

		return saveModifiedEntityAndReturnMessages(entite, entiteDto.getIdAgentModification());
	}

	/**
	 * Ce service met a jour SISERVNW et SISERV lorsque l on modifie l entite On
	 * ne peut pas modifier une entite inactive, et une entite EN PREVISION
	 * n'existe pas dans l'AS400
	 * 
	 * @param result
	 *            ReturnMessageDto
	 * @param dto
	 *            EntiteDto
	 * @param entite
	 *            Entite
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto createOrUpdateSiServ(ReturnMessageDto result, EntiteDto dto, Entite entite) {

		if (entite.getStatut().equals(StatutEntiteEnum.ACTIF) || entite.getStatut().equals(StatutEntiteEnum.TRANSITOIRE)) {
			result = siservUpdateService.updateSiservNwAndSiServ(entite, dto, result);
		}

		return result;
	}

	/**
	 * #16255 : RG a checker
	 * 
	 * @param entiteDto
	 *            EntiteDto
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto checkRequiredData(EntiteDto entiteDto, ReturnMessageDto result) {

		if (result == null)
			result = new ReturnMessageDto();

		// champ obligatoire parent + sigle + libellé
		if (null == entiteDto.getSigle() || "".equals(entiteDto.getSigle().trim())) {
			result.getErrors().add("Le sigle est obligatoire.");
		}

		if (null == entiteDto.getLabel() || "".equals(entiteDto.getLabel().trim())) {
			result.getErrors().add("Le libellé est obligatoire.");
		}

		if (null == entiteDto.getEntiteParent() || null == entiteDto.getEntiteParent().getIdEntite() || 0 == entiteDto.getEntiteParent().getIdEntite()) {
			result.getErrors().add("L'entité parente est obligatoire.");
		}

		return result;
	}

	/**
	 * #16255 : RG a checker
	 * 
	 * @param entiteDto
	 *            EntiteDto
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto checkDataToCreateEntity(EntiteDto entiteDto, ReturnMessageDto result) {

		if (result == null)
			result = new ReturnMessageDto();

		result = checkRequiredData(entiteDto, result);

		if (!result.getErrors().isEmpty())
			return result;

		// le parent doit etre a P ou A
		Entite entiteParent = adsRepository.get(Entite.class, entiteDto.getEntiteParent().getIdEntite());
		if (!(StatutEntiteEnum.PREVISION.getIdRefStatutEntite() == entiteParent.getStatut().getIdRefStatutEntite() || StatutEntiteEnum.ACTIF.equals(entiteParent.getStatut()))) {
			result.getErrors().add("Le statut de l'entité parente n'est ni active ni en prévision.");
		}

		// l entite remplacee ne peut pas etre en PREVISION
		if (null != entiteDto.getEntiteRemplacee() && null != entiteDto.getEntiteRemplacee().getIdEntite() && 0 != entiteDto.getEntiteRemplacee().getIdEntite()) {
			Entite entiteRemplacee = adsRepository.get(Entite.class, entiteDto.getEntiteRemplacee().getIdEntite());
			if (StatutEntiteEnum.PREVISION.equals(entiteRemplacee.getStatut())) {
				result.getErrors().add("Une entité au statut en prévision ne peut pas être remplacée.");
			}
		}

		return result;
	}

	/**
	 * La modification se fait uniquement sur une seule entite
	 * 
	 * @param entiteDto
	 *            EntiteDto
	 * @param entite
	 *            Entite
	 * @param existingServiCodes
	 *            List<String>
	 * 
	 * @return l entite modifiee Entite
	 */
	protected Entite modifyCoreEntites(EntiteDto entiteDto, Entite entite, List<String> existingServiCodes) {
		// si on est en prevision aalors on map toutes les données
		// sinon on ne map que le commentaire
		if (StatutEntiteEnum.PREVISION.equals(entite.getStatut())) {
			// on mappe les donnees communes avec la creation
			mappingData(entiteDto, entite, false);
		} else {
			entite.setCommentaire(entiteDto.getCommentaire());
		}
		// ces champs sont specifiques a la modification
		entite.setIdAgentModification(entiteDto.getIdAgentModification());
		entite.setDateModification(new Date());

		return entite;
	}

	/**
	 * Ce mapping est utile en creation ET modification
	 * 
	 * @param entiteDto
	 * @param entite
	 */
	protected void mappingData(EntiteDto entiteDto, Entite entite, boolean withDelibActif) {

		// modif + creation
		entite.setLabel(entiteDto.getLabel());
		entite.setLabelCourt(entiteDto.getLabelCourt());
		entite.setSigle(entiteDto.getSigle());
		if (withDelibActif) {
			entite.setDateDeliberationActif(entite.getDateDeliberationActif());
			entite.setRefDeliberationActif(entite.getRefDeliberationActif());
		} else {
			entite.setDateDeliberationActif(entiteDto.getDateDeliberationActif());
			entite.setRefDeliberationActif(entiteDto.getRefDeliberationActif());
		}
		entite.setDateDeliberationInactif(entiteDto.getDateDeliberationInactif());
		entite.setRefDeliberationInactif(entiteDto.getRefDeliberationInactif());
		entite.setCommentaire(entiteDto.getCommentaire());
		entite.setNfa(entiteDto.getNfa());

		if (null != entiteDto.getTypeEntite() && null != entiteDto.getTypeEntite().getId()) {
			entite.setTypeEntite(adsRepository.get(TypeEntite.class, entiteDto.getTypeEntite().getId()));
		}

		if (null != entiteDto.getEntiteRemplacee() && null != entiteDto.getEntiteRemplacee().getIdEntite()) {
			entite.setEntiteRemplacee(adsRepository.get(Entite.class, entiteDto.getEntiteRemplacee().getIdEntite()));
		} else {
			entite.setEntiteRemplacee(null);
		}
	}

	protected Entite buildCoreEntites(EntiteDto entiteDto, Entite parent, List<String> existingServiCodes, boolean withChildren, boolean withDelibActif) {

		Entite newEntity = new Entite();

		// on mappe les donnees communes avec la modification
		mappingData(entiteDto, newEntity, withDelibActif);

		// ces champs sont specifiques a la creation
		newEntity.setEntiteParent(parent);
		// l agent qui cree
		newEntity.setIdAgentCreation(entiteDto.getIdAgentCreation());
		newEntity.setDateCreation(new Date());
		// le statut de l entite
		newEntity.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getIdStatut()));

		if (parent != null)
			newEntity.addParent(parent);

		SiservInfo sisInfo = new SiservInfo();
		sisInfo.setCodeServi(entiteDto.getCodeServi() == null || entiteDto.getCodeServi().equals("") ? null : entiteDto.getCodeServi());
		sisInfo.addToEntite(newEntity);

		if (withChildren) {
			for (EntiteDto enfantDto : entiteDto.getEnfants()) {
				buildCoreEntites(enfantDto, newEntity, existingServiCodes, withChildren, withDelibActif);
			}
		}

		return newEntity;
	}

	protected ReturnMessageDto saveNewEntityAndReturnMessages(Entite entite, Integer idAgentHisto, TypeHistoEnum typeHisto, ReturnMessageDto result, boolean isDuplication) {
		// on recupere l arbre en entier
		Entite root = treeRepository.getWholeTree().get(0);
		// pour ensuite verifier les donnees de la nouvelle entite avec l arbre
		ReturnMessageDto errorMessages = dataConsistencyService.checkDataConsistencyForNewEntity(root, entite, result, isDuplication);

		if (errorMessages.getErrors().isEmpty()) {
			adsRepository.persistEntity(entite, new EntiteHisto(entite, idAgentHisto, typeHisto));
			if (result == null) {
				result = new ReturnMessageDto();
			}
			String message = "L'entité est bien créée.";
			if (!result.getInfos().contains(message))
				result.getInfos().add(message);

			if (!errorMessages.getInfos().isEmpty()) {
				for (String inf : errorMessages.getInfos()) {
					if (!result.getInfos().contains(inf))
						result.getInfos().add(inf);
				}
			}
			result.setId(entite.getIdEntite());
		} else {
			adsRepository.clear();
			throw new ReturnMessageDtoException(errorMessages);
		}

		return result;
	}

	protected ReturnMessageDto saveModifiedEntityAndReturnMessages(Entite entite, Integer idAgentHisto) {

		ReturnMessageDto result = null;
		// on recupere l arbre en entier
		Entite root = treeRepository.getWholeTree().get(0);
		// pour ensuite verifier les donnees de la nouvelle entite avec l arbre
		ReturnMessageDto errorMessages = dataConsistencyService.checkDataConsistencyForModifiedEntity(root, entite, result);

		if (errorMessages.getErrors().isEmpty()) {
			adsRepository.persistEntity(entite, new EntiteHisto(entite, idAgentHisto, TypeHistoEnum.MODIFICATION));
			result = new ReturnMessageDto();
			result.getInfos().add("L'entité est bien modifiée.");
			result.setId(entite.getIdEntite());
		} else {
			adsRepository.clear();
			throw new ReturnMessageDtoException(errorMessages);
		}

		return result;
	}

	/**
	 * Supprime d une entite en statut Provisoire uniquement et ses enfants si
	 * parametre à TRUE en statut Provisoire uniquement
	 * 
	 * #16230 : RG
	 * 
	 * @param idEntite
	 *            Integer
	 * @return ReturnMessageDto
	 */
	@Override
	@Transactional(value = "adsTransactionManager")
	public ReturnMessageDto deleteEntity(Integer idEntite, Integer idAgent, ReturnMessageDto result, boolean withChildren) {

		if (result == null)
			result = new ReturnMessageDto();

		// 17765
		// on verifie les droits de la personne
		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		result = accessRightsService.verifAccessRightEcriture(convertedIdAgent, result);
		if (!result.getErrors().isEmpty())
			return result;

		Entite entite = adsRepository.get(Entite.class, idEntite);
		result = checkDataToDeleteEntity(entite, result, withChildren);

		if (!result.getErrors().isEmpty())
			return result;

		// RG3 : les fiches de poste en statut "en création" qui sont associées
		// à l'entité sont également supprimées
		// si SIRH retourne une erreur (Fiche de Poste dans un autre statut que
		// En Création)
		// on ne supprime pas
		ReturnMessageDto resultSIRHWS = sirhWsConsumer.deleteFichesPosteByIdEntite(entite.getIdEntite(), idAgent, entite.getSigle());
		for (String err : resultSIRHWS.getErrors()) {
			result.getErrors().add(err);
		}
		for (String inf : resultSIRHWS.getInfos()) {
			result.getInfos().add(inf);
		}

		if (withChildren) {
			deleteFichesPosteOfEntityRecursive(entite, result, withChildren, idAgent);
		}

		if (!result.getErrors().isEmpty())
			return result;

		adsRepository.removeEntiteAvecPersistHisto(entite, new EntiteHisto(entite, idAgent, TypeHistoEnum.SUPPRESSION));
		result.getInfos().add("L'entité est bien supprimée.");

		return result;
	}

	private void deleteFichesPosteOfEntityRecursive(Entite entite, ReturnMessageDto result, boolean withChildren, Integer idAgent) {

		if (withChildren) {

			if (null != entite.getEntitesEnfants()) {
				for (Entite enfant : entite.getEntitesEnfants()) {

					ReturnMessageDto resultSIRHWS = sirhWsConsumer.deleteFichesPosteByIdEntite(enfant.getIdEntite(), idAgent, enfant.getSigle());
					for (String err : resultSIRHWS.getErrors()) {
						result.getErrors().add(err);
					}
					for (String inf : resultSIRHWS.getInfos()) {
						result.getInfos().add(inf);
					}

					deleteFichesPosteOfEntityRecursive(enfant, result, withChildren, idAgent);
				}
			}
		}
	}

	protected ReturnMessageDto checkDataToDeleteEntity(Entite entite, ReturnMessageDto result, boolean withChildren) {

		if (result == null)
			result = new ReturnMessageDto();

		if (null == entite) {
			result.getErrors().add("L'entité n'existe pas.");
			return result;
		}

		// RG1 : l'entité ne doit pas avoir d'entité fille autre que en
		// PREVISION
		if ((null == entite.getEntitesEnfants() || !entite.getEntitesEnfants().isEmpty()) && !withChildren) {
			result.getErrors().add("L'entité ne peut être supprimée, car elle a un ou des entités fille.");
			return result;
		}

		// on verifie que l entite et les enfants sont tous en PREVISION
		checkStatutOfEntityAndTheirsChildren(entite, result, Arrays.asList(StatutEntiteEnum.PREVISION), withChildren);

		return result;
	}

	/**
	 * Duplique une entite sans fiche de poste. Le parametre withChildren permet
	 * de dupliquer les entites enfant en meme temps.
	 * 
	 * @param entiteDto
	 *            EntiteDto
	 * @param result
	 *            ReturnMessageDto
	 * @param withChildren
	 *            boolean
	 * @return ReturnMessageDto
	 */
	@Override
	@Transactional(value = "adsTransactionManager")
	public ReturnMessageDto duplicateEntity(Integer idAgent, EntiteDto entiteDto, ReturnMessageDto result, boolean withChildren, boolean withDelibActif) {
		// 17765
		// on verifie les droits de la personne
		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		result = accessRightsService.verifAccessRightEcriture(convertedIdAgent, result);
		if (!result.getErrors().isEmpty())
			return result;

		if (withChildren) {
			return duplicateEntityWithChildren(entiteDto, result, withDelibActif);
		} else {
			return duplicateEntity(idAgent, entiteDto, result, withDelibActif);
		}
	}

	/**
	 * Duplique une entite avec fiches de poste. Le parametre withChildren
	 * permet de dupliquer les entites enfant en meme temps.
	 * 
	 * @param entiteDto
	 *            EntiteDto
	 * @param result
	 *            ReturnMessageDto
	 * @param withChildren
	 *            boolean
	 * @return ReturnMessageDto
	 */
	@Override
	@Transactional(readOnly = true)
	public ReturnMessageDto duplicateFichesPosteOfEntity(Integer idAgent, EntiteDto entiteDto, ReturnMessageDto result, boolean withChildren, boolean withFDP) {
		if (withFDP) {
			if (withChildren) {
				// 3e temps, on cree les task job pour la duplication des fiches
				// de
				// poste
				// on recupere l entite avec tous ses enfants nouvellement crees
				// afin d avoir les nouveaux id_entite et les anciens
				// (remplaces)
				Entite newEntiteRoot = treeRepository.getEntiteFromIdEntite(result.getListIds().get(0));
				result = dupliqueFichesPosteRecursive(newEntiteRoot, result);
			} else {
				// RG : les fiches de poste en statut "validées" qui sont
				// associées
				// à l'entité sont dupliquées en statut "en creation" sur le
				// nouveau
				// service
				// si SIRH retourne une erreur, c'est que l'insertion en BD du
				// job n'a
				// pas fonctionné
				Entite newEntiteRoot = treeRepository.getEntiteFromIdEntite(result.getId());
				ReturnMessageDto resultSIRHWS = sirhWsConsumer.dupliqueFichesPosteByIdEntite(result.getId(), newEntiteRoot.getEntiteRemplacee().getIdEntite(), entiteDto.getIdAgentCreation());
				for (String err : resultSIRHWS.getErrors()) {
					result.getErrors().add(err);
				}
				for (String inf : resultSIRHWS.getInfos()) {
					result.getInfos().add(inf);
				}
			}
		}

		return result;
	}

	/**
	 * Duplique une entite uniquement.
	 * 
	 * @param entiteDto
	 *            EntiteDto
	 * @param result
	 *            ReturnMessageDto
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto duplicateEntity(Integer idAgent, EntiteDto entiteDto, ReturnMessageDto result, boolean withDelibActif) {
		if (result == null)
			result = new ReturnMessageDto();

		// on verifie que entiteDto est "actif" ou transitoire"
		EntiteDto entiteCheck = consultationService.getEntityByIdEntite(entiteDto.getIdEntite());

		result = checkStatutDuplicateEntite(entiteCheck, result);
		if (!result.getErrors().isEmpty()) {
			return result;
		}

		// on remanie de DTO pour sa creation
		entiteDto.setIdEntite(null);
		entiteDto.setCodeServi(null);

		result = createEntity(idAgent, entiteDto, TypeHistoEnum.CREATION_DUPLICATION, result, true, withDelibActif);
		if (result.getErrors().size() > 0) {
			return result;
		}

		return result;
	}

	/**
	 * Duplique une entite avec toutes ses entites fille.
	 * 
	 * @param entiteDto
	 *            EntiteDto
	 * @param result
	 *            ReturnMessageDto
	 * @param withDelibActif
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto duplicateEntityWithChildren(EntiteDto entiteDto, ReturnMessageDto result, boolean withDelibActif) {

		if (result == null)
			result = new ReturnMessageDto();

		// on recupere l entite avec son sous arbre
		EntiteDto entityWithChildrenToDuplicate = consultationService.getEntityByIdEntiteWithChildren(entiteDto.getIdEntite());

		// 1er temps, on verifie les statuts de l entite sans ses enfants
		result = checkStatutDuplicateEntite(entityWithChildrenToDuplicate, result);
		if (!result.getErrors().isEmpty()) {
			// on throw une RuntimeException pour le rollback
			return result;
		}

		// #18143
		// 2e temps, on filtre l'arbre pour retirer les entites en PREVISION ET
		// INACTIVE
		filtreArbreEntite(entityWithChildrenToDuplicate, Arrays.asList(StatutEntiteEnum.ACTIF.getIdRefStatutEntite(), StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite()));

		// 3e temps, on duplique les entites
		entityWithChildrenToDuplicate.setEntiteParent(entiteDto.getEntiteParent());
		entityWithChildrenToDuplicate.setEntiteRemplacee(entiteDto.getEntiteRemplacee());
		entityWithChildrenToDuplicate.getEntiteRemplacee().setIdEntite(entiteDto.getIdEntite());

		result = createEntityRecursive(entityWithChildrenToDuplicate, result, TypeHistoEnum.CREATION_DUPLICATION, entiteDto.getIdAgentCreation(), null, true, withDelibActif);
		if (!result.getErrors().isEmpty()) {
			// on throw une RuntimeException pour le rollback
			throw new ReturnMessageDtoException(result);
		}

		return result;
	}

	/**
	 * Duplication des fiches de postes associées à l entite et toutes ses
	 * entites fille.
	 * 
	 * RG : les fiches de poste en statut "validées" qui sont associées à
	 * l'entité sont dupliquées en statut "en creation" sur le nouveau service
	 * si SIRH retourne une erreur, c'est que l'insertion en BD du job n'a pas
	 * fonctionné
	 * 
	 * @param entiteDto
	 *            EntiteDto
	 * @param result
	 *            ReturnMessageDto
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto dupliqueFichesPosteRecursive(Entite entite, ReturnMessageDto result) {

		ReturnMessageDto resultSIRHWS = sirhWsConsumer.dupliqueFichesPosteByIdEntite(entite.getIdEntite(), entite.getEntiteRemplacee().getIdEntite(), entite.getIdAgentCreation());

		for (String err : resultSIRHWS.getErrors()) {
			result.getErrors().add(err);
		}
		for (String inf : resultSIRHWS.getInfos()) {
			result.getInfos().add(inf);
		}

		if (null != entite.getEntitesEnfants()) {
			for (Entite enfant : entite.getEntitesEnfants()) {
				dupliqueFichesPosteRecursive(enfant, result);
			}
		}

		return result;
	}

	/**
	 * Creer de maniere recursive toute une branche d entites
	 * 
	 * @param entiteDto
	 *            EntiteDto
	 * @param result
	 *            ReturnMessageDto
	 * @param typeHisto
	 *            TypeHistoEnum
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto createEntityRecursive(EntiteDto entiteDto, ReturnMessageDto result, TypeHistoEnum typeHisto, Integer idAgentCreation, Integer idParent, boolean isDuplication,
			boolean withDelibActif) {

		// on remanie de DTO pour sa creation
		// entiteDto.setIdEntite(null);
		entiteDto.setCodeServi(null);
		entiteDto.setIdAgentCreation(idAgentCreation);
		entiteDto.setEntiteRemplacee(entiteDto);
		entiteDto.setDateDeliberationActif(null);
		entiteDto.setRefDeliberationActif(null);
		entiteDto.setDateDeliberationInactif(null);
		entiteDto.setRefDeliberationInactif(null);

		result = createEntity(idAgentCreation, entiteDto, typeHisto, result, isDuplication, withDelibActif);
		if (!result.getErrors().isEmpty()) {
			return result;
		}

		if (idParent == null) {
			result.getListIds().add(result.getId());
		}
		Integer idEntiteParent = result.getId();

		if (null != entiteDto.getEnfants()) {
			for (EntiteDto enfant : entiteDto.getEnfants()) {
				enfant.getEntiteParent().setIdEntite(idEntiteParent);
				result = createEntityRecursive(enfant, result, typeHisto, idAgentCreation, idEntiteParent, isDuplication, withDelibActif);
				if (!result.getErrors().isEmpty()) {
					return result;
				}
			}
		}

		return result;
	}

	/**
	 * Check les statuts de toutes les entites d une branche pour la
	 * duplication.
	 * 
	 * @param entite
	 *            EntiteDto
	 * @param result
	 *            ReturnMessageDto
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto checkStatutDuplicateEntite(EntiteDto entite, ReturnMessageDto result) {

		// on verifie que entiteDto est "actif" ou transitoire"
		if (!entite.getIdStatut().equals(StatutEntiteEnum.ACTIF.getIdRefStatutEntite()) && !entite.getIdStatut().equals(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite())) {
			result.getErrors().add("Le statut de l'entité " + entite.getSigle() + " n'est ni active ni transitoire.");
			return result;
		}

		// #18143 on ne check plus les entites enfant
		// if (null != entite.getEnfants()) {
		// for (EntiteDto enfant : entite.getEnfants()) {
		// result = checkRecursiveStatutDuplicateEntite(enfant, result);
		// if (!result.getErrors().isEmpty()) {
		// return result;
		// }
		// }
		// }

		return result;
	}

	protected ReturnMessageDto checkEntiteParentWithCodeAS400Alphanumerique(Entite entiteParent, ReturnMessageDto result) {

		if (null != entiteParent.getSiservInfo() && null != entiteParent.getSiservInfo().getCodeServi() && entiteParent.getSiservInfo().getCodeServi().trim().matches(".*[0-9]+")) {
			result.getErrors().add("Vous ne pouvez pas créer d'entité sous cette entité parent, car elle a un code AS400 numérique.");
		}

		return result;
	}

	/**
	 * Check les statuts de toutes les entites d une branche selon les statuts
	 * acceptés passés en parametre
	 * 
	 * @param entite
	 *            EntiteDto
	 * @param result
	 *            ReturnMessageDto
	 * @param listIdStatutAcceptes
	 *            List<Integer> liste des ID de statuts acceptés
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto checkStatutOfEntityAndTheirsChildren(Entite entite, ReturnMessageDto result, List<StatutEntiteEnum> listIdStatutAcceptes, boolean withChildren) {

		// on verifie que entiteDto est "actif" ou transitoire"
		if (!listIdStatutAcceptes.contains(entite.getStatut())) {
			String error = "Le statut de l'entité " + entite.getSigle() + " n'est pas ";

			for (StatutEntiteEnum statutEnum : listIdStatutAcceptes) {
				error += statutEnum.toString() + ", ni ";
			}
			error = error.substring(0, error.length() - 5);

			result.getErrors().add(error);
			return result;
		}

		// #18143 on ne check plus les entites enfant
		if (null != entite.getEntitesEnfants() && withChildren) {
			for (Entite enfant : entite.getEntitesEnfants()) {
				result = checkStatutOfEntityAndTheirsChildren(enfant, result, listIdStatutAcceptes, withChildren);
				if (!result.getErrors().isEmpty()) {
					return result;
				}
			}
		}

		return result;
	}

	/**
	 * Deplace les fiches de poste d une entite transitoire vers une entite
	 * active.
	 * 
	 * @param idAgent
	 *            Integer
	 * @param idEntiteSource
	 *            Integer
	 * @param idEntiteCible
	 *            Integer
	 * @return ReturnMessageDto
	 */
	@Override
	@Transactional(readOnly = true)
	public ReturnMessageDto deplaceFichesPosteFromEntityToOtherEntity(Integer idAgent, Integer idEntiteSource, Integer idEntiteCible) {

		ReturnMessageDto result = new ReturnMessageDto();
		// 17765
		// on verifie les droits de la personne
		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		result = accessRightsService.verifAccessRightEcriture(convertedIdAgent, result);
		if (!result.getErrors().isEmpty())
			return result;

		// on verifie les statuts des entites
		Entite entiteSource = adsRepository.get(Entite.class, idEntiteSource);
		if (null == entiteSource) {
			result.getErrors().add("L'entité source n'existe pas.");
			return result;
		}
		if (!StatutEntiteEnum.TRANSITOIRE.equals(entiteSource.getStatut())) {
			result.getErrors().add("L'entité source n'est pas en statut transitoire.");
			return result;
		}

		Entite entiteCible = adsRepository.get(Entite.class, idEntiteCible);
		if (null == entiteCible) {
			result.getErrors().add("L'entité cible n'existe pas.");
			return result;
		}
		if (!StatutEntiteEnum.ACTIF.equals(entiteCible.getStatut())) {
			result.getErrors().add("L'entité cible n'est pas en statut actif.");
			return result;
		}

		// si tout est ok
		// on appel SIRH-WS
		result = sirhWsConsumer.deplaceFichePosteFromEntityToOtherEntity(idEntiteSource, idEntiteCible, idAgent);

		return result;
	}

	/**
	 * on filtre un arbre d entiteDto pour ne garder que les entites ayant un
	 * statut comme en parametre
	 * 
	 * @param entite
	 *            EntiteDto
	 * @param statutAGarder
	 *            List<StatutEntiteEnum>
	 */
	private void filtreArbreEntite(EntiteDto entite, List<Integer> idStatutAGarder) {

		if (null != entite.getEnfants()) {
			for (Iterator<EntiteDto> iterator = entite.getEnfants().iterator(); iterator.hasNext();) {
				EntiteDto enfant = iterator.next();
				if (idStatutAGarder.contains(enfant.getIdStatut())) {
					filtreArbreEntite(enfant, idStatutAGarder);
				} else {
					iterator.remove();
				}
			}
		}
	}
}
