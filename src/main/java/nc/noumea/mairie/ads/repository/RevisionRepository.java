package nc.noumea.mairie.ads.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ads.domain.Revision;

import org.springframework.stereotype.Repository;

@Repository
public class RevisionRepository implements IRevisionRepository {

	@PersistenceContext(name = "adsPersistenceUnit")
	private EntityManager adsEntityManager;
	
	@Override
	public Revision getLatestRevision() {

		TypedQuery<Revision> q = adsEntityManager.createNamedQuery("getLatestRevision", Revision.class);
		
		return q.getSingleResult();
	}

}
