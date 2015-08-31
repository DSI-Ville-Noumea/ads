package nc.noumea.mairie.ads.service.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeEntite;
import nc.noumea.mairie.ads.dto.ChangeStatutDto;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.repository.IMairieRepository;
import nc.noumea.mairie.domain.Siserv;
import nc.noumea.mairie.domain.SiservNw;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class SiservUpdateServiceTest extends AbstractDataServiceTest {

	@Test
	public void createCodeServiIfEmpty_ServiIsNotEmpty_doNothing() {

		// Given
		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.getSiservInfo().setCodeServi("AAAA");

		SiservUpdateService service = new SiservUpdateService();

		// When
		service.createCodeServiIfEmpty(n, new ArrayList<String>());

		// Then
		assertEquals("AAAA", n.getSiservInfo().getCodeServi());
	}

	@Test
	public void createCodeServiIfEmpty_ServiIsnull_doNothing() {

		// Given
		Entite n = new Entite();

		SiservUpdateService service = new SiservUpdateService();

		// When
		service.createCodeServiIfEmpty(n, new ArrayList<String>());

		// Then
		assertNull(n.getSiservInfo());
	}

	@Test
	public void createCodeServiIfEmpty_EntityHasNoParent_doNothing() {

		// Given
		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());

		SiservUpdateService service = new SiservUpdateService();

		// When
		service.createCodeServiIfEmpty(n, new ArrayList<String>());

		// Then
		assertNull(n.getSiservInfo().getCodeServi());
	}

	@Test
	public void createCodeServiIfEmpty_parentEntityCodeIsEmpty_doNothing() {

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		SiservUpdateService service = new SiservUpdateService();

		// When
		service.createCodeServiIfEmpty(n, new ArrayList<String>());

		// Then
		assertNull(n.getSiservInfo().getCodeServi());
	}

	@Test
	public void createCodeServiIfEmpty_NoOtherEntityAtSameLevel_computeCode() {

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DBFFDDEEFFGGHHAA");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("DBFFDDEEFFGGHHAA");

		SiservUpdateService service = new SiservUpdateService();

		// When
		service.createCodeServiIfEmpty(n, existingSiservs);

		// Then
		assertEquals("DBFFDDEEFFGGHHBA", n.getSiservInfo().getCodeServi());
		assertEquals(2, existingSiservs.size());
		assertTrue(existingSiservs.contains("DBFFDDEEFFGGHHBA"));
	}

	@Test
	public void createCodeServiIfEmpty_NoOtherEntityAtSameLevel_lastLevel_computeCode() {

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DBFFDDEEFFGGHHBA");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("DBFFDDEEFFGGHHBA");

		SiservUpdateService service = new SiservUpdateService();

		// When
		service.createCodeServiIfEmpty(n, existingSiservs);

		// Then
		assertEquals("DBFFDDEEFFGGHHBB", n.getSiservInfo().getCodeServi());
		assertEquals(2, existingSiservs.size());
		assertTrue(existingSiservs.contains("DBFFDDEEFFGGHHBB"));
	}

	@Test
	public void createCodeServiIfEmpty_WSithOtherEntitysAtSameLevel_computeCode() {

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DBFFDDEEFFGGHHAA");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		Entite n1 = new Entite();
		n1.setSiservInfo(new SiservInfo());
		n1.getSiservInfo().setCodeServi("DBFFDDEEFFGGHHBA");
		n1.addParent(nparent);

		Entite n2 = new Entite();
		n2.setSiservInfo(new SiservInfo());
		n2.getSiservInfo().setCodeServi("DBFFDDEEFFGGHHCA");
		n2.addParent(nparent);

		SiservUpdateService service = new SiservUpdateService();

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("DBFFDDEEFFGGHHAA");
		existingSiservs.add("DBFFDDEEFFGGHHBA");
		existingSiservs.add("DBFFDDEEFFGGHHCA");

		// When
		service.createCodeServiIfEmpty(n, existingSiservs);

		// Then
		assertEquals("DBFFDDEEFFGGHHDA", n.getSiservInfo().getCodeServi());
		assertEquals(4, existingSiservs.size());
		assertTrue(existingSiservs.contains("DBFFDDEEFFGGHHDA"));
	}

	@Test
	public void createCodeServiIfEmpty_WithOtherEntitysAtLowerLevel_computeCodes() {

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DBFFDDEEFFGGHHAA");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		SiservUpdateService service = new SiservUpdateService();

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("DBFFDDEEFFGGHHAA");

		// When
		service.createCodeServiIfEmpty(n, existingSiservs);

		// Then
		assertEquals("DBFFDDEEFFGGHHBA", n.getSiservInfo().getCodeServi());
		assertEquals(2, existingSiservs.size());
		assertTrue(existingSiservs.contains("DBFFDDEEFFGGHHAA"));
		assertTrue(existingSiservs.contains("DBFFDDEEFFGGHHBA"));
	}

	@Test
	public void createCodeServiIfEmpty_WithOtherEntitysAtLowerLevelTooLow_dontComputeCodes() {

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DBCCDDEEFFGGHHII");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		SiservUpdateService service = new SiservUpdateService();

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("DBCCDDEEFFGGHHII");

		// When
		service.createCodeServiIfEmpty(n, existingSiservs);

		// Then
		assertNull(n.getSiservInfo().getCodeServi());
		assertEquals(1, existingSiservs.size());
	}

	@Test
	public void createCodeServiIfEmpty_LastLevel_DontComputeCode() {

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DBDDEEFFGGHHIIJJ");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		SiservUpdateService service = new SiservUpdateService();

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("DBDDEEFFGGHHIIJJ");
		// When
		service.createCodeServiIfEmpty(n, existingSiservs);

		// Then
		assertNull(n.getSiservInfo().getCodeServi());
		assertEquals(1, existingSiservs.size());
	}

	@Test
	public void createCodeServiIfEmpty_withDoubleAA() {

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DAEAAAAAAAAAAAAA");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		SiservUpdateService service = new SiservUpdateService();

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("DAEAAAAAAAAAAAAA");
		// When
		service.createCodeServiIfEmpty(n, existingSiservs);

		// Then
		assertEquals("DAEBAAAAAAAAAAAA", n.getSiservInfo().getCodeServi());
		assertEquals(2, existingSiservs.size());
		assertTrue(existingSiservs.contains("DAEAAAAAAAAAAAAA"));
		assertTrue(existingSiservs.contains("DAEBAAAAAAAAAAAA"));
	}

	@Test
	public void createCodeServiIfEmpty_withTripleAAA() {

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DAABAAAAAAAAAAAA");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		SiservUpdateService service = new SiservUpdateService();

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("DAABAAAAAAAAAAAA");
		// When
		service.createCodeServiIfEmpty(n, existingSiservs);

		// Then
		assertEquals("DAABBAAAAAAAAAAA", n.getSiservInfo().getCodeServi());
		assertEquals(2, existingSiservs.size());
		assertTrue(existingSiservs.contains("DAABAAAAAAAAAAAA"));
		assertTrue(existingSiservs.contains("DAABBAAAAAAAAAAA"));
	}

	@Test
	public void createCodeServi_SuperEntiteAS400_EntiteParentIsSuperEntite() {

		TypeEntite typeEntite = new TypeEntite();
		typeEntite.setEntiteAs400(true);

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("ABBCAAAAAAAAAAAA");
		nparent.setTypeEntite(typeEntite);

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);
		n.setTypeEntite(typeEntite);

		SiservUpdateService service = new SiservUpdateService();

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("ABBCAAAAAAAAAAAA");
		// When
		service.createCodeServiIfEmpty(n, existingSiservs);

		// Then
		assertEquals("BAAAAAAAAAAAAAAA", n.getSiservInfo().getCodeServi());
		assertEquals(2, existingSiservs.size());
		assertTrue(existingSiservs.contains("ABBCAAAAAAAAAAAA"));
		assertTrue(existingSiservs.contains("BAAAAAAAAAAAAAAA"));
	}

	@Test
	public void createCodeServi_SuperEntiteAS400_EntiteParentNotIsSuperEntite() {

		TypeEntite typeEntite = new TypeEntite();
		typeEntite.setEntiteAs400(true);

		TypeEntite typeEntiteParent = new TypeEntite();
		typeEntiteParent.setEntiteAs400(false);

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("ABBCAAAAAAAAAAAA");
		nparent.setTypeEntite(typeEntiteParent);

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);
		n.setTypeEntite(typeEntite);

		SiservUpdateService service = new SiservUpdateService();

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("ABBCAAAAAAAAAAAA");
		// When
		service.createCodeServiIfEmpty(n, existingSiservs);

		// Then
		assertEquals("BAAAAAAAAAAAAAAA", n.getSiservInfo().getCodeServi());
		assertEquals(2, existingSiservs.size());
		assertTrue(existingSiservs.contains("ABBCAAAAAAAAAAAA"));
		assertTrue(existingSiservs.contains("BAAAAAAAAAAAAAAA"));
	}
	
	@Test
	public void createCodeServiIfEmpty_withLettreZForEntiteParent() {

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DAABZAAAAAAAAAAA");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		SiservUpdateService service = new SiservUpdateService();

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("DAABZAAAAAAAAAAA");
		// When
		service.createCodeServiIfEmpty(n, existingSiservs);

		// Then
		assertEquals("DAABZBAAAAAAAAAA", n.getSiservInfo().getCodeServi());
		assertEquals(2, existingSiservs.size());
		assertTrue(existingSiservs.contains("DAABZAAAAAAAAAAA"));
		assertTrue(existingSiservs.contains("DAABZBAAAAAAAAAA"));
	}

	@Test
	public void createCodeServiIfEmpty_withLettreZ_MemeNiveau() {

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DAABZAAAAAAAAAAA");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		SiservUpdateService service = new SiservUpdateService();

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("DAABZAAAAAAAAAAA");
		existingSiservs.add("DAABZBAAAAAAAAAA");
		existingSiservs.add("DAABZCAAAAAAAAAA");
		existingSiservs.add("DAABZDAAAAAAAAAA");
		existingSiservs.add("DAABZEAAAAAAAAAA");
		existingSiservs.add("DAABZFAAAAAAAAAA");
		existingSiservs.add("DAABZGAAAAAAAAAA");
		existingSiservs.add("DAABZHAAAAAAAAAA");
		existingSiservs.add("DAABZIAAAAAAAAAA");
		existingSiservs.add("DAABZJAAAAAAAAAA");
		existingSiservs.add("DAABZKAAAAAAAAAA");
		existingSiservs.add("DAABZLAAAAAAAAAA");
		existingSiservs.add("DAABZMAAAAAAAAAA");
		existingSiservs.add("DAABZNAAAAAAAAAA");
		existingSiservs.add("DAABZOAAAAAAAAAA");
		existingSiservs.add("DAABZPAAAAAAAAAA");
		existingSiservs.add("DAABZQAAAAAAAAAA");
		existingSiservs.add("DAABZRAAAAAAAAAA");
		existingSiservs.add("DAABZSAAAAAAAAAA");
		existingSiservs.add("DAABZTAAAAAAAAAA");
		existingSiservs.add("DAABZUAAAAAAAAAA");
		existingSiservs.add("DAABZVAAAAAAAAAA");
		existingSiservs.add("DAABZWAAAAAAAAAA");
		existingSiservs.add("DAABZXAAAAAAAAAA");
		existingSiservs.add("DAABZYAAAAAAAAAA");
		existingSiservs.add("DAABZZAAAAAAAAAA");
		// When
		service.createCodeServiIfEmpty(n, existingSiservs);

		// Then
		assertNull(n.getSiservInfo().getCodeServi());
	}

	@Test
	public void updateSiservByOneEntityOnly_BadStatus_nothingToDo() {

		Entite entite = constructEntite(1, "DCAA", false);

		ChangeStatutDto changeStatutDto = new ChangeStatutDto();
		changeStatutDto.setIdEntite(1);
		changeStatutDto.setIdAgent(9005138);
		changeStatutDto.setIdStatut(StatutEntiteEnum.PREVISION.getIdRefStatutEntite());

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		ReturnMessageDto result = service.createOrDisableSiservByOneEntityOnly(entite, changeStatutDto, null);

		assertTrue(result.getErrors().isEmpty());

		Mockito.verify(sirhRepository, Mockito.never()).persist(Mockito.isA(SiservNw.class));
		Mockito.verify(sirhRepository, Mockito.never()).persist(Mockito.isA(Siserv.class));
	}

	@Test
	public void updateSiservByOneEntityOnly_createSiServ() {

		Entite entiteParent = constructEntite(1, "DCBAAAAAAAAAAAAAA", false);
		Entite entite = constructEntite(2, "DCBBAAAAAAAAAAAAA", false);
		entite.addParent(entiteParent);

		SiservNw siservNwParent = Mockito.spy(constructSiServNw("DCBAAAAAAAAAAAAAA", true));
		List<SiservNw> existingSiservNws = new ArrayList<SiservNw>();
		existingSiservNws.add(siservNwParent);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getAllSiservNw()).thenReturn(existingSiservNws);

		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				SiservNw newSiservNw = (SiservNw) invocation.getArguments()[0];
				assertEquals("DCBB", newSiservNw.getSiServ().getServi());
				assertEquals(" ", newSiservNw.getCodeActif());
				return null;
			}
		}).when(sirhRepository).persist(Mockito.isA(SiservNw.class));

		ChangeStatutDto changeStatutDto = new ChangeStatutDto();
		changeStatutDto.setIdEntite(1);
		changeStatutDto.setIdAgent(9005138);
		changeStatutDto.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		ReturnMessageDto result = service.createOrDisableSiservByOneEntityOnly(entite, changeStatutDto, null);

		assertTrue(result.getErrors().isEmpty());

		Mockito.verify(sirhRepository, Mockito.times(1)).persist(Mockito.isA(SiservNw.class));
		Mockito.verify(sirhRepository, Mockito.times(1)).persist(Mockito.isA(Siserv.class));
	}

	@Test
	public void createOrUpdateSiservNwForOneEntity_noCodeService_LevelSuperior16() {

		Entite entite = constructEntite(2, null, false);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		try {
			service.createOrUpdateSiservNwForOneEntity(entite);
		} catch(ReturnMessageDtoException e) {
			Mockito.verify(sirhRepository, Mockito.never()).persist(Mockito.isA(SiservNw.class));
			Mockito.verify(sirhRepository, Mockito.never()).persist(Mockito.isA(Siserv.class));
			return;
		}
		
		fail("error");
	}

	@Test
	public void createOrUpdateSiservNwForOneEntity_NewSiservNw_NotSiserv() {

		Entite entiteParent = constructEntite(1, "DCBCBAAAAAAAAAAAA", false);
		Entite entite = constructEntite(2, "DCBCBBAAAAAAAAAAA", false);
		entite.addParent(entiteParent);

		Siserv siservParent = new Siserv();
		siservParent.setServi("DCBC");
		SiservNw siservNwPArent = Mockito.spy(constructSiServNw("DCBCBAAAAAAAAAAAA", true));
		siservNwPArent.setSiServ(siservParent);
		List<SiservNw> existingSiservNws = new ArrayList<SiservNw>();
		existingSiservNws.add(siservNwPArent);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getAllSiservNw()).thenReturn(existingSiservNws);

		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				SiservNw newSiservNw = (SiservNw) invocation.getArguments()[0];
				assertEquals("DCBC", newSiservNw.getSiServ().getServi());
				assertEquals(" ", newSiservNw.getCodeActif());
				return null;
			}
		}).when(sirhRepository).persist(Mockito.isA(SiservNw.class));

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		service.createOrUpdateSiservNwForOneEntity(entite);

		Mockito.verify(sirhRepository, Mockito.times(1)).persist(Mockito.isA(SiservNw.class));
		Mockito.verify(sirhRepository, Mockito.never()).persist(Mockito.isA(Siserv.class));
		// SISERVHIERARCHIE est mis a jour
		assertEquals(1, siservNwPArent.getSiservNwEnfant().size());
	}

	@Test
	public void createOrUpdateSiservNwForOneEntity_NewSiservNw_NewSiserv() {

		Entite entiteParent = constructEntite(1, "DCBAAAAAAAAAAAAAA", false);
		Entite entite = constructEntite(2, "DCBBAAAAAAAAAAAAA", false);
		entite.addParent(entiteParent);

		SiservNw siservNwParent = Mockito.spy(constructSiServNw("DCBAAAAAAAAAAAAAA", true));
		List<SiservNw> existingSiservNws = new ArrayList<SiservNw>();
		existingSiservNws.add(siservNwParent);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getAllSiservNw()).thenReturn(existingSiservNws);

		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				SiservNw newSiservNw = (SiservNw) invocation.getArguments()[0];
				assertEquals("DCBB", newSiservNw.getSiServ().getServi());
				assertEquals(" ", newSiservNw.getCodeActif());
				return null;
			}
		}).when(sirhRepository).persist(Mockito.isA(SiservNw.class));

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		service.createOrUpdateSiservNwForOneEntity(entite);

		Mockito.verify(sirhRepository, Mockito.times(1)).persist(Mockito.isA(SiservNw.class));
		Mockito.verify(sirhRepository, Mockito.times(1)).persist(Mockito.isA(Siserv.class));
		// SISERVHIERARCHIE est mis a jour
		assertEquals(1, siservNwParent.getSiservNwEnfant().size());
	}

	@Test
	public void createOrUpdateSiservNwForOneEntity_ModifySiservNwExisting_ModifySiservExisting() {

		Entite entiteParent = constructEntite(1, "DCBAAAAAAAAAAAAAA", false);
		Entite entite = constructEntite(2, "DCBBAAAAAAAAAAAAA", false);
		entite.addParent(entiteParent);

		SiservNw siservNwParent = Mockito.spy(constructSiServNw("DCBAAAAAAAAAAAAAA", false));
		SiservNw siservNwEnfant = Mockito.spy(constructSiServNw("DCBBAAAAAAAAAAAAA", false));

		Siserv siServ = Mockito.spy(new Siserv());
		siServ.setServi("DCBB");
		siServ.setCodeActif("I");

		siservNwEnfant.setSiServ(siServ);

		List<SiservNw> existingSiservNws = new ArrayList<SiservNw>();
		existingSiservNws.add(siservNwParent);
		existingSiservNws.add(siservNwEnfant);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getAllSiservNw()).thenReturn(existingSiservNws);

		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				SiservNw newSiservNw = (SiservNw) invocation.getArguments()[0];
				assertEquals("DCBB", newSiservNw.getSiServ().getServi());
				assertEquals(" ", newSiservNw.getCodeActif());
				return null;
			}
		}).when(sirhRepository).persist(Mockito.isA(SiservNw.class));

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		service.createOrUpdateSiservNwForOneEntity(entite);

		Mockito.verify(sirhRepository, Mockito.times(1)).persist(Mockito.isA(SiservNw.class));
		Mockito.verify(sirhRepository, Mockito.times(1)).persist(Mockito.isA(Siserv.class));
		// SISERVHIERARCHIE est mis a jour
		assertEquals(1, siservNwParent.getSiservNwEnfant().size());

		assertEquals(siServ.getCodeActif(), " ");
		assertEquals(siservNwEnfant.getCodeActif(), " ");
	}

	@Test
	public void updateSiservByOneEntityOnly_DisableEntity() {

		ChangeStatutDto changeStatutDto = new ChangeStatutDto();
		changeStatutDto.setIdStatut(StatutEntiteEnum.INACTIF.getIdRefStatutEntite());

		Entite entite = constructEntite(1, "CBCBAAAAAAAAAAAA", false);

		SiservNw siserNw1 = Mockito.spy(constructSiServNw(null, true));
		SiservNw siserNw2 = Mockito.spy(constructSiServNw(null, true));

		Siserv siServ = Mockito.spy(new Siserv());
		siServ.setCodeActif("");
		siServ.getSiservNw().add(siserNw1);
		siServ.getSiservNw().add(siserNw2);

		siserNw1.setCodeActif("");
		siserNw1.setSiServ(siServ);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getSiservNwByCode("CBCBAAAAAAAAAAAA")).thenReturn(siserNw1);

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		service.createOrDisableSiservByOneEntityOnly(entite, changeStatutDto, null);

		Mockito.verify(sirhRepository, Mockito.times(1)).persist(Mockito.isA(SiservNw.class));
		Mockito.verify(sirhRepository, Mockito.never()).persist(Mockito.isA(Siserv.class));

		assertEquals("I", siserNw1.getCodeActif());
		assertEquals("", siserNw2.getCodeActif());
		assertEquals("", siServ.getCodeActif());
	}

	@Test
	public void disableSiServNw_noCodeService() {

		// si plus de 16 niveau, le code service est NULL
		Entite entite = constructEntite(1, null, false);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);

		SiservUpdateService service = new SiservUpdateService();

		service.disableSiServNw(entite);

		Mockito.verify(sirhRepository, Mockito.never()).persist(Mockito.isA(SiservNw.class));
		Mockito.verify(sirhRepository, Mockito.never()).persist(Mockito.isA(Siserv.class));
	}

	@Test
	public void disableSiServNw_NotDisableSiserv() {

		// si plus de 16 niveau, le code service est NULL
		// ROOT
		Entite entite = constructEntite(1, "CBCBAAAAAAAAAAAA", false);

		SiservNw siserNw1 = Mockito.spy(constructSiServNw(null, true));
		SiservNw siserNw2 = Mockito.spy(constructSiServNw(null, true));

		Siserv siServ = Mockito.spy(new Siserv());
		siServ.setCodeActif("");
		siServ.getSiservNw().add(siserNw1);
		siServ.getSiservNw().add(siserNw2);

		siserNw1.setCodeActif("");
		siserNw1.setSiServ(siServ);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getSiservNwByCode("CBCBAAAAAAAAAAAA")).thenReturn(siserNw1);

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		service.disableSiServNw(entite);

		Mockito.verify(sirhRepository, Mockito.times(1)).persist(Mockito.isA(SiservNw.class));
		Mockito.verify(sirhRepository, Mockito.never()).persist(Mockito.isA(Siserv.class));

		assertEquals("I", siserNw1.getCodeActif());
		assertEquals("", siserNw2.getCodeActif());
		assertEquals("", siServ.getCodeActif());
	}

	@Test
	public void disableSiServNw_DisableSiserv_becauseOnlyAllSiServNwDisable() {

		// si plus de 16 niveau, le code service est NULL
		// ROOT
		Entite entite = constructEntite(1, "CBCBAAAAAAAAAAAA", false);

		SiservNw siserNw1 = Mockito.spy(constructSiServNw(null, true));
		SiservNw siserNw2 = Mockito.spy(constructSiServNw(null, false));

		Siserv siServ = Mockito.spy(new Siserv());
		siServ.setCodeActif("");
		siServ.getSiservNw().add(siserNw1);
		siServ.getSiservNw().add(siserNw2);

		siserNw1.setCodeActif("");
		siserNw1.setSiServ(siServ);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getSiservNwByCode("CBCBAAAAAAAAAAAA")).thenReturn(siserNw1);

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		service.disableSiServNw(entite);

		Mockito.verify(sirhRepository, Mockito.times(1)).persist(Mockito.isA(SiservNw.class));
		Mockito.verify(sirhRepository, Mockito.times(1)).persist(Mockito.isA(Siserv.class));

		assertEquals("I", siserNw1.getCodeActif());
		assertEquals("I", siserNw2.getCodeActif());
		assertEquals("I", siServ.getCodeActif());
	}

	@Test
	public void disableSiServNw_DisableSiserv_becauseOnlyOneSiServNw() {

		// si plus de 16 niveau, le code service est NULL
		// ROOT
		Entite entite = constructEntite(1, "CBCBAAAAAAAAAAAA", false);

		SiservNw siserNw1 = Mockito.spy(constructSiServNw(null, true));

		Siserv siServ = new Siserv();
		siServ.setCodeActif("");
		siServ.getSiservNw().add(siserNw1);

		siserNw1.setSiServ(siServ);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getSiservNwByCode("CBCBAAAAAAAAAAAA")).thenReturn(siserNw1);

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		service.disableSiServNw(entite);

		Mockito.verify(sirhRepository, Mockito.times(1)).persist(Mockito.isA(SiservNw.class));
		Mockito.verify(sirhRepository, Mockito.times(1)).persist(Mockito.isA(Siserv.class));

		assertEquals("I", siserNw1.getCodeActif());
		assertEquals("I", siServ.getCodeActif());
	}

	@Test
	public void updateSiservNwAndSiServ_notExist() {

		Entite entite = constructEntite(1, "CBCBAAAAAAAAAAAA", true);

		EntiteDto entiteDto = constructEntiteDto(1, "CBCBAAAAAAAAAAAA", true);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getSiservNwByCode(entite.getSiservInfo().getCodeServi())).thenReturn(null);

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		ReturnMessageDto result = service.updateSiservNwAndSiServ(entite, entiteDto, null);

		assertEquals(result.getErrors().get(0), "L'entité n'existe pas dans l'AS400.");
		Mockito.verify(sirhRepository, Mockito.never()).persist(Mockito.isA(SiservNw.class));
	}

	@Test
	public void updateSiservNwAndSiServ_SiservNwInactive() {

		Entite entite = constructEntite(1, "CBCBAAAAAAAAAAAA", true);

		EntiteDto entiteDto = constructEntiteDto(1, "CBCBAAAAAAAAAAAA", true);

		SiservNw siserNw1 = Mockito.spy(constructSiServNw("CBCBAAAAAAAAAAAA", false));

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getSiservNwByCode(entite.getSiservInfo().getCodeServi())).thenReturn(siserNw1);

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		ReturnMessageDto result = service.updateSiservNwAndSiServ(entite, entiteDto, null);

		assertEquals(result.getErrors().get(0), "Un service inactif ne peut pas être modifié dans l'AS400.");
		Mockito.verify(sirhRepository, Mockito.never()).persist(Mockito.isA(SiservNw.class));
	}

	@Test
	public void updateSiservNwAndSiServ_SiservNwModifie_PasEnfant() {

		Entite entite = constructEntite(1, "CBCBAAAAAAAAAAAA", true);

		EntiteDto entiteDto = constructEntiteDto(1, "CBCBAAAAAAAAAAAA", true);
		entiteDto.setLabelCourt("labelCourt");
		entiteDto.setSigle("SED-DMD-NW");

		SiservNw siserNw1 = Mockito.spy(constructSiServNw("CBCBCAAAAAAAAAAA", true));
		siserNw1.setSigle("");

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getSiservNwByCode(entite.getSiservInfo().getCodeServi())).thenReturn(siserNw1);

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		ReturnMessageDto result = service.updateSiservNwAndSiServ(entite, entiteDto, null);

		assertTrue(result.getErrors().isEmpty());
		assertEquals(siserNw1.getLiServ(), "labelCourt                                                  ");
		assertEquals(siserNw1.getSigle(), "SED-DMD-NW          ");
		Mockito.verify(sirhRepository, Mockito.times(1)).persist(Mockito.isA(SiservNw.class));
	}

	@Test
	public void updateSiservNwAndSiServ_SiservNwModifie_WithEnfants() {

		Entite entite = constructEntite(1, "CBCBAAAAAAAAAAAA", true);

		EntiteDto entiteDto = constructEntiteDto(1, "CBCBAAAAAAAAAAAA", true);
		entiteDto.setLabelCourt("labelCourt");
		entiteDto.setSigle("SED-DMD-NW");

		SiservNw siserNw1 = Mockito.spy(constructSiServNw("DCCBCAAAAAAAAAAA", true));
		siserNw1.setSigle("");

		SiservNw siserNwEnfant1 = Mockito.spy(constructSiServNw(null, true));
		SiservNw siserNwEnfant2 = Mockito.spy(constructSiServNw(null, false));

		siserNw1.getSiservNwEnfant().add(siserNwEnfant1);
		siserNw1.getSiservNwEnfant().add(siserNwEnfant2);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getSiservNwByCode(entite.getSiservInfo().getCodeServi())).thenReturn(siserNw1);

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		ReturnMessageDto result = service.updateSiservNwAndSiServ(entite, entiteDto, null);

		assertTrue(result.getErrors().isEmpty());
		assertEquals(siserNw1.getLiServ(), "labelCourt                                                  ");
		assertEquals(siserNw1.getSigle(), "SED-DMD-NW          ");
		assertEquals(siserNwEnfant1.getParentSigle(), "SED-DMD-NW          ");
		assertEquals(siserNwEnfant2.getParentSigle(), "");
		Mockito.verify(sirhRepository, Mockito.times(1)).persist(Mockito.isA(SiservNw.class));
	}

	@Test
	public void updateSiserv_NoChildSiServ() {

		SiservNw siserNw = Mockito.spy(constructSiServNw("CBCBAAAAAAAAAAAA", true));
		siserNw.setSigle("siglesiserNw");

		Siserv siServ = Mockito.spy(new Siserv());
		siServ.setCodeActif("");
		siServ.getSiservNw().add(siserNw);
		siServ.setSigle("siglesiServ");

		siserNw.setSiServ(siServ);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		service.updateSiserv(siserNw);

		assertEquals(siServ.getLiServ(), siserNw.getLiServ());
		assertEquals(siServ.getSigle(), "siglesiserNw        ");
	}

	@Test
	public void updateSiserv_WithChildSiServ() {

		SiservNw siserNw = Mockito.spy(constructSiServNw("CBCBAAAAAAAAAAAA", true));
		siserNw.setSigle("siglesiserNw");

		Siserv siServ = Mockito.spy(new Siserv());
		siServ.setCodeActif("");
		siServ.getSiservNw().add(siserNw);
		siServ.setSigle("siglesiServ");

		siserNw.setSiServ(siServ);

		Siserv siServEnfant = Mockito.spy(new Siserv());
		siServEnfant.setCodeActif("");
		siServEnfant.setSigle("sigleEnfant1");
		siServEnfant.setParentSigle("");

		Siserv siServEnfant2 = Mockito.spy(new Siserv());
		siServEnfant2.setCodeActif("I");
		siServEnfant2.setSigle("sigleEnfant2");
		siServEnfant2.setParentSigle("");

		List<Siserv> listSiservEnfants = new ArrayList<Siserv>();
		listSiservEnfants.add(siServEnfant);
		listSiservEnfants.add(siServEnfant2);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getSiservFromParentSigle(siServ.getSigle())).thenReturn(listSiservEnfants);

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		service.updateSiserv(siserNw);

		assertEquals(siServ.getLiServ(), siserNw.getLiServ());
		assertEquals(siServ.getSigle(), "siglesiserNw        ");
		assertEquals(siServEnfant.getParentSigle(), "siglesiserNw        ");
		assertEquals(siServEnfant2.getParentSigle(), "");
		assertEquals(siServEnfant.getSigle(), "sigleEnfant1");
		assertEquals(siServEnfant2.getSigle(), "sigleEnfant2");
	}

	@Test
	public void updateSiserv_Level5() {

		SiservNw siserNw = Mockito.spy(constructSiServNw("CBCBCAAAAAAAAAAA", true));
		siserNw.setSigle("siglesiserNw");

		Siserv siServ = Mockito.spy(new Siserv());
		siServ.setCodeActif("");
		siServ.getSiservNw().add(siserNw);
		siServ.setSigle("siglesiServ");
		siServ.setLiServ("");

		siserNw.setSiServ(siServ);

		Siserv siServEnfant = Mockito.spy(new Siserv());
		siServEnfant.setCodeActif("");
		siServEnfant.setSigle("sigleEnfant1");
		siServEnfant.setParentSigle("");

		Siserv siServEnfant2 = Mockito.spy(new Siserv());
		siServEnfant2.setCodeActif("I");
		siServEnfant2.setSigle("sigleEnfant2");
		siServEnfant2.setParentSigle("");

		List<Siserv> listSiservEnfants = new ArrayList<Siserv>();
		listSiservEnfants.add(siServEnfant);
		listSiservEnfants.add(siServEnfant2);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getSiservFromParentSigle(siServ.getSigle())).thenReturn(listSiservEnfants);

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		service.updateSiserv(siserNw);

		assertEquals(siServ.getLiServ(), "");
		assertEquals(siServ.getSigle(), "siglesiServ");
		assertEquals(siServEnfant.getParentSigle(), "");
		assertEquals(siServEnfant2.getParentSigle(), "");
		assertEquals(siServEnfant.getSigle(), "sigleEnfant1");
		assertEquals(siServEnfant2.getSigle(), "sigleEnfant2");
	}

}
