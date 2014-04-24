package nc.noumea.mairie.ads.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.TypeNoeud;
import nc.noumea.mairie.ads.dto.DiffRevisionDto;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.repository.IRevisionRepository;

import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.ads.repository.TreeRepository;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

public class RevisionServiceTest {

	@Test
	public void getRevisionsByDateEffetDesc_ReturnRepoResultAsDto() {
		
		// Given
		Revision rev1 = new Revision();
		rev1.setIdRevision(12);
		Revision rev2 = new Revision();
		rev2.setIdRevision(13);
		
		IRevisionRepository rR = Mockito.mock(IRevisionRepository.class);
		Mockito.when(rR.getAllRevisionsByDateEffetDesc()).thenReturn(Arrays.asList(rev2, rev1));
		
		RevisionService service = new RevisionService();
		ReflectionTestUtils.setField(service, "revisionRepository", rR);
		
		// When
		List<RevisionDto> result = service.getRevisionsByDateEffetDesc();
		
		// Then
		assertEquals(2, result.size());
		assertEquals(rev2.getIdRevision(), result.get(0).getIdRevision());
		assertTrue(result.get(0).isCanEdit());
		assertEquals(rev1.getIdRevision(), result.get(1).getIdRevision());
		assertFalse(result.get(1).isCanEdit());
	}

	@Test
	public void getLatestyRevisionForDate_CallRevisionRepo() {

		// Given
		Revision revision = new Revision();
		Date date = new Date();

		IRevisionRepository revRepo = Mockito.mock(IRevisionRepository.class);
		Mockito.when(revRepo.getLatestRevisionForDate(date)).thenReturn(revision);

		RevisionService revisionService = new RevisionService();
		ReflectionTestUtils.setField(revisionService, "revisionRepository", revRepo);

		// When
		Revision result = revisionService.getLatestyRevisionForDate(date);

		// Then
		assertEquals(revision, result);
	}

	@Test
	public void getRevisionById_callsRepositoryAndReturnResult() {

		// Given
		Revision revision = new Revision();
		revision.setIdRevision(7);

		IRevisionRepository revRepo = Mockito.mock(IRevisionRepository.class);
		Mockito.when(revRepo.getRevision(7l)).thenReturn(revision);

		RevisionService revisionService = new RevisionService();
		ReflectionTestUtils.setField(revisionService, "revisionRepository", revRepo);

		// When
		RevisionDto result = revisionService.getRevisionById(7l);

		// Then
		assertEquals(7, result.getIdRevision());
	}

	@Test
	public void getRevisionById_noRevision_ReturnNull() {

		// Given
		IRevisionRepository revRepo = Mockito.mock(IRevisionRepository.class);
		Mockito.when(revRepo.getRevision(7l)).thenReturn(null);

		RevisionService revisionService = new RevisionService();
		ReflectionTestUtils.setField(revisionService, "revisionRepository", revRepo);

		// When
		RevisionDto result = revisionService.getRevisionById(7l);

		// Then
		assertNull(result);
	}

	@Test
	public void rollbackToPreviousRevision_RevisionDoesNotExists_ReturnErrorMessage() {

		// Given
		IRevisionRepository revRepo = Mockito.mock(IRevisionRepository.class);
		Mockito.when(revRepo.getRevision(7l)).thenReturn(null);

		RevisionService revisionService = new RevisionService();
		ReflectionTestUtils.setField(revisionService, "revisionRepository", revRepo);

		// When
		List<ErrorMessageDto> result = revisionService.rollbackToPreviousRevision(new RevisionDto(), 7l);

		// Then
		assertEquals(1, result.size());
		assertEquals("La révision id [7] donnée en paramètre n'existe pas.", result.get(0).getMessage());
	}

	@Test
	public void rollbackToPreviousRevision_RevisionExistsButHasNotBeenAppliedYet_ReturnErrorMessage() {

		// Given
		Revision rev = new Revision();
		rev.setDateEffet(new LocalDate(2014, 5, 10).toDate());

		IRevisionRepository revRepo = Mockito.mock(IRevisionRepository.class);
		Mockito.when(revRepo.getRevision(7l)).thenReturn(rev);

		IHelperService hS = Mockito.mock(IHelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2014, 4, 19, 12, 7, 56).toDate());

		RevisionService revisionService = new RevisionService();
		ReflectionTestUtils.setField(revisionService, "revisionRepository", revRepo);
		ReflectionTestUtils.setField(revisionService, "helperService", hS);

