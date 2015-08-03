package nc.noumea.mairie.ads.service.impl;

import java.util.Date;
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
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.service.IHelperService;
import nc.noumea.mairie.ads.service.ISiservUpdateService;
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

	protected Entite buildCoreEntites(EntiteDto entiteDto, Entite parent, List<String> existingServiCodes) {

		Entite newEntity = new Entite();
		newEntity.setLabel(entiteDto.getLabel());
		newEntity.setSigle(entiteDto.getSigle());
		newEntity.setTypeEntite(adsRepository.get(TypeEntite.class, entiteDto.getTypeEntite().getId()));

		if (parent != null)
			newEntity.addParent(parent);

		SiservInfo sisInfo = new SiservInfo();
		sisInfo.setCodeServi(entiteDto.getCodeServi() == null || entiteDto.getCodeServi().equals("") ? null : entiteDto
				.getCodeServi());
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
	public ReturnMessageDto createEntity(EntiteDto entiteDto) {

		ReturnMessageDto result = new ReturnMessageDto();

		// statut PREVISION OBLIGATOIRE
		entiteDto.setIdStatut(StatutEntiteEnum.PREVISION.getIdRefStatutEntite());

		result = checkDataToCreateEntity(entiteDto);

		if (!result.getErrors().isEmpty())
			return result;

		List<String> existingServiCodes = sirhRepository.getAllServiCodes();
		Entite entiteParent = adsRepository.get(Entite.class, entiteDto.getEntiteParent().getIdEntite());
		Entite entite = buildCoreEntites(entiteDto, entiteParent, existingServiCodes, false);

		return saveNewEntityAndReturnMessages(entite, entiteDto.getIdAgentCreation());
	}

	/**
	 * Modification d une entite
	 * 
	 * @param entiteDto
	 *            EntiteDto
	 * @return ReturnMessageDto
	 */
	@Override
	@Transactional(value = "adsTransactionManager")
	public ReturnMessageDto modifyEntity(EntiteDto entiteDto) {

		ReturnMessageDto result = new ReturnMessageDto();

		Entite entite = adsRepository.get(Entite.class, entiteDto.getIdEntite());

		if (null == entite) {
			result.getErrors().add("L'entité n'existe pas.");
			return result;
		}

		result = checkDataToModifyEntity(entiteDto, entite);
		if (!result.getErrors().isEmpty())
			return result;

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

		if (entite.getStatut().equals(StatutEntiteEnum.ACTIF)
				|| entite.getStatut().equals(StatutEntiteEnum.TRANSITOIRE)) {
			result = siservUpdateService.updateSiservNwAndSiServ(entite, dto);
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
	protected ReturnMessageDto checkRequiredData(EntiteDto entiteDto) {

		ReturnMessageDto result = new ReturnMessageDto();

		// champ obligatoire parent + sigle + libellé
		if (null == entiteDto.getSigle() || "".equals(entiteDto.getSigle().trim())) {
			result.getErrors().add("Le sigle est obligatoire.");
		}

		if (null == entiteDto.getLabel() || "".equals(entiteDto.getLabel().trim())) {
			result.getErrors().add("Le libellé est obligatoire.");
		}

		if (null == entiteDto.getEntiteParent() || null == entiteDto.getEntiteParent().getIdEntite()
				|| 0 == entiteDto.getEntiteParent().getIdEntite()) {
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
	protected ReturnMessageDto checkDataToCreateEntity(EntiteDto entiteDto) {

		ReturnMessageDto result = new ReturnMessageDto();

		result = checkRequiredData(entiteDto);

		if (!result.getErrors().isEmpty())
			return result;

		// le parent doit etre a P ou A
		Entite entiteParent = adsRepository.get(Entite.class, entiteDto.getEntiteParent().getIdEntite());
		if (!(StatutEntiteEnum.PREVISION.getIdRefStatutEntite() == entiteParent.getStatut().getIdRefStatutEntite() || StatutEntiteEnum.ACTIF
				.equals(entiteParent.getStatut()))) {
			result.getErrors().add("Le statut de l'entité parente n'est ni active ni en prévision.");
		}

		// l entite remplacee ne peut pas etre en PREVISION
		if (null != entiteDto.getEntiteRemplacee() && null != entiteDto.getEntiteRemplacee().getIdEntite()
				&& 0 != entiteDto.getEntiteRemplacee().getIdEntite()) {
			Entite entiteRemplacee = adsRepository.get(Entite.class, entiteDto.getEntiteRemplacee().getIdEntite());
			if (StatutEntiteEnum.PREVISION.equals(entiteRemplacee.getStatut())) {
				result.getErrors().add("Une entité au statut en prévision ne peut pas être remplacée.");
			}
		}

		return result;
	}

	/**
	 * #16236 : RG
	 * 
	 * @param entiteDto
	 *            EntiteDto
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto checkDataToModifyEntity(EntiteDto entiteDto, Entite entite) {

		ReturnMessageDto result = new ReturnMessageDto();

		if (StatutEntiteEnum.INACTIF.equals(entite.getStatut())) {
			result.getErrors().add("Une entité en statut inactive ne peut pas être modifiée.");
			return result;
		}

		result = checkRequiredData(entiteDto);

		if (!result.getErrors().isEmpty())
			return result;

		checkTypeEntiteAS400ToModify(result, entiteDto, entite);

		return result;
	}

	// #17083 gestion des "supers entites AS400"
	// si l on essaie de modifier le type d une entite active, transitoire ou
	// inactive
	protected ReturnMessageDto checkTypeEntiteAS400ToModify(ReturnMessageDto result, EntiteDto entiteDto, Entite entite) {

		if (null != entiteDto.getTypeEntite()
				&& !entiteDto.getTypeEntite().getId().equals(entite.getTypeEntite().getIdTypeEntite())) {

			TypeEntite nouveauTypeEntite = adsRepository.get(TypeEntite.class, entiteDto.getTypeEntite().getId());

			if (!StatutEntiteEnum.PREVISION.equals(entite.getStatut()) && nouveauTypeEntite.isEntiteAs400()) {

				List<TypeEntite> listSuperEntiteAS400 = adsRepository.getListeTypeEntiteIsSuperEntiteAS400();

				String superEntiteAS400Str = "";
				if (null != listSuperEntiteAS400) {
					for (TypeEntite entiteAS400 : listSuperEntiteAS400) {
						if (entiteAS400.isEntiteAs400()) {
							superEntiteAS400Str += entiteAS400.getLabel() + ", ";
						}
					}
					if (superEntiteAS400Str.length() > 2) {
						superEntiteAS400Str = superEntiteAS400Str.substring(0, superEntiteAS400Str.length() - 2);
					}
				}

				result.getErrors().add(
						"Vous ne pouvez pas modifier le type d'une entité active ou en transition en "
								+ superEntiteAS400Str);
				return result;
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
		// on mappe les donnees communes avec la creation
		mappingData(entiteDto, entite);
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
	protected void mappingData(EntiteDto entiteDto, Entite entite) {

		// modif + creation
		entite.setLabel(entiteDto.getLabel());
		entite.setLabelCourt(entiteDto.getLabelCourt());
		entite.setSigle(entiteDto.getSigle());
		entite.setDateDeliberationActif(entiteDto.getDateDeliberationActif());
		entite.setRefDeliberationActif(entiteDto.getRefDeliberationActif());
		entite.setDateDeliberationInactif(entiteDto.getDateDeliberationInactif());
		entite.setRefDeliberationInactif(entiteDto.getRefDeliberationInactif());
		entite.setCommentaire(entiteDto.getCommentaire());

		if (null != entiteDto.getTypeEntite() && null != entiteDto.getTypeEntite().getId()) {
			entite.setTypeEntite(adsRepository.get(TypeEntite.class, entiteDto.getTypeEntite().getId()));
		}

		if (null != entiteDto.getEntiteRemplacee() && null != entiteDto.getEntiteRemplacee().getIdEntite()) {
			entite.setEntiteRemplacee(adsRepository.get(Entite.class, entiteDto.getEntiteRemplacee().getIdEntite()));
		}
	}

	protected Entite buildCoreEntites(EntiteDto entiteDto, Entite parent, List<String> existingServiCodes,
			boolean withChildren) {

		Entite newEntity = new Entite();

		// on mappe les donnees communes avec la modification
		mappingData(entiteDto, newEntity);

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
		sisInfo.setCodeServi(entiteDto.getCodeServi() == null || entiteDto.getCodeServi().equals("") ? null : entiteDto
				.getCodeServi());
		sisInfo.addToEntite(newEntity);

		if (withChildren) {
			for (EntiteDto enfantDto : entiteDto.getEnfants()) {
				buildCoreEntites(enfantDto, newEntity, existingServiCodes, withChildren);
			}
		}

		return newEntity;
	}

	protected ReturnMessageDto saveNewEntityAndReturnMessages(Entite entite, Integer idAgentHisto) {

		ReturnMessageDto result = null;
		// on recupere l arbre en entier
		Entite root = treeRepository.getWholeTree().get(0);
		// pour ensuite verifier les donnees de la nouvelle entite avec l arbre
		ReturnMessageDto errorMessages = dataConsistencyService.checkDataConsistencyForNewEntity(root, entite);

		if (errorMessages.getErrors().isEmpty()) {
			adsRepository.persistEntity(entite, new EntiteHisto(entite, idAgentHisto, TypeHistoEnum.CREATION));
			result = new ReturnMessageDto();
			result.getInfos().add("L'entité est bien créée.");
			if (!errorMessages.getInfos().isEmpty()) {
				result.getInfos().addAll(errorMessages.getInfos());
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
		ReturnMessageDto errorMessages = dataConsistencyService.checkDataConsistencyForModifiedEntity(root, entite);

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
	 * Supprime d une entite en statut Provisoire uniquement
	 * 
	 * #16230 : RG
	 * 
	 * @param idEntite
	 *            Integer
	 * @return ReturnMessageDto
	 */
	@Override
	@Transactional(value = "adsTransactionManager")
	public ReturnMessageDto deleteEntity(Integer idEntite, Integer idAgent) {

		ReturnMessageDto result = new ReturnMessageDto();

		Entite entite = adsRepository.get(Entite.class, idEntite);
		result = checkDataToDeleteEntity(entite);

		if (!result.getErrors().isEmpty())
			return result;

		// RG3 : les fiches de poste en statut "en création" qui sont associées
		// à l'entité sont également supprimées
		// si SIRH retourne une erreur (Fiche de Poste dans un autre statut que
		// En Création)
		// on ne supprime pas
		result = sirhWsConsumer.deleteFichesPosteByIdEntite(entite.getIdEntite(), idAgent);

		if (!result.getErrors().isEmpty())
			return result;

		adsRepository.removeEntiteAvecPersistHisto(entite, new EntiteHisto(entite, idAgent, TypeHistoEnum.SUPPRESSION));

		result.getInfos().add("L'entité est bien supprimée.");

		return result;
	}

	protected ReturnMessageDto checkDataToDeleteEntity(Entite entite) {

		ReturnMessageDto result = new ReturnMessageDto();

		if (null == entite) {
			result.getErrors().add("L'entité n'existe pas.");
			return result;
		}

		// RG1 : l'entité ne doit pas avoir d'entité fille
		if (null == entite.getEntitesEnfants() || !entite.getEntitesEnfants().isEmpty()) {
			result.getErrors().add("L'entité ne peut être supprimée, car elle a un ou des entités fille.");
			return result;
		}

		return result;
	}

	@Override
	public ReturnMessageDto duplicateEntity(EntiteDto entiteDto) {
		ReturnMessageDto result = new ReturnMessageDto();

		// on verifie que entiteDto est "actif" ou transitoire"
		if (!(String.valueOf(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite()).equals(
				entiteDto.getIdStatut().toString()) || String.valueOf(StatutEntiteEnum.ACTIF.getIdRefStatutEntite())
				.equals(entiteDto.getIdStatut().toString()))) {
			result.getErrors().add("Le statut de l'entité n'est ni active ni en transitoire.");
			return result;
		}

		// on remanie de DTO pour sa creation
		entiteDto.setIdEntite(null);

		result = createEntity(entiteDto);
		if (result.getErrors().size() > 0) {
			return result;
		}

		// RG : les fiches de poste en statut "validées" qui sont associées
		// à l'entité sont dupliquées en statut "en creation" sur le nouveau
		// service
		// si SIRH retourne une erreur, c'est que l'insertion en BD du job n'a
		// pas
		// fonctionné
		result = sirhWsConsumer.dupliqueFichesPosteByIdEntite(result.getId(), entiteDto.getIdAgentCreation());

		return result;

	}
}
