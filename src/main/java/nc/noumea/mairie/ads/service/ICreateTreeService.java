package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.TypeHistoEnum;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;

public interface ICreateTreeService {

	ReturnMessageDto createEntity(Integer idAgent, EntiteDto entiteDto, TypeHistoEnum typeHisto, ReturnMessageDto result, boolean isDuplication, boolean withDelibActif);

	ReturnMessageDto modifyEntity(Integer idAgent, EntiteDto entiteDto, ReturnMessageDto result);

	ReturnMessageDto deleteEntity(Integer idEntite, Integer idAgent, ReturnMessageDto result, boolean withChildren);

	ReturnMessageDto duplicateFichesPosteOfEntity(Integer idAgent, EntiteDto entiteDto, ReturnMessageDto result, boolean withChildren, boolean withFDP);

	ReturnMessageDto deplaceFichesPosteFromEntityToOtherEntity(Integer idAgent, Integer idEntiteSource, Integer idEntiteCible);

	ReturnMessageDto duplicateEntity(Integer idAgent, EntiteDto entiteDto, ReturnMessageDto result, boolean withChildren, boolean withDelibActif);

	ReturnMessageDto transiteFichesPosteFromEntity(Integer idAgent, boolean chkInactif, boolean chkTransitoire, Integer idEntite);

}
