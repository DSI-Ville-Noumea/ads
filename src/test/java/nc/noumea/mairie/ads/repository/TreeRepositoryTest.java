package nc.noumea.mairie.ads.repository;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeHistoEnum;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext-test.xml" })
public class TreeRepositoryTest {

	@Autowired
	TreeRepository repository;
	
	@PersistenceContext(unitName = "adsPersistenceUnit")
	private EntityManager adsEntityManager;
		
	//@Test
	@Transactional("adsTransactionManager")
	public void getLatestRevision_1PreviousRevision_ReturnIt() {
		
		// Given
		Entite n1 = new Entite();
		adsEntityManager.persist(n1);
		
		Entite n2 = new Entite();
		n2.addParent(n1);
		adsEntityManager.persist(n2);
		
		Entite n3 = new Entite();
		n3.addParent(n1);
		adsEntityManager.persist(n3);
		
		Entite n4 = new Entite();
		n4.addParent(n3);
		adsEntityManager.persist(n4);
		
		Entite n5 = new Entite();
		adsEntityManager.persist(n5);
		
		Entite n6 = new Entite();
		n6.setEntiteParent(n5);
		adsEntityManager.persist(n6);

		adsEntityManager.flush();

		// When
		List<Entite> result = repository.getWholeTree();
		
		// Then
		assertEquals(4, result.size());
		assertEquals(n1.getIdEntite(), result.get(0).getIdEntite());
		assertEquals(n2.getIdEntite(), result.get(1).getIdEntite());
		assertEquals(n3.getIdEntite(), result.get(2).getIdEntite());
		assertEquals(n4.getIdEntite(), result.get(3).getIdEntite());
	}
	
	@Test
	@Transactional("adsTransactionManager")
	public void getEntiteFromIdEntite_1result() {
		
		// Given
		Entite n1 = new Entite();
		n1.setSigle("sigle");
		n1.setLabel("label");
		adsEntityManager.persist(n1);

		adsEntityManager.flush();

		// When
		Entite result = repository.getEntiteFromIdEntite(n1.getIdEntite());
		
		// Then
		assertEquals(n1.getIdEntite(), result.getIdEntite());
	}
	
	@Test
	@Transactional("adsTransactionManager")
	public void getEntiteFromIdEntite_badSigle() {
		
		// Given
		Entite n1 = new Entite();
		n1.setSigle("sigle");
		n1.setLabel("label");
		adsEntityManager.persist(n1);

		adsEntityManager.flush();

		// When
		Entite result = repository.getEntiteFromIdEntite(n1.getIdEntite()+1);
		
		// Then
		assertNull(result);
	}
	
	@Test
	@Transactional("adsTransactionManager")
	public void getEntiteFromSigle_1result() {
		
		// Given
		Entite n1 = new Entite();
		n1.setSigle("sigle");
		n1.setLabel("label");
		n1.setStatut(StatutEntiteEnum.ACTIF);
		adsEntityManager.persist(n1);

		adsEntityManager.flush();

		// When
		Entite result = repository.getEntiteActiveFromSigle("sigle");
		
		// Then
		assertEquals(n1.getIdEntite(), result.getIdEntite());
	}
	
	@Test
	@Transactional("adsTransactionManager")
	public void getEntiteFromSigle_noResult_EntityDisable() {
		
		// Given
		Entite n1 = new Entite();
		n1.setSigle("sigle");
		n1.setLabel("label");
		n1.setStatut(StatutEntiteEnum.INACTIF);
		adsEntityManager.persist(n1);

		adsEntityManager.flush();

		// When
		Entite result = repository.getEntiteActiveFromSigle("sigle");
		
		// Then
		assertNull(result);
	}
	
	@Test
	@Transactional("adsTransactionManager")
	public void getEntiteFromSigle_badSigle() {
		
		// Given
		Entite n1 = new Entite();
		n1.setSigle("sigle");
		n1.setLabel("label");
		n1.setStatut(StatutEntiteEnum.ACTIF);
		adsEntityManager.persist(n1);

		adsEntityManager.flush();

		// When
		Entite result = repository.getEntiteActiveFromSigle("sigleError");
		
		// Then
		assertNull(result);
	}
	
