package nc.noumea.mairie.ads.service;

import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;

public interface ICreateTreeService {

	/**
	 * This methods takes EntiteDto rootEntity
	 * to build a new tree save it in database.
	 * @param rootEntity EntiteDto
	 * @return
	 */
	List<ErrorMessageDto> createTreeFromEntites(EntiteDto rootEntity);

	/**
	 * This methods takes a Entite rootEntity
	 * to build a new tree and save it in database.
	 * This is used for rollback situation where a tree copy is made without
	 * needing the user to enter information (no need of Dto).
	 * @param rootEntity Entite
	 * @param isRollback boolean
	 * @return
	 */
	List<ErrorMessageDto> createTreeFromEntites(Entite rootEntity,
			boolean isRollback);

	ReturnMessageDto createEntity(EntiteDto entiteDto);

	ReturnMessageDto modifyEntity(EntiteDto entiteDto);

	ReturnMessageDto deleteEntity(Integer idEntite);
}
