package nc.noumea.mairie.ads.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.ads.domain.TypeNoeud;
import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class ReferenceDataServiceTest {

	@Test
	public void getReferenceDataListTypeNoeud_returnFullList() {

		// Given
		TypeNoeud tn1 = new TypeNoeud();
		tn1.setIdTypeNoeud(45);
		tn1.setLabel("toto");
		TypeNoeud tn2 = new TypeNoeud();
		tn2.setIdTypeNoeud(75);
		tn2.setLabel("titi");

		IAdsRepository aR = Mockito.mock(IAdsRepository.class);
		Mockito.when(aR.getAll(TypeNoeud.class)).thenReturn(Arrays.asList(tn1, tn2));

		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", aR);

		// When
		List<ReferenceDto> result = service.getReferenceDataListTypeNoeud();

		// Then
		assertEquals(2, result.size());
		assertEquals(45, (int) result.get(0).getId());
		assertEquals("toto", result.get(0).getLabel());
		assertEquals(75, (int) result.get(1).getId());
		assertEquals("titi", result.get(1).getLabel());
	}

	@Test
	public void doesTypeNoeudValueAlreadyExists_doesNotExists_ReturnFalse() {

		// Given
		TypeNoeud tn1 = new TypeNoeud();
		tn1.setIdTypeNoeud(45);
		tn1.setLabel("toto");
		TypeNoeud tn2 = new TypeNoeud();
		tn2.setIdTypeNoeud(75);
		tn2.setLabel("titi");

		IAdsRepository aR = Mockito.mock(IAdsRepository.class);
		Mockito.when(aR.getAll(TypeNoeud.class)).thenReturn(Arrays.asList(tn1, tn2));

		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", aR);

		// When & Then
		assertFalse(service.doesTypeNoeudValueAlreadyExists("tutu"));
	}

	@Test
	public void doesTypeNoeudValueAlreadyExists_doesNotExists_ReturnTrue() {

		// Given
		TypeNoeud tn1 = new TypeNoeud();
		tn1.setIdTypeNoeud(45);
		tn1.setLabel("toto");
		TypeNoeud tn2 = new TypeNoeud();
		tn2.setIdTypeNoeud(75);
		tn2.setLabel("tité");

		IAdsRepository aR = Mockito.mock(IAdsRepository.class);
		Mockito.when(aR.getAll(TypeNoeud.class)).thenReturn(Arrays.asList(tn1, tn2));

		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", aR);

		// When & Then
		assertTrue(service.doesTypeNoeudValueAlreadyExists("TiTe"));
	}

	@Test
	public void saveNewTypeNoeud_persistNewTypeNoeud() {

		// Given
		IAdsRepository aR = Mockito.mock(IAdsRepository.class);
		String label = "yoyoy";
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", aR);

		// When
		service.saveNewTypeNoeud(label);
		
		// Then
		Mockito.verify(aR, Mockito.times(1)).persistEntity(Mockito.isA(TypeNoeud.class));
	}

}
