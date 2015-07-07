package nc.noumea.mairie.ws;

import java.util.List;

import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.sirh.dto.FichePosteDto;

public interface ISirhWSConsumer {

	ReturnMessageDto deleteFichesPosteByIdEntite(Integer idEntite, Integer idAgent);

	ReturnMessageDto activeFichesPosteByIdEntite(Integer idEntite, Integer idAgent);
	
	List<FichePosteDto> getListFichesPosteByIdEntite(Integer idEntite, List<String> listStatutFichePoste);
}
