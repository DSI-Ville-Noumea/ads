package nc.noumea.mairie.ads.viewModel;

import java.util.Arrays;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.ReferenceDto;

import nc.noumea.mairie.ads.view.tools.ViewModelHelper;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

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
		vM.getSelectedNoeud().setIdTypeNoeud(2);
		
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
		vM.getSelectedNoeud().setIdTypeNoeud(2);
		
		ReferenceDto ref1 = new ReferenceDto();
		ref1.setId(1);
		
		// When
		vM.setSelectedType(ref1);
		
		// Then
		assertEquals(1, (long) vM.getSelectedNoeud().getIdTypeNoeud());
	}
	
	@Test
	public void toggleEditModeGlobalCommand_UpdateEditMode() {
		
		// Given
		RevisionTreeNodeDetailViewModel vM = new RevisionTreeNodeDetailViewModel();
		
		// When
		vM.toggleEditModeGlobalCommand(true);
		
		// Then
		assertTrue(vM.isEditMode());
	}

	@Test
	public void toggleActifSelectedNodeCommand_changeNodeActif_fireEvent() {

		// Given
		NoeudDto ndto = new NoeudDto();
		ndto.setActif(true);

		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);

		RevisionTreeNodeDetailViewModel vM = new RevisionTreeNodeDetailViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);
		vM.setSelectedNoeud(ndto);

		// When
		vM.toggleActifSelectedNodeCommand();

		// Then
		assertFalse(ndto.isActif());
		Mockito.verify(vMh, Mockito.times(1)).postNotifyChange(null, null, ndto, "actif");
	}

	@Test
	public void toggleActifSelectedNodeCommand_selectedNodeIsNull_dontFireEvent() {

		// Given
		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);

		RevisionTreeNodeDetailViewModel vM = new RevisionTreeNodeDetailViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);

		// When
		vM.toggleActifSelectedNodeCommand();

		// Then
		Mockito.verify(vMh, Mockito.never()).postNotifyChange(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyString());
	}
}
