package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.repository.IRevisionRepository;
import nc.noumea.mairie.ads.repository.ISirhRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TreeDataConsistencyService implements ITreeDataConsistencyService {

	private final String DUPLICATED_SIGLE_ERR_MSG = "Le sigle '%s' est dupliqué sur plus d'un noeud.";
	private final String MISSING_SIGLE_ERR_MSG = "Le sigle est manquant sur un noeud.";
	private final String MISSING_AGENT_MSG = "Révision : L'id de l'agent est manquant.";
	private final String AGENT_DOES_NOT_EXISTS_MSG = "Révision : L'agent renseigné n'existe pas.";
	private final String MISSING_DATE_EFFET_MSG = "Révision : La date d'effet est manquante.";
	private final String MISSING_DATE_DECRET_MSG = "Révision : La date de décrêt est manquante.";
	private final String DATE_EFFET_TOO_OLD_MSG = "Révision : La date d'effet est antérieure à celle de la dernière révision.";
	private final String DATE_DECRET_TOO_OLD_MSG = "Révision : La date de décrêt est antérieure à celle de la dernière révision.";
	private final String DUPLICATED_SISERV_CODE_ERR_MSG = "Le code SISERV '%s' est dupliqué sur plus d'un noeud.";
	private final String MISSING_SISERV_CODE_ERR_MSG = "Le code SISERV du noeud '%s' est vide alors que celui de son sous service '%s' est rempli.";

	@Autowired
	private IRevisionRepository revisionRepository;

	@Autowired
	private ISirhRepository sirhRepository;

	@Override
	public List<ErrorMessageDto> checkDataConsistency(Revision revision, Noeud racine, boolean isRollback) {

		List<ErrorMessageDto> errorMessages = new ArrayList<>();

		// check revision details
		checkRevisionDetails(revision, errorMessages, isRollback);

		// check that all SIGLES are differents and not empty
		checkAllSiglesAreDifferent(racine, errorMessages);

		// check that all SISERV codes are differents
		checkAllSiservCodesAreDifferent(racine, errorMessages);

		// check that a service without SISERV code is not parent of a service having a SISERV code
		// because that would disable the export the child node in SISERV services
		//checkSiservCodesHierarchy(racine, errorMessages);

		return errorMessages;
	}

	protected void checkRevisionDetails(Revision revision, List<ErrorMessageDto> errorMessages, boolean isRollback) {

		if (revision.getIdAgent() == null || revision.getIdAgent() == 0) {
			ErrorMessageDto error = new ErrorMessageDto();
			error.setMessage(MISSING_AGENT_MSG);
			errorMessages.add(error);
		} else if (sirhRepository.getAgent(revision.getIdAgent()) == null) {
			ErrorMessageDto error = new ErrorMessageDto();
			error.setMessage(AGENT_DOES_NOT_EXISTS_MSG);
			errorMessages.add(error);
		}

		Revision latestRevision = revisionRepository.getLatestRevision();

		if (revision.getDateEffet() == null) {
			ErrorMessageDto error = new ErrorMessageDto();
			error.setMessage(MISSING_DATE_EFFET_MSG);
			errorMessages.add(error);
		} else {
			if (!isRollback && latestRevision.getDateEffet().after(revision.getDateEffet())) {
				ErrorMessageDto error = new ErrorMessageDto();
				error.setMessage(DATE_EFFET_TOO_OLD_MSG);
				errorMessages.add(error);
			}
		}

		if (revision.getDateDecret() == null) {
			ErrorMessageDto error = new ErrorMessageDto();
			error.setMessage(MISSING_DATE_DECRET_MSG);
			errorMessages.add(error);
		} else {
			if (!isRollback && latestRevision.getDateDecret().after(revision.getDateDecret())) {
				ErrorMessageDto error = new ErrorMessageDto();
				error.setMessage(DATE_DECRET_TOO_OLD_MSG);
				errorMessages.add(error);
			}
		}

	}

	protected void checkAllSiglesAreDifferent(Noeud noeud, List<ErrorMessageDto> errorMessages) {

		Map<String, Integer> sigles = new HashMap<>();

		checkAllSiglesAreDifferentRecursirve(noeud, sigles, errorMessages);
	}

	protected void checkAllSiglesAreDifferentRecursirve(Noeud noeud, Map<String, Integer> sigles, List<ErrorMessageDto> errorMessages) {

		// Detect if sigle is empty
		if (StringUtils.isBlank(noeud.getSigle())) {
			ErrorMessageDto error = new ErrorMessageDto();
			error.setIdNoeud(noeud.getIdNoeud());
			error.setSigle(noeud.getSigle());
			error.setMessage(MISSING_SIGLE_ERR_MSG);
			errorMessages.add(error);
		} else {
			String capSigle = StringUtils.upperCase(noeud.getSigle());
			sigles.put(capSigle, sigles.get(capSigle) == null ? 1 : sigles.get(capSigle) + 1);

			// Detect if sigle is duplicated
			if (sigles.get(capSigle) > 1) {
				ErrorMessageDto error = new ErrorMessageDto();
				error.setIdNoeud(noeud.getIdNoeud());
				error.setSigle(noeud.getSigle());
				error.setMessage(String.format(DUPLICATED_SIGLE_ERR_MSG, capSigle));
				errorMessages.add(error);
			}
		}

		// Recursive call through children
		for (Noeud enfant : noeud.getNoeudsEnfants()) {
			checkAllSiglesAreDifferentRecursirve(enfant, sigles, errorMessages);
		}
	}


	protected void checkAllSiservCodesAreDifferent(Noeud noeud, List<ErrorMessageDto> errorMessages) {

		Map<String, Integer> codes = new HashMap<>();

		checkAllSiservCodesAreDifferentRecursirve(noeud, codes, errorMessages);
	}

	protected void checkAllSiservCodesAreDifferentRecursirve(Noeud noeud, Map<String, Integer> codes, List<ErrorMessageDto> errorMessages) {

		String capCode = StringUtils.upperCase(noeud.getSiservInfo().getCodeServi());
		codes.put(capCode, codes.get(capCode) == null ? 1 : codes.get(capCode) + 1);

		// Detect if sigle is duplicated
		if (StringUtils.isNoneBlank(capCode) && codes.get(capCode) > 1) {
			ErrorMessageDto error = new ErrorMessageDto();
			error.setIdNoeud(noeud.getIdNoeud());
			error.setSigle(noeud.getSigle());
			error.setMessage(String.format(DUPLICATED_SISERV_CODE_ERR_MSG, capCode));
			errorMessages.add(error);
		}

		// Recursive call through children
		for (Noeud enfant : noeud.getNoeudsEnfants()) {
			checkAllSiservCodesAreDifferentRecursirve(enfant, codes, errorMessages);
		}

	}

	protected void checkSiservCodesHierarchy(Noeud noeud, List<ErrorMessageDto> errorMessages) {

		boolean hasCodeServi = StringUtils.isNoneBlank(noeud.getSiservInfo().getCodeServi());

		for (Noeud enfant : noeud.getNoeudsEnfants()) {
			if (!hasCodeServi && StringUtils.isNoneBlank(enfant.getSiservInfo().getCodeServi())) {
				ErrorMessageDto error = new ErrorMessageDto();
				error.setIdNoeud(noeud.getIdNoeud());
				error.setSigle(noeud.getSigle());
				error.setMessage(String.format(MISSING_SISERV_CODE_ERR_MSG, noeud.getSigle(), enfant.getSigle()));
				errorMessages.add(error);
			}
			checkSiservCodesHierarchy(enfant, errorMessages);
		}
	}
}
