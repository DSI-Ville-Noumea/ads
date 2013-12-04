package nc.noumea.mairie.ads.repository;

import java.util.List;

import nc.noumea.mairie.ads.domain.Revision;

public interface IRevisionRepository {

	Revision getLatestRevision();

	List<Revision> getAllRevisionsByDateEffetDesc();
}
