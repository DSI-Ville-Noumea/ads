package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;

import java.util.List;

public interface ICreateTreeService {

	/**
	 * This methods takes a RevisionDto and NoeudDto rootnode
	 * to build a new tree revision and save it in database.
	 * @param revision RevisionDto
	 * @param rootNode NoeudDto
	 * @return
	 */
	List<ErrorMessageDto> createTreeFromRevisionAndNoeuds(RevisionDto revision, NoeudDto rootNode);

	/**
	 * This methods takes a RevisionDto and Noeud rootnode
	 * to build a new tree revision and save it in database.
	 * This is used for rollback situation where a tree copy is made without
	 * needing the user to enter information (no need of Dto).
	 * @param revision RevisionDto
	 * @param rootNode Noeud
	 * @param isRollback boolean
	 * @return
	 */
	List<ErrorMessageDto> createTreeFromRevisionAndNoeuds(RevisionDto revision, Noeud rootNode, boolean isRollback);
}
