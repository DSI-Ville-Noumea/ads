package nc.noumea.mairie.ads.viewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.view.tools.ViewModelHelper;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class AdsViewModelTest {

	@Test
	public void newRevisionCommand_setEditAndViewMode() {

		// Given
		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
		
		AdsViewModel vM = new AdsViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);
		vM.setEditMode(false);

		// When
		vM.newRevisionCommand();

		// Then
		assertTrue(vM.isEditMode());
		Mockito.verify(vMh, Mockito.times(1)).postGlobalCommand(null, null, "newRevisionFromCurrentOne", null);
	}

	@Test
	public void cancelRevisionCommand_notSaving_setEditAndViewMode() {

		// Given
		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
		
		AdsViewModel vM = new AdsViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);
		vM.setEditMode(true);
		vM.setSaving(false);

		// When
		vM.cancelRevisionCommand();

		// Then
		assertFalse(vM.isEditMode());
		Mockito.verify(vMh, Mockito.times(1)).postGlobalCommand(null, null, "revisionListChanged", null);
	}

	@Test
	public void cancelRevisionCommand_isSaving_dontSetEditAndViewMode() {

		// Given
		AdsViewModel vM = new AdsViewModel();
		vM.setEditMode(true);
		vM.setSaving(true);

		// When
		vM.cancelRevisionCommand();

		// Then
		assertTrue(vM.isEditMode());
	}

	@Test
	public void saveRevisionCommand_isNotCurrentlySaving_postCommandToGetLatestTree() {

		// Given
		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
		
		AdsViewModel vM = new AdsViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);
		vM.setEditMode(true);
		vM.setSaving(false);

		// When
		vM.saveRevisionCommand();

		// Then
		assertTrue(vM.isSaving());
		assertTrue(vM.isEditMode());
		
		Mockito.verify(vMh, Mockito.times(1)).postGlobalCommand(null, null, "whatIsTheCurrentRevisionTree", null);
	}
	
	@Test
	public void saveRevisionCommand_isCurrentlySaving_doNothing() {

		// Given
		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
		
		AdsViewModel vM = new AdsViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);
		vM.setEditMode(true);
		vM.setSaving(true);

		// When
		vM.saveRevisionCommand();

		// Then
		assertTrue(vM.isSaving());
		assertTrue(vM.isEditMode());
		
		Mockito.verify(vMh, Mockito.never()).postGlobalCommand(null, null, "whatIsTheCurrentRevisionTree", null);
	}
	
	@Test
	public void thisIsTheCurrentRevisionTree_isalreadyCurrentlySaving_callCreateService() {

		// Given
		ICreateTreeService cts = Mockito.mock(ICreateTreeService.class);

		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
		
		AdsViewModel vM = new AdsViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);
		ReflectionTestUtils.setField(vM, "createTreeService", cts);
		vM.setEditMode(true);
		vM.setSaving(true);
		RevisionDto dto = new RevisionDto();
		vM.setSelectedRevision(dto);

		NoeudDto rootNode = new NoeudDto();
		
		// When
		vM.thisIsTheCurrentRevisionTree(rootNode);

		// Then
		assertFalse(vM.isSaving());
		assertFalse(vM.isEditMode());
		
		Mockito.verify(cts, Mockito.times(1)).createTreeFromRevisionAndNoeuds(dto, rootNode);
		Mockito.verify(vMh, Mockito.times(1)).postGlobalCommand(null, null, "revisionListChanged", null);
	}
	
	@Test
	public void thisIsTheCurrentRevisionTree_isNotCurrentlySaving_doNothing() {

		// Given
		ICreateTreeService cts = Mockito.mock(ICreateTreeService.class);

		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
		
		AdsViewModel vM = new AdsViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);
		ReflectionTestUtils.setField(vM, "createTreeService", cts);
		vM.setEditMode(true);
		vM.setSaving(false);
		RevisionDto dto = new RevisionDto();
		vM.setSelectedRevision(dto);

		NoeudDto rootNode = new NoeudDto();
		
		// When
		vM.thisIsTheCurrentRevisionTree(rootNode);

		// Then
		assertFalse(vM.isSaving());
		assertTrue(vM.isEditMode());
		
		Mockito.verify(cts, Mockito.never()).createTreeFromRevisionAndNoeuds(dto, rootNode);
		Mockito.verify(vMh, Mockito.never()).postGlobalCommand(null, null, "revisionListChanged", null);
	}
	
	@Test
	public void updateSelectedRevision_updateCurrentSelectedRevision() {
		
		// Given
		RevisionDto dto = new RevisionDto();
		
		AdsViewModel vM = new AdsViewModel();
		vM.setSelectedRevision(null);
		
		// When
		vM.updateSelectedRevision(dto);
		
		// Then
		assertEquals(dto, vM.getSelectedRevision());
	}

}
