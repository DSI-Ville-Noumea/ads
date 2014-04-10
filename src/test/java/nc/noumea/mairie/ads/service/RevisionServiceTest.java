package nc.noumea.mairie.ads.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.repository.IRevisionRepository;

import org.junit.Test;
import org.mockito.Mockito;
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
}
