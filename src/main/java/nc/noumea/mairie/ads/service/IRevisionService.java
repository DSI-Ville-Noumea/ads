package nc.noumea.mairie.ads.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ads.domain.Revision;
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
	 * @return
	 */
	Revision rollbackToPreviousRevision(Integer revisionId);

	/**
	 * Returns a specific Revision by its Id
	 * @param idRevision
	 * @return
	 */
	RevisionDto getRevisionById(Long idRevision);
}
