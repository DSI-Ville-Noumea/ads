package nc.noumea.mairie.ads.viewModel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.view.tools.ViewModelHelper;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class AdsViewModelTest {

	@Test
	public void cancelCommand_notSaving_setEditAndViewMode() {

		// Given
		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
		
		AdsViewModel vM = new AdsViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);
		vM.setEditMode(true);
		vM.setSaving(false);

		// When
		vM.cancelCommand();

		// Then
		assertFalse(vM.isEditMode());
		Mockito.verify(vMh, Mockito.times(1)).postGlobalCommand(null, null, "revisionListChanged", null);
	}

	@Test
	public void cancelCommand_isSaving_dontSetEditAndViewMode() {

		// Given
		AdsViewModel vM = new AdsViewModel();
		vM.setEditMode(true);
		vM.setSaving(true);

		// When
		vM.cancelCommand();

		// Then
		assertTrue(vM.isEditMode());
	}

	@Test
	public void saveCommand_isNotCurrentlySaving_postCommandToGetLatestTree() {

		// Given
		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
		
		AdsViewModel vM = new AdsViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);
		vM.setEditMode(true);
		vM.setSaving(false);

		// When
		vM.saveCommand();

		// Then
		assertTrue(vM.isSaving());
		assertTrue(vM.isEditMode());
		
		Mockito.verify(vMh, Mockito.times(1)).postGlobalCommand(null, null, "whatIsTheCurrentTree", null);
	}
	
	@Test
	public void saveCommand_isCurrentlySaving_doNothing() {

		// Given
		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
		
		AdsViewModel vM = new AdsViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);
		vM.setEditMode(true);
		vM.setSaving(true);

		// When
		vM.saveCommand();

		// Then
		assertTrue(vM.isSaving());
		assertTrue(vM.isEditMode());
		
		Mockito.verify(vMh, Mockito.never()).postGlobalCommand(null, null, "whatIsTheCurrentTree", null);
	}
	
	@Test
	public void thisIsTheCurrentTree_isalreadyCurrentlySaving_callCreateService() {

		// Given
		ICreateTreeService cts = Mockito.mock(ICreateTreeService.class);

		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
		
		AdsViewModel vM = new AdsViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);
		ReflectionTestUtils.setField(vM, "createTreeService", cts);
		vM.setEditMode(true);
		vM.setSaving(true);

		EntiteDto rootEntity = new EntiteDto();
		
		// When
		vM.thisIsTheCurrentTree(rootEntity);

		// Then
		assertFalse(vM.isSaving());
		assertFalse(vM.isEditMode());
		
		Mockito.verify(cts, Mockito.times(1)).createTreeFromEntites(rootEntity);
		Mockito.verify(vMh, Mockito.times(1)).postGlobalCommand(null, null, "listChanged", null);
	}
	
	@Test
	public void thisIsTheCurrentTree_isNotCurrentlySaving_doNothing() {

		// Given
		ICreateTreeService cts = Mockito.mock(ICreateTreeService.class);

		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
		
		AdsViewModel vM = new AdsViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);
		ReflectionTestUtils.setField(vM, "createTreeService", cts);
		vM.setEditMode(true);
		vM.setSaving(false);

		EntiteDto rootEntity = new EntiteDto();
		
		// When
		vM.thisIsTheCurrentTree(rootEntity);

		// Then
		assertFalse(vM.isSaving());
		assertTrue(vM.isEditMode());
		
		Mockito.verify(cts, Mockito.never()).createTreeFromEntites(rootEntity);
		Mockito.verify(vMh, Mockito.never()).postGlobalCommand(null, null, "revisionListChanged", null);
	}
}
