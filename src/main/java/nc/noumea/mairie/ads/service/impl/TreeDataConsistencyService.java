package nc.noumea.mairie.ads.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.repository.ISirhRepository;
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
	private ISirhRepository sirhRepository;

	@Override
	public List<ErrorMessageDto> checkDataConsistencyForWholeTree(Entite racine, boolean isRollback) {

		List<ErrorMessageDto> errorMessages = new ArrayList<>();

		// check that all SIGLES are differents and not empty
		Map<String, Integer> sigles = new HashMap<>();
		checkAllSiglesAreDifferent(racine, errorMessages, sigles, null);
		
		// check that all SISERV codes are differents
		Map<String, Integer> codes = new HashMap<>();
		checkAllSiservCodesAreDifferent(racine, errorMessages, codes);

		// check that a service without SISERV code is not parent of a service having a SISERV code
		// because that would disable the export the child entity in SISERV services
		checkSiservCodesHierarchy(racine, errorMessages);

		return errorMessages;
	}

	@Override
	public List<ErrorMessageDto> checkDataConsistencyForNewEntity(Entite racine, Entite newEntity) {

		List<ErrorMessageDto> errorMessages = new ArrayList<>();

		// check that all SIGLES are differents and not empty
		Map<String, Integer> sigles = new HashMap<String, Integer>();
		// in first time, we check the tree
		checkAllSiglesAreDifferent(racine, errorMessages, sigles, null);
		// in second time, we check the new entity and compare to the tree
//		checkAllSiglesAreDifferent(newEntity, errorMessages, sigles, null);
		
		// check that all SISERV codes are differents
		Map<String, Integer> codes = new HashMap<String, Integer>();
		// in first time, we check the tree
		checkAllSiservCodesAreDifferent(racine, errorMessages, codes);
		// in second time, we check the new entity and compare to the tree
//		checkAllSiservCodesAreDifferent(newEntity, errorMessages, codes);

		// check that a service without SISERV code is not parent of a service having a SISERV code
		// because that would disable the export the child entity in SISERV services
		checkSiservCodesHierarchy(newEntity, errorMessages);

		return errorMessages;
	}
	


	@Override
	public List<ErrorMessageDto> checkDataConsistencyForModifiedEntity(Entite racine, Entite entiteModifiee) {

		List<ErrorMessageDto> errorMessages = new ArrayList<>();

		// check that all SIGLES are differents and not empty
		Map<String, Integer> sigles = new HashMap<String, Integer>();
		// in first time, we check the tree
		checkAllSiglesAreDifferent(racine, errorMessages, sigles, entiteModifiee);
		checkAllSiglesAreDifferent(entiteModifiee, errorMessages, sigles, null);

		return errorMessages;
	}

	/**
	 * Ce service permet de verifier que tous les sigles de toutes les entites de l arbre sont differents
	 * 
	 * @param entite l entite racine de l arbre (l arbre complet)
	 * @param errorMessages les erreurs eventuelles
	 * @param sigles liste des sigles de tout l'arbre
	 * @param entiteModifiee A ne renseigner que dans le cas d une modification d une seule entite
	 */
	protected void checkAllSiglesAreDifferent(Entite entite, List<ErrorMessageDto> errorMessages, Map<String, Integer> sigles, Entite entiteModifiee) {

		checkAllSiglesAreDifferentRecursirve(entite, sigles, errorMessages, entiteModifiee);
	}

	protected void checkAllSiglesAreDifferentRecursirve(Entite entite, Map<String, Integer> sigles, List<ErrorMessageDto> errorMessages, Entite entiteModifiee) {

		// Detect if sigle is empty
		// pour eviter un doublon, car l entite modifiee est aussi presente dans l entite racine
		if(null == entiteModifiee || null == entiteModifiee.getIdEntite()
				|| !entite.getIdEntite().equals(entiteModifiee.getIdEntite())) {
			if (StringUtils.isBlank(entite.getSigle())) {
				ErrorMessageDto error = new ErrorMessageDto();
				error.setIdEntite(entite.getIdEntite());
				error.setSigle(entite.getSigle());
				error.setMessage(MISSING_SIGLE_ERR_MSG);
				errorMessages.add(error);
			} else {
				String capSigle = StringUtils.upperCase(entite.getSigle());
				sigles.put(capSigle, sigles.get(capSigle) == null ? 1 : sigles.get(capSigle) + 1);
	
				// Detect if sigle is duplicated
				if (sigles.get(capSigle) > 1) {
					ErrorMessageDto error = new ErrorMessageDto();
					error.setIdEntite(entite.getIdEntite());
					error.setSigle(entite.getSigle());
					error.setMessage(String.format(DUPLICATED_SIGLE_ERR_MSG, capSigle));
					errorMessages.add(error);
				}
			}
	
			// Recursive call through children
			for (Entite enfant : entite.getEntitesEnfants()) {
				checkAllSiglesAreDifferentRecursirve(enfant, sigles, errorMessages, entiteModifiee);
			}
		}
	}


	protected void checkAllSiservCodesAreDifferent(Entite entite, List<ErrorMessageDto> errorMessages, Map<String, Integer> codes) {

		checkAllSiservCodesAreDifferentRecursirve(entite, codes, errorMessages);
	}

	protected void checkAllSiservCodesAreDifferentRecursirve(Entite entite, Map<String, Integer> codes, List<ErrorMessageDto> errorMessages) {

		String capCode = StringUtils.upperCase(entite.getSiservInfo().getCodeServi());
		if(capCode.equals("DCCC")) {
			System.out.println(entite.getIdEntite());
		}
		codes.put(capCode, codes.get(capCode) == null ? 1 : codes.get(capCode) + 1);

		// Detect if sigle is duplicated
		if (StringUtils.isNoneBlank(capCode) && codes.get(capCode) > 1) {
			ErrorMessageDto error = new ErrorMessageDto();
			error.setIdEntite(entite.getIdEntite());
			error.setSigle(entite.getSigle());
			error.setMessage(String.format(DUPLICATED_SISERV_CODE_ERR_MSG, capCode));
			errorMessages.add(error);
		}

		// Recursive call through children
		for (Entite enfant : entite.getEntitesEnfants()) {
			checkAllSiservCodesAreDifferentRecursirve(enfant, codes, errorMessages);
		}

	}

	protected void checkSiservCodesHierarchy(Entite entite, List<ErrorMessageDto> errorMessages) {

		boolean hasCodeServi = StringUtils.isNoneBlank(entite.getSiservInfo().getCodeServi());

		for (Entite enfant : entite.getEntitesEnfants()) {
			if (!hasCodeServi && StringUtils.isNoneBlank(enfant.getSiservInfo().getCodeServi())) {
				ErrorMessageDto error = new ErrorMessageDto();
				error.setIdEntite(entite.getIdEntite());
				error.setSigle(entite.getSigle());
				error.setMessage(String.format(MISSING_SISERV_CODE_ERR_MSG, entite.getSigle(), enfant.getSigle()));
				errorMessages.add(error);
			}
			checkSiservCodesHierarchy(enfant, errorMessages);
		}
	}
}
