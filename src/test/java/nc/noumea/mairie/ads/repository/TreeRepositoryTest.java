package nc.noumea.mairie.ads.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;

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
		Revision rev = new Revision();
		adsEntityManager.persist(rev);
		
		Noeud n1 = new Noeud();
		n1.setRevision(rev);
		n1.setIdService(1);
		adsEntityManager.persist(n1);
		
		Noeud n2 = new Noeud();
		n2.setRevision(rev);
		n2.addParent(n1);
		n2.setIdService(2);
		adsEntityManager.persist(n2);
		
		Noeud n3 = new Noeud();
		n3.setRevision(rev);
		n3.addParent(n1);
		n3.setIdService(3);
		adsEntityManager.persist(n3);
		
		Noeud n4 = new Noeud();
		n4.setRevision(rev);
		n4.addParent(n3);
		n4.setIdService(4);
		adsEntityManager.persist(n4);
		
		Revision rev2 = new Revision();
		adsEntityManager.persist(rev2);
		
		Noeud n5 = new Noeud();
		n5.setRevision(rev2);
		adsEntityManager.persist(n5);
		
		Noeud n6 = new Noeud();
		n6.setRevision(rev2);
		n6.setNoeudParent(n5);
		adsEntityManager.persist(n6);

		adsEntityManager.flush();

		// When
		List<Noeud> result = repository.getWholeTreeForRevision(1l);
		
		// Then
		assertEquals(4, result.size());
		assertEquals(n1.getIdNoeud(), result.get(0).getIdNoeud());
		assertEquals(n2.getIdNoeud(), result.get(1).getIdNoeud());
		assertEquals(n3.getIdNoeud(), result.get(2).getIdNoeud());
		assertEquals(n4.getIdNoeud(), result.get(3).getIdNoeud());
	}
	
	@Test
	@Transactional("adsTransactionManager")
	public void getNoeudFromSigle_1result() {
		
		// Given
		Revision rev = new Revision();
		adsEntityManager.persist(rev);
		
		Noeud n1 = new Noeud();
		n1.setRevision(rev);
		n1.setIdService(1);
		n1.setSigle("sigle");
		adsEntityManager.persist(n1);

		adsEntityManager.flush();

		// When
		Noeud result = repository.getNoeudFromSigle("sigle", rev.getIdRevision());
		
		// Then
		assertEquals(n1.getIdNoeud(), result.getIdNoeud());
	}
	
	@Test
	@Transactional("adsTransactionManager")
	public void getNoeudFromSigle_badRevision() {
		
		// Given
		Revision rev = new Revision();
		adsEntityManager.persist(rev);
		
		Noeud n1 = new Noeud();
		n1.setRevision(rev);
		n1.setIdService(1);
		n1.setSigle("sigle");
		adsEntityManager.persist(n1);

		adsEntityManager.flush();

		// When
		Noeud result = repository.getNoeudFromSigle("sigle", rev.getIdRevision()+1);
		
		// Then
		assertNull(result);
	}
	
	@Test
	@Transactional("adsTransactionManager")
	public void getNoeudFromSigle_badSigle() {
		
		// Given
		Revision rev = new Revision();
		adsEntityManager.persist(rev);
		
		Noeud n1 = new Noeud();
		n1.setRevision(rev);
		n1.setIdService(1);
		n1.setSigle("sigle");
		adsEntityManager.persist(n1);

		adsEntityManager.flush();

		// When
		Noeud result = repository.getNoeudFromSigle("sigleError", rev.getIdRevision());
		
		// Then
		assertNull(result);
	}
}
