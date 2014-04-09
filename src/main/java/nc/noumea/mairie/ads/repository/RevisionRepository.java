package nc.noumea.mairie.ads.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import nc.noumea.mairie.ads.domain.Revision;

import org.springframework.stereotype.Repository;

@Repository
public class RevisionRepository implements IRevisionRepository {

	@PersistenceContext(unitName = "adsPersistenceUnit")
	private EntityManager adsEntityManager;
	
	@Override
	public Revision getLatestRevision() {

		TypedQuery<Revision> q = adsEntityManager.createNamedQuery("getLatestRevision", Revision.class);
		q.setMaxResults(1);

		return q.getSingleResult();
	}

	@Override
	public List<Revision> getAllRevisionsByDateEffetDesc() {
		
		TypedQuery<Revision> q = adsEntityManager.createNamedQuery("getAllRevisionByDateEffetDesc", Revision.class);
		
		return q.getResultList();
	}

	@Override
	public Revision getLatestRevisionForDate(Date date) {

		TypedQuery<Revision> q = adsEntityManager.createQuery("from Revision r where r.dateEffet = :d and r.exportedSiserv = false order by r.dateModif desc", Revision.class);
		q.setParameter("d", date, TemporalType.DATE);
		q.setMaxResults(1);

		List<Revision> result = q.getResultList();

		return result.size() == 0 ? null : result.get(0);
	}

	@Override
	public void updateRevisionToExported(Revision revision) {

		Query q = adsEntityManager.createQuery("update Revision r set r.exportedSiserv = true where r.idRevision = :id");
		q.setParameter("id", revision.getIdRevision());
		q.executeUpdate();
	}

	@Override
	public Revision getRevision(Long idRevision) {
		return adsEntityManager.find(Revision.class, idRevision);
	}

}
