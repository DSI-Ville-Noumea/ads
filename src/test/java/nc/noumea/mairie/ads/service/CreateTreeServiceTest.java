package nc.noumea.mairie.ads.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.TypeEntite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.repository.ISirhRepository;
import nc.noumea.mairie.ads.service.impl.CreateTreeService;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class CreateTreeServiceTest {

	@Test
	public void buildCoreEntites_recursiveBuildEntiteFromDto() {
		
		// Given
		EntiteDto ne = new EntiteDto();
		ne.setLabel("TestLabel2");
		ne.setIdTypeEntite(7);
		ne.setSigle("NICA");
		
		EntiteDto n = new EntiteDto();
		n.setCodeServi("DCDB");
		n.setLabel("TestLabel");
		n.setIdTypeEntite(6);
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
	public void createTreeFromEntites_returnEmptyResultDto() {
		
		// Given
		
		EntiteDto n = new EntiteDto();
		n.setLabel("TestLabel");
		n.setSigle("NICO");
		
		IAdsRepository adsR = Mockito.mock(IAdsRepository.class);
		
		Mockito.doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Entite entity = (Entite) invocation.getArguments()[0];
				assertEquals("NICO", entity.getSigle());
				assertEquals("TestLabel", entity.getLabel());
				
				return null;
			}
		}).when(adsR).persistEntity(Mockito.isA(Entite.class));
		
		ITreeDataConsistencyService tdcs = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(tdcs.checkDataConsistency((Mockito.isA(Entite.class)), Mockito.eq(false)))
			.thenReturn(new ArrayList<ErrorMessageDto>());

		ISirhRepository sirhRepo = Mockito.mock(ISirhRepository.class);
		Mockito.when(sirhRepo.getAllServiCodes()).thenReturn(new ArrayList<String>());

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsR);
		ReflectionTestUtils.setField(service, "dataConsistencyService", tdcs);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepo);
		
		// When
		List<ErrorMessageDto> result = service.createTreeFromEntites(n);
		
		// Then
		assertEquals(0, result.size());
	}
	
	@Test
	public void createTreeFromRevisionAndNoeuds_1dcerror_returnItInResultDto() {
		
		// Given
		EntiteDto n = new EntiteDto();
		n.setLabel("TestLabel");
		n.setSigle("NICO");
		
		IAdsRepository adsR = Mockito.mock(IAdsRepository.class);
		
		ErrorMessageDto er1 = new ErrorMessageDto();
		ITreeDataConsistencyService tdcs = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(tdcs.checkDataConsistency((Mockito.isA(Entite.class)), Mockito.eq(false))).thenReturn(Arrays.asList(er1));

		ISirhRepository sirhRepo = Mockito.mock(ISirhRepository.class);
		Mockito.when(sirhRepo.getAllServiCodes()).thenReturn(new ArrayList<String>());

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsR);
		ReflectionTestUtils.setField(service, "dataConsistencyService", tdcs);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepo);
		
		// When
		List<ErrorMessageDto> result = service.createTreeFromEntites(n);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(er1, result.get(0));

		Mockito.verify(adsR, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
	}

	@Test
	public void createCodeServiIfEmpty_ServiIsNotEmpty_doNothing() {

		// Given
		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.getSiservInfo().setCodeServi("AAAA");

		CreateTreeService service = new CreateTreeService();

		// When
		service.createCodeServiIfEmpty(n, new ArrayList<String>());

		// Then
		assertEquals("AAAA", n.getSiservInfo().getCodeServi());
	}

	@Test
	public void createCodeServiIfEmpty_ServiIsnull_doNothing() {

		// Given
		Entite n = new Entite();

		CreateTreeService service = new CreateTreeService();

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

		CreateTreeService service = new CreateTreeService();

		// When
		service.createCodeServiIfEmpty(n, new ArrayList<String>());

		// Then
		assertNull(n.getSiservInfo().getCodeServi());
	}

	@Test
	public void createCodeServiIfEmpty_pârentEntityCodeIsEmpty_doNothing() {

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		CreateTreeService service = new CreateTreeService();

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
		nparent.getSiservInfo().setCodeServi("DBAA");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("DBAA");

		CreateTreeService service = new CreateTreeService();

		// When
		service.createCodeServiIfEmpty(n, existingSiservs);

		// Then
		assertEquals("DBBA", n.getSiservInfo().getCodeServi());
		assertEquals(2, existingSiservs.size());
		assertTrue(existingSiservs.contains("DBBA"));
	}

	@Test
	public void createCodeServiIfEmpty_WSithOtherEntitysAtSameLevel_computeCode() {

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DBAA");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		Entite n1 = new Entite();
		n1.setSiservInfo(new SiservInfo());
		n1.getSiservInfo().setCodeServi("DBBA");
		n1.addParent(nparent);

		Entite n2 = new Entite();
		n2.setSiservInfo(new SiservInfo());
		n2.getSiservInfo().setCodeServi("DBCA");
		n2.addParent(nparent);

		CreateTreeService service = new CreateTreeService();

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("DBAA");
		existingSiservs.add("DBBA");
		existingSiservs.add("DBCA");

		// When
		service.createCodeServiIfEmpty(n, existingSiservs);

		// Then
		assertEquals("DBDA", n.getSiservInfo().getCodeServi());
		assertEquals(4, existingSiservs.size());
		assertTrue(existingSiservs.contains("DBDA"));
	}

	@Test
	public void createCodeServiIfEmpty_WSithOtherEntitysAtlowerLevel_computeCodes() {

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DBAA");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		CreateTreeService service = new CreateTreeService();

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("DBAA");

		// When
		service.createCodeServiIfEmpty(n, existingSiservs);

		// Then
		assertEquals("DBBA", n.getSiservInfo().getCodeServi());
		assertEquals(2, existingSiservs.size());
		assertTrue(existingSiservs.contains("DBAA"));
		assertTrue(existingSiservs.contains("DBBA"));
	}

	@Test
	public void createCodeServiIfEmpty_WSithOtherEntitysAtlowerLevelTooLow_dontComputeCodes() {

		// Given
		Entite nparent = new Entite();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DBCC");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		CreateTreeService service = new CreateTreeService();

		List<String> existingSiservs = new ArrayList<>();
		existingSiservs.add("DBCC");

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
		nparent.getSiservInfo().setCodeServi("DBDD");

		Entite n = new Entite();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		CreateTreeService service = new CreateTreeService();

		// When
		service.createCodeServiIfEmpty(n, new ArrayList<String>());

		// Then
		assertNull(n.getSiservInfo().getCodeServi());
	}
}
