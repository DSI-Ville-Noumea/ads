package nc.noumea.mairie.ads.viewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.ITreeConsultationService;
import nc.noumea.mairie.ads.view.tools.ViewModelHelper;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

public class RevisionTreeViewModelTest {

	@Test
	public void updateSelectedRevision_LoadTreeFromServiceAndBuildTreeView() {

		// Given
		Integer idRevision = 7;
		RevisionDto revDto = new RevisionDto();
		revDto.setIdRevision(idRevision);

		NoeudDto rootNode = new NoeudDto();
		rootNode.setIdNoeud(789456);
		rootNode.getEnfants().add(new NoeudDto());
		rootNode.getEnfants().get(0).setIdNoeud(123456);
		ITreeConsultationService treeConsultationService = Mockito.mock(ITreeConsultationService.class);
		Mockito.when(treeConsultationService.getTreeOfSpecificRevision(idRevision)).thenReturn(rootNode);

		RevisionTreeViewModel vM = new RevisionTreeViewModel();
		ReflectionTestUtils.setField(vM, "treeConsultationService", treeConsultationService);

		// When
		vM.updateSelectedRevision(revDto);

		// Then
		assertEquals(789456, vM.getNoeudTree().getRoot().getData().getIdNoeud());
		assertEquals(1, vM.getNoeudTree().getRoot().getChildren().size());
		assertEquals(123456, vM.getNoeudTree().getRoot().getChildren().get(0).getData().getIdNoeud());
	}

	@Test
	public void updateSelectedRevision_RevisionIsNull_BuildDefaultTree() {

		// Given
		RevisionTreeViewModel vM = new RevisionTreeViewModel();

		// When
		vM.updateSelectedRevision(null);

		// Then
		assertEquals(0, vM.getNoeudTree().getRoot().getData().getIdNoeud());
		assertNull(vM.getNoeudTree().getRoot().getChildren());
	}

