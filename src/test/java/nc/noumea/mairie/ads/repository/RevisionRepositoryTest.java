package nc.noumea.mairie.ads.repository;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.ads.domain.Revision;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/META-INF/spring/applicationContext-test.xml"})
public class RevisionRepositoryTest {

	@Autowired
	RevisionRepository repository;
	
	@PersistenceContext(unitName = "persistenceUnitTest")
	private EntityManager adsEntityManager;
	
	@Test
	@Transactional("transactionManager")
	public void getLatestRevision_1PreviousRevision_ReturnIt() {
		
		// Given
		Revision rev1 = new Revision();
		rev1.setDateEffet(new DateTime(2013, 1, 17, 0, 0, 0, 0).toDate());
		adsEntityManager.persist(rev1);
		
		Revision rev2 = new Revision();
		rev2.setDateEffet(new DateTime(2013, 1, 16, 0, 0, 0, 0).toDate());
		adsEntityManager.persist(rev2);
		
		Revision rev3 = new Revision();
		rev3.setDateEffet(new DateTime(2014, 1, 18, 0, 0, 0, 0).toDate());
		adsEntityManager.persist(rev3);
		
		// When
		Revision result = repository.getLatestRevision();
		
		// Then
		assertEquals(rev1.getIdRevision(), result.getIdRevision());
	}
}
