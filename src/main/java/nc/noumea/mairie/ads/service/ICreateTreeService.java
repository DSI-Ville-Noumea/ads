package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.TypeHistoEnum;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;

public interface ICreateTreeService {

	ReturnMessageDto createEntity(EntiteDto entiteDto, TypeHistoEnum typeHisto);

	ReturnMessageDto modifyEntity(EntiteDto entiteDto);

	ReturnMessageDto deleteEntity(Integer idEntite, Integer idAgent);

	ReturnMessageDto duplicateEntity(EntiteDto entiteDto, ReturnMessageDto result);

}
