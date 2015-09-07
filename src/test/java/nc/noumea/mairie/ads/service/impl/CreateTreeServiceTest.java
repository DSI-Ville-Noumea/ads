package nc.noumea.mairie.ads.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeEntite;
import nc.noumea.mairie.ads.domain.TypeHistoEnum;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.repository.IMairieRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.ads.service.IAccessRightsService;
import nc.noumea.mairie.ads.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ads.service.ISiservUpdateService;
import nc.noumea.mairie.ads.service.ITreeConsultationService;
import nc.noumea.mairie.ads.service.ITreeDataConsistencyService;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class CreateTreeServiceTest extends AbstractDataServiceTest {

	@Test
	public void buildCoreEntites_recursiveBuildEntiteFromDto() {

		// Given
		ReferenceDto type = new ReferenceDto();
		type.setId(7);

		EntiteDto ne = new EntiteDto();
		ne.setLabel("TestLabel2");
		ne.setTypeEntite(type);
		ne.setSigle("NICA");

		ReferenceDto type2 = new ReferenceDto();
		type2.setId(6);

		EntiteDto n = new EntiteDto();
		n.setCodeServi("DCDB");
		n.setLabel("TestLabel");
		n.setTypeEntite(type2);
		n.setSigle("NICO");
		n.getEnfants().add(ne);

		TypeEntite tn6 = new TypeEntite();
		TypeEntite tn7 = new TypeEntite();
		IAdsRepository adsR = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsR.get(TypeEntite.class, 7)).thenReturn(tn7);
		Mockito.when(adsR.get(TypeEntite.class, 6)).thenReturn(tn6);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsR);

		// When
		Entite result = service.buildCoreEntites(n, null, new ArrayList<String>());

		// Then
		assertEquals("DCDB", result.getSiservInfo().getCodeServi());
		assertEquals("TestLabel", result.getLabel());
		assertEquals(tn6, result.getTypeEntite());
		assertEquals("NICO", result.getSigle());
		assertEquals(1, result.getEntitesEnfants().size());

		Entite enfantResult = result.getEntitesEnfants().iterator().next();
		assertNull(enfantResult.getSiservInfo().getCodeServi());
		assertEquals("TestLabel2", enfantResult.getLabel());
		assertEquals(tn7, enfantResult.getTypeEntite());
		assertEquals("NICA", enfantResult.getSigle());
		assertEquals(0, enfantResult.getEntitesEnfants().size());
	}

	@Test
	public void checkRequiredData_NullDatas() {

		EntiteDto entiteDto = new EntiteDto();

		CreateTreeService service = new CreateTreeService();

		ReturnMessageDto result = service.checkRequiredData(entiteDto, null);

		assertEquals(result.getErrors().size(), 3);
		assertEquals(result.getErrors().get(0), "Le sigle est obligatoire.");
		assertEquals(result.getErrors().get(1), "Le libellé est obligatoire.");
		assertEquals(result.getErrors().get(2), "L'entité parente est obligatoire.");
	}

	@Test
	public void checkRequiredData_EmptyDatas() {

		EntiteDto entiteDto = new EntiteDto();
		entiteDto.setSigle("");
		entiteDto.setLabel("");
		entiteDto.setEntiteParent(new EntiteDto());

		CreateTreeService service = new CreateTreeService();

		ReturnMessageDto result = service.checkRequiredData(entiteDto, null);

		assertEquals(result.getErrors().size(), 3);
		assertEquals(result.getErrors().get(0), "Le sigle est obligatoire.");
		assertEquals(result.getErrors().get(1), "Le libellé est obligatoire.");
		assertEquals(result.getErrors().get(2), "L'entité parente est obligatoire.");
	}

	@Test
	public void checkDataToCreateEntity() {

		EntiteDto entiteParentDto = new EntiteDto();
		entiteParentDto.setIdEntite(2);

		EntiteDto entiteRemplaceeDto = new EntiteDto();
		entiteRemplaceeDto.setIdEntite(5);

		EntiteDto entiteDto = new EntiteDto();
		entiteDto.setSigle("SIGLE");
		entiteDto.setLabel("LABEL");
		entiteDto.setEntiteParent(entiteParentDto);
		entiteDto.setEntiteRemplacee(entiteRemplaceeDto);

		Entite entiteParent = new Entite();
		entiteParent.setIdEntite(2);

		Entite entiteRemplacee = new Entite();
		entiteRemplacee.setIdEntite(5);

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getEntiteParent().getIdEntite())).thenReturn(
				entiteParent);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getEntiteRemplacee().getIdEntite())).thenReturn(
				entiteRemplacee);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);

		for (int i = 0; i < 4; i++) {

			entiteParent.setStatut(StatutEntiteEnum.getStatutEntiteEnum(i));
			entiteRemplacee.setStatut(StatutEntiteEnum.getStatutEntiteEnum(i));

			ReturnMessageDto result = service.checkDataToCreateEntity(entiteDto, null);

			if (i == 0) {
				assertEquals(result.getErrors().size(), 1);
				assertEquals(result.getErrors().get(0), "Une entité au statut en prévision ne peut pas être remplacée.");
			}
			if (i == 1) {
				assertEquals(result.getErrors().size(), 0);
			}
			if (i == 2 || i == 3) {
				assertEquals(result.getErrors().size(), 1);
				assertEquals(result.getErrors().get(0),
						"Le statut de l'entité parente n'est ni active ni en prévision.");
			}
		}
	}

	@Test
	public void buildCoreEntites_generateCodeServi() {

		Entite parent = constructEntite(1, "DCAA", false);

		EntiteDto entiteDto = constructEntiteDto(null, null, false);

		List<String> existingServiCodes = new ArrayList<String>();

		TypeEntite typeEntite = new TypeEntite();
		typeEntite.setIdTypeEntite(entiteDto.getTypeEntite().getId());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, entiteDto.getTypeEntite().getId())).thenReturn(typeEntite);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);

		Entite result = service.buildCoreEntites(entiteDto, parent, existingServiCodes, false);

		// assertEquals(result.getSiservInfo().getCodeServi(), "DCBA");
		assertEquals(result.getEntiteParent().getIdEntite().intValue(), 1);

		checkEntite(result, entiteDto, false);
	}

	@Test
	public void buildCoreEntitesWithChildren_generateCodeServi() {

		Entite parent = constructEntite(1, "DCAA", true);

		EntiteDto entiteDto = constructEntiteDto(null, null, true);

		List<String> existingServiCodes = new ArrayList<String>();

		TypeEntite typeEntite = new TypeEntite();
		typeEntite.setIdTypeEntite(entiteDto.getTypeEntite().getId());

		TypeEntite typeEntiteEnfant = new TypeEntite();
		typeEntiteEnfant.setIdTypeEntite(entiteDto.getEnfants().get(0).getTypeEntite().getId());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, entiteDto.getTypeEntite().getId())).thenReturn(typeEntite);
		Mockito.when(adsRepository.get(TypeEntite.class, entiteDto.getEnfants().get(0).getTypeEntite().getId()))
				.thenReturn(typeEntiteEnfant);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);

		Entite result = service.buildCoreEntites(entiteDto, parent, existingServiCodes, true);

		// assertEquals(result.getSiservInfo().getCodeServi(), "DCBA");
		assertEquals(result.getEntiteParent().getIdEntite().intValue(), 1);

		checkEntite(result, entiteDto, true);
	}

	@Test
	public void saveNewEntityAndReturnMessages_throwException() {
		ReturnMessageDto result = new ReturnMessageDto();

		Entite entite = constructEntite(1, "DCAA", true);

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);

		ReturnMessageDto errorMessages = new ReturnMessageDto();
		errorMessages.getErrors().add("custom error");

		ITreeDataConsistencyService dataConsistencyService = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(dataConsistencyService.checkDataConsistencyForNewEntity(racine.get(0), entite, result))
				.thenReturn(errorMessages);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		try {
			result = service.saveNewEntityAndReturnMessages(entite, 9005138, TypeHistoEnum.CREATION, result);
		} catch (ReturnMessageDtoException e) {
			ReturnMessageDto result2 = e.getErreur();
			assertEquals(result2.getErrors().get(0), "custom error");
		}

		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
	}

	@Test
	public void saveNewEntityAndReturnMessages_ok() {
		ReturnMessageDto result = new ReturnMessageDto();

		Entite entite = constructEntite(1, "DCAA", true);

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);

		ReturnMessageDto errorMessages = new ReturnMessageDto();

		ITreeDataConsistencyService dataConsistencyService = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(dataConsistencyService.checkDataConsistencyForNewEntity(racine.get(0), entite, result))
				.thenReturn(errorMessages);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		try {
			result = service.saveNewEntityAndReturnMessages(entite, 9005138, TypeHistoEnum.CREATION, result);
		} catch (ReturnMessageDtoException e) {
			fail("error");
		}

		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
		assertEquals(result.getInfos().get(0), "L'entité est bien créée.");
		assertEquals(result.getId().intValue(), 1);
	}

	@Test
	public void saveModifiedEntityAndReturnMessages_throwException() {
		Entite entite = constructEntite(1, "DCAA", true);

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);

		ReturnMessageDto errorMessages = new ReturnMessageDto();
		errorMessages.getErrors().add("custom error");

		ITreeDataConsistencyService dataConsistencyService = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(dataConsistencyService.checkDataConsistencyForModifiedEntity(racine.get(0), entite, null))
				.thenReturn(errorMessages);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		try {
			service.saveModifiedEntityAndReturnMessages(entite, 9005138);
		} catch (ReturnMessageDtoException e) {
			ReturnMessageDto result2 = e.getErreur();
			assertEquals(result2.getErrors().get(0), "custom error");
		}

		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
	}

	@Test
	public void saveModifiedEntityAndReturnMessages_ok() {
		ReturnMessageDto result = new ReturnMessageDto();

		Entite entite = constructEntite(1, "DCAA", true);

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);

		ReturnMessageDto errorMessages = new ReturnMessageDto();

		ITreeDataConsistencyService dataConsistencyService = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(dataConsistencyService.checkDataConsistencyForModifiedEntity(racine.get(0), entite, null))
				.thenReturn(errorMessages);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		try {
			result = service.saveModifiedEntityAndReturnMessages(entite, 9005138);
		} catch (ReturnMessageDtoException e) {
			fail("error");
		}

		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
		assertEquals(result.getInfos().get(0), "L'entité est bien modifiée.");
		assertEquals(result.getId().intValue(), 1);
	}

	@Test
	public void modifyEntity_entityNotExist() {
		ReturnMessageDto result = new ReturnMessageDto();

		EntiteDto entiteDto = constructEntiteDto(1, "DCAA", false);

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getIdEntite())).thenReturn(null);

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(result);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.modifyEntity(9005138, entiteDto, result);

		assertEquals(result.getErrors().get(0), "L'entité n'existe pas.");
	}

	@Test
	public void createEntity_BadRight() {
		ReturnMessageDto result = new ReturnMessageDto();

		EntiteDto entiteDto = constructEntiteDto(1, "DCAA", false);
		entiteDto.setEntiteParent(new EntiteDto());
		entiteDto.getEntiteParent().setIdEntite(2);
		entiteDto.getEntiteParent().setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());

		Entite entite = constructEntite(1, "DCAA", false);

		Entite entiteParent = new Entite();
		entiteParent.setIdEntite(2);
		entiteParent.setStatut(StatutEntiteEnum.ACTIF);

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getIdEntite())).thenReturn(entite);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getEntiteParent().getIdEntite())).thenReturn(
				entiteParent);

		List<String> existingServiCodes = new ArrayList<String>();
		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getAllServiCodes()).thenReturn(existingServiCodes);

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		ReturnMessageDto errorMessages = new ReturnMessageDto();
		ReturnMessageDto erreur = new ReturnMessageDto();
		erreur.getErrors().add("Mauvais login");

		ITreeDataConsistencyService dataConsistencyService = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(
				dataConsistencyService.checkDataConsistencyForNewEntity(Mockito.isA(Entite.class),
						Mockito.isA(Entite.class), Mockito.any(ReturnMessageDto.class))).thenReturn(errorMessages);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(erreur);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.createEntity(9005138, entiteDto, TypeHistoEnum.CREATION, result);

		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
		assertEquals(result.getErrors().get(0), "Mauvais login");
		assertEquals(result.getInfos().size(), 0);
	}

	@Test
	public void createEntity() {
		ReturnMessageDto result = new ReturnMessageDto();

		EntiteDto entiteDto = constructEntiteDto(1, "DCAA", false);
		entiteDto.setEntiteParent(new EntiteDto());
		entiteDto.getEntiteParent().setIdEntite(2);
		entiteDto.getEntiteParent().setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());

		Entite entite = constructEntite(1, "DCAA", false);

		Entite entiteParent = new Entite();
		entiteParent.setIdEntite(2);
		entiteParent.setStatut(StatutEntiteEnum.ACTIF);

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getIdEntite())).thenReturn(entite);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getEntiteParent().getIdEntite())).thenReturn(
				entiteParent);

		List<String> existingServiCodes = new ArrayList<String>();
		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getAllServiCodes()).thenReturn(existingServiCodes);

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		ReturnMessageDto errorMessages = new ReturnMessageDto();

		ITreeDataConsistencyService dataConsistencyService = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(
				dataConsistencyService.checkDataConsistencyForNewEntity(Mockito.isA(Entite.class),
						Mockito.isA(Entite.class), Mockito.any(ReturnMessageDto.class))).thenReturn(errorMessages);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(result);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.createEntity(9005138, entiteDto, TypeHistoEnum.CREATION, result);

		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
		assertEquals(result.getInfos().get(0), "L'entité est bien créée.");
	}

	@Test
	public void createOrUpdateSiServ() {

		ReturnMessageDto result = new ReturnMessageDto();

		EntiteDto entiteDto = constructEntiteDto(1, "DCAA", false);
		entiteDto.setEntiteParent(new EntiteDto());
		entiteDto.getEntiteParent().setIdEntite(2);

		Entite entite = constructEntite(1, "DCAA", false);
		entite.setStatut(StatutEntiteEnum.PREVISION);

		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);

		result = service.createOrUpdateSiServ(result, entiteDto, entite);

		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservNwAndSiServ(Mockito.isA(Entite.class),
				Mockito.isA(EntiteDto.class), Mockito.any(ReturnMessageDto.class));

		Mockito.reset(siservUpdateService);

		entite.setStatut(StatutEntiteEnum.ACTIF);
		result = service.createOrUpdateSiServ(result, entiteDto, entite);

		Mockito.verify(siservUpdateService, Mockito.times(1)).updateSiservNwAndSiServ(Mockito.isA(Entite.class),
				Mockito.isA(EntiteDto.class), Mockito.any(ReturnMessageDto.class));

		Mockito.reset(siservUpdateService);

		entite.setStatut(StatutEntiteEnum.TRANSITOIRE);
		result = service.createOrUpdateSiServ(result, entiteDto, entite);

		Mockito.verify(siservUpdateService, Mockito.times(1)).updateSiservNwAndSiServ(Mockito.isA(Entite.class),
				Mockito.isA(EntiteDto.class), Mockito.any(ReturnMessageDto.class));

		Mockito.reset(siservUpdateService);

		entite.setStatut(StatutEntiteEnum.INACTIF);
		result = service.createOrUpdateSiServ(result, entiteDto, entite);

		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservNwAndSiServ(Mockito.isA(Entite.class),
				Mockito.isA(EntiteDto.class), Mockito.any(ReturnMessageDto.class));
	}

	@Test
	public void modifyEntity_notModifySiSerNw() {
		ReturnMessageDto result = new ReturnMessageDto();

		EntiteDto entiteDto = constructEntiteDto(1, "DCAA", false);
		entiteDto.setEntiteParent(new EntiteDto());
		entiteDto.getEntiteParent().setIdEntite(2);

		Entite entite = constructEntite(1, "DCAA", false);
		entite.setStatut(StatutEntiteEnum.PREVISION);

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getIdEntite())).thenReturn(entite);

		List<String> existingServiCodes = new ArrayList<String>();
		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getAllServiCodes()).thenReturn(existingServiCodes);

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		ReturnMessageDto errorMessages = new ReturnMessageDto();

		ITreeDataConsistencyService dataConsistencyService = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(dataConsistencyService.checkDataConsistencyForModifiedEntity(racine.get(0), entite, null))
				.thenReturn(errorMessages);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(result);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.modifyEntity(9005138, entiteDto, result);

		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
		assertEquals(result.getInfos().get(0), "L'entité est bien modifiée.");
		assertEquals(result.getId().intValue(), 1);
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservNwAndSiServ(Mockito.isA(Entite.class),
				Mockito.isA(EntiteDto.class), Mockito.any(ReturnMessageDto.class));
	}

	@Test
	public void modifyEntity() {
		ReturnMessageDto result = new ReturnMessageDto();

		EntiteDto entiteDto = constructEntiteDto(1, "DCAA", false);
		entiteDto.setEntiteParent(new EntiteDto());
		entiteDto.getEntiteParent().setIdEntite(2);

		Entite entite = constructEntite(1, "DCAA", false);

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getIdEntite())).thenReturn(entite);

		List<String> existingServiCodes = new ArrayList<String>();
		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getAllServiCodes()).thenReturn(existingServiCodes);

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		ReturnMessageDto errorMessages = new ReturnMessageDto();

		ITreeDataConsistencyService dataConsistencyService = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(dataConsistencyService.checkDataConsistencyForModifiedEntity(racine.get(0), entite, null))
				.thenReturn(errorMessages);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		Mockito.when(
				siservUpdateService.updateSiservNwAndSiServ(Mockito.isA(Entite.class), Mockito.isA(EntiteDto.class),
						Mockito.any(ReturnMessageDto.class))).thenReturn(result);

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(result);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.modifyEntity(9005138, entiteDto, result);

		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
		assertEquals(result.getInfos().get(0), "L'entité est bien modifiée.");
		assertEquals(result.getId().intValue(), 1);
		Mockito.verify(siservUpdateService, Mockito.times(1)).updateSiservNwAndSiServ(Mockito.isA(Entite.class),
				Mockito.isA(EntiteDto.class), Mockito.any(ReturnMessageDto.class));
	}

	@Test
	public void checkDataToDeleteEntity_entityNotExist() {

		CreateTreeService service = new CreateTreeService();
		ReturnMessageDto result = service.checkDataToDeleteEntity(null, null, false);

		assertEquals(result.getErrors().get(0), "L'entité n'existe pas.");
	}

	@Test
	public void checkDataToDeleteEntity_noChild() {

		Entite entite = constructEntite(1, "DCAA", false);
		entite.setStatut(StatutEntiteEnum.PREVISION);

		CreateTreeService service = new CreateTreeService();
		ReturnMessageDto result = service.checkDataToDeleteEntity(entite, null, false);

		assertTrue(result.getErrors().isEmpty());
	}

	@Test
	public void checkDataToDeleteEntity_HaveChildren() {

		Entite entite = constructEntite(1, "DCAA", true);

		CreateTreeService service = new CreateTreeService();
		ReturnMessageDto result = service.checkDataToDeleteEntity(entite, null, false);

		assertEquals(result.getErrors().get(0), "L'entité ne peut être supprimée, car elle a un ou des entités fille.");
	}

	@Test
	public void deleteEntity_errorFichePosteSirh() {
		ReturnMessageDto result = new ReturnMessageDto();

		Integer idAgent = 9005138;
		Integer idEntite = 1;

		Entite entite = constructEntite(idEntite, "DCAA", false);
		entite.setStatut(StatutEntiteEnum.PREVISION);

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, idEntite)).thenReturn(entite);

		ReturnMessageDto rmd = new ReturnMessageDto();
		rmd.getErrors().add("error delete Fiche Poste");

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.deleteFichesPosteByIdEntite(entite.getIdEntite(), idAgent)).thenReturn(rmd);

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(result);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.deleteEntity(idEntite, idAgent, result, false);

		assertEquals(result.getErrors().get(0), "error delete Fiche Poste");
		Mockito.verify(adsRepository, Mockito.never()).removeEntiteAvecPersistHisto(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
	}

	@Test
	public void deleteEntity_ok() {
		ReturnMessageDto result = new ReturnMessageDto();

		Integer idAgent = 9005138;
		Integer idEntite = 1;

		Entite entite = constructEntite(idEntite, "DCAA", false);
		entite.setStatut(StatutEntiteEnum.PREVISION);

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, idEntite)).thenReturn(entite);

		ReturnMessageDto rmd = new ReturnMessageDto();

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.deleteFichesPosteByIdEntite(entite.getIdEntite(), idAgent)).thenReturn(rmd);

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(result);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.deleteEntity(idEntite, idAgent, result, false);

		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "L'entité est bien supprimée.");
		Mockito.verify(adsRepository, Mockito.times(1)).removeEntiteAvecPersistHisto(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
		Mockito.verify(sirhWsConsumer, Mockito.times(1)).deleteFichesPosteByIdEntite(Mockito.anyInt(),
				Mockito.anyInt());
	}

	@Test
	public void deleteEntity_with_children_ok() {
		ReturnMessageDto result = new ReturnMessageDto();

		Integer idAgent = 9005138;
		Integer idEntite = 1;

		Entite entite = constructEntite(idEntite, "DCAA", true);
		entite.setStatut(StatutEntiteEnum.PREVISION);

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, idEntite)).thenReturn(entite);

		ReturnMessageDto rmd = new ReturnMessageDto();

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.deleteFichesPosteByIdEntite(entite.getIdEntite(), idAgent)).thenReturn(rmd);

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(result);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.deleteEntity(idEntite, idAgent, result, true);

		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "L'entité est bien supprimée.");
		Mockito.verify(adsRepository, Mockito.times(1)).removeEntiteAvecPersistHisto(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
		Mockito.verify(sirhWsConsumer, Mockito.times(2)).deleteFichesPosteByIdEntite(Mockito.anyInt(),
				Mockito.anyInt());
	}

	@Test
	public void checkDataToDeleteEntity_WithChildren_badStatut() {

		Entite entite = constructEntite(1, "DCAA", true);

		CreateTreeService service = new CreateTreeService();
		ReturnMessageDto result = service.checkDataToDeleteEntity(entite, null, true);

		assertEquals(result.getErrors().get(0), "Le statut de l'entité SED-DMD n'est pas PREVISION");
	}

	@Test
	public void checkDataToDeleteEntity_WithChildren_badStatutForChild() {

		Entite entite = constructEntite(1, "DCAA", true);
		entite.setStatut(StatutEntiteEnum.PREVISION);
		
		Entite entitePetitEnfant = constructEntite(1, "DCAA", false);
		entitePetitEnfant.setSigle("JOHANN");
		
		entite.getEntitesEnfants().add(entitePetitEnfant);

		CreateTreeService service = new CreateTreeService();
		ReturnMessageDto result = service.checkDataToDeleteEntity(entite, null, true);

		assertEquals(result.getErrors().get(0), "Le statut de l'entité JOHANN n'est pas PREVISION");
	}

	@Test
	public void checkDataToDeleteEntity_WithChildren() {

		Entite entite = constructEntite(1, "DCAA", true);

		CreateTreeService service = new CreateTreeService();
		ReturnMessageDto result = service.checkDataToDeleteEntity(entite, null, true);

		assertEquals(result.getErrors().get(0), "Le statut de l'entité SED-DMD n'est pas PREVISION");
	}

	@Test
	public void duplicateEntity_badStatut() {

		EntiteDto entiteDto = constructEntiteDto(1, "DCAA", false);
		entiteDto.setEntiteParent(new EntiteDto());
		entiteDto.getEntiteParent().setIdEntite(2);
		entiteDto.getEntiteParent().setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());

		entiteDto.setEntiteRemplacee(new EntiteDto());
		entiteDto.getEntiteRemplacee().setSigle("NONO");
		entiteDto.getEntiteRemplacee().setIdEntite(1);
		entiteDto.getEntiteRemplacee().setIdStatut(StatutEntiteEnum.PREVISION.getIdRefStatutEntite());

		Entite entiteParent = new Entite();
		entiteParent.setIdEntite(2);
		entiteParent.setStatut(StatutEntiteEnum.ACTIF);

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		ITreeConsultationService consultationService = Mockito.mock(ITreeConsultationService.class);
		Mockito.when(consultationService.getEntityByIdEntite(entiteDto.getIdEntite())).thenReturn(
				entiteDto.getEntiteRemplacee());

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "consultationService", consultationService);

		ReturnMessageDto result = service.duplicateEntity(9005138, entiteDto, null);

		assertEquals(result.getErrors().size(), 1);
		assertEquals(result.getErrors().get(0), "Le statut de l'entité NONO n'est ni active ni transitoire.");
	}

	@Test
	public void duplicateEntity_badParentStatut() {
		ReturnMessageDto result = new ReturnMessageDto();

		EntiteDto entiteDto = constructEntiteDto(1, "DCAA", false);
		entiteDto.setEntiteParent(new EntiteDto());
		entiteDto.getEntiteParent().setIdEntite(2);
		entiteDto.getEntiteParent().setIdStatut(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite());

		entiteDto.setEntiteRemplacee(new EntiteDto());
		entiteDto.getEntiteRemplacee().setIdEntite(1);
		entiteDto.getEntiteRemplacee().setIdStatut(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite());

		Entite entiteRemplacee = new Entite();
		entiteRemplacee.setIdEntite(entiteDto.getEntiteRemplacee().getIdEntite());
		entiteRemplacee.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getEntiteRemplacee().getIdStatut()));

		Entite entiteParent = new Entite();
		entiteParent.setIdEntite(entiteDto.getEntiteParent().getIdEntite());
		entiteParent.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getEntiteParent().getIdStatut()));

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getEntiteParent().getIdEntite())).thenReturn(
				entiteParent);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getEntiteRemplacee().getIdEntite())).thenReturn(
				entiteRemplacee);

		ITreeConsultationService consultationService = Mockito.mock(ITreeConsultationService.class);
		Mockito.when(consultationService.getEntityByIdEntite(entiteDto.getIdEntite())).thenReturn(
				entiteDto.getEntiteRemplacee());

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, result)).thenReturn(result);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "consultationService", consultationService);
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		result = service.duplicateEntity(9005138, entiteDto, result);

		assertEquals(result.getErrors().size(), 1);
		assertEquals(result.getErrors().get(0), "Le statut de l'entité parente n'est ni active ni en prévision.");
	}

	@Test
	public void duplicateEntity_OK() {
		ReturnMessageDto result = new ReturnMessageDto();
		EntiteDto entiteDto = constructEntiteDto(1, "DCAA", false);
		entiteDto.setEntiteParent(new EntiteDto());
		entiteDto.getEntiteParent().setIdEntite(2);
		entiteDto.getEntiteParent().setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		entiteDto.setIdAgentCreation(9005138);

		entiteDto.setEntiteRemplacee(new EntiteDto());
		entiteDto.getEntiteRemplacee().setIdEntite(1);
		entiteDto.getEntiteRemplacee().setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());

		Entite entiteRemplacee = new Entite();
		entiteRemplacee.setIdEntite(entiteDto.getEntiteRemplacee().getIdEntite());
		entiteRemplacee.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getEntiteRemplacee().getIdStatut()));

		Entite entiteParent = new Entite();
		entiteParent.setIdEntite(entiteDto.getEntiteParent().getIdEntite());
		entiteParent.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getEntiteParent().getIdStatut()));

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getEntiteParent().getIdEntite())).thenReturn(
				entiteParent);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getEntiteRemplacee().getIdEntite())).thenReturn(
				entiteRemplacee);

		List<String> existingServiCodes = new ArrayList<String>();
		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getAllServiCodes()).thenReturn(existingServiCodes);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		ReturnMessageDto errorMessages = new ReturnMessageDto();
		ITreeDataConsistencyService dataConsistencyService = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(
				dataConsistencyService.checkDataConsistencyForNewEntity(Mockito.isA(Entite.class),
						Mockito.isA(Entite.class), Mockito.any(ReturnMessageDto.class))).thenReturn(errorMessages);

		ITreeConsultationService consultationService = Mockito.mock(ITreeConsultationService.class);
		Mockito.when(consultationService.getEntityByIdEntite(entiteDto.getIdEntite())).thenReturn(
				entiteDto.getEntiteRemplacee());

		ReturnMessageDto resultPart = new ReturnMessageDto();
		resultPart.setId(1);

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, resultPart)).thenReturn(
				new ReturnMessageDto());

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "consultationService", consultationService);
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		try {
			result = service.duplicateEntity(9005138, entiteDto, resultPart);
		} catch (ReturnMessageDtoException e) {
			fail("error");
		}

		assertEquals(result.getErrors().size(), 0);
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
		assertEquals(result.getInfos().size(), 1);
		assertEquals(result.getInfos().get(0), "L'entité est bien créée.");
	}
	
	@Test
	public void duplicateFichesPosteOfEntity() {
		
		ReturnMessageDto result = new ReturnMessageDto();
		result.setId(1);
		
		EntiteDto entiteDto = constructEntiteDto(1, "DCAA", false);
		entiteDto.setEntiteParent(new EntiteDto());
		entiteDto.getEntiteParent().setIdEntite(2);
		entiteDto.getEntiteParent().setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		entiteDto.setIdAgentCreation(9005138);

		entiteDto.setEntiteRemplacee(new EntiteDto());
		entiteDto.getEntiteRemplacee().setIdEntite(1);
		entiteDto.getEntiteRemplacee().setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());

		Entite entiteRemplacee = new Entite();
		entiteRemplacee.setIdEntite(entiteDto.getEntiteRemplacee().getIdEntite());
		entiteRemplacee.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getEntiteRemplacee().getIdStatut()));

		Entite entite = new Entite();
		entite.setIdEntite(entiteDto.getIdEntite());
		entite.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getEntiteParent().getIdStatut()));
		entite.setEntiteRemplacee(entiteRemplacee);

		ReturnMessageDto rmd = new ReturnMessageDto();
		rmd.getInfos().add("6 FDP vont être dupliquées.");
		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.dupliqueFichesPosteByIdEntite(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(rmd);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromIdEntite(result.getId())).thenReturn(entite);

		ReturnMessageDto resultPart = new ReturnMessageDto();
		resultPart.setId(1);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		try {
			result = service.duplicateFichesPosteOfEntity(9005138, entiteDto, resultPart, false);
		} catch (ReturnMessageDtoException e) {
			fail("error");
		}

		assertEquals(result.getErrors().size(), 0);
		Mockito.verify(sirhWsConsumer, Mockito.times(1)).dupliqueFichesPosteByIdEntite(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
		assertEquals(result.getInfos().size(), 1);
		assertEquals(result.getInfos().get(0), "6 FDP vont être dupliquées.");
	}
	
	@Test
	public void duplicateFichesPosteOfEntityWithChildren_OK() {

		ReturnMessageDto resultPart = new ReturnMessageDto();
		resultPart.setId(1);
		resultPart.setListIds(Arrays.asList(1));

		EntiteDto entiteDto = constructEntiteDto(1, "DCAA", false);
		entiteDto.setIdEntite(13);
		entiteDto.setEntiteParent(new EntiteDto());
		entiteDto.getEntiteParent().setIdEntite(2);
		entiteDto.getEntiteParent().setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		entiteDto.setIdAgentCreation(9005138);

		entiteDto.setEntiteRemplacee(new EntiteDto());
		entiteDto.getEntiteRemplacee().setIdEntite(1);
		entiteDto.getEntiteRemplacee().setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());

