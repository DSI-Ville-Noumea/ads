package nc.noumea.mairie.ads.service;

import static org.junit.Assert.*;

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
	
	@Test 
	public void createOrModifyTypeNoeud_modify() {
		
		ReferenceDto dto = new ReferenceDto();
		dto.setId(2);
		dto.setLabel("label");
		dto.setActif(true);
		
		TypeNoeud type = Mockito.spy(new TypeNoeud());
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeNoeud.class, dto.getId())).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		service.createOrModifyTypeNoeud(dto);

		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(type);
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(TypeNoeud.class));
	}
	
	@Test 
	public void createOrModifyTypeNoeud_create() {
		
		ReferenceDto dto = new ReferenceDto();
		dto.setId(null);
		dto.setLabel("label");
		dto.setActif(true);
		
		TypeNoeud type = Mockito.spy(new TypeNoeud());
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeNoeud.class, dto.getId())).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		service.createOrModifyTypeNoeud(dto);

		Mockito.verify(adsRepository, Mockito.never()).persistEntity(type);
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(TypeNoeud.class));
	}
	
	@Test 
	public void createOrModifyTypeNoeud_notFound() {
		
		ReferenceDto dto = new ReferenceDto();
		dto.setId(3);
		dto.setLabel("label");
		dto.setActif(true);
		
		TypeNoeud type = Mockito.spy(new TypeNoeud());
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeNoeud.class, dto.getId())).thenReturn(null);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		try {
			service.createOrModifyTypeNoeud(dto);
		} catch(TypeNoeudNotFoundException e) {
			Mockito.verify(adsRepository, Mockito.never()).persistEntity(type);
			Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(TypeNoeud.class));
			return;
		}

		fail("KO");
	}
	
	@Test 
	public void getTypeNoeudById() {
		
		TypeNoeud type = new TypeNoeud();
		type.setIdTypeNoeud(3);
		type.setLabel("label");
		type.setActif(true);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeNoeud.class, 3)).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReferenceDto result = service.getTypeNoeudById(3);
		
		assertEquals(result.getId().intValue(), type.getIdTypeNoeud());
		assertEquals(result.getLabel(), type.getLabel());
		assertEquals(result.isActif(), type.isActif());
	}
	
	@Test 
	public void deleteTypeNoeudById() {
		
		TypeNoeud type = new TypeNoeud();
		type.setIdTypeNoeud(3);
		type.setLabel("label");
		type.setActif(true);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeNoeud.class, 3)).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		service.deleteTypeNoeudById(3);
		
		Mockito.verify(adsRepository, Mockito.times(1)).removeEntity(Mockito.isA(TypeNoeud.class));
	}
	
	@Test 
	public void deleteTypeNoeudById_notFound() {
		
		TypeNoeud type = new TypeNoeud();
		type.setIdTypeNoeud(3);
		type.setLabel("label");
		type.setActif(true);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeNoeud.class, 3)).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		try {
			service.deleteTypeNoeudById(2);
		} catch(TypeNoeudNotFoundException e) {
			Mockito.verify(adsRepository, Mockito.never()).removeEntity(Mockito.isA(TypeNoeud.class));
			return;
		}
	
		fail("KO");
	}
	
	@Test 
	public void disableTypeNoeudById() {
		
		TypeNoeud type = Mockito.spy(new TypeNoeud());
		type.setIdTypeNoeud(3);
		type.setLabel("label");
		type.setActif(true);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeNoeud.class, 3)).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		service.disableTypeNoeudById(3);
		
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(TypeNoeud.class));
		assertFalse(type.isActif());
	}
	
	@Test 
	public void disableTypeNoeudById_notFound() {
		
		TypeNoeud type = new TypeNoeud();
		type.setIdTypeNoeud(3);
		type.setLabel("label");
		type.setActif(true);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeNoeud.class, 3)).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		try {
			service.disableTypeNoeudById(2);
		} catch(TypeNoeudNotFoundException e) {
			Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(TypeNoeud.class));
			assertTrue(type.isActif());
			return;
		}
	
		fail("KO");
	}

}
