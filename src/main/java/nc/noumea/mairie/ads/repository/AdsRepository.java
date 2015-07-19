package nc.noumea.mairie.ads.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.TypeEntite;

import org.springframework.stereotype.Repository;

@Repository
public class AdsRepository implements IAdsRepository {

	@PersistenceContext(unitName = "adsPersistenceUnit")
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
	public void persistEntity(Entite entity, EntiteHisto histo) {
		adsEntityManager.persist(entity);
		adsEntityManager.persist(histo);
	}

	@Override
	public void persistTypeEntity(TypeEntite typeEntity) {
		adsEntityManager.persist(typeEntity);
	}

	@Override
	public void removeTypeEntity(TypeEntite typeEntity) {
		adsEntityManager.remove(typeEntity);
	}

	@Override
	public void flush() {
		adsEntityManager.flush();
	}

	@Override
	public void clear() {
		adsEntityManager.clear();
	}

	@Override
	public void removeEntiteAvecPersistHisto(Entite entity, EntiteHisto histo) {
		adsEntityManager.persist(histo);
		entity.getEntiteParent().getEntitesEnfants().remove(entity);
		adsEntityManager.remove(entity);
	}
}
