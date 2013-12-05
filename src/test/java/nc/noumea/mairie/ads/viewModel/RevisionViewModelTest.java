package nc.noumea.mairie.ads.viewModel;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ads.dto.RevisionDto;

import org.junit.Test;

public class RevisionViewModelTest {

	@Test
	public void updateSelectedRevision_SetParameterAsCurrentRevision() {
	
		// Given
		RevisionDto rev = new RevisionDto();
		
		RevisionViewModel model = new RevisionViewModel();
		
		// When
		model.updateSelectedRevision(rev);
		
		// Then
		assertEquals(rev, model.getRevision());
	
	}
}
