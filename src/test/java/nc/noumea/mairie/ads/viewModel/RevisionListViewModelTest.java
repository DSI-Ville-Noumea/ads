package nc.noumea.mairie.ads.viewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.IRevisionService;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class RevisionListViewModelTest {

	@Test
	public void revisionListChanged_GetNewListFromService() {
		
		// Given
		RevisionDto rev3 = new RevisionDto();
		rev3.setIdRevision(3);
		rev3.setDateEffet(new DateTime(2013, 12, 20, 8, 0, 0, 0).toDate());
		RevisionDto rev2 = new RevisionDto();
		rev2.setIdRevision(2);
		rev2.setDateEffet(new DateTime(2013, 12, 19, 8, 0, 0, 0).toDate());
		
		IRevisionService rS = Mockito.mock(IRevisionService.class);
		Mockito.when(rS.getRevisionsByDateEffetDesc()).thenReturn(Arrays.asList(rev3, rev2));
		
		RevisionListViewModel vM = new RevisionListViewModel();
		ReflectionTestUtils.setField(vM, "revisionService", rS);
		vM.setSelectedRevision(new RevisionDto());
		
		// When
		vM.revisionListChanged();
		
		// Then
		assertEquals(2, vM.getRevisions().size());
		assertEquals(rev3, vM.getRevisions().get(0));
		assertEquals(rev2, vM.getRevisions().get(1));
		assertNull(vM.getSelectedRevision());
		
	}
}
