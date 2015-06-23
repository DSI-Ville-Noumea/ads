package nc.noumea.mairie.ads.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

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
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext-test.xml" })
public class RevisionRepositoryTest {

	@Autowired
	RevisionRepository repository;
	
	@PersistenceContext(unitName = "adsPersistenceUnit")
	private EntityManager adsEntityManager;

	@Test
	@Transactional("adsTransactionManager")
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
		assertEquals(rev3.getIdRevision(), result.getIdRevision());

		adsEntityManager.clear();
		adsEntityManager.flush();
	}
	
	@Test
	@Transactional("adsTransactionManager")
	public void getAllRevisionsByDateEffetDesc_returnRevisionsOrderedByDateEffetDesc() {
		
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
		List<Revision> result = repository.getAllRevisionsByDateEffetDesc();
		
		// Then
		assertEquals(rev3, result.get(0));
		assertEquals(rev1, result.get(1));
		assertEquals(rev2, result.get(2));

		adsEntityManager.clear();
		adsEntityManager.flush();
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getLatestRevisionForDate_NoRevision_ReturnNull() {

		// Given
		Date date = new DateTime(2014, 5, 6, 0, 0).toDate();

		// When
		Revision result = repository.getLatestRevisionForDate(date);

		// Then
		assertNull(result);
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getLatestRevisionForDate_1Revision_NotMatching_ReturnNull() {

		// Given
		Revision r1 = new Revision();
		adsEntityManager.persist(r1);
		Date date = new DateTime(2014, 5, 6, 0, 0).toDate();

		// When
		Revision result = repository.getLatestRevisionForDate(date);

		// Then
		assertNull(result);

		adsEntityManager.clear();
		adsEntityManager.flush();
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getLatestRevisionForDate_2Revisions_1Matching_ReturnIt() {

		// Given
		Revision r1 = new Revision();
		r1.setDateEffet(new DateTime(2014, 5, 6, 0, 0).toDate());
		r1.setDateModif(new DateTime(2014, 4, 19, 12, 27, 2).toDate());
		adsEntityManager.persist(r1);

		Revision r2 = new Revision();
		r2.setDateEffet(new DateTime(2014, 5, 9, 0, 0).toDate());
		r2.setDateModif(new DateTime(2014, 4, 22, 12, 27, 2).toDate());
		adsEntityManager.persist(r2);

		Date date = new DateTime(2014, 5, 6, 6, 0, 1).toDate();

		// When
		Revision result = repository.getLatestRevisionForDate(date);

		// Then
		assertEquals(r1, result);

		adsEntityManager.clear();
		adsEntityManager.flush();
	}

	@Test
	@Transactional("adsTransactionManager")
	public void getLatestRevisionForDate_3Revisions_2Matching_ReturnTheNewestOne() {

		// Given
		Revision r1 = new Revision();
		r1.setDateEffet(new DateTime(2014, 5, 6, 0, 0).toDate());
		r1.setDateModif(new DateTime(2014, 4, 19, 12, 27, 2).toDate());
		adsEntityManager.persist(r1);

		Revision r2 = new Revision();
		r2.setDateEffet(new DateTime(2014, 5, 9, 0, 0).toDate());
		r2.setDateModif(new DateTime(2014, 4, 22, 12, 27, 2).toDate());
		adsEntityManager.persist(r2);

		Revision r3 = new Revision();
		r3.setDateEffet(new DateTime(2014, 5, 6, 0, 0).toDate());
		r3.setDateModif(new DateTime(2014, 4, 19, 13, 0, 54).toDate());
		adsEntityManager.persist(r3);

		Date date = new DateTime(2014, 5, 6, 6, 0, 1).toDate();

		// When
		Revision result = repository.getLatestRevisionForDate(date);

		// Then
		assertEquals(r3, result);

		adsEntityManager.clear();
		adsEntityManager.flush();
	}
}
