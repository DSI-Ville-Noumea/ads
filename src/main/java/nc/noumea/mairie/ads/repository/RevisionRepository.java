package nc.noumea.mairie.ads.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ads.domain.Revision;

import org.springframework.stereotype.Repository;

@Repository
public class RevisionRepository implements IRevisionRepository {

	@PersistenceContext(unitName = "adsPersistenceUnit")
	private EntityManager adsEntityManager;
	
	@Override
	public Revision getLatestRevision() {

		TypedQuery<Revision> q = adsEntityManager.createNamedQuery("getLatestRevision", Revision.class);
		
		return q.getSingleResult();
	}

	@Override
	public List<Revision> getAllRevisionsByDateEffetDesc() {
		
		TypedQuery<Revision> q = adsEntityManager.createNamedQuery("getAllRevisionByDateEffetDesc", Revision.class);
		
		return q.getResultList();
	}

	@Override
	public Revision getLatestRevisionForDate(Date date) {

		TypedQuery<Revision> q = adsEntityManager.createQuery("from Revision r where r.dateEffet = :d order by r.dateModif desc", Revision.class);
		q.setParameter("d", date, TemporalType.DATE);
		q.setMaxResults(1);

		List<Revision> result = q.getResultList();

		return result.size() == 0 ? null : result.get(0);
	}

}
