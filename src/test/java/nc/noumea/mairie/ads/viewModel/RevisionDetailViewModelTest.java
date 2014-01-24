package nc.noumea.mairie.ads.viewModel;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ads.dto.RevisionDto;

import org.junit.Test;

public class RevisionDetailViewModelTest {

	@Test
	public void updateSelectedRevision_SetRevisionToParameter() {
		
		// Given
		RevisionDetailViewModel vM = new RevisionDetailViewModel();
		RevisionDto revision = new RevisionDto();
		
		// When
		vM.setRevision(revision);
		
		// Then
		assertEquals(revision, vM.getRevision());
	}
}
