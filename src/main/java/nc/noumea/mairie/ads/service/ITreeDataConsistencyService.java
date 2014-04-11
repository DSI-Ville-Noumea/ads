package nc.noumea.mairie.ads.service;

import java.util.List;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;

public interface ITreeDataConsistencyService {

	public List<ErrorMessageDto> checkDataConsistency(Revision revision, Noeud racine, boolean isRollback);
}
