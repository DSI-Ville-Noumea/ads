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
import nc.noumea.mairie.ads.service.IAccessRightsService;
import nc.noumea.mairie.ads.service.IAgentMatriculeConverterService;

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
	public void createOrModifyTypeEntite_modify() {

		ReferenceDto dto = new ReferenceDto();
		dto.setId(2);
		dto.setLabel("label");

		ReturnMessageDto result = new ReturnMessageDto();

		TypeEntite type = Mockito.spy(new TypeEntite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, dto.getId())).thenReturn(type);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(new ReturnMessageDto());

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.createOrModifyTypeEntite(9005138, dto, result);

		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "Le type d'entité est bien modifié.");

		Mockito.verify(adsRepository, Mockito.times(1)).persistTypeEntity(type);
		Mockito.verify(adsRepository, Mockito.times(1)).persistTypeEntity(Mockito.isA(TypeEntite.class));
	}

	@Test
	public void createOrModifyTypeEntite_badRight() {

		ReferenceDto dto = new ReferenceDto();
		dto.setId(null);
		dto.setLabel("label");

		ReturnMessageDto result = new ReturnMessageDto();

		ReturnMessageDto erreurDroit = new ReturnMessageDto();
		erreurDroit.getErrors().add("Votre identifiant n'a pas les droits nécessaires pour effectuer cette opération.");

		TypeEntite type = Mockito.spy(new TypeEntite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, dto.getId())).thenReturn(type);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(erreurDroit);

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.createOrModifyTypeEntite(9005138, dto, result);

		assertFalse(result.getErrors().isEmpty());
		assertEquals(result.getErrors().get(0),
				"Votre identifiant n'a pas les droits nécessaires pour effectuer cette opération.");

		Mockito.verify(adsRepository, Mockito.never()).persistTypeEntity(type);
		Mockito.verify(adsRepository, Mockito.times(0)).persistTypeEntity(Mockito.isA(TypeEntite.class));
	}

	@Test
	public void createOrModifyTypeEntite_create() {

		ReferenceDto dto = new ReferenceDto();
		dto.setId(null);
		dto.setLabel("label");

		ReturnMessageDto result = new ReturnMessageDto();

		TypeEntite type = Mockito.spy(new TypeEntite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, dto.getId())).thenReturn(type);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(new ReturnMessageDto());

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.createOrModifyTypeEntite(9005138, dto, result);

		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "Le type d'entité est bien créé.");

		Mockito.verify(adsRepository, Mockito.never()).persistTypeEntity(type);
		Mockito.verify(adsRepository, Mockito.times(1)).persistTypeEntity(Mockito.isA(TypeEntite.class));
	}

	@Test
	public void createOrModifyTypeEntite_notFound() {

		ReferenceDto dto = new ReferenceDto();
		dto.setId(3);
		dto.setLabel("label");
		ReturnMessageDto result = new ReturnMessageDto();

		TypeEntite type = Mockito.spy(new TypeEntite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, dto.getId())).thenReturn(null);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(new ReturnMessageDto());

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.createOrModifyTypeEntite(9005138, dto, result);

		assertEquals(result.getErrors().get(0), "Le type d'entité n'existe pas.");

		Mockito.verify(adsRepository, Mockito.never()).persistTypeEntity(type);
		Mockito.verify(adsRepository, Mockito.never()).persistTypeEntity(Mockito.isA(TypeEntite.class));

	}

	@Test
	public void getTypeEntiteById() {

		TypeEntite type = new TypeEntite();
		type.setIdTypeEntite(3);
		type.setLabel("label");

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, 3)).thenReturn(type);

		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);

		ReferenceDto result = service.getTypeEntiteById(3);

		assertEquals(result.getId(), type.getIdTypeEntite());
		assertEquals(result.getLabel(), type.getLabel());
	}

	@Test
	public void deleteTypeEntiteById_BadAccessRight() {

		TypeEntite type = new TypeEntite();
		type.setIdTypeEntite(3);
		type.setLabel("label");
		ReturnMessageDto result = new ReturnMessageDto();

		ReturnMessageDto erreurDroit = new ReturnMessageDto();
		erreurDroit.getErrors()
				.add("Votre identifiant n'as pas les droits nécessaires pour effectuer cette opération.");

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, 3)).thenReturn(type);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(erreurDroit);

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.deleteTypeEntiteById(9005138, 3, result);

		assertFalse(result.getErrors().isEmpty());
		assertEquals(result.getErrors().get(0),
				"Votre identifiant n'as pas les droits nécessaires pour effectuer cette opération.");

		Mockito.verify(adsRepository, Mockito.never()).removeTypeEntity(Mockito.isA(TypeEntite.class));
	}

	@Test
	public void deleteTypeEntiteById() {

		TypeEntite type = new TypeEntite();
		type.setIdTypeEntite(3);
		type.setLabel("label");

		ReturnMessageDto result = new ReturnMessageDto();

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, 3)).thenReturn(type);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(new ReturnMessageDto());

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.deleteTypeEntiteById(9005138, 3, result);

		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "Le type d'entité est bien supprimé.");

		Mockito.verify(adsRepository, Mockito.times(1)).removeTypeEntity(Mockito.isA(TypeEntite.class));
	}

	@Test
	public void deleteTypeEntiteById_notFound() {

		TypeEntite type = new TypeEntite();
		type.setIdTypeEntite(3);
		type.setLabel("label");

		ReturnMessageDto result = new ReturnMessageDto();

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(new ReturnMessageDto());

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);
		Mockito.when(adsRepository.get(TypeEntite.class, 3)).thenReturn(type);

		ReferenceDataService service = new ReferenceDataService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.deleteTypeEntiteById(9005138, 2, result);

		assertEquals(result.getErrors().get(0), "Le type d'entité n'existe pas.");
		Mockito.verify(adsRepository, Mockito.never()).persistTypeEntity(Mockito.isA(TypeEntite.class));
	}

}
