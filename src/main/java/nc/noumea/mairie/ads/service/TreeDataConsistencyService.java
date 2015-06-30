package nc.noumea.mairie.ads.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.repository.ISirhRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TreeDataConsistencyService implements ITreeDataConsistencyService {

	private final String DUPLICATED_SIGLE_ERR_MSG = "Le sigle '%s' est dupliqué sur plus d'un noeud.";
	private final String MISSING_SIGLE_ERR_MSG = "Le sigle est manquant sur un noeud.";
	private final String DUPLICATED_SISERV_CODE_ERR_MSG = "Le code SISERV '%s' est dupliqué sur plus d'un noeud.";
	private final String MISSING_SISERV_CODE_ERR_MSG = "Le code SISERV du noeud '%s' est vide alors que celui de son sous service '%s' est rempli.";

	@Autowired
	private ISirhRepository sirhRepository;

	@Override
	public List<ErrorMessageDto> checkDataConsistency(Entite racine, boolean isRollback) {

		List<ErrorMessageDto> errorMessages = new ArrayList<>();

		// check that all SIGLES are differents and not empty
		checkAllSiglesAreDifferent(racine, errorMessages);

		// check that all SISERV codes are differents
		checkAllSiservCodesAreDifferent(racine, errorMessages);

		// check that a service without SISERV code is not parent of a service having a SISERV code
		// because that would disable the export the child entity in SISERV services
		//checkSiservCodesHierarchy(racine, errorMessages);

		return errorMessages;
	}

	protected void checkAllSiglesAreDifferent(Entite noeud, List<ErrorMessageDto> errorMessages) {

		Map<String, Integer> sigles = new HashMap<>();

		checkAllSiglesAreDifferentRecursirve(noeud, sigles, errorMessages);
	}

	protected void checkAllSiglesAreDifferentRecursirve(Entite entite, Map<String, Integer> sigles, List<ErrorMessageDto> errorMessages) {

		// Detect if sigle is empty
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
			checkAllSiglesAreDifferentRecursirve(enfant, sigles, errorMessages);
		}
	}


	protected void checkAllSiservCodesAreDifferent(Entite entite, List<ErrorMessageDto> errorMessages) {

		Map<String, Integer> codes = new HashMap<>();

		checkAllSiservCodesAreDifferentRecursirve(entite, codes, errorMessages);
	}

	protected void checkAllSiservCodesAreDifferentRecursirve(Entite entite, Map<String, Integer> codes, List<ErrorMessageDto> errorMessages) {

		String capCode = StringUtils.upperCase(entite.getSiservInfo().getCodeServi());
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
