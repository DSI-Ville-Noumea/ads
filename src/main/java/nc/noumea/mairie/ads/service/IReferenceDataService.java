package nc.noumea.mairie.ads.service;

import java.util.List;

import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;

public interface IReferenceDataService {

	List<ReferenceDto> getReferenceDataListTypeEntite();

	boolean doesTypeEntiteValueAlreadyExists(String value);

	ReturnMessageDto createOrModifyTypeEntite(Integer idAgent, ReferenceDto dto, ReturnMessageDto result);

	ReferenceDto getTypeEntiteById(Integer id);

	ReturnMessageDto deleteTypeEntiteById(Integer idAgent, Integer id, ReturnMessageDto result);
}
