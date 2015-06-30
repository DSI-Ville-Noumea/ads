package nc.noumea.mairie.ads.service;

import java.util.List;

import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;

public interface IReferenceDataService {

	List<ReferenceDto> getReferenceDataListTypeEntite();

	boolean doesTypeEntiteValueAlreadyExists(String value);

	void saveNewTypeEntite(String label);

	ReturnMessageDto createOrModifyTypeEntite(ReferenceDto dto);

	ReferenceDto getTypeEntiteById(Integer id);

	ReturnMessageDto deleteTypeEntiteById(Integer id);

	ReturnMessageDto disableTypeEntiteById(Integer id);
}
