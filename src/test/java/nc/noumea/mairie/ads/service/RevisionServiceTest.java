package nc.noumea.mairie.ads.service;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.repository.IRevisionRepository;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

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
		assertEquals(rev1.getIdRevision(), result.get(1).getIdRevision());
	}
	
}