		// When
		List<ErrorMessageDto> result = revisionService.rollbackToPreviousRevision(new RevisionDto(), 7l);

		// Then
		assertEquals(1, result.size());
		assertEquals("La révision id [7] n'a pas encore été appliquée, elle ne peut donc pas être réappliquée.", result.get(0).getMessage());
	}

	@Test
	public void rollbackToPreviousRevision_RevisionExists_SetDateEffet_DateDecret_AndDescription() {

		// Given
		Noeud rootNode = new Noeud();
		final Revision rev = new Revision();
		rev.setDateEffet(new LocalDate(2014, 4, 18).toDate());
		rev.setDateDecret(new LocalDate(2014, 4, 1).toDate());

		IRevisionRepository revRepo = Mockito.mock(IRevisionRepository.class);
		Mockito.when(revRepo.getRevision(7l)).thenReturn(rev);

		final Date currentDate = new DateTime(2014, 4, 19, 12, 7, 56).toDate();
		IHelperService hS = Mockito.mock(IHelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTreeForRevision(7l)).thenReturn(Arrays.asList(rootNode));

		RevisionDto dto = new RevisionDto();

		ICreateTreeService createTreeService = Mockito.mock(ICreateTreeService.class);
		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				RevisionDto dto = (RevisionDto) invocation.getArguments()[0];
				assertEquals(rev.getDateDecret(), dto.getDateDecret());
				assertEquals(currentDate, dto.getDateEffet());
				assertEquals("Rollback à la révision id [7].", dto.getDescription());
				return new ArrayList<ErrorMessageDto>();
			}
		}).when(createTreeService).createTreeFromRevisionAndNoeuds(dto, rootNode, true);

		RevisionService revisionService = new RevisionService();
		ReflectionTestUtils.setField(revisionService, "revisionRepository", revRepo);
		ReflectionTestUtils.setField(revisionService, "helperService", hS);
		ReflectionTestUtils.setField(revisionService, "treeRepository", treeRepository);
		ReflectionTestUtils.setField(revisionService, "createTreeService", createTreeService);

		// When
		List<ErrorMessageDto> result = revisionService.rollbackToPreviousRevision(dto, 7l);

		// Then
		assertEquals(0, result.size());
	}

	@Test
	public void getRevisionsDiff_revision1doesNotExists_ThrowException() {

		// Given
		Long idRevision = 1l;
		Long idRevision2 = 2l;

		IRevisionRepository revRepo = Mockito.mock(IRevisionRepository.class);
		Mockito.when(revRepo.getRevision(1l)).thenReturn(null);
		Mockito.when(revRepo.getRevision(2l)).thenReturn(new Revision());

		RevisionService revisionService = new RevisionService();
		ReflectionTestUtils.setField(revisionService, "revisionRepository", revRepo);

		// When
		try {
			revisionService.getRevisionsDiff(idRevision, idRevision2);
		} catch (RevisionNotFoundException e) {
			return;
		}

		// Then
		fail("Should have thrown exception !");
	}

	@Test
	public void getRevisionsDiff_revision2doesNotExists_ThrowException() {

		// Given
		Long idRevision = 1l;
		Long idRevision2 = 2l;

		IRevisionRepository revRepo = Mockito.mock(IRevisionRepository.class);
		Mockito.when(revRepo.getRevision(1l)).thenReturn(new Revision());
		Mockito.when(revRepo.getRevision(2l)).thenReturn(null);

		RevisionService revisionService = new RevisionService();
		ReflectionTestUtils.setField(revisionService, "revisionRepository", revRepo);

		// When
		try {
			revisionService.getRevisionsDiff(idRevision, idRevision2);
		} catch (RevisionNotFoundException e) {
			return;
		}

		// Then
		fail("Should have thrown exception !");
	}

	@Test
	public void getRevisionsDiff_revisionHasTwoMoreServices_ListItInAddedNodes() {

		// Given
		Revision rev1 = new Revision();
		rev1.setIdRevision(1);
		Long idRevision = 1l;
		Revision rev2 = new Revision();
		rev2.setIdRevision(2);
		Long idRevision2 = 2l;

		IRevisionRepository revRepo = Mockito.mock(IRevisionRepository.class);
		Mockito.when(revRepo.getRevision(1l)).thenReturn(rev1);
		Mockito.when(revRepo.getRevision(2l)).thenReturn(rev2);

		// Source tree
		List<Noeud> noeuds1 = new ArrayList<>();
		Noeud n1 = new Noeud();
		n1.setIdService(12);
		n1.setRevision(rev1);
		noeuds1.add(n1);

		// Target tree with two more children
		List<Noeud> noeuds2 = new ArrayList<>();
		Noeud n2 = new Noeud();
		n2.setIdService(12);
		noeuds2.add(n2);
		n2.setRevision(rev2);
		Noeud n3 = new Noeud();
		n3.setIdService(55);
		n3.addParent(n2);
		noeuds2.add(n3);
		n3.setRevision(rev2);
		Noeud n4 = new Noeud();
		n4.setIdService(56);
		n4.addParent(n3);
		noeuds2.add(n4);
		n4.setRevision(rev2);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTreeForRevision(rev1.getIdRevision())).thenReturn(noeuds1);
		Mockito.when(treeRepository.getWholeTreeForRevision(rev2.getIdRevision())).thenReturn(noeuds2);


		RevisionService revisionService = new RevisionService();
		ReflectionTestUtils.setField(revisionService, "revisionRepository", revRepo);
		ReflectionTestUtils.setField(revisionService, "treeRepository", treeRepository);

		// When
		DiffRevisionDto dto = revisionService.getRevisionsDiff(idRevision, idRevision2);

		// Then
		assertEquals(2, dto.getAddedNodes().size());
		assertEquals(0, dto.getRemovedNodes().size());
		assertEquals(0, dto.getMovedNodes().size());
		assertEquals(0, dto.getModifiedNodes().size());

		assertEquals(55, dto.getAddedNodes().get(0).getIdService());
		assertEquals(12, dto.getAddedNodes().get(0).getParent().getIdService());
		assertEquals(56, dto.getAddedNodes().get(1).getIdService());
		assertEquals(55, dto.getAddedNodes().get(1).getParent().getIdService());
	}

	@Test
	public void getRevisionsDiff_revisionHasTwoLessServices_ListItInRemovedNodes() {

		// Given
		Revision rev1 = new Revision();
		rev1.setIdRevision(1);
		Long idRevision = 1l;
		Revision rev2 = new Revision();
		rev2.setIdRevision(2);
		Long idRevision2 = 2l;

		IRevisionRepository revRepo = Mockito.mock(IRevisionRepository.class);
		Mockito.when(revRepo.getRevision(1l)).thenReturn(rev1);
		Mockito.when(revRepo.getRevision(2l)).thenReturn(rev2);

		// Source tree
		List<Noeud> noeuds1 = new ArrayList<>();
		Noeud n1 = new Noeud();
		n1.setIdService(12);
		n1.setRevision(rev1);
		noeuds1.add(n1);
		Noeud n2 = new Noeud();
		n2.setIdService(13);
		n2.setRevision(rev1);
		n2.setNoeudParent(n1);
		noeuds1.add(n2);
		Noeud n3 = new Noeud();
		n3.setIdService(14);
		n3.setRevision(rev1);
		n3.setNoeudParent(n1);
		noeuds1.add(n3);
		Noeud n4 = new Noeud();
		n4.setIdService(15);
		n4.setRevision(rev1);
		n4.setNoeudParent(n3);
		noeuds1.add(n4);

		// Target tree with two less children
		List<Noeud> noeuds2 = new ArrayList<>();
		Noeud n5 = new Noeud();
		n5.setIdService(12);
		n5.setRevision(rev2);
		noeuds2.add(n5);
		Noeud n6 = new Noeud();
		n6.setIdService(13);
		n6.setRevision(rev1);
		n6.setNoeudParent(n5);
		noeuds2.add(n6);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTreeForRevision(rev1.getIdRevision())).thenReturn(noeuds1);
		Mockito.when(treeRepository.getWholeTreeForRevision(rev2.getIdRevision())).thenReturn(noeuds2);


		RevisionService revisionService = new RevisionService();
		ReflectionTestUtils.setField(revisionService, "revisionRepository", revRepo);
		ReflectionTestUtils.setField(revisionService, "treeRepository", treeRepository);

		// When
		DiffRevisionDto dto = revisionService.getRevisionsDiff(idRevision, idRevision2);

		// Then
		assertEquals(0, dto.getAddedNodes().size());
		assertEquals(2, dto.getRemovedNodes().size());
		assertEquals(0, dto.getMovedNodes().size());
		assertEquals(0, dto.getModifiedNodes().size());

		assertEquals(14, dto.getRemovedNodes().get(0).getIdService());
		assertEquals(12, dto.getRemovedNodes().get(0).getParent().getIdService());
		assertEquals(15, dto.getRemovedNodes().get(1).getIdService());
		assertEquals(14, dto.getRemovedNodes().get(1).getParent().getIdService());
	}

	@Test
	public void getRevisionsDiff_revisionHasOneMovedService_ListItInMovedNodes() {

		// Given
		Revision rev1 = new Revision();
		rev1.setIdRevision(1);
		Long idRevision = 1l;
		Revision rev2 = new Revision();
		rev2.setIdRevision(2);
		Long idRevision2 = 2l;

		IRevisionRepository revRepo = Mockito.mock(IRevisionRepository.class);
		Mockito.when(revRepo.getRevision(1l)).thenReturn(rev1);
		Mockito.when(revRepo.getRevision(2l)).thenReturn(rev2);

		// Source tree
		List<Noeud> noeuds1 = new ArrayList<>();
		Noeud n1 = new Noeud();
		n1.setIdService(12);
		n1.setRevision(rev1);
		noeuds1.add(n1);
		Noeud n2 = new Noeud();
		n2.setIdService(13);
		n2.setRevision(rev1);
		n2.setNoeudParent(n1);
		noeuds1.add(n2);
		Noeud n3 = new Noeud();
		n3.setIdService(14);
		n3.setRevision(rev1);
		n3.setNoeudParent(n1);
		noeuds1.add(n3);
		Noeud n4 = new Noeud();
		n4.setIdService(15);
		n4.setRevision(rev1);
		n4.setNoeudParent(n3);
		noeuds1.add(n4);

		// Target tree with one moved node
		List<Noeud> noeuds2 = new ArrayList<>();
		Noeud n5 = new Noeud();
		n5.setIdService(12);
		n5.setRevision(rev2);
		noeuds2.add(n5);
		Noeud n6 = new Noeud();
		n6.setIdService(13);
		n6.setRevision(rev2);
		n6.setNoeudParent(n5);
		noeuds2.add(n6);
		Noeud n7 = new Noeud();
		n7.setIdService(14);
		n7.setRevision(rev2);
		n7.setNoeudParent(n6);
		noeuds2.add(n7);
		Noeud n8 = new Noeud();
		n8.setIdService(15);
		n8.setRevision(rev2);
		n8.setNoeudParent(n7);
		noeuds2.add(n8);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTreeForRevision(rev1.getIdRevision())).thenReturn(noeuds1);
		Mockito.when(treeRepository.getWholeTreeForRevision(rev2.getIdRevision())).thenReturn(noeuds2);


		RevisionService revisionService = new RevisionService();
		ReflectionTestUtils.setField(revisionService, "revisionRepository", revRepo);
		ReflectionTestUtils.setField(revisionService, "treeRepository", treeRepository);

		// When
		DiffRevisionDto dto = revisionService.getRevisionsDiff(idRevision, idRevision2);

		// Then
		assertEquals(0, dto.getAddedNodes().size());
		assertEquals(0, dto.getRemovedNodes().size());
		assertEquals(1, dto.getMovedNodes().size());
		assertEquals(0, dto.getModifiedNodes().size());

		assertEquals(14, dto.getMovedNodes().get(0).getKey().getIdService());
		assertEquals(12, dto.getMovedNodes().get(0).getKey().getParent().getIdService());

		assertEquals(14, dto.getMovedNodes().get(0).getValue().getIdService());
		assertEquals(13, dto.getMovedNodes().get(0).getValue().getParent().getIdService());
	}

	@Test
	public void getRevisionsDiff_revisionHasTwoModifiedServices_ListItInModifiedNodes() {

		// Given
		Revision rev1 = new Revision();
		rev1.setIdRevision(1);
		Long idRevision = 1l;
		Revision rev2 = new Revision();
		rev2.setIdRevision(2);
		Long idRevision2 = 2l;

		IRevisionRepository revRepo = Mockito.mock(IRevisionRepository.class);
		Mockito.when(revRepo.getRevision(1l)).thenReturn(rev1);
		Mockito.when(revRepo.getRevision(2l)).thenReturn(rev2);

		// Source tree
		List<Noeud> noeuds1 = new ArrayList<>();
		Noeud n1 = new Noeud();
		n1.setIdService(12);
		n1.setRevision(rev1);
		n1.setSiservInfo(new SiservInfo());
		noeuds1.add(n1);
		Noeud n2 = new Noeud();
		n2.setIdService(13);
		n2.setRevision(rev1);
		n2.setNoeudParent(n1);
		n2.setLabel("Aaaaaaaaaaaa");
		n2.setSigle("AAAA");
		n2.setTypeNoeud(new TypeNoeud());
		n2.getTypeNoeud().setIdTypeNoeud(1);
		n2.setSiservInfo(new SiservInfo());
		noeuds1.add(n2);
		Noeud n3 = new Noeud();
		n3.setIdService(14);
		n3.setRevision(rev1);
		n3.setNoeudParent(n1);
		n3.setSiservInfo(new SiservInfo());
		noeuds1.add(n3);
		Noeud n4 = new Noeud();
		n4.setIdService(15);
		n4.setRevision(rev1);
		n4.setNoeudParent(n3);
		n4.setSiservInfo(new SiservInfo());
		n4.getSiservInfo().setCodeServi("DADA");
		noeuds1.add(n4);

		// Target tree with two more children
		List<Noeud> noeuds2 = new ArrayList<>();
		Noeud n5 = new Noeud();
		n5.setIdService(12);
		n5.setRevision(rev2);
		n5.setSiservInfo(new SiservInfo());
		noeuds2.add(n5);
		Noeud n6 = new Noeud();
		n6.setIdService(13);
		n6.setRevision(rev2);
		n6.setNoeudParent(n5);
		n6.setLabel("Bbbbbbbbbbbb");
		n6.setSigle("BBBB");
		n6.setTypeNoeud(new TypeNoeud());
		n6.getTypeNoeud().setIdTypeNoeud(2);
		n6.setSiservInfo(new SiservInfo());
		noeuds2.add(n6);
		Noeud n7 = new Noeud();
		n7.setIdService(14);
		n7.setRevision(rev2);
		n7.setNoeudParent(n5);
		n7.setSiservInfo(new SiservInfo());
		noeuds2.add(n7);
		Noeud n8 = new Noeud();
		n8.setIdService(15);
		n8.setRevision(rev2);
		n8.setNoeudParent(n7);
		n8.setSiservInfo(new SiservInfo());
		n8.getSiservInfo().setCodeServi("DODO");
		noeuds2.add(n8);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTreeForRevision(rev1.getIdRevision())).thenReturn(noeuds1);
		Mockito.when(treeRepository.getWholeTreeForRevision(rev2.getIdRevision())).thenReturn(noeuds2);

		RevisionService revisionService = new RevisionService();
		ReflectionTestUtils.setField(revisionService, "revisionRepository", revRepo);
		ReflectionTestUtils.setField(revisionService, "treeRepository", treeRepository);

		// When
		DiffRevisionDto dto = revisionService.getRevisionsDiff(idRevision, idRevision2);

		// Then
		assertEquals(0, dto.getAddedNodes().size());
		assertEquals(0, dto.getRemovedNodes().size());
		assertEquals(0, dto.getMovedNodes().size());
		assertEquals(2, dto.getModifiedNodes().size());

		assertEquals(13, dto.getModifiedNodes().get(0).getKey().getIdService());
		assertEquals(12, dto.getModifiedNodes().get(0).getKey().getParent().getIdService());
		assertEquals(1, (int) dto.getModifiedNodes().get(0).getKey().getIdTypeNoeud());
		assertEquals("AAAA", dto.getModifiedNodes().get(0).getKey().getSigle());
		assertEquals("Aaaaaaaaaaaa", dto.getModifiedNodes().get(0).getKey().getLabel());

		assertEquals(13, dto.getModifiedNodes().get(0).getValue().getIdService());
		assertEquals(12, dto.getModifiedNodes().get(0).getValue().getParent().getIdService());
		assertEquals(2, (int) dto.getModifiedNodes().get(0).getValue().getIdTypeNoeud());
		assertEquals("BBBB", dto.getModifiedNodes().get(0).getValue().getSigle());
		assertEquals("Bbbbbbbbbbbb", dto.getModifiedNodes().get(0).getValue().getLabel());

		assertEquals(15, dto.getModifiedNodes().get(1).getKey().getIdService());
		assertEquals(14, dto.getModifiedNodes().get(1).getKey().getParent().getIdService());
		assertEquals("DADA", dto.getModifiedNodes().get(1).getKey().getCodeServi());

		assertEquals(15, dto.getModifiedNodes().get(1).getValue().getIdService());
		assertEquals(14, dto.getModifiedNodes().get(1).getValue().getParent().getIdService());
		assertEquals("DODO", dto.getModifiedNodes().get(1).getValue().getCodeServi());
	}
}
