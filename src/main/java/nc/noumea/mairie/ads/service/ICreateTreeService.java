package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.TypeHistoEnum;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;

public interface ICreateTreeService {

	ReturnMessageDto createEntity(Integer idAgent, EntiteDto entiteDto, TypeHistoEnum typeHisto, ReturnMessageDto result);

	ReturnMessageDto modifyEntity(Integer idAgent, EntiteDto entiteDto, ReturnMessageDto result);

	ReturnMessageDto deleteEntity(Integer idEntite, Integer idAgent, ReturnMessageDto result);

	ReturnMessageDto duplicateEntity(Integer idAgent, EntiteDto entiteDto, ReturnMessageDto result, boolean withChildren);

}
