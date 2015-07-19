package nc.noumea.mairie.ads.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.Siserv;
import nc.noumea.mairie.domain.SiservNw;

import org.springframework.stereotype.Repository;

@Repository
public class MairieRepository implements IMairieRepository {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager sirhEntityManager;

	@Override
	public List<SiservNw> getAllSiservNw() {
		return sirhEntityManager.createQuery("from SiservNw", SiservNw.class).getResultList();
	}

	@Override
	public void persist(Object entity) {
		sirhEntityManager.persist(entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllServiCodes() {
		Query q = sirhEntityManager.createNativeQuery("select servi from siservnw");
		return q.getResultList();
	}

	@Override
	public Siserv getSiservByCode(String codeAS400) {

		TypedQuery<Siserv> q = sirhEntityManager.createNamedQuery("getSiservFromCodeServi", Siserv.class);
		q.setParameter("servi", codeAS400);

		List<Siserv> result = q.getResultList();

		if (result.size() > 0)
			return result.get(0);
		else
			return null;
	}

	@Override
	public SiservNw getSiservNwByCode(String codeAS400) {

		TypedQuery<SiservNw> q = sirhEntityManager.createNamedQuery("getSiservNwFromCodeServi", SiservNw.class);
		q.setParameter("servi", codeAS400);

		List<SiservNw> result = q.getResultList();

		if (result.size() > 0)
			return result.get(0);
		else
			return null;
	}
}
