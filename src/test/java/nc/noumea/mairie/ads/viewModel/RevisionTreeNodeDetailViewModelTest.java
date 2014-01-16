package nc.noumea.mairie.ads.viewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import nc.noumea.mairie.ads.dto.NoeudDto;

import org.junit.Test;

public class RevisionTreeNodeDetailViewModelTest {

	@Test
	public void revisionTreeNodeSelectedChangeCommand_replaceSelectedTreeNode() {

		// Given
		RevisionTreeNodeDetailViewModel vM = new RevisionTreeNodeDetailViewModel();
		NoeudDto ndto = new NoeudDto();

		// When
		vM.revisionTreeNodeSelectedChangeCommand(ndto);

		// Then
		assertEquals(ndto, vM.getSelectedNoeud());
	}

	@Test
	public void updateSelectedRevision_SetSelectedNodeToNull() {

		// Given
		RevisionTreeNodeDetailViewModel vM = new RevisionTreeNodeDetailViewModel();
		vM.setSelectedNoeud(new NoeudDto());

		// When
		vM.updateSelectedRevision();

		// Then
		assertNull(vM.getSelectedNoeud());
	}
}
