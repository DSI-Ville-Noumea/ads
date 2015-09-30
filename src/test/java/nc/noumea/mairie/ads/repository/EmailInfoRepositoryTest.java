package nc.noumea.mairie.ads.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.ads.domain.EmailInfo;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeHistoEnum;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext-test.xml" })
public class EmailInfoRepositoryTest {

	@Autowired
	EmailInfoRepository		repository;

	@PersistenceContext(unitName = "adsPersistenceUnit")
	private EntityManager	adsEntityManager;

	@Test
	@Transactional("adsTransactionManager")
	public void getListeEntiteHistoChangementStatutVeille_ok() {

		EntiteHisto histo = new EntiteHisto();
		histo.setIdEntite(1);
		histo.setSigle("sigle");
		histo.setLabel("label");

		DateTime hier = new DateTime(new Date());
		hier = hier.minusDays(1);
		histo.setDateHisto(hier.toDate());

		histo.setStatut(StatutEntiteEnum.INACTIF);
		histo.setType(TypeHistoEnum.CHANGEMENT_STATUT);
		histo.setIdAgentHisto(9005138);
		adsEntityManager.persist(histo);

		List<EntiteHisto> result = repository.getListeEntiteHistoChangementStatutVeille();

		// Then
		assertTrue(!result.isEmpty());
		assertEquals(histo.getIdEntite(), result.get(0).getIdEntite());
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getListeEntiteHistoChangementStatutVeille_ok_bis() {

		EntiteHisto histoChgtStatut = new EntiteHisto();
		histoChgtStatut.setIdEntite(1);
		histoChgtStatut.setSigle("sigle");
		histoChgtStatut.setLabel("label");

		DateTime hier = new DateTime(new Date());
		hier = hier.minusDays(1);
		histoChgtStatut.setDateHisto(hier.toDate());

		histoChgtStatut.setStatut(StatutEntiteEnum.INACTIF);
		histoChgtStatut.setType(TypeHistoEnum.CHANGEMENT_STATUT);
		histoChgtStatut.setIdAgentHisto(9005138);
		adsEntityManager.persist(histoChgtStatut);

		EntiteHisto histoModification = new EntiteHisto();
		histoModification.setIdEntite(1);
		histoModification.setSigle("sigle");
		histoModification.setLabel("label");
		histoModification.setDateHisto(hier.toDate());
		histoModification.setStatut(StatutEntiteEnum.ACTIF);
		histoModification.setType(TypeHistoEnum.MODIFICATION);
		histoModification.setIdAgentHisto(9005138);
		adsEntityManager.persist(histoModification);

		List<EntiteHisto> result = repository.getListeEntiteHistoChangementStatutVeille();

		// Then
		assertTrue(!result.isEmpty());
		assertEquals(1, result.size());
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getListeEntiteHistoChangementStatutVeille_ko_bad_date() {

		EntiteHisto histo = new EntiteHisto();
		histo.setIdEntite(1);
		histo.setSigle("sigle");
		histo.setLabel("label");

		DateTime hier = new DateTime(new Date());
		hier = hier.minusDays(5);
		histo.setDateHisto(hier.toDate());

		histo.setStatut(StatutEntiteEnum.INACTIF);
		histo.setType(TypeHistoEnum.CHANGEMENT_STATUT);
		histo.setIdAgentHisto(9005138);
		adsEntityManager.persist(histo);

		List<EntiteHisto> result = repository.getListeEntiteHistoChangementStatutVeille();

		// Then
		assertTrue(result.isEmpty());
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getListeEntiteHistoChangementStatutVeille_empty_list() {

		EntiteHisto histo = new EntiteHisto();
		histo.setIdEntite(1);
		histo.setSigle("sigle");
		histo.setLabel("label");

		DateTime hier = new DateTime(new Date());
		hier = hier.minusDays(1);
		histo.setDateHisto(hier.toDate());

		histo.setStatut(StatutEntiteEnum.INACTIF);
		histo.setType(TypeHistoEnum.CREATION);
		histo.setIdAgentHisto(9005138);
		adsEntityManager.persist(histo);

		List<EntiteHisto> result = repository.getListeEntiteHistoChangementStatutVeille();

		// Then
		assertTrue(result.isEmpty());
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getListeIdAgentEmailInfo_ok() {

		EmailInfo emailInfo = new EmailInfo();
		emailInfo.setIdAgent(9005138);
		emailInfo.setActif(true);
		adsEntityManager.persist(emailInfo);

		List<Integer> result = repository.getListeIdAgentEmailInfo();

		// Then
		assertTrue(!result.isEmpty());
		assertEquals(emailInfo.getIdAgent(), result.get(0));
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getListeIdAgentEmailInfo_ok_bis() {

		EmailInfo emailInfo = new EmailInfo();
		emailInfo.setIdAgent(9005138);
		emailInfo.setActif(true);
		adsEntityManager.persist(emailInfo);

		EmailInfo emailInfo2 = new EmailInfo();
		emailInfo2.setIdAgent(9002990);
		emailInfo2.setActif(true);
		adsEntityManager.persist(emailInfo2);

		EmailInfo emailInfo3 = new EmailInfo();
		emailInfo3.setIdAgent(9002991);
		emailInfo3.setActif(false);
		adsEntityManager.persist(emailInfo3);

		List<Integer> result = repository.getListeIdAgentEmailInfo();

		// Then
		assertTrue(!result.isEmpty());
		assertEquals(2, result.size());
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getListeIdAgentEmailInfo_empty_list() {

		EmailInfo emailInfo = new EmailInfo();
		emailInfo.setIdAgent(9002991);
		emailInfo.setActif(false);
		adsEntityManager.persist(emailInfo);

		List<Integer> result = repository.getListeIdAgentEmailInfo();

		// Then
		assertTrue(result.isEmpty());
	}
}
