package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.dto.ReturnMessageDto;

public interface IAccessRightsService {

	ReturnMessageDto verifAccessRightAdministrateur(Integer idAgent, ReturnMessageDto result);

	ReturnMessageDto verifAccessRightEcriture(Integer idAgent, ReturnMessageDto result);
}
