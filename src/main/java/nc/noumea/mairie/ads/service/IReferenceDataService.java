package nc.noumea.mairie.ads.service;

import java.util.List;

import nc.noumea.mairie.ads.dto.ReferenceDto;

public interface IReferenceDataService {

	List<ReferenceDto> getReferenceDataListTypeNoeud();

	boolean doesTypeNoeudValueAlreadyExists(String value);

	void saveNewTypeNoeud(String label);

	void createOrModifyTypeNoeud(ReferenceDto dto);

	ReferenceDto getTypeNoeudById(Integer id);

	void deleteTypeNoeudById(Integer id);

	void disableTypeNoeudById(Integer id);
}
