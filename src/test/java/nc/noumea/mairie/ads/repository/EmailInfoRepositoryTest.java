package nc.noumea.mairie.ads.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import nc.noumea.mairie.ads.domain.EmailInfo;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeHistoEnum;

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
	public void getListeEntiteHistoChangementStatutVeille_ok_WithOrder() {

		DateTime hierPremier = new DateTime(new Date());
		hierPremier = hierPremier.minusDays(1).withHourOfDay(9);

		DateTime hierDeuxieme = new DateTime(new Date());
		hierDeuxieme = hierDeuxieme.minusDays(1).withHourOfDay(14);

		// 1ere entite
		EntiteHisto histoChgtStatutAutreEntite = new EntiteHisto();
		histoChgtStatutAutreEntite.setIdEntite(2);
		histoChgtStatutAutreEntite.setSigle("sigle2");
		histoChgtStatutAutreEntite.setLabel("label2");
		histoChgtStatutAutreEntite.setDateHisto(hierPremier.toDate());
		histoChgtStatutAutreEntite.setStatut(StatutEntiteEnum.INACTIF);
		histoChgtStatutAutreEntite.setType(TypeHistoEnum.CHANGEMENT_STATUT);
		histoChgtStatutAutreEntite.setIdAgentHisto(9005138);
		adsEntityManager.persist(histoChgtStatutAutreEntite);

		// 2eme entite avec +sieur histo		
		EntiteHisto histoChgtStatut1 = new EntiteHisto();
		histoChgtStatut1.setIdEntite(1);
		histoChgtStatut1.setSigle("sigle");
		histoChgtStatut1.setLabel("label");
		histoChgtStatut1.setDateHisto(hierPremier.toDate());
		histoChgtStatut1.setStatut(StatutEntiteEnum.TRANSITOIRE);
		histoChgtStatut1.setType(TypeHistoEnum.CHANGEMENT_STATUT);
		histoChgtStatut1.setIdAgentHisto(9005138);
		adsEntityManager.persist(histoChgtStatut1);
		
		EntiteHisto histoChgtStatut = new EntiteHisto();
		histoChgtStatut.setIdEntite(1);
		histoChgtStatut.setSigle("sigle");
		histoChgtStatut.setLabel("label");
		histoChgtStatut.setDateHisto(hierDeuxieme.toDate());
		histoChgtStatut.setStatut(StatutEntiteEnum.INACTIF);
		histoChgtStatut.setType(TypeHistoEnum.CHANGEMENT_STATUT);
		histoChgtStatut.setIdAgentHisto(9005138);
		adsEntityManager.persist(histoChgtStatut);

		EntiteHisto histoModification = new EntiteHisto();
		histoModification.setIdEntite(1);
		histoModification.setSigle("sigle");
		histoModification.setLabel("label");
		histoModification.setDateHisto(hierPremier.toDate());
		histoModification.setStatut(StatutEntiteEnum.ACTIF);
		histoModification.setType(TypeHistoEnum.MODIFICATION);
		histoModification.setIdAgentHisto(9005138);
		adsEntityManager.persist(histoModification);

		List<EntiteHisto> result = repository.getListeEntiteHistoChangementStatutVeille();

		// Then
		assertTrue(!result.isEmpty());
		assertEquals(3, result.size());
		assertEquals(histoChgtStatut.getIdEntite(), result.get(0).getIdEntite());
		assertEquals(histoChgtStatut.getDateHisto(), result.get(0).getDateHisto());
		assertEquals(histoChgtStatut1.getIdEntite(), result.get(1).getIdEntite());
		assertEquals(histoChgtStatut1.getDateHisto(), result.get(1).getDateHisto());
		assertEquals(histoChgtStatutAutreEntite.getIdEntite(), result.get(2).getIdEntite());
		assertEquals(histoChgtStatutAutreEntite.getDateHisto(), result.get(2).getDateHisto());
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
	public void getListeDestinataireEmailInfo_emptyList() {

		EmailInfo emailInfoCopieCachee = new EmailInfo();
		emailInfoCopieCachee.setMail("test@nono");
		emailInfoCopieCachee.setDestinataire(false);
		emailInfoCopieCachee.setCopie(false);
		emailInfoCopieCachee.setCopieCachee(true);
		adsEntityManager.persist(emailInfoCopieCachee);

		EmailInfo emailInfoCopie = new EmailInfo();
		emailInfoCopie.setMail("test@nono");
		emailInfoCopie.setDestinataire(false);
		emailInfoCopie.setCopie(true);
		emailInfoCopie.setCopieCachee(false);
		adsEntityManager.persist(emailInfoCopie);

		List<String> result = repository.getListeDestinataireEmailInfo();

		// Then
		assertTrue(result.isEmpty());
		assertNotNull(result);
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getListeDestinataireEmailInfo_ok() {

		EmailInfo emailInfoDest2 = new EmailInfo();
		emailInfoDest2.setMail("test@nono");
		emailInfoDest2.setDestinataire(true);
		emailInfoDest2.setCopie(false);
		emailInfoDest2.setCopieCachee(false);
		adsEntityManager.persist(emailInfoDest2);

		EmailInfo emailInfoDest = new EmailInfo();
		emailInfoDest.setMail("test@nono");
		emailInfoDest.setDestinataire(true);
		emailInfoDest.setCopie(false);
		emailInfoDest.setCopieCachee(false);
		adsEntityManager.persist(emailInfoDest);

		EmailInfo emailInfoCopie = new EmailInfo();
		emailInfoCopie.setMail("test@nono");
		emailInfoCopie.setDestinataire(false);
		emailInfoCopie.setCopie(true);
		emailInfoCopie.setCopieCachee(false);
		adsEntityManager.persist(emailInfoCopie);

		List<String> result = repository.getListeDestinataireEmailInfo();

		// Then
		assertFalse(result.isEmpty());
		assertNotNull(result);
		assertEquals(2, result.size());
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getListeCopieEmailInfo_emptyList() {

		EmailInfo emailInfoCopieCachee = new EmailInfo();
		emailInfoCopieCachee.setMail("test@nono");
		emailInfoCopieCachee.setDestinataire(false);
		emailInfoCopieCachee.setCopie(false);
		emailInfoCopieCachee.setCopieCachee(true);
		adsEntityManager.persist(emailInfoCopieCachee);

		EmailInfo emailInfoDest = new EmailInfo();
		emailInfoDest.setMail("test@nono");
		emailInfoDest.setDestinataire(true);
		emailInfoDest.setCopie(false);
		emailInfoDest.setCopieCachee(false);
		adsEntityManager.persist(emailInfoDest);

		List<String> result = repository.getListeCopieEmailInfo();

		// Then
		assertTrue(result.isEmpty());
		assertNotNull(result);
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getListeCopieEmailInfo_ok() {

		EmailInfo emailInfoDest2 = new EmailInfo();
		emailInfoDest2.setMail("test@nono");
		emailInfoDest2.setDestinataire(true);
		emailInfoDest2.setCopie(false);
		emailInfoDest2.setCopieCachee(false);
		adsEntityManager.persist(emailInfoDest2);

		EmailInfo emailInfoDest = new EmailInfo();
		emailInfoDest.setMail("test@nono");
		emailInfoDest.setDestinataire(true);
		emailInfoDest.setCopie(false);
		emailInfoDest.setCopieCachee(false);
		adsEntityManager.persist(emailInfoDest);

		EmailInfo emailInfoCopie2 = new EmailInfo();
		emailInfoCopie2.setMail("test@nono");
		emailInfoCopie2.setDestinataire(false);
		emailInfoCopie2.setCopie(true);
		emailInfoCopie2.setCopieCachee(false);
		adsEntityManager.persist(emailInfoCopie2);

		EmailInfo emailInfoCopie = new EmailInfo();
		emailInfoCopie.setMail("test@nono");
		emailInfoCopie.setDestinataire(false);
		emailInfoCopie.setCopie(true);
		emailInfoCopie.setCopieCachee(false);
		adsEntityManager.persist(emailInfoCopie);

		List<String> result = repository.getListeCopieEmailInfo();

		// Then
		assertFalse(result.isEmpty());
		assertNotNull(result);
		assertEquals(2, result.size());
	}


	@Test
	@Transactional("adsTransactionManager")
	public void getListeCopieCacheeEmailInfo_emptyList() {

		EmailInfo emailInfoDest = new EmailInfo();
		emailInfoDest.setMail("test@nono");
		emailInfoDest.setDestinataire(true);
		emailInfoDest.setCopie(false);
		emailInfoDest.setCopieCachee(false);
		adsEntityManager.persist(emailInfoDest);

		EmailInfo emailInfoCopie = new EmailInfo();
		emailInfoCopie.setMail("test@nono");
		emailInfoCopie.setDestinataire(false);
		emailInfoCopie.setCopie(true);
		emailInfoCopie.setCopieCachee(false);
		adsEntityManager.persist(emailInfoCopie);

		List<String> result = repository.getListeCopieCacheeEmailInfo();

		// Then
		assertTrue(result.isEmpty());
		assertNotNull(result);
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getListeCopieCacheeEmailInfo_ok() {

		EmailInfo emailInfoDest2 = new EmailInfo();
		emailInfoDest2.setMail("test@nono");
		emailInfoDest2.setDestinataire(true);
		emailInfoDest2.setCopie(false);
		emailInfoDest2.setCopieCachee(false);
		adsEntityManager.persist(emailInfoDest2);

		EmailInfo emailInfoDest = new EmailInfo();
		emailInfoDest.setMail("test@nono");
		emailInfoDest.setDestinataire(true);
		emailInfoDest.setCopie(false);
		emailInfoDest.setCopieCachee(false);
		adsEntityManager.persist(emailInfoDest);

		EmailInfo emailInfoCopieCachee2 = new EmailInfo();
		emailInfoCopieCachee2.setMail("test@nono");
		emailInfoCopieCachee2.setDestinataire(false);
		emailInfoCopieCachee2.setCopie(true);
		emailInfoCopieCachee2.setCopieCachee(true);
		adsEntityManager.persist(emailInfoCopieCachee2);

		EmailInfo emailInfoCopieCachee = new EmailInfo();
		emailInfoCopieCachee.setMail("test@nono");
		emailInfoCopieCachee.setDestinataire(false);
		emailInfoCopieCachee.setCopie(true);
		emailInfoCopieCachee.setCopieCachee(true);
		adsEntityManager.persist(emailInfoCopieCachee);

		EmailInfo emailInfoCopie2 = new EmailInfo();
		emailInfoCopie2.setMail("test@nono");
		emailInfoCopie2.setDestinataire(false);
		emailInfoCopie2.setCopie(true);
		emailInfoCopie2.setCopieCachee(false);
		adsEntityManager.persist(emailInfoCopie2);

		EmailInfo emailInfoCopie = new EmailInfo();
		emailInfoCopie.setMail("test@nono");
		emailInfoCopie.setDestinataire(false);
		emailInfoCopie.setCopie(true);
		emailInfoCopie.setCopieCachee(false);
		adsEntityManager.persist(emailInfoCopie);

		List<String> result = repository.getListeCopieCacheeEmailInfo();

		// Then
		assertFalse(result.isEmpty());
		assertNotNull(result);
		assertEquals(2, result.size());
	}
}
