package nc.noumea.mairie.ads.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class TreeDataConsistencyService implements ITreeDataConsistencyService {

	private static String DUPLICATED_SIGLE_ERR_MSG = "Le sigle '%s' est dupliqué sur plus d'un noeud.";
	private static String MISSING_SIGLE_ERR_MSG = "Le sigle est manquant sur un noeud.";
	
	@Override
	public List<ErrorMessageDto> checkDataConsistency(Revision revision, Noeud racine) {

		List<ErrorMessageDto> errorMessages = new ArrayList<ErrorMessageDto>();
		
		// check revision details
		checkRevisionDetails(revision, errorMessages);
		
		// check that all SIGLES are differents and not empty
		checkAllSiglesAreDifferent(racine, errorMessages);
		
		// check that all SISERV codes are differents
		
		// check that a service without SISERV code is not parent of a service having a SISERV code
		
		return errorMessages;
	}
	
	protected void checkRevisionDetails(Revision revision, List<ErrorMessageDto> errorMessages) {
		
	}
	
	protected void checkAllSiglesAreDifferent(Noeud noeud, List<ErrorMessageDto> errorMessages) {
		
		Map<String, Integer> sigles = new HashMap<String, Integer>();
		
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
		}
		else {
			sigles.put(noeud.getSigle(), sigles.get(noeud.getSigle()) == null ? 1 : sigles.get(noeud.getSigle()) + 1);
			
			// Detect if sigle is duplicated
			if (sigles.get(noeud.getSigle()) > 1) {
				ErrorMessageDto error = new ErrorMessageDto();
				error.setIdNoeud(noeud.getIdNoeud());
				error.setSigle(noeud.getSigle());
				error.setMessage(String.format(DUPLICATED_SIGLE_ERR_MSG, noeud.getSigle()));
				errorMessages.add(error);
			}
		}
		
		// Recursive call through children
		for (Noeud enfant : noeud.getNoeudsEnfants()) {
			checkAllSiglesAreDifferentRecursirve(enfant, sigles, errorMessages);
		}
	}

}
