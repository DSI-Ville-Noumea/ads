package nc.noumea.mairie.ads.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeEntite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.repository.IMairieRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.ads.service.ISiservUpdateService;
import nc.noumea.mairie.ads.service.ITreeDataConsistencyService;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.junit.Test;
import org.mockito.Mockito;
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

		ReturnMessageDto result = service.checkRequiredData(entiteDto);

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

		ReturnMessageDto result = service.checkRequiredData(entiteDto);

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

			ReturnMessageDto result = service.checkDataToCreateEntity(entiteDto);

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
	public void checkDataToModifyEntity() {

		EntiteDto entiteParentDto = new EntiteDto();
		entiteParentDto.setIdEntite(2);

		EntiteDto entiteDto = new EntiteDto();
		entiteDto.setSigle("SIGLE");
		entiteDto.setLabel("LABEL");
		entiteDto.setEntiteParent(entiteParentDto);

		Entite entite = new Entite();

		CreateTreeService service = new CreateTreeService();

		for (int i = 0; i < 4; i++) {

			entite.setStatut(StatutEntiteEnum.getStatutEntiteEnum(i));

			ReturnMessageDto result = service.checkDataToModifyEntity(entiteDto, entite);

			if (i == 0 || i == 1 || i == 2) {
				assertEquals(result.getErrors().size(), 0);
			}
			if (i == 3) {
				assertEquals(result.getErrors().size(), 1);
				assertEquals(result.getErrors().get(0), "Une entité en statut inactive ne peut pas être modifiée.");
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

		Entite entite = constructEntite(1, "DCAA", true);

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);

		ReturnMessageDto errorMessages = new ReturnMessageDto();
		errorMessages.getErrors().add("custom error");

		ITreeDataConsistencyService dataConsistencyService = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(dataConsistencyService.checkDataConsistencyForNewEntity(racine.get(0), entite)).thenReturn(
				errorMessages);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		try {
			service.saveNewEntityAndReturnMessages(entite, 9005138);
		} catch (ReturnMessageDtoException e) {
			ReturnMessageDto result = e.getErreur();
			assertEquals(result.getErrors().get(0), "custom error");
		}

		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
	}

	@Test
	public void saveNewEntityAndReturnMessages_ok() {

		Entite entite = constructEntite(1, "DCAA", true);

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);

		ReturnMessageDto errorMessages = new ReturnMessageDto();

		ITreeDataConsistencyService dataConsistencyService = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(dataConsistencyService.checkDataConsistencyForNewEntity(racine.get(0), entite)).thenReturn(
				errorMessages);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		ReturnMessageDto result = null;
		try {
			result = service.saveNewEntityAndReturnMessages(entite, 9005138);
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
		Mockito.when(dataConsistencyService.checkDataConsistencyForModifiedEntity(racine.get(0), entite)).thenReturn(
				errorMessages);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		try {
			service.saveModifiedEntityAndReturnMessages(entite, 9005138);
		} catch (ReturnMessageDtoException e) {
			ReturnMessageDto result = e.getErreur();
			assertEquals(result.getErrors().get(0), "custom error");
		}

		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
	}

	@Test
	public void saveModifiedEntityAndReturnMessages_ok() {

		Entite entite = constructEntite(1, "DCAA", true);

		List<Entite> racine = new ArrayList<Entite>();
		racine.add(new Entite());

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);

		ReturnMessageDto errorMessages = new ReturnMessageDto();

		ITreeDataConsistencyService dataConsistencyService = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(dataConsistencyService.checkDataConsistencyForModifiedEntity(racine.get(0), entite)).thenReturn(
				errorMessages);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		ReturnMessageDto result = null;
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

		EntiteDto entiteDto = constructEntiteDto(1, "DCAA", false);

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, entiteDto.getIdEntite())).thenReturn(null);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);

		ReturnMessageDto result = service.modifyEntity(entiteDto);

		assertEquals(result.getErrors().get(0), "L'entité n'existe pas.");
	}

	@Test
	public void createEntity() {

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
						Mockito.isA(Entite.class))).thenReturn(errorMessages);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		ReturnMessageDto result = service.createEntity(entiteDto);

		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
		assertEquals(result.getInfos().get(0), "L'entité est bien créée.");
	}
	
	@Test
	public void createOrUpdateSiServ() {

		ReturnMessageDto result = new  ReturnMessageDto();
				
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
				Mockito.isA(EntiteDto.class));

		Mockito.reset(siservUpdateService);
		
		entite.setStatut(StatutEntiteEnum.ACTIF);
		result = service.createOrUpdateSiServ(result, entiteDto, entite);
		
		Mockito.verify(siservUpdateService, Mockito.times(1)).updateSiservNwAndSiServ(Mockito.isA(Entite.class),
				Mockito.isA(EntiteDto.class));

		Mockito.reset(siservUpdateService);

		
		entite.setStatut(StatutEntiteEnum.TRANSITOIRE);
		result = service.createOrUpdateSiServ(result, entiteDto, entite);
		
		Mockito.verify(siservUpdateService, Mockito.times(1)).updateSiservNwAndSiServ(Mockito.isA(Entite.class),
				Mockito.isA(EntiteDto.class));

		Mockito.reset(siservUpdateService);

		
		entite.setStatut(StatutEntiteEnum.INACTIF);
		result = service.createOrUpdateSiServ(result, entiteDto, entite);
		
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservNwAndSiServ(Mockito.isA(Entite.class),
				Mockito.isA(EntiteDto.class));
	}

	@Test
	public void modifyEntity_notModifySiSerNw() {

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
		Mockito.when(dataConsistencyService.checkDataConsistencyForModifiedEntity(racine.get(0), entite)).thenReturn(
				errorMessages);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		
		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);

		ReturnMessageDto result = service.modifyEntity(entiteDto);

		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
		assertEquals(result.getInfos().get(0), "L'entité est bien modifiée.");
		assertEquals(result.getId().intValue(), 1);
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservNwAndSiServ(Mockito.isA(Entite.class),
				Mockito.isA(EntiteDto.class));
	}

	@Test
	public void modifyEntity() {

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
		Mockito.when(dataConsistencyService.checkDataConsistencyForModifiedEntity(racine.get(0), entite)).thenReturn(
				errorMessages);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTree()).thenReturn(racine);

		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		Mockito.when(siservUpdateService.updateSiservNwAndSiServ(Mockito.isA(Entite.class), Mockito.isA(EntiteDto.class))).thenReturn(new ReturnMessageDto());
		
		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);
		ReflectionTestUtils.setField(service, "dataConsistencyService", dataConsistencyService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);

		ReturnMessageDto result = service.modifyEntity(entiteDto);

		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
		assertEquals(result.getInfos().get(0), "L'entité est bien modifiée.");
		assertEquals(result.getId().intValue(), 1);
		Mockito.verify(siservUpdateService, Mockito.times(1)).updateSiservNwAndSiServ(Mockito.isA(Entite.class),
				Mockito.isA(EntiteDto.class));
	}

	@Test
	public void checkDataToDeleteEntity_entityNotExist() {

		CreateTreeService service = new CreateTreeService();
		ReturnMessageDto result = service.checkDataToDeleteEntity(null);

		assertEquals(result.getErrors().get(0), "L'entité n'existe pas.");
	}

	@Test
	public void checkDataToDeleteEntity_noChild() {

		Entite entite = constructEntite(1, "DCAA", false);

		CreateTreeService service = new CreateTreeService();
		ReturnMessageDto result = service.checkDataToDeleteEntity(entite);

		assertTrue(result.getErrors().isEmpty());
	}

	@Test
	public void checkDataToDeleteEntity_HaveChildren() {

		Entite entite = constructEntite(1, "DCAA", true);

		CreateTreeService service = new CreateTreeService();
		ReturnMessageDto result = service.checkDataToDeleteEntity(entite);

		assertEquals(result.getErrors().get(0), "L'entité ne peut être supprimée, car elle a un ou des entités fille.");
	}

	@Test
	public void deleteEntity_errorFichePosteSirh() {

		Integer idAgent = 9005138;
		Integer idEntite = 1;

		Entite entite = constructEntite(idEntite, "DCAA", false);

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, idEntite)).thenReturn(entite);

		ReturnMessageDto rmd = new ReturnMessageDto();
		rmd.getErrors().add("error delete Fiche Poste");

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.deleteFichesPosteByIdEntite(entite.getIdEntite(), idAgent)).thenReturn(rmd);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);

		ReturnMessageDto result = service.deleteEntity(idEntite, idAgent);

		assertEquals(result.getErrors().get(0), "error delete Fiche Poste");
		Mockito.verify(adsRepository, Mockito.never()).removeEntiteAvecPersistHisto(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
	}

	@Test
	public void deleteEntity_ok() {

		Integer idAgent = 9005138;
		Integer idEntite = 1;

		Entite entite = constructEntite(idEntite, "DCAA", false);

		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, idEntite)).thenReturn(entite);

		ReturnMessageDto rmd = new ReturnMessageDto();

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.deleteFichesPosteByIdEntite(entite.getIdEntite(), idAgent)).thenReturn(rmd);

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);

		ReturnMessageDto result = service.deleteEntity(idEntite, idAgent);

		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "L'entité est bien supprimée.");
		Mockito.verify(adsRepository, Mockito.times(1)).removeEntiteAvecPersistHisto(Mockito.isA(Entite.class),
				Mockito.isA(EntiteHisto.class));
	}
	
	@Test
	public void checkTypeEntiteAS400ToModify_returnError() {

		ReturnMessageDto result = new ReturnMessageDto();
		
		// liste entite AS400
		TypeEntite typeEntiteAS400_1 = new TypeEntite();
		typeEntiteAS400_1.setIdTypeEntite(1);
		typeEntiteAS400_1.setEntiteAs400(true);
		typeEntiteAS400_1.setLabel("entite AS400 1");
		
		TypeEntite typeEntiteAS400_2 = new TypeEntite();
		typeEntiteAS400_2.setIdTypeEntite(2);
		typeEntiteAS400_2.setEntiteAs400(true);
		typeEntiteAS400_2.setLabel("entite AS400 2");
		
		EntiteDto entiteParentDto = new EntiteDto();
		entiteParentDto.setIdEntite(2);

		ReferenceDto refDto = new ReferenceDto();
		refDto.setId(1);
		
		EntiteDto entiteDto = new EntiteDto();
		entiteDto.setSigle("SIGLE");
		entiteDto.setLabel("LABEL");
		entiteDto.setEntiteParent(entiteParentDto);
		entiteDto.setTypeEntite(refDto);
		
		Entite entite = new Entite();
		entite.setStatut(StatutEntiteEnum.ACTIF);
		entite.setTypeEntite(typeEntiteAS400_2);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, entiteDto.getTypeEntite().getId())).thenReturn(typeEntiteAS400_1);
		Mockito.when(adsRepository.getListeTypeEntiteIsSuperEntiteAS400()).thenReturn(Arrays.asList(typeEntiteAS400_1, typeEntiteAS400_2));
		
		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		result = service.checkTypeEntiteAS400ToModify(result, entiteDto, entite);
		
		assertEquals("Vous ne pouvez pas modifier le type d'une entité active ou en transition en entite AS400 1, entite AS400 2", result.getErrors().get(0));
	}
	
	@Test
	public void checkTypeEntiteAS400ToModify_sameType() {

		ReturnMessageDto result = new ReturnMessageDto();
		
		// liste entite AS400
		TypeEntite typeEntiteAS400_1 = new TypeEntite();
		typeEntiteAS400_1.setIdTypeEntite(1);
		typeEntiteAS400_1.setEntiteAs400(true);
		typeEntiteAS400_1.setLabel("entite AS400 1");
		
		TypeEntite typeEntiteAS400_2 = new TypeEntite();
		typeEntiteAS400_2.setIdTypeEntite(2);
		typeEntiteAS400_2.setEntiteAs400(true);
		typeEntiteAS400_2.setLabel("entite AS400 2");
		
		EntiteDto entiteParentDto = new EntiteDto();
		entiteParentDto.setIdEntite(2);

		ReferenceDto refDto = new ReferenceDto();
		refDto.setId(1);
		
		EntiteDto entiteDto = new EntiteDto();
		entiteDto.setSigle("SIGLE");
		entiteDto.setLabel("LABEL");
		entiteDto.setEntiteParent(entiteParentDto);
		entiteDto.setTypeEntite(refDto);
		
		Entite entite = new Entite();
		entite.setStatut(StatutEntiteEnum.ACTIF);
		entite.setTypeEntite(typeEntiteAS400_1);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, entiteDto.getTypeEntite().getId())).thenReturn(typeEntiteAS400_1);
		Mockito.when(adsRepository.getListeTypeEntiteIsSuperEntiteAS400()).thenReturn(Arrays.asList(typeEntiteAS400_1, typeEntiteAS400_2));
		
		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		result = service.checkTypeEntiteAS400ToModify(result, entiteDto, entite);
		
		assertTrue(result.getErrors().isEmpty());
	}
	
	@Test
	public void checkTypeEntiteAS400ToModify_testStatut() {

		ReturnMessageDto result = new ReturnMessageDto();
		
		// liste entite AS400
		TypeEntite typeEntiteAS400_1 = new TypeEntite();
		typeEntiteAS400_1.setIdTypeEntite(1);
		typeEntiteAS400_1.setEntiteAs400(true);
		typeEntiteAS400_1.setLabel("entite AS400 1");
		
		TypeEntite typeEntiteAS400_2 = new TypeEntite();
		typeEntiteAS400_2.setIdTypeEntite(2);
		typeEntiteAS400_2.setEntiteAs400(true);
		typeEntiteAS400_2.setLabel("entite AS400 2");
		
		EntiteDto entiteParentDto = new EntiteDto();
		entiteParentDto.setIdEntite(2);

		ReferenceDto refDto = new ReferenceDto();
		refDto.setId(1);
		
		EntiteDto entiteDto = new EntiteDto();
		entiteDto.setSigle("SIGLE");
		entiteDto.setLabel("LABEL");
		entiteDto.setEntiteParent(entiteParentDto);
		entiteDto.setTypeEntite(refDto);
		
		Entite entite = new Entite();
		entite.setTypeEntite(typeEntiteAS400_2);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(TypeEntite.class, entiteDto.getTypeEntite().getId())).thenReturn(typeEntiteAS400_1);
		Mockito.when(adsRepository.getListeTypeEntiteIsSuperEntiteAS400()).thenReturn(Arrays.asList(typeEntiteAS400_1, typeEntiteAS400_2));
		
		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		for(int i=0; i<4; i++) {
			entite.setStatut(StatutEntiteEnum.getStatutEntiteEnum(i));
			result = service.checkTypeEntiteAS400ToModify(result, entiteDto, entite);
			
			if(i == 0) {
				assertTrue(result.getErrors().isEmpty());
			}else{
				assertFalse(result.getErrors().isEmpty());
			}
		}
	}
}
