package nc.noumea.mairie.ads.viewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import nc.noumea.mairie.ads.service.IReferenceDataService;
import nc.noumea.mairie.ads.view.tools.ViewModelHelper;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.zkoss.zul.Window;

public class NewReferenceDataViewModelTest {

	@Test
	public void closeWindowCommand_callDetach() {
		
		// Given
		Window win = Mockito.mock(Window.class);
		NewReferenceDataViewModel model = new NewReferenceDataViewModel();
		
		// When
		model.closeWindowCommand(win);
		
		// Then
		Mockito.verify(win, Mockito.times(1)).detach();
	}
	
	@Test
	public void saveNewValueCommand_saveAndCallDetachOnWindow() {
		
		// Given
		Window win = Mockito.mock(Window.class);
		IReferenceDataService rDs = Mockito.mock(IReferenceDataService.class);
		String label = "test";
		
		ViewModelHelper vmH = Mockito.mock(ViewModelHelper.class);
		
		NewReferenceDataViewModel model = new NewReferenceDataViewModel();
		model.setNewLabel(label);
		ReflectionTestUtils.setField(model, "referenceDataService", rDs);
		ReflectionTestUtils.setField(model, "viewModelHelper", vmH);
		
		// When
		model.saveNewValueCommand(win);
		
		// Then
		Mockito.verify(win, Mockito.times(1)).detach();
		Mockito.verify(rDs, Mockito.times(1)).saveNewTypeEntite(label);
		Mockito.verify(vmH, Mockito.times(1)).postGlobalCommand(null, null, "typeEntiteListChangedGlobalCommand", null);
	}
	
	@Test
	public void checkValueCommand_LabelIsNullOrEmpty() {
		
		// Given
		IReferenceDataService rDs = Mockito.mock(IReferenceDataService.class);
		String label = "";
		
		NewReferenceDataViewModel model = new NewReferenceDataViewModel();
		model.setNewLabel(label);
		ReflectionTestUtils.setField(model, "referenceDataService", rDs);
		
		// When
		model.checkValueCommand(label);
		
		// Then
		assertEquals("Le label ne peut être vide !", model.getMessage());
		assertTrue(model.isCantSave());
	}
	
	@Test
	public void checkValueCommand_LabelIsAlreadyExisting() {
		
		// Given
		String label = "dds";
		IReferenceDataService rDs = Mockito.mock(IReferenceDataService.class);
		Mockito.when(rDs.doesTypeEntiteValueAlreadyExists(label)).thenReturn(true);
		
		NewReferenceDataViewModel model = new NewReferenceDataViewModel();
		model.setNewLabel(label);
		ReflectionTestUtils.setField(model, "referenceDataService", rDs);
		
		// When
		model.checkValueCommand(label);
		
		// Then
		assertEquals("Ce type existe déjà !", model.getMessage());
		assertTrue(model.isCantSave());
	}
	
	@Test
	public void checkValueCommand_valid() {
		
		// Given
		String label = "dds";
		IReferenceDataService rDs = Mockito.mock(IReferenceDataService.class);
		Mockito.when(rDs.doesTypeEntiteValueAlreadyExists(label)).thenReturn(false);
		
		NewReferenceDataViewModel model = new NewReferenceDataViewModel();
		model.setNewLabel(label);
		ReflectionTestUtils.setField(model, "referenceDataService", rDs);
		
		// When
		model.checkValueCommand(label);
		
		// Then
		assertNull(model.getMessage());
		assertFalse(model.isCantSave());
	}
}
