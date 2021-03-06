package nc.noumea.mairie.ads.service;

import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;

public interface ITreeDataConsistencyService {

	List<ErrorMessageDto> checkDataConsistencyForWholeTree(Entite racine, boolean isRollback);

	ReturnMessageDto checkDataConsistencyForNewEntity(Entite racine, Entite newEntity, ReturnMessageDto result, boolean isDuplication);

	ReturnMessageDto checkDataConsistencyForModifiedEntity(Entite racine, Entite entiteModifiee, ReturnMessageDto result);

	boolean checkSigleExisting(String sigle);
}
