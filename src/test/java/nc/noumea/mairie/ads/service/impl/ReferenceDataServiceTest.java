package nc.noumea.mairie.ads.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.ads.domain.TypeEntite;
import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.service.impl.ReferenceDataService;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class ReferenceDataServiceTest {

	@Test
	public void getReferenceDataListTypeEntite_returnFullList() {

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
		List<ReferenceDto> result = service.getReferenceDataListTypeEntite();

		// Then
		assertEquals(2, result.size());
		assertEquals(45, (int) result.get(0).getId());
		assertEquals("toto", result.get(0).getLabel());
		assertEquals(75, (int) result.get(1).getId());
		assertEquals("titi", result.get(1).getLabel());
	}

	@Test
	public void doesTypeEntiteValueAlreadyExists_doesNotExists_ReturnFalse() {

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
		assertFalse(service.doesTypeEntiteValueAlreadyExists("tutu"));
	}

	@Test
	public void doesTypeEntiteValueAlreadyExists_doesNotExists_ReturnTrue() {

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
		assertTrue(service.doesTypeEntiteValueAlreadyExists("TiTe"));
	}

	@Test
	public void saveNewTypeEntite_persistNewTypeEntite() {

		// Given
		IAdsRepository aR = Mockito.mock(IAdsRepository.class);
		String label = "yoyoy";
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", aR);

		// When
		service.saveNewTypeEntite(label);
		
		// Then
		Mockito.verify(aR, Mockito.times(1)).persistEntity(Mockito.isA(TypeEntite.class));
	}
	
	@Test 
	public void createOrModifyTypeEntite_modify() {
		
		ReferenceDto dto = new ReferenceDto();
		dto.setId(2);
		dto.setLabel("label");
		dto.setActif(true);
		
		TypeEntite type = Mockito.spy(new TypeEntite());
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, dto.getId())).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReturnMessageDto result = service.createOrModifyTypeEntite(dto);

		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "Le type d'entité est bien modifié.");

		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(type);
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(TypeEntite.class));
	}
	
	@Test 
	public void createOrModifyTypeEntite_create() {
		
		ReferenceDto dto = new ReferenceDto();
		dto.setId(null);
		dto.setLabel("label");
		dto.setActif(true);
		
		TypeEntite type = Mockito.spy(new TypeEntite());
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, dto.getId())).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReturnMessageDto result = service.createOrModifyTypeEntite(dto);

		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "Le type d'entité est bien créé.");

		Mockito.verify(adsRepository, Mockito.never()).persistEntity(type);
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(TypeEntite.class));
	}
	
	@Test 
	public void createOrModifyTypeEntite_notFound() {
		
		ReferenceDto dto = new ReferenceDto();
		dto.setId(3);
		dto.setLabel("label");
		dto.setActif(true);
		
		TypeEntite type = Mockito.spy(new TypeEntite());
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, dto.getId())).thenReturn(null);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReturnMessageDto result = service.createOrModifyTypeEntite(dto);
		
		assertEquals(result.getErrors().get(0), "Le type d'entité n'existe pas.");
		
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(type);
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(TypeEntite.class));
		
	}
	
	@Test 
	public void getTypeEntiteById() {
		
		TypeEntite type = new TypeEntite();
		type.setIdTypeEntite(3);
		type.setLabel("label");
		type.setActif(true);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, 3)).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReferenceDto result = service.getTypeEntiteById(3);
		
		assertEquals(result.getId(), type.getIdTypeEntite());
		assertEquals(result.getLabel(), type.getLabel());
		assertEquals(result.isActif(), type.isActif());
	}
	
	@Test 
	public void deleteTypeEntiteById() {
		
		TypeEntite type = new TypeEntite();
		type.setIdTypeEntite(3);
		type.setLabel("label");
		type.setActif(true);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, 3)).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReturnMessageDto result = service.deleteTypeEntiteById(3);

		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "Le type d'entité est bien supprimé.");
		
		Mockito.verify(adsRepository, Mockito.times(1)).removeEntity(Mockito.isA(TypeEntite.class));
	}
	
	@Test 
	public void deleteTypeEntiteById_notFound() {
		
		TypeEntite type = new TypeEntite();
		type.setIdTypeEntite(3);
		type.setLabel("label");
		type.setActif(true);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, 3)).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReturnMessageDto result = service.deleteTypeEntiteById(2);
		
		assertEquals(result.getErrors().get(0), "Le type d'entité n'existe pas.");
		Mockito.verify(adsRepository, Mockito.never()).removeEntity(Mockito.isA(TypeEntite.class));
	}
	
	@Test 
	public void disableTypeEntiteById() {
		
		TypeEntite type = Mockito.spy(new TypeEntite());
		type.setIdTypeEntite(3);
		type.setLabel("label");
		type.setActif(true);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, 3)).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReturnMessageDto result = service.disableTypeEntiteById(3);
		
		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "Le type d'entité est bien désactivé.");
		
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(TypeEntite.class));
		assertFalse(type.isActif());
	}
	
	@Test 
	public void disableTypeEntiteById_notFound() {
		
		TypeEntite type = new TypeEntite();
		type.setIdTypeEntite(3);
		type.setLabel("label");
		type.setActif(true);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, 3)).thenReturn(type);
		
		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReturnMessageDto result = service.disableTypeEntiteById(2);
		
		assertEquals(result.getErrors().get(0), "Le type d'entité n'existe pas.");
		
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(TypeEntite.class));
		assertTrue(type.isActif());
	}

}
