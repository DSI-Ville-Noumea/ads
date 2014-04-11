package nc.noumea.mairie.ads.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.repository.IRevisionRepository;

import nc.noumea.mairie.ads.repository.ITreeRepository;
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
}
