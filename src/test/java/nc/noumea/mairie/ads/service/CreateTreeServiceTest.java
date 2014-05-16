package nc.noumea.mairie.ads.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.beust.jcommander.internal.Lists;
import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.TypeNoeud;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.repository.ISirhRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;

import nc.noumea.mairie.sirh.domain.Siserv;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class CreateTreeServiceTest {

	@Test
	public void buildCoreNoeuds_recursiveBuildNoeudFromDto() {
		
		// Given
		Revision revision = new Revision();
		revision.setIdRevision(15);
		
		NoeudDto ne = new NoeudDto();
		ne.setIdService(0);
		ne.setLabel("TestLabel2");
		ne.setIdTypeNoeud(7);
		ne.setSigle("NICA");
		ne.setActif(false);
		
		NoeudDto n = new NoeudDto();
		n.setCodeServi("DCDB");
		n.setIdService(57);
		n.setLabel("TestLabel");
		n.setIdTypeNoeud(6);
		n.setSigle("NICO");
		n.setActif(true);
		n.getEnfants().add(ne);
		
		TypeNoeud tn6 = new TypeNoeud();
		TypeNoeud tn7 = new TypeNoeud();
		IAdsRepository adsR = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsR.get(TypeNoeud.class, 7)).thenReturn(tn7);
		Mockito.when(adsR.get(TypeNoeud.class, 6)).thenReturn(tn6);
		
		ITreeRepository tr = Mockito.mock(ITreeRepository.class);
		Mockito.when(tr.getNextServiceId()).thenReturn(89);
		
		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsR);
		ReflectionTestUtils.setField(service, "treeRepository", tr);
		
		// When
		Noeud result = service.buildCoreNoeuds(n, null, revision, new ArrayList<String>());
		
		// Then
		assertEquals(revision, result.getRevision());
		assertEquals("DCDB", result.getSiservInfo().getCodeServi());
		assertEquals(57, (int)result.getIdService());
		assertEquals("TestLabel", result.getLabel());
		assertEquals(tn6, result.getTypeNoeud());
		assertEquals("NICO", result.getSigle());
		assertEquals(1, result.getNoeudsEnfants().size());
		assertTrue(result.isActif());

		Noeud enfantResult = result.getNoeudsEnfants().iterator().next();
		assertEquals(revision, enfantResult.getRevision());
		assertNull(enfantResult.getSiservInfo().getCodeServi());
		assertEquals(89, (int)enfantResult.getIdService());
		assertEquals("TestLabel2", enfantResult.getLabel());
		assertEquals(tn7, enfantResult.getTypeNoeud());
		assertEquals("NICA", enfantResult.getSigle());
		assertEquals(0, enfantResult.getNoeudsEnfants().size());
		assertFalse(enfantResult.isActif());
	}
	
	@Test
	public void createTreeFromRevisionAndNoeuds_returnEmptyResultDto() {
		
		// Given
		RevisionDto revDto = new RevisionDto();
		revDto.setIdAgent(9005138);
		final Date dateEffet = new DateTime(2014, 1, 28, 0, 0, 0).toDate();
		revDto.setDateEffet(dateEffet);
		final Date dateDecret = new DateTime(2014, 1, 14, 0, 0, 0).toDate();
		revDto.setDateDecret(dateDecret);
		final String description = "description";
		revDto.setDescription(description);
		final Date dateModif = new DateTime(2014, 1, 16, 8, 25, 23).toDate();
		
		NoeudDto n = new NoeudDto();
		n.setIdService(57);
		n.setLabel("TestLabel");
		n.setSigle("NICO");
		
		IHelperService hS = Mockito.mock(IHelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(dateModif);
		
		IAdsRepository adsR = Mockito.mock(IAdsRepository.class);
		Mockito.doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Revision rev = (Revision) invocation.getArguments()[0];
				assertEquals(9005138, (int) rev.getIdAgent());
				assertEquals(dateEffet, rev.getDateEffet());
				assertEquals(dateDecret, rev.getDateDecret());
				assertEquals(description, rev.getDescription());
				assertEquals(dateModif, rev.getDateModif());

				return null;
			}
		}).when(adsR).persistEntity(Mockito.isA(Revision.class));
		
		Mockito.doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Noeud node = (Noeud) invocation.getArguments()[0];
				assertEquals("NICO", node.getSigle());
				assertEquals("TestLabel", node.getLabel());
				assertEquals(57, (int) node.getIdService());
				
				return null;
			}
		}).when(adsR).persistEntity(Mockito.isA(Noeud.class));
		
		ITreeDataConsistencyService tdcs = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(tdcs.checkDataConsistency(Mockito.isA(Revision.class), (Mockito.isA(Noeud.class)), Mockito.eq(false))).thenReturn(new ArrayList<ErrorMessageDto>());

		ISirhRepository sirhRepo = Mockito.mock(ISirhRepository.class);
		Mockito.when(sirhRepo.getAllServiCodes()).thenReturn(new ArrayList<String>());

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "dataConsistencyService", tdcs);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepo);
		
		// When
		List<ErrorMessageDto> result = service.createTreeFromRevisionAndNoeuds(revDto, n);
		
		// Then
		assertEquals(0, result.size());
	}
	
	@Test
	public void createTreeFromRevisionAndNoeuds_1dcerror_returnItInResultDto() {
		
		// Given
		RevisionDto revDto = new RevisionDto();
		revDto.setIdAgent(9005138);
		final Date dateEffet = new DateTime(2014, 1, 28, 0, 0, 0).toDate();
		revDto.setDateEffet(dateEffet);
		final Date dateDecret = new DateTime(2014, 1, 14, 0, 0, 0).toDate();
		revDto.setDateDecret(dateDecret);
		final String description = "description";
		revDto.setDescription(description);
		final Date dateModif = new DateTime(2014, 1, 16, 8, 25, 23).toDate();
		
		NoeudDto n = new NoeudDto();
		n.setIdService(57);
		n.setLabel("TestLabel");
		n.setSigle("NICO");
		
		IHelperService hS = Mockito.mock(IHelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(dateModif);
		
		IAdsRepository adsR = Mockito.mock(IAdsRepository.class);
		
		ErrorMessageDto er1 = new ErrorMessageDto();
		ITreeDataConsistencyService tdcs = Mockito.mock(ITreeDataConsistencyService.class);
		Mockito.when(tdcs.checkDataConsistency(Mockito.isA(Revision.class), (Mockito.isA(Noeud.class)), Mockito.eq(false))).thenReturn(Arrays.asList(er1));

		ISirhRepository sirhRepo = Mockito.mock(ISirhRepository.class);
		Mockito.when(sirhRepo.getAllServiCodes()).thenReturn(new ArrayList<String>());

		CreateTreeService service = new CreateTreeService();
		ReflectionTestUtils.setField(service, "adsRepository", adsR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "dataConsistencyService", tdcs);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepo);
		
		// When
		List<ErrorMessageDto> result = service.createTreeFromRevisionAndNoeuds(revDto, n);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(er1, result.get(0));

		Mockito.verify(adsR, Mockito.never()).persistEntity(Mockito.isA(Revision.class));
		Mockito.verify(adsR, Mockito.never()).persistEntity(Mockito.isA(Noeud.class));
	}

	@Test
	public void createCodeServiIfEmpty_ServiIsNotEmpty_doNothing() {

		// Given
		Noeud n = new Noeud();
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
		Noeud n = new Noeud();

		CreateTreeService service = new CreateTreeService();

		// When
		service.createCodeServiIfEmpty(n, new ArrayList<String>());

		// Then
		assertNull(n.getSiservInfo());
	}

	@Test
	public void createCodeServiIfEmpty_NodeHasNoParent_doNothing() {

		// Given
		Noeud n = new Noeud();
		n.setSiservInfo(new SiservInfo());

		CreateTreeService service = new CreateTreeService();

		// When
		service.createCodeServiIfEmpty(n, new ArrayList<String>());

		// Then
		assertNull(n.getSiservInfo().getCodeServi());
	}

	@Test
	public void createCodeServiIfEmpty_pârentNodeCodeIsEmpty_doNothing() {

		// Given
		Noeud nparent = new Noeud();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("");

		Noeud n = new Noeud();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		CreateTreeService service = new CreateTreeService();

		// When
		service.createCodeServiIfEmpty(n, new ArrayList<String>());

		// Then
		assertNull(n.getSiservInfo().getCodeServi());
	}

	@Test
	public void createCodeServiIfEmpty_NoOtherNodeAtSameLevel_computeCode() {

		// Given
		Noeud nparent = new Noeud();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DBAA");

		Noeud n = new Noeud();
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
	public void createCodeServiIfEmpty_WSithOtherNodesAtSameLevel_computeCode() {

		// Given
		Noeud nparent = new Noeud();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DBAA");

		Noeud n = new Noeud();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		Noeud n1 = new Noeud();
		n1.setSiservInfo(new SiservInfo());
		n1.getSiservInfo().setCodeServi("DBBA");
		n1.addParent(nparent);

		Noeud n2 = new Noeud();
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
	public void createCodeServiIfEmpty_WSithOtherNodesAtlowerLevel_computeCodes() {

		// Given
		Noeud nparent = new Noeud();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DBAA");

		Noeud n = new Noeud();
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
	public void createCodeServiIfEmpty_WSithOtherNodesAtlowerLevelTooLow_dontComputeCodes() {

		// Given
		Noeud nparent = new Noeud();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DBCC");

		Noeud n = new Noeud();
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
		Noeud nparent = new Noeud();
		nparent.setSiservInfo(new SiservInfo());
		nparent.getSiservInfo().setCodeServi("DBDD");

		Noeud n = new Noeud();
		n.setSiservInfo(new SiservInfo());
		n.addParent(nparent);

		CreateTreeService service = new CreateTreeService();

		// When
		service.createCodeServiIfEmpty(n, new ArrayList<String>());

		// Then
		assertNull(n.getSiservInfo().getCodeServi());
	}
}
