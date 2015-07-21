package nc.noumea.mairie.ads.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.repository.ITreeRepository;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class TreeDataConsistencyServiceTest {

	@Test
	public void checkAllSiglesAreDifferent_allAreDifferents_oneIsEmpty_return1Error() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();
		Entite root = new Entite();

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkAllSiglesAreDifferent(root, errorMessages, null, new HashMap<String, Integer>(), null);

		// Then
		assertEquals(1, errorMessages.size());
		assertEquals("Le sigle est manquant sur une entité.", errorMessages.get(0).getMessage());
	}

	@Test
	public void checkAllSiglesAreDifferent_allAreDifferents_noneIsEmpty_returnNoError() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();
		Entite root = new Entite();
		root.setSigle("TOTO");

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkAllSiglesAreDifferent(root, errorMessages, null, new HashMap<String, Integer>(), null);

		// Then
		assertEquals(0, errorMessages.size());
	}

	@Test
	public void checkAllSiglesAreDifferent_TwoAreSame_noneIsEmpty_return1Error() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();
		Entite root = new Entite();
		root.setSigle("TOTO");
		Entite e1 = new Entite();
		e1.setSigle("TOTi");
		e1.setStatut(StatutEntiteEnum.ACTIF);
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setSigle("toti");
		e2.setStatut(StatutEntiteEnum.ACTIF);
		root.getEntitesEnfants().add(e2);

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkAllSiglesAreDifferent(root, errorMessages, null, new HashMap<String, Integer>(), null);

		// Then
		assertEquals(1, errorMessages.size());
		assertEquals("Le sigle 'TOTI' ne peut être dupliqué sur deux entités en statut \"actif\" au même moment.", errorMessages.get(0).getMessage());
	}

	@Test
	public void checkAllSiglesAreDifferent_TwoActifAndInactifAreSame_noneIsEmpty_return0Error() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();
		Entite root = new Entite();
		root.setSigle("TOTO");
		Entite e1 = new Entite();
		e1.setSigle("TOTi");
		e1.setStatut(StatutEntiteEnum.ACTIF);
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setSigle("toti");
		e2.setStatut(StatutEntiteEnum.INACTIF);
		root.getEntitesEnfants().add(e2);

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkAllSiglesAreDifferent(root, errorMessages, null, new HashMap<String, Integer>(), null);

		// Then
		assertEquals(0, errorMessages.size());
	}

	@Test
	public void checkDataConsistencyForNewEntity_createEntitePrevisionWithSameSigleExisting_return1Info() {

		// Given
		Entite root = new Entite();
		root.setSigle("TOTO");
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("AAAA");
		Entite e1 = new Entite();
		e1.setSigle("TOTi");
		e1.setStatut(StatutEntiteEnum.ACTIF);
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setSigle("toti");
		e2.setStatut(StatutEntiteEnum.INACTIF);
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi("DCAA");
		root.getEntitesEnfants().add(e2);
		
		Entite newEntity = new Entite();
		newEntity.setSigle("TOTi");
		newEntity.setStatut(StatutEntiteEnum.PREVISION);
		newEntity.setSiservInfo(new SiservInfo());
		newEntity.getSiservInfo().setCodeServi("DCBA");
		

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		ReturnMessageDto result = service.checkDataConsistencyForNewEntity(root, newEntity);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals("Attention, le sigle est déjà utilisé par une autre entité active.", result.getInfos().get(0));
	}

	@Test
	public void checkDataConsistencyForNewEntity_createEntitePrevisionWithOtherSigleExisting_return1Info() {

		// Given
		Entite root = new Entite();
		root.setSigle("TOTO");
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("AAAA");
		Entite e1 = new Entite();
		e1.setSigle("TOTi");
		e1.setStatut(StatutEntiteEnum.ACTIF);
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setSigle("toti");
		e2.setStatut(StatutEntiteEnum.INACTIF);
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi("DCAA");
		root.getEntitesEnfants().add(e2);
		
		Entite newEntity = new Entite();
		newEntity.setSigle("TEST");
		newEntity.setStatut(StatutEntiteEnum.PREVISION);
		newEntity.setSiservInfo(new SiservInfo());
		newEntity.getSiservInfo().setCodeServi("DCBA");
		

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		ReturnMessageDto result = service.checkDataConsistencyForNewEntity(root, newEntity);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkDataConsistencyForNewEntity_modifyEntitePrevisionWithSameSigleExisting_return1Info() {

		// Given
		Entite root = new Entite();
		root.setSigle("TOTO");
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("AAAA");
		Entite e1 = new Entite();
		e1.setSigle("TOTi");
		e1.setStatut(StatutEntiteEnum.ACTIF);
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setSigle("toti");
		e2.setStatut(StatutEntiteEnum.INACTIF);
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi("DCAA");
		root.getEntitesEnfants().add(e2);
		
		Entite entiteModifiee = new Entite();
		entiteModifiee.setSigle("TOTi");
		entiteModifiee.setStatut(StatutEntiteEnum.PREVISION);
		entiteModifiee.setSiservInfo(new SiservInfo());
		entiteModifiee.getSiservInfo().setCodeServi("DCBA");
		

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		ReturnMessageDto result = service.checkDataConsistencyForModifiedEntity(root, entiteModifiee);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals("Attention, le sigle est déjà utilisé par une autre entité active.", result.getInfos().get(0));
	}

	@Test
	public void checkDataConsistencyForNewEntity_modifyEntiteActiveWithSameSigleExisting_return1Info() {

		// Given
		Entite root = new Entite();
		root.setSigle("TOTO");
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("AAAA");
		Entite e1 = new Entite();
		e1.setSigle("TOTi");
		e1.setStatut(StatutEntiteEnum.ACTIF);
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setSigle("toti");
		e2.setStatut(StatutEntiteEnum.INACTIF);
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi("DCAA");
		root.getEntitesEnfants().add(e2);
		
		Entite entiteModifiee = new Entite();
		entiteModifiee.setSigle("TOTi");
		entiteModifiee.setStatut(StatutEntiteEnum.ACTIF);
		entiteModifiee.setSiservInfo(new SiservInfo());
		entiteModifiee.getSiservInfo().setCodeServi("DCBA");
		

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		ReturnMessageDto result = service.checkDataConsistencyForModifiedEntity(root, entiteModifiee);

		// Then
		assertEquals("Le sigle 'TOTI' ne peut être dupliqué sur deux entités en statut \"actif\" au même moment.", result.getErrors().get(0));
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkDataConsistencyForNewEntity_modifyEntitePrevisionWithOtherSigleExisting_return1Info() {

		// Given
		Entite root = new Entite();
		root.setSigle("TOTO");
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("AAAA");
		Entite e1 = new Entite();
		e1.setSigle("TOTi");
		e1.setStatut(StatutEntiteEnum.ACTIF);
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setSigle("toti");
		e2.setStatut(StatutEntiteEnum.INACTIF);
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi("DCAA");
		root.getEntitesEnfants().add(e2);
		
		Entite entiteModifiee = new Entite();
		entiteModifiee.setSigle("TEST");
		entiteModifiee.setStatut(StatutEntiteEnum.PREVISION);
		entiteModifiee.setSiservInfo(new SiservInfo());
		entiteModifiee.getSiservInfo().setCodeServi("DCBA");
		

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		ReturnMessageDto result = service.checkDataConsistencyForModifiedEntity(root, entiteModifiee);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkAllSiservCodesAreDifferent_allAreDifferents_noneIsEmpty_returnNoError() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();
		Entite root = new Entite();
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("DAAA");
		Entite e1 = new Entite();
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getEntitesEnfants().add(e1);

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkAllSiservCodesAreDifferent(root, errorMessages, null, new HashMap<String, Integer>());

		// Then
		assertEquals(0, errorMessages.size());
	}

	@Test
	public void checkAllSiservCodesAreDifferent_TwoAreSame_noneIsEmpty_return1Error() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();
		Entite root = new Entite();
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("DAAA");
		Entite e1 = new Entite();
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi("DBAA");
		root.getEntitesEnfants().add(e2);

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkAllSiservCodesAreDifferent(root, errorMessages, null, new HashMap<String, Integer>());

		// Then
		assertEquals(1, errorMessages.size());
		assertEquals("Le code SISERV 'DBAA' est dupliqué sur plus d'une entité.", errorMessages.get(0).getMessage());
	}

	@Test
	public void checkAllSiservCodesAreDifferent_BlankAndEmptyDontMatter_return0Errors() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();
		Entite root = new Entite();
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("");
		Entite e1 = new Entite();
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi(null);
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi(" ");
		root.getEntitesEnfants().add(e2);

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkAllSiservCodesAreDifferent(root, errorMessages, null, new HashMap<String, Integer>());

		// Then
		assertEquals(0, errorMessages.size());
	}

	@Test
	public void checkSiservCodesHierarchy_EverythingOk_NoErrors() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();

		Entite root = new Entite();
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("DAAA");
		Entite e1 = new Entite();
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi("DCAA");
		root.getEntitesEnfants().add(e2);

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkSiservCodesHierarchy(root, errorMessages, null);

		// Then
		assertEquals(0, errorMessages.size());
	}

	@Test
	public void checkSiservCodesHierarchy_TwoChildrenWithoutParentSiservCode_2Errors() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();

		Entite root = new Entite();
		root.setIdEntite(1);
		root.setSigle("DSI");
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("");
		Entite e1 = new Entite();
		e1.setSigle("SIE");
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setIdEntite(5);
		e2.setSigle("SED");
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi("");
		root.getEntitesEnfants().add(e2);
		Entite e21 = new Entite();
		e21.setSigle("SED-DMD");
		e21.setSiservInfo(new SiservInfo());
		e21.getSiservInfo().setCodeServi("DCCA");
		e2.getEntitesEnfants().add(e21);

		// replace the HashSet of enfants by LinkedHashset impl of Set in
		// order to keep the order of leaves and make sure the test passes
		LinkedHashSet<Entite> enfants = new LinkedHashSet<>();
		enfants.add(e1);
		enfants.add(e2);
		root.setEntitesEnfants(enfants);

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkSiservCodesHierarchy(root, errorMessages, null);

		// Then
		assertEquals(2, errorMessages.size());
		assertEquals("Le code SISERV de l'entité 'DSI' est vide alors que celui de sa sous entité 'SIE' est rempli.", errorMessages.get(0).getMessage());
		assertEquals("DSI", errorMessages.get(0).getSigle());
		assertEquals(1, (long) errorMessages.get(0).getIdEntite());
		assertEquals("Le code SISERV de l'entité 'SED' est vide alors que celui de sa sous entité 'SED-DMD' est rempli.", errorMessages.get(1).getMessage());
		assertEquals("SED", errorMessages.get(1).getSigle());
		assertEquals(5, (long) errorMessages.get(1).getIdEntite());
	}
	
	@Test
	public void checkSigleExisting_ko(){
		
		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);

		TreeDataConsistencyService service = new TreeDataConsistencyService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		
		assertFalse(service.checkSigleExisting("sigle"));
	}
	
	@Test
	public void checkSigleExisting_ok(){
		
		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteActiveFromSigle("sigle")).thenReturn(new Entite());

		TreeDataConsistencyService service = new TreeDataConsistencyService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		
		assertTrue(service.checkSigleExisting("sigle"));
	}
}
