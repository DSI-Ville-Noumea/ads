package nc.noumea.mairie.ads.viewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.service.ITreeConsultationService;
import nc.noumea.mairie.ads.view.tools.ViewModelHelper;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

public class TreeViewModelTest {

	@Test
	public void updateSelectedRevision_LoadTreeFromServiceAndBuildTreeView() {

		// Given
		EntiteDto rootNode = new EntiteDto();
		rootNode.setIdEntite(789456);
		rootNode.getEnfants().add(new EntiteDto());
		rootNode.getEnfants().get(0).setIdEntite(123456);

		ITreeConsultationService treeConsultationService = Mockito.mock(ITreeConsultationService.class);
		Mockito.when(treeConsultationService.getWholeTree()).thenReturn(rootNode);

		TreeViewModel vM = new TreeViewModel();
		ReflectionTestUtils.setField(vM, "treeConsultationService", treeConsultationService);

		// When
		vM.updateSelected();

		// Then
		assertEquals(789456, vM.getEntiteTree().getRoot().getData().getIdEntite().intValue());
		assertEquals(1, vM.getEntiteTree().getRoot().getChildren().size());
		assertEquals(123456, vM.getEntiteTree().getRoot().getChildren().get(0).getData().getIdEntite().intValue());
	}

	@Test
	public void updateSelectedRevision_RevisionIsNull_BuildDefaultTree() {

		ITreeConsultationService treeConsultationService = Mockito.mock(ITreeConsultationService.class);
		Mockito.when(treeConsultationService.getWholeTree()).thenReturn(new EntiteDto());
		// Given
		TreeViewModel vM = new TreeViewModel();
		ReflectionTestUtils.setField(vM, "treeConsultationService", treeConsultationService);

		// When
		vM.updateSelected();

		// Then
		assertNull(vM.getEntiteTree().getRoot().getData().getIdEntite());
		assertTrue(vM.getEntiteTree().getRoot().getChildren().isEmpty());
	}

	@Test
	public void createNewNodeCommand_createItAndSetSelectedNode() {

		// Given
		final TreeNode<EntiteDto> selectedTreeItem = new DefaultTreeNode<>(new EntiteDto(),
				new ArrayList<DefaultTreeNode<EntiteDto>>());

		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
		Mockito.doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Map<String, Object> args = (Map<String, Object>) invocation.getArguments()[3];
				assertEquals("NOUVEAU", selectedTreeItem.getChildren().get(0).getData().getSigle());
				return null;
			}

		})
				.when(vMh)
				.postGlobalCommand(Mockito.anyString(), Mockito.anyString(),
						Mockito.eq("revisionTreeNodeSelectedChangeCommand"), Mockito.isA(Map.class));

		TreeViewModel vM = new TreeViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);

		vM.setSelectedTreeItem(selectedTreeItem);

		// When
		vM.createNewNodeCommand();

		// Then
		assertEquals(1, selectedTreeItem.getChildren().size());
		assertNull(selectedTreeItem.getChildren().get(0).getData().getIdEntite());
		assertEquals("NOUVEAU", selectedTreeItem.getChildren().get(0).getData().getSigle());
		assertEquals(0, selectedTreeItem.getChildren().get(0).getChildren().size());
		assertEquals(selectedTreeItem.getChildren().get(0), vM.getSelectedTreeItem());
		assertEquals(selectedTreeItem.getData().getEnfants().get(0), selectedTreeItem.getChildren().get(0).getData());

		Mockito.verify(vMh, Mockito.times(1)).postGlobalCommand(Mockito.anyString(), Mockito.anyString(),
				Mockito.eq("revisionTreeNodeSelectedChangeCommand"), Mockito.isA(Map.class));
	}

	@Test
	public void deleteNodeCommand_NodeParentIsRoot_doNothing() {

		// Given
		EntiteDto rootNode = new EntiteDto();
		rootNode.setSigle("Root");
		EntiteDto childNode = new EntiteDto();
		rootNode.getEnfants().add(childNode);

		TreeNode<EntiteDto> childNodeTreeItem = new DefaultTreeNode<>(childNode,
				new ArrayList<DefaultTreeNode<EntiteDto>>());

		TreeNode<EntiteDto> rootNodeTreeItem = new DefaultTreeNode<>(rootNode, Arrays.asList(childNodeTreeItem));

		TreeViewModel vM = new TreeViewModel();
		vM.setSelectedTreeItem(childNodeTreeItem);

		// When
		vM.deleteNodeCommand();

		// Then
		assertEquals(rootNodeTreeItem.getChildren().get(0), childNodeTreeItem);
		assertEquals(rootNodeTreeItem, childNodeTreeItem.getParent());
	}

	@Test
	public void deleteNodeCommand_deleteNodeAndChildrenFromTree() {

		// Given
		EntiteDto rootNode = new EntiteDto();
		rootNode.setSigle("Something");
		EntiteDto childNode = new EntiteDto();
		rootNode.getEnfants().add(childNode);

		TreeNode<EntiteDto> childNodeTreeItem = new DefaultTreeNode<>(childNode,
				new ArrayList<DefaultTreeNode<EntiteDto>>());

		TreeNode<EntiteDto> rootNodeTreeItem = new DefaultTreeNode<>(rootNode, Arrays.asList(childNodeTreeItem));

		TreeViewModel vM = new TreeViewModel();
		vM.setSelectedTreeItem(childNodeTreeItem);

		// When
		vM.deleteNodeCommand();

		// Then
		assertEquals(0, rootNodeTreeItem.getChildren().size());
		assertNull(childNodeTreeItem.getParent());
	}

	@Test
	public void onDropCommand_MoveTreeNode() {

		// Given
		EntiteDto rootNode = new EntiteDto();
		rootNode.setSigle("Something");
		EntiteDto childNode1 = new EntiteDto();
		rootNode.getEnfants().add(childNode1);
		EntiteDto childNode2 = new EntiteDto();
		rootNode.getEnfants().add(childNode2);
		EntiteDto subChild = new EntiteDto();
		childNode1.getEnfants().add(subChild);

		TreeNode<EntiteDto> subChildNodeTreeItem = new DefaultTreeNode<>(subChild,
				new ArrayList<DefaultTreeNode<EntiteDto>>());

		TreeNode<EntiteDto> childNodeTreeItem1 = new DefaultTreeNode<>(childNode1, Arrays.asList(subChildNodeTreeItem));

		TreeNode<EntiteDto> childNodeTreeItem2 = new DefaultTreeNode<>(childNode2,
				new ArrayList<DefaultTreeNode<EntiteDto>>());

		TreeNode<EntiteDto> rootNodeTreeItem = new DefaultTreeNode<>(rootNode, Arrays.asList(childNodeTreeItem1,
				childNodeTreeItem2));

		TreeViewModel vM = new TreeViewModel();

		// When
		vM.onDropCommand((DefaultTreeNode<EntiteDto>) subChildNodeTreeItem,
				(DefaultTreeNode<EntiteDto>) childNodeTreeItem2);

		// Then
		assertEquals(2, rootNodeTreeItem.getChildren().size());
		assertEquals(0, childNodeTreeItem1.getChildren().size());
		assertEquals(1, childNodeTreeItem2.getChildren().size());
		assertEquals(childNodeTreeItem2, subChildNodeTreeItem.getParent());
	}

}
