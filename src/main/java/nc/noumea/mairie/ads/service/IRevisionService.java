package nc.noumea.mairie.ads.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.dto.DiffRevisionDto;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.RevisionDto;

public interface IRevisionService {

	/**
	 * This method returns a list of Revision DTO
	 * ordered by date effet desc (the newest first)
	 * @return a list of RevisionDto objects
	 */
	List<RevisionDto> getRevisionsByDateEffetDesc();

	/**
	 * Returns the latest Revision which Date Effet is
	 * the given date
	 * @return One or no Revision object
	 */
	Revision getLatestyRevisionForDate(Date date);

	/**
	 * Updates a given revision setting
	 * exportedSiserv status to true.
	 * @param revision to Update
	 */
	void updateRevisionToExported(Revision revision);

	byte[] exportRevisionToGraphMl(long idRevision);

	/**
	 * This method creates a new revision of the tree
	 * by taking the {revisionId} previous version
	 * and reapplying it on top of the current version.
	 * By Default and if null, revisionId is the previous version
	 * @return a list of messages in case the Revision could not be created
	 */
	List<ErrorMessageDto> rollbackToPreviousRevision(RevisionDto revisionDto, Long idRevision);

	/**
	 * Returns a specific Revision by its Id
	 * @param idRevision
	 * @return
	 */
	RevisionDto getRevisionById(Long idRevision);

	/**
	 * This method takes in parameter two revision id to compute the diff from revision to revision 2.
	 * @param idRevision the source of the diff
	 * @param idRevisionTo the target of the diff
	 * @return the list of modifications to get from revision to revision 2
	 */
	DiffRevisionDto getRevisionsDiff(Long idRevision, Long idRevisionTo);

	/**
	 * Return the current revision
	 * 
	 * @return the current revision
	 */
	RevisionDto getCurrentRevision();
}