	@Test
	@Transactional("adsTransactionManager")
	public void getEntiteFromCodeServi_1result() {
		
		// Given
		Entite n1 = new Entite();
		
		SiservInfo siservInfo = new SiservInfo();
		siservInfo.setEntite(n1);
		siservInfo.setCodeServi("DCAA");
		
		n1.setSigle("sigle");
		n1.setLabel("label");
		n1.setSiservInfo(siservInfo);
		adsEntityManager.persist(n1);
		adsEntityManager.persist(siservInfo);

		adsEntityManager.flush();

		// When
		Entite result = repository.getEntiteFromCodeServi("DCAA");
		
		// Then
		assertEquals(n1.getIdEntite(), result.getIdEntite());
	}
	
	@Test
	@Transactional("adsTransactionManager")
	public void getEntiteFromCodeServi_badCodeServi() {
		
		// Given
		Entite n1 = new Entite();
		
		SiservInfo siservInfo = new SiservInfo();
		siservInfo.setEntite(n1);
		siservInfo.setCodeServi("DCAA");
		
		n1.setSigle("sigle");
		n1.setLabel("label");
		n1.setSiservInfo(siservInfo);
		adsEntityManager.persist(n1);
		adsEntityManager.persist(siservInfo);

		adsEntityManager.flush();

		// When
		Entite result = repository.getEntiteFromCodeServi("ERROR");
		
		// Then
		assertNull(result);
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getListEntiteHistoByIdEntite_ko() {
		
		EntiteHisto histo = new EntiteHisto();
		histo.setIdEntite(1);
		histo.setSigle("sigle");
		histo.setLabel("label");
		histo.setDateHisto(new Date());
		histo.setStatut(StatutEntiteEnum.INACTIF);
		histo.setType(TypeHistoEnum.CREATION);
		histo.setIdAgentHisto(9005138);
		adsEntityManager.persist(histo);
		
		// When
		List<EntiteHisto> result = repository.getListEntiteHistoByIdEntite(histo.getIdEntite()+1);
		
		// Then
		assertTrue(result.isEmpty());
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getListEntiteHistoByIdEntite_ok() {
		
		EntiteHisto histo = new EntiteHisto();
		histo.setIdEntite(1);
		histo.setSigle("sigle");
		histo.setLabel("label");
		histo.setDateHisto(new Date());
		histo.setStatut(StatutEntiteEnum.INACTIF);
		histo.setType(TypeHistoEnum.CREATION);
		histo.setIdAgentHisto(9005138);
		adsEntityManager.persist(histo);
		
		// When
		List<EntiteHisto> result = repository.getListEntiteHistoByIdEntite(histo.getIdEntite());
		
		// Then
		assertTrue(!result.isEmpty());
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getListEntityByStatut_returnList() {
		
		Entite entite = new Entite();
		entite.setIdEntite(1);
		entite.setSigle("sigle");
		entite.setLabel("label");
		entite.setStatut(StatutEntiteEnum.INACTIF);
		adsEntityManager.persist(entite);
		
		Entite entite2 = new Entite();
		entite2.setIdEntite(2);
		entite2.setSigle("sigle2");
		entite2.setLabel("label2");
		entite2.setStatut(StatutEntiteEnum.ACTIF);
		adsEntityManager.persist(entite2);
		
		// When
		List<Entite> result = repository.getListEntityByStatut(StatutEntiteEnum.ACTIF);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(new Integer(2), result.get(0).getIdEntite());
		assertEquals("SIGLE2", result.get(0).getSigle());
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getListEntityByStatut_EmptyList() {
		
		Entite entite2 = new Entite();
		entite2.setIdEntite(2);
		entite2.setSigle("sigle");
		entite2.setLabel("label");
		entite2.setStatut(StatutEntiteEnum.PREVISION);
		adsEntityManager.persist(entite2);
		
		Entite entite = new Entite();
		entite.setIdEntite(1);
		entite.setSigle("sigle");
		entite.setLabel("label");
		entite.setStatut(StatutEntiteEnum.INACTIF);
		adsEntityManager.persist(entite);
		
		// When
		List<Entite> result = repository.getListEntityByStatut(StatutEntiteEnum.ACTIF);
		
		// Then
		assertNotNull(result);
		assertEquals(0, result.size());
	}
}
