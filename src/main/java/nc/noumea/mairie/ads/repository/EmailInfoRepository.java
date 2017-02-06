package nc.noumea.mairie.ads.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.TypeHistoEnum;

@Repository
public class EmailInfoRepository implements IEmailInfoRepository {

	@PersistenceContext(unitName = "adsPersistenceUnit")
	private EntityManager adsEntityManager;

	@Override
	public List<EntiteHisto> getListeEntiteHistoChangementStatutVeille() {

		String requeteJpa = "select n from EntiteHisto n where n.type = :type and dateHisto > :dateHistoMatin and dateHisto <= :dateHistoSoir order by n.dateHisto desc";
		Query query = adsEntityManager.createQuery(requeteJpa);
		query.setParameter("type", TypeHistoEnum.CHANGEMENT_STATUT);

		DateTime hierMatin = new DateTime(new Date());
		DateTime hierSoir = new DateTime(new Date());
		hierMatin = hierMatin.minusDays(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(1);
		hierSoir = hierSoir.minusDays(1).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
		query.setParameter("dateHistoMatin", hierMatin.toDate());
		query.setParameter("dateHistoSoir", hierSoir.toDate());

		return query.getResultList();
	}

	@Override
	public List<String> getListeDestinataireEmailInfo() {
		TypedQuery<String> q = adsEntityManager.createNamedQuery("getListeDestinataireEmailInfo", String.class);
		return q.getResultList();
	}

	@Override
	public List<String> getListeCopieEmailInfo() {
		TypedQuery<String> q = adsEntityManager.createNamedQuery("getListeCopieEmailInfo", String.class);
		return q.getResultList();
	}

	@Override
	public List<String> getListeCopieCacheeEmailInfo() {
		TypedQuery<String> q = adsEntityManager.createNamedQuery("getListeCopieCacheeEmailInfo", String.class);
		return q.getResultList();
	}

}
