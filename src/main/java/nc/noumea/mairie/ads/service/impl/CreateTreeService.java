package nc.noumea.mairie.ads.service.impl;

import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeEntite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.repository.ISirhRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.service.IHelperService;
import nc.noumea.mairie.ads.service.ITreeDataConsistencyService;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.apache.commons.lang3.StringUtils;
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
	private ISirhRepository sirhRepository;

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Autowired
	private ITreeDataConsistencyService dataConsistencyService;

	private static String LIST_STATIC_CHARS = "BCDEFGHIJKLMNOPQRSTUVWXYZ";

	@Override
	@Transactional(value = "adsTransactionManager")
	public List<ErrorMessageDto> createTreeFromEntites(EntiteDto rootEntity) {

		List<String> existingServiCodes = sirhRepository.getAllServiCodes();

		Entite racine = buildCoreEntites(rootEntity, null, existingServiCodes);

		return saveWholeTreeAndReturnMessages(racine, false);
	}

	@Override
	@Transactional(value = "adsTransactionManager")
	public List<ErrorMessageDto> createTreeFromEntites(Entite rootEntity, boolean isRollback) {

		Entite racine = buildCoreEntites(rootEntity);

		return saveWholeTreeAndReturnMessages(racine, isRollback);
	}

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

		createCodeServiIfEmpty(newEntity, existingServiCodes);

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

		return saveNewEntityAndReturnMessages(entite);
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

		List<String> existingServiCodes = sirhRepository.getAllServiCodes();
		entite = modifyCoreEntites(entiteDto, entite, existingServiCodes);

		return saveModifiedEntityAndReturnMessages(entite);
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
		entite.setDateModification(entiteDto.getDateModification());

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

		if (null != entiteDto.getTypeEntite() && null != entiteDto.getTypeEntite().getId()) {
			entite.setTypeEntite(adsRepository.get(TypeEntite.class, entiteDto.getTypeEntite().getId()));
		}
	}

	protected Entite buildCoreEntites(EntiteDto entiteDto, Entite parent, List<String> existingServiCodes,
			boolean withChildren) {

		Entite newEntity = new Entite();

		// on mappe les donnees communes avec la modification
		mappingData(entiteDto, newEntity);

		// ces champs sont specifiques a la creation
		newEntity.setEntiteParent(parent);

		if (null != entiteDto.getEntiteRemplacee() && null != entiteDto.getEntiteRemplacee().getIdEntite()) {
			newEntity.setEntiteRemplacee(adsRepository.get(Entite.class, entiteDto.getEntiteRemplacee().getIdEntite()));
		}
		// l agent qui cree
		newEntity.setIdAgentCreation(entiteDto.getIdAgentCreation());
		newEntity.setDateCreation(entiteDto.getDateCreation());
		// le statut de l entite
		newEntity.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getIdStatut()));

		if (parent != null)
			newEntity.addParent(parent);

		SiservInfo sisInfo = new SiservInfo();
		sisInfo.setCodeServi(entiteDto.getCodeServi() == null || entiteDto.getCodeServi().equals("") ? null : entiteDto
				.getCodeServi());
		sisInfo.addToEntite(newEntity);

		createCodeServiIfEmpty(newEntity, existingServiCodes);

		if (withChildren) {
			for (EntiteDto enfantDto : entiteDto.getEnfants()) {
				buildCoreEntites(enfantDto, newEntity, existingServiCodes, withChildren);
			}
		}

		return newEntity;
	}

	protected void createCodeServiIfEmpty(Entite entite, List<String> existingSiservCodes) {

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
		int level = codeParent.indexOf('A');

		if (level == -1)
			return;

		String newCode = codeParent.substring(0, level);
		String code = "";
		for (int i = 0; i < LIST_STATIC_CHARS.length(); i++) {
			code = newCode.concat(String.valueOf(LIST_STATIC_CHARS.charAt(i)));
			code = StringUtils.rightPad(code, 4, 'A');
			if (!existingSiservCodes.contains(code))
				break;
			else
				code = "";
		}

		// We've found the code !!
		if (!StringUtils.isBlank(code)) {
			entite.getSiservInfo().setCodeServi(code);
			existingSiservCodes.add(code);
		}
	}

	protected List<ErrorMessageDto> saveWholeTreeAndReturnMessages(Entite rootEntity, boolean isRollback) {

		List<ErrorMessageDto> errorMessages = dataConsistencyService.checkDataConsistencyForWholeTree(rootEntity,
				isRollback);

		if (errorMessages.size() == 0) {
			adsRepository.persistEntity(rootEntity);
		}

		return errorMessages;
	}

	protected ReturnMessageDto saveNewEntityAndReturnMessages(Entite entite) {

		ReturnMessageDto result = null;
		// on recupere l arbre en entier
		Entite root = treeRepository.getWholeTree().get(0);
		// pour ensuite verifier les donnees de la nouvelle entite avec l arbre
		List<ErrorMessageDto> errorMessages = dataConsistencyService.checkDataConsistencyForNewEntity(root, entite);

		if (errorMessages.size() == 0) {
			adsRepository.persistEntity(entite);
			result = new ReturnMessageDto();
			result.getInfos().add("L'entité est bien créée.");
			result.setId(entite.getIdEntite());
		} else {
			adsRepository.clear();
			throw new ReturnMessageDtoException(new ReturnMessageDto(errorMessages));
		}

		return result;
	}

	protected ReturnMessageDto saveModifiedEntityAndReturnMessages(Entite entite) {

		ReturnMessageDto result = null;
		// on recupere l arbre en entier
		Entite root = treeRepository.getWholeTree().get(0);
		// pour ensuite verifier les donnees de la nouvelle entite avec l arbre
		List<ErrorMessageDto> errorMessages = dataConsistencyService
				.checkDataConsistencyForModifiedEntity(root, entite);

		if (errorMessages.size() == 0) {
			adsRepository.persistEntity(entite);
			result = new ReturnMessageDto();
			result.getInfos().add("L'entité est bien modifiée.");
			result.setId(entite.getIdEntite());
		} else {
			adsRepository.clear();
			throw new ReturnMessageDtoException(new ReturnMessageDto(errorMessages));
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

		adsRepository.removeEntity(entite);

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
}