//		Entite entiteRemplacee = new Entite();
//		entiteRemplacee.setIdEntite(entiteDto.getEntiteRemplacee().getIdEntite());
//		entiteRemplacee.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getEntiteRemplacee().getIdStatut()));
//
//		Entite entite = new Entite();
//		entite.setIdEntite(entiteDto.getEntiteParent().getIdEntite());
//		entite.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getEntiteParent().getIdStatut()));
//		entite.setEntiteRemplacee(entiteRemplacee);

		ReturnMessageDto rmd = new ReturnMessageDto();
		rmd.getInfos().add("6 FDP vont être dupliquées.");
		
		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.dupliqueFichesPosteByIdEntite(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(rmd);

		// entite a dupliquer et ses enfants pour checker les statuts
		Entite newEntites = new Entite();
		newEntites.setIdEntite(11);
		newEntites.setEntiteRemplacee(new Entite());
		newEntites.getEntiteRemplacee().setIdEntite(1);
		Entite newEntite1 = new Entite();
		newEntite1.setIdEntite(12);
		newEntite1.setEntiteRemplacee(new Entite());
		newEntite1.getEntiteRemplacee().setIdEntite(2);
		newEntites.getEntitesEnfants().add(newEntite1);
		Entite newEntite2 = new Entite();
		newEntite2.setIdEntite(13);
		newEntite2.setEntiteRemplacee(new Entite());
		newEntite2.getEntiteRemplacee().setIdEntite(3);
		newEntites.getEntitesEnfants().add(newEntite2);
		Entite newEntite21 = new Entite();
		newEntite21.setIdEntite(14);
		newEntite21.setEntiteRemplacee(new Entite());
		newEntite21.getEntiteRemplacee().setIdEntite(4);
		newEntite2.getEntitesEnfants().add(newEntite21);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromIdEntite(1)).thenReturn(newEntites);

		// entite a dupliquer et ses enfants pour checker les statuts
		EntiteDto root = new EntiteDto();
		root.setIdEntite(2);
		root.setLabel("DSI");
		root.setSigle("DSI");
		root.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		root.setEntiteParent(new EntiteDto());
		root.getEntiteParent().setIdEntite(1);
		EntiteDto e1 = new EntiteDto();
		e1.setIdEntite(3);
		e1.setLabel("SIE");
		e1.setSigle("SIE");
		e1.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		e1.setEntiteParent(root);
		root.getEnfants().add(e1);
		EntiteDto e2 = new EntiteDto();
		e2.setIdEntite(4);
		e2.setLabel("SED");
		e2.setSigle("SED");
		e2.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		e2.setEntiteParent(e1);
		root.getEnfants().add(e2);
		EntiteDto e21 = new EntiteDto();
		e21.setIdEntite(5);
		e21.setLabel("SED-DMD");
		e21.setSigle("SED-DMD");
		e21.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		e21.setEntiteParent(e2);
		e2.getEnfants().add(e21);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		
		try {
			service.duplicateFichesPosteOfEntity(9005138, entiteDto, resultPart, true);
		} catch (ReturnMessageDtoException e) {
			fail("error");
		}

		assertEquals(resultPart.getErrors().size(), 0);
		Mockito.verify(sirhWsConsumer, Mockito.times(4)).dupliqueFichesPosteByIdEntite(Mockito.anyInt(),
				Mockito.anyInt(), Mockito.anyInt());
		assertEquals(resultPart.getInfos().size(), 4);
		assertEquals(resultPart.getInfos().get(0), "6 FDP vont être dupliquées.");
		assertEquals(resultPart.getInfos().get(1), "6 FDP vont être dupliquées.");
		assertEquals(resultPart.getInfos().get(2), "6 FDP vont être dupliquées.");
		assertEquals(resultPart.getInfos().get(3), "6 FDP vont être dupliquées.");
	}

	@Test
	public void checkRecursiveStatutDuplicateEntite_ok() {

		ReturnMessageDto result = new ReturnMessageDto();

		EntiteDto root = new EntiteDto();
		root.setSigle("DSI");
		root.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		EntiteDto e1 = new EntiteDto();
		e1.setSigle("SIE");
		e1.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		root.getEnfants().add(e1);
		EntiteDto e2 = new EntiteDto();
		e2.setSigle("SED");
		e2.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		root.getEnfants().add(e2);
		EntiteDto e21 = new EntiteDto();
		e21.setSigle("SED-DMD");
		e21.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		e2.getEnfants().add(e21);

		CreateTreeService service = new CreateTreeService();
		result = service.checkStatutDuplicateEntite(root, result);

		assertTrue(result.getErrors().isEmpty());
	}

	@Test
	public void checkRecursiveStatutDuplicateEntite_ko() {

		ReturnMessageDto result = new ReturnMessageDto();

		EntiteDto root = new EntiteDto();
		root.setSigle("DSI");
		root.setIdStatut(StatutEntiteEnum.PREVISION.getIdRefStatutEntite());
		EntiteDto e1 = new EntiteDto();
		e1.setSigle("SIE");
		e1.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		root.getEnfants().add(e1);
		EntiteDto e2 = new EntiteDto();
		e2.setSigle("SED");
		e2.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		root.getEnfants().add(e2);
		EntiteDto e21 = new EntiteDto();
		e21.setSigle("SED-DMD");
		e21.setIdStatut(StatutEntiteEnum.PREVISION.getIdRefStatutEntite());
		e2.getEnfants().add(e21);

		CreateTreeService service = new CreateTreeService();
		result = service.checkStatutDuplicateEntite(root, result);

		assertEquals("Le statut de l'entité DSI n'est ni active ni transitoire.", result.getErrors().get(0));
	}

	@Test
	public void checkRecursiveStatutDuplicateEntite_koBis() {

		ReturnMessageDto result = new ReturnMessageDto();

		EntiteDto root = new EntiteDto();
		root.setSigle("DSI");
		root.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		EntiteDto e1 = new EntiteDto();
		e1.setSigle("SIE");
		e1.setIdStatut(StatutEntiteEnum.INACTIF.getIdRefStatutEntite());
		root.getEnfants().add(e1);
		EntiteDto e2 = new EntiteDto();
		e2.setSigle("SED");
		e2.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		root.getEnfants().add(e2);
		EntiteDto e21 = new EntiteDto();
		e21.setSigle("SED-DMD");
		e21.setIdStatut(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite());
		e2.getEnfants().add(e21);

		CreateTreeService service = new CreateTreeService();
		result = service.checkStatutDuplicateEntite(root, result);

		assertTrue(result.getErrors().isEmpty());
	}

	@Test
	public void duplicateEntityWithChildren_OK() {

		ReturnMessageDto resultPart = new ReturnMessageDto();
		resultPart.setId(1);

		EntiteDto entiteDto = constructEntiteDto(1, "DCAA", false);
		entiteDto.setIdEntite(13);
		entiteDto.setEntiteParent(new EntiteDto());
		entiteDto.getEntiteParent().setIdEntite(2);
		entiteDto.getEntiteParent().setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		entiteDto.setIdAgentCreation(9005138);

		entiteDto.setEntiteRemplacee(new EntiteDto());
		entiteDto.getEntiteRemplacee().setIdEntite(1);
		entiteDto.getEntiteRemplacee().setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());

		Entite entiteRemplacee = new Entite();
		entiteRemplacee.setIdEntite(entiteDto.getEntiteRemplacee().getIdEntite());
		entiteRemplacee.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getEntiteRemplacee().getIdStatut()));

		Entite entiteParent = new Entite();
		entiteParent.setIdEntite(entiteDto.getEntiteParent().getIdEntite());
		entiteParent.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getEntiteParent().getIdStatut()));

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getEntiteParent().getIdEntite())).thenReturn(
				entiteParent);
		Mockito.when(adsRepository.get(Entite.class, 3)).thenReturn(entiteParent);
		Mockito.when(adsRepository.get(Entite.class, 4)).thenReturn(entiteParent);
		Mockito.when(adsRepository.get(Entite.class, 5)).thenReturn(entiteParent);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getEntiteRemplacee().getIdEntite())).thenReturn(
				entiteRemplacee);

		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Entite entite = (Entite) invocation.getArguments()[0];
				entite.setIdEntite(1);
				return entite;
			}
		}).when(adsRepository).persistEntity(Mockito.isA(Entite.class), Mockito.isA(EntiteHisto.class));

		List<String> existingServiCodes = new ArrayList<String>();
		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getAllServiCodes()).thenReturn(existingServiCodes);

		// entite a dupliquer et ses enfants pour checker les statuts
		Entite newEntites = new Entite();
		newEntites.setIdEntite(11);
		newEntites.setEntiteRemplacee(new Entite());
		newEntites.getEntiteRemplacee().setIdEntite(1);
		Entite newEntite1 = new Entite();
		newEntite1.setIdEntite(12);
		newEntite1.setEntiteRemplacee(new Entite());
		newEntite1.getEntiteRemplacee().setIdEntite(2);
		newEntites.getEntitesEnfants().add(newEntite1);
		Entite newEntite2 = new Entite();
		newEntite2.setIdEntite(13);
		newEntite2.setEntiteRemplacee(new Entite());
		newEntite2.getEntiteRemplacee().setIdEntite(3);
		newEntites.getEntitesEnfants().add(newEntite2);
		Entite newEntite21 = new Entite();
		newEntite21.setIdEntite(14);
		newEntite21.setEntiteRemplacee(new Entite());
		newEntite21.getEntiteRemplacee().setIdEntite(4);
		newEntite2.getEntitesEnfants().add(newEntite21);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);
		Mockito.when(treeRepository.getEntiteFromIdEntite(1)).thenReturn(newEntites);

		ReturnMessageDto errorMessages = new ReturnMessageDto();
		ITreeDataConsistencyService dataConsistencyService = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(
				dataConsistencyService.checkDataConsistencyForNewEntity(Mockito.isA(Entite.class),
						Mockito.isA(Entite.class), Mockito.any(ReturnMessageDto.class))).thenReturn(errorMessages);

		// entite a dupliquer et ses enfants pour checker les statuts
		EntiteDto root = new EntiteDto();
		root.setIdEntite(2);
		root.setLabel("DSI");
		root.setSigle("DSI");
		root.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		root.setEntiteParent(new EntiteDto());
		root.getEntiteParent().setIdEntite(1);
		EntiteDto e1 = new EntiteDto();
		e1.setIdEntite(3);
		e1.setLabel("SIE");
		e1.setSigle("SIE");
		e1.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		e1.setEntiteParent(root);
		root.getEnfants().add(e1);
		EntiteDto e2 = new EntiteDto();
		e2.setIdEntite(4);
		e2.setLabel("SED");
		e2.setSigle("SED");
		e2.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		e2.setEntiteParent(e1);
		root.getEnfants().add(e2);
		EntiteDto e21 = new EntiteDto();
		e21.setIdEntite(5);
		e21.setLabel("SED-DMD");
		e21.setSigle("SED-DMD");
		e21.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		e21.setEntiteParent(e2);
		e2.getEnfants().add(e21);

		ITreeConsultationService consultationService = Mockito.mock(ITreeConsultationService.class);
		Mockito.when(consultationService.getEntityByIdEntiteWithChildren(entiteDto.getIdEntite())).thenReturn(root);

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, resultPart)).thenReturn(resultPart);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "consultationService", consultationService);
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);
		try {
			service.duplicateEntity(9005138, entiteDto, resultPart, true);
		} catch (ReturnMessageDtoException e) {
			fail("error");
		}

		assertEquals(resultPart.getErrors().size(), 0);
		Mockito.verify(adsRepository, Mockito.times(4)).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
		assertEquals(resultPart.getInfos().size(), 1);
		assertEquals(resultPart.getInfos().get(0), "L'entité est bien créée.");
	}

	@Test
	public void duplicateEntityWithChildren_filtreEntiteEnfantPrevisionInactif() {

		ReturnMessageDto resultPart = new ReturnMessageDto();
		resultPart.setId(1);

		EntiteDto entiteDto = constructEntiteDto(1, "DCAA", false);
		entiteDto.setIdEntite(13);
		entiteDto.setEntiteParent(new EntiteDto());
		entiteDto.getEntiteParent().setIdEntite(2);
		entiteDto.getEntiteParent().setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		entiteDto.setIdAgentCreation(9005138);

		entiteDto.setEntiteRemplacee(new EntiteDto());
		entiteDto.getEntiteRemplacee().setIdEntite(1);
		entiteDto.getEntiteRemplacee().setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());

		Entite entiteRemplacee = new Entite();
		entiteRemplacee.setIdEntite(entiteDto.getEntiteRemplacee().getIdEntite());
		entiteRemplacee.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getEntiteRemplacee().getIdStatut()));

		Entite entiteParent = new Entite();
		entiteParent.setIdEntite(entiteDto.getEntiteParent().getIdEntite());
		entiteParent.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getEntiteParent().getIdStatut()));

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getEntiteParent().getIdEntite())).thenReturn(
				entiteParent);
		Mockito.when(adsRepository.get(Entite.class, 3)).thenReturn(entiteParent);
		Mockito.when(adsRepository.get(Entite.class, 4)).thenReturn(entiteParent);
		Mockito.when(adsRepository.get(Entite.class, 5)).thenReturn(entiteParent);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getEntiteRemplacee().getIdEntite())).thenReturn(
				entiteRemplacee);

		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Entite entite = (Entite) invocation.getArguments()[0];
				entite.setIdEntite(1);
				return entite;
			}
		}).when(adsRepository).persistEntity(Mockito.isA(Entite.class), Mockito.isA(EntiteHisto.class));

		List<String> existingServiCodes = new ArrayList<String>();
		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getAllServiCodes()).thenReturn(existingServiCodes);

		// entite a dupliquer et ses enfants pour checker les statuts
		Entite newEntites = new Entite();
		newEntites.setIdEntite(11);
		newEntites.setEntiteRemplacee(new Entite());
		newEntites.getEntiteRemplacee().setIdEntite(1);
		Entite newEntite1 = new Entite();
		newEntite1.setIdEntite(12);
		newEntite1.setEntiteRemplacee(new Entite());
		newEntite1.getEntiteRemplacee().setIdEntite(2);
		newEntites.getEntitesEnfants().add(newEntite1);
		Entite newEntite2 = new Entite();
		newEntite2.setIdEntite(13);
		newEntite2.setEntiteRemplacee(new Entite());
		newEntite2.getEntiteRemplacee().setIdEntite(3);
		newEntites.getEntitesEnfants().add(newEntite2);
		Entite newEntite21 = new Entite();
		newEntite21.setIdEntite(14);
		newEntite21.setEntiteRemplacee(new Entite());
		newEntite21.getEntiteRemplacee().setIdEntite(4);
		newEntite2.getEntitesEnfants().add(newEntite21);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);
		Mockito.when(treeRepository.getEntiteFromIdEntite(1)).thenReturn(newEntites);

		ReturnMessageDto errorMessages = new ReturnMessageDto();
		ITreeDataConsistencyService dataConsistencyService = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(
				dataConsistencyService.checkDataConsistencyForNewEntity(Mockito.isA(Entite.class),
						Mockito.isA(Entite.class), Mockito.any(ReturnMessageDto.class))).thenReturn(errorMessages);

		// entite a dupliquer et ses enfants pour checker les statuts
		EntiteDto root = new EntiteDto();
		root.setIdEntite(2);
		root.setLabel("DSI");
		root.setSigle("DSI");
		root.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		root.setEntiteParent(new EntiteDto());
		root.getEntiteParent().setIdEntite(1);
		EntiteDto e1 = new EntiteDto();
		e1.setIdEntite(3);
		e1.setLabel("SIE");
		e1.setSigle("SIE");
		e1.setIdStatut(StatutEntiteEnum.PREVISION.getIdRefStatutEntite());
		e1.setEntiteParent(root);
		root.getEnfants().add(e1);
		EntiteDto e2 = new EntiteDto();
		e2.setIdEntite(4);
		e2.setLabel("SED");
		e2.setSigle("SED");
		e2.setIdStatut(StatutEntiteEnum.INACTIF.getIdRefStatutEntite());
		e2.setEntiteParent(e1);
		root.getEnfants().add(e2);
		EntiteDto e21 = new EntiteDto();
		e21.setIdEntite(5);
		e21.setLabel("SED-DMD");
		e21.setSigle("SED-DMD");
		e21.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		e21.setEntiteParent(e2);
		e2.getEnfants().add(e21);

		ITreeConsultationService consultationService = Mockito.mock(ITreeConsultationService.class);
		Mockito.when(consultationService.getEntityByIdEntiteWithChildren(entiteDto.getIdEntite())).thenReturn(root);

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, resultPart)).thenReturn(resultPart);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "consultationService", consultationService);
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);
		try {
			service.duplicateEntity(9005138, entiteDto, resultPart, true);
		} catch (ReturnMessageDtoException e) {
			fail("error");
		}

		assertEquals(resultPart.getErrors().size(), 0);
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
		assertEquals(resultPart.getInfos().size(), 1);
		assertEquals(resultPart.getInfos().get(0), "L'entité est bien créée.");
	}

	@Test
	public void duplicateEntityWithChildren_filtreEntiteEnfantPrevisionInactif_2eCas() {

		ReturnMessageDto resultPart = new ReturnMessageDto();
		resultPart.setId(1);

		EntiteDto entiteDto = constructEntiteDto(1, "DCAA", false);
		entiteDto.setIdEntite(13);
		entiteDto.setEntiteParent(new EntiteDto());
		entiteDto.getEntiteParent().setIdEntite(2);
		entiteDto.getEntiteParent().setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		entiteDto.setIdAgentCreation(9005138);

		entiteDto.setEntiteRemplacee(new EntiteDto());
		entiteDto.getEntiteRemplacee().setIdEntite(1);
		entiteDto.getEntiteRemplacee().setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());

		Entite entiteRemplacee = new Entite();
		entiteRemplacee.setIdEntite(entiteDto.getEntiteRemplacee().getIdEntite());
		entiteRemplacee.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getEntiteRemplacee().getIdStatut()));

		Entite entiteParent = new Entite();
		entiteParent.setIdEntite(entiteDto.getEntiteParent().getIdEntite());
		entiteParent.setStatut(StatutEntiteEnum.getStatutEntiteEnum(entiteDto.getEntiteParent().getIdStatut()));

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getEntiteParent().getIdEntite())).thenReturn(
				entiteParent);
		Mockito.when(adsRepository.get(Entite.class, 3)).thenReturn(entiteParent);
		Mockito.when(adsRepository.get(Entite.class, 4)).thenReturn(entiteParent);
		Mockito.when(adsRepository.get(Entite.class, 5)).thenReturn(entiteParent);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getEntiteRemplacee().getIdEntite())).thenReturn(
				entiteRemplacee);

		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Entite entite = (Entite) invocation.getArguments()[0];
				entite.setIdEntite(1);
				return entite;
			}
		}).when(adsRepository).persistEntity(Mockito.isA(Entite.class), Mockito.isA(EntiteHisto.class));

		List<String> existingServiCodes = new ArrayList<String>();
		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getAllServiCodes()).thenReturn(existingServiCodes);

		// entite a dupliquer et ses enfants pour checker les statuts
		Entite newEntites = new Entite();
		newEntites.setIdEntite(11);
		newEntites.setEntiteRemplacee(new Entite());
		newEntites.getEntiteRemplacee().setIdEntite(1);
		Entite newEntite1 = new Entite();
		newEntite1.setIdEntite(12);
		newEntite1.setEntiteRemplacee(new Entite());
		newEntite1.getEntiteRemplacee().setIdEntite(2);
		newEntites.getEntitesEnfants().add(newEntite1);
		Entite newEntite2 = new Entite();
		newEntite2.setIdEntite(13);
		newEntite2.setEntiteRemplacee(new Entite());
		newEntite2.getEntiteRemplacee().setIdEntite(3);
		newEntites.getEntitesEnfants().add(newEntite2);
		Entite newEntite21 = new Entite();
		newEntite21.setIdEntite(14);
		newEntite21.setEntiteRemplacee(new Entite());
		newEntite21.getEntiteRemplacee().setIdEntite(4);
		newEntite2.getEntitesEnfants().add(newEntite21);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);
		Mockito.when(treeRepository.getEntiteFromIdEntite(1)).thenReturn(newEntites);

		ReturnMessageDto errorMessages = new ReturnMessageDto();
		ITreeDataConsistencyService dataConsistencyService = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(
				dataConsistencyService.checkDataConsistencyForNewEntity(Mockito.isA(Entite.class),
						Mockito.isA(Entite.class), Mockito.any(ReturnMessageDto.class))).thenReturn(errorMessages);

		// entite a dupliquer et ses enfants pour checker les statuts
		EntiteDto root = new EntiteDto();
		root.setIdEntite(2);
		root.setLabel("DSI");
		root.setSigle("DSI");
		root.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		root.setEntiteParent(new EntiteDto());
		root.getEntiteParent().setIdEntite(1);
		EntiteDto e1 = new EntiteDto();
		e1.setIdEntite(3);
		e1.setLabel("SIE");
		e1.setSigle("SIE");
		e1.setIdStatut(StatutEntiteEnum.PREVISION.getIdRefStatutEntite());
		e1.setEntiteParent(root);
		root.getEnfants().add(e1);
		EntiteDto e2 = new EntiteDto();
		e2.setIdEntite(4);
		e2.setLabel("SED");
		e2.setSigle("SED");
		e2.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		e2.setEntiteParent(e1);
		root.getEnfants().add(e2);
		EntiteDto e21 = new EntiteDto();
		e21.setIdEntite(5);
		e21.setLabel("SED-DMD");
		e21.setSigle("SED-DMD");
		e21.setIdStatut(StatutEntiteEnum.INACTIF.getIdRefStatutEntite());
		e21.setEntiteParent(e2);
		e2.getEnfants().add(e21);

		ITreeConsultationService consultationService = Mockito.mock(ITreeConsultationService.class);
		Mockito.when(consultationService.getEntityByIdEntiteWithChildren(entiteDto.getIdEntite())).thenReturn(root);

		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(9005138)).thenReturn(9005138);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(9005138, resultPart)).thenReturn(resultPart);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "consultationService", consultationService);
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);
		try {
			service.duplicateEntity(9005138, entiteDto, resultPart, true);
		} catch (ReturnMessageDtoException e) {
			fail("error");
		}

		assertEquals(resultPart.getErrors().size(), 0);
		Mockito.verify(adsRepository, Mockito.times(2)).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
		assertEquals(resultPart.getInfos().size(), 1);
		assertEquals(resultPart.getInfos().get(0), "L'entité est bien créée.");
	}

	@Test
	public void checkEntiteParentWithCodeAS400Alphanumerique_codeNumerique() {

		Entite entiteParent = new Entite();
		entiteParent.setSiservInfo(new SiservInfo());
		entiteParent.getSiservInfo().setCodeServi("0122");

		ReturnMessageDto result = new ReturnMessageDto();

		CreateTreeService service = new CreateTreeService();
		result = service.checkEntiteParentWithCodeAS400Alphanumerique(entiteParent, result);

		assertEquals(result.getErrors().get(0),
				"Vous ne pouvez pas créer d'entité sous cette entité parent, car elle a un code AS400 numérique.");
	}

	@Test
	public void checkEntiteParentWithCodeAS400Alphanumerique_codeNumerique_casReelCCAS() {

		Entite entiteParent = new Entite();
		entiteParent.setSiservInfo(new SiservInfo());
		entiteParent.getSiservInfo().setCodeServi("5000           ");

		ReturnMessageDto result = new ReturnMessageDto();

		CreateTreeService service = new CreateTreeService();
		result = service.checkEntiteParentWithCodeAS400Alphanumerique(entiteParent, result);

		assertEquals(result.getErrors().get(0),
				"Vous ne pouvez pas créer d'entité sous cette entité parent, car elle a un code AS400 numérique.");
	}

	@Test
	public void checkEntiteParentWithCodeAS400Alphanumerique_codeAlphanumerique() {

		Entite entiteParent = new Entite();
		entiteParent.setSiservInfo(new SiservInfo());
		entiteParent.getSiservInfo().setCodeServi("DCA2  ");

		ReturnMessageDto result = new ReturnMessageDto();

		CreateTreeService service = new CreateTreeService();
		result = service.checkEntiteParentWithCodeAS400Alphanumerique(entiteParent, result);

		assertEquals(result.getErrors().get(0),
				"Vous ne pouvez pas créer d'entité sous cette entité parent, car elle a un code AS400 numérique.");
	}

	@Test
	public void checkEntiteParentWithCodeAS400Alphanumerique_codeAlpha() {

		Entite entiteParent = new Entite();
		entiteParent.setSiservInfo(new SiservInfo());
		entiteParent.getSiservInfo().setCodeServi("DCAA");

		ReturnMessageDto result = new ReturnMessageDto();

		CreateTreeService service = new CreateTreeService();
		result = service.checkEntiteParentWithCodeAS400Alphanumerique(entiteParent, result);

		assertTrue(result.getErrors().isEmpty());
	}
	
	@Test 
	public void deplaceFichesPosteFromEntityToOtherEntity_notAccessRight() {
		
		Integer idAgent = 9005138;
		Integer idEntiteSource = 1;
		Integer idEntiteCible = 2;
		
		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent)).thenReturn(9005138);

		ReturnMessageDto rmDto = new ReturnMessageDto();
		rmDto.getErrors().add("error droit");
		
		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(Mockito.anyInt(), Mockito.any(ReturnMessageDto.class))).thenReturn(rmDto);
		
		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);
		
		ReturnMessageDto result = service.deplaceFichesPosteFromEntityToOtherEntity(idAgent, idEntiteSource, idEntiteCible);
		assertEquals(result.getErrors().get(0), "error droit");
	}
	
	@Test 
	public void deplaceFichesPosteFromEntityToOtherEntity_notEntitySource() {
		
		Integer idAgent = 9005138;
		Integer idEntiteSource = 1;
		Integer idEntiteCible = 2;
		
		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent)).thenReturn(9005138);
		
		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(Mockito.anyInt(), Mockito.any(ReturnMessageDto.class))).thenReturn(new ReturnMessageDto());
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, idEntiteSource)).thenReturn(null);
		
		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReturnMessageDto result = service.deplaceFichesPosteFromEntityToOtherEntity(idAgent, idEntiteSource, idEntiteCible);
		assertEquals(result.getErrors().get(0), "L'entité source n'existe pas.");
	}
	
	@Test 
	public void deplaceFichesPosteFromEntityToOtherEntity_badStatutEntitySource() {
		
		Integer idAgent = 9005138;
		Integer idEntiteSource = 1;
		Integer idEntiteCible = 2;
		
		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent)).thenReturn(9005138);
		
		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(Mockito.anyInt(), Mockito.any(ReturnMessageDto.class))).thenReturn(new ReturnMessageDto());
		
		Entite entiteSource = new Entite();
		entiteSource.setStatut(StatutEntiteEnum.ACTIF);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, idEntiteSource)).thenReturn(entiteSource);
		
		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReturnMessageDto result = service.deplaceFichesPosteFromEntityToOtherEntity(idAgent, idEntiteSource, idEntiteCible);
		assertEquals(result.getErrors().get(0), "L'entité source n'est pas en statut transitoire.");
	}
	
	@Test 
	public void deplaceFichesPosteFromEntityToOtherEntity_notEntityCible() {
		
		Integer idAgent = 9005138;
		Integer idEntiteSource = 1;
		Integer idEntiteCible = 2;
		
		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent)).thenReturn(9005138);
		
		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(Mockito.anyInt(), Mockito.any(ReturnMessageDto.class))).thenReturn(new ReturnMessageDto());
		
		Entite entiteSource = new Entite();
		entiteSource.setStatut(StatutEntiteEnum.TRANSITOIRE);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, idEntiteSource)).thenReturn(entiteSource);
		Mockito.when(adsRepository.get(Entite.class, idEntiteCible)).thenReturn(null);
		
		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReturnMessageDto result = service.deplaceFichesPosteFromEntityToOtherEntity(idAgent, idEntiteSource, idEntiteCible);
		assertEquals(result.getErrors().get(0), "L'entité cible n'existe pas.");
	}
	
	@Test 
	public void deplaceFichesPosteFromEntityToOtherEntity_badStatutEntityCible() {
		
		Integer idAgent = 9005138;
		Integer idEntiteSource = 1;
		Integer idEntiteCible = 2;
		
		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent)).thenReturn(9005138);
		
		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(Mockito.anyInt(), Mockito.any(ReturnMessageDto.class))).thenReturn(new ReturnMessageDto());
		
		Entite entiteSource = new Entite();
		entiteSource.setStatut(StatutEntiteEnum.TRANSITOIRE);
		
		Entite entiteCible = new Entite();
		entiteCible.setStatut(StatutEntiteEnum.PREVISION);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, idEntiteSource)).thenReturn(entiteSource);
		Mockito.when(adsRepository.get(Entite.class, idEntiteCible)).thenReturn(entiteCible);
		
		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReturnMessageDto result = service.deplaceFichesPosteFromEntityToOtherEntity(idAgent, idEntiteSource, idEntiteCible);
		assertEquals(result.getErrors().get(0), "L'entité cible n'est pas en statut actif.");
	}
	
	@Test 
	public void deplaceFichesPosteFromEntityToOtherEntity_ok() {
		
		Integer idAgent = 9005138;
		Integer idEntiteSource = 1;
		Integer idEntiteCible = 2;
		
		IAgentMatriculeConverterService converterService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent)).thenReturn(9005138);
		
		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.verifAccessRightEcriture(Mockito.anyInt(), Mockito.any(ReturnMessageDto.class))).thenReturn(new ReturnMessageDto());
		
		Entite entiteSource = new Entite();
		entiteSource.setStatut(StatutEntiteEnum.TRANSITOIRE);
		
		Entite entiteCible = new Entite();
		entiteCible.setStatut(StatutEntiteEnum.ACTIF);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, idEntiteSource)).thenReturn(entiteSource);
		Mockito.when(adsRepository.get(Entite.class, idEntiteCible)).thenReturn(entiteCible);
		
		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.deplaceFichePosteFromEntityToOtherEntity(idEntiteSource, idEntiteCible, idAgent)).thenReturn(new ReturnMessageDto());
		
		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "converterService", converterService);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		
		ReturnMessageDto result = service.deplaceFichesPosteFromEntityToOtherEntity(idAgent, idEntiteSource, idEntiteCible);
		assertTrue(result.getErrors().isEmpty());
		Mockito.verify(sirhWsConsumer, Mockito.times(1)).deplaceFichePosteFromEntityToOtherEntity(idEntiteSource, idEntiteCible, idAgent);
	}

}
