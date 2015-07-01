package nc.noumea.mairie.ads.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.ads.domain.Entite;

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
	public void getEntiteFromSigle_1result() {
		
		// Given
		Entite n1 = new Entite();
		n1.setSigle("sigle");
		adsEntityManager.persist(n1);

		adsEntityManager.flush();

		// When
		Entite result = repository.getEntiteFromSigle("sigle");
		
		// Then
		assertEquals(n1.getIdEntite(), result.getIdEntite());
	}
	
	@Test
	@Transactional("adsTransactionManager")
	public void getEntiteFromSigle_badSigle() {
		
		// Given
		Entite n1 = new Entite();
		n1.setSigle("sigle");
		adsEntityManager.persist(n1);

		adsEntityManager.flush();

		// When
		Entite result = repository.getEntiteFromSigle("sigleError");
		
		// Then
		assertNull(result);
	}
}
