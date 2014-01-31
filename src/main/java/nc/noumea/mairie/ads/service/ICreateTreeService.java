package nc.noumea.mairie.ads.service;

import java.util.List;

import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;

public interface ICreateTreeService {

	List<ErrorMessageDto> createTreeFromRevisionAndNoeuds(RevisionDto revision, NoeudDto rootNode);
	
}
