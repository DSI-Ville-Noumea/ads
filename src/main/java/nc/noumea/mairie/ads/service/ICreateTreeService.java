package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;

public interface ICreateTreeService {

	void createTreeFromRevisionAndNoeuds(RevisionDto revision, NoeudDto rootNode);
	
}
