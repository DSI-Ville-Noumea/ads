package nc.noumea.mairie.ads.repository;

import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.sirh.domain.Siserv;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class SirhRepository implements ISirhRepository {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager sirhEntityManager;

	@Override
	public Agent getAgent(Integer idAgent) {
		return sirhEntityManager.find(Agent.class, idAgent);
	}

	@Override
	public List<Siserv> getAllSiserv() {
		return sirhEntityManager.createQuery("from Siserv", Siserv.class).getResultList();
	}

	@Override
	public void persist(Object entity) {
		sirhEntityManager.persist(entity);
	}

	@Override
	public void deleteAllSiservAds() {
		sirhEntityManager.createQuery("delete from SiservAds").executeUpdate();
	}

	@Override
	public void flush() {
		sirhEntityManager.flush();
	}

	@Override
	public List<String> getAllServiCodes() {
		Query q = sirhEntityManager.createNativeQuery("select servi from siserv");
		return q.getResultList();
	}
}
