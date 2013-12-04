package nc.noumea.mairie.ads.service;

import java.util.List;

import nc.noumea.mairie.ads.dto.RevisionDto;

public interface IRevisionService {

	List<RevisionDto> getRevisionsByDateEffetDesc();
	
}
