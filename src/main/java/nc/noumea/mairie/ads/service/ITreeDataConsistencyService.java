package nc.noumea.mairie.ads.service;

import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;

public interface ITreeDataConsistencyService {

	public List<ErrorMessageDto> checkDataConsistencyForWholeTree(Entite racine, boolean isRollback);

	List<ErrorMessageDto> checkDataConsistencyForNewEntity(Entite racine,
			Entite newEntity);

	List<ErrorMessageDto> checkDataConsistencyForModifiedEntity(Entite racine,
			Entite entiteModifiee);
}