	@Test
	public void createNewNodeCommand_createItAndSetSelectedNode() {

		// Given
		final TreeNode<NoeudDto> selectedTreeItem = new DefaultTreeNode<>(new NoeudDto(),
				new ArrayList<DefaultTreeNode<NoeudDto>>());

		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
		Mockito.doAnswer(new Answer() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Map<String, Object> args = (Map<String, Object>) invocation.getArguments()[3];
				assertEquals("NOUVEAU", selectedTreeItem.getChildren().get(0).getData().getSigle());
				return null;
			}

		}).when(vMh).postGlobalCommand(Mockito.anyString(), Mockito.anyString(), Mockito.eq("revisionTreeNodeSelectedChangeCommand"), Mockito.isA(Map.class));

		RevisionTreeViewModel vM = new RevisionTreeViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);

		vM.setSelectedTreeItem(selectedTreeItem);

		// When
		vM.createNewNodeCommand();

		// Then
		assertEquals(1, selectedTreeItem.getChildren().size());
		assertEquals(0, selectedTreeItem.getChildren().get(0).getData().getIdNoeud());
		assertEquals(0, selectedTreeItem.getChildren().get(0).getData().getIdService());
		assertEquals("NOUVEAU", selectedTreeItem.getChildren().get(0).getData().getSigle());
		assertEquals(0, selectedTreeItem.getChildren().get(0).getChildren().size());
		assertEquals(selectedTreeItem.getChildren().get(0), vM.getSelectedTreeItem());

		Mockito.verify(vMh, Mockito.times(1)).postGlobalCommand(Mockito.anyString(), Mockito.anyString(), Mockito.eq("revisionTreeNodeSelectedChangeCommand"), Mockito.isA(Map.class));
	}

	@Test
	public void deleteNodeCommand_NodeParentIsRoot_doNothing() {

		// Given
		NoeudDto rootNode = new NoeudDto();
		rootNode.setSigle("Root");
		NoeudDto childNode = new NoeudDto();
		rootNode.getEnfants().add(childNode);

		TreeNode<NoeudDto> childNodeTreeItem = new DefaultTreeNode<>(childNode,
				new ArrayList<DefaultTreeNode<NoeudDto>>());

		TreeNode<NoeudDto> rootNodeTreeItem = new DefaultTreeNode<>(rootNode, Arrays.asList(childNodeTreeItem));

		RevisionTreeViewModel vM = new RevisionTreeViewModel();
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
		NoeudDto rootNode = new NoeudDto();
		rootNode.setSigle("Something");
		NoeudDto childNode = new NoeudDto();
		rootNode.getEnfants().add(childNode);

		TreeNode<NoeudDto> childNodeTreeItem = new DefaultTreeNode<>(childNode,
				new ArrayList<DefaultTreeNode<NoeudDto>>());

		TreeNode<NoeudDto> rootNodeTreeItem = new DefaultTreeNode<>(rootNode, Arrays.asList(childNodeTreeItem));

		RevisionTreeViewModel vM = new RevisionTreeViewModel();
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
		NoeudDto rootNode = new NoeudDto();
		rootNode.setSigle("Something");
		NoeudDto childNode1 = new NoeudDto();
		rootNode.getEnfants().add(childNode1);
		NoeudDto childNode2 = new NoeudDto();
		rootNode.getEnfants().add(childNode2);
		NoeudDto subChild = new NoeudDto();
		childNode1.getEnfants().add(subChild);

		TreeNode<NoeudDto> subChildNodeTreeItem = new DefaultTreeNode<>(subChild,
				new ArrayList<DefaultTreeNode<NoeudDto>>());
		
		TreeNode<NoeudDto> childNodeTreeItem1 = new DefaultTreeNode<>(childNode1,
				Arrays.asList(subChildNodeTreeItem));
		
		TreeNode<NoeudDto> childNodeTreeItem2 = new DefaultTreeNode<>(childNode2,
				new ArrayList<DefaultTreeNode<NoeudDto>>());

		TreeNode<NoeudDto> rootNodeTreeItem = new DefaultTreeNode<>(rootNode, Arrays.asList(childNodeTreeItem1, childNodeTreeItem2));

		RevisionTreeViewModel vM = new RevisionTreeViewModel();

		// When
		vM.onDropCommand((DefaultTreeNode<NoeudDto>)subChildNodeTreeItem, (DefaultTreeNode<NoeudDto>)childNodeTreeItem2);

		// Then
		assertEquals(2, rootNodeTreeItem.getChildren().size());
		assertEquals(0, childNodeTreeItem1.getChildren().size());
		assertEquals(1, childNodeTreeItem2.getChildren().size());
		assertEquals(childNodeTreeItem2, subChildNodeTreeItem.getParent());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void whatIsTheCurrentRevisionTree_PostGlobalCommandWithTree() {

		// Given
		ViewModelHelper vMh = Mockito.mock(ViewModelHelper.class);
		
		final NoeudDto rootNode = new NoeudDto();
		
		Mockito.doAnswer(new Answer() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Map<String, Object> args = (Map<String, Object>) invocation.getArguments()[3];
				assertTrue(args.containsKey("currentRevisionTree"));
				assertEquals(rootNode, args.get("currentRevisionTree"));
				return null;
			}
			
		}).when(vMh).postGlobalCommand(Mockito.anyString(), Mockito.anyString(), Mockito.eq("thisIsTheCurrentRevisionTree"), Mockito.isA(Map.class));
		
		RevisionTreeViewModel vM = new RevisionTreeViewModel();
		ReflectionTestUtils.setField(vM, "viewModelHelper", vMh);
		vM.setNoeudTree(new DefaultTreeModel<>(vM.buildTreeNodes(rootNode), true));
		
		// When
		vM.whatIsTheCurrentRevisionTree();
		
		// Then
		Mockito.verify(vMh, Mockito.times(1)).postGlobalCommand(Mockito.anyString(), Mockito.anyString(), Mockito.eq("thisIsTheCurrentRevisionTree"), Mockito.isA(Map.class));
	}
}
