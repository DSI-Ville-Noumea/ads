package nc.noumea.mairie.ads.service;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.ads.domain.TypeEntite;
import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class ReferenceDataServiceTest {

	@Test
	public void getReferenceDataListTypeNoeud_returnFullList() {

		// Given
		TypeEntite tn1 = new TypeEntite();
		tn1.setIdTypeEntite(45);
		tn1.setLabel("toto");
		TypeEntite tn2 = new TypeEntite();
		tn2.setIdTypeEntite(75);
		tn2.setLabel("titi");

		IAdsRepository aR = Mockito.mock(IAdsRepository.class);
		Mockito.when(aR.getAll(TypeEntite.class)).thenReturn(Arrays.asList(tn1, tn2));

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
		TypeEntite tn1 = new TypeEntite();
		tn1.setIdTypeEntite(45);
		tn1.setLabel("toto");
		TypeEntite tn2 = new TypeEntite();
		tn2.setIdTypeEntite(75);
		tn2.setLabel("titi");

		IAdsRepository aR = Mockito.mock(IAdsRepository.class);
		Mockito.when(aR.getAll(TypeEntite.class)).thenReturn(Arrays.asList(tn1, tn2));

		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", aR);

		// When & Then
		assertFalse(service.doesTypeNoeudValueAlreadyExists("tutu"));
	}

	@Test
	public void doesTypeNoeudValueAlreadyExists_doesNotExists_ReturnTrue() {

		// Given
		TypeEntite tn1 = new TypeEntite();
		tn1.setIdTypeEntite(45);
		tn1.setLabel("toto");
		TypeEntite tn2 = new TypeEntite();
		tn2.setIdTypeEntite(75);
		tn2.setLabel("tité");

		IAdsRepository aR = Mockito.mock(IAdsRepository.class);
		Mockito.when(aR.getAll(TypeEntite.class)).thenReturn(Arrays.asList(tn1, tn2));

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
		Mockito.verify(aR, Mockito.times(1)).persistEntity(Mockito.isA(TypeEntite.class));
	}
	
	@Test 
	public void createOrModifyTypeNoeud_modify() {
		
		ReferenceDto dto = new ReferenceDto();
		dto.setId(2);
		dto.setLabel("label");
		dto.setActif(true);
		
		TypeEntite type = Mockito.spy(new TypeEntite());
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, dto.getId())).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		service.createOrModifyTypeNoeud(dto);

		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(type);
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(TypeEntite.class));
	}
	
	@Test 
	public void createOrModifyTypeNoeud_create() {
		
		ReferenceDto dto = new ReferenceDto();
		dto.setId(null);
		dto.setLabel("label");
		dto.setActif(true);
		
		TypeEntite type = Mockito.spy(new TypeEntite());
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, dto.getId())).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		service.createOrModifyTypeNoeud(dto);

		Mockito.verify(adsRepository, Mockito.never()).persistEntity(type);
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(TypeEntite.class));
	}
	
	@Test 
	public void createOrModifyTypeNoeud_notFound() {
		
		ReferenceDto dto = new ReferenceDto();
		dto.setId(3);
		dto.setLabel("label");
		dto.setActif(true);
		
		TypeEntite type = Mockito.spy(new TypeEntite());
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, dto.getId())).thenReturn(null);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		try {
			service.createOrModifyTypeNoeud(dto);
		} catch(TypeNoeudNotFoundException e) {
			Mockito.verify(adsRepository, Mockito.never()).persistEntity(type);
			Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(TypeEntite.class));
			return;
		}

		fail("KO");
	}
	
	@Test 
	public void getTypeNoeudById() {
		
		TypeEntite type = new TypeEntite();
		type.setIdTypeEntite(3);
		type.setLabel("label");
		type.setActif(true);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, 3)).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReferenceDto result = service.getTypeNoeudById(3);
		
		assertEquals(result.getId().intValue(), type.getIdTypeEntite());
		assertEquals(result.getLabel(), type.getLabel());
		assertEquals(result.isActif(), type.isActif());
	}
	
	@Test 
	public void deleteTypeNoeudById() {
		
		TypeEntite type = new TypeEntite();
		type.setIdTypeEntite(3);
		type.setLabel("label");
		type.setActif(true);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, 3)).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		service.deleteTypeNoeudById(3);
		
		Mockito.verify(adsRepository, Mockito.times(1)).removeEntity(Mockito.isA(TypeEntite.class));
	}
	
	@Test 
	public void deleteTypeNoeudById_notFound() {
		
		TypeEntite type = new TypeEntite();
		type.setIdTypeEntite(3);
		type.setLabel("label");
		type.setActif(true);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, 3)).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		try {
			service.deleteTypeNoeudById(2);
		} catch(TypeNoeudNotFoundException e) {
			Mockito.verify(adsRepository, Mockito.never()).removeEntity(Mockito.isA(TypeEntite.class));
			return;
		}
	
		fail("KO");
	}
	
	@Test 
	public void disableTypeNoeudById() {
		
		TypeEntite type = Mockito.spy(new TypeEntite());
		type.setIdTypeEntite(3);
		type.setLabel("label");
		type.setActif(true);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, 3)).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		service.disableTypeNoeudById(3);
		
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(TypeEntite.class));
		assertFalse(type.isActif());
	}
	
	@Test 
	public void disableTypeNoeudById_notFound() {
		
		TypeEntite type = new TypeEntite();
		type.setIdTypeEntite(3);
		type.setLabel("label");
		type.setActif(true);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, 3)).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		try {
			service.disableTypeNoeudById(2);
		} catch(TypeNoeudNotFoundException e) {
			Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(TypeEntite.class));
			assertTrue(type.isActif());
			return;
		}
	
		fail("KO");
	}

}
