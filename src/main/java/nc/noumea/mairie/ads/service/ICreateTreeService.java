package nc.noumea.mairie.ads.service;

import java.util.List;

import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;

public interface ICreateTreeService {

	/**
	 * This methods takes a RevisionDto and NoeudDto rootnode
	 * to build a new tree revision and save it in database.
	 * @param revision
	 * @param rootNode
	 * @return
	 */
	List<ErrorMessageDto> createTreeFromRevisionAndNoeuds(RevisionDto revision, NoeudDto rootNode);
}
