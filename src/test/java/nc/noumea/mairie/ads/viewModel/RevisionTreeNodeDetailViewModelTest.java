package nc.noumea.mairie.ads.viewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.ReferenceDto;

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
	
	@Test
	public void getSelectedType_NoSelectedNode_returnNull() {
		
		// Given
		RevisionTreeNodeDetailViewModel vM = new RevisionTreeNodeDetailViewModel();
		
		// When
		ReferenceDto result = vM.getSelectedType();
		
		// Then
		assertNull(result);
	}
	
	@Test
	public void getSelectedType_selectedNodeTypeIsNull_returnNull() {
		
		// Given
		RevisionTreeNodeDetailViewModel vM = new RevisionTreeNodeDetailViewModel();
		vM.setSelectedNoeud(new NoeudDto());
		vM.getSelectedNoeud().setIdTypeNoeud(null);
		
		// When
		ReferenceDto result = vM.getSelectedType();
		
		// Then
		assertNull(result);
	}
	
	@Test
	public void getSelectedType_selectedNodeTypeIsNull_returnValueFromList() {
		
		// Given
		RevisionTreeNodeDetailViewModel vM = new RevisionTreeNodeDetailViewModel();
		vM.setSelectedNoeud(new NoeudDto());
		vM.getSelectedNoeud().setIdTypeNoeud(2l);
		
		ReferenceDto ref1 = new ReferenceDto();
		ref1.setId(1);
		ReferenceDto ref2 = new ReferenceDto();
		ref2.setId(2);
		vM.setDataList(Arrays.asList(ref1, ref2));
		
		
		// When
		ReferenceDto result = vM.getSelectedType();
		
		// Then
		assertEquals(ref2, result);
	}
	
	@Test
	public void setSelectedType_SetSelectedNodeTypeToGivenValue() {
		// Given
		RevisionTreeNodeDetailViewModel vM = new RevisionTreeNodeDetailViewModel();
		vM.setSelectedNoeud(new NoeudDto());
		vM.getSelectedNoeud().setIdTypeNoeud(2l);
		
		ReferenceDto ref1 = new ReferenceDto();
		ref1.setId(1);
		
		// When
		vM.setSelectedType(ref1);
		
		// Then
		assertEquals(1, (long) vM.getSelectedNoeud().getIdTypeNoeud());
	}
}
