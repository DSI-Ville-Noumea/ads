package nc.noumea.mairie.ads.service.impl;

import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.service.IAccessRightsService;
import nc.noumea.mairie.sirh.dto.AccessRightOrganigrammeDto;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessRightsService implements IAccessRightsService {

	private Logger logger = LoggerFactory.getLogger(AccessRightsService.class);
	private final String BAD_RIGHT = "Votre identifiant n'a pas les droits nécessaires pour effectuer cette opération.";

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Override
	public ReturnMessageDto verifAccessRightAdministrateur(Integer idAgent) {
		ReturnMessageDto result = new ReturnMessageDto();
		// on recupere les droits via SIRH-WS
		try {
			AccessRightOrganigrammeDto droit = sirhWsConsumer.getAutorisationOrganigramme(idAgent);
			if (droit == null || !droit.isAdministrateur()) {
				logger.debug(BAD_RIGHT);
				result.getErrors().add(BAD_RIGHT);
				return result;
			} else {
				return result;
			}
		} catch (Exception e) {
			logger.debug(BAD_RIGHT);
			result.getErrors().add(BAD_RIGHT);
			return result;
		}
	}

	@Override
	public ReturnMessageDto verifAccessRightEcriture(Integer idAgent) {
		ReturnMessageDto result = new ReturnMessageDto();
		// on recupere les droits via SIRH-WS
		try {
			AccessRightOrganigrammeDto droit = sirhWsConsumer.getAutorisationOrganigramme(idAgent);
			if (droit == null || !droit.isEdition()) {
				logger.debug(BAD_RIGHT);
				result.getErrors().add(BAD_RIGHT);
				return result;
			} else {
				return result;
			}
		} catch (Exception e) {
			logger.debug(BAD_RIGHT);
			result.getErrors().add(BAD_RIGHT);
			return result;
		}
	}
}
