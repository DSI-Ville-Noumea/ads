package nc.noumea.mairie.ads.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.sirh.domain.Siserv;
import nc.noumea.mairie.sirh.domain.SiservNw;

import org.springframework.stereotype.Repository;

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
	public List<SiservNw> getAllSiservNw() {
		return sirhEntityManager.createQuery("from SiservNw", SiservNw.class).getResultList();
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

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllServiCodes() {
		Query q = sirhEntityManager.createNativeQuery("select servi from siserv");
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
}
