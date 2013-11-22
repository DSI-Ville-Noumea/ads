package nc.noumea.mairie.ads.repository;

import nc.noumea.mairie.ads.domain.Revision;

public interface IRevisionRepository {

	Revision getLatestRevision();
}
