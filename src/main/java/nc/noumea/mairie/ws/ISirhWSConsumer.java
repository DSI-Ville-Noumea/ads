package nc.noumea.mairie.ws;

import java.util.List;

import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.sirh.dto.AccessRightOrganigrammeDto;
import nc.noumea.mairie.sirh.dto.FichePosteDto;

public interface ISirhWSConsumer {

	ReturnMessageDto deleteFichesPosteByIdEntite(Integer idEntite, Integer idAgent, String sigle);

	ReturnMessageDto dupliqueFichesPosteByIdEntite(Integer idEntiteNew, Integer idEntiteOld, Integer idAgent);

	ReturnMessageDto activeFichesPosteByIdEntite(Integer idEntite, Integer idAgent);

	List<FichePosteDto> getListFichesPosteByIdEntite(Integer idEntite, List<String> listStatutFichePoste);

	AccessRightOrganigrammeDto getAutorisationOrganigramme(Integer idAgent);

	ReturnMessageDto deplaceFichePosteFromEntityToOtherEntity(Integer idEntiteSource, Integer idEntiteCible, Integer idAgent);

	ReturnMessageDto rendInactivesFichePosteFromEntity(Integer idEntite, Integer idAgent);

	ReturnMessageDto rendTransitoireFichePosteFromEntity(Integer idEntite, Integer idAgent);
}
