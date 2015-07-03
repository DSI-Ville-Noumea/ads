package nc.noumea.mairie.ads.viewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReferenceDto;

import org.junit.Test;

public class TreeEntityDetailViewModelTest {

	@Test
	public void revisionTreeEntitySelectedChangeCommand_replaceSelectedTreeEntity() {

		// Given
		TreeEntityDetailViewModel vM = new TreeEntityDetailViewModel();
		EntiteDto ndto = new EntiteDto();

		// When
		vM.revisionTreeEntitySelectedChangeCommand(ndto);

		// Then
		assertEquals(ndto, vM.getSelectedEntite());
	}

	@Test
	public void updateSelectedRevision_SetSelectedEntityToNull() {

		// Given
		TreeEntityDetailViewModel vM = new TreeEntityDetailViewModel();
		vM.setSelectedEntite(new EntiteDto());

		// When
		vM.updateSelectedRevision();

		// Then
		assertNull(vM.getSelectedEntite());
	}
	
	@Test
	public void getSelectedType_NoSelectedEntity_returnNull() {
		
		// Given
		TreeEntityDetailViewModel vM = new TreeEntityDetailViewModel();
		
		// When
		ReferenceDto result = vM.getSelectedType();
		
		// Then
		assertNull(result);
	}
	
	@Test
	public void getSelectedType_selectedEntityTypeIsNull_returnNull() {
		
		// Given
		TreeEntityDetailViewModel vM = new TreeEntityDetailViewModel();
		vM.setSelectedEntite(new EntiteDto());
		vM.getSelectedEntite().setTypeEntite(null);
		
		// When
		ReferenceDto result = vM.getSelectedType();
		
		// Then
		assertNull(result);
	}
	
	@Test
	public void getSelectedType_selectedEntityTypeIsNull_returnValueFromList() {
		
		// Given
		ReferenceDto type = new ReferenceDto();
		type.setId(2);
		
		TreeEntityDetailViewModel vM = new TreeEntityDetailViewModel();
		vM.setSelectedEntite(new EntiteDto());
		vM.getSelectedEntite().setTypeEntite(type);
		
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
	public void setSelectedType_SetSelectedEntityTypeToGivenValue() {
		// Given
		ReferenceDto type = new ReferenceDto();
		type.setId(2);
		
		TreeEntityDetailViewModel vM = new TreeEntityDetailViewModel();
		vM.setSelectedEntite(new EntiteDto());
		vM.getSelectedEntite().setTypeEntite(type);
		
		ReferenceDto ref1 = new ReferenceDto();
		ref1.setId(1);
		
		// When
		vM.setSelectedType(ref1);
		
		// Then
		assertEquals(1, vM.getSelectedEntite().getTypeEntite().getId().intValue());
	}
	
	@Test
	public void toggleEditModeGlobalCommand_UpdateEditMode() {
		
		// Given
		TreeEntityDetailViewModel vM = new TreeEntityDetailViewModel();
		
		// When
		vM.toggleEditModeGlobalCommand(true);
		
		// Then
		assertTrue(vM.isEditMode());
	}

//	@Test
//	public void toggleActifSelectedEntityCommand_changeEntityActif_fireEvent() {
//
//		// Given
//		EntiteDto ndto = new EntiteDto();
//
//		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
//
//		TreeEntityDetailViewModel vM = new TreeEntityDetailViewModel();
//		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);
//		vM.setSelectedEntite(ndto);
//
//		// When
//		vM.toggleActifSelectedEntityCommand();
//
//		// Then
//		Mockito.verify(vMh, Mockito.times(1)).postNotifyChange(null, null, ndto, "actif");
//	}

//	@Test
//	public void toggleActifSelectedEntityCommand_selectedEntityIsNull_dontFireEvent() {
//
//		// Given
//		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
//
//		TreeEntityDetailViewModel vM = new TreeEntityDetailViewModel();
//		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);
//
//		// When
//		vM.toggleActifSelectedEntityCommand();
//
//		// Then
//		Mockito.verify(vMh, Mockito.never()).postNotifyChange(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyString());
//	}
}
