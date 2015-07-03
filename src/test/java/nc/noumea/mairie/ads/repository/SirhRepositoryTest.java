package nc.noumea.mairie.ads.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.sirh.domain.Siserv;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext-test.xml" })
public class SirhRepositoryTest {

	@Autowired
	SirhRepository repository;

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager sirhEntityManager;

	@Test
	@Transactional("sirhTransactionManager")
	public void getSiservByCode_ReturnNull() {

		// Given
		Siserv n1 = new Siserv();
		n1.setServi("a");
		n1.setCodeActif("");
		n1.setLi22("li22");
		n1.setLiServ("liServ");
		n1.setParentSigle("parentSigle");
		n1.setSigle("sigle");
		sirhEntityManager.persist(n1);

		sirhEntityManager.flush();

		// When
		Siserv result = repository.getSiservByCode("toto");

		// Then
		assertNull(result);
	}

	@Test
	@Transactional("sirhTransactionManager")
	public void getSiservByCode_ReturnSiserv() {

		// Given
		Siserv n1 = new Siserv();
		n1.setServi("a");
		n1.setCodeActif("");
		n1.setLi22("li22");
		n1.setLiServ("liServ");
		n1.setParentSigle("parentSigle");
		n1.setSigle("sigle");
		sirhEntityManager.persist(n1);

		sirhEntityManager.flush();

		// When
		Siserv result = repository.getSiservByCode("a");

		// Then
		assertNotNull(result);
		assertEquals("a", result.getServi());
	}

}
