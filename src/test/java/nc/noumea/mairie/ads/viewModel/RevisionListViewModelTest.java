package nc.noumea.mairie.ads.viewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.IRevisionService;
import nc.noumea.mairie.ads.view.tools.ViewModelHelper;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class RevisionListViewModelTest {

	@Test
	public void revisionListChanged_updateListFromService() {

		// Given
		RevisionDto rev1 = new RevisionDto();
		RevisionDto rev2 = new RevisionDto();
		RevisionListItemViewModel vm2 = new RevisionListItemViewModel(rev2);

		IRevisionService rS = Mockito.mock(IRevisionService.class);
		Mockito.when(rS.getRevisionsByDateEffetDesc()).thenReturn(Arrays.asList(rev1, rev2));

		RevisionListViewModel vM = new RevisionListViewModel();
		ReflectionTestUtils.setField(vM, "revisionService", rS);
		vM.getRevisions().add(vm2);
		vM.setSelectedRevision(vm2);

		// When
		vM.revisionListChanged();

		// Then
		assertNull(vM.getSelectedRevision());
		assertEquals(2, vM.getRevisions().size());
		assertFalse(vm2.isEditModeStyle());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void newRevisionFromCurrentOne_setFirstRevisionOfListAsSelected_SendCommandupdateSelectedRevision() {

		// Given
		RevisionDto rev1 = new RevisionDto();
		RevisionListItemViewModel vm1 = new RevisionListItemViewModel(rev1);
		RevisionDto rev2 = new RevisionDto();
		RevisionListItemViewModel vm2 = new RevisionListItemViewModel(rev2);

		ViewModelHelper vmh = Mockito.mock(ViewModelHelper.class);

		RevisionListViewModel vM = new RevisionListViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vmh);
		vM.getRevisions().add(vm1);
		vM.getRevisions().add(vm2);

		// When
		vM.newRevisionFromCurrentOne();

		// Then
		assertEquals(vm1, vM.getSelectedRevision());
		assertTrue(vm1.isEditModeStyle());
		assertFalse(vm2.isEditModeStyle());

		Mockito.verify(vmh, Mockito.times(1)).postGlobalCommand(Mockito.anyString(), Mockito.anyString(),
				Mockito.eq("updateSelectedRevision"), (java.util.Map<String, Object>) Mockito.notNull());
	}
}
