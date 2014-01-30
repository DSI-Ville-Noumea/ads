package nc.noumea.mairie.ads.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

@Repository
public class AdsRepository implements IAdsRepository {

	@PersistenceContext(name = "adsPersistenceUnit")
	private EntityManager adsEntityManager;

	@Override
	public <T> List<T> getAll(Class<T> T) {

		CriteriaBuilder cb = adsEntityManager.getCriteriaBuilder();

		CriteriaQuery<T> q = cb.createQuery(T);
		Root<T> c = q.from(T);
		q.select(c);

		return adsEntityManager.createQuery(q).getResultList();
	}

	@Override
	public <T> T get(Class<T> T, Object primaryKey) {

		if (primaryKey == null)
			return null;

		return adsEntityManager.find(T, primaryKey);

	}

	@Override
	public void persistEntity(Object entity) {
		adsEntityManager.persist(entity);
	}
}
