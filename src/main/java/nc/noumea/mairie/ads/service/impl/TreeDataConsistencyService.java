package nc.noumea.mairie.ads.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.repository.IMairieRepository;
import nc.noumea.mairie.ads.service.ITreeDataConsistencyService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TreeDataConsistencyService implements ITreeDataConsistencyService {

	private final String DUPLICATED_SIGLE_ERR_MSG = "Le sigle '%s' est dupliqué sur plus d'une entité.";
	private final String MISSING_SIGLE_ERR_MSG = "Le sigle est manquant sur une entité.";
	private final String DUPLICATED_SISERV_CODE_ERR_MSG = "Le code SISERV '%s' est dupliqué sur plus d'une entité.";
	private final String MISSING_SISERV_CODE_ERR_MSG = "Le code SISERV de l'entité '%s' est vide alors que celui de sa sous entité '%s' est rempli.";

	@Autowired
	private IMairieRepository sirhRepository;

	@Override
	public List<ErrorMessageDto> checkDataConsistencyForWholeTree(Entite racine, boolean isRollback) {

		List<ErrorMessageDto> errorMessages = new ArrayList<>();

		// check that all SIGLES are differents and not empty
		Map<String, Integer> sigles = new HashMap<>();
		checkAllSiglesAreDifferent(racine, errorMessages, null, sigles, null);
		
		// check that all SISERV codes are differents
		Map<String, Integer> codes = new HashMap<>();
		checkAllSiservCodesAreDifferent(racine, errorMessages, null, codes);

		// check that a service without SISERV code is not parent of a service having a SISERV code
		// because that would disable the export the child entity in SISERV services
		checkSiservCodesHierarchy(racine, errorMessages, null);

		return errorMessages;
	}

	@Override
	public ReturnMessageDto checkDataConsistencyForNewEntity(Entite racine, Entite newEntity) {

		ReturnMessageDto errorMessages = new ReturnMessageDto();

		// check that all SIGLES are differents and not empty
		Map<String, Integer> sigles = new HashMap<String, Integer>();
		// in first time, we check the tree
		checkAllSiglesAreDifferent(racine, null, errorMessages, sigles, null);
		// in second time, we check the new entity and compare to the tree
		checkSigleForEntitePrevisionCreatedOrModified(errorMessages, sigles, newEntity);
		
//		checkAllSiglesAreDifferent(newEntity, errorMessages, sigles, null);
		
		// check that all SISERV codes are differents
		Map<String, Integer> codes = new HashMap<String, Integer>();
		// in first time, we check the tree
		checkAllSiservCodesAreDifferent(racine, null, errorMessages, codes);
		// in second time, we check the new entity and compare to the tree
//		checkAllSiservCodesAreDifferent(newEntity, errorMessages, codes);

		// check that a service without SISERV code is not parent of a service having a SISERV code
		// because that would disable the export the child entity in SISERV services
		checkSiservCodesHierarchy(newEntity, null, errorMessages);

		return errorMessages;
	}
	


	@Override
	public ReturnMessageDto checkDataConsistencyForModifiedEntity(Entite racine, Entite entiteModifiee) {

		ReturnMessageDto errorMessages = new ReturnMessageDto();

		// check that all SIGLES are differents and not empty
		Map<String, Integer> sigles = new HashMap<String, Integer>();
		// in first time, we check the tree
		checkAllSiglesAreDifferent(racine, null, errorMessages, sigles, entiteModifiee);
		checkAllSiglesAreDifferent(entiteModifiee, null, errorMessages, sigles, null);
		
		checkSigleForEntitePrevisionCreatedOrModified(errorMessages, sigles, entiteModifiee);
		
		// dans le cas ou l entite modifiee est en statut PREVISION

		return errorMessages;
	}
	
	protected void checkSigleForEntitePrevisionCreatedOrModified(ReturnMessageDto returnMessageDto, Map<String, Integer> sigles, Entite entite) {
		if(StatutEntiteEnum.PREVISION.equals(entite.getStatut())) {
			String capSigle = StringUtils.upperCase(entite.getSigle());
			sigles.put(capSigle, sigles.get(capSigle) == null ? 1 : sigles.get(capSigle) + 1);
			if(sigles.get(capSigle) > 1) {
				returnMessageDto.getInfos().add("Attention, le sigle est déjà utilisé par une autre entité active.");
			}
		}
	}

	/**
	 * Ce service permet de verifier que tous les sigles de toutes les entites de l arbre sont differents
	 * 
	 * @param entite l entite racine de l arbre (l arbre complet)
	 * @param errorMessages les erreurs eventuelles
	 * @param sigles liste des sigles de tout l'arbre
	 * @param entiteModifiee A ne renseigner que dans le cas d une modification d une seule entite
	 */
	protected void checkAllSiglesAreDifferent(Entite entite, List<ErrorMessageDto> errorMessages, ReturnMessageDto returnMessageDto, Map<String, Integer> sigles, Entite entiteModifiee) {

		checkAllSiglesAreDifferentRecursirve(entite, sigles, errorMessages, returnMessageDto, entiteModifiee);
	}

	protected void checkAllSiglesAreDifferentRecursirve(Entite entite, Map<String, Integer> sigles, List<ErrorMessageDto> errorMessages, ReturnMessageDto returnMessageDto, Entite entiteModifiee) {

		// Detect if sigle is empty
		// pour eviter un doublon, car l entite modifiee est aussi presente dans l entite racine
		if(null == entiteModifiee || null == entiteModifiee.getIdEntite()
				|| !entite.getIdEntite().equals(entiteModifiee.getIdEntite())) {
			if (StringUtils.isBlank(entite.getSigle())) {
				addError(errorMessages, returnMessageDto, entite, MISSING_SIGLE_ERR_MSG, null);
			} else if(StatutEntiteEnum.ACTIF.equals(entite.getStatut())) {
				String capSigle = StringUtils.upperCase(entite.getSigle());
				sigles.put(capSigle, sigles.get(capSigle) == null ? 1 : sigles.get(capSigle) + 1);
	
				// Detect if sigle is duplicated
				if (sigles.get(capSigle) > 1) {
					addError(errorMessages, returnMessageDto, entite, String.format(DUPLICATED_SIGLE_ERR_MSG, capSigle), null);
				}
			}
	
			// Recursive call through children
			for (Entite enfant : entite.getEntitesEnfants()) {
				checkAllSiglesAreDifferentRecursirve(enfant, sigles, errorMessages, returnMessageDto, entiteModifiee);
			}
		}
	}


	protected void checkAllSiservCodesAreDifferent(Entite entite, List<ErrorMessageDto> errorMessages, ReturnMessageDto returnMessageDto, Map<String, Integer> codes) {

		checkAllSiservCodesAreDifferentRecursirve(entite, codes, errorMessages, returnMessageDto);
	}

	protected void checkAllSiservCodesAreDifferentRecursirve(Entite entite, Map<String, Integer> codes, List<ErrorMessageDto> errorMessages, ReturnMessageDto returnMessageDto) {

		String capCode = StringUtils.upperCase(entite.getSiservInfo().getCodeServi());
		codes.put(capCode, codes.get(capCode) == null ? 1 : codes.get(capCode) + 1);

		// Detect if sigle is duplicated
		if (StringUtils.isNoneBlank(capCode) && codes.get(capCode) > 1) {
			addError(errorMessages, returnMessageDto, entite, String.format(DUPLICATED_SISERV_CODE_ERR_MSG, capCode), null);
		}

		// Recursive call through children
		for (Entite enfant : entite.getEntitesEnfants()) {
			checkAllSiservCodesAreDifferentRecursirve(enfant, codes, errorMessages, returnMessageDto);
		}
	}

	protected void checkSiservCodesHierarchy(Entite entite, List<ErrorMessageDto> errorMessages, ReturnMessageDto returnMessageDto) {

		boolean hasCodeServi = StringUtils.isNoneBlank(entite.getSiservInfo().getCodeServi());

		for (Entite enfant : entite.getEntitesEnfants()) {
			if (!hasCodeServi && StringUtils.isNoneBlank(enfant.getSiservInfo().getCodeServi())) {
				addError(errorMessages, returnMessageDto, entite, String.format(MISSING_SISERV_CODE_ERR_MSG, entite.getSigle(), enfant.getSigle()), null);
			}
			checkSiservCodesHierarchy(enfant, errorMessages, returnMessageDto);
		}
	}
	
	private void addError(List<ErrorMessageDto> errorMessages, ReturnMessageDto returnMessageDto, Entite entite, String labelError, String labelInfo) {
		if(null != errorMessages) {
			ErrorMessageDto error = new ErrorMessageDto();
			error.setIdEntite(entite.getIdEntite());
			error.setSigle(entite.getSigle());
			error.setMessage(labelError);
			errorMessages.add(error);
		}
		if(null != returnMessageDto) {
			if(null != labelError) {
				returnMessageDto.getErrors().add(labelError);
			}
			if(null != labelInfo) {
				returnMessageDto.getInfos().add(labelInfo);
			}
		}
	}
}
